package com.agora.app.service.impl;

import com.agora.app.exception.ASNotFoundException;
import com.agora.app.exception.ASRequestRestApiException;
import com.agora.app.service.RestService;
import com.agora.app.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

@Slf4j
@Service
public class RestServiceImpl implements RestService {

    @Value("${application.baseUri}")
    private String baseUri;

    @Value("${agora.chat.robot.name}")
    private String chatRobotName;

    private static final String DEFAULT_CHAT_GROUP_NAME = "AI Chatbot'group";

    private static final String DEFAULT_CHAT_GROUP_MEMBER_NAME_BELLA = "Bella";

    private static final String DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES = "Miles";

    @Autowired
    private TokenService tokenService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override public void registerChatUserName(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken());

        Map<String, String> body = new HashMap<>();
        body.put("username", chatUserName);
        body.put("password", "123");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (HttpClientErrorException e) {
            log.error("register chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new ASRequestRestApiException("Register chat user error.");
        }
    }

    @Override public boolean checkIfChatUserNameExists(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String token = tokenService.getAppToken();
        System.out.println("token: " + token);

        headers.setBearerAuth(token);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            log.error("get chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new ASRequestRestApiException("Get chat user error.");
        } catch (Exception e) {
            log.error("get chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new ASRequestRestApiException("Get chat user error.");
        }

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void addContact(String appkey, String chatUserName, String contactName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/" + chatUserName + "/contacts/users/" + contactName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("chat user add contact. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new ASRequestRestApiException("Add contact error.");
        }
    }

    @Override
    public String createChatGroup(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken());

        Map<String, Object> body = new HashMap<>();
        body.put("groupname", DEFAULT_CHAT_GROUP_NAME);
        body.put("public", false);
        body.put("owner", chatUserName);
        body.put("custom", "default");

        List<String> members = new ArrayList<>();
        members.add(DEFAULT_CHAT_GROUP_MEMBER_NAME_BELLA);
        members.add(DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES);
        members.add(chatRobotName);
        body.put("members", members);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("create chat group. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new ASRequestRestApiException("Create chat group error.");
        }

        Map<String, String> data = (Map<String, String>) responseEntity.getBody().get("data");
        return data.get("groupid");
    }

    @Override public String getChatUserUuid(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (RestClientException e) {
            log.error("get chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new ASRequestRestApiException("Get chat user error.");
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) responseEntity.getBody().get("entities");
        return (String) results.get(0).get("uuid");
    }

    @Override
    public void sendTextMessageToUser(String appkey, String from, String to, String messageContent,
            Map<String, Object> ext) {

        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/messages/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken());

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        List<String> tos = new ArrayList<>();
        tos.add(to);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);
        if (ext != null) {
            body.put("ext", ext);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to user. chatUserName : {}, error : {}", to, e.getMessage());
            throw new ASRequestRestApiException("Send message to user error.");
        }
    }

    @Override public void sendTextMessageToGroup(String appkey, String from, String groupId,
            String messageContent, Map<String, Object> ext) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/messages/chatgroups";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken());

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        List<String> tos = new ArrayList<>();
        tos.add(groupId);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);
        if (ext != null) {
            body.put("ext", ext);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to group. groupId : {}, error : {}", groupId, e.getMessage());
            throw new ASRequestRestApiException("Send message to group error.");
        }

    }

    @Override public void sendTextMessageToGroupMember(String appkey, String from, String groupId,
            String groupMember, String messageContent, Map<String, Object> ext) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/messages/chatgroups/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken());

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        List<String> tos = new ArrayList<>();
        tos.add(groupId);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);
        List<String> users = new ArrayList<>();
        users.add(groupMember);
        body.put("users", users);
        if (ext != null) {
            body.put("ext", ext);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to group member. groupId : {}, error : {}", groupId, e.getMessage());
            throw new ASRequestRestApiException("Send message to group member error.");
        }
    }

    @Override public String getChatGroupCustom(String appkey, String chatGroupId) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups/" + chatGroupId;;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat group created. appkey : {}, error : {}", appkey, e.getMessage());
        }

        if (responseEntity == null) {
            return null;
        }

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) responseEntity.getBody().get("data");

        return (String) data.get(0).get("custom");
    }

    @Override public void updateGroupCustom(String appkey, String chatGroupId, String custom) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups/" + chatGroupId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken());

        Map<String, Object> body = new HashMap<>();
        body.put("custom", custom);
        body.put("avatar", custom);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
        } catch (Exception e) {
            log.error("update chat group custom. appkey : {}, chatGroupId : {}, error : {}", appkey, chatGroupId, e.getMessage());
            throw new ASRequestRestApiException("Update chat group custom error.");
        }
    }

    @Override public List<String> getChatGroupMembers(String appkey, String chatGroupId) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups/" + chatGroupId + "/users?pagenum=1&pagesize=9";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat group members. appkey : {}, groupId : {}, error : {}", appkey, chatGroupId, e.getMessage());
            if (e.getMessage().contains("Not Found")) {
                throw new ASNotFoundException("Chat group not found.");
            }
        }

        List<Map<String, String>> members =
                (List<Map<String, String>>) responseEntity.getBody().get("data");

        List<String> memberNames = new ArrayList<>();
        members.forEach(member -> {
            String username = member.get("member");
            if (username == null) {
                memberNames.add(member.get("owner"));
            } else {
                memberNames.add(username);
            }
        });

        Collections.reverse(memberNames);

        return memberNames;
    }

    @Override public String uploadFile(String appkey, String id, File file) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/chatfiles";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(tokenService.getAppToken());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("upload chat file error. appkey : {}, id :{}, error : {}", appkey, id, e.getMessage());
            throw new ASRequestRestApiException("upload chat file error.");
        }

        List<Map<String, String>> entities =
                (List<Map<String, String>>) responseEntity.getBody().get("entities");
        String fileUuid = entities.get(0).get("uuid");
        String fileUrl = convertHttpToHttps(baseUri) + "/" + orgName + "/" + appName + "/chatfiles" + "/" + fileUuid;

        return fileUrl;
    }

    @Override public BufferedInputStream downloadThumbImage(String appkey, String urlPath) {
        String url = baseUri + urlPath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("thumbnail", "true");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setBearerAuth(tokenService.getAppToken());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<byte[]> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        } catch (Exception e) {
            log.error("download chat file error. appkey : {}, url : {}, error : {}", appkey, url, e.getMessage());
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseEntity.getBody());
        return new BufferedInputStream(byteArrayInputStream);
    }

    private String convertHttpToHttps(String baseUri) {
        String finalBaseUri = baseUri;
        if (baseUri.startsWith("http://")) {
            finalBaseUri = baseUri.replace("http://", "https://");
        }

        return finalBaseUri;
    }
}

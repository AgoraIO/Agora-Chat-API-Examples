# chat-app-demo-server

## 简介

该服务为 Agora Demo 提供后端服务，可作为 App 使用 Agora Chat 功能的服务器端实现示例，该服务中包括 AI 机器人功能。
AI 机器人实现原理：
1.给 AgoraChat Demo 使用的 AppKey 开通实时消息回调功能。
2.使用实时消息回调功能，将用户给 ChatBot 发送的消息实时的回调给 AppServer，AppServer 将给 ChatBot 发送的消息筛选出来。
3.将消息发送给 Open AI，最后将 Open AI 回复的消息，在调用 REST API 将消息发送给用户，来完成用户与 Open AI 聊天的功能。

用户与群聊 AI 机器人聊天流程图：
![](../../../Group-ChatBot-en-Group ChatBot.png)

- 该服务目前提供的主要功能有

```
1、用户登录；
2、上传用户头像；
3、获取群组头像；
4、使用 Agora Chat 发送后回调消息，需要开通发送后消息回调功能（注意开通发送后消息回调时，将 'REST 消息是否需要回调' 设置为否），将 CallBackController 中的 url 配置到回调地址中，作为接收回调消息的地址；
5、AI 机器人功能，使用的 OpenAI 实现与用户聊天的功能；
```

## 技术选择

* [Spring Boot](https://spring.io/projects/spring-boot)
* [OpenAI](https://platform.openai.com/docs/api-reference/introduction)
* [发送后消息回调](https://docs.agora.io/en/agora-chat/reference/callbacks-events?platform=android)

## 主要组件

* JDK 11
* MySQL
* Redis

## 数据库使用说明

* 使用MySQL存储用户信息
* 建表SQL见 [建表SQL](./doc/create_tables.sql)

## 使用

- 若初次使用 Agora，需前往 [Agora Console](https://sso2.agora.io/en/login?redirectUri=https%3A%2F%2Fsso2.agora.io%2Fapi%2Fv0%2Foauth%2Fauthorize%3Fresponse_type%3Dcode%26client_id%3Dconsole%26redirect_uri%3Dhttps%253A%252F%252Fconsole.agora.io%252Fapi%252Fv2%252Foauth%252Fen%26scope%3Dbasic_info) 注册成为 Agora 开发者；

- 注册成为 Agora 开发者后，开通 [Agora_chat](https://docs.agora.io/en/agora-chat/get-started/enable?platform=android) 服务；
- 
- 成为 Agora 开发者并成功开通 Agora Chat 服务后，可在自己的服务器部署服务

  - 服务配置文件参考：[application.properties](./chat-app-demo-server/src/main/resources/application.properties)

  - AppKey组成规则：${orgName}#${appName}，拿到AppKey后可得到对应的orgName和appName；
  - 使用自己的AppKey、Rest服务器域名、AppId、AppCert修改配置文件，如下：
    ```
        application.appkey=XXX
        application.baseUri=https://XXX.chat.agora.io.com
        application.agoraAppId=XXX
        application.agoraAppCert=XXX
    ```

  - 安装MySQL，并根据[建表SQL](./doc/create_tables.sql)创建数据库及表，设置服务配置文件：
    ```
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        spring.datasource.url=jdbc:mysql://localhost:3306/app_server?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
        spring.datasource.username=root
        spring.datasource.password=123456789
    ```
  - 安装Redis，设置Redis服务配置文件：
    ```
        spring.redis.channel.nodes=tcp://127.0.0.1:6379
        spring.redis.channel.password=
        spring.redis.channel.timeout=10000
        spring.redis.channel.expireTime=86400
    ```
  - AI 机器人配置：
    ```
        ## Whether to use an AI bot
        agora.chat.robot.enable.switch=false

        ## The bot's name, as the Agora chat user ID, needs to be registered by yourself.
        agora.chat.robot.name=chatbot_ai

        ## ChatGPT
        chatgpt.api.key=XXX
        chatgpt.url=https://api.openai.com/v1/chat/completions
        chatgpt.model=gpt-4
        chatgpt.max.tokens=1700
        chatgpt.temperature=0.1

        chatgpt.image.url=https://api.openai.com/v1/images/generations
        chatgpt.image.model=dall-e-3
        chatgpt.image.size=1024x1024
        chatgpt.image.n=1
    ```

  - 启动服务即可

## API

### 用户登录

用户登录并获取用户 token，用于客户端 sdk 登录 Agora Chat 服务器。

**Path:** `http://localhost:8095/app/chat/user/login`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | application/json |

**Request Body example:**

{"userAccount":"tom", "userPassword":"123456"}

**Request Body params:**

| Param       | Data Type | description |
|-------------| --------- |-------------|
| userAccount | String    | 用户名         |
| userPassword     | String    | 用户密码        |

**request example:**

```
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:8095/app/chat/user/login' -d '{"userAccount":"tom", "userPassword":"123456"}'
```

**Response Parameters:**

| Param           | Data Type | description                          |
| --------------- |-----------|--------------------------------------|
| statusCode            | Integer   | 响应状态码                                |
| token     | String    | 用户 token，用于客户端 sdk 登录 Agora Chat 服务器 |
| accessToken     | String    | 与 token 定义相同                         |
| expireTimestamp     | Long      | Token 的过期时间，毫秒时间戳                    |
| chatUserName | String    | agora chat username                  |
| avatarUrl | String    | 用户头像 url                             |

**response example:**

```json
{
  "code": "RES_OK",
  "statusCode": 200,
  "accessToken": "XXX",
  "token": "XXX",
  "expireTimestamp": 1735900124595,
  "chatUserName": "tom",
  "agoraUid": "2019543019"
}
```

---

### 上传用户头像

**Path:** `http://localhost:8095/app/chat/user/{userAccount}/avatar/upload`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | multipart/form-data |

**Request Body example:**
file=@/Users/XXX/image.jpg

**Request Body params:**

| Param   | description |
|---------|-------------|
| file    | 头像本地路径      |

**request example:**

```
curl -X POST http://localhost:8095/app/chat/user/tom/avatar/upload -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' -F file=@/Users/XXX/image.jpg
```

**Response Parameters:**

| Param           | Data Type | description                |
| --------------- |-----------|----------------------------|
| statusCode            | Integer   | 响应状态码                      |
| avatarUrl | String    | 用户头像 url                   |

**response example:**

```json
{
    "code": "RES_OK",
    "statusCode": 200,
    "avatarUrl": "xxx"
}
```

---

### 获取群组头像

**Path:** `http://localhost:8095/app/chat/group/{groupId}/avatarurl`

**HTTP Method:** `GET`

**request example:**

```
curl -X GET http://localhost:8095/app/chat/group/242023244300303/avatarurl
```

**Response Parameters:**

| Param           | Data Type | description |
| --------------- |-----------|-------------|
| code            | Integer   | 响应状态码       |
| avatarUrl | String    | 群组头像 url    |

**response example:**

```json
{
    "code": 200,
    "avatarUrl": "xxx"
}
```


# How to Implement the Server for the Agora Chat Demo

## Introduction

This service provides backend support for Agora Demo, serving as a server implementation example for apps utilizing Agora Chat. It includes AI chatbot capabilities.

The AI chatbot works in the following way:

1. Enable the real-time message callback function for the App Key used by Agora Chat Demo.
2. Use the callback to forward messages sent by users to the chatbot to the App Server in real-time. The App Server screens out messages intended for the chatbot.
3. Send these messages to OpenAI and use RESTful APIs to deliver OpenAI's responses to the user, thus enabling the interaction between users and the chatbot.

Following is the flowchat of the chat between a user and the chatbot in a group:

![wecom-temp-9681206a6f7a75f524bdb3c49f040622](/Users/easemob-dn0164/Desktop/wecom-temp-9681206a6f7a75f524bdb3c49f040622.png)

- This service provides the following functions:

```
1. User login
2. Upload user avatars
3. Fetch group avatars
4. Message post-sending callback: Before using this function, you need to enable it on the Agora Console by configuring a post-sending callback rule to set "REST Message Required" to "No" and set the URL in CallBackController as the callback address.
5. AI chatbot functionality powered by OpenAI for interactions with users.

```

## Technology stack

* [Spring Boot](https://spring.io/projects/spring-boot)
* [OpenAI](https://platform.openai.com/docs/api-reference/introduction)
* [Post-sending callback](https://docs.agora.io/en/agora-chat/reference/callbacks-events?platform=android)

## Core components

* JDK 11
* MySQL
* Redis

## Database

* MySQL for user information storage
* [Table creation SQL statements](/doc/create_tables.sql)

## How to use the service

### Prerequisites

- For the first use of Agora Chat, register as an Agora developer at [Agora Console](https://sso2.agora.io/en/login?redirectUri=https%3A%2F%2Fsso2.agora.io%2Fapi%2Fv0%2Foauth%2Fauthorize%3Fresponse_type%3Dcode%26client_id%3Dconsole%26redirect_uri%3Dhttps%253A%252F%252Fconsole.agora.io%252Fapi%252Fv2%252Foauth%252Fen%26scope%3Dbasic_info).

- After registration, activate [Agora Chat](https://docs.agora.io/en/agora-chat/get-started/enable?platform=android).

### Service Deployment

Deploy the service at your server:

- Configure the service by reference to [application.properties](/src/main/resources/application.properties)
- Get the App Key. The App Key is in the format of ${orgName}#${appName}.
- Update configurations with your App Key, REST server domain name, App ID, and App Cert:

    ```
        application.appkey=XXX
        application.baseUri=https://XXX.chat.agora.io.com
        application.agoraAppId=XXX
        application.agoraAppCert=XXX
    ```

- Install MySQL, create the database and tables by reference to [table creation SQL](/doc/create_tables.sql), and configure the server configuration file:

    ```
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        spring.datasource.url=jdbc:mysql://localhost:3306/app_server?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
        spring.datasource.username=root
        spring.datasource.password=123456789
    ```
- Install Redis and configure the Redis configuration file:

    ```
        spring.redis.channel.nodes=tcp://127.0.0.1:6379
        spring.redis.channel.password=
        spring.redis.channel.timeout=10000
        spring.redis.channel.expireTime=86400
    ```
- Configure the AI chatbot:

    ```
        ## Whether to use an AI chatbot
        agora.chat.robot.enable.switch=false
    
        ## The chatbot's name, as with the Agora chat user ID, needs to be registered by yourself.
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

- Start the service.

## API

### Log in to your server

Log in to your server and get the user token for you to log in to the Agora Chat server.

**Path:** `http://localhost:8095/app/chat/user/login`

**HTTP method:** `POST`

**Request headers:**

| Param        | description       |
| ------------ | ----------------- |
| Content-Type | application/json. |

**Request body example**

{"userAccount":"tom", "userPassword":"123456"}

**Request body params:**

| Param       | Data Type | description |
|-------------| --------- |-------------|
| userAccount | String    | User ID.        |
| userPassword     | String    | Login password.        |

**Request example:**

```
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:8095/app/chat/user/login' -d '{"userAccount":"tom", "userPassword":"123456"}'
```

**Response parameters**

| Param           | Data Type | description                          |
| --------------- |-----------|--------------------------------------|
| statusCode            | Integer   | Response status code.                                |
| token     | String    | The user token for the client SDK to log in to the Agora Chat server.|
| accessToken     | String    | Same as the `token` parameter.   |
| expireTimestamp     | Long      | Token expiry timestamp in the unit of millisecond.                   |
| chatUserName | String    | The user ID for login to Agora Chat.            |
| avatarUrl | String    | The URL of the user avatar.                           |

**Response example**

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

### Upload user avatar

**Path:** `http://localhost:8095/app/chat/user/{userAccount}/avatar/upload`

**HTTP method:** `POST`

**Request headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | multipart/form-data. |

**Request body example:**

file=@/Users/XXX/image.jpg

**Request body params:**

| Param   | description |
|---------|-------------|
| file    | Local avatar path.     |

**Request example:**

```
curl -X POST http://localhost:8095/app/chat/user/tom/avatar/upload -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' -F file=@/Users/XXX/image.jpg
```

**Response parameters:**

| Param           | Data Type | description                |
| --------------- |-----------|----------------------------|
| statusCode            | Integer   | Response status code.                     |
| avatarUrl | String    | The URL of the user avatar.                  |

**Response example:**

```json
{
    "code": "RES_OK",
    "statusCode": 200,
    "avatarUrl": "xxx"
}
```

---

### Get the group avatar

**Path:** `http://localhost:8095/app/chat/group/{groupId}/avatarurl`

**HTTP method:** `GET`

**Request example:**

```
curl -X GET http://localhost:8095/app/chat/group/242023244300303/avatarurl
```

**Response parameters:**

| Param           | Data Type | description |
| --------------- |-----------|-------------|
| code            | Integer   | Response status code.       |
| avatarUrl | String    | The URL of the group avatar.    |

**Response example:**

```json
{
  "code": 200,
  "avatarUrl": "xxx"
}
```


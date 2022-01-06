# 发送和接收点对点消息

本页面介绍了如何快速集成 Agora Chat SDK 来实现单聊。

## 消息发送与接收流程
// todo 需要增加一张流程图

登录 Agora Chat 流程如下：

使用帐号和密码在 App Server 上注册。
注册成功后，使用账号和密码从 App Server 中获取 Token 。
使用账号和 Token 登录到 Chat 服务器。
// todo 需要增加一张流程图

发送和接收点对点消息包括以下流程：

客户端 A 发送点对点消息到 Chat 服务器。
Chat 服务器将消息发送到客户端 B。客户端 B 收到点对点消息。

## 前提条件

- 有效的 Agora Chat 开发者账号。
- 创建 Agora Chat 项目并获取 AppKey 。//todo 增加跳转链接
- [npm](https://www.npmjs.com/get-npm)
- SDK 支持 IE9+、FireFox10+、Chrome54+、Safari6+ 之间文本、表情、图片、音频、地址消息相互发送。
- SDK 本身已支持 IE9+、FireFox10+、Chrome54+、Safari6+。


## 操作步骤

### 1. 准备开发环境

本节介绍如何创建项目，将 Agora Chat SDK 集成进你的项目中。

#### 新建 Web 项目

新建一个目录 Agora_quickstart。在目录下运行 npm init 创建一个 package.json 文件，然后创建以下文件:

index.html
index.js
此时你的目录中包含以下文件：

Agora_quickstart
├─ index.html
├─ index.js
└─ package.json

### 2. 集成 SDK

- 在 `package.json` 中的 `dependencies` 字段中加入 `agora-chat-sdk` 及对应版本：

    ```json
   {
     "name": "web",
     "version": "1.0.0",
     "description": "",
     "main": "index.js",
     "scripts": {
       "test": "echo \"Error: no test specified\" && exit 1"
     },
     "dependencies": {
       "agora-chat-sdk": "latest"
     },
     "author": "",
     "license": "ISC"
   }
   ```

- 在你的 JS 文件中导入 `agora-chat-sdk` 模块：

```JavaScript
import WebIM from 'agora-chat-sdk'
```

### 3. 实现用户界面

index.html 的内容如下。<script src="./dist/bundle.js"></script> 用来引用 webpack 打包之后的bundle.js 文件。webpack 的配置会在后续步骤提及。

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Agora Chat Examples</title>
</head>

<body>
    <h2 class="left-align">Agora Chat Examples</h5>
        <form id="loginForm">
            <div class="col" style="min-width: 433px; max-width: 443px">
                <div class="card" style="margin-top: 0px; margin-bottom: 0px;">
                    <div class="row card-content" style="margin-bottom: 0px; margin-top: 10px;">
                        <div class="input-field">
                            <label>Username</label>
                            <input type="text" placeholder="Username" id="userID">
                        </div>
                        <div class="input-field">
                            <label>Password</label>
                            <input type="passward" placeholder="Password" id="password">
                        </div>
                        <div class="row">
                            <div>
                                <button type="button" id="register">register</button>
                                <button type="button" id="login">login</button>
                                <button type="button" id="logout">logout</button>
                            </div>
                        </div>
                        <div class="input-field">
                            <label>Peer username</label>
                            <input type="text" placeholder="Peer username" id="peerId">
                        </div>
                        <div class="input-field">
                            <label>Peer Message</label>
                            <input type="text" placeholder="Peer message" id="peerMessage">
                            <button type="button" id="send_peer_message">send</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <hr>
        <div id="log"></div>
</body>
<script src="./dist/bundle.js"></script>
</html>
```


### 4. 实现消息发送与接收

index.js 的内容如下。本文使用 import 的方法导入 SDK，并使用 webpack 对 JS 文件进行打包，以避免浏览器兼容性问题。你需要分别将代码中的 "<Your app key>" 替换为你之前获取的 AppKey。

```Javascript
import WebIM from 'agora-chat-sdk'
const appKey = "<Your app key>"

let username, password

// 初始化客户端
WebIM.conn = new WebIM.connection({
    appKey: appKey,
})

// 添加回调函数
WebIM.conn.addEventHandler('connection&message', {
    onConnected: () => {
        document.getElementById("log").appendChild(document.createElement('div')).append("Connect success !")
    },
    onDisconnected: () => {
        document.getElementById("log").appendChild(document.createElement('div')).append("Logout success !")
    },
    onTextMessage: (message) => {
        console.log(message)
        document.getElementById("log").appendChild(document.createElement('div')).append("Message from: " + message.from + " Message: " + message.msg)
    },
    onTokenWillExpire: (params) => {
        document.getElementById("log").appendChild(document.createElement('div')).append("Token is about to expire")
        refreshToken(username, password)
    },
    onTokenExpired: (params) => {
        document.getElementById("log").appendChild(document.createElement('div')).append("The token has expired")
        refreshToken(username, password)
    },
    onError: (error) => {
        console.log('on error', error)
    }
})

// 从 app server 获取token
function refreshToken(username, password) {
    postData('https://a41.easemob.com/app/chat/user/login', { "userAccount": username, "userPassword": password })
        .then((res) => {
            let agoraToken = res.accessToken
            WebIM.conn.resetToken(agoraToken)
        })
}

// 发送请求
function postData(url, data) {
    return fetch(url, {
        body: JSON.stringify(data),
        cache: 'no-cache',
        headers: {
            'content-type': 'application/json'
        },
        method: 'POST',
        mode: 'cors',
        redirect: 'follow',
        referrer: 'no-referrer',
    })
        .then(response => response.json())
}

// 按钮行为定义
window.onload = function () {
    // 注册
    document.getElementById("register").onclick = function(){
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        postData('https://a41.easemob.com/app/chat/user/register', { "userAccount": username, "userPassword": password })
            .then((res) => {
                if (res.errorInfo && res.errorInfo.indexOf('already exists') !== -1) {
                    document.getElementById("log").appendChild(document.createElement('div')).append(`${username} already exists`)
                    return
                }
                document.getElementById("log").appendChild(document.createElement('div')).append(`${username} regist success`)
            })
    }
    // 登录
    document.getElementById("login").onclick = function () {
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        postData('https://a41.easemob.com/app/chat/user/login', { "userAccount": username, "userPassword": password })
            .then((res) => {
                let agoraToken = res.accessToken
                let easemobUserName = res.easemobUserName
                WebIM.conn.open({
                    user: easemobUserName,
                    agoraToken: agoraToken
                });
            })
    }

    // 登出
    document.getElementById("logout").onclick = function () {
        WebIM.conn.close();
    }

    // 发送一条单聊消息
    document.getElementById("send_peer_message").onclick = function () {
        let peerId = document.getElementById("peerId").value.toString()
        let peerMessage = document.getElementById("peerMessage").value.toString()

        let id = WebIM.conn.getUniqueId()
        let msg = new WebIM.message('txt', id);
        msg.set({
            msg: peerMessage,
            to: peerId,
            chatType: 'singleChat',
            success: function () {
                console.log('send private text success');
                document.getElementById("log").appendChild(document.createElement('div')).append("Message send to: " + peerId + " Message: " + peerMessage)
            },
            fail: function (e) {
                console.log('send private text fail');
            }
        });
        WebIM.conn.send(msg.body);
    }
}

```

### 5. 运行项目

本文使用 webpack 对项目进行打包，并使用 webpack-dev-server 运行项目。

1.在 package.json 的 dependencies 字段中添加 webpack，webpack-cli，webpack-dev-server。并在 scripts 字段中增加 build 和 start:dev 命令。

```json
{
    "name": "web",
    "version": "1.0.0",
    "description": "",
    "main": "index.js",
    "scripts": {
        "build": "webpack --config webpack.config.js",
        "start:dev": "webpack serve --open --config webpack.config.js"
    },
    "dependencies": {
        "agora-chat-sdk": "latest",
        "webpack": "^5.50.0",
        "webpack-dev-server": "^3.11.2",
        "webpack-cli": "^4.8.0"
    },
    "author": "",
    "license": "ISC"
}
```

2.在项目根目录添加 webpack.config.js 文件，用于配置 webpack。文件内容如下：

```Javascript
const path = require('path');

module.exports = {
    entry: './index.js',
    mode: 'production',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, './dist'),
    },
    devServer: {
        compress: true,
        port: 9000,
        https: true
    }
};
```

此时你的目录中包含以下文件：

Agora_quickstart
├─ index.html
├─ index.js
├─ package.json
└─webpack.config.js

3.在项目根目录运行以下命令，安装依赖项。

```bash
$ npm install
```

4.运行以下命令使用 webpack 构建并运行项目。

```bash
# 使用 webpack 打包
$ npm run build

# 使用 webpack-dev-server 运行项目
$ npm run start:dev
```

项目启动之后，在页面输入用户名、密码进行注册，然后用注册的用户名密码登录，在登录成功之后，输入对方的用户名和要发送的消息，点击“发送”按钮进行发送，可以同时再打开一个页面相互收发消息。

### 6. 参考信息

集成 SDK 有两种方式：

#### 方法一：通过 npm 安装并导入 SDK

1. 在 `package.json` 中的 `dependencies` 字段中加入 `agora-chat-sdk` 及对应版本：

    ```json
   {
     "name": "web",
     "version": "1.0.0",
     "description": "",
     "main": "index.js",
     "scripts": {
       "test": "echo \"Error: no test specified\" && exit 1"
     },
     "dependencies": {
       "agora-chat-sdk": "latest"
     },
     "author": "",
     "license": "ISC"
   }
   ```

2. 在你的 JS 文件中导入 `agora-chat-sdk` 模块：

```JavaScript
import WebIM from 'agora-chat-sdk'
```

#### 方法二：从官网获取并导入 SDK

1. 下载 [Agora Chat SDK for Web](https://docs....)。将 `libs` 中的 JS 文件保存到你的项目下。（下载地址需添加，现在没有单独下载 SDK 的地址）

2. 在 HTML 文件中，对 JS 文件进行引用。

```JavaScript
   <script src="path to the JS file"></script>
```

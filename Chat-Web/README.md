# 发送和接收点对点消息

本页面介绍了如何快速集成 Agora Chat SDK 来实现单聊。


## 前提条件

- 有效的 Agora Chat 开发者账号。
- 创建 Agora Chat 项目并获取 AppKey 。//todo 增加跳转链接
- [npm](https://www.npmjs.com/get-npm)


## 操作步骤

### 1. 准备开发环境

本节介绍如何创建项目，将 Agora Chat SDK 集成进你的项目中，并添加相应的设备权限。

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
    <script src="./dist/bundle.js"></script>
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

</html>
```


### 4. 实现消息发送与接收

index.js 的内容如下。本文使用 import 的方法导入 SDK，并使用 webpack 对 JS 文件进行打包，以避免浏览器兼容性问题。你需要分别将代码中的 "<Your app key>" 替换为你之前获取的 AppKey。

```Javascript
import WebIM from 'agora-chat-sdk'

// 初始化客户端
WebIM.conn = new WebIM.connect({
    appKey: "<Your app key>",
    isHttpDNS: true,
    https: true
})

// 添加回调函数
WebIM.conn.listen({
    onOpened: function (message) {
        document.getElementById("log").appendChild(document.createElement('div')).append("Connect success !")
    }, // 连接成功回调 
    onClosed: function (message) {
        document.getElementById("log").appendChild(document.createElement('div')).append("Logout success !")
    }, // 连接关闭回调
    onTextMessage: function (message) {
        console.log(message)
        document.getElementById("log").appendChild(document.createElement('div')).append("Message from: " + message.from + " Message: " + message.msg)
    }, // 收到文本消息
})

// 按钮行为定义
window.onload = function () {
    // 注册
    document.getElementById("register").onclick = function(){
        let username = document.getElementById("userID").value.toString()
        let password = document.getElementById("password").value.toString()
        WebIM.conn.registerUser({
            username: username,
            password: password,
            success: function () {
                document.getElementById("log").appendChild(document.createElement('div')).append("registerUser success: " + username)
            },
            error: function (error) {
                document.getElementById("log").appendChild(document.createElement('div')).append("registerUser error")
            },
        });
    }
    // 登录
    document.getElementById("login").onclick = function () {
        let username = document.getElementById("userID").value.toString()
        let password = document.getElementById("password").value.toString()
        WebIM.conn.open({
            user: username,
            pwd: password,
            appKey: "<Your app key>"
        });
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
        "webpack": "5.28.0",
        "webpack-dev-server": "3.11.2",
        "webpack-cli": "4.5.0"
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
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, './dist'),
    },
    devServer: {
        compress: true,
        port: 9000
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

# 发送和接收点对点消息

本页面介绍了如何快速集成 Easemob Chat SDK 来实现单聊。


## 前提条件

- 有效的 Easemob Chat 开发者账号。
- [创建 Easemob Chat 项目并获取 AppKey](https://docs-im.easemob.com/im/quickstart/guide/experience) 。
- [npm](https://www.npmjs.com/get-npm)
- SDK 支持 IE9+、FireFox10+、Chrome54+、Safari6+ 之间文本、表情、图片、音频、地址消息相互发送。
- SDK 本身已支持 IE9+、FireFox10+、Chrome54+、Safari6+。


## 操作步骤

### 1. 准备开发环境

本节介绍如何创建项目，将 Easemob Chat SDK 集成进你的项目中。

#### 新建 Web 项目

新建一个目录 Easemob_quickstart。在目录下运行 npm init 创建一个 package.json 文件，然后创建以下文件:

index.html
index.js
此时你的目录中包含以下文件：

Easemob_quickstart
├─ index.html
├─ index.js
└─ package.json

### 2. 集成 SDK

- 在 `package.json` 中的 `dependencies` 字段中加入 `easemob-websdk` 及对应版本：

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
       "easemob-websdk": "latest"
     },
     "author": "",
     "license": "ISC"
   }
   ```

- 在你的 JS 文件中导入 `easemob-websdk` 模块：

```JavaScript
import WebIM from 'easemob-websdk'
```

### 3. 实现用户界面

index.html 的内容如下。<script src="./dist/bundle.js"></script> 用来引用 webpack 打包之后的bundle.js 文件。webpack 的配置会在后续步骤提及。

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Easemob Chat Examples</title>
</head>

<body>
    <h2 class="left-align">Easemob Chat Examples</h5>
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
import WebIM from 'easemob-websdk'
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
    onError: (error) => {
        console.log('on error', error)
    }
})


// 按钮行为定义
window.onload = function () {
    // 注册
    document.getElementById("register").onclick = function(){
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        WebIM.conn
            .registerUser({ username, password })
            .then((res) => {
                document
                .getElementById("log")
                .appendChild(document.createElement("div"))
                .append(`register user ${username} success`);
            })
            .catch((e) => {
                document
                .getElementById("log")
                .appendChild(document.createElement("div"))
                .append(`${username} already exists`);
            });
    }
    // 登录
    document.getElementById("login").onclick = function () {
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        WebIM.conn
            .open({ user: username, pwd: password })
            .then((res) => {
                document
                .getElementById("log")
                .appendChild(document.createElement("div"))
                .append(`Login Success`);
            })
            .catch((e) => {
                document
                .getElementById("log")
                .appendChild(document.createElement("div"))
                .append(`Login failed`);
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
        let option = {
            chatType: 'singleChat',    // 设置为单聊
            type: 'txt',               // 消息类型
            to: peerId,                // 接收消息对象（用户 ID)
            msg: peerMessage           // 消息
        }
        let msg = WebIM.message.create(option); 
        WebIM.conn.send(msg).then((res) => {
            console.log('send private text success');
            document.getElementById("log").appendChild(document.createElement('div')).append("Message send to: " + peerId + " Message: " + peerMessage)
        }).catch(() => {
            console.log('send private text fail');
        })
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
        "easemob-websdk": "latest",
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

Easemob_quickstart
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

1. 在 `package.json` 中的 `dependencies` 字段中加入 `easemob-websdk` 及对应版本：

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
       "easemob-websdk": "latest"
     },
     "author": "",
     "license": "ISC"
   }
   ```

2. 在你的 JS 文件中导入 `easemob-websdk` 模块：

```JavaScript
import WebIM from 'easemob-websdk'
```

#### 方法二：从官网获取并导入 SDK

1. 下载 [Easemob Chat SDK for Web](https://www.easemob.com/download/im)。将 `demo/src/config` 中的 Easemob-chat 文件保存到你的项目下。

2. 在 HTML 文件中，对 JS 文件进行引用。

```JavaScript
   <script src="path to the JS file"></script>
```

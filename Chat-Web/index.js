import WebIM from 'agora-chat-sdk'
var username, password
// 初始化客户端
WebIM.conn = new WebIM.connection({
    appKey: "41117440#383391",
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
        document.getElementById("log").appendChild(document.createElement('div')).append("Message from: " + message.from + " Message: " + message.data)
    }, // 收到文本消息
    onTokenWillexpire: function (params) {
        document.getElementById("log").appendChild(document.createElement('div')).append("Token is about to expire")
        refreshToken(username, password)
    },
    onTokenExpired: function (params) {
        document.getElementById("log").appendChild(document.createElement('div')).append("The token has expired")
        refreshToken(username, password)
    },
    onError: function (error) {
        console.log('on error', error)
    }
})

// 重新获取并设置 agora token
function refreshToken(username, password) {
    postData('http://a41.easemob.com/app/chat/user/login', { "userAccount": username, "userPassword": password })
        .then((res) => {
            let agoraToken = res.accessToken
            WebIM.conn.resetToken(agoraToken)
        })
}

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
    document.getElementById("register").onclick = function () {
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        // 1.使用 token 方式
        postData('http://a41.easemob.com/app/chat/user/register', { "userAccount": username, "userPassword": password })
            .then((res) => {
                if (res.errorInfo && res.errorInfo.indexOf('already exists') !== -1) {
                    document.getElementById("log").appendChild(document.createElement('div')).append(`${username} already exists`)
                }
            })
        // 2.使用用户名密码的方式
        // WebIM.conn.registerUser({username, password})
        document.getElementById("log").appendChild(document.createElement('div')).append("register user"+username)
    }
    // 登录
    document.getElementById("login").onclick = function () {
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        // 1.使用 token 方式
        postData('http://a41.easemob.com/app/chat/user/login', { "userAccount": username, "userPassword": password })
            .then((res) => {
                let agoraToken = res.accessToken
                let easemobUserName = res.chatUserName
                WebIM.conn.open({
                    user: easemobUserName,
                    agoraToken: agoraToken
                });
            })
        // 2.使用用户名密码的方式
        // WebIM.conn.open({
        //     user: username,
        //     pwd: password,
        // });
    }

    // 登出
    document.getElementById("logout").onclick = function () {
        WebIM.conn.close();
        document.getElementById("log").appendChild(document.createElement('div')).append("logout")
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


import WebIM from 'agora-chat-sdk'
var username, password
// 初始化客户端
WebIM.conn = new WebIM.connection({
    appKey: "easemob-demo#easeim",
    isHttpDNS: false,
    https: true,
    url: 'http://im-api-v2.easemob.com/ws',
    apiUrl: 'http://a1.easemob.com',
    isDebug: true
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
    postData('http://a1-hsb.easemob.com/app/user/login', { "userAccount": username, "userPassword": password })
        .then((res) => {
            let agoraToken = res.accessToken
            WebIM.conn.resetToken(agoraToken)
        })
}

function postData(url, data) {
    // Default options are marked with *
    return fetch(url, {
        body: JSON.stringify(data), // must match 'Content-Type' header
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        // credentials: 'same-origin', // include, same-origin, *omit
        headers: {
            'content-type': 'application/json'
        },
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, cors, *same-origin
        redirect: 'follow', // manual, *follow, error
        referrer: 'no-referrer', // *client, no-referrer
    })
        .then(response => response.json()) // parses response to JSON
}

// 按钮行为定义
window.onload = function () {
    // 注册
    document.getElementById("register").onclick = function () {
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        // 使用 token 方式
        // postData('http://a1-hsb.easemob.com/app/user/register', { "userAccount": username, "userPassword": password })
        //     .then((res) => {
        //         if (res.errorInfo && res.errorInfo.indexOf('already exists') !== -1) {
        //             document.getElementById("log").appendChild(document.createElement('div')).append(`${username} already exists`)
        //         }
        //     })
        WebIM.conn.registerUser({username, password})
        document.getElementById("log").appendChild(document.createElement('div')).append("register user"+username)
    }
    // 登录
    document.getElementById("login").onclick = function () {
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        // 使用 token 方式
        // postData('http://a1-hsb.easemob.com/app/user/login', { "userAccount": username, "userPassword": password })
        //     .then((res) => {
        //         let agoraToken = res.accessToken
        //         let easemobUserName = res.easemobUserName
        //         console.log('-------', {
        //             user: easemobUserName,
        //             agoraToken: agoraToken,
        //             appKey: "easemob-demo#chatdemoui"
        //         })
        //         WebIM.conn.open({
        //             user: easemobUserName,
        //             agoraToken: agoraToken,
        //             appKey: "easemob-demo#chatdemoui"
        //         });
        //     })

        WebIM.conn.open({
            user: username,
            pwd: password,
            appKey: "easemob-demo#easeim"
        });
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


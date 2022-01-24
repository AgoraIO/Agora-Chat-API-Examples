import WebIM from 'agora-chat'
var username, password
WebIM.conn = new WebIM.connection({
    appKey: "41117440#383391",
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

// 重新获取并设置 agora token
function refreshToken(username, password) {
    postData('https://a41.easemob.com/app/chat/user/login', { "userAccount": username, "userPassword": password })
        .then((res) => {
            let agoraToken = res.accessToken
            WebIM.conn.renewToken(agoraToken)
            document.getElementById("log").appendChild(document.createElement('div')).append("Token has been updated")
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
        postData('https://a41.easemob.com/app/chat/user/register', { "userAccount": username, "userPassword": password })
            .then((res) => {
                document.getElementById("log").appendChild(document.createElement('div')).append(`register user ${username} success`)
            })
            .catch((res)=> {
                document.getElementById("log").appendChild(document.createElement('div')).append(`${username} already exists`)
            })
        // 2.使用用户名密码的方式
        // WebIM.conn.registerUser({username, password})
        // document.getElementById("log").appendChild(document.createElement('div')).append("register user "+username)
    }
    // 登录
    document.getElementById("login").onclick = function () {
        document.getElementById("log").appendChild(document.createElement('div')).append("Logging in...")
        username = document.getElementById("userID").value.toString()
        password = document.getElementById("password").value.toString()
        // 1.使用 token 方式
        postData('https://a41.easemob.com/app/chat/user/login', { "userAccount": username, "userPassword": password })
            .then((res) => {
                let agoraToken = res.accessToken
                let easemobUserName = res.chatUserName
                WebIM.conn.open({
                    user: easemobUserName,
                    agoraToken: agoraToken
                });
            })
            .catch((res)=> {
                document.getElementById("log").appendChild(document.createElement('div')).append(`Login failed`)
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
        let option = {
            chatType: 'singleChat',    // 设置为单聊
            type: 'txt',               // 消息类型
            to: 'userID',              // 接收消息对象（用户 ID)
            msg: 'message content'     // 消息
        }
        let msg = WebIM.message.create(option); 
        WebIM.conn.send(msg).then((res) => {
            console.log('send private text success');
            document.getElementById("log").appendChild(document.createElement('div')).append("Message send to: " + peerId + " Message: " + peerMessage)
        }).catch((err) => {
            console.log('send private text fail', err);
        })
    }
}


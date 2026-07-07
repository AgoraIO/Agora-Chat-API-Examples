import WebIM from 'agora-chat'
var username, accessToken
WebIM.conn = new WebIM.connection({
  appId: "your appId", // Replace with your Agora Chat SDK App ID
});
// Listen for connection and message events
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
        if (accessToken) {
            WebIM.conn.renewToken(accessToken)
            document.getElementById("log").appendChild(document.createElement('div')).append("Token has been updated")
        } else {
            document.getElementById("log").appendChild(document.createElement('div')).append("Please enter a new token and login again.")
        }
    },
    onTokenExpired: (params) => {
        document.getElementById("log").appendChild(document.createElement('div')).append("The token has expired, please login again.")
    },
    onError: (error) => {
        console.log('on error', error)
    }
})

// Button behavior definition
// login
document.getElementById("login").onclick = function () {
    document.getElementById("log").appendChild(document.createElement('div')).append("Logging in...")
    username = document.getElementById("userID").value.toString()
    accessToken = document.getElementById("token").value.toString()
    if (!username) {
        document.getElementById("log").appendChild(document.createElement('div')).append("User ID is required")
        return
    }
    if (!accessToken) {
        document.getElementById("log").appendChild(document.createElement('div')).append("Access token is required")
        return
    }

    WebIM.conn.open({
        user: username,
        accessToken
    });
}

// logout
document.getElementById("logout").onclick = function () {
    WebIM.conn.close();
    document.getElementById("log").appendChild(document.createElement('div')).append("logout")
}

// Send a single chat message
document.getElementById("send_peer_message").onclick = function () {
    let peerId = document.getElementById("peerId").value.toString()
    let peerMessage = document.getElementById("peerMessage").value.toString()
    let option = {
        chatType: 'singleChat',    // Set it to single chat
        type: 'txt',               // Message type
        to: peerId,                // The user receiving the message (user ID)
        msg: peerMessage           // The message content
    }
    let msg = WebIM.message.create(option);
    WebIM.conn.send(msg).then((res) => {
        console.log('send private text success');
        document.getElementById("log").appendChild(document.createElement('div')).append("Message send to: " + peerId + " Message: " + peerMessage)
    }).catch((err) => {
        console.log('send private text fail', err);
    })
}

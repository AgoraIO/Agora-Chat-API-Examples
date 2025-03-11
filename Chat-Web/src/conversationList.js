import WebIM from 'agora-chat'

// Get conversation list
document.getElementById("conversationList").onclick = function () {
    document.getElementById("log").appendChild(document.createElement('div')).append("getServerConversations...")
    WebIM.conn.getServerConversations().then((res) => {
        console.log('getServerConversations success',res)
        document.getElementById("log").appendChild(document.createElement('div')).append("getServerConversations success")
        let str='';
        res.data.conversations.map((item) => {
          str +=
            "\n" +
            JSON.stringify({
              conversationId: item.conversationId,
              conversationType: item.conversationType
            });
        });
        var odIV = document.createElement("div");
        odIV.style.whiteSpace = "pre";
        document.getElementById("log").appendChild(odIV).append('getServerConversations:', str)
    }).catch((e) => {
        console.log('getServerConversations failed',e)
        document.getElementById("log").appendChild(document.createElement('div')).append("getServerConversations failed")
    })
}

// Get roaming message
document.getElementById("roamingMessage").onclick = function () {
    document.getElementById("log").appendChild(document.createElement('div')).append("getRoamingMessage...")
    let converationId = document.getElementById("converationId").value.toString()
    WebIM.conn.getHistoryMessages({ targetId: converationId, chatType: "singleChat" }).then((res) => {
        console.log('getRoamingMessage success')
        document.getElementById("log").appendChild(document.createElement('div')).append("getRoamingMessage success")
        let str='';
        res.messages.map((item) => {
            str += '\n'+ JSON.stringify({
                messageId:item.id,
                messageType:item.type,
                from: item.from,
                to: item.to,
            }) 
        })
        var odIV = document.createElement("div");
        odIV.style.whiteSpace = "pre";
        document.getElementById("log").appendChild(odIV).append('roamingMessage:', str)
    }).catch(() => {
        document.getElementById("log").appendChild(document.createElement('div')).append("getRoamingMessage failed")
    })
}
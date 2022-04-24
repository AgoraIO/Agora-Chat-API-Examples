import WebIM from 'agora-chat'

// 按钮行为定义
//获取会话列表
document.getElementById("sessionList").onclick = function () {
    document.getElementById("log").appendChild(document.createElement('div')).append("getSessionList...")
    WebIM.conn.getSessionList().then((res) => {
        console.log('getSessionList success')
        document.getElementById("log").appendChild(document.createElement('div')).append("getSessionList success")
        let str='';
        res.data.channel_infos.map((item) => {
            const chanelId = item.channel_id;
            let reg = /(?<=_).*?(?=@)/;
            const username = chanelId.match(reg)[0];
            str += '\n'+ JSON.stringify({
                conversationId:username,
                conversationType:chanelId.indexOf('@conference.easemob.com')>=0 ? 'groupChat':'singleChat'
            })
        })
        var odIV = document.createElement("div");
        odIV.style.whiteSpace = "pre";
        document.getElementById("log").appendChild(odIV).append('sessionList:', str)
    }).catch(() => {
        document.getElementById("log").appendChild(document.createElement('div')).append("getSessionList failed")
    })
}

//获取漫游消息
document.getElementById("roamingMessage").onclick = function () {
    document.getElementById("log").appendChild(document.createElement('div')).append("getRoamingMessage...")
    let converationId = document.getElementById("converationId").value.toString()
    WebIM.conn.getHistoryMessages({ targetId: converationId }).then((res) => {
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
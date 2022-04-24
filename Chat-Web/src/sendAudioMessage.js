import WebIM from 'agora-chat'
import recorder from '../utils/recordAudio'
var _startTime, _endTime, recorderObj, time = 60, timer = null;

// 添加回调函数
WebIM.conn.addEventHandler('audioMessage', {
    onAudioMessage: (message) => {
        document.getElementById("log").appendChild(document.createElement('div')).append("Message from: " + message.from + " Type: " + message.type)
    }
})

// 按钮行为定义

//发送一条音频消息
document.getElementById("send_audio_message").onclick = function () {
    document.getElementById("recordBox").style.display = 'block';
}
//录制音频并发送音频消息
document.getElementById("recordBox").onclick = function () {
    let step = document.getElementById("recordBox").textContent.toString();
    if (step === 'start recording') {
        //开始录音
        document.getElementById("recordBox").textContent = 'recording';
        _startTime = new Date().getTime();
        window.clearInterval(timer)

        recorder.get((rec, val) => {
            recorderObj = rec;
            MediaStream = val
            if (rec) {
                timer = setInterval(() => {
                    if (time <= 0) {
                        rec.stop();
                        time = 60
                        timer = null;
                        window.clearInterval(timer)
                    } else {
                        time--;
                        rec.start();
                    }
                }, 1000);
            }
        });
    } else if (step === 'recording') {
        //停止录音，发消息
        window.clearInterval(timer)
        let targetId = document.getElementById("peerId").value.toString()
        _endTime = new Date().getTime();
        let duration = (_endTime - _startTime) / 1000;
        if (recorderObj) {
            recorderObj.stop();
            // 重置说话时间
            time = 60;
            // 获取语音二进制文件
            let blob = recorderObj.getBlob();
            // 发送语音功能
            const uri = {
                url: WebIM.utils.parseDownloadResponse.call(WebIM.conn, blob),
                filename: "audio-message.wav",
                filetype: "audio",
                data: blob,
                length: duration,
                duration: duration,
            };
            MediaStream.getTracks()[0].stop()
            let option = {
                chatType: 'singleChat',             // 会话类型，设置为单聊。
                type: 'audio',                      // 消息类型，设置为音频。
                to: targetId,                       // 消息接收方。
                file: uri,
                filename: uri.filename,
                onFileUploadError: function () {
                    // 消息上传失败。      
                    console.log('onFileUploadError');
                },
                onFileUploadProgress: function (e) {
                    // 上传进度的回调。
                    console.log('onFileUploadProgress', e)
                },
                onFileUploadComplete: function () {
                    // 消息上传成功。
                    console.log('onFileUploadComplete');
                }
            };
            let msg = WebIM.message.create(option);
            WebIM.conn.send(msg).then((res) => {
                console.log('send private audio success');
                document.getElementById("recordBox").style.display = 'none';
                document.getElementById("recordBox").textContent = 'start recording';
                document.getElementById("log").appendChild(document.createElement('div')).append("Message send to: " + targetId + " Type: " + msg.type)
            }).catch((err) => {
                console.log('send private audio fail', err);
            })
        }

    }
}
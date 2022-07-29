import WebIM from 'agora-chat'
import recorder from '../utils/recordAudio'
var _startTime, _endTime, recorderObj, time = 60, timer = null, MediaStream;

WebIM.conn.addEventHandler('audioMessage', {
    onAudioMessage: (message) => {
        document.getElementById("log").appendChild(document.createElement('div')).append("Message from: " + message.from + " Type: " + message.type)
    }
})


// Send an audio message
document.getElementById("send_audio_message").onclick = function () {
    document.getElementById("recordBox").style.display = 'block';
}
// Record audio and send audio messages
document.getElementById("recordBox").onclick = function () {
    let step = document.getElementById("recordBox").textContent.toString();
    if (step === 'start recording') {
        // Start the recording
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
        // Stop recording and send a message
        window.clearInterval(timer)
        let targetId = document.getElementById("peerId").value.toString()
        _endTime = new Date().getTime();
        let duration = (_endTime - _startTime) / 1000;
        if (recorderObj) {
            recorderObj.stop();
            // Reset speaking time
            time = 60;
            // Get the speech binaries
            let blob = recorderObj.getBlob();
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
                chatType: 'singleChat',
                type: 'audio',
                to: targetId,
                file: uri,
                filename: uri.filename,
                onFileUploadError: function () {
                    // Failed to upload message.     
                    console.log('onFileUploadError');
                },
                onFileUploadProgress: function (e) {
                    // Callback of upload progress.
                    console.log('onFileUploadProgress', e)
                },
                onFileUploadComplete: function () {
                    // The message was uploaded successfully.
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
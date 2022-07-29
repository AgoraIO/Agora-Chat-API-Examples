
window.URL = window.URL || window.webkitURL

const HZRecorder = function(stream, config){
    config = config || {}
    config.sampleBits = config.sampleBits || 16 
    config.sampleRate = config.sampleRate || (16000)

    var context = new AudioContext()
    var audioInput = context.createMediaStreamSource(stream)
    var recorder = context.createScriptProcessor(16 * 1024, 1, 1)
    var emptyCheckCount = 0
    var emptyDatacount = 0

    const interpolateArray = (data, newSampleRate, oldSampleRate) => {
        var fitCount = Math.round(data.length * (newSampleRate / oldSampleRate))
        var newData = new Array()
        var springFactor = new Number((data.length - 1) / (fitCount - 1))
        newData[0] = data[0] // for new allocation
        for(var i = 1; i < fitCount - 1; i++){
            var tmp = i * springFactor
            var before = new Number(Math.floor(tmp)).toFixed()
            var after = new Number(Math.ceil(tmp)).toFixed()
            var atPoint = tmp - before
            newData[i] = linearInterpolate(data[before], data[after], atPoint)
        }
        newData[fitCount - 1] = data[data.length - 1] // for new allocation
        return newData
    }
    const linearInterpolate = (before, after, atPoint) => {
        return before + (after - before) * atPoint
    }

    var audioData = {
        size: 0, // Length of recording file
        buffer: [], // Recording the cache
        inputSampleRate: context.sampleRate, // Input sampling rate
        inputSampleBits: 16, // Input sampling digits 8, 16
        outputSampleRate: config.sampleRate, // Output sampling rate
        outputSampleBits: config.sampleBits, // Output sampling digit 8, 16
        input: function(data){
            this.buffer.push(new Float32Array(data))
            this.size += data.length
        },
        compress: function(){
            // change sampleRate
            var data = new Float32Array(this.size)
            var offset = 0
            for(var i = 0; i < this.buffer.length; i++){
                data.set(this.buffer[i], offset)
                offset += this.buffer[i].length
            }

            var result = interpolateArray(data, this.outputSampleRate, this.inputSampleRate)

            return result
        },
        encodeWAV: function(){
            var sampleRate = Math.min(this.inputSampleRate, this.outputSampleRate)
            var sampleBits = Math.min(this.inputSampleBits, this.outputSampleBits)
            var bytes = this.compress()
            var dataLength = bytes.length * (sampleBits / 8)
            var buffer = new ArrayBuffer(44 + dataLength)
            var data = new DataView(buffer)

            var channelCount = 1// mono
            var offset = 0

            var writeString = function(str){
                for(var i = 0; i < str.length; i++){
                    data.setUint8(offset + i, str.charCodeAt(i))
                }
            }

            // Resource exchange file identifier
            writeString('RIFF')
            offset += 4
            // The total number of bytes from the next address to the end of the file, i.e. the file size -8
            data.setUint32(offset, 36 + dataLength, true)
            offset += 4
            // WAV file flag
            writeString('WAVE')
            offset += 4
            // Waveform format mark
            writeString('fmt ')
            offset += 4
            // Filter bytes, usually 0x10 = 16
            data.setUint32(offset, 16, true)
            offset += 4
            // Format category (PCM sampled data)
            data.setUint16(offset, 1, true)
            offset += 2
            // The channel number
            data.setUint16(offset, channelCount, true)
            offset += 2
            // Sampling rate, the number of samples per second, indicates the playback speed of each channel
            data.setUint32(offset, sampleRate, true)
            offset += 4
            // Waveform data transmission rate (average number of bytes per second) Mono x data bits per second x data bits per sample /8
            data.setUint32(offset, channelCount * sampleRate * (sampleBits / 8), true)
            offset += 4
            // Fast data adjustment number of bytes consumed per sample monophonic x number of data bits per sample /8
            data.setUint16(offset, channelCount * (sampleBits / 8), true)
            offset += 2
            // Per sample data bit
            data.setUint16(offset, sampleBits, true)
            offset += 2
            // Data identifier
            writeString('data')
            offset += 4
            // The total number of sampled data, namely the total size of data, is -44
            data.setUint32(offset, dataLength, true)
            offset += 4
            // Write sampled data
            if(sampleBits === 8){
                for(var i = 0; i < bytes.length; i++, offset++){
                    var s = Math.max(-1, Math.min(1, bytes[i]))
                    var val = s < 0 ? s * 0x8000 : s * 0x7FFF
                    val = parseInt(255 / (65535 / (val + 32768)))
                    data.setInt8(offset, val, true)
                }
            }
            else{
                for(var i = 0; i < bytes.length; i++, offset += 2){
                    var s = Math.max(-1, Math.min(1, bytes[i]))
                    data.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true)
                }
            }

            return new Blob([ data ], { type: 'audio/wav' })
        }
    }

    // Start the recording
    this.start = function(){
        audioInput.connect(recorder)
        recorder.connect(context.destination)
    }
    this.isEmptyData = function(d){
        // Basically confirm that the sampled voice data is null, that is, there is no voice input
        var l = Math.floor(d.length / 10)
        var vol = 0
        for(var i = 0; i < l; i++){
            vol += Math.abs(d[i * 10])
        }
        emptyCheckCount++
        if(vol < 10){
            emptyDatacount++

            if(emptyDatacount > 10){
                // recording = false;
                // this.stop();
                console.log('stoped')
                return true
            }
        }
        else{
            emptyDatacount = 0
        }
        return false
    }
    // stop
    this.stop = function(){
        if(context.state === 'running'){
            context.close()
        }

        recorder.disconnect()
    }

    // Get audio files
    this.getBlob = function(){
        this.stop()
        return audioData.encodeWAV()
    }

    // play
    this.play = function(audio){
        audio.src = window.URL.createObjectURL(this.getBlob())
    }

    // Audio collection
    recorder.onaudioprocess = (e) => {
        audioData.input(e.inputBuffer.getChannelData(0))
    }
}
HZRecorder.setErrorInfoText = (errorMessage) => {
    HZRecorder.errorMessage = errorMessage
}

// get recorder
HZRecorder.get = function(callback, config){
    if(callback){
            navigator.mediaDevices.getUserMedia(
                { audio: true } // 只启用音频
                ).then((suc)=>{
                    let rec = new HZRecorder(suc, config)
                    callback(rec,suc)
                }).catch((error)=>{
                    switch(error.code || error.name){
                        case 'PERMISSION_DENIED':
                        case 'PermissionDeniedError':
                            console.log('用户拒绝提供信息。')
                            break
                        case 'NOT_SUPPORTED_ERROR':
                        case 'NotSupportedError':
                            console.log('浏览器不支持硬件设备。')
                            break
                        case 'MANDATORY_UNSATISFIED_ERROR':
                        case 'MandatoryUnsatisfiedError':
                            console.log('无法发现指定的硬件设备。')
                            break
                        default:
                            console.log('当前浏览器不支持录音功能。(建议使用Chrome)')
                            break
                        }
                    console.log('当前浏览器不支持录音功能。')
                })
    }
}
export default HZRecorder

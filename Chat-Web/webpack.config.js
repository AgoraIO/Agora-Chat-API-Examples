const path = require('path');

module.exports = {
    entry: ['./src/index.js','./src/conversationList.js','./src/sendAudioMessage.js'],
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
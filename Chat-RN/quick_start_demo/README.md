# Agora Chat IM React-Native Quick Start

_English | [中文](README.zh.md)_

## Overview

This article mainly introduces how to quickly integrate the SDK to receive and send messages.

This page shows a sample code to add peer-to-peer messaging into a Windows project by using the Agora Chat SDK.

![image](./res/main.png)

## Project structure

The project uses a single app to combine a variety of functionalities.

| Function              | Location           |
| --------------------- | ------------------ |
| Init React Native SDK | [App.js](./App.js) |
| Set Connect listener  | [App.js](./App.js) |
| Register account      | [App.js](./App.js) |
| Login to server       | [App.js](./App.js) |
| Logout from server    | [App.js](./App.js) |
| Send text message     | [App.js](./App.js) |
| receive message       | [App.js](./App.js) |

## How to run the sample project

### Prerequisites

Before proceeding, ensure that your development and run environment meets the following requirements.

If your target platform is iOS:

- MacOS 10.15.7 or above
- Xcode 12.4 or above, including command line tools
- React Native 0.63.4 or later
- NodeJs 16 or above, including npm package management tool
- CocoaPods package management tool
- Yarn compile and run tool
- Watchman debugging tool
- A physical or virtual mobile device running iOS 11.0 or later

If your target platform is Android:

- MacOS 10.15.7 or above, Windows 10 or above
- Android Studio 4.0 or above, including JDK 1.8 or above
- React Native 0.63.4 or later
- CocoaPods package management tool if your operating system is Macos.
- Powershell 5.1 or above installed if your operating system is Windows.
- NodeJs 16 or above, including npm package management tool
- Yarn compile and run tool
- Watchman debugging tool
- A physical or virtual mobile device running Android 6.0 or later

For more information, see [RN dev](https://reactnative.dev/).

### Steps to run

#### Create a demo project.

1. Make sure you have set up the development environment based on your operating system and target platform.
2. In your terminal, run the following command to create a React Native project.
   ```sh
   npx react-native init token_login_demo
   cd token_login_demo
   yarn
   ```
   A successful execution of this command generates a project named `token_login_demo` in the directory that you run the command.
3. Run the following command to import the Chat SDK using yarn:
   ```sh
   yarn add react-native-agora-chat
   ```
4. Execute the scripts or tools according to your target platform.
   If your target platform is iOS:
   ```sh
   cd ios && pod install && cd ..
   ```

#### Implement sample code.

To send a one-to-one message, chat users should register a Chat account, log into Agora Chat, and send a text message.

Open `token_login_demo/App.js`, and replace the code with the following:

```javascript
// Import depend packages.
import React, {useEffect} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import {
  ChatClient,
  ChatOptions,
  ChatMessageChatType,
  ChatMessage,
} from 'react-native-agora-chat';

// The App Object.
const App = () => {
  // The variable defines.
  const title = 'AgoraChatQuickstart';
  const requestGetTokenUrl = 'https://a41.chat.agora.io/app/chat/user/login';
  const requestRegistryAccountUrl =
    'https://a41.chat.agora.io/app/chat/user/register';
  const appKey = '81446724#514456';
  const [username, setUsername] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [userId, setUserId] = React.useState('');
  const [content, setContent] = React.useState('');
  const [logText, setWarnText] = React.useState('Show log area');
  const chatClient = ChatClient.getInstance();
  const chatManager = chatClient.chatManager;

  // output console log.
  useEffect(() => {
    logText.split('\n').forEach((value, index, array) => {
      if (index === 0) {
        console.log(value);
      }
    });
  }, [logText]);

  // Output UI logs.
  const rollLog = text => {
    setWarnText(preLogText => {
      let newLogText = text;
      preLogText
        .split('\n')
        .filter((value, index, array) => {
          if (index > 8) {
            return false;
          }
          return true;
        })
        .forEach((value, index, array) => {
          newLogText += '\n' + value;
        });
      return newLogText;
    });
  };

  useEffect(() => {
    // Register listeners for messaging.
    const setMessageListener = () => {
      let msgListener = {
        onMessagesReceived(messages) {
          for (let index = 0; index < messages.length; index++) {
            rollLog('received msgId: ' + messages[index].msgId);
          }
        },
        onCmdMessagesReceived: messages => {},
        onMessagesRead: messages => {},
        onGroupMessageRead: groupMessageAcks => {},
        onMessagesDelivered: messages => {},
        onMessagesRecalled: messages => {},
        onConversationsUpdate: () => {},
        onConversationRead: (from, to) => {},
      };

      chatManager.removeAllMessageListener();
      chatManager.addMessageListener(msgListener);
    };

    // Initialize the SDK.
    // Initialize any interface before calling it.
    const init = () => {
      let o = new ChatOptions({
        autoLogin: false,
        appKey: appKey,
      });
      chatClient.removeAllConnectionListener();
      chatClient
        .init(o)
        .then(() => {
          rollLog('init success');
          this.isInitialized = true;
          let listener = {
            onTokenWillExpire() {
              rollLog('token expire.');
            },
            onTokenDidExpire() {
              rollLog('token did expire');
            },
            onConnected() {
              rollLog('onConnected');
              setMessageListener();
            },
            onDisconnected(errorCode) {
              rollLog('onDisconnected:' + errorCode);
            },
          };
          chatClient.addConnectionListener(listener);
        })
        .catch(error => {
          rollLog(
            'init fail: ' +
              (error instanceof Object ? JSON.stringify(error) : error),
          );
        });
    };

    init();
  }, [chatClient, chatManager, appKey]);

  const requestHttp = url => {
    rollLog(`requestHttp: userAccount: ${username}, userPassword: ${password}`);
    return fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        userAccount: username,
        userPassword: password,
      }),
    });
  };
  const requestGetToken = () => {
    return requestHttp(requestGetTokenUrl);
  };
  const requestRegistryAccount = () => {
    return requestHttp(requestRegistryAccountUrl);
  };

  // Register an account for login.
  const registerAccount = () => {
    if (this.isInitialized === false || this.isInitialized === undefined) {
      rollLog('Perform initialization first.');
      return;
    }
    rollLog('start register account ...');
    requestRegistryAccount()
      .then(response => {
        response
          .json()
          .then(value => {
            if (value.code === 'RES_OK') {
              rollLog(
                `register success: userName = ${username}, password = ${password}`,
              );
            } else {
              rollLog('response token fail:' + JSON.stringify(value));
            }
          })
          .catch(error => {
            rollLog('response token fail:' + JSON.stringify(error));
          });
      })
      .catch(error => {
        rollLog('register fail: ' + JSON.stringify(error));
      });
  };

  // Log in with an account ID and token.
  const loginWithToken = () => {
    if (this.isInitialized === false || this.isInitialized === undefined) {
      rollLog('Perform initialization first.');
      return;
    }
    rollLog('start request token ...');
    requestGetToken()
      .then(response => {
        rollLog('request token success.');
        response
          .json()
          .then(value => {
            if (value.code === 'RES_OK') {
              rollLog(
                `response token success: username = ${username}, token = ${value.accessToken}`,
              );
              const token = value.accessToken;
              rollLog('start login ...');
              chatClient
                .loginWithAgoraToken(username, token)
                .then(() => {
                  rollLog('login operation success.');
                })
                .catch(reason => {
                  rollLog('login fail: ' + JSON.stringify(reason));
                });
            } else {
              rollLog('response token fail:' + JSON.stringify(value));
            }
          })
          .catch(error => {
            rollLog('response token fail:' + JSON.stringify(error));
          });
      })
      .catch(error => {
        rollLog('request token fail: ' + JSON.stringify(error));
      });
  };

  // Log out from server.
  const logout = () => {
    if (this.isInitialized === false || this.isInitialized === undefined) {
      rollLog('Perform initialization first.');
      return;
    }
    rollLog('start logout ...');
    chatClient
      .logout()
      .then(() => {
        rollLog('logout success.');
      })
      .catch(reason => {
        rollLog('logout fail:' + JSON.stringify(reason));
      });
  };

  // Send a text message to somebody.
  const sendmsg = () => {
    if (this.isInitialized === false || this.isInitialized === undefined) {
      rollLog('Perform initialization first.');
      return;
    }
    let msg = ChatMessage.createTextMessage(
      userId,
      content,
      ChatMessageChatType.PeerChat,
    );
    const callback = new (class {
      onProgress(locaMsgId, progress) {
        rollLog(`send message process: ${locaMsgId}, ${progress}`);
      }
      onError(locaMsgId, error) {
        rollLog(`send message fail: ${locaMsgId}, ${JSON.stringify(error)}`);
      }
      onSuccess(message) {
        rollLog('send message success: ' + message.localMsgId);
      }
    })();
    rollLog('start send message ...');
    chatClient.chatManager
      .sendMessage(msg, callback)
      .then(() => {
        rollLog('send message: ' + msg.localMsgId);
      })
      .catch(reason => {
        rollLog('send fail: ' + JSON.stringify(reason));
      });
  };

  // Render the UI.
  return (
    <SafeAreaView>
      <View style={styles.titleContainer}>
        <Text style={styles.title}>{title}</Text>
      </View>
      <ScrollView>
        <View style={styles.inputCon}>
          <TextInput
            multiline
            style={styles.inputBox}
            placeholder="Enter username"
            onChangeText={text => setUsername(text)}
            value={username}
          />
        </View>
        <View style={styles.inputCon}>
          <TextInput
            multiline
            style={styles.inputBox}
            placeholder="Enter password"
            onChangeText={text => setPassword(text)}
            value={password}
          />
        </View>
        <View style={styles.buttonCon}>
          <Text style={styles.eachBtn} onPress={registerAccount}>
            SIGN UP
          </Text>
          <Text style={styles.eachBtn} onPress={loginWithToken}>
            SIGN IN
          </Text>
          <Text style={styles.eachBtn} onPress={logout}>
            SIGN OUT
          </Text>
        </View>
        <View style={styles.inputCon}>
          <TextInput
            multiline
            style={styles.inputBox}
            placeholder="Enter the username you want to send"
            onChangeText={text => setUserId(text)}
            value={userId}
          />
        </View>
        <View style={styles.inputCon}>
          <TextInput
            multiline
            style={styles.inputBox}
            placeholder="Enter content"
            onChangeText={text => setContent(text)}
            value={content}
          />
        </View>
        <View style={styles.buttonCon}>
          <Text style={styles.btn2} onPress={sendmsg}>
            SEND TEXT
          </Text>
        </View>
        <View>
          <Text style={styles.logText} multiline={true}>
            {logText}
          </Text>
        </View>
        <View>
          <Text style={styles.logText}>{}</Text>
        </View>
        <View>
          <Text style={styles.logText}>{}</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

// Sets UI styles.
const styles = StyleSheet.create({
  titleContainer: {
    height: 60,
    backgroundColor: '#6200ED',
  },
  title: {
    lineHeight: 60,
    paddingLeft: 15,
    color: '#fff',
    fontSize: 20,
    fontWeight: '700',
  },
  inputCon: {
    marginLeft: '5%',
    width: '90%',
    height: 60,
    paddingBottom: 6,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
  },
  inputBox: {
    marginTop: 15,
    width: '100%',
    fontSize: 14,
    fontWeight: 'bold',
  },
  buttonCon: {
    marginLeft: '2%',
    width: '96%',
    flexDirection: 'row',
    marginTop: 20,
    height: 26,
    justifyContent: 'space-around',
    alignItems: 'center',
  },
  eachBtn: {
    height: 40,
    width: '28%',
    lineHeight: 40,
    textAlign: 'center',
    color: '#fff',
    fontSize: 16,
    backgroundColor: '#6200ED',
    borderRadius: 5,
  },
  btn2: {
    height: 40,
    width: '45%',
    lineHeight: 40,
    textAlign: 'center',
    color: '#fff',
    fontSize: 16,
    backgroundColor: '#6200ED',
    borderRadius: 5,
  },
  logText: {
    padding: 10,
    marginTop: 10,
    color: '#ccc',
    fontSize: 14,
    lineHeight: 20,
  },
});

export default App;
```

#### Build and run your project

You are now ready to build and run the project your built!

To build and run the project on an iOS device, take the following steps:

1. Connect an iPhone device to your computer and set the device to Developer mode.
2. Open `token_login_demo/ios` and open `token_login_demo.xcworkspace` with Xcode.
3. In **Targets** > **token_login_demo** > **Signing & Capabilities**, set the signing of the project.
4. Click `Build` in Xcode to build the project. When the build succeeds, Xcode runs the project and installs it on your device. You see the app user interface.

To build and run the project on an iOS simulator, take the following steps:

1. Open `token_login_demo/ios` and open `token_login_demo.xcworkspace` with Xcode.
2. In Xcode, set `iPhone (12.4)` as the iOS simulator.
3. Click `Build` in Xcode to build the project. When the build succeeds, Xcode runs the project and installs it on the simulator. You see the app user interface.

To build and run the project on an Android device, take the following steps:

1. Open `token_login_demo/android` in Android Studio.
2. Connect an Android device to your computer and set the device to USB debugging mode.
3. In terminal, type in `adb reverse tcp:8081 tcp:8081` to set up data forwarding.
4. Run the following command to execute `"start": "react-native start"` in `package.json`:

```sh
yarn start
```

1. Click `Build` in Android Studio to build the project. When the build succeeds, Android Studio runs the project and installs it on the device. You see the app interface.

#### Test your app

Refer to the following steps to register a Chat account, log into Agora Chat and send and receive a message.

1. On one device or simulator, enter a username and password, click **SIGN UP** to register a Chat account.
2. Click **SIGN IN** to log into Agora Chat.
3. On a second device or simulator, repeat the above steps to create another account and log into Agora Chat. Ensure that you use a different user ID (username) on this device or simulator.
4. From the first device or simulator, enter the username you set in step 3, type in the text message you want to send, and click **SEND TEXT**. You can receive the text message from the other device or simulator.

You can also read from the logs below to see whether you have successfully signed up, signed in, and sent a text message.

## Feedback

If you have any problems or suggestions regarding the sample projects, feel free to file an issue.

## Reference

- [Product Overview](https://docs.agora.io/en/Interactive%20Broadcast/product_live?platform=React%20Native)
- [API Reference](https://docs.agora.io/en/Interactive%20Broadcast/API%20Reference/react_native/index.html)

## Related resources

- Check our [FAQ](https://docs.agora.io/en/faq) to see if your issue has been recorded.
- Dive into [Agora SDK Samples](https://github.com/AgoraIO) to see more tutorials
- Take a look at [Agora Use Case](https://github.com/AgoraIO-usecase) for more complicated real use case
- Repositories managed by developer communities can be found at [Agora Community](https://github.com/AgoraIO-Community)
- If you encounter problems during integration, feel free to ask questions in [Stack Overflow](https://stackoverflow.com/questions/tagged/agora.io)

## Known issues

Please do not use the latest node version 18, do not use the latest version 0.69 of react-native, and the latest version 18 of react. They have big compatibility issues with previous versions.

## License

The example project is under the MIT license.

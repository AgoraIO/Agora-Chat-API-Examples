# API Example iOS

_[English](README.md) | 中文_

## 简介

本文介绍如何极简集成 Agora 即时通讯 React-Native SDK，在你的 app 中实现发送和接收单聊文本消息。

![image](./res/main.png)

## 项目结构

此项目使用一个单独的 app 实现了多种功能。

| Function              | Location           |
| --------------------- | ------------------ |
| Init React Native SDK | [App.js](./App.js) |
| Set Connect listener  | [App.js](./App.js) |
| Register account      | [App.js](./App.js) |
| Login to server       | [App.js](./App.js) |
| Logout from server    | [App.js](./App.js) |
| Send text message     | [App.js](./App.js) |
| receive message       | [App.js](./App.js) |

## 如何运行示例项目

### 前提条件

集成前请确认 app 的开发和运行环境满足以下要求：

对于 iOS 平台：

- MacOS 10.15.7 或以上版本
- Xcode 12.4 或以上版本，包括命令行工具
- React Native 0.63.4 或以上版本
- NodeJs 16 或以上版本，包含 npm 包管理工具
- CocoaPods 包管理工具
- Yarn 编译运行工具
- Watchman 调试工具
- 运行环境真机或模拟器 iOS 11.0 或以上版本

对于 Android 平台：

- MacOS 10.15.7 或以上版本，Windows 10 或以上版本
- Android Studio 4.0 或以上版本，包括 JDK 1.8 或以上版本
- React Native 0.63.4 或以上版本
- 如果用 Macos 系统开发，需要 CocoaPods 包管理工具
- 如果用 Windows 开发，需要 Powershell 5.1 或以上版本
- NodeJs 16 或以上版本，包含 npm 包管理工具
- Yarn 编译运行工具
- Watchman 调试工具
- 运行环境真机或模拟器 Android 6.0 或以上版本

配置开发或者运行环境如果遇到问题，请参考 [RN 官网](https://reactnative.dev/)。

### 运行步骤

#### 创建一个演示项目。

创建一个 React Native 项目并将 Agora Chat 集成进去

1. 根据开发系统和目标平台准备开发环境；
2. 打开终端，进入需要创建项目的目录，输入命令创建 React Native 项目：

   ```sh
   npx react-native init token_login_demo
   cd token_login_demo
   yarn
   ```

   创建好的项目名称是 `token_login_demo`。

3. 在终端命令行，输入以下命令添加依赖：

   ```sh
   yarn add react-native-agora-chat
   ```

4. 在目标平台执行脚本

iOS：

```sh
cd ios && pod install && cd ..
```

#### 实现样例代码

要发送一对一消息，聊天用户需要注册一个 Chat 帐户，登录 Agora Chat，然后发送消息。

打开 `token_login_demo/App.js`，将代码替换为以下内容：

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
  // The settings.
  const title = 'AgoraChatQuickstart';
  const [appKey, setAppKey] = React.useState('81446724#514456');
  const [username, setUsername] = React.useState('asterisk0020');
  const [password, setPassword] = React.useState('qwer');
  const [userId, setUserId] = React.useState('');
  const [content, setContent] = React.useState('');
  const [logText, setWarnText] = React.useState('Show log area');

  // Output the console log.
  useEffect(() => {
    logText.split('\n').forEach((value, index, array) => {
      if (index === 0) {
        console.log(value);
      }
    });
  }, [logText]);

  // Output the UI log.
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

  const requestHttp = url => {
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
    return requestHttp('https://a1.chat.agora.io/app/chat/user/login');
  };
  const requestRegistryAccount = () => {
    return requestHttp('https://a1.chat.agora.io/app/chat/user/register');
  };

  // Register the listener for message.
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

    ChatClient.getInstance().chatManager.removeAllMessageListener();
    ChatClient.getInstance().chatManager.addMessageListener(msgListener);
  };

  // Initialize sdk.
  // Please initialize before calling any interface.
  const init = () => {
    let o = new ChatOptions({
      autoLogin: false,
      appKey: appKey,
    });
    ChatClient.getInstance().removeAllConnectionListener();
    ChatClient.getInstance()
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
            rollLog('login success.');
            setMessageListener();
          },
          onDisconnected(errorCode) {
            rollLog('login fail: ' + errorCode);
          },
        };
        ChatClient.getInstance().addConnectionListener(listener);
      })
      .catch(error => {
        rollLog(
          'init fail: ' +
            (error instanceof Object ? JSON.stringify(error) : error),
        );
      });
  };

  // Register an account.
  const registerAccount = () => {
    if (this.isInitialized === false || this.isInitialized === undefined) {
      rollLog('Perform initialization first.');
      return;
    }
    rollLog('start register account ...');
    requestRegistryAccount()
      .then(response => {
        rollLog(`register success: userName = ${username}, password = ******`);
      })
      .catch(error => {
        rollLog('register fail: ' + JSON.stringify(error));
      });
  };

  // Login with account ID and token.
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
            rollLog(
              `response token success: username = ${username}, token = ******`,
            );
            const token = value.accessToken;
            rollLog('start login ...');
            ChatClient.getInstance()
              .loginWithAgoraToken(username, token)
              .then(() => {
                rollLog('login operation success.');
              })
              .catch(reason => {
                rollLog('login fail: ' + JSON.stringify(reason));
              });
          })
          .catch(error => {
            rollLog('response token fail:' + JSON.stringify(error));
          });
      })
      .catch(error => {
        rollLog('request token fail: ' + JSON.stringify(error));
      });
  };

  // Logout from server.
  const logout = () => {
    if (this.isInitialized === false || this.isInitialized === undefined) {
      rollLog('Perform initialization first.');
      return;
    }
    rollLog('start logout ...');
    ChatClient.getInstance()
      .logout()
      .then(() => {
        rollLog('logout success.');
      })
      .catch(reason => {
        rollLog('logout fail:' + JSON.stringify(reason));
      });
  };

  // Send a text message.
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
    ChatClient.getInstance()
      .chatManager.sendMessage(msg, callback)
      .then(() => {
        rollLog('send message: ' + msg.localMsgId);
      })
      .catch(reason => {
        rollLog('send fail: ' + JSON.stringify(reason));
      });
  };

  // The UI render.
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
            placeholder="Enter appkey"
            onChangeText={text => setAppKey(text)}
            value={appKey}
          />
        </View>
        <View style={styles.buttonCon}>
          <Text style={styles.btn2} onPress={init}>
            INIT SDK
          </Text>
        </View>
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

// Set UI styles.
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

#### 编译和运行项目

现在你可以开始在目标平台创建和运行项目。

编译并在 iOS 真机运行：

1. 连接苹果手机，设置为开发者模式；
2. 打开 `token_login_demo/ios`，使用 `xcode` 打开 `token_login_demo.xcworkspace`；
3. 依次点击 **Targets** > **token_login_demo** > **Signing & Capabilities** 在签名选项下设置应用签名；
4. 点击 `Build` 构建并运行项目。程序构建完成后，自动安装和运行，并显示应用界面。

编译并在 iOS 模拟器中运行：

1. 打开 `token_login_demo/ios`，使用 `xcode` 打开 `token_login_demo.xcworkspace`；
2. 在 `xcode` 中，选择模拟器 `iphone13`；
3. 点击 `Build` 构建并运行项目。程序构建完成后，自动安装和运行，并显示应用界面。

编译并在 Android 真机运行：

1. 在 Android Studio 中打开 `token_login_demo/android`；
2. 连接 Android 系统手机，设置为开发者模式，并且设置 USB 可调式；
3. 设置数据转发：在终端命令行输入 `adb reverse tcp:8081 tcp:8081`；
4. 启动服务：执行 `package.json` 里面的命令：`"start": "react-native start"`，在终端中运行命令 `yarn start`：

   ```sh
   yarn start
   ```

5. 程序构建完成后，自动安装和运行，并显示应用界面。

#### 测试你的 app

参考以下代码测试注册账号，登录，发送和接收消息。

1. 在真机或模拟器上输入用户名和密码，点击 **注册**。
2. 点击 **登录**。
3. 在另一台真机或模拟器上注册和登录一个新用户。
4. 在第一台真机或模拟器上输入第二台上的用户名，编辑消息并点击 **发送**，在第二台机器上接收消息。

同时你可以在下方查看日志，检查注册，登录，发送消息是否成功。

## 反馈

如果你有任何问题或建议，可以通过 issue 的形式反馈。

## 参考文档

- [RTC Objective-C SDK 产品概述](https://docs.agora.io/en/Interactive%20Broadcast/product_live?platform=React%20Native)
- [RTC Objective-C SDK API 参考](https://docs.agora.io/en/Interactive%20Broadcast/API%20Reference/react_native/index.html)

## 相关资源

- 你可以先参阅 [常见问题](https://docs.agora.io/cn/faq)
- 如果你想了解更多官方示例，可以参考 [官方 SDK 示例](https://github.com/AgoraIO)
- 如果你想了解声网 SDK 在复杂场景下的应用，可以参考 [官方场景案例](https://github.com/AgoraIO-usecase)
- 如果你想了解声网的一些社区开发者维护的项目，可以查看 [社区](https://github.com/AgoraIO-Community)
- 若遇到问题需要开发者帮助，你可以到 [开发者社区](https://rtcdeveloper.com/) 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单

## 已知问题

请不要使用最新node版本18， 不要使用react-native最新版本0.69，以及react最新版本18。他们和之前的版本有很大的兼容性问题。

## 代码许可

示例项目遵守 MIT 许可证。
/* eslint-disable @typescript-eslint/no-unused-vars */
/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import * as React from 'react';
import {
  Platform,
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
  ChatPushConfig,
} from 'react-native-agora-chat';
import messaging from '@react-native-firebase/messaging';

async function requestUserPermission() {
  const authStatus = await messaging().requestPermission({
    announcement: true,
  });
  const enabled =
    authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
    authStatus === messaging.AuthorizationStatus.PROVISIONAL;

  if (enabled) {
    console.log('Authorization status:', authStatus);
  }
}

async function checkApplicationPermission() {
  const authorizationStatus = await messaging().requestPermission();

  if (authorizationStatus === messaging.AuthorizationStatus.AUTHORIZED) {
    console.log('User has notification permissions enabled.');
  } else if (
    authorizationStatus === messaging.AuthorizationStatus.PROVISIONAL
  ) {
    console.log('User has provisional notification permissions.');
  } else {
    console.log('User has notification permissions disabled');
  }
}

async function requestFcmToken() {
  // https://rnfirebase.io/reference/messaging#getToken
  // await messaging().registerDeviceForRemoteMessages();
  const fcmToken = await messaging().getToken();
  console.log('fcm token: ', fcmToken);
  return fcmToken;
}

function registerMessageHandler() {
  const ret = messaging().onMessage(async remoteMessage => {
    console.log(
      'A new FCM message arrived!',
      Platform.OS,
      JSON.stringify(remoteMessage),
    );
  });
  messaging().setBackgroundMessageHandler(async remoteMessage => {
    console.log(
      'Message handled in the background!',
      Platform.OS,
      remoteMessage,
    );
  });
  return ret;
}

registerMessageHandler();

// The App Object.
const App = (): React.JSX.Element => {
  console.log('test:App:');
  let _appKey: string = '';
  let _currentId: string = '';
  let _currentPs: string = '';
  let _agoraAppId: string = '';
  let _targetId: string = '';
  let _senderId: string = '';
  let _requestGetTokenUrl: string = '';
  let _requestRegistryAccountUrl: string = '';

  try {
    const env = require('./env');
    _appKey = env.appKey;
    _currentId = env.id;
    _currentPs = env.ps;
    _agoraAppId = env.agoraAppId;
    _targetId = env.targetId;
    _senderId = env.senderId;
    _requestGetTokenUrl = env.requestGetTokenUrl;
    _requestRegistryAccountUrl = env.requestRegistryAccountUrl;
  } catch (error) {
    console.error(error);
  }

  // variable defines.
  const title = 'AgoraChatQuickstart';
  // const senderId = '';
  let fcmToken = React.useRef('');
  const requestGetTokenUrl = _requestGetTokenUrl;
  const requestRegistryAccountUrl = _requestRegistryAccountUrl;
  const [appKey, setAppKey] = React.useState(_appKey);
  const [senderId, setSenderId] = React.useState(_senderId);
  const [username, setUsername] = React.useState(_currentId);
  const [password, setPassword] = React.useState(_currentPs);
  const [userId, setUserId] = React.useState(_targetId);
  const [content, setContent] = React.useState('');
  const [logText, setWarnText] = React.useState('Show log area');
  let isInitialized = React.useRef(false);
  const numberOfLines = 10;

  // output console log.
  React.useEffect(() => {
    logText.split('\n').forEach((value, index, array) => {
      if (index === 0) {
        console.log(value);
      }
    });
  }, [logText]);

  // listen received message.
  React.useEffect(() => {
    // return registerMessageHandler();
  }, [logText]);

  // output ui log.
  const rollLog = (text: string) => {
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

  const requestHttp = (url: string) => {
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

  // register listener for message.
  const setMessageListener = () => {
    let msgListener = {
      onMessagesReceived(messages: ChatMessage[]) {
        for (let index = 0; index < messages.length; index++) {
          rollLog('received msgId: ' + messages[index].msgId);
        }
      },
    };

    ChatClient.getInstance().chatManager.removeAllMessageListener();
    ChatClient.getInstance().chatManager.addMessageListener(msgListener);
  };

  // Init sdk.
  // Please initialize any interface before calling it.
  const init = async () => {
    console.log('init:');
    await requestUserPermission();
    await checkApplicationPermission();
    fcmToken.current = await requestFcmToken();
    rollLog('fcm token: ' + fcmToken.current);

    const pushConfig = new ChatPushConfig({
      deviceId: senderId,
      deviceToken: fcmToken.current,
    });
    let o = new ChatOptions({
      autoLogin: false,
      appKey: appKey,
      pushConfig: pushConfig,
    });
    console.log('push config', JSON.stringify(pushConfig));
    rollLog('push config: ' + JSON.stringify(pushConfig));
    ChatClient.getInstance().removeAllConnectionListener();
    ChatClient.getInstance()
      .init(o)
      .then(() => {
        rollLog('init success');
        isInitialized.current = true;
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
          onDisconnected(errorCode: any) {
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

  // register account for login
  const registerAccount = () => {
    if (
      isInitialized.current === false ||
      isInitialized.current === undefined
    ) {
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

  // login with account id and password
  const loginWithPassword = () => {
    if (
      isInitialized.current === false ||
      isInitialized.current === undefined
    ) {
      rollLog('Perform initialization first.');
      return;
    }
    rollLog('start login ...');
    ChatClient.getInstance()
      .login(username, password)
      .then(() => {
        rollLog('login operation success.');
      })
      .catch(reason => {
        rollLog('login fail: ' + JSON.stringify(reason));
      });
  };

  // login with account id and token
  const loginWithToken = () => {
    if (
      isInitialized.current === false ||
      isInitialized.current === undefined
    ) {
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
              `response token success: username = ${username}, token = ${JSON.stringify(
                value,
              )}`,
            );
            const token = value.accessToken;
            rollLog('start login ...');
            ChatClient.getInstance()
              .loginWithAgoraToken(username, token)
              .then(() => {
                rollLog('login success.');
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

  // logout from server.
  const logout = () => {
    if (
      isInitialized.current === false ||
      isInitialized.current === undefined
    ) {
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

  // send text message to somebody
  const sendmsg = () => {
    if (
      isInitialized.current === false ||
      isInitialized.current === undefined
    ) {
      rollLog('Perform initialization first.');
      return;
    }
    let msg = ChatMessage.createTextMessage(
      userId,
      content,
      ChatMessageChatType.PeerChat,
    );
    const callback = new (class {
      onProgress(locaMsgId: string, progress: any) {
        rollLog(`send message process: ${locaMsgId}, ${progress}`);
      }
      onError(locaMsgId: string, error: any) {
        rollLog(`send message fail: ${locaMsgId}, ${JSON.stringify(error)}`);
      }
      onSuccess(message: ChatMessage) {
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

  const updatePush = () => {
    console.log('test:updatePush:', senderId, fcmToken.current);
    ChatClient.getInstance()
      .updatePushConfig(
        new ChatPushConfig({deviceId: senderId, deviceToken: fcmToken.current}),
      )
      .then(() => {
        rollLog('updatePush: success');
        console.log('updatePush: success');
      })
      .catch(reason => {
        rollLog(`updatePush fail: ${JSON.stringify(reason)}`);
        console.log('updatePush fail', JSON.stringify(reason));
      });
  };

  // ui render.
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
        <View style={styles.inputCon}>
          <TextInput
            multiline
            style={styles.inputBox}
            placeholder="Enter FCM sendId"
            onChangeText={text => setSenderId(text)}
            value={senderId}
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
        <View style={styles.buttonCon}>
          <Text style={styles.btn2} onPress={updatePush}>
            PUSH BIND
          </Text>
        </View>
        <View>
          <Text style={styles.logText} numberOfLines={numberOfLines}>
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

// ui styles sets.
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

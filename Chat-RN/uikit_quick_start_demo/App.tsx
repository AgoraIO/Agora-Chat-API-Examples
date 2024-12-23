/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import * as React from 'react';
import {Pressable, SafeAreaView, Text, View} from 'react-native';
import {
  ChatFragment,
  TextInput,
  useChatSdkContext,
} from 'react-native-agora-chat-uikit';

import {GlobalContainer} from 'react-native-agora-chat-uikit';

const appKey = 'xxx';
const userId = 'xxx';
const userPs = 'xxx';
const peerId = 'xxx';

function SendMessage() {
  const [page, setPage] = React.useState(0);
  const [appkey, setAppkey] = React.useState(appKey);
  const [id, setId] = React.useState(userId);
  const [ps, setPs] = React.useState(userPs);
  const [peer, setPeer] = React.useState(peerId);
  const im = useChatSdkContext();

  if (page === 0) {
    return (
      // 登录页面
      <SafeAreaView style={{flex: 1}}>
        <TextInput
          placeholder="Please App Key."
          value={appkey}
          onChangeText={setAppkey}
        />
        <TextInput
          placeholder="Please Login ID."
          value={id}
          onChangeText={setId}
        />
        <TextInput
          placeholder="Please Login token or password."
          value={ps}
          onChangeText={setPs}
        />
        <TextInput
          placeholder="Please peer ID."
          value={peer}
          onChangeText={setPeer}
        />
        <Pressable
          onPress={() => {
            console.log('test:zuoyu:login', id, ps);
            im.login({
              id: id,
              pass: ps,
              type: 'agora',
              onResult: res => {
                console.log('login result', res);
                console.log('test:zuoyu:error', res);
                if (res.result === true) {
                  setPage(1);
                } else {
                  console.warn('login failed');
                }
              },
            });
          }}>
          <Text>{'Login'}</Text>
        </Pressable>
        <Pressable
          onPress={() => {
            im.logout({
              onResult: result => {
                console.log('logout result', result);
              },
            });
          }}>
          <Text>{'Logout'}</Text>
        </Pressable>
      </SafeAreaView>
    );
  } else if (page === 1) {
    // 聊天页面
    return (
      <SafeAreaView style={{flex: 1}}>
        <ChatFragment screenParams={{params: {chatId: peer, chatType: 0}}} />
      </SafeAreaView>
    );
  } else {
    return <View />;
  }
}

function App(): React.JSX.Element {
  // return <View><Text>{'test'}</Text></View>;
  return (
    <GlobalContainer
      option={{appKey: appKey, autoLogin: false, debugModel: true}}>
      <SendMessage />
    </GlobalContainer>
  );
}

export default App;

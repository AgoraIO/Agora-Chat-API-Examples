/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import * as React from 'react';
import {Pressable, SafeAreaView, Text, View} from 'react-native';
import {
  ChatConversationType,
  ConversationDetail,
  TextInput,
  useChatContext,
} from 'react-native-agora-chat-uikit';

import {Container} from 'react-native-agora-chat-uikit';

const appKey = 'easemob#easeim';
const userId = 'zuoyu';
const userPs =
  'YWMt1MnJtLxsEe-BRI0OrKx9iVzzvlQ7sUrSpVuQGlyIzFR0-qKAZz4R7pnDi6mwsod0AwMAAAGT1HK4iTeeSAB26nGOzMM-YzVzt027k45U2uofkCB_oU4AvREXvykrqA';
const peerId = 'zuoyu2';

function SendMessage() {
  const [page, setPage] = React.useState(0);
  const [appkey, setAppkey] = React.useState(appKey);
  const [id, setId] = React.useState(userId);
  const [ps, setPs] = React.useState(userPs);
  const [peer, setPeer] = React.useState(peerId);
  const im = useChatContext();

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
              userId: id,
              userToken: ps,
              result: res => {
                console.log('login result', res);
                console.log('test:zuoyu:error', res);
                if (res.isOk === true) {
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
              result: result => {
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
        <ConversationDetail
          type={'chat'}
          convId={peerId}
          convType={ChatConversationType.PeerChat}
          NavigationBar={
            <View
              style={{height: 40, width: 40, backgroundColor: 'green'}}
              onTouchEnd={() => {
                im.logout({
                  result: result => {
                    console.log('logout result', result);
                    setPage(0);
                  },
                });
              }}
            />
          }
        />
      </SafeAreaView>
    );
  } else {
    return <View />;
  }
}

function App(): React.JSX.Element {
  // return <View><Text>{'test'}</Text></View>;
  return (
    <Container options={{appKey: appKey, autoLogin: false, debugModel: true}}>
      <SendMessage />
    </Container>
  );
}

export default App;

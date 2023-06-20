/* eslint-disable react-native/no-inline-styles */
/* eslint-disable react/no-unstable-nested-components */
/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import React from 'react';

import { accountType, appKey, dlog } from './src/config';
import { MainScreen } from './src/Main';
import { MessageScreen } from './src/Message';
import { AppServerClient } from './src/AppServerClient';
import { ChatClient, ChatOptions } from 'react-native-chat-sdk';
import {
  ActivityIndicator,
  Platform,
  Text,
  TouchableOpacity,
} from 'react-native';
import { LogMemo } from './src/Log';

const Root = createNativeStackNavigator();

const App = () => {
  // dlog.log("App:");
  const [ready, setReady] = React.useState(false);
  const enableLog = true;
  const logHeightRef = React.useRef<number | string>(1);
  const [logHeight, setLogHeight] = React.useState(logHeightRef.current);

  const logRef = React.useRef({
    logHandler: (message?: any, ...optionalParams: any[]) => {
      console.log(message, ...optionalParams);
    },
  });
  if (Platform.OS !== 'ios') {
    dlog.handler = (message?: any, ...optionalParams: any[]) => {
      logRef.current?.logHandler?.(message, ...optionalParams);
    };
  }

  if (accountType !== 'easemob') {
    AppServerClient.rtcTokenUrl = 'https://a41.easemob.com/token/rtc/channel';
    AppServerClient.mapUrl = 'https://a41.easemob.com/agora/channel/mapper';
  }

  if (ready === false) {
    const init = () => {
      ChatClient.getInstance()
        .init(
          new ChatOptions({
            appKey: appKey,
            autoLogin: false,
            debugModel: enableLog,
          })
        )
        .then(() => {
          setReady(true);
        })
        .catch((e) => {
          console.warn('init:error:', e);
        });
    };
    init();
  }

  if (ready === false) {
    return <ActivityIndicator />;
  }

  const HeaderRight = () => {
    return (
      <TouchableOpacity
        onPress={() => {
          logHeightRef.current = logHeightRef.current === 1 ? '90%' : 1;
          setLogHeight(logHeightRef.current);
        }}
      >
        <Text style={{ fontWeight: '600', color: 'blue' }}>DevLog</Text>
      </TouchableOpacity>
    );
  };

  return (
    <>
      <NavigationContainer>
        <Root.Navigator initialRouteName="Main">
          <Root.Screen
            options={() => {
              return {
                headerRight: HeaderRight,
              };
            }}
            name="Main"
            component={MainScreen}
          />
          <Root.Screen
            options={() => {
              return {
                headerShown: true,
                presentation: Platform.select({
                  ios: undefined,
                  default: 'fullScreenModal',
                }),
                headerRight: HeaderRight,
                headerBackVisible: true,
              };
            }}
            name="Message"
            component={MessageScreen}
          />
        </Root.Navigator>
      </NavigationContainer>
      <LogMemo
        containerStyle={{
          position: 'absolute',
          width: '100%',
          height: logHeight,
          bottom: 0,
        }}
        propsRef={logRef}
        maxLineNumber={100}
      />
    </>
  );
};

export default App;

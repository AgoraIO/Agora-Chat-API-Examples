import * as React from 'react';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import {
  RootParamsList,
  accountType,
  defaultId,
  defaultPs,
  defaultTargetId,
  dlog,
} from './config';
import { TextInput } from 'react-native';
import { ChatClient } from 'react-native-chat-sdk';
import { AppServerClient } from './AppServerClient';

export function MainScreen({
  navigation,
}: NativeStackScreenProps<typeof RootParamsList>): JSX.Element {
  dlog.log('MainScreen:', defaultId, defaultPs);
  const placeholder1 = 'Please User Id';
  const placeholder2 = 'Please User Password or Token';
  const placeholder3 = 'Please Chat Target ID';
  const placeholder4 = 'Please Chat Target Type: 0 or 1';
  const [id, setId] = React.useState(defaultId);
  const [token, setToken] = React.useState(defaultPs);
  const [logged, setLogged] = React.useState(false);
  const [chatId, setChatId] = React.useState(defaultTargetId);
  const [chatType, setChatType] = React.useState('0');
  const type = accountType;

  const login = () => {
    dlog.log('MainScreen:login:', id, token, type, id.split('0'));
    if (type !== 'easemob') {
      AppServerClient.getAccountToken({
        userId: id,
        userPassword: token,
        onResult: (params: { data?: any; error?: any }) => {
          if (params.error === undefined) {
            ChatClient.getInstance()
              .loginWithAgoraToken(id, params.data.token)
              .then(() => {
                dlog.log('loginWithAgoraToken:success:');
                setLogged(true);
              })
              .catch((e) => {
                dlog.log('loginWithAgoraToken:error:', e);
              });
          } else {
            dlog.log('loginWithAgoraToken:error:', params.error);
          }
        },
      });
    } else {
      ChatClient.getInstance()
        .login(id, token)
        .then(() => {
          dlog.log('login:success:');
          setLogged(true);
        })
        .catch((e) => {
          dlog.log('login:error:', e);
          if (e.code === 200) {
            setLogged(true);
          }
        });
    }
  };
  const registry = () => {
    AppServerClient.registerAccount({
      userId: id,
      userPassword: token,
      onResult: (params: { data?: any; error?: any }) => {
        dlog.log('registerAccount:', id, token, params);
      },
    });
  };
  const logout = () => {
    ChatClient.getInstance()
      .logout()
      .then(() => {
        dlog.log('logout:success:');
        setLogged(false);
      })
      .catch((e) => {
        dlog.log('logout:error:', e);
      });
  };
  const gotoMessage = React.useCallback(
    async (params: { chatId: string; chatType: number }) => {
      // eslint-disable-next-line @typescript-eslint/no-shadow
      const { chatId, chatType } = params;
      if (logged !== true) {
        dlog.log('gotoMessage:', 'Please log in first.');
        return;
      }
      if (chatId === undefined || chatId.trim().length === 0) {
        dlog.log('gotoMessage:', 'Please input chatId first.');
        return;
      }
      navigation.push('Message', { chatId, chatType, currentId: id });
    },
    [id, logged, navigation]
  );

  const addListener = React.useCallback(() => {
    return () => {};
  }, []);
  React.useEffect(() => {
    const ret = addListener();
    return () => ret();
  }, [addListener]);
  return (
    <SafeAreaView style={styles.container} edges={['right', 'left', 'bottom']}>
      <View style={styles.container}>
        <View style={styles.inputContainer}>
          <TextInput
            style={styles.input}
            placeholder={placeholder1}
            value={id}
            onChangeText={(t) => {
              setId(t);
            }}
          />
          <TextInput
            style={styles.input}
            placeholder={placeholder2}
            value={token}
            onChangeText={(t) => {
              setToken(t);
            }}
          />
        </View>
        <View style={styles.buttonContainer}>
          <Pressable style={styles.button} onPress={login}>
            <Text style={styles.buttonText}>SIGN IN</Text>
          </Pressable>
          <Pressable style={styles.button} onPress={registry}>
            <Text style={styles.buttonText}>SIGN UP</Text>
          </Pressable>
          <Pressable style={styles.button} onPress={logout}>
            <Text style={styles.buttonText}>SIGN OUT</Text>
          </Pressable>
        </View>
        <View style={styles.inputContainer}>
          <TextInput
            style={styles.input}
            placeholder={placeholder3}
            value={chatId}
            onChangeText={setChatId}
          />
        </View>
        <View style={styles.inputContainer}>
          <TextInput
            style={styles.input}
            placeholder={placeholder4}
            value={chatType}
            onChangeText={setChatType}
          />
        </View>
        <View style={styles.buttonContainer}>
          <Pressable
            style={styles.button}
            onPress={() => {
              gotoMessage({ chatId: chatId, chatType: parseInt(chatType, 10) });
            }}
          >
            <Text style={styles.buttonText}>Send Message</Text>
          </Pressable>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  log: {
    position: 'absolute',
    width: '100%',
  },
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    flexWrap: 'wrap',
  },
  button: {
    height: 40,
    marginHorizontal: 10,
    backgroundColor: 'blue',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
  },
  buttonText: {
    color: 'white',
    fontWeight: '500',
  },
  inputContainer: {
    marginHorizontal: 20,
    // backgroundColor: 'red',
  },
  input: {
    height: 40,
    borderBottomColor: '#0041FF',
    borderBottomWidth: 1,
    backgroundColor: 'white',
    marginVertical: 10,
  },
});

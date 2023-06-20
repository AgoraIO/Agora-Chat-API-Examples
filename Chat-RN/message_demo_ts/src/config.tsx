import { ChatLog } from 'react-native-chat-sdk';

export const RootParamsList: Record<string, object | undefined> = {
  Main: {},
  Message: {},
};
export let appKey = '1135220126133718#demo';
export let agoraAppId = '';
export let defaultId = 'asterisk001';
export let defaultPs = 'qwerty';
export let accountType: 'agora' | 'easemob' | undefined;
export const autoLogin = false;
export const debugModel = true;
export let defaultTargetId = 'du005';

try {
  appKey = require('../env').appKey;
  defaultId = require('../env').id;
  defaultPs = require('../env').ps;
  agoraAppId = require('../env').agoraAppId;
  accountType = require('../env').accountType;
  defaultTargetId = require('../env').targetId as string;
} catch (error) {
  console.error(error);
}

export const dlog = new ChatLog();
dlog.tag = 'demo';

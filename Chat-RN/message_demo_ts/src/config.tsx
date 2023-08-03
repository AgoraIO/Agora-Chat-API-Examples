import { ChatLog } from 'react-native-agora-chat';

export const RootParamsList: Record<string, object | undefined> = {
  Main: {},
  Message: {},
};
export let appKey = '';
export let agoraAppId = '';
export let defaultId = '';
export let defaultPs = '';
export let accountType: 'agora' | 'easemob' | undefined;
export const autoLogin = false;
export const debugModel = true;
export let defaultTargetId = '';

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

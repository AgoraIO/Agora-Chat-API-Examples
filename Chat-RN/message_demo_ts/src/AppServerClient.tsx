import { dlog } from './config';
import { ChatClient } from 'react-native-chat-sdk';

export class AppServerClient {
  private static _rtcTokenUrl: string =
    'https://a1.easemob.com/token/rtcToken/v1';
  private static _mapUrl: string = 'https://a1.easemob.com/channel/mapper';
  private static _regUrl: string =
    'https://a41.easemob.com/app/chat/user/register';
  private static _tokenUrl: string =
    'https://a41.easemob.com/app/chat/user/login';

  protected _(): void {}
  private static async req(params: {
    method: 'GET' | 'POST';
    url: string;
    kvs: any;
    from: 'requestToken' | 'requestUserMap';
    onResult: (p: { data?: any; error?: any }) => void;
  }): Promise<void> {
    dlog.log('AppServerClient:req:', params);
    try {
      const accessToken = await ChatClient.getInstance().getAccessToken();
      dlog.log('AppServerClient:req:', accessToken);
      const json = params.kvs as {
        userAccount: string;
        channelName: string;
        appkey: string;
        userChannelId?: number;
      };
      const url = `${params.url}?appkey=${encodeURIComponent(
        json.appkey
      )}&channelName=${encodeURIComponent(
        json.channelName
      )}&userAccount=${encodeURIComponent(json.userAccount)}`;
      dlog.log('AppServerClient:req:', url);
      const response = await fetch(url, {
        method: params.method,
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const value = await response.json();
      dlog.log('AppServerClient:req:', value, value.code);
      if (value.code === 'RES_0K' || value.code === 'RES_OK') {
        if (params.from === 'requestToken') {
          params.onResult({
            data: {
              token: value.accessToken,
              uid: value.agoraUserId ?? json.userChannelId,
            },
          });
        } else if (params.from === 'requestUserMap') {
          params.onResult({
            data: {
              result: value.result,
            },
          });
        }
      } else {
        params.onResult({ error: { code: value.code } });
      }
    } catch (error) {
      params.onResult({ error });
    }
  }
  public static getRtcToken(params: {
    userAccount: string;
    channelId: string;
    appKey: string;
    userChannelId?: number | undefined;
    type?: 'easemob' | 'agora' | undefined;
    onResult: (params: { data?: any; error?: any }) => void;
  }): void {
    const tokenUrl = (url: string) => {
      dlog.log('test:tokenUrl', params.type, url);
      let ret = url;
      if (params.type !== 'easemob') {
        ret += `/${params.channelId}/agorauid/${params.userChannelId!}`;
      }
      return ret;
    };

    AppServerClient.req({
      method: 'GET',
      url: tokenUrl(AppServerClient._rtcTokenUrl),
      kvs: {
        userAccount: params.userAccount,
        channelName: params.channelId,
        appkey: params.appKey,
        userChannelId: params.userChannelId,
      },
      from: 'requestToken',
      onResult: params.onResult,
    });
  }
  public static getRtcMap(params: {
    userAccount: string;
    channelId: string;
    appKey: string;
    onResult: (params: { data?: any; error?: any }) => void;
  }): void {
    AppServerClient.req({
      method: 'GET',
      url: AppServerClient._mapUrl,
      kvs: {
        userAccount: params.userAccount,
        channelName: params.channelId,
        appkey: params.appKey,
      },
      from: 'requestUserMap',
      onResult: params.onResult,
    });
  }

  private static async req2(params: {
    userId: string;
    userPassword: string;
    from: 'registerAccount' | 'getAccountToken';
    onResult: (params: { data?: any; error?: any }) => void;
  }): Promise<void> {
    try {
      let url = '';
      if (params.from === 'getAccountToken') {
        url = AppServerClient._tokenUrl;
      } else if (params.from === 'registerAccount') {
        url = AppServerClient._regUrl;
      }
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userAccount: params.userId,
          userPassword: params.userPassword,
        }),
      });
      const value = await response.json();
      dlog.log('test:value:', url, value, value.code);
      if (value.code === 'RES_0K' || value.code === 'RES_OK') {
        if (params.from === 'getAccountToken') {
          params.onResult({ data: { token: value.accessToken } });
        } else if (params.from === 'registerAccount') {
          params.onResult({ data: {} });
        }
      } else {
        params.onResult({ error: { code: value.code } });
      }
    } catch (error) {
      params.onResult({ error });
    }
  }

  public static registerAccount(params: {
    userId: string;
    userPassword: string;
    onResult: (params: { data?: any; error?: any }) => void;
  }): void {
    this.req2({ ...params, from: 'registerAccount' });
  }

  public static getAccountToken(params: {
    userId: string;
    userPassword: string;
    onResult: (params: { data?: any; error?: any }) => void;
  }): void {
    this.req2({ ...params, from: 'getAccountToken' });
  }

  public static set rtcTokenUrl(url: string) {
    AppServerClient._rtcTokenUrl = url;
  }
  public static set mapUrl(url: string) {
    AppServerClient._mapUrl = url;
  }
  public static set regUrl(url: string) {
    AppServerClient._regUrl = url;
  }
  public static set tokenUrl(url: string) {
    AppServerClient._tokenUrl = url;
  }
}

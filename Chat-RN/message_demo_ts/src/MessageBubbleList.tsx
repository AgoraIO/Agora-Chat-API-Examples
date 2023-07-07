/* eslint-disable @typescript-eslint/no-shadow */
/* eslint-disable react-native/no-inline-styles */
import {
  ActivityIndicator,
  FlatList,
  ListRenderItem,
  ListRenderItemInfo,
  Pressable,
  Image as RNImage,
  StyleSheet,
  Text,
  View,
  useWindowDimensions,
} from 'react-native';
import * as React from 'react';
import { ChatDownloadStatus, ChatMessageType } from 'react-native-chat-sdk';
import Ionicons from '@expo/vector-icons/Ionicons';
import { AntDesign, Feather, MaterialIcons } from '@expo/vector-icons';
import { FileHandler } from './FileHandler';
import Lottie from 'lottie-react-native';
import { Video, ResizeMode } from 'expo-av';
import { dlog } from './config';
import FastImage from 'react-native-fast-image';

export type MessageItemStateType =
  | 'unread'
  | 'read'
  | 'arrived'
  | 'played'
  | 'sending'
  | 'failed'
  | 'receiving'
  | 'recalled';

export interface MessageItemType {
  sender: string;
  timestamp: number;
  isSender?: boolean;
  key: string;
  msgId: string;
  type: ChatMessageType;
  state?: MessageItemStateType;
  onPress?: (data: MessageItemType) => void;
  onLongPress?: (data: MessageItemType) => void;
  ext?: any;
  isTip?: boolean;
}

export interface TextMessageItemType extends MessageItemType {
  text: string;
}

export interface ImageMessageItemType extends FileMessageItemType {
  localThumbPath?: string;
  remoteThumbPath?: string;
  width?: number;
  height?: number;
}
export interface VoiceMessageItemType extends FileMessageItemType {
  duration: number;
}
export interface CustomMessageItemType extends MessageItemType {
  // SubComponent: (props: React.PropsWithChildren<any>) => React.ReactElement;
  SubComponent: React.FunctionComponent<
    MessageItemType & { eventType: string; data: any }
  >;
  SubComponentProps: MessageItemType & { eventType: string; data: any };
}
export interface VideoMessageItemType extends FileMessageItemType {
  duration: number;
  thumbnailLocalPath?: string;
  thumbnailRemoteUrl?: string;
  width?: number;
  height?: number;
}
export interface LocationMessageItemType extends MessageItemType {
  address: string;
  latitude: string;
  longitude: string;
}
export interface FileMessageItemType extends MessageItemType {
  localPath: string;
  remoteUrl: string;
  fileSize?: number;
  displayName?: string;
  fileStatus: ChatDownloadStatus;
}

export const StateLabel = React.memo(
  ({ state }: { state?: MessageItemStateType }) => {
    if (state === 'sending') {
      return <ActivityIndicator size={12} />;
    } else if (state === 'failed') {
      return <AntDesign name="exclamationcircleo" size={12} color="red" />;
    } else {
      return <Ionicons name="checkmark-done" size={12} color="green" />;
    }
  }
);

function localUrlEscape(localPath: string): string {
  if (localPath.startsWith('file://')) {
    return localPath.replace(/#/g, '%23').replace(/ /g, '%20');
  } else {
    return localPath;
  }
}

export function updateUrl(url: string) {
  let r = url;
  if (r.startsWith('http://') === false && r.startsWith('https://') === false) {
    if (r.length > 0) {
      r = r.includes('file://') ? r : `file://${r}`;
      if (r.includes('file://')) {
        r = localUrlEscape(r);
      }
    }
  }
  return r;
}

export async function getImageExistedPath(
  msg: ImageMessageItemType
): Promise<string | undefined> {
  let isExisted = false;
  let ret: string | undefined;
  do {
    if (msg.localThumbPath && msg.localThumbPath.length > 0) {
      isExisted = await new FileHandler().isExisted({
        fileUri: updateUrl(msg.localThumbPath),
      });
      if (isExisted === true) {
        ret = updateUrl(msg.localThumbPath);
        break;
      }
    }
    if (msg.localPath && msg.localPath.length > 0) {
      isExisted = await new FileHandler().isExisted({
        fileUri: updateUrl(msg.localPath),
      });
      if (isExisted === true) {
        ret = updateUrl(msg.localPath);
        break;
      }
    }
  } while (false);

  return ret;
}

const RenderRecallMessage = (props: MessageItemType): JSX.Element => {
  const { state, ext, ...others } = props;
  if (state === ('' as any)) {
    dlog.log(others);
  }
  if (state === 'recalled') {
    const tip = ext.recall.tip;
    return (
      <View style={{ flex: 1, justifyContent: 'center' }}>
        <Text>{tip}</Text>
      </View>
    );
  }
  return <View />;
};

const TextMessageRenderItemDefault: ListRenderItem<MessageItemType> =
  React.memo(
    (info: ListRenderItemInfo<MessageItemType>): React.ReactElement | null => {
      const { width: screenWidth } = useWindowDimensions();
      const { item } = info;
      const msg = item as TextMessageItemType;
      if (item.state === 'recalled') {
        return <RenderRecallMessage {...item} />;
      }
      return (
        <View
          style={[
            styles.container,
            {
              flexDirection: msg.isSender ? 'row-reverse' : 'row',
              maxWidth: screenWidth * 0.9,
            },
          ]}
        >
          <View
            style={[
              {
                marginRight: msg.isSender ? undefined : 10,
                marginLeft: msg.isSender ? 10 : undefined,
              },
            ]}
          >
            <Ionicons name="person-circle" size={24} color="black" />
          </View>
          <View
            style={[
              styles.innerContainer,
              {
                borderBottomRightRadius: msg.isSender ? undefined : 12,
                borderBottomLeftRadius: msg.isSender ? 12 : undefined,
                maxWidth: screenWidth * 0.7,
              },
            ]}
          >
            <Text
              style={[
                styles.text,
                {
                  backgroundColor: msg.isSender ? '#0041FF' : '#F2F2F2',
                  color: msg.isSender ? 'white' : '#333333',
                },
              ]}
            >
              {msg.text}
            </Text>
          </View>
          <View
            style={[
              {
                marginRight: msg.isSender ? 10 : undefined,
                marginLeft: msg.isSender ? undefined : 10,
                opacity: 1,
              },
            ]}
          >
            <StateLabel state={msg.state} />
          </View>
        </View>
      );
    }
  );

export const FileMessageRenderItemDefault: ListRenderItem<MessageItemType> =
  React.memo(
    (info: ListRenderItemInfo<MessageItemType>): React.ReactElement | null => {
      const { width: screenWidth } = useWindowDimensions();
      const { item } = info;
      const msg = item as FileMessageItemType;
      if (item.state === 'recalled') {
        return <RenderRecallMessage {...item} />;
      }

      return (
        <View
          style={[
            styles.container,
            {
              flexDirection: msg.isSender ? 'row-reverse' : 'row',
              maxWidth: screenWidth * 0.9,
            },
          ]}
        >
          <View
            style={[
              {
                marginRight: msg.isSender ? undefined : 10,
                marginLeft: msg.isSender ? 10 : undefined,
              },
            ]}
          >
            <Ionicons name="person-circle" size={24} color="black" />
          </View>
          <View
            style={[
              styles.innerContainer,
              {
                borderBottomRightRadius: msg.isSender ? undefined : 12,
                borderBottomLeftRadius: msg.isSender ? 12 : undefined,
                maxWidth: screenWidth * 0.7,
              },
            ]}
          >
            <View style={styles.file}>
              {msg.isSender ? (
                <View>
                  <Feather name="file" size={36} color="black" />
                </View>
              ) : null}
              <View>
                <Text
                  numberOfLines={1}
                  style={[
                    {
                      fontSize: 15,
                      fontWeight: '600',
                      lineHeight: 22,
                      color: '#333333',
                      maxWidth: screenWidth * 0.6,
                    },
                  ]}
                >
                  {msg.displayName}
                </Text>
                <Text
                  numberOfLines={1}
                  style={[
                    {
                      fontSize: 12,
                      fontWeight: '400',
                      lineHeight: 20,
                      color: '#666666',
                      maxWidth: screenWidth * 0.6,
                    },
                  ]}
                >
                  {msg.fileSize}
                </Text>
              </View>

              {msg.isSender ? null : (
                <View>
                  <Feather name="file" size={36} color="black" />
                </View>
              )}
            </View>
          </View>
          <View
            style={[
              {
                marginRight: msg.isSender ? 10 : undefined,
                marginLeft: msg.isSender ? undefined : 10,
                opacity: 1,
              },
            ]}
          >
            <StateLabel state={msg.state} />
          </View>
        </View>
      );
    }
  );

const ImageMessageRenderItemDefault: ListRenderItem<MessageItemType> =
  React.memo(
    (info: ListRenderItemInfo<MessageItemType>): React.ReactElement | null => {
      const { item } = info;
      const msg = item as ImageMessageItemType;
      const { width: wWidth } = useWindowDimensions();
      const [width, setWidth] = React.useState(wWidth * 0.6);
      const [height, setHeight] = React.useState((wWidth * 0.6 * 4) / 3);
      const isFastImage = true;

      const url = (msg: ImageMessageItemType) => {
        let r: string;
        if (msg.localThumbPath && msg.localThumbPath.length > 0) {
          r = msg.localThumbPath;
        } else if (msg.remoteThumbPath && msg.remoteThumbPath.length > 0) {
          r = msg.remoteThumbPath;
        } else if (msg.localPath && msg.localPath.length > 0) {
          r = msg.localPath;
        } else {
          r = msg.remoteUrl ?? '';
        }
        return updateUrl(r);
      };
      const urlAsync = async (msg: ImageMessageItemType) => {
        return getImageExistedPath(msg);
      };

      const [_url, setUrl] = React.useState(url(msg));

      const checked = async (msg: ImageMessageItemType, count: number = 0) => {
        const ret = await urlAsync(msg);
        if (ret) {
          setUrl(updateUrl(ret));
        } else {
          if (count > 3) {
            return;
          }
          setTimeout(() => checked(msg, ++count), 1000);
        }
      };

      const hw = (params: {
        height: number;
        width: number;
      }): { width: number; height: number } => {
        const { height, width } = params;
        let ret = params;
        if (width / height >= 10) {
          const w = wWidth * 0.6;
          ret = {
            width: w,
            height: w * 0.1,
          };
        } else if (width * 4 >= 3 * height) {
          const w = wWidth * 0.6;
          ret = {
            width: w,
            height: w * (height / width),
          };
        } else if (width * 10 >= 1 * height) {
          const h = (wWidth * 0.6 * 4) / 3;
          ret = {
            width: (width / height) * h,
            height: h,
          };
        } else {
          // width / height < 1 / 10
          const h = (wWidth * 0.6 * 4) / 3;
          ret = {
            width: 0.1 * h,
            height: h,
          };
        }
        return ret;
      };

      if (item.state === 'recalled') {
        return <RenderRecallMessage {...item} />;
      }

      return (
        <View
          style={[
            styles.container,
            {
              flexDirection: msg.isSender ? 'row-reverse' : 'row',
              // maxWidth: '80%',
            },
          ]}
        >
          <View
            style={[
              {
                marginRight: msg.isSender ? undefined : 10,
                marginLeft: msg.isSender ? 10 : undefined,
              },
            ]}
          >
            <Ionicons name="person-circle" size={24} color="black" />
          </View>
          <View>
            {isFastImage ? (
              <FastImage
                style={{ height: height, width: width, borderRadius: 10 }}
                source={
                  _url.length === 0
                    ? 0
                    : {
                        uri: _url,
                      }
                }
                resizeMode={FastImage.resizeMode.cover}
                onLoad={(e) => {
                  dlog.log('test:onLoad:', e.nativeEvent);
                  const ret = hw(e.nativeEvent);
                  setHeight(ret.height);
                  setWidth(ret.width);
                }}
                onError={() => {
                  dlog.log('test:onError:');
                  setUrl('');
                  checked(msg);
                }}
              />
            ) : (
              <RNImage
                source={
                  _url.length === 0
                    ? 0
                    : {
                        uri: _url,
                      }
                }
                resizeMode="contain"
                resizeMethod="scale"
                style={{ height: height, width: width, borderRadius: 10 }}
                onLoad={(e) => {
                  dlog.log('test:onLoad:', e.nativeEvent);
                  const ret = hw(e.nativeEvent.source);
                  setHeight(ret.height);
                  setWidth(ret.width);
                }}
                onError={() => {
                  dlog.log('test:onError:');
                  setUrl('');
                  // setUrl(
                  //   "https://dogefs.s3.ladydaily.com/~/source/unsplash/photo-1622697872837-f353c9be5ab8?ixlib=rb-4.0.3&q=85&fmt=jpg&crop=entropy&cs=srgb&dl=chen-jian-fm6_ysxat6Y-unsplash.jpg&w=640"
                  // );
                  // setUrl(
                  //   "file:///storage/emulated/0/Android/data/com.example.rn.message.demo/easemob%23easeim/files/du004/du005/thumb_3eff6e50-f3de-11ed-8882-ef83bd3db406"
                  // );
                  checked(msg);
                }}
              />
            )}
          </View>
          <View
            style={[
              {
                marginRight: msg.isSender ? 10 : undefined,
                marginLeft: msg.isSender ? undefined : 10,
                opacity: 1,
              },
            ]}
          >
            <StateLabel state={msg.state} />
          </View>
        </View>
      );
    }
  );

const Voice = ({ isPlaying }: { isPlaying: boolean }) => {
  if (isPlaying === true) {
    return (
      <Lottie
        source={{
          uri: 'https://assets3.lottiefiles.com/packages/lf20_p8wyx27k.json',
        }}
        autoPlay
        loop
        style={{ height: 30, width: 30 }}
      />
    );
  } else {
    return <MaterialIcons name="multitrack-audio" size={30} color="black" />;
  }
};

const VoiceMessageRenderItemDefault: ListRenderItem<MessageItemType> =
  React.memo(
    (info: ListRenderItemInfo<MessageItemType>): React.ReactElement | null => {
      const { item } = info;
      const { width } = useWindowDimensions();
      const msg = item as VoiceMessageItemType;
      const isPlayingRef = React.useRef(false);
      const [isPlaying, setIsPlaying] = React.useState(isPlayingRef.current);
      const _width = (duration: number) => {
        if (duration < 0) {
          throw new Error('The voice length cannot be less than 0.');
        }
        let r = width * 0.7 * (1 / 60) * (duration > 60 ? 60 : duration);
        r += 150;
        return r;
      };

      if (item.state === 'recalled') {
        return <RenderRecallMessage {...item} />;
      }

      return (
        <View
          style={[
            styles.container,
            {
              flexDirection: msg.isSender ? 'row-reverse' : 'row',
              width: _width(msg.duration ?? 1),
            },
          ]}
        >
          <View
            style={[
              {
                marginRight: msg.isSender ? undefined : 10,
                marginLeft: msg.isSender ? 10 : undefined,
              },
            ]}
          >
            <Ionicons name="person-circle" size={24} color="black" />
          </View>
          <View
            style={[
              styles.innerContainer,
              {
                flexDirection: msg.isSender ? 'row-reverse' : 'row',
                justifyContent: 'space-between',
                borderBottomRightRadius: msg.isSender ? undefined : 12,
                borderBottomLeftRadius: msg.isSender ? 12 : undefined,
                backgroundColor: msg.isSender ? '#0041FF' : '#F2F2F2',
                flexGrow: 1,
                paddingHorizontal: 12,
                alignItems: 'center',
              },
            ]}
          >
            <Pressable
              onPress={() => {
                setIsPlaying(!isPlaying);
              }}
            >
              <Voice isPlaying={isPlaying} />
            </Pressable>

            <Text
              style={[
                styles.text,
                {
                  color: msg.isSender ? 'white' : 'black',
                  backgroundColor: msg.isSender ? '#0041FF' : '#F2F2F2',
                },
              ]}
            >
              {Math.round(msg.duration).toString() + "'"}
            </Text>
          </View>
          <View
            style={[
              {
                marginRight: msg.isSender ? 10 : undefined,
                marginLeft: msg.isSender ? undefined : 10,
                opacity: 1,
              },
            ]}
          >
            <StateLabel state={msg.state} />
          </View>
        </View>
      );
    }
  );
const VideoMessageRenderItemDefault: ListRenderItem<MessageItemType> =
  React.memo(
    (info: ListRenderItemInfo<MessageItemType>): React.ReactElement | null => {
      const { item } = info;
      const msg = item as VideoMessageItemType;
      const { width: wWidth } = useWindowDimensions();
      const [width, setWidth] = React.useState(wWidth * 0.6);
      const [height, setHeight] = React.useState((wWidth * 0.6 * 4) / 3);
      const video = React.useRef<Video>({} as any);
      const [status, setStatus] = React.useState({} as any);
      const [url, setUrl] = React.useState(msg.remoteUrl ?? msg.localPath);

      const onCheck = async () => {
        const ret = await new FileHandler().isExisted({
          fileUri: updateUrl(msg.localPath),
        });
        if (ret) {
          setUrl(updateUrl(msg.localPath));
        }
      };

      const hw = (params: {
        height: number;
        width: number;
      }): { width: number; height: number } => {
        const { height, width } = params;
        let ret = params;
        if (width / height >= 10) {
          const w = wWidth * 0.6;
          ret = {
            width: w,
            height: w * 0.1,
          };
        } else if (width * 4 >= 3 * height) {
          const w = wWidth * 0.6;
          ret = {
            width: w,
            height: w * (height / width),
          };
        } else if (width * 10 >= 1 * height) {
          const h = (wWidth * 0.6 * 4) / 3;
          ret = {
            width: (width / height) * h,
            height: h,
          };
        } else {
          // width / height < 1 / 10
          const h = (wWidth * 0.6 * 4) / 3;
          ret = {
            width: 0.1 * h,
            height: h,
          };
        }
        return ret;
      };

      if (item.state === 'recalled') {
        return <RenderRecallMessage {...item} />;
      }

      return (
        <View
          style={[
            styles.container,
            {
              flexDirection: msg.isSender ? 'row-reverse' : 'row',
              // maxWidth: '80%',
            },
          ]}
        >
          <View
            style={[
              {
                marginRight: msg.isSender ? undefined : 10,
                marginLeft: msg.isSender ? 10 : undefined,
              },
            ]}
          >
            <Ionicons name="person-circle" size={24} color="black" />
          </View>
          <Pressable
            onPress={() => {
              if (status?.isPlaying) {
                status.isPlaying
                  ? video.current?.pauseAsync()
                  : video.current?.playAsync();
              }
            }}
          >
            <Video
              ref={video}
              style={{ height: height, width: width, borderRadius: 10 }}
              source={{
                // uri: "https://d23dyxeqlo5psv.cloudfront.net/big_buck_bunny.mp4",
                uri: url,
              }}
              useNativeControls
              resizeMode={ResizeMode.CONTAIN}
              isLooping
              onPlaybackStatusUpdate={(s) =>
                setStatus(() => {
                  dlog.log('video:onPlaybackStatusUpdate:', s);
                  return s;
                })
              }
              onReadyForDisplay={(e) => {
                dlog.log('video:onReadyForDisplay:', e);
                const ret = hw(e.naturalSize);
                setHeight(ret.height);
                setWidth(ret.width);
              }}
              onError={(e) => {
                dlog.log('video:onError:', e);
                onCheck();
              }}
              onLoad={(s) => {
                dlog.log('video:status:', s);
              }}
            />
          </Pressable>
          <View
            style={[
              {
                marginRight: msg.isSender ? 10 : undefined,
                marginLeft: msg.isSender ? undefined : 10,
                opacity: 1,
              },
            ]}
          >
            <StateLabel state={msg.state} />
          </View>
        </View>
      );
    }
  );

const MessageRenderItem: ListRenderItem<MessageItemType> = (
  info: ListRenderItemInfo<MessageItemType>
): React.ReactElement | null => {
  const { item } = info;
  let MessageItem: ListRenderItem<MessageItemType> | undefined;
  if (item.type === ChatMessageType.TXT) {
    MessageItem = TextMessageRenderItemDefault;
  } else if (item.type === ChatMessageType.IMAGE) {
    MessageItem = ImageMessageRenderItemDefault;
  } else if (item.type === ChatMessageType.VOICE) {
    MessageItem = VoiceMessageRenderItemDefault;
  } else if (item.type === ChatMessageType.CUSTOM) {
  } else if (item.type === ChatMessageType.VIDEO) {
    MessageItem = VideoMessageRenderItemDefault;
  } else if (item.type === ChatMessageType.LOCATION) {
  } else if (item.type === ChatMessageType.FILE) {
    MessageItem = FileMessageRenderItemDefault;
  }
  if (MessageItem === null || MessageItem === undefined) {
    return null;
  }
  return (
    <Pressable
      onPress={() => {
        item.onPress?.(item);
      }}
      onLongPress={() => {
        // TODO: download file
        item.onLongPress?.(item);
      }}
      style={{
        width: '100%',
        alignItems:
          item.isTip === true
            ? 'center'
            : item.isSender === true
            ? 'flex-end'
            : 'flex-start',
      }}
    >
      <MessageItem {...info} />
    </Pressable>
  );
};

export type InsertDirectionType = 'before' | 'after';
export type MessageBubbleListRef = {
  addMessage: (params: {
    msgs: MessageItemType[];
    direction: InsertDirectionType;
  }) => void;
  updateMessageState: (params: {
    localMsgId: string;
    result: boolean;
    reason?: any;
    item?: MessageItemType;
  }) => void;
};
export type MessageBubbleListProps = {
  propRef: React.RefObject<MessageBubbleListRef>;
};
export function MessageBubbleList(props: MessageBubbleListProps): JSX.Element {
  dlog.log('MessageBubbleList:', props);
  const { propRef } = props;
  const data1 = React.useMemo(() => [] as MessageItemType[], []);
  const data2 = React.useMemo(() => [] as MessageItemType[], []);
  const currentData = React.useRef(data1);
  const [items, setItems] = React.useState<MessageItemType[]>(data1);

  const updateDataInternal = React.useCallback(
    (data: MessageItemType[]) => {
      if (data === data1) {
        for (let index = 0; index < data1.length; index++) {
          const element = data1[index] as MessageItemType;
          data2[index] = element;
        }
        data2.splice(data1.length, data2.length);
        setItems(data2);
        currentData.current = data2;
      } else if (data === data2) {
        for (let index = 0; index < data2.length; index++) {
          const element = data2[index] as MessageItemType;
          data1[index] = element;
        }
        data1.splice(data2.length, data1.length);
        setItems(data1);
        currentData.current = data1;
      } else {
        throw new Error('This is impossible.');
      }
    },
    [data1, data2]
  );

  const updateData = React.useCallback(
    ({
      type,
      list,
      direction,
    }: {
      type: 'add' | 'update-all' | 'update-part' | 'del-one';
      list: MessageItemType[];
      direction: InsertDirectionType;
    }) => {
      switch (type) {
        case 'add':
          if (direction === 'after') {
            items.push(...list);
          } else {
            const tmp = list.concat(items);
            items.length = 0;
            items.push(...tmp);
          }
          break;
        case 'update-all':
          for (let index = 0; index < items.length; index++) {
            const item = items[index];
            if (item) {
              for (const i of list) {
                if (item.key === i.key) {
                  items[index] = i;
                }
              }
            }
          }
          break;
        case 'update-part':
          for (let index = 0; index < items.length; index++) {
            const item = items[index];
            for (const i of list) {
              if (item?.key === i.key) {
                const old = item;
                items[index] = (old ? { ...old, ...i } : i) as MessageItemType;
                break;
              }
            }
          }
          break;
        case 'del-one':
          {
            let hadDeleted = false;
            for (let index = 0; index < items.length; index++) {
              const item = items[index];
              if (item) {
                for (const i of list) {
                  if (i.key === undefined) {
                    if (item.msgId === i.msgId) {
                      items.splice(index, 1);
                      hadDeleted = true;
                      break;
                    }
                  } else {
                    if (item.key === i.key) {
                      items.splice(index, 1);
                      hadDeleted = true;
                      break;
                    }
                  }
                }
              }
              if (hadDeleted === true) {
                break;
              }
            }
          }
          break;
        default:
          return;
      }
      updateDataInternal(items);
    },
    [items, updateDataInternal]
  );

  if (propRef.current) {
    propRef.current.addMessage = (params: {
      msgs: MessageItemType[];
      direction: InsertDirectionType;
    }) => {
      updateData({
        type: 'add',
        list: params.msgs,
        direction: params.direction,
      });
    };
    propRef.current.updateMessageState = (params: {
      localMsgId: string;
      result: boolean;
      reason?: any;
      item?: MessageItemType;
    }) => {
      if (params.result === true && params.item) {
        updateData({
          type: 'update-all',
          list: [params.item],
          direction: 'after',
        });
      } else {
        updateData({
          type: 'update-part',
          list: [
            {
              key: params.localMsgId,
              state: 'failed',
            } as MessageItemType,
          ],
          direction: 'after',
        });
      }
    };
  }

  return (
    <FlatList
      style={{ backgroundColor: 'white' }}
      data={items}
      extraData={items}
      renderItem={MessageRenderItem}
      keyExtractor={(item: MessageItemType, _: number) => {
        return item.key;
      }}
    />
  );
}

const styles = StyleSheet.create({
  container: {
    justifyContent: 'flex-start',
    // backgroundColor: 'yellow',
    alignItems: 'flex-end',
    flexDirection: 'row',
    padding: 10,
  },
  innerContainer: {
    // flex: 1,
    borderTopLeftRadius: 12,
    borderTopRightRadius: 12,
    // backgroundColor: 'red',
    overflow: 'hidden',
  },
  text: {
    backgroundColor: 'rgba(242, 242, 242, 1)',
    padding: 10,
    flexWrap: 'wrap',
  },
  file: {
    flexDirection: 'row',
    backgroundColor: 'rgba(242, 242, 242, 1)',
    padding: 10,
  },
});

const MessageBubbleListMemo = React.memo(MessageBubbleList);
export default MessageBubbleListMemo;

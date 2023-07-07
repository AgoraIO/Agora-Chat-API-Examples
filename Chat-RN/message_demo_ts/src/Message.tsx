/* eslint-disable react-native/no-inline-styles */
/* eslint-disable @typescript-eslint/no-shadow */
/* eslint-disable react/no-unstable-nested-components */
import {
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  useWindowDimensions,
  View,
} from 'react-native';
import * as React from 'react';
import { Picker } from '@react-native-picker/picker';
import {
  ChatClient,
  ChatCustomMessageBody,
  ChatError,
  ChatFileMessageBody,
  ChatImageMessageBody,
  ChatLocationMessageBody,
  ChatMessage,
  ChatMessageDirection,
  ChatMessageEventListener,
  ChatMessageStatus,
  ChatMessageStatusCallback,
  ChatMessageType,
  ChatTextMessageBody,
  ChatVideoMessageBody,
  ChatVoiceMessageBody,
} from 'react-native-chat-sdk';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { dlog, RootParamsList } from './config';
import { FileHandler } from './FileHandler';
import { ImageHandler } from './ImageHandler';
import { VideoHandler } from './VideoHandler';
import {
  CustomMessageItemType,
  FileMessageItemType,
  ImageMessageItemType,
  InsertDirectionType,
  LocationMessageItemType,
  MessageBubbleList,
  MessageBubbleListRef,
  MessageItemStateType,
  MessageItemType,
  TextMessageItemType,
  updateUrl,
  VideoMessageItemType,
  VoiceMessageItemType,
} from './MessageBubbleList';
import { VoiceHandler } from './VoiceHandler';
import * as Audio from 'expo-av';

type MessageScreenProps = NativeStackScreenProps<typeof RootParamsList>;

export function MessageScreen({ route }: MessageScreenProps): JSX.Element {
  dlog.log('MessageScreen:', route);
  const params = route.params as any;
  const currentId = params?.currentId ?? '';
  const chatId = params?.chatId ?? '';
  const chatType = params?.chatType ?? 0;
  const [selectedType, setSelectedType] = React.useState(
    ChatMessageType.TXT.toString()
  );
  const msgRef = React.useRef<MessageBubbleListRef>({} as any);
  const contentRef = React.useRef({} as any);
  const seqId = React.useRef(0);
  const [json, setJson] = React.useState('This is file info.');
  const voiceRef = React.useRef<VoiceHandler | undefined>();
  const [visible, setVisible] = React.useState<boolean>(false);
  const msgBubbleDataRef = React.useRef<MessageItemType | undefined>();
  const [msgBubbleData, setMsgBubbleData] = React.useState(
    msgBubbleDataRef.current
  );
  const { height: windowHeight } = useWindowDimensions();
  const [minimize, setMinimize] = React.useState(false);
  const minimizeHeight = 60;
  const [buttonName, setButtonName] = React.useState<'Mini' | 'Comm'>('Mini');

  const onPress = React.useCallback((data: MessageItemType) => {
    switch (data.type) {
      case ChatMessageType.VOICE:
        const voice = data as VoiceMessageItemType;
        voiceRef.current = new VoiceHandler({
          type: 'playback',
          file: {
            uri: updateUrl(voice.localPath),
          } as Audio.AVPlaybackSourceObject,
          onFinished: async () => {
            await voiceRef.current?.startPlayAudio();
          },
        });
        break;

      default:
        break;
    }
  }, []);

  const onLongPress = React.useCallback(
    (data: MessageItemType) => {
      msgBubbleDataRef.current = data;
      setMsgBubbleData(msgBubbleDataRef.current);
      setVisible(visible === true ? false : true);
    },
    [visible]
  );

  const downloadAttachment = (item: MessageItemType) => {
    if (
      item.type === ChatMessageType.FILE ||
      item.type === ChatMessageType.VIDEO ||
      item.type === ChatMessageType.IMAGE
    ) {
      ChatClient.getInstance()
        .chatManager.getMessage(item.msgId)
        .then((msg) => {
          if (msg) {
            ChatClient.getInstance()
              .chatManager.downloadAttachment(msg, {
                onProgress: (localMsgId: string, progress: number) => {
                  dlog.log('onProgress:', localMsgId, progress);
                },
                onError: (localMsgId: string, error: ChatError) => {
                  dlog.log('onError:', localMsgId, error);
                },
                onSuccess: (message: ChatMessage) => {
                  // TODO: update status
                  dlog.log('onSuccess:', message.localMsgId);
                  msgRef.current?.updateMessageState({
                    localMsgId: message.localMsgId,
                    result: true,
                    item: convertFromMessage(message),
                  });
                },
              } as ChatMessageStatusCallback)
              .then()
              .catch();
          }
        })
        .catch();
    }
  };

  const genBubbleData = () => {
    let ret = {} as
      | {
          msgs: MessageItemType[];
          direction: InsertDirectionType;
        }
      | undefined;
    const t = selectedType as ChatMessageType;
    ret = {
      msgs: [
        {
          sender: currentId,
          timestamp: Date.now(),
          isSender: true,
          key: seqId.current.toString(),
          msgId: seqId.current.toString(),
          state: 'sending',
          onPress: onPress,
          onLongPress: onLongPress,
          type: ChatMessageType.TXT,
        } as MessageItemType,
      ],
      direction: 'after',
    };
    ++seqId.current;
    switch (t) {
      case ChatMessageType.CMD:
        ret = undefined;
        break;
      case ChatMessageType.CUSTOM:
        ret = undefined;
        break;
      case ChatMessageType.FILE:
        {
          let msg = ret.msgs[0] as FileMessageItemType;
          msg.type = ChatMessageType.FILE;
          const file = contentRef.current.file as {
            name: string;
            size?: number | undefined;
            uri: string;
            mimeType?: string | undefined;
            lastModified?: number | undefined;
            file?: any;
            output?: any;
          };
          msg.displayName = file.name;
          msg.localPath = file.uri ?? '';
        }
        break;
      case ChatMessageType.IMAGE:
        {
          let msg = ret.msgs[0] as ImageMessageItemType;
          msg.type = ChatMessageType.IMAGE;
          const image = contentRef.current.image as {
            uri: string;
            width: number;
            height: number;
            exif?: Record<string, any>;
            base64?: string;
            duration?: number;
          };
          msg.localPath = image.uri;
          msg.width = image.width;
          msg.height = image.height;
        }

        break;
      case ChatMessageType.LOCATION:
        ret = undefined;
        break;
      case ChatMessageType.TXT:
        {
          let msg = ret.msgs[0] as TextMessageItemType;
          msg.type = ChatMessageType.TXT;
          msg.text = contentRef.current?.text;
        }
        break;
      case ChatMessageType.VIDEO:
        {
          let msg = ret.msgs[0] as VideoMessageItemType;
          msg.type = ChatMessageType.VIDEO;
          const video = contentRef.current.video as {
            uri: string;
            width: number;
            height: number;
            exif?: Record<string, any>;
            base64?: string;
            duration?: number;
          };
          const thumb = contentRef.current.thumb as {
            uri: string;
            width: number;
            height: number;
          };
          msg.localPath = video.uri;
          msg.thumbnailLocalPath = thumb.uri;
        }
        break;
      case ChatMessageType.VOICE:
        {
          let msg = ret.msgs[0] as VoiceMessageItemType;
          msg.type = ChatMessageType.VOICE;
          const voice = contentRef.current.voice as {
            uri: string;
            duration: number;
          };
          msg.localPath = voice.uri;
          msg.duration = voice.duration;
        }
        break;

      default:
        ret = undefined;
        break;
    }
    return ret;
  };
  const genMsgData = () => {
    let ret: ChatMessage | undefined;
    const t = selectedType as ChatMessageType;
    switch (t) {
      case ChatMessageType.CMD:
        ret = undefined;
        break;
      case ChatMessageType.CUSTOM:
        ret = undefined;
        break;
      case ChatMessageType.FILE:
        {
          const file = contentRef.current.file as {
            name: string;
            size?: number | undefined;
            uri: string;
            mimeType?: string | undefined;
            lastModified?: number | undefined;
            file?: any;
            output?: any;
          };
          const filePath = file.uri ?? '';
          const displayName = file.name;
          ret = ChatMessage.createFileMessage(chatId, filePath, chatType, {
            displayName,
          });
        }
        break;
      case ChatMessageType.IMAGE:
        {
          const image = contentRef.current.image as {
            uri: string;
            width: number;
            height: number;
            exif?: Record<string, any>;
            base64?: string;
            duration?: number;
          };
          const localPath = image.uri;
          const width = image.width;
          const height = image.height;
          ret = ChatMessage.createImageMessage(chatId, localPath, chatType, {
            displayName: '',
            width,
            height,
          });
        }

        break;
      case ChatMessageType.LOCATION:
        ret = undefined;
        break;
      case ChatMessageType.TXT:
        {
          const text = contentRef.current?.text;
          ret = ChatMessage.createTextMessage(chatId, text, chatType);
        }
        break;
      case ChatMessageType.VIDEO:
        {
          const video = contentRef.current.video as {
            uri: string;
            width: number;
            height: number;
            exif?: Record<string, any>;
            base64?: string;
            duration?: number;
          };
          const thumb = contentRef.current.thumb as {
            uri: string;
            width: number;
            height: number;
          };
          const localPath = video.uri;
          const thumbnailLocalPath = thumb.uri;
          const duration = video.duration;
          const width = thumb.width;
          const height = thumb.height;
          // todo: ios: advise change the file:// protocol supported by ios.
          const localPathIos = localPath.replace('file://', '');
          const thumbnailLocalPathIos = thumbnailLocalPath.replace(
            'file://',
            ''
          );
          ret = ChatMessage.createVideoMessage(
            chatId,
            Platform.select({ ios: localPathIos, default: localPath }),
            chatType,
            {
              displayName: '',
              width,
              height,
              duration: duration ?? 0,
              thumbnailLocalPath: Platform.select({
                ios: thumbnailLocalPathIos,
                default: thumbnailLocalPath,
              }),
            }
          );
        }
        break;
      case ChatMessageType.VOICE:
        {
          const voice = contentRef.current.voice as {
            uri: string;
            duration: number;
          };
          const localPath = voice.uri;
          const duration = voice.duration;
          ret = ChatMessage.createVoiceMessage(chatId, localPath, chatType, {
            displayName: '',
            duration,
          });
        }
        break;

      default:
        ret = undefined;
        break;
    }
    return ret;
  };

  const convertFromMessage = React.useCallback(
    (msg: ChatMessage): MessageItemType => {
      const convertFromMessageState = (msg: ChatMessage) => {
        let ret: MessageItemStateType;
        if (msg.status === ChatMessageStatus.SUCCESS) {
          ret = 'arrived' as MessageItemStateType;
        } else if (msg.status === ChatMessageStatus.CREATE) {
          ret = 'sending' as MessageItemStateType;
        } else if (msg.status === ChatMessageStatus.FAIL) {
          ret = 'failed' as MessageItemStateType;
        } else if (msg.status === ChatMessageStatus.PROGRESS) {
          if (msg.direction === ChatMessageDirection.RECEIVE) {
            ret = 'receiving' as MessageItemStateType;
          } else {
            ret = 'sending' as MessageItemStateType;
          }
        } else {
          ret = 'failed' as MessageItemStateType;
        }
        if (ret === 'sending' || ret === 'receiving') {
          if (Date.now() > msg.localTime + 1000 * 60) {
            ret = 'failed';
          }
        }
        return ret;
      };
      const convertFromMessageBody = (
        msg: ChatMessage,
        item: MessageItemType
      ) => {
        const type = msg.body.type;
        switch (type) {
          case ChatMessageType.VOICE:
            {
              const body = msg.body as ChatVoiceMessageBody;
              const r = item as VoiceMessageItemType;
              r.localPath = body.localPath;
              r.remoteUrl = body.remotePath;
              r.fileSize = body.fileSize;
              r.fileStatus = body.fileStatus;
              r.duration = body.duration;
              r.displayName = body.displayName;
              r.type = ChatMessageType.VOICE;
            }
            break;
          case ChatMessageType.IMAGE:
            {
              const body = msg.body as ChatImageMessageBody;
              const r = item as ImageMessageItemType;
              r.localPath = body.localPath;
              r.remoteUrl = body.remotePath;
              r.fileSize = body.fileSize;
              r.fileStatus = body.fileStatus;
              r.displayName = body.displayName;
              r.localThumbPath = body.thumbnailLocalPath;
              r.remoteThumbPath = body.thumbnailRemotePath;
              r.type = ChatMessageType.IMAGE;
            }
            break;
          case ChatMessageType.TXT:
            {
              const body = msg.body as ChatTextMessageBody;
              const r = item as TextMessageItemType;
              r.text = body.content;
              r.type = ChatMessageType.TXT;
            }
            break;
          case ChatMessageType.CUSTOM:
            {
              const body = msg.body as ChatCustomMessageBody;
              const r = item as CustomMessageItemType;
              r.SubComponentProps = {
                eventType: body.event,
                data: body.params,
                ...item,
              } as MessageItemType & { eventType: string; data: any };
              r.type = ChatMessageType.CUSTOM;
            }
            break;
          case ChatMessageType.LOCATION:
            {
              const body = msg.body as ChatLocationMessageBody;
              const r = item as LocationMessageItemType;
              r.address = body.address;
              r.latitude = body.latitude;
              r.longitude = body.longitude;
              r.type = ChatMessageType.LOCATION;
            }
            break;
          case ChatMessageType.FILE:
            {
              const body = msg.body as ChatFileMessageBody;
              const r = item as FileMessageItemType;
              r.localPath = body.localPath;
              r.remoteUrl = body.remotePath;
              r.fileSize = body.fileSize;
              r.fileStatus = body.fileStatus;
              r.displayName = body.displayName;
              r.type = ChatMessageType.FILE;
            }
            break;
          case ChatMessageType.VIDEO:
            {
              const body = msg.body as ChatVideoMessageBody;
              const r = item as VideoMessageItemType;
              r.localPath = body.localPath;
              r.remoteUrl = body.remotePath;
              r.fileSize = body.fileSize;
              r.fileStatus = body.fileStatus;
              r.duration = body.duration;
              r.thumbnailLocalPath = body.thumbnailLocalPath;
              r.thumbnailRemoteUrl = body.thumbnailRemotePath;
              r.width = body.width;
              r.height = body.height;
              r.displayName = body.displayName;
              r.type = ChatMessageType.VIDEO;
            }
            break;
          default:
            throw new Error('This is impossible.');
        }
      };
      const r = {
        sender: msg.from,
        timestamp: msg.serverTime,
        isSender: msg.direction === ChatMessageDirection.RECEIVE ? false : true,
        key: msg.localMsgId,
        msgId: msg.msgId,
        state: convertFromMessageState(msg),
        ext: msg.attributes,
        onPress: onPress,
        onLongPress: onLongPress,
      } as MessageItemType;
      convertFromMessageBody(msg, r);
      return r;
    },
    [onLongPress, onPress]
  );

  const onSend = () => {
    const msg = genMsgData();
    if (msg) {
      const bubble = genBubbleData();
      if (bubble) {
        const b = bubble.msgs[0] as MessageItemType;
        b.key = msg.localMsgId;
        b.msgId = msg.msgId;
        msgRef.current?.addMessage(bubble);
      }
      ChatClient.getInstance()
        .chatManager.sendMessage(msg, {
          onProgress: (localMsgId: string, progress: number) => {
            dlog.log('onProgress:', localMsgId, progress);
          },
          onError: (localMsgId: string, error: ChatError) => {
            dlog.log('onError:', localMsgId, error);
            msgRef.current?.updateMessageState({
              localMsgId,
              result: false,
              reason: error,
            });
          },
          onSuccess: (message: ChatMessage) => {
            dlog.log('onSuccess:', message.localMsgId);
            msgRef.current?.updateMessageState({
              localMsgId: message.localMsgId,
              result: true,
              item: convertFromMessage(message),
            });
          },
        } as ChatMessageStatusCallback)
        .then()
        .catch((e) => {
          dlog.log('sendMessage:error:', e);
        });
    }
  };

  const openFile = async () => {
    const ret = await new FileHandler().getFile();
    dlog.log('openFile:', ret);
    if (ret.cancelled !== true) {
      contentRef.current = { file: ret };
      setJson(JSON.stringify(contentRef.current));
    }
  };
  const openImage = async () => {
    const ret = await new ImageHandler().getImage();
    dlog.log('openImage:', ret);
    if (ret.cancelled !== true) {
      contentRef.current = { image: ret };
      setJson(JSON.stringify(contentRef.current));
    }
  };
  const openCamera = async () => {
    const image = new ImageHandler();
    const per = await image.getPermission();
    if (per !== true) {
      const _per = await image.requestPermission();
      if (_per !== true) {
        return;
      }
    }
    const ret = await image.getCamera();
    dlog.log('openCamera:', ret);
    if (ret.cancelled !== true) {
      contentRef.current = { image: ret };
      setJson(JSON.stringify(contentRef.current));
    }
  };
  const openVideo = async () => {
    const video = new VideoHandler();
    const ret = await video.getVideo();
    dlog.log('openVideo:', ret);
    if (ret.cancelled !== true) {
      const _ret = await video.getThumbnail({ fileName: ret.uri });
      dlog.log('openVideo:', _ret);
      contentRef.current = { video: ret, thumb: _ret };
      setJson(JSON.stringify(contentRef.current));
    }
  };
  const stopRecord = async () => {
    if (voiceRef.current) {
      const ret = await voiceRef.current.stopRecording();
      dlog.log('stopRecord:', ret);
      contentRef.current = { voice: ret };
      setJson(JSON.stringify(contentRef.current));
      voiceRef.current = undefined;
    }
  };

  React.useEffect(() => {
    const genBubbleDataFromServer = (message: ChatMessage) => {
      let ret = {} as
        | {
            msgs: MessageItemType[];
            direction: InsertDirectionType;
          }
        | undefined;
      const t = message.body.type;
      ret = {
        msgs: [
          {
            sender: message.from,
            timestamp: message.serverTime,
            isSender: message.from === currentId ? true : false,
            key: seqId.current.toString(),
            msgId: message.msgId,
            state: 'arrived',
            onPress: onPress,
            onLongPress: onLongPress,
          } as MessageItemType,
        ],
        direction: 'after',
      };
      ++seqId.current;
      switch (t) {
        case ChatMessageType.CMD:
          ret = undefined;
          break;
        case ChatMessageType.CUSTOM:
          ret = undefined;
          break;
        case ChatMessageType.FILE:
          {
            let msg = ret.msgs[0] as FileMessageItemType;
            const body = message.body as ChatFileMessageBody;
            msg.type = ChatMessageType.FILE;
            msg.displayName = body.displayName;
            msg.localPath = body.localPath;
            msg.remoteUrl = body.remotePath;
          }
          break;
        case ChatMessageType.IMAGE:
          {
            let msg = ret.msgs[0] as ImageMessageItemType;
            const body = message.body as ChatImageMessageBody;
            msg.type = ChatMessageType.IMAGE;
            msg.localPath = body.localPath;
            msg.remoteUrl = body.remotePath;
            msg.remoteThumbPath = body.thumbnailRemotePath;
            msg.localThumbPath = body.thumbnailLocalPath;
            msg.width = body.width;
            msg.height = body.height;
          }

          break;
        case ChatMessageType.LOCATION:
          ret = undefined;
          break;
        case ChatMessageType.TXT:
          {
            let msg = ret.msgs[0] as TextMessageItemType;
            const body = message.body as ChatTextMessageBody;
            msg.type = ChatMessageType.TXT;
            msg.text = body.content;
          }
          break;
        case ChatMessageType.VIDEO:
          {
            let msg = ret.msgs[0] as VideoMessageItemType;
            const body = message.body as ChatVideoMessageBody;
            msg.type = ChatMessageType.VIDEO;
            msg.localPath = body.localPath;
            msg.remoteUrl = body.remotePath;
            msg.thumbnailLocalPath = body.thumbnailLocalPath;
            msg.thumbnailRemoteUrl = body.thumbnailRemotePath;
          }
          break;
        case ChatMessageType.VOICE:
          {
            let msg = ret.msgs[0] as VoiceMessageItemType;
            const body = message.body as ChatVoiceMessageBody;
            msg.type = ChatMessageType.VOICE;
            msg.localPath = body.localPath;
            msg.remoteUrl = body.remotePath;
            msg.duration = body.duration;
          }
          break;

        default:
          ret = undefined;
          break;
      }
      return ret;
    };
    const listener = {
      onMessagesReceived: (messages: Array<ChatMessage>) => {
        for (const message of messages) {
          if (
            message.body.type === ChatMessageType.TXT ||
            message.body.type === ChatMessageType.FILE ||
            message.body.type === ChatMessageType.IMAGE ||
            message.body.type === ChatMessageType.VIDEO ||
            message.body.type === ChatMessageType.VOICE
          ) {
            if (message.conversationId === chatId) {
              const ret = genBubbleDataFromServer(message);
              if (ret) {
                msgRef.current?.addMessage(ret);
              }
            }
          }
        }
      },
    } as ChatMessageEventListener;
    ChatClient.getInstance().chatManager.addMessageListener(listener);
    return () => {
      ChatClient.getInstance().chatManager.removeAllMessageListener();
    };
  }, [chatId, currentId, onLongPress, onPress]);

  // There are problems with react-navigation native stack and keyboard being used together.
  const keyboardVerticalOffset = Platform.select({
    ios: 64,
    default: 0,
  });

  const RenderContextMenu = ({
    visible,
    data,
    onClose,
  }: {
    data: MessageItemType;
    visible: boolean;
    onClose: ({
      data,
      type,
    }: {
      data: MessageItemType;
      type: 'da' | 'cancel';
    }) => void;
  }) => {
    return (
      <Modal visible={visible} animationType={'fade'} transparent={true}>
        <View
          style={{
            backgroundColor: '#F2F2F2',
            width: 200,
            alignSelf: 'center',
            padding: 20,
            borderRadius: 5,
            top: windowHeight / 2,
          }}
        >
          <TouchableOpacity
            style={{ height: 30 }}
            onPress={() => {
              onClose({ data, type: 'da' });
            }}
          >
            <Text style={{ color: 'blue' }}>download attachment</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={{ height: 30 }}
            onPress={() => {
              onClose({ data, type: 'cancel' });
            }}
          >
            <Text style={{ color: 'blue' }}>cancel</Text>
          </TouchableOpacity>
        </View>
      </Modal>
    );
  };

  const RenderVoiceButton = React.memo(
    ({ onStart, onEnd }: { onStart?: () => void; onEnd?: () => void }) => {
      const startRecordContent = 'Start Record' as
        | 'Start Record'
        | 'Stop Record';
      const stopRecordContent = 'Stop Record' as 'Start Record' | 'Stop Record';
      const [voiceButtonContent, setVoiceButtonContent] =
        React.useState(startRecordContent);
      return (
        <Pressable
          style={styles.button2}
          onPressIn={() => {
            setVoiceButtonContent(stopRecordContent);
            onStart?.();
          }}
          onPressOut={() => {
            setVoiceButtonContent(startRecordContent);
            onEnd?.();
          }}
        >
          <Text style={styles.buttonText}>{voiceButtonContent}</Text>
        </Pressable>
      );
    }
  );
  const RenderBody = ({ type }: { type: ChatMessageType }) => {
    let ret = <View />;
    switch (type) {
      case ChatMessageType.CMD:
        break;
      case ChatMessageType.FILE:
        ret = (
          <View style={styles.file}>
            <Text style={{ flex: 1 }} numberOfLines={20}>
              {json}
            </Text>
            <Pressable
              style={styles.button2}
              onPress={() => {
                openFile();
              }}
            >
              <Text style={styles.buttonText}>Select File</Text>
            </Pressable>
          </View>
        );
        break;
      case ChatMessageType.CUSTOM:
        break;
      case ChatMessageType.IMAGE:
        ret = (
          <View style={[styles.image, { flexDirection: 'column' }]}>
            <View style={{ flexDirection: 'row' }}>
              <Pressable
                style={[styles.button2, { marginRight: 20 }]}
                onPress={() => {
                  openImage();
                }}
              >
                <Text style={styles.buttonText}>Select Image</Text>
              </Pressable>
              <Pressable
                style={styles.button2}
                onPress={() => {
                  openCamera();
                }}
              >
                <Text style={styles.buttonText}>Open Camera</Text>
              </Pressable>
            </View>
            <View style={{ flexDirection: 'row' }}>
              <Text style={{ flexWrap: 'wrap' }} numberOfLines={20}>
                {json}
              </Text>
            </View>
          </View>
        );
        break;
      case ChatMessageType.LOCATION:
        break;
      case ChatMessageType.TXT:
        ret = (
          <View style={styles.txt}>
            <TextInput
              style={{ height: 30 }}
              placeholder="Please input text content..."
              onChangeText={(t) => {
                contentRef.current = { text: t };
              }}
            />
          </View>
        );
        break;
      case ChatMessageType.VIDEO:
        ret = (
          <View style={styles.file}>
            <Text style={{ flex: 1 }} numberOfLines={20}>
              {json}
            </Text>
            <Pressable
              style={styles.button2}
              onPress={() => {
                openVideo();
              }}
            >
              <Text style={styles.buttonText}>Select Video</Text>
            </Pressable>
          </View>
        );
        break;
      case ChatMessageType.VOICE:
        ret = (
          <View style={styles.file}>
            <Text style={{ flex: 1 }} numberOfLines={20}>
              {json}
            </Text>
            <RenderVoiceButton
              onStart={() => {
                voiceRef.current = new VoiceHandler({ type: 'record' });
                voiceRef.current.startRecording();
              }}
              onEnd={() => {
                stopRecord();
              }}
            />
          </View>
        );
        break;

      default:
        break;
    }
    return ret;
  };
  return (
    <View
      style={{
        flex: 1,
        // backgroundColor: "green",
      }}
    >
      <View
        style={{
          flex: 1,
          // backgroundColor: "red",
        }}
      >
        <MessageBubbleList propRef={msgRef} />
      </View>
      <KeyboardAvoidingView
        pointerEvents="box-none"
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        keyboardVerticalOffset={keyboardVerticalOffset}
      >
        <View
          style={{
            // backgroundColor: "yellow",
            padding: 10,
            height: minimize === true ? minimizeHeight : undefined,
          }}
        >
          <View style={{ flexDirection: 'row' }}>
            <Picker
              style={{
                height: Platform.select({ ios: 200, default: 30 }),
                width: Platform.select({ ios: 200, default: 150 }),
                flexShrink: 1,
              }}
              selectedValue={selectedType}
              onValueChange={(itemValue, _) => setSelectedType(itemValue)}
            >
              <Picker.Item
                label={ChatMessageType.CMD}
                value={ChatMessageType.CMD.toString()}
              />
              <Picker.Item
                label={ChatMessageType.CUSTOM}
                value={ChatMessageType.CUSTOM.toString()}
              />
              <Picker.Item
                label={ChatMessageType.FILE}
                value={ChatMessageType.FILE.toString()}
              />
              <Picker.Item
                label={ChatMessageType.IMAGE}
                value={ChatMessageType.IMAGE.toString()}
              />
              <Picker.Item
                label={ChatMessageType.LOCATION}
                value={ChatMessageType.LOCATION.toString()}
              />
              <Picker.Item
                label={ChatMessageType.TXT}
                value={ChatMessageType.TXT.toString()}
              />
              <Picker.Item
                label={ChatMessageType.VIDEO}
                value={ChatMessageType.VIDEO.toString()}
              />
              <Picker.Item
                label={ChatMessageType.VOICE}
                value={ChatMessageType.VOICE.toString()}
              />
            </Picker>
            <Pressable
              style={styles.button}
              onPress={() => {
                onSend();
              }}
            >
              <Text style={styles.buttonText}>Send Message</Text>
            </Pressable>
            <Pressable
              style={styles.button}
              onPress={() => {
                setMinimize(minimize === true ? false : true);
                setButtonName(buttonName === 'Mini' ? 'Comm' : 'Mini');
              }}
            >
              <Text style={styles.buttonText}>{buttonName}</Text>
            </Pressable>
          </View>
          <RenderBody type={selectedType as ChatMessageType} />
        </View>
      </KeyboardAvoidingView>

      <RenderContextMenu
        data={msgBubbleData ?? ({} as MessageItemType)}
        visible={visible}
        onClose={({ data, type }) => {
          setVisible(visible === true ? false : true);
          if (type === 'da') {
            downloadAttachment(data);
          }
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
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
  button2: {
    padding: 10,
    height: 40,
    backgroundColor: 'blue',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 5,
  },
  buttonText: {
    color: 'white',
    fontWeight: '500',
  },
  file: {
    // backgroundColor: "green",
    borderRadius: 5,
    justifyContent: 'center',
    flexDirection: 'row',
  },
  image: {
    // backgroundColor: "green",
    borderRadius: 5,
    justifyContent: 'center',
    flexDirection: 'row',
  },
  txt: {
    backgroundColor: '#d3d3d3',
    overflow: 'hidden',
    borderRadius: 5,
    paddingHorizontal: 10,
    justifyContent: 'center',
  },
});

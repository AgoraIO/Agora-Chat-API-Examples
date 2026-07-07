import * as VideoThumbnails from 'expo-video-thumbnails';
import * as ImagePicker from 'expo-image-picker';
import type { PickedMedia } from './ImageHandler';

function toPickedMedia(ret: ImagePicker.ImagePickerResult): PickedMedia {
  if (ret.canceled || !ret.assets?.[0]) {
    return { cancelled: true };
  }
  const asset = ret.assets[0];
  return {
    cancelled: false,
    uri: asset.uri,
    width: asset.width,
    height: asset.height,
    duration: asset.duration ?? undefined,
  };
}

export class VideoHandler {
  constructor() {}
  async getVideo(): Promise<PickedMedia> {
    const ret = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ['videos'],
    });
    return toPickedMedia(ret);
  }
  async getThumbnail(params: { fileName: string }) {
    const ret: VideoThumbnails.VideoThumbnailsResult =
      await VideoThumbnails.getThumbnailAsync(params.fileName);
    return ret;
  }
}

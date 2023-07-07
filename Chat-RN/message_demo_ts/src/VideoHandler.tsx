import * as VideoThumbnails from 'expo-video-thumbnails';
import * as ImagePicker from 'expo-image-picker';

export class VideoHandler {
  constructor() {}
  async getVideo() {
    const ret = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Videos,
    });
    return ret;
  }
  async getThumbnail(params: { fileName: string }) {
    const ret: VideoThumbnails.VideoThumbnailsResult =
      await VideoThumbnails.getThumbnailAsync(params.fileName);
    return ret;
  }
}

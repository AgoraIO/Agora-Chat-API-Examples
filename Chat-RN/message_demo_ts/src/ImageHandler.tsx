import * as ImagePicker from 'expo-image-picker';

export class ImageHandler {
  constructor() {}
  async getPermission() {
    const ml = await ImagePicker.getMediaLibraryPermissionsAsync();
    const ca = await ImagePicker.getCameraPermissionsAsync();
    if (ml.granted === true && ca.granted === true) {
      return true;
    } else {
      return false;
    }
  }
  async requestPermission() {
    const ca = await ImagePicker.requestCameraPermissionsAsync();
    if (ca.granted !== true) {
      return false;
    }
    const ml = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (ml.granted === true) {
      return true;
    }
    return false;
  }
  async getImage() {
    const ret = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
    });
    return ret;
  }
  async getCamera() {
    const ret = await ImagePicker.launchCameraAsync();
    return ret;
  }
}

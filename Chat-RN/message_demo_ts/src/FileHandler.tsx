import * as DocumentPicker from 'expo-document-picker';
import * as fileSystem from 'expo-file-system';

export class FileHandler {
  constructor() {}
  async getFile() {
    const ret: DocumentPicker.DocumentResult =
      await DocumentPicker.getDocumentAsync();
    const { type, ...others } = ret;
    if (type === 'cancel') {
      return { ...others, cancelled: true };
    } else {
      return { ...others, cancelled: false };
    }
  }
  async isExisted(params: { fileUri: string }) {
    try {
      const ret = await fileSystem.getInfoAsync(params.fileUri);
      return ret.exists;
    } catch (error) {
      return false;
    }
  }
}

import * as DocumentPicker from 'expo-document-picker';
import * as fileSystem from 'expo-file-system';

export class FileHandler {
  constructor() {}
  async getFile() {
    const ret = await DocumentPicker.getDocumentAsync();
    if (ret.canceled || !ret.assets?.[0]) {
      return { cancelled: true };
    }
    return { ...ret.assets[0], cancelled: false };
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

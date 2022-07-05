package io.agora.agorachatquickstart.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;


public class ImageUtils {

    /**
     * Open system Album
     * @param activity
     * @param requestCode
     */
    public static void openPhotoAlbum(Activity activity, int requestCode) {
        Intent intent = null;
        if(Build.VERSION.SDK_INT >= 29 && activity.getApplicationInfo().targetSdkVersion >= 29) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }
}

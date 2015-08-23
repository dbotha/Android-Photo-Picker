package kite.ly.photopicker;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by deon on 23/08/15.
 */
public class PhotoPicker {
    public static final String EXTRA_SELECTED_PHOTOS = "ly.kite.photopicker.EXTRA_SELECTED_PHOTOS";

    public static void startPhotoPickerForResult(Activity activity, int requestCode) {
        Intent i = new Intent(activity, DeviceFolderActivity.class);
        activity.startActivityForResult(i, requestCode);
    }
}

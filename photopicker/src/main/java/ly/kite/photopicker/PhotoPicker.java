package ly.kite.photopicker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Parcelable;

/**
 * Created by deon on 23/08/15.
 */
public class PhotoPicker {
    public static final String EXTRA_SELECTED_PHOTOS = "ly.kite.photopicker.EXTRA_SELECTED_PHOTOS";

    public static void startPhotoPickerForResult(Activity activity, int requestCode) {
        Intent i = new Intent(activity, DeviceFolderActivity.class);
        activity.startActivityForResult(i, requestCode);
    }

    public static void startPhotoPickerForResult(Fragment fragment, int requestCode) {
        Intent i = new Intent(fragment.getActivity(), DeviceFolderActivity.class);
        fragment.startActivityForResult(i, requestCode);
    }

    public static Photo[] getResultPhotos(Intent data) {
        Parcelable[] parcelables = data.getParcelableArrayExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS);
        Photo[] photos = new Photo[parcelables.length];
        System.arraycopy(photos, 0, photos, 0, parcelables.length);
        return photos;
    }
}

package ly.kite.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE_PHOTO_PICKER = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLaunchClick(View view) {
        PhotoPicker.startPhotoPickerForResult(this, REQUEST_CODE_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Parcelable[] parcelables = data.getParcelableArrayExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS);
                Photo[] photos = new Photo[parcelables.length];
                System.arraycopy(photos, 0, photos, 0, parcelables.length);

                Toast.makeText(this, "User selected " + photos.length + " photos", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Photo Picking Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("dbotha", "Unknown result code: " + resultCode);
            }
        }
    }
}

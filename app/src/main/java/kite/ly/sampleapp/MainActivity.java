package kite.ly.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import kite.ly.photopicker.DeviceFolderActivity;
import kite.ly.photopicker.PhotoPicker;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE_PHOTO_PICKER = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLaunchClick(View view) {
        PhotoPicker.startPhotoPickerForResult(this, REQUEST_CODE_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Parcelable[] parcelables = data.getParcelableArrayExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS);
                PhotoPicker[] photos = new PhotoPicker[parcelables.length];
                System.arraycopy(photos, 0, photos, 0, parcelables.length);

                Toast.makeText(this, "User selected " + photos.length + " photos", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Instagram Picking Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("dbotha", "Unknown result code: " + resultCode);
            }
        }
    }
}

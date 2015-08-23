package ly.kite.photopicker;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class GalleryActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_MEDIA_BUCKET_ID = "ly.kite.photopicker.EXTRA_MEDIA_BUCKET_ID";
    public static final String EXTRA_MEDIA_BUCKET_NAME = "ly.kite.photopicker.EXTRA_MEDIA_BUCKET_NAME";

    private static final String KEY_SELECTED_PHOTOS = "ly.kite.photopicker.KEY_SELECTED_PHOTOS";

    // These are the Contacts rows that we will retrieve.
    static final String[] GALLERY_PROJECTION = new String[] {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATE_TAKEN
    };

    private final HashMap<Integer, Photo> selectedPhotos = new HashMap<>();
    private GridView gridView;
    private GalleryCursorAdapter adapter;
    private String mediaBucketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (savedInstanceState != null) {
            Parcelable[] selected = savedInstanceState.getParcelableArray(KEY_SELECTED_PHOTOS);
            for (Parcelable parcelable : selected) {
                Photo photo = (Photo) parcelable;
                selectedPhotos.put(photo.getId(), photo);
            }
        }

        this.mediaBucketId = getIntent().getStringExtra(EXTRA_MEDIA_BUCKET_ID);
        setTitle(getIntent().getStringExtra(EXTRA_MEDIA_BUCKET_NAME));

        this.gridView = (GridView) findViewById(R.id.gridview);
        adapter = new GalleryCursorAdapter();
        gridView.setAdapter(adapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                boolean checked = gridView.isItemChecked(position);
                gridView.setItemChecked(position, !checked);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_MEDIA_BUCKET_ID, mediaBucketId);
        outState.putString(EXTRA_MEDIA_BUCKET_NAME, getTitle().toString());
        outState.putParcelableArray(KEY_SELECTED_PHOTOS, selectedPhotos.values().toArray(new Photo[0]));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_folder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Images.Media.BUCKET_ID + " = " + this.mediaBucketId;
        String sort = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, baseUri, GALLERY_PROJECTION, selection, null, sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        adapter.swapCursor(null);

    }

    private final class GalleryCursorAdapter extends CursorAdapter {

        public GalleryCursorAdapter() {
            super(GalleryActivity.this, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.photo_cell, parent, false);
            PhotoViewHolder holder = new PhotoViewHolder();
            holder.photo = (ImageView) view.findViewById(R.id.imageview);
            holder.checkbox = (ImageView) view.findViewById(R.id.checkbox);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            PhotoViewHolder holder = (PhotoViewHolder) view.getTag();

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));
            Picasso.with(context)
                    .load(imageURI)
                    .fit()
                    .centerCrop()
                    .into(holder.photo);


            holder.checkbox.setImageResource(selectedPhotos.containsKey(id) ? R.drawable.checkbox_on : R.drawable.checkbox_off);
        }
    }

    private static final class PhotoViewHolder {
        ImageView photo;
        ImageView checkbox;
    }

    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.photo_selection_menu, menu);

            int selectCount = gridView.getCheckedItemCount();
            mode.setTitle("" + selectCount);

            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_done) {
                Intent i = new Intent();
                Photo[] photos = new Photo[selectedPhotos.size()];
                selectedPhotos.values().toArray(photos);
                i.putExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS, photos);
                setResult(Activity.RESULT_OK, i);
                finish();
            }

            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            selectedPhotos.clear();
            adapter.notifyDataSetChanged();
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int selectCount = gridView.getCheckedItemCount();
            mode.setTitle("" + selectCount);

            Cursor cursor = (Cursor) adapter.getItem(position);
            int photoId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(photoId));

            if (checked) {
                selectedPhotos.put(photoId, new Photo(imageURI, photoId));
            } else {
                selectedPhotos.remove(photoId);
            }

            adapter.notifyDataSetChanged();
        }

    }

}

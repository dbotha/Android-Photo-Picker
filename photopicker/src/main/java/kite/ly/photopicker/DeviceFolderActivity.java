package kite.ly.photopicker;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashSet;


public class DeviceFolderActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    // These are the Media rows that we will retrieve.
    private static final String[] FOLDER_SUMMARY_PROJECTION = new String[] {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
    };

    private GridView gridView;
    private FolderCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_folder);

        this.gridView = (GridView) findViewById(R.id.gridview);
        adapter = new FolderCursorAdapter();
        gridView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                String bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                String folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                Intent i = new Intent(DeviceFolderActivity.this, GalleryActivity.class);
                i.putExtra(GalleryActivity.EXTRA_MEDIA_BUCKET_ID, bucketId);
                i.putExtra(GalleryActivity.EXTRA_MEDIA_BUCKET_NAME, folderName);
                startActivity(i);
            }
        });
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

        String sortBy = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " COLLATE NOCASE ASC, "
                + MediaStore.Images.Media.DATE_TAKEN + " DESC";

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, baseUri, FOLDER_SUMMARY_PROJECTION, null, null, sortBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Filter all returned media data to only contain distinct folders names
        MatrixCursor distinctFolderCursor = new MatrixCursor(FOLDER_SUMMARY_PROJECTION); // Same projection used in loader
        final HashSet<String> distinctFolders = new HashSet<>();

        if (cursor.moveToFirst()) {
            do {
                String folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                if (!distinctFolders.contains(folderName)) {
                    distinctFolders.add(folderName);
                    distinctFolderCursor.addRow(new Object[] {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3)});
                }
            } while (cursor.moveToNext());
        }

        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        adapter.swapCursor(distinctFolderCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        adapter.swapCursor(null);

    }

    private final class FolderCursorAdapter extends CursorAdapter {

        public FolderCursorAdapter() {
            super(DeviceFolderActivity.this, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.folder_cell, parent, false);
            FolderViewHolder holder = new FolderViewHolder();
            holder.cover = (ImageView) view.findViewById(R.id.imageview);
            holder.title = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            FolderViewHolder holder = (FolderViewHolder) view.getTag();
            String folderTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            holder.title.setText(folderTitle);

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));
            Picasso.with(context)
                    .load(imageURI)
                    .fit()
                    .centerCrop()
                    .into(holder.cover);
        }
    }

    private static final class FolderViewHolder {
        ImageView cover;
        TextView title;
    }

}

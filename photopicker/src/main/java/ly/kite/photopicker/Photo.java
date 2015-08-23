package ly.kite.photopicker;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by deon on 23/08/15.
 */
public class Photo implements Parcelable {

    private final Uri uri;
    private final int photoId;

    public Photo(Parcel in){
        uri = in.readParcelable(Uri.class.getClassLoader());
        photoId = in.readInt();
    }

    Photo(Uri uri, int photoId) {
        this.uri = uri;
        this.photoId = photoId;
    }

    public Uri getUri() {
        return uri;
    }

    int getId() {
        return photoId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeInt(photoId);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public int hashCode() {
        int v = 17;
        v = v * 31 + uri.hashCode();
        v = v * 31 + photoId;
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Photo)) {
            return false;
        }

        Photo photo = (Photo) o;
        return photo.uri.equals(uri) && photo.getId() == photoId;
    }

}

# Android Photo Picker

A image picker providing a simple UI for a user to pick photos from their device external storage / camera roll / photo gallery. 

## Video Preview

[![Preview](https://github.com/OceanLabs/InstagramPhotoPicker-Android/raw/master/screenshot.png)](https://vimeo.com/137073188)

## Requirements

* Android API Level 14 - Android 4.0 (ICE_CREAM_SANDWICH)

## Installation
### Android Studio / Gradle

We publish builds of our library to the Maven central repository as an .aar file. This file contains all of the classes, resources, and configurations that you'll need to use the library. To install the library inside Android Studio, you can simply declare it as dependecy in your build.gradle file.

```java
dependencies {
    compile 'ly.kite:photo-picker:1.+@aar'
}
```

Once you've updated your build.gradle file, you can force Android Studio to sync with your new configuration by selecting Tools -> Android -> Sync Project with Gradle Files

This should download the aar dependency at which point you'll have access to the API calls. If it cannot find the dependency, you should make sure you've specified mavenCentral() as a repository in your build.gradle

## Usage

To launch the Photo Picker:

```java
// Somewhere in an Activity:

import ly.kite.photopicker.Photo;
import ly.kite.photopicker.PhotoPicker;

static final int REQUEST_CODE_PHOTO_PICKER = 1;

PhotoPicker.startPhotoPickerForResult(this, REQUEST_CODE_PHOTO_PICKER);
```

Implement `onActivityResult`:

```java

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
    
```

### Sample Apps
The project is bundled with a Sample App to highlight the libraries usage.

## License
This project is available under the MIT license. See the [LICENSE](LICENSE) file for more info.
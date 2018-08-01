package sfllhkhan95.doodle.auth.utils;


import android.graphics.Bitmap;

import pk.aspirasoft.core.io.BitmapDownloadTask;
import pk.aspirasoft.core.io.OnCompleteListener;

public class FacebookUserPhotoDownloader {

    private final String photoUrl;

    private OnCompleteListener<Bitmap> photoTracker;

    public FacebookUserPhotoDownloader(String userId, int wd, int ht) {
        String width = String.valueOf(wd);
        String height = String.valueOf(ht);

        this.photoUrl = "https://graph.facebook.com/" + userId +
                "/picture?width=" + width + "&height=" + height;
    }

    public void setPhotoTracker(OnCompleteListener<Bitmap> photoTracker) {
        this.photoTracker = photoTracker;
    }

    public void execute() {
        BitmapDownloadTask downloadTask = new BitmapDownloadTask();
        downloadTask.setOnCompleteListener(photoTracker);
        downloadTask.execute(photoUrl);
    }

}
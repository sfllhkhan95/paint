package pk.aspirasoft.core.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;


public class BitmapDownloadTask extends AsyncTask<Bitmap> {

    @Nullable
    @Override
    protected Bitmap run(String... params) throws Exception {
        FileDownloadTask fileDownloadTask = new FileDownloadTask();
        InputStream inputStream = fileDownloadTask.run(params);
        return BitmapFactory.decodeStream(inputStream);
    }
}

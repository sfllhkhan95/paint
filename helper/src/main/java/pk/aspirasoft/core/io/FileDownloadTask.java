package pk.aspirasoft.core.io;


import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.URL;


public class FileDownloadTask extends AsyncTask<InputStream> {
    @Nullable
    @Override
    protected InputStream run(String... params) throws Exception {
        String url = params[0];
        URL fileUrl = new URL(url);
        return (InputStream) fileUrl.getContent();
    }
}

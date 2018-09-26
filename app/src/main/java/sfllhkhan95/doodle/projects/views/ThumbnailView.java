package sfllhkhan95.doodle.projects.views;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import sfllhkhan95.doodle.projects.models.Thumbnail;

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
public class ThumbnailView {

    private final ImageView iconView;

    public ThumbnailView(ImageView iconView) {
        this.iconView = iconView;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        Bitmap mBitmap = thumbnail.getIcon();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Glide.with(iconView.getContext())
                .load(stream.toByteArray())
                .into(iconView);
    }

}
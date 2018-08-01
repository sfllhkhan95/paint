package sfllhkhan95.doodle.projects.views;

import android.widget.ImageView;
import android.widget.TextView;

import sfllhkhan95.doodle.projects.models.Thumbnail;

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
public class ThumbnailView {

    private final TextView nameView;
    private final ImageView iconView;

    public ThumbnailView(TextView nameView, ImageView iconView) {
        this.nameView = nameView;
        this.iconView = iconView;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        String name = thumbnail.getName();
        int startFrom = name.indexOf('_');
        int endAt = name.indexOf('.');
        if (startFrom != -1 && endAt != -1) {
            name = name.substring(startFrom + 1, endAt);
        }
        nameView.setText(name);
        iconView.setImageBitmap(thumbnail.getIcon());
    }

}
package sfllhkhan95.doodle.projects.views;

import sfllhkhan95.doodle.projects.models.Thumbnail;

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
public class ThumbnailView {

    private final SquareImageView iconView;

    public ThumbnailView(SquareImageView iconView) {
        this.iconView = iconView;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        iconView.setImageBitmap(thumbnail.getIcon());
    }

}
package sfllhkhan95.doodle.projects.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.utils.AuthHandler;
import sfllhkhan95.doodle.auth.views.UserView;
import sfllhkhan95.doodle.projects.models.Thumbnail;
import sfllhkhan95.doodle.projects.views.ThumbnailView;

public class ThumbnailAdapter extends ArrayAdapter<Thumbnail> {

    private final Activity context;
    private final int gridLayoutId;

    private final AuthHandler mAuthHandler;

    private final List<Thumbnail> thumbnails;

    ThumbnailAdapter(Activity context, int gridLayoutId, List<Thumbnail> thumbnails) {
        super(context, gridLayoutId, thumbnails);
        this.gridLayoutId = gridLayoutId;
        this.context = context;
        this.thumbnails = thumbnails;

        this.mAuthHandler = new AuthHandler(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View gridItem = convertView;
        ThumbnailView thumbnailView;
        if (gridItem == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            gridItem = inflater.inflate(gridLayoutId, parent, false);

            thumbnailView = new ThumbnailView((ImageView) gridItem.findViewById(R.id.projectIcon));

            gridItem.setTag(thumbnailView);
        } else {
            thumbnailView = (ThumbnailView) gridItem.getTag();
        }
        try {
            gridItem.findViewById(R.id.deleteButton).setOnClickListener(thumbnails.get(position));
            gridItem.findViewById(R.id.shareButton).setOnClickListener(thumbnails.get(position));
            ((TextView) gridItem.findViewById(R.id.email)).setText(mAuthHandler.getCurrentUser().getEmail());
            new UserView(context)
                    .setEmailView((TextView) gridItem.findViewById(R.id.email))
                    .setAvatarView((ImageView) gridItem.findViewById(R.id.userAvatar))
                    .showUser(mAuthHandler.getCurrentUser());
        } catch (Exception ignored) {

        }
        thumbnailView.setThumbnail(thumbnails.get(position));
        return gridItem;
    }

}
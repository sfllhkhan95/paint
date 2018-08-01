package sfllhkhan95.doodle.projects.models;

import android.graphics.Bitmap;
import android.view.View;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.views.ConfirmationDialog;
import sfllhkhan95.doodle.projects.utils.DoodleDatabase;
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater;

public class Thumbnail implements View.OnClickListener {

    private final ThumbnailInflater inflater;

    private Bitmap icon;
    private String name;

    public Thumbnail(ThumbnailInflater inflater, Bitmap icon, String name) {
        this.inflater = inflater;
        this.icon = icon;
        this.name = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onClick(View view) {
        new ConfirmationDialog.Builder(view.getContext())
                .setHeadline("Delete")
                .setIcon(R.drawable.ic_action_delete)
                .setTitle(view.getContext().getResources().getString(R.string.confirmDeleteTitle))
                .setMessage(view.getContext().getResources().getString(R.string.confirmDeleteMessage))
                .setPositiveButton("Delete", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DoodleDatabase.removeDoodle(Thumbnail.this.getName());
                        inflater.run();
                    }
                }, true)
                .setNegativeButton("Cancel", null, true)
                .create()
                .show();
    }

}
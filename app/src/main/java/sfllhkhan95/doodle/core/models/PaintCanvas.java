package sfllhkhan95.doodle.core.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.projects.utils.DoodleDatabase;


/**
 * @author saifkhichi96
 */
public class PaintCanvas extends Canvas {

    private static final int DEFAULT_BG_COLOR = R.color.defaultCanvasColor;
    private int bgColor;

    private Bitmap bgImage;
    private Bitmap bitmap;
    private String projectName = null;

    public PaintCanvas(Context context, DisplayMetrics metrics) {
        bgColor = context.getResources().getColor(DEFAULT_BG_COLOR);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        setBitmap(bitmap);
    }

    public static PaintCanvas loadFromBitmap(Context context, DisplayMetrics metrics, Bitmap srcBmp) {
        PaintCanvas canvas = new PaintCanvas(context, metrics);

        float deviceAspect = metrics.widthPixels / (float) metrics.heightPixels;
        float bmpAspect = srcBmp.getWidth() / (float) srcBmp.getHeight();

        if (deviceAspect != bmpAspect) {
            if (deviceAspect > bmpAspect) { // Device is wider, fit width
                float targetHeight = srcBmp.getWidth() / deviceAspect;
                srcBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        (int) ((srcBmp.getHeight() - targetHeight) / 2),
                        srcBmp.getWidth(),
                        (int) targetHeight
                );
            } else {
                float targetWidth = srcBmp.getHeight() * deviceAspect;
                srcBmp = Bitmap.createBitmap(
                        srcBmp,
                        (int) ((srcBmp.getWidth() - targetWidth) / 2),
                        0,
                        (int) targetWidth,
                        srcBmp.getHeight()
                );
            }

            if (metrics.widthPixels > metrics.heightPixels) {
                int w = metrics.widthPixels;
                int h = (int) (metrics.widthPixels * srcBmp.getHeight() / (float) srcBmp.getWidth());
                canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, w, h, false);
            } else {
                int h = metrics.heightPixels;
                int w = (int) (metrics.heightPixels * srcBmp.getWidth() / (float) srcBmp.getHeight());
                canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, w, h, false);
            }
        } else {
            canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, metrics.widthPixels, metrics.heightPixels, false);
        }

        return canvas;
    }

    public static PaintCanvas loadFromPath(Context context, DisplayMetrics metrics, String bmpPath) {
        Bitmap srcBmp = DoodleDatabase.loadDoodle(bmpPath, metrics.widthPixels, metrics.heightPixels);
        PaintCanvas canvas = loadFromBitmap(context, metrics, srcBmp);
        canvas.setProjectName(bmpPath);

        return canvas;
    }

    public int getColor() {
        return bgColor;
    }

    public void setColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void drawShapes(Tools tools) {
        tools.paint(this);
    }

    public void drawBackground() {
        if (bgImage == null) {
            drawColor(bgColor);
        } else {
            drawBitmap(bgImage, 0, 0, null);
        }
    }

    private void setProjectName(String title) {
        this.projectName = title;
    }

    public void clearProjectName() {
        this.projectName = null;
    }

    public void saveProject() {
        if (projectName == null) {
            DoodleDatabase.saveDoodle(bitmap);
        } else {
            DoodleDatabase.saveDoodle(bitmap, projectName);
        }
    }

    public int getColor(PointF touchAt) {
        try {
            return bitmap.getPixel((int) touchAt.x, (int) touchAt.y);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return DEFAULT_BG_COLOR;
        }
    }
}
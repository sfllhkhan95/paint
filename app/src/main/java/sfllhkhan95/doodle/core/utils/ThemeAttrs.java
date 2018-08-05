package sfllhkhan95.doodle.core.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.util.TypedValue;

import sfllhkhan95.doodle.R;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 3.5.1 04/08/2018 2:58 PM
 */
public class ThemeAttrs {

    public final static String THEME_DEFAULT = "DEFAULT";
    public final static String THEME_SUNLIGHT = "SUNLIGHT";
    public final static String THEME_OCEAN = "OCEAN";

    public static int colorPrimary(Context context) {
        return getColor(context, R.attr.colorPrimary);
    }

    public static int colorPrimaryDark(Context context) {
        return getColor(context, R.attr.colorPrimaryDark);
    }

    public static int colorAccent(Context context) {
        return getColor(context, R.attr.colorAccent);
    }

    private static int getColor(Context context, @AttrRes int attrId) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{attrId});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

}

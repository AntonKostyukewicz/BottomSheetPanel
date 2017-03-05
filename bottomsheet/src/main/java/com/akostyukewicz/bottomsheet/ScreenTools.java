package com.akostyukewicz.bottomsheet;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class ScreenTools {

    public static int[] getScreenSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int[] sizeArray = new int[2];
        sizeArray[0] = size.x;
        sizeArray[1] = size.y;
        return sizeArray;
    }

    public static int getScreenHeight(Context ctx) {
        return getScreenSize(ctx)[1];
    }

    public static int getScreenWidth(Context ctx) {
        return getScreenSize(ctx)[0];
    }

    public static int getToolbarHeight(Context context) {
        TypedValue tv = new TypedValue();
        int toolbarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return toolbarHeight;
    }

    public static int getStatusBarHeight(Context context) {
        final int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight;
        if (resourceId > 0)
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        else
            statusBarHeight = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * context.getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

}

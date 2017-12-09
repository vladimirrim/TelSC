package ru.spbau.mit.telsc.model.core.pixels;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by mikhail on 09.12.17.
 */

public class Pixels {
    public static void setTransparent(Bitmap image, int x, int y) {
        setAlpha(image, x, y, 0);
    }

    public static void setAlpha(Bitmap image, int x, int y, int alpha) {
        int color = image.getPixel(x, y);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        image.setPixel(x, y, Color.argb(alpha, red, blue, green));
    }
}

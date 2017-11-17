package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by mikhail on 14.11.17.
 */

public class Filters {
    static public Bitmap grayScale(Bitmap image) {
        Bitmap grayScaledImage = Bitmap.createBitmap(image);
        for (int i = 0; i < grayScaledImage.getHeight(); i++) {
            for (int j = 0; j < grayScaledImage.getWidth(); j++) {
                int color = grayScaledImage.getPixel(i, j);
                int alpha = Color.alpha(color);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int average = (red + green + blue) / 3;
                grayScaledImage.setPixel(i, j, Color.argb(alpha, average, average, average));
            }
        }
        return grayScaledImage;
    }
}

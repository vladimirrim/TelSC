package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;

/**
 * Created by mikhail on 14.11.17.
 */

public class Transformations {
    static public Bitmap rotate(Bitmap image, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }
}

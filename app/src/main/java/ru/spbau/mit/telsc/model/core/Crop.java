package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;

import ru.spbau.mit.telsc.model.core.polygon.Polygon;

import static ru.spbau.mit.telsc.model.core.pixels.Pixels.setTransparent;

/**
 * Created by mikhail on 24.11.17.
 */

public class Crop {
    /**
     * Simple crop for rect.
     * @param image where to do crop.
     * @param rect to crop.
     * @return cropped image.
     */
    public static Bitmap cropRect(Bitmap image, Rect rect) {
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (!(i >= rect.bottom && i <= rect.top && j >= rect.left && j <= rect.right))
                    setTransparent(image, j, i);
            }
        }

        return image;
    }

    /**
     * Method to crop polygon from image. Cropping performed by setting aplha channel to transparent.
     * @param image where to do crop.
     * @param bound vertices of polygon which to crop.
     * @return cropped image.
     */
    public static Bitmap crop(Bitmap image, Polygon bound) {
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Polygon.Point pixel = new Polygon.Point(j, i);
                Polygon.Algorithm algorithm = Polygon.Algorithm.WINDING_NUMBER;

                if (!bound.isVertex(pixel) && !bound.contains(pixel, algorithm)) {
                    setTransparent(image, j, i);
                }
            }
        }

        return image;
    }
}

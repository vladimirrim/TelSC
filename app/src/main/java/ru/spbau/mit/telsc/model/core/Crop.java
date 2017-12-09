package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;

import ru.spbau.mit.telsc.model.core.polygon.Polygon;

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
                    setTransparent(image, i, j);
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

    /**
     * Crop any polygon given as array of serial points.
     * TODO: Not working correctly with vertices!
     * @param image where to do crop.
     * @param bound of polygon which to crop.
     * @return cropped image.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Bitmap crop(Bitmap image, ArrayList<Point> bound) {
        bound.sort((p1, p2) -> p1.y == p2.y ? p1.x - p2.x : p1.y - p2.y);

        int currentBoundPoint = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            int currentY = bound.get(currentBoundPoint).y;

            int left = 0, right = 0;
            boolean prevIsBound = false;
            for (int j = 0, currentBoundPointCopy = currentBoundPoint; j < image.getWidth(); j++) {
                int currentX = bound.get(currentBoundPointCopy).x;

                if (j == currentX && currentY == i) {
                    currentBoundPointCopy++;
                    if (!prevIsBound)
                        right++;
                    prevIsBound = true;
                }
                else {
                    prevIsBound = false;
                }
            }

            prevIsBound = false;
            for (int j = 0; j < image.getWidth(); j++) {
                int currentX = bound.get(currentBoundPoint).x;

                if (j == currentX && currentY == i) {
                    currentBoundPoint++;
                    if (!prevIsBound) {
                        left++;
                        right--;
                    }
                    prevIsBound = true;
                }
                else {
                    if (left % 2 == 1 && right % 2 == 1)
                        setTransparent(image, i, j);
                }
            }
        }

        return image;
    }

    private static void setTransparent(Bitmap image, int y, int x) {
        setAlpha(image, y, x, 0);
    }

    private static void setAlpha(Bitmap image, int y, int x, int alpha) {
        int color = image.getPixel(y, x);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        image.setPixel(y, x, Color.argb(alpha, red, blue, green));
    }
}

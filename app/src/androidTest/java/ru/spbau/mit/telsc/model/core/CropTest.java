package ru.spbau.mit.telsc.model.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.support.test.InstrumentationRegistry;
import android.widget.ImageView;

import org.junit.Test;

import ru.spbau.mit.telsc.model.core.polygon.Polygon;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 30.11.17.
 */
public class CropTest {
    @Test
    public void cropRectSimple() throws Exception {
        int width = 5, height = 5, blackColor = 0xFF000000;
        int color[] = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color[width * i + j] = blackColor;
            }
        }

        Bitmap blackImage = Bitmap.createBitmap(color, width, height, Bitmap.Config.ARGB_8888);
        blackImage = blackImage.copy(Bitmap.Config.ARGB_8888, true);
        Rect rect = new Rect(1, 3, 3, 1);
        Crop.cropRect(blackImage, rect);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i <= rect.top && i >= rect.bottom && j >= rect.left && j <= rect.right) {
                    assertEquals(blackColor, blackImage.getPixel(j, i));
                } else
                    assertEquals(0, blackImage.getPixel(j, i));
            }
        }
    }

    @Test
    public void cropRect() throws Exception {
        int width = 32, height = 32, blackColor = 0xFF000000;
        int color[] = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color[width * i + j] = blackColor;
            }
        }

        Bitmap blackImage = Bitmap.createBitmap(color, width, height, Bitmap.Config.ARGB_8888);
        blackImage = blackImage.copy(Bitmap.Config.ARGB_8888, true);
        Rect rect = new Rect(width / 4, 3 * height / 4, 3 * width / 4, height / 4);
        Crop.cropRect(blackImage, rect);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i <= rect.top && i >= rect.bottom && j >= rect.left && j <= rect.right) {
                    assertEquals(blackColor, blackImage.getPixel(j, i));
                } else
                    assertEquals(0, blackImage.getPixel(j, i));
            }
        }
    }

    @Test
    public void cropPolygon() throws Exception {
        int width = 32, height = 32, blackColor = 0xFF000000;
        int color[] = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color[width * i + j] = blackColor;
            }
        }

        Bitmap blackImage = Bitmap.createBitmap(color, width, height, Bitmap.Config.ARGB_8888);
        blackImage = blackImage.copy(Bitmap.Config.ARGB_8888, true);
        Rect rect = new Rect(width / 4, 3 * height / 4, 3 * width / 4, height / 4);
        Polygon bound = getPolygonRect(rect);
        Crop.crop(blackImage, bound);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i <= rect.top && i >= rect.bottom && j >= rect.left && j <= rect.right) {
                    assertEquals(blackColor, blackImage.getPixel(j, i));
                } else {
                    assertEquals(0, blackImage.getPixel(j, i));
                }
            }
        }
    }

    private Polygon getPolygonRect(Rect rect) {
        Polygon rectPolygon = new Polygon();

        int y = rect.bottom;
        for (int i = rect.left; i <= rect.right; i++)
            rectPolygon.add(new Polygon.Point(i, y));

        int x = rect.right;
        for (int i = rect.bottom; i <= rect.top; i++)
            rectPolygon.add(new Polygon.Point(x, i));

        y = rect.top;
        for (int i = rect.right; i >= rect.left; i--)
            rectPolygon.add(new Polygon.Point(i, y));

        x = rect.left;
        for (int i = rect.top; i >= rect.bottom; i--)
            rectPolygon.add(new Polygon.Point(x, i));

        return rectPolygon;
    }
}
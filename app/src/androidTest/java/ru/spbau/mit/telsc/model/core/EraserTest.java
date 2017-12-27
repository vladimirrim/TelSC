package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.Point;

import org.junit.Test;

import java.time.chrono.Era;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 09.12.17.
 */

public class EraserTest {
    @Test
    public void erase() throws Exception {
        int width = 5, height = 5, blackColor = 0xFF000000, erasedColor = 0x00000000;
        int color[] = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color[width * i + j] = blackColor;
            }
        }

        ArrayList<Point> curve = new ArrayList<>();
        curve.add(new Point(1,1));
        curve.add(new Point(2,2));
        curve.add(new Point(3,3));

        Bitmap blackImage = Bitmap.createBitmap(color, width, height, Bitmap.Config.ARGB_8888);
        blackImage = blackImage.copy(Bitmap.Config.ARGB_8888, true);

        Eraser.erase(blackImage, curve);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!curve.contains(new Point(j, i))) {
                    assertEquals(blackColor, blackImage.getPixel(j, i));
                } else {
                    assertEquals(erasedColor, blackImage.getPixel(j, i));
                }
            }
        }
    }
}
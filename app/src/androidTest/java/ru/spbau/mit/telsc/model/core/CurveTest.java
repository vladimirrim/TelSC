package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 30.11.17.
 */
public class CurveTest {
    @Test
    public void drawCurve() throws Exception {
        int width = 5, height = 5, blackColor = 0xFF000000, curveColor = 0xFFFFFFFF;
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

        Curve.drawCurve(blackImage, curve, curveColor);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!curve.contains(new Point(j, i))) {
                    assertEquals(blackColor, blackImage.getPixel(j, i));
                } else {
                    assertEquals(curveColor, blackImage.getPixel(j, i));
                }
            }
        }
    }
}
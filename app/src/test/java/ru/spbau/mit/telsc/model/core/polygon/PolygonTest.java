package ru.spbau.mit.telsc.model.core.polygon;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 30.11.17.
 */

public class PolygonTest {
    @Test
    public void addPoint() throws Exception {
        ArrayList<Polygon.Point> points = new ArrayList<>();
        points.add(new Polygon.Point(1, 1));
        points.add(new Polygon.Point(2, 1));
        points.add(new Polygon.Point(3, 1));
        Polygon polygon = new Polygon(points);

        polygon.add(new Polygon.Point(3, 2));
        polygon.add(new Polygon.Point(3, 3));
        polygon.add(new Polygon.Point(2, 3));
        polygon.add(new Polygon.Point(1, 3));
        polygon.add(new Polygon.Point(2, 2));

        assertEquals(new Polygon.Point(1, 1), polygon.get(0));
        assertEquals(new Polygon.Point(2, 1), polygon.get(1));
        assertEquals(new Polygon.Point(3, 1), polygon.get(2));
        assertEquals(new Polygon.Point(3, 2), polygon.get(3));
        assertEquals(new Polygon.Point(3, 3), polygon.get(4));
        assertEquals(new Polygon.Point(2, 3), polygon.get(5));
        assertEquals(new Polygon.Point(1, 3), polygon.get(6));
        assertEquals(new Polygon.Point(2, 2), polygon.get(7));
        assertEquals(new Polygon.Point(1, 1), polygon.get(8));
    }

    @Test
    public void addCollection() throws Exception {
        ArrayList<Polygon.Point> points = new ArrayList<>();
        points.add(new Polygon.Point(1, 1));
        points.add(new Polygon.Point(2, 1));
        points.add(new Polygon.Point(3, 1));
        Polygon polygon = new Polygon(points);

        points = new ArrayList<>();
        points.add(new Polygon.Point(3, 2));
        points.add(new Polygon.Point(3, 3));
        points.add(new Polygon.Point(2, 3));
        points.add(new Polygon.Point(1, 3));
        points.add(new Polygon.Point(2, 2));

        polygon.add(points);

        assertEquals(new Polygon.Point(1, 1), polygon.get(0));
        assertEquals(new Polygon.Point(2, 1), polygon.get(1));
        assertEquals(new Polygon.Point(3, 1), polygon.get(2));
        assertEquals(new Polygon.Point(3, 2), polygon.get(3));
        assertEquals(new Polygon.Point(3, 3), polygon.get(4));
        assertEquals(new Polygon.Point(2, 3), polygon.get(5));
        assertEquals(new Polygon.Point(1, 3), polygon.get(6));
        assertEquals(new Polygon.Point(2, 2), polygon.get(7));
        assertEquals(new Polygon.Point(1, 1), polygon.get(8));
    }

    @Test
    public void containsRectangle() throws Exception {
        /* Rectangle 3 * 3: (1,1) to (3,3) */
        ArrayList<Polygon.Point> points = new ArrayList<>();
        points.add(new Polygon.Point(1, 1));
        points.add(new Polygon.Point(2, 1));
        points.add(new Polygon.Point(3, 1));
        points.add(new Polygon.Point(3, 2));
        points.add(new Polygon.Point(3, 3));
        points.add(new Polygon.Point(2, 3));
        points.add(new Polygon.Point(1, 3));
        points.add(new Polygon.Point(1, 2));
        Polygon polygon = new Polygon(points);

        int height = 5, width = 5;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!(i >= 1 && i <= 3 && j >= 1 && j <= 3))
                    assertFalse(polygon.contains(new Polygon.Point(j, i), Polygon.Algorithm.BOTH_TRUE));
                else if (i == 2 && j == 2)
                    assertTrue(polygon.contains(new Polygon.Point(j, i), Polygon.Algorithm.BOTH_TRUE));
            }
        }
    }

    @Test
    public void containsCircle() throws Exception {
        /* Circle (2,1) - (3,2) - (2,3) - (1,2) */
        ArrayList<Polygon.Point> points = new ArrayList<>();
        points.add(new Polygon.Point(2, 1));
        points.add(new Polygon.Point(3, 2));
        points.add(new Polygon.Point(2, 3));
        points.add(new Polygon.Point(1, 2));
        Polygon polygon = new Polygon(points);

        int height = 5, width = 5;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!(j == 2 && i == 1 || j == 3 && i == 2 || j == 2 && i == 3 || j == 1 && i == 2 || j == 2 && i == 2))
                    assertFalse(polygon.contains(new Polygon.Point(j, i), Polygon.Algorithm.BOTH_TRUE));
                else if (i == 2 && j == 2)
                    assertTrue(polygon.contains(new Polygon.Point(j, i), Polygon.Algorithm.BOTH_TRUE));
            }
        }
    }
}
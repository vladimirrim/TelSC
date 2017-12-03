package ru.spbau.mit.telsc.model.core.polygon;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mikhail on 30.11.17.
 */

// Copyright 2000 softSurfer, 2012 Dan Sunday
// This code may be freely used and modified for any purpose
// providing that this copyright notice is included with it.
// SoftSurfer makes no warranty for this code, and cannot be held
// liable for any real or imagined damage resulting from its use.
// Users of this code must verify correctness for their application.

public class Polygon {
    public static class Point {
        private int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (!(other instanceof Point)) return false;

            Point otherPoint = (Point) other;
            return this.x == otherPoint.x && this.y == otherPoint.y;
        }
    }

    public enum Algorithm {
        CROSSING_NUMBER, WINDING_NUMBER, BOTH_TRUE, ONE_TRUE
    }

    private ArrayList<Point> vertices;

    public Polygon() {
        vertices = new ArrayList<>();
    }

    public Polygon(@NotNull ArrayList<Point> vertices) {
        this.vertices = vertices;
        if (!vertices.isEmpty())
            vertices.add(vertices.get(0));
    }

    public void add(@NotNull Point point) {
        if (!vertices.isEmpty())
            vertices.remove(vertices.size() - 1);
        vertices.add(point);
        vertices.add(vertices.get(0));
    }

    public void add(@NotNull Collection<Point> points) {
        if (!vertices.isEmpty())
            vertices.remove(vertices.size() - 1);
        vertices.addAll(points);
        vertices.add(vertices.get(0));
    }

    public Point get(int index) {
        return vertices.get(index);
    }

    public boolean isVertex(Point point) {
        return vertices.contains(point);
    }

    public boolean contains(Point point, Algorithm algorithm) {
        switch (algorithm) {
            case CROSSING_NUMBER:
                return containsCrossingNumber(point);
            case WINDING_NUMBER:
                return containsWindingNumber(point);
            case BOTH_TRUE:
                return containsWindingNumber(point) && containsCrossingNumber(point);
            case ONE_TRUE:
                return containsWindingNumber(point) || containsCrossingNumber(point);
            default:
                throw new IllegalArgumentException();
        }
    }

    // a Point is defined by its coordinates {int x, y;}

    // isLeft(): tests if a point is Left|On|Right of an infinite line.
    //    Input:  three points P0, P1, and P2
    //    Return: >0 for P2 left of the line through P0 and P1
    //            =0 for P2  on the line
    //            <0 for P2  right of the line
    //    See: Algorithm 1 "Area of Triangles and Polygons"
    private int isLeft(Point P0, Point P1, Point P2) {
        return ((P1.x - P0.x) * (P2.y - P0.y) - (P2.x -  P0.x) * (P1.y - P0.y));
    }
    //===================================================================


    // cn_PnPoly(): crossing number test for a point in a polygon
    //      Input:   point = a point,
    //               V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
    //      Return:  0 = outside, 1 = inside
    // This code is patterned after [Franklin, 2000]
    private boolean containsCrossingNumber(Point point) {
        int crossingNumber = 0;    // the  crossing number counter

        // loop through all edges of the polygon
        for (int i = 0; i < vertices.size() - 1; i++) {
            // edge from V[i]  to V[i+1]
            Point currentV = vertices.get(i), nextV = vertices.get(i + 1);

            if (((currentV.y <= point.y) && (nextV.y > point.y)) // an upward crossing
                    || ((currentV.y > point.y) && (nextV.y <=  point.y))) { // a downward crossing
                // compute  the actual edge-ray intersect x-coordinate
                double xIntersection = (double)(point.y  - currentV.y) / (nextV.y - currentV.y);
                if (point.x <  currentV.x + xIntersection * (nextV.x - currentV.x)) // point.x < intersect
                    crossingNumber++;   // a valid crossing of y=point.y right of point.x
            }
        }

        return crossingNumber % 2 != 0;    // 0 if even (out), and 1 if  odd (in)
    }


    // wn_PnPoly(): winding number test for a point in a polygon
    //      Input:   P = a point,
    //               V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
    //      Return:  wn = the winding number (=0 only when P is outside)
    private boolean containsWindingNumber(Point P) {
        int windingNumber = 0;    // the  winding number counter
        // loop through all edges of the polygon
        for (int i = 0; i < vertices.size() - 1; i++) {
            // edge from V[i]  to V[i+1]
            Point currentV = vertices.get(i), nextV = vertices.get(i + 1);

            if (currentV.y <= P.y) { // start y <= P.y
                if (nextV.y > P.y) // an upward crossing
                    if (isLeft(currentV, nextV, P) > 0) // P left of  edge
                        windingNumber++; // have  a valid up intersect
            }
            else {                        // start y > P.y (no test needed)
                if (nextV.y <= P.y)     // a downward crossing
                    if (isLeft(currentV, nextV, P) < 0)  // P right of  edge
                        windingNumber--;            // have  a valid down intersect
            }
        }
        return windingNumber != 0;
    }
}

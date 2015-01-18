package againstthewall;

import java.awt.geom.Point2D;

public class Vec {
    
    public static double distance(double x, double y) {
        return Math.abs(x - y);
    }
    
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    
    public static double distance(final Point2D.Double p1, final Point2D.Double p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }
    
    public static double intensity(double x, double y) {
        return distance(0, 0, x, y);
    }
    
    public static double intensity(final Point2D.Double p) {
        return intensity(p.x, p.y);
    }
    
    public static Point2D.Double neg(final Point2D.Double p) {
        return new Point2D.Double(-p.x, -p.y);
    }
    
    public static Point2D.Double add(final Point2D.Double p1, final Point2D.Double p2) {
        return new Point2D.Double(p1.x+p2.x, p1.y+p2.y);
    }
    
    public static Point2D.Double sub(final Point2D.Double p1, final Point2D.Double p2) {
        return add(p1, neg(p2));
    }
    
    public static Point2D.Double scale(final Point2D.Double p, double scale) {
        return new Point2D.Double(p.x*scale, p.y*scale);
    }
    
    public static double dot(final Point2D.Double p1, final Point2D.Double p2) {
        return p1.x*p2.x + p1.y*p2.y;
    }
    
    public static Point2D.Double normalize(final Point2D.Double p) {
        double intensity = intensity(p);
        return new Point2D.Double(p.x/intensity, p.y/intensity);
    }
    
    public static Point2D.Double bounce(final Point2D.Double v, final Point2D.Double n) {
        double scaleN = -2 * Vec.dot(v, n);
        return Vec.add(v, Vec.scale(n, scaleN));
    }
}

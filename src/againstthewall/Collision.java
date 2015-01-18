package againstthewall;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import againstthewall.GameData.Ball;

public class Collision {
    
    public static Point2D.Double collide(final Ball ball, final Point2D.Double edge) {
        
        Point2D.Double distance = Vec.sub(ball.center, edge);
        Point2D.Double n = Vec.normalize(distance);
        return Vec.bounce(ball.velocity, n);
    }
    
    public static Point2D.Double checkAndCollideEdge(final Ball ball, final Point2D.Double edge, double range) {
        
        boolean isCollided = Range.LSE(Vec.distance(ball.center, edge), ball.radius/2, range);
        if (isCollided) {
            return collide(ball, edge);
        } else {
            return null;
        }
    }
    
    public static Point2D.Double checkAndCollideSide(final Ball ball, final Point2D.Double a, final Point2D.Double b, double range) {
        
        if (a.y == b.y) {
            
            boolean isCollided = (Range.LSE(Vec.distance(ball.center.y, a.y), ball.radius/2, range)
                                  && Range.inRange(ball.center.x, a.x, b.x));

            if (isCollided) {
                return new Point2D.Double(ball.velocity.x, -ball.velocity.y);
            } else {
                return null;
            }
            
        } else if (a.x == b.x) {
            
            boolean isCollided = (Range.LSE(Vec.distance(ball.center.x, a.x), ball.radius/2, range)
                                  && Range.inRange(ball.center.y, a.y, b.y));

            if (isCollided) {
                return new Point2D.Double(-ball.velocity.x, ball.velocity.y);
            } else {
                return null;
            }
            
        } else {
            System.err.print("Collision with slopes are not implemeted");
            return null;
        }
    }
    
    public static Point2D.Double checkAndCollidePolygon(Ball ball, ArrayList<Point2D.Double> points, double range) {    
        
        for (int i = 0; i < points.size(); i++) {
            Point2D.Double result = checkAndCollideSide(ball, points.get(i), points.get((i + 1) % points.size()), range);
            if (result != null) {
                return result;
            }
        }
        
        for (Point2D.Double point: points) {
            Point2D.Double result = checkAndCollideEdge(ball, point, range);
            if (result != null) {
                return result;
            }
        } 
        
        return null;
    }
}

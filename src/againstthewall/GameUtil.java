package againstthewall;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import againstthewall.GameData.*;

public class GameUtil {

	public static ArrayList<Brick> getBrickWall(final Wall wall, final ArrayList<Color> rowColors) {
        
        ArrayList<Brick> bricks = new ArrayList<Brick>();
        double brickWidth = 1.0 / wall.columns;
        double brickHeight = wall.height / wall.rows;
        
        for (int i = 0; i < wall.rows; i++) {
            for (int j = 0; j < wall.columns; j++) {
                Rectangle2D.Double dimension = new Rectangle2D.Double(j * brickWidth, wall.x + i * brickHeight, brickWidth, brickHeight);
                Color color = rowColors.get(i);
                bricks.add(new Brick(dimension, color));
            }
        }
        
        return bricks;
    }
    
    public static Game initGame() {
        
        Sky sky = new Sky(new Color(0x11110e), Color.WHITE, new Ellipse2D.Double(0, 0, 1.0/250.0, 1.0/250.0), 100);
        Wall wall = new Wall(5, 10, 0.1, 0.5);
        
        ArrayList<Color> rowColors = new ArrayList<Color>();
        rowColors.add(new Color(0xff6961));
        rowColors.add(new Color(0xfdfd96));
        rowColors.add(new Color(0x77dd77));
        rowColors.add(new Color(0xaec6cf));
        rowColors.add(new Color(0xb39eb5));
        
        Color brickEdgeColor = Color.BLACK;
        Stroke brickEdgeStroke = new BasicStroke(1f/50f * (float)(wall.height/wall.rows));
        
        double padWidth = 0.1;
        double padHeight = 1.0 / 40.0;
        Pad pad = new Pad(new Rectangle2D.Double(0.5 - padWidth/2, 1 - padHeight, padWidth, padHeight), Color.WHITE);
        
        double ballRadius = 1.0/40.0;
        Point2D.Double ballDimension = new Point2D.Double(0.5, 1 - padHeight - ballRadius/2);
        double start = -Math.PI * Math.random();
        Paint ballColor = new GradientPaint((float)(-ballRadius/2), (float)(-ballRadius/2),
                                            new Color(0xff6961), (float)ballRadius, (float)ballRadius, new Color(0xfdfd96));
        Ball ball = new Ball(ballDimension, ballRadius, new Point2D.Double(0, 0), 0.1, ballColor);

        Point2D.Double ballStartVelocity = new Point2D.Double(0.01*Math.cos(start), 0.01*Math.sin(start));
        
        return new Game(sky, wall, rowColors, brickEdgeColor, brickEdgeStroke, pad, ball, ballStartVelocity);
    }
    
    public static Point2D.Double checkAndCollideCircle(Ball ball, Circle circle, double range) {
        
        double x = ball.center.x + ((ball.center.x-circle.center.x)*(ball.radius/(ball.radius + circle.radius)));
        double y = ball.center.y + ((ball.center.y-circle.center.y)*(ball.radius/(ball.radius + circle.radius)));
        Point2D.Double collisionPoint = new Point2D.Double(x, y);
        
        return Collision.checkAndCollideEdge(ball, collisionPoint, range);
    }
    
    public static Point2D.Double checkAndCollidePad(Ball ball, Pad pad, double range) {
        
        for (Circle edge: pad.edges) {
            Point2D.Double result = checkAndCollideCircle(ball, edge, range);
            if (result != null) {
                return result;
            }
        }
        
        return Collision.checkAndCollidePolygon(ball, pad.points, range);
    }

}

package againstthewall;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameData {
	
	public static final class Brick {

        public final Rectangle2D.Double dimension;
        public final ArrayList<Point2D.Double> points;
        public boolean isActive;
        public final Color color;
        
        public static Color edgeColor;
        public static Stroke edgeStroke;

        public Brick(final Rectangle2D.Double dimension, final Color color) {
            this.dimension = dimension;
            this.points = new ArrayList<Point2D.Double>();
            this.points.add(new Point2D.Double(dimension.x, dimension.y));
            this.points.add(new Point2D.Double(dimension.x + dimension.width, dimension.y));
            this.points.add(new Point2D.Double(dimension.x + dimension.width, dimension.y + dimension.height));
            this.points.add(new Point2D.Double(dimension.x, dimension.y + dimension.height));
            this.color = color;
            this.isActive = true;
        }
        
        public static void initEdge(final Color color, final Stroke stroke) {
            edgeColor = color;
            edgeStroke = stroke;
        }
    }
    
    public static final class Ball {
        
        public Point2D.Double center;
        public final LinkedList<Point2D.Double> ballCenterHistory;
        public final double radius;
        public final Ellipse2D.Double dimension;
        public Point2D.Double velocity;
        public double angle;
        public final double rotationVelocity;
        public final Paint color;
        
        public Ball(final Point2D.Double center, double radius, final Point2D.Double velocity, double rotationVelocity, final Paint color) {
            this.center = center;
            this.ballCenterHistory = new LinkedList<Point2D.Double>();
            this.radius = radius;
            this.dimension = new Ellipse2D.Double(-radius/2, -radius/2, radius, radius);
            this.velocity = velocity;
            this.angle = 0;
            this.rotationVelocity = rotationVelocity;
            this.color = color;
        }
    }
    
    public static class Star {
        
        public Point2D.Double position;
        public double velocity;
        
        public Star(final Point2D.Double position, double velocity) {
            this.position = position;
            this.velocity = velocity;
        }
    }
    
    public static class Sky {
        
        public final Color color;
        public final Color starColor;
        public final Ellipse2D.Double starShape;
        public final ArrayList<Star> stars;
        
        public Sky(final Color color, final Color starColor, final Ellipse2D.Double starShape, int numberOfStars) {
            this.color = color;
            this.starColor = starColor;
            this.starShape = starShape;
            this.stars = new ArrayList<Star>();
            for (int i = 0; i < numberOfStars; i++) {
                stars.add(new Star(new Point2D.Double(Math.random(), Math.random()), 0.001*Math.random()));
            }
        }
    }
    
    public static final class Wall {
        
        public final int rows;
        public final int columns;
        public final double x;
        public final double height;
     
        public Wall(int rows, int columns, double x, double height) {
            this.rows = rows;
            this.columns = columns;
            this.x = x;
            this.height = height;
        }
    }
    
    public static final class Circle {
        
        public final Point2D.Double center;
        public final double radius;
        
        public Circle(final Point2D.Double center, double radius) {
            this.center = center;
            this.radius = radius;
        }
    }
    
    public static final class Pad {
        
        public final Rectangle2D.Double dimension;
        public final Ellipse2D.Double edge;
        public final ArrayList<Point2D.Double> points;
        public final Circle[] edges;
        public final Color color;
        
        public Pad(final Rectangle2D.Double dimension, final Color color) {
            this.dimension = dimension;
            this.edge = new Ellipse2D.Double(0,0,dimension.height, dimension.height);
            this.points = new ArrayList<Point2D.Double>();
            this.points.add(new Point2D.Double(dimension.x, dimension.y));
            this.points.add(new Point2D.Double(dimension.x + dimension.width, dimension.y));
            this.points.add(new Point2D.Double(dimension.x + dimension.width, dimension.y + dimension.height));
            this.points.add(new Point2D.Double(dimension.x, dimension.y + dimension.height));
            Circle edge1 = new Circle(new Point2D.Double(dimension.x, dimension.y + dimension.height/2), dimension.height);
            Circle edge2 = new Circle(new Point2D.Double(dimension.x + dimension.width, dimension.y + dimension.height), dimension.height);
            this.edges = new Circle[]{edge1, edge2};
            this.color = color;
        }
    }
    
    public static final class Game {
        
        public final Sky sky;
        public final ArrayList<Brick> bricks;
        public final Pad pad;
        public final Ball ball;
        public final Point2D.Double ballStartVelocity;
        public final ArrayList<Point2D.Double> edge;
        public boolean gameOver;
        public int score;
        public ArrayList<Integer> scores;
        public boolean dirtyScores;
        
        public Game(final Sky sky, final Wall wall, final ArrayList<Color> rowColors, final Color brickEdgeColor,
                    final Stroke brickEdgeStroke, final Pad pad, final Ball ball, final Point2D.Double ballStartVelocity) {
            this.sky = sky;
            this.bricks = GameUtil.getBrickWall(wall, rowColors);
            Brick.initEdge(brickEdgeColor, brickEdgeStroke);
            this.pad = pad;
            this.ball = ball;
            this.ballStartVelocity = ballStartVelocity;
            this.edge = new ArrayList<Point2D.Double>();
            this.edge.add(new Point2D.Double(0, 0));
            this.edge.add(new Point2D.Double(1, 0));
            this.edge.add(new Point2D.Double(1, 1));
            this.edge.add(new Point2D.Double(0, 1));
            this.gameOver = false;
            this.score = 0;
            this.scores = new ArrayList<>();
            try {
				scores.addAll(HighScoreData.loadHighScoreData());
			} catch (IOException e) {}
            this.dirtyScores = true;
        }
    }
}

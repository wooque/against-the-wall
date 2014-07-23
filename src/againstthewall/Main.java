package againstthewall;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable, MouseMotionListener, MouseListener {

    public static final class Brick {

        public final Rectangle.Double dimension;
        // TODO: do this without mutabiliy
        public boolean isActive;
        public final Color color;
        
        public static Color edgeColor;
        public static Stroke edgeStroke;

        public Brick(final Rectangle2D.Double dimension, final Color color) {
            this.dimension = dimension;
            this.color = color;
            this.isActive = true;
        }
        
        public static void initEdge(final Color color, final Stroke stroke) {
            edgeColor = color;
            edgeStroke = stroke;
        }
    }
    
    public static final class HorizontalSide {
        public final Point2D.Double leftPoint;
        public final double width;
        
        public HorizontalSide(final Point2D.Double leftPoint, double width) {
            this.leftPoint = leftPoint;
            this.width = width;
        }
    }
    
    public static final class VerticalSide {
        public final Point2D.Double upPoint;
        public final double height;
        
        public VerticalSide(final Point2D.Double upPoint, double height) {
            this.upPoint = upPoint;
            this.height = height;
        }
    }
    
    public static final class Ball {
        
        // TODO: do this without mutability
        public Point2D.Double center;
        public final double radius;
        public final Point2D.Double velocity;
        // TODO: see to do this without mutability
        public double angle;
        public final double rotationVelocity;
        public final Paint color;
        
        public Ball(final Point2D.Double center, double radius, final Point2D.Double velocity, double rotationVelocity, final Paint color) {
            this.center = center;
            this.radius = radius;
            this.velocity = velocity;
            this.angle = 0;
            this.rotationVelocity = rotationVelocity;
            this.color = color;
        }
    }
    
    public static final class CollisionResult {
        
        public final Point2D.Double ballVelocity;
        public final boolean isCollided;
        
        public CollisionResult(final Point2D.Double ballVelocity, boolean isCollided) {
            this.ballVelocity = ballVelocity;
            this.isCollided = isCollided;
        }
    }
    
    public static class Sky {
        public final Color color;
        public final Color starColor;
        public final Ellipse2D.Double starShape;
        public final ArrayList<Point2D.Double> stars;
        
        public Sky(final Color color, final Color starColor, final Ellipse2D.Double starShape, int numberOfStars) {
            this.color = color;
            this.starColor = starColor;
            this.starShape = starShape;
            this.stars = new ArrayList<Point2D.Double>();
            for (int i = 0; i < numberOfStars; i++) {
                stars.add(new Point2D.Double(Math.random(), Math.random()));
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
    
    public static final class Pad {
        public final Rectangle2D.Double dimension;
        public final Color color;
        
        public Pad(final Rectangle2D.Double dimension, final Color color) {
            this.dimension = dimension;
            this.color = color;
        }
    }
    
    public static final class Game {
        public final Sky sky;
        public final ArrayList<Brick> bricks;
        public final Pad pad;
        public final Ball ball;
        // TODO: do this without mutability
        public boolean gameOver;
        public int score;
        
        public Game(final Sky sky, final Wall wall, final ArrayList<Color> rowColors, final Color brickEdgeColor,
                    final Stroke brickEdgeStroke, final Pad pad, final Ball ball) {
            this.sky = sky;
            this.bricks = getBrickWall(wall, rowColors);
            Brick.initEdge(brickEdgeColor, brickEdgeStroke);
            this.pad = pad;
            this.ball = ball;
            this.gameOver = false;
            this.score = 0;
        }
    }
    
    public static Game initGame() {
        Sky sky = new Sky(Color.BLACK, Color.WHITE, new Ellipse2D.Double(0, 0, 1.0/250.0, 1.0/250.0), 200);
        Wall wall = new Wall(5, 10, 0.1, 0.5);
        
        ArrayList<Color> rowColors = new ArrayList<Color>();
        rowColors.add(Color.RED);
        rowColors.add(Color.YELLOW);
        rowColors.add(Color.BLUE);
        rowColors.add(Color.GREEN);
        rowColors.add(Color.CYAN);
        
        Color brickEdgeColor = Color.BLACK;
        Stroke brickEdgeStroke = new BasicStroke(1f/50f * (float)(wall.height/wall.rows));
        
        double padWidth = 0.1;
        double padHeight = 1.0 / 40.0;
        Pad pad = new Pad(new Rectangle2D.Double(0.5 - padWidth/2, 1 - padHeight, padWidth, padHeight), Color.WHITE);
        
        double ballRadius = 1.0/40.0;
        Point2D.Double ballDimension = new Point2D.Double(0.5, 1 - padHeight - ballRadius/2);
        Point2D.Double ballVelocity = new Point2D.Double(0.01, -0.02);
        Paint ballColor = new GradientPaint(0, 0, Color.RED, (float)ballRadius, (float)ballRadius, Color.YELLOW);
        Ball ball = new Ball(ballDimension, ballRadius, ballVelocity, 0.01, ballColor);
        
        return new Game(sky, wall, rowColors, brickEdgeColor, brickEdgeStroke, pad, ball);
    }
    
    public static final double RANGE = 0.0005;
    
    public static boolean inRange(double x, double lower, double higher) {
        return (higher > lower) && (x >= lower) && (x <= higher);
    }

    public static boolean grOrEq(double a, double b, double range) {
        return inRange(a, b, b + range);
    }
    
    public static boolean lsOrEq(double a, double b, double range) {
        return inRange(a, b - range, b);
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
        double scaleN = -2 * dot(v, n);
        return add(v, scale(n, scaleN));
    }
    
    public static Point2D.Double collide(final Ball ball, final Point2D.Double edge) {
        
        Point2D.Double distance = sub(ball.center, edge);
        Point2D.Double n = normalize(distance);
        return bounce(ball.velocity, n);
    }
    
    public static CollisionResult checkAndCollideEdge(final Ball ball, final Point2D.Double edge) {
        
        boolean isCollided = lsOrEq(distance(ball.center, edge), ball.radius/2, RANGE);
        Point2D.Double newBallVelocity = isCollided? collide(ball, edge): ball.velocity;
        return new CollisionResult(newBallVelocity, isCollided);
    }
    
    public static CollisionResult checkAndCollideUpSide(final Ball ball, final HorizontalSide upSide) {
        
        boolean isCollided = (grOrEq(ball.center.y + ball.radius/2, upSide.leftPoint.y, RANGE)
                            && ball.center.x >= upSide.leftPoint.x && ball.center.x <= upSide.leftPoint.x + upSide.width);

       Point2D.Double newBallVelocity = isCollided? new Point2D.Double(ball.velocity.x, -ball.velocity.y): ball.velocity;
       return new CollisionResult(newBallVelocity, isCollided);
    }
    
    public static CollisionResult checkAndCollideDownSide(final Ball ball, final HorizontalSide downSide) {
        
        boolean isCollided = (lsOrEq(ball.center.y - ball.radius/2, downSide.leftPoint.y + downSide.width, RANGE)
                            && ball.center.x >= downSide.leftPoint.x && ball.center.x <= downSide.leftPoint.x + downSide.width);

       Point2D.Double newBallVelocity = isCollided? new Point2D.Double(ball.velocity.x, -ball.velocity.y): ball.velocity;
       return new CollisionResult(newBallVelocity, isCollided);
    }
    
    public static CollisionResult checkAndCollideLeftSide(final Ball ball, final VerticalSide leftSide) {
        
        boolean isCollided = (grOrEq(ball.center.x + ball.radius/2, leftSide.upPoint.x, RANGE)
                            && ball.center.y >= leftSide.upPoint.y && ball.center.y <= leftSide.upPoint.y + leftSide.height);

       Point2D.Double newBallVelocity = isCollided? new Point2D.Double(-ball.velocity.x, ball.velocity.y): ball.velocity;
       return new CollisionResult(newBallVelocity, isCollided);
    }
    
    public static CollisionResult checkAndCollideRightSide(final Ball ball, final VerticalSide rightSide) {
        
        boolean isCollided = (lsOrEq(ball.center.x - ball.radius/2, rightSide.upPoint.x + rightSide.height, RANGE)
                            && ball.center.y >= rightSide.upPoint.y && ball.center.y <= rightSide.upPoint.y + rightSide.height);

       Point2D.Double newBallVelocity = isCollided? new Point2D.Double(-ball.velocity.x, ball.velocity.y): ball.velocity;
       return new CollisionResult(newBallVelocity, isCollided);
    }
    
    private CollisionResult checkForCollision(Ball ball, Rectangle2D.Double brick) {
        
        CollisionResult result = checkAndCollideEdge(ball, new Point2D.Double(brick.x, brick.y));
        if (result.isCollided) {
            return result;
        }
        
        result = checkAndCollideEdge(ball, new Point2D.Double(brick.x + brick.width, brick.y));
        if (result.isCollided) {
            return result;
        }
        
        result = checkAndCollideEdge(ball, new Point2D.Double(brick.x, brick.y + brick.width));
        if (result.isCollided) {
            return result;
        }
        
        result = checkAndCollideEdge(ball, new Point2D.Double(brick.x + brick.width, brick.y + brick.width));
        if (result.isCollided) {
            return result;
        }
        
        result = checkAndCollideUpSide(ball, new HorizontalSide(new Point2D.Double(brick.x, brick.y), brick.width));
        if (result.isCollided) {
            return result;
        }
        
        result = checkAndCollideDownSide(ball, new HorizontalSide(new Point2D.Double(brick.x, brick.y + brick.height), brick.width));
        if (result.isCollided) {
            return result;
        }
        
        result = checkAndCollideLeftSide(ball, new VerticalSide(new Point2D.Double(brick.x, brick.y), brick.height));
        if (result.isCollided) {
            return result;
        }
        
        result = checkAndCollideLeftSide(ball, new VerticalSide(new Point2D.Double(brick.x + brick.width, brick.y), brick.height));
        if (result.isCollided) {
            return result;
        }
        
        return new CollisionResult(ball.velocity, false);
    }

    private void passTime(Game game, double time) {
        
        if (intensity(game.ball.velocity) <= 0)
            return;
            
        Point2D.Double newBallCenter;  
        newBallCenter = add(game.ball.center, scale(game.ball.velocity, time));
        game.ball.center = newBallCenter;
           
        // TODO: do this without mutability
        game.ball.angle += game.ball.rotationVelocity * time;
        game.ball.angle %= 2 * Math.PI;
        
        CollisionResult result = checkAndCollideLeftSide(game.ball, new VerticalSide(new Point2D.Double(1, 0), 1));
        if (result.isCollided) {
            return;
        }
        
        result = checkAndCollideRightSide(game.ball, new VerticalSide(new Point2D.Double(0, 0), 1));
        if (result.isCollided) {
            return;
        }
        
        result = checkAndCollideDownSide(game.ball, new HorizontalSide(new Point2D.Double(0, 0), 1));
        if (result.isCollided) {
            return;
        }
        
        result = checkAndCollideUpSide(game.ball, new HorizontalSide(new Point2D.Double(0, 1), game.pad.dimension.x));
        if (result.isCollided) {
            return;
        }
        
        result = checkAndCollideUpSide(game.ball, new HorizontalSide(new Point2D.Double(0, 1), game.pad.dimension.x));
        if (result.isCollided) {
            return;
        }
        
        double rightPadSide = game.pad.dimension.x + game.pad.dimension.width;
        double rightPadSideToRightScreenSide = 1 - rightPadSide;
        result = checkAndCollideUpSide(game.ball, new HorizontalSide(new Point2D.Double(rightPadSide, 1), rightPadSideToRightScreenSide));
        if (result.isCollided) {
            return;
        }
            
        result = checkForCollision(game.ball, game.pad.dimension);
        if(result.isCollided) {
            return;
        }
            
        // TODO: do this without mutability
        for (Brick brick: game.bricks) {
            if (brick.isActive) {
                result = checkForCollision(game.ball, brick.dimension);
                if (result.isCollided) {
                    brick.isActive = false;
                    game.score++;
                    if (game.score == 50) {
                        game.gameOver = true;
                    }
                    break;
                }
            }
        }
    }
    
    public static final Game game = initGame();

    public static final AffineTransform ident = new AffineTransform();
    
    private static void resetDrawingArea(Graphics2D g2d, Dimension dim) {
        g2d.setTransform(ident);
        g2d.scale(dim.width, dim.height);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dim = getSize();
        Graphics2D g2d = (Graphics2D) g;
        
        // TODO: do dynamic range
        // RANGE = 1.0 / dim.height;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setPaint(game.sky.color);
        g2d.fillRect(0, 0, dim.width, dim.height);
        
        // TODO extract constants
        if(!game.gameOver) {
            g2d.setPaint(Color.WHITE);
            g2d.drawString("Score: " + game.score, 0, g2d.getFontMetrics().getHeight());
        }

        resetDrawingArea(g2d, dim);
        g2d.setPaint(game.sky.starColor);
        for (Point2D.Double star : game.sky.stars) {
            resetDrawingArea(g2d, dim);
            g2d.translate(star.x, star.y);
            g2d.fill(game.sky.starShape);
        }

        if (!game.gameOver) {
            
            for (Brick brick: game.bricks) {
                if (brick.isActive) {
                    resetDrawingArea(g2d, dim);
                    g2d.setPaint(brick.color);
                    g2d.fill(brick.dimension);
                    g2d.setPaint(Brick.edgeColor);
                    g2d.setStroke(Brick.edgeStroke);
                    g2d.draw(brick.dimension);
                }
            }

            resetDrawingArea(g2d, dim);
            g2d.setPaint(game.pad.color);
            g2d.fill(game.pad.dimension);
    
            resetDrawingArea(g2d, dim);
            g2d.setPaint(game.ball.color);
            g2d.fill(new Ellipse2D.Double(game.ball.center.x, game.ball.center.y, game.ball.radius, game.ball.radius));
        } else {
            
            resetDrawingArea(g2d, dim);
            // TODO extract constants
            g2d.translate(0.2, 0.3);
            g2d.scale(5.0/dim.width, 5.0/dim.height);
            g2d.setPaint(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int visina = fm.getHeight();
            if (game.score < 50) {
                g2d.drawString("GAME OVER", 0, visina);
                g2d.drawString("Your score: " + game.score, 0, 2 * visina);
            } else {
                g2d.drawString("YOU WON", 0, visina);
            }
        }
        
        resetDrawingArea(g2d, dim);
    }
    
    public void run() {
        long starttime;

        while (true) {
            starttime = System.currentTimeMillis();
            for(int i = 0; i < 10 && !game.gameOver; i++) {
                passTime(game, 0.1);
                if (game.gameOver)
                    break;
            }
            repaint();
            starttime += 40;
            try {
                Thread.sleep(Math.max(0, starttime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved(final MouseEvent event) {
        if (event.getX() >= 0 && event.getX() <= getSize().width * (1 - game.pad.dimension.width)) {
            game.pad.dimension.x = (double)event.getX() / getSize().width;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO: add starting on click
        // game.ball.velocity = BALL_VELOCITY;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public static void createAndShowGUI() {

        JFrame frame = new JFrame("Against the Wall");
        frame.setSize(new Dimension(600, 600));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Main panel = new Main();
        panel.addMouseMotionListener(panel);
        panel.addMouseListener(panel);
        panel.setOpaque(true);
        frame.add(panel);

        frame.setVisible(true);

        Thread t = new Thread(panel);
        t.start();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

package againstthewall;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable, MouseMotionListener, MouseListener {

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
        public final ArrayList<Point2D.Double> points;
        public final Color color;
        
        public Pad(final Rectangle2D.Double dimension, final Color color) {
            this.dimension = dimension;
            this.points = new ArrayList<Point2D.Double>();
            this.points.add(new Point2D.Double(dimension.x, dimension.y));
            this.points.add(new Point2D.Double(dimension.x + dimension.width, dimension.y));
            this.points.add(new Point2D.Double(dimension.x + dimension.width, dimension.y + dimension.height));
            this.points.add(new Point2D.Double(dimension.x, dimension.y + dimension.height));
            this.color = color;
        }
    }
    
    public static final class Game {
        public final Sky sky;
        public final ArrayList<Brick> bricks;
        public final Pad pad;
        public final Ball ball;
        public final ArrayList<Point2D.Double> edge;
        public boolean gameOver;
        public int score;
        
        public Game(final Sky sky, final Wall wall, final ArrayList<Color> rowColors, final Color brickEdgeColor,
                    final Stroke brickEdgeStroke, final Pad pad, final Ball ball) {
            this.sky = sky;
            this.bricks = getBrickWall(wall, rowColors);
            Brick.initEdge(brickEdgeColor, brickEdgeStroke);
            this.pad = pad;
            this.ball = ball;
            this.edge = new ArrayList<Point2D.Double>();
            this.edge.add(new Point2D.Double(0, 0));
            this.edge.add(new Point2D.Double(1, 0));
            this.edge.add(new Point2D.Double(1, 1));
            this.edge.add(new Point2D.Double(0, 1));
            this.gameOver = false;
            this.score = 0;
        }
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
        Point2D.Double ballVelocity = new Point2D.Double(0, 0);
        Paint ballColor = new GradientPaint((float)(-ballRadius/2), (float)(-ballRadius/2),
                                            new Color(0xff6961), (float)ballRadius, (float)ballRadius, new Color(0xfdfd96));
        Ball ball = new Ball(ballDimension, ballRadius, ballVelocity, 0.1, ballColor);
        
        return new Game(sky, wall, rowColors, brickEdgeColor, brickEdgeStroke, pad, ball);
    }
    
    public static final Point2D.Double BALL_START_VELOCITY = new Point2D.Double(0.005, -0.01);
    public static final double RANGE = 0.001;
    
    public static boolean inRange(double x, double barrier1, double barrier2) {
        
        double high;
        double low;
        
        if (barrier1 > barrier2) {
            high = barrier1;
            low = barrier2;
        } else {
            high = barrier2;
            low = barrier1;
        }
        
        return (x >= low) && (x <= high);
    }
    
    public static boolean Eq(double a, double b, double range) {
        return inRange(a, b - range/2, b + range/2);
    }
    
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
        double scaleN = -2 * dot(v, n);
        return add(v, scale(n, scaleN));
    }
    
    public static Point2D.Double collide(final Ball ball, final Point2D.Double edge) {
        
        Point2D.Double distance = sub(ball.center, edge);
        Point2D.Double n = normalize(distance);
        return bounce(ball.velocity, n);
    }
    
    public static Point2D.Double checkAndCollideEdge(final Ball ball, final Point2D.Double edge) {
        
        boolean isCollided = Eq(distance(ball.center, edge), ball.radius/2, RANGE);
        if (isCollided) {
            return collide(ball, edge);
        } else {
            return null;
        }
    }
    
    public static Point2D.Double checkAndCollideSide(final Ball ball, final Point2D.Double a, final Point2D.Double b) {
        
        if (a.y == b.y) {
            
            boolean isCollided = (Eq(distance(ball.center.y, a.y), ball.radius/2, RANGE)
                                  && inRange(ball.center.x, a.x, b.x));

            if (isCollided) {
                return new Point2D.Double(ball.velocity.x, -ball.velocity.y);
            } else {
                return null;
            }
            
        } else if (a.x == b.x) {
            
            boolean isCollided = (Eq(distance(ball.center.x, a.x), ball.radius/2, RANGE)
                                  && inRange(ball.center.y, a.y, b.y));

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
    
    private Point2D.Double checkForCollision(Ball ball, ArrayList<Point2D.Double> points) {
        
        for (Point2D.Double point: points) {
            Point2D.Double result = checkAndCollideEdge(ball, point);
            if (result != null) {
                return result;
            }
        }     
        
        for (int i = 0; i < points.size(); i++) {
            Point2D.Double result = checkAndCollideSide(ball, points.get(i), points.get((i + 1) % points.size()));
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }

    private void passTime(Game game, double time) {
        
        if (intensity(game.ball.velocity) <= 0)
            return;
            
        Point2D.Double newBallCenter;  
        newBallCenter = add(game.ball.center, scale(game.ball.velocity, time));
        game.ball.ballCenterHistory.add(game.ball.center);
        if (game.ball.ballCenterHistory.size() > 100)
            game.ball.ballCenterHistory.pop();
        game.ball.center = newBallCenter;
        
        if (game.ball.center.y + game.ball.radius/2 >= 1) {
            game.gameOver = true;
            game.ball.velocity = new Point2D.Double(0, 0);
            return;
        }

        game.ball.angle += game.ball.rotationVelocity * time;
        game.ball.angle %= 2 * Math.PI;
        
        for (Star star: game.sky.stars) {
            star.position.x += star.velocity * time;
            if (star.position.x >= 1.0) {
                star.position.x = 0.0;
            }
        }
             
        Point2D.Double result = checkForCollision(game.ball, game.edge);
        if (result != null) {
            game.ball.velocity = result;
            return;
        }
        
        result = checkForCollision(game.ball, game.pad.points);
        if(result != null) {
            game.ball.velocity = result;
            return;
        }

        for (Brick brick: game.bricks) {
            if (brick.isActive) {
                result = checkForCollision(game.ball, brick.points);
                if (result != null) {
                    game.ball.velocity = result;
                    brick.isActive = false;
                    game.score++;
                    if (game.score == 50) {
                        game.gameOver = true;
                    }
                    break;
                }
            } else {
               brick.dimension.x += 0.001;
               brick.dimension.y += 0.001;
               brick.dimension.height -= 0.002;
               brick.dimension.width -= 0.002;
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
        
        if(!game.gameOver) {
            g2d.setPaint(Color.WHITE);
            g2d.drawString("Score: " + game.score, 0, g2d.getFontMetrics().getHeight());
        }

        resetDrawingArea(g2d, dim);
        g2d.setPaint(game.sky.starColor);
        for (Star star : game.sky.stars) {
            resetDrawingArea(g2d, dim);
            g2d.translate(star.position.x, star.position.y);
            g2d.fill(game.sky.starShape);
        }

        if (!game.gameOver) {
            
            for (Brick brick: game.bricks) {
                resetDrawingArea(g2d, dim);
                g2d.setPaint(brick.color);
                g2d.fill(brick.dimension);
                g2d.setPaint(Brick.edgeColor);
                g2d.setStroke(Brick.edgeStroke);
                g2d.draw(brick.dimension);
            }

            resetDrawingArea(g2d, dim);
            g2d.setPaint(game.pad.color);
            g2d.fill(game.pad.dimension);
    
            resetDrawingArea(g2d, dim);
            for (int i = 0; i < game.ball.ballCenterHistory.size() - 1; i++) {
                g2d.setStroke(new BasicStroke((float)(0.8*game.ball.radius)));
                g2d.setPaint(new Color(1.0f, 1.0f, 1.0f, (float)(0.25*i/game.ball.ballCenterHistory.size())));
                g2d.draw(new Line2D.Double(game.ball.ballCenterHistory.get(i), game.ball.ballCenterHistory.get(i+1)));
            }
            
            resetDrawingArea(g2d, dim);
            g2d.setPaint(game.ball.color);
            g2d.translate(game.ball.center.x, game.ball.center.y);
            g2d.rotate(game.ball.angle);
            g2d.fill(game.ball.dimension);         
            
        } else {
            
            resetDrawingArea(g2d, dim);
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
        if (inRange(event.getX(), 0, getSize().width * (1 - game.pad.dimension.width))) {
            
            double diff = (double)event.getX() / getSize().width - game.pad.dimension.x;
            game.pad.dimension.x += diff;
            
            for (Point2D.Double point: game.pad.points) {
                point.x += diff;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        game.ball.velocity = BALL_START_VELOCITY;
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

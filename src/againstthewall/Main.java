package againstthewall;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;

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
        
        public Game(final Sky sky, final Wall wall, final ArrayList<Color> rowColors, final Color brickEdgeColor,
                    final Stroke brickEdgeStroke, final Pad pad, final Ball ball, final Point2D.Double ballStartVelocity) {
            this.sky = sky;
            this.bricks = getBrickWall(wall, rowColors);
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
        double start = -Math.PI * Math.random();
        Paint ballColor = new GradientPaint((float)(-ballRadius/2), (float)(-ballRadius/2),
                                            new Color(0xff6961), (float)ballRadius, (float)ballRadius, new Color(0xfdfd96));
        Ball ball = new Ball(ballDimension, ballRadius, new Point2D.Double(0, 0), 0.1, ballColor);

        Point2D.Double ballStartVelocity = new Point2D.Double(0.01*Math.cos(start), 0.01*Math.sin(start));
        
        return new Game(sky, wall, rowColors, brickEdgeColor, brickEdgeStroke, pad, ball, ballStartVelocity);
    }
    
    public static final double RANGE = 0.0025;
    
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
    
    public static boolean EQ(double a, double b, double range) {
        return inRange(a, b - range/2, b + range/2);
    }
    
    public static boolean LSE(double a, double b, double range) {
        return inRange(a, b - range, b );
    }
    
    public static boolean GRE(double a, double b, double range) {
        return inRange(a, b, b + range);
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
        
        boolean isCollided = LSE(distance(ball.center, edge), ball.radius/2, RANGE);
        if (isCollided) {
            return collide(ball, edge);
        } else {
            return null;
        }
    }
    
    public static Point2D.Double checkAndCollideSide(final Ball ball, final Point2D.Double a, final Point2D.Double b) {
        
        if (a.y == b.y) {
            
            boolean isCollided = (EQ(distance(ball.center.y, a.y), ball.radius/2, RANGE)
                                  && inRange(ball.center.x, a.x, b.x));

            if (isCollided) {
                return new Point2D.Double(ball.velocity.x, -ball.velocity.y);
            } else {
                return null;
            }
            
        } else if (a.x == b.x) {
            
            boolean isCollided = (EQ(distance(ball.center.x, a.x), ball.radius/2, RANGE)
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
    
    public Point2D.Double checkAndCollidePolygon(Ball ball, ArrayList<Point2D.Double> points) {    
        
        for (int i = 0; i < points.size(); i++) {
            Point2D.Double result = checkAndCollideSide(ball, points.get(i), points.get((i + 1) % points.size()));
            if (result != null) {
                return result;
            }
        }
        
        for (Point2D.Double point: points) {
            Point2D.Double result = checkAndCollideEdge(ball, point);
            if (result != null) {
                return result;
            }
        } 
        
        return null;
    }
    
    public Point2D.Double checkAndCollideCircle(Ball ball, Circle circle) {
        
        double x = ball.center.x + ((ball.center.x-circle.center.x)*(ball.radius/(ball.radius + circle.radius)));
        double y = ball.center.y + ((ball.center.y-circle.center.y)*(ball.radius/(ball.radius + circle.radius)));
        Point2D.Double collisionPoint = new Point2D.Double(x, y);
        
        return checkAndCollideEdge(ball, collisionPoint);
    }
    
    public Point2D.Double checkAndCollidePad(Ball ball, Pad pad) {
        
        for (Circle edge: pad.edges) {
            Point2D.Double result = checkAndCollideCircle(ball, edge);
            if (result != null) {
                return result;
            }
        }
        
        return checkAndCollidePolygon(ball, pad.points);
    }

    private void passTime(Game game, double time) {
        
        if (game == null) 
            return;
        
        for (Star star: game.sky.stars) {
            star.position.x += star.velocity * time;
            if (star.position.x >= 1.0) {
                star.position.x = 0.0;
            }
        }
        
        if (intensity(game.ball.velocity) <= 0)
            return;
            
        Point2D.Double newBallCenter;  
        newBallCenter = add(game.ball.center, scale(game.ball.velocity, time));
        
        game.ball.ballCenterHistory.add(game.ball.center);
        if (game.ball.ballCenterHistory.size() > 100)
            game.ball.ballCenterHistory.pop();
        
        game.ball.center = newBallCenter;
        
        if (GRE(game.ball.center.y + game.ball.radius/2, 1, RANGE)) {
            game.gameOver = true;
            game.ball.velocity = new Point2D.Double(0, 0);
            return;
        }

        game.ball.angle += game.ball.rotationVelocity * time;
        game.ball.angle %= 2 * Math.PI;
             
        Point2D.Double result = checkAndCollidePolygon(game.ball, game.edge);
        if (result != null) {
            game.ball.velocity = result;
            return;
        }
        
        result = checkAndCollidePad(game.ball, game.pad);
        if(result != null) {
            game.ball.velocity = result;
            return;
        }

        for (Brick brick: game.bricks) {
            if (brick.isActive) {
                result = checkAndCollidePolygon(game.ball, brick.points);
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
                if (brick.dimension.width != 0 && brick.dimension.height != 0) {
                    brick.dimension.x += 0.001;
                    brick.dimension.y += 0.001;
                    brick.dimension.height -= 0.002;
                    brick.dimension.width -= 0.002;
                }
            }
        }
    }
    
    public static Game game = initGame();

    public static final AffineTransform ident = new AffineTransform();
    
    private static void resetDrawingArea(Graphics2D g2d, Dimension dim) {
        g2d.setTransform(ident);
        g2d.scale(dim.width, dim.height);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (game == null)
            return;

        Dimension dim = getSize();
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setPaint(game.sky.color);
        g2d.fillRect(0, 0, dim.width, dim.height);

        resetDrawingArea(g2d, dim);
        g2d.setPaint(game.sky.starColor);
        for (Star star : game.sky.stars) {
            resetDrawingArea(g2d, dim);
            g2d.translate(star.position.x, star.position.y);
            g2d.fill(game.sky.starShape);
        }

        if (!game.gameOver) {

            resetDrawingArea(g2d, dim);
            g2d.scale(2.0/dim.width, 2.0/dim.height);
            g2d.setPaint(Color.WHITE);
            g2d.drawString("Score: " + game.score, 0, g2d.getFontMetrics().getHeight());
            
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
            g2d.translate(game.pad.dimension.x - game.pad.edge.width/2, game.pad.dimension.y);
            g2d.fill(game.pad.edge);
            g2d.translate(game.pad.dimension.width, 0);
            g2d.fill(game.pad.edge);
    
            resetDrawingArea(g2d, dim);
            for (int i = 0; i < game.ball.ballCenterHistory.size() - 1; i++) {
                g2d.setStroke(new BasicStroke((float)(0.8*game.ball.radius)));
                g2d.setPaint(new Color(1.0f, 1.0f, 1.0f, (float)(0.10*i/game.ball.ballCenterHistory.size())));
                g2d.draw(new Line2D.Double(game.ball.ballCenterHistory.get(i), game.ball.ballCenterHistory.get(i+1)));
            }
            
            resetDrawingArea(g2d, dim);
            g2d.setPaint(game.ball.color);
            g2d.translate(game.ball.center.x, game.ball.center.y);
            g2d.rotate(game.ball.angle);
            g2d.fill(game.ball.dimension);         
            
        } else {
            
            resetDrawingArea(g2d, dim);
            g2d.translate(0.5, 0.5);
            g2d.scale(5.0/dim.width, 5.0/dim.height);
            g2d.setPaint(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int height = fm.getHeight();
            if (game.score < game.bricks.size()) {
                int gameOverWidth = fm.stringWidth("GAME OVER");
                int scoreWidth = fm.stringWidth("Your score: " + game.score);
                g2d.drawString("GAME OVER", -gameOverWidth/2, -height);
                g2d.drawString("Your score: " + game.score, -scoreWidth/2, 0);
            } else {
                int winWidth = fm.stringWidth("YOU WON");
                g2d.drawString("YOU WON", -winWidth/2, -height/2);
            }
        }
        
        resetDrawingArea(g2d, dim);
    }
    
    public void run() {
        long starttime;

        while (true) {
            starttime = System.currentTimeMillis();
            for(int i = 0; i < 10; i++) {
                passTime(game, 0.1);
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
        
        if (game != null) {
            if (inRange(event.getX(),
                        getSize().width * (game.pad.dimension.width/2 + game.pad.edge.width/2),
                        getSize().width * (1 - game.pad.dimension.width/2 - game.pad.edge.width/2))) {
                
                double diff = (double)event.getX() / getSize().width - (game.pad.dimension.x + game.pad.dimension.width/2);
                game.pad.dimension.x += diff;
                
                for (Circle circle: game.pad.edges) {
                    circle.center.x += diff;
                }
                
                for (Point2D.Double point: game.pad.points) {
                    point.x += diff;
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (game != null) {
            if (game.ball.velocity.equals(new Point2D.Double(0, 0)) && !game.gameOver) {
                game.ball.velocity = game.ballStartVelocity;
            } else {
                game = initGame();
            }
        }
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

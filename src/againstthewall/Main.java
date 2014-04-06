package againstthewall;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable, MouseMotionListener, MouseListener {

    public static class Brick {

        public final Point2D.Double pos;
        public final Color color;

        public Brick(Point2D.Double pos, Color color) {
            this.pos = pos;
            this.color = color;
        }
    }

    public static final Color SKY_COLOR = Color.BLACK;

    public static final int NUM_OF_STARS = 200;
    public static final Color STAR_COLOR = Color.WHITE;
    public static final BasicStroke STAR_STROKE = new BasicStroke(0);
    public static final Line2D.Double STAR_SHAPE = new Line2D.Double(0, 0, 0, 0);
    public static final Point2D.Double STARS[] = new Point2D.Double[NUM_OF_STARS];

    public static final int WALL_ROW = 5;
    public static final int WALL_COL = 10;
    public static final double WALL_START = 0.1;
    public static final double WALL_HEIGHT = 0.5;

    public static final int NUM_OF_BRICKS = WALL_COL * WALL_ROW;
    public static final double BRICK_WIDTH = 1.0 / WALL_COL;
    public static final double BRICK_HEIGHT = WALL_HEIGHT / WALL_ROW;
    public static final Rectangle2D.Double BRICK_SHAPE = new Rectangle2D.Double(0, 0, BRICK_WIDTH, BRICK_HEIGHT);
    public static final ArrayList<Brick> BRICKS = new ArrayList<>(NUM_OF_BRICKS);

    public static final Color PAD_COLOR = Color.WHITE;
    public static final double PAD_WIDTH = 0.1;
    public static final double PAD_HEIGHT = 1.0 / 40.0;
    public static final double PAD_START_X = 0.5 - PAD_WIDTH / 2;
    public static final double PAD_START_Y = 1 - PAD_HEIGHT;
    public static final Rectangle2D.Double PAD_SHAPE = new Rectangle2D.Double(0, 0, PAD_WIDTH, PAD_HEIGHT);

    public static final Color BALL_COLOR = Color.YELLOW;
    public static final double BALL_RADIUS = 1.0 / 40.0;
    public static final double BALL_START_X = 0.5 - BALL_RADIUS / 2;
    public static final double BALL_START_Y = 1 - PAD_HEIGHT - BALL_RADIUS;
    public static final Ellipse2D.Double BALL_SHAPE = new Ellipse2D.Double(0, 0, BALL_RADIUS, BALL_RADIUS);
    public static final double BALL_START_ANGLE = -Math.toRadians(10 + Math.random() * 160);
    public static final double BALL_VELOCITY = 0.015;

    public static final AffineTransform ident = new AffineTransform();

    static {

        for (int i = 0; i < NUM_OF_STARS; i++) {
            STARS[i] = new Point2D.Double(Math.random(), Math.random());
        }

        for (int i = 0; i < WALL_ROW; i++) {
            for (int j = 0; j < WALL_COL; j++) {
                Point2D.Double pos = new Point2D.Double(j * BRICK_WIDTH, WALL_START + i * BRICK_HEIGHT);
                Color color = new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
                BRICKS.add(new Brick(pos, color));
            }
        }
        ident.setToIdentity();
    }

    // mutable state
    private static double padX = PAD_START_X;
    private static Point2D.Double ballPos = new Point2D.Double(BALL_START_X, BALL_START_Y);
    private static double ballAngle = -Math.toRadians(10 + Math.random() * 160);
    private static double ballVelocity;

    private static void resetDrawingArea(Graphics2D g2d, Dimension dim) {
        g2d.setTransform(ident);
        g2d.scale(dim.width, dim.height);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dim = getSize();
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setPaint(SKY_COLOR);
        g2d.fillRect(0, 0, dim.width, dim.height);

        resetDrawingArea(g2d, dim);

        g2d.setPaint(STAR_COLOR);
        g2d.setStroke(STAR_STROKE);
        for (Point2D.Double star : STARS) {
            resetDrawingArea(g2d, dim);
            g2d.translate(star.x, star.y);
            g2d.draw(STAR_SHAPE);
        }

        for (Brick brick : BRICKS) {
            resetDrawingArea(g2d, dim);
            g2d.translate(brick.pos.x, brick.pos.y);
            g2d.setPaint(brick.color);
            g2d.fill(BRICK_SHAPE);
        }

        resetDrawingArea(g2d, dim);
        g2d.translate(padX, PAD_START_Y);
        g2d.setPaint(PAD_COLOR);
        g2d.fill(PAD_SHAPE);

        resetDrawingArea(g2d, dim);
        g2d.translate(ballPos.x, ballPos.y);
        g2d.setPaint(BALL_COLOR);
        g2d.fill(BALL_SHAPE);
        
        resetDrawingArea(g2d, dim);
    }
    
    private void passTime() {
        if (ballVelocity > 0) {
            ballPos.x += Math.cos(ballAngle) * ballVelocity;
            ballPos.y += Math.sin(ballAngle) * ballVelocity;
            
            if (ballPos.x <= 0 || ballPos.x + BALL_RADIUS >= 1) {
                ballAngle = Math.toRadians(-180) - ballAngle;
            }
            
            if (ballPos.y <= 0) {
                ballAngle = -ballAngle;
            }
            
            if ((ballPos.y + BALL_RADIUS + PAD_HEIGHT >= 1) && (ballPos.x + BALL_RADIUS / 2 >= padX) && (ballPos.x + BALL_RADIUS / 2 <= padX + PAD_WIDTH)) {
                ballAngle = -ballAngle;
            }
            
            if (ballPos.y >= 1) {
                ballVelocity = 0;
            }
        }
    }

    public void run() {
        long starttime;

        while (true) {
            starttime = System.currentTimeMillis();
            repaint();
            passTime();
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
        if (event.getX() >= 0 && event.getX() <= getSize().width * (1 - PAD_WIDTH)) {
            padX = (double)event.getX() / getSize().width;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ballVelocity = BALL_VELOCITY;
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

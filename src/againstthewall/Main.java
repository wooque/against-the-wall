package againstthewall;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

import againstthewall.GameData.*;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable, MouseMotionListener, MouseListener {
    
    public static final double RANGE = 0.0025;
    
    public static void passTime(Game game, double time) {
        
        if (game == null) 
            return;
        
        for (Star star: game.sky.stars) {
            star.position.x += star.velocity * time;
            if (star.position.x >= 1.0) {
                star.position.x = 0.0;
            }
        }
        
        if (Vec.intensity(game.ball.velocity) <= 0)
            return;
            
        Point2D.Double newBallCenter;  
        newBallCenter = Vec.add(game.ball.center, Vec.scale(game.ball.velocity, time));

        game.ball.ballCenterHistory.add(game.ball.center);
        if (game.ball.ballCenterHistory.size() > 100)
            game.ball.ballCenterHistory.pop();
        
        game.ball.center = newBallCenter;
        
        if (Range.GRE(game.ball.center.y + game.ball.radius/2, 1, RANGE)) {
            game.gameOver = true;
            game.ball.velocity = new Point2D.Double(0, 0);
            return;
        }

        game.ball.angle += game.ball.rotationVelocity * time;
        game.ball.angle %= 2 * Math.PI;
             
        Point2D.Double result = Collision.checkAndCollidePolygon(game.ball, game.edge, RANGE);
        if (result != null) {
            game.ball.velocity = result;
            return;
        }
        
        result = GameUtil.checkAndCollidePad(game.ball, game.pad, RANGE);
        if(result != null) {
            game.ball.velocity = result;
            return;
        }

        for (Brick brick: game.bricks) {
            if (brick.isActive) {
                result = Collision.checkAndCollidePolygon(game.ball, brick.points, RANGE);
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
    
    public static Game game = GameUtil.initGame();

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
            if (Range.inRange(event.getX(),
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
                game = GameUtil.initGame();
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

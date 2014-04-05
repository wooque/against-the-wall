package againstthewall;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable, MouseMotionListener {
    
    boolean init;
    Point2D.Double stars[];
    Rectangle2D.Double bricks[];
    Color bricksColors[];
    Dimension dim;
    Rectangle2D.Double pad;
    
    public void init() {
        if (!init) {
            dim = getSize();
            stars = new Point2D.Double[100];
            for (int i = 0; i < 100; i++) {
                stars[i] = new Point2D.Double(Math.random() * dim.width, Math.random() * dim.height);
            }

            double wallLine = dim.height * 0.1;
            double brickWidth = dim.width / 10;
            double brickHeigth = dim.height * 0.1;
            
            bricks = new Rectangle2D.Double[40];
            bricksColors = new Color[40];
            for (int i = 0; i < 4; i++) {
                
                for (int j = 0; j < 10; j++) {
                    bricks[i * 10 + j] = new Rectangle2D.Double(j * (dim.width / 10), wallLine, brickWidth, brickHeigth);
                    bricksColors[i * 10 + j] = new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
                }
                
                wallLine += dim.height * 0.1;
            }
            
            pad = new Rectangle2D.Double(dim.width * 0.45, (39.0 / 40.0) * dim.height, dim.width / 10, dim.height / 40);
            
            init = true;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dim = getSize();
        Graphics2D g2d = (Graphics2D)g;
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, dim.width, dim.height);
        if (init) {
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 100; i++) {
                g2d.fillRect((int)stars[i].x, (int)stars[i].y, 1, 1);
            }
            
            for (int i = 0; i < 40; i++) {
                g2d.setPaint(bricksColors[i]);
                g2d.fill(bricks[i]);
            }
            
            g2d.setColor(Color.WHITE);
            synchronized (pad) {
                g2d.fill(pad);
            }
        }
    }
    
    public void run() {
        long starttime;

        while (true) {
            starttime = System.currentTimeMillis();
            try {
                repaint();
                starttime += 40;
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
    public void mouseMoved(MouseEvent event) {
        synchronized (pad) {
            if (event.getX() >= 0 && event.getX() <= getSize().width * 0.9)
                pad.x = event.getX();
        }
    }

    public static void createAndShowGUI() {
        
        JFrame frame = new JFrame("Against the Wall");
        frame.setSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Main panel = new Main();
        panel.addMouseMotionListener(panel);
        panel.setOpaque(true);
        frame.add(panel);
        
        frame.setVisible(true);
        
        panel.init();
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

package againstthewall;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable {
    
    boolean init;
    Point2D.Double stars[];
    Rectangle2D.Double bricks[];
    Dimension dim;
    
    public void init() {
        if (!init) {
            dim = getSize();
            stars = new Point2D.Double[100];
            for (int i = 0; i < 100; i++) {
                stars[i] = new Point2D.Double(Math.random() * dim.width, Math.random() * dim.height);
            }
            
            double wallBegin = dim.height * 0.2;
            double wallEnd = dim.height * 0.5;
            
            bricks = new Rectangle2D.Double[40];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 10; j++) {
                    bricks[i * 10 + j] = new Rectangle2D.Double();
                }
            }
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

    public static void createAndShowGUI() {
        
        JFrame frame = new JFrame("Against the Wall");
        frame.setSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Main panel = new Main();
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

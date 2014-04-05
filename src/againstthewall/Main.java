package againstthewall;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable {

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

	public static void main(String args[]) {
		Main panel = new Main();
		panel.setOpaque(true);

		JFrame frame = new JFrame("Against the Wall");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.getContentPane().add(panel);
		frame.setSize(new Dimension(800, 600));
		frame.setVisible(true);
		// frame.addKeyListener(panel);
		// frame.addMouseWheelListener(panel);

		Thread t = new Thread(panel);
		t.start();
	}
}

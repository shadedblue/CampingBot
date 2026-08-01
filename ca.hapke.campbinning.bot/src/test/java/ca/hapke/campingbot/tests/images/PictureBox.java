package ca.hapke.campingbot.tests.images;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import ca.hapke.campingbot.util.ImageCache;
import ca.hapke.campingbot.util.Sprite;


/*-
 * To use this class, Create a JLabel on your JFrame, then change your 
 * JLabel name = new JLabel("text"); 
 * to
 * PictureBox name = new PictureBox(this, "folder", "filename.jpg"); 
 * 
 * @author Mr. Hapke
 */
public class PictureBox extends JLabel {

	private class PictureBoxRenderer extends Thread {
		private boolean finished = false;
		private boolean alive = true;

		private PictureBoxRenderer() {
			super("PictureBoxRenderer: " + PictureBox.this.hashCode());

			if (DEBUG)
				System.out.println("Renderer created");
		}

		@Override
		public void run() {
			if (DEBUG)
				System.out.println("Renderer run");
			startTime = System.currentTimeMillis();
			long delay = 1000 / 30;
			while (alive) {
				if (s != null) {
					long currentTime = System.currentTimeMillis();
					currentFrame = s.getFrame(currentTime);

					repaint();
				}
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
					alive = false;
				}
			}
			finished = true;
			if (DEBUG)
				System.out.println("Renderer finished");
		}
	}

	private static final long serialVersionUID = 1381812448276589893L;

	private static final boolean DEBUG = false;

	private Sprite s;
	private Image currentFrame;
	private ImageCache cache = ImageCache.getInstance();
	private int frameNumber;

	private PictureBoxRenderer t;
	protected long startTime;
	private int boxWidth;
	private int boxHeight;
	
	private BasicStroke borderStroke;
	private Color borderColor;


	public PictureBox(JFrame frame, String folder, String filename) {
		setImage(folder, filename);
		startRenderer();
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				startRenderer();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				stopRenderer();
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				startRenderer();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				stopRenderer();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				stopRenderer();
			}

			@Override
			public void windowActivated(WindowEvent e) {
				startRenderer();
			}
		});
	}

	private void startRenderer() {
		if (t == null || t.finished) {
			t = new PictureBoxRenderer();
			t.start();
		}
	}

	private void stopRenderer() {
		if (t != null)
			t.alive = false;
	}

	public void setImage(String folder, String filename) {
		try {
			s = cache.getImage(folder, filename);
			if (s == null)
				return;
			s.scale(boxWidth, boxHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void setImage(Sprite sprite) {
		try {
			s = sprite;
			if (s == null)
				return;
			s.scale(boxWidth, boxHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void scaleImage() {
		boxWidth = getWidth();
		boxHeight = getHeight();
		if (s != null) {
			s.scale(boxWidth, boxHeight);
		}
	}

	@Override
	protected void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);
		gfx.setColor(getBackground());
		gfx.fillRect(0, 0, getWidth(), getHeight());
		if (currentFrame != null) {
			gfx.drawImage(currentFrame, 0, 0, null);
			if (borderStroke != null && borderColor != null) {
				gfx.setColor(borderColor);
				if (gfx instanceof Graphics2D) {
					Graphics2D g2 = (Graphics2D) gfx;
					g2.setStroke(borderStroke);
				}
				int w = currentFrame.getWidth(null);
				int h = currentFrame.getHeight(null);
				gfx.drawRect(0, 0, w, h);
			}
		}

		if (DEBUG) {
			gfx.setColor(Color.black);
			gfx.drawString("Frame #" + frameNumber++, 10, 10);
		}
	}

	public void setCustomBorder(int borderWidth, Color c) {
		this.borderColor = c;
		this.borderStroke = new BasicStroke(borderWidth);
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		scaleImage();
	}

	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		scaleImage();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		scaleImage();
	}

	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		scaleImage();
	}

	@Override
	public String toString() {
		return "PictureBox [" + s.toString() + "]]";
	}

}

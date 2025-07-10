package ca.hapke.campingbot.tests.images;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import ca.hapke.campingbot.util.ImageCache;
import ca.hapke.campingbot.util.Sprite;

/**
 * @author Mr. Hapke
 */
public class FrmPictureBoxTest extends JFrame {

	private static final long serialVersionUID = 8643869947757940158L;
	private static final String FOLDER = "assets";
	private String[] FOLDERS = new String[] { FOLDER };
	private String[] IMAGES = new String[] { "andrew.png" };
	private int i = 0;
	private PictureBox picTest;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					FrmPictureBoxTest frame = new FrmPictureBoxTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrmPictureBoxTest() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 381, 457);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0 };
		gbl_contentPane.rowHeights = new int[] { 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0 };
		gbl_contentPane.rowWeights = new double[] { 1.0 };
		contentPane.setLayout(gbl_contentPane);

		String folder = FOLDERS[0];
		String file = IMAGES[0];
		picTest = new PictureBox(this, folder, file);
		Sprite originalImg = ImageCache.getInstance().getImage(folder, file);
		picTest.setBorder(new LineBorder(new Color(0, 0, 0)));
		picTest.setCustomBorder(5, Color.cyan);
		GridBagConstraints gbc_picTest = new GridBagConstraints();
		gbc_picTest.anchor = GridBagConstraints.NORTH;
		gbc_picTest.weighty = 1.0;
		gbc_picTest.weightx = 1.0;
		gbc_picTest.fill = GridBagConstraints.BOTH;
		gbc_picTest.insets = new Insets(0, 0, 5, 0);
		gbc_picTest.gridx = 0;
		gbc_picTest.gridy = 0;
		contentPane.add(picTest, gbc_picTest);

		File outImg = File.createTempFile("silly-image", ".gif");
		overlayAlwaysSunny(originalImg, spriteSet, outImg);

	}
}

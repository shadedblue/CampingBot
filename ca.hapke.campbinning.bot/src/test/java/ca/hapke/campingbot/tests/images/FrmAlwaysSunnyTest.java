package ca.hapke.campingbot.tests.images;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.commands.overlays.AlwaysSunnyOverlaySet;
import ca.hapke.campingbot.commands.overlays.OverlayAlwaysSunnyCommand;
import ca.hapke.campingbot.util.ImageCache;
import ca.hapke.campingbot.util.Sprite;

/**
 * @author Mr. Hapke
 */
public class FrmAlwaysSunnyTest extends JFrame {

	private static final long serialVersionUID = 8643869947757940158L;
	private static final String FOLDER = "assets";
	private static final String FILENAME = "andrew.png";
	private PictureBox picTest;
	private File resultFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					FrmAlwaysSunnyTest frame = new FrmAlwaysSunnyTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FrmAlwaysSunnyTest() throws Exception {
		CampingSystem.getInstance().setAssetsFolder(FOLDER);

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

		picTest = new PictureBox(this, FOLDER, FILENAME);
		Sprite originalSprite = ImageCache.getInstance().getImage(FOLDER, FILENAME);
		Image originalImg = originalSprite.getFirstFrame();
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

		resultFile = File.createTempFile("silly-image", ".gif");
		System.out.println("Using Temp file: " + resultFile.toString());
		resultFile.deleteOnExit();
		AlwaysSunnyOverlaySet overlaySet = OverlayAlwaysSunnyCommand.PEPE_SILVIE_SET;
		OverlayAlwaysSunnyCommand.overlayAlwaysSunny(originalImg, overlaySet, resultFile);
		String resultKey = "sunny-overlayed";
		Sprite resultSprite = ImageCache.loadGif(resultKey, resultFile);
		picTest.setImage(resultSprite);
	}
}

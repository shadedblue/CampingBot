package ca.hapke.campbinning.bot.ui;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSystem;
import ca.hapke.campbinning.bot.CampingXmlSerializer;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.channels.CampingChat;
import ca.hapke.campbinning.bot.channels.CampingChatManager;
import ca.hapke.campbinning.bot.interval.CampingIntervalThread;
import ca.hapke.campbinning.bot.interval.ExecutionTimeTracker;
import ca.hapke.campbinning.bot.interval.IntervalByExecutionTime;
import ca.hapke.campbinning.bot.interval.IntervalBySeconds;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserDefaultComparator;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;
import ca.odell.glazedlists.swing.DefaultEventListModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;

/**
 * @author Nathan Hapke
 */
public class CampingBotUi extends JFrame {

	private class UiTableRefresher implements IntervalBySeconds {
		@Override
		public int getSeconds() {
			return 10;
		}

		@Override
		public void doWork() {
			userModel.fireTableDataChanged();
			secModel.fireTableDataChanged();
			timeModel.fireTableDataChanged();
		}

		@Override
		public boolean shouldRun() {
			return isVisible();
		}
	}

	private class UiDailyEvent implements IntervalByExecutionTime {
		SimpleDateFormat df = new SimpleDateFormat("EEEE MMMM d k");
		private long nextExecutionTime = 0;

		@Override
		public long getNextExecutionTime() {
			return nextExecutionTime;
		}

		@Override
		public void doWork() {
			String dayOfWeek = df.format(new Date());
			eventLogger.add(new EventItem(dayOfWeek));
		}

		@Override
		public boolean shouldRun() {
			return true;
		}

		@Override
		public void generateNextExecTime() {
			Instant next = Instant.now();
			next = next.plus(Duration.ofHours(1));
			next = next.truncatedTo(ChronoUnit.HOURS);
			nextExecutionTime = next.toEpochMilli();
		}
	}

	private static final String CAMPING_BOT = "Camping Bot";
	private static final long serialVersionUID = -4742415703187806424L;
	private JPanel contentPane;

	private JTable tblUsers;
	private DefaultEventTableModel<CampingUser> userModel;
	private JTable tblSeconds;
	private DefaultEventTableModel<ExecutionTimeTracker<IntervalBySeconds>> secModel;
	private JTable tblTime;
	private DefaultEventTableModel<IntervalByExecutionTime> timeModel;

	private EventLogger eventLogger = EventLogger.getInstance();
	private CampingBot bot;
	private JScrollPane sclUsers;
	private JLabel lblStatus;
	private JButton btnConnect;
	private JTextField txtChat;
	private JList<CampingChat> lstChats;
	private JScrollPane sclChats;
	private JLabel lblChats;
	private JTextArea txtCategoryValue;
	private JComboBox<String> cmbCategories;
	private Map<String, HasCategories> categoriesMap = new HashMap<>();
	private Map<String, String> categoryMap = new HashMap<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					CampingBotUi frame = new CampingBotUi();
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
	public CampingBotUi() {
		ApiContextInitializer.init();
		bot = new CampingBot();

		setTitle(CAMPING_BOT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 903, 654);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		///

		TableFormatCampingUser usersFormat = new TableFormatCampingUser();
		EventList<CampingUser> usersEvents = CampingUserMonitor.getInstance().getUsers();
		SortedList<CampingUser> usersSorted = new SortedList<>(usersEvents, new CampingUserDefaultComparator());
		userModel = new DefaultEventTableModel<CampingUser>(GlazedListsSwing.swingThreadProxyList(usersSorted),
				usersFormat);

		sclUsers = new JScrollPane();
		sclUsers.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclUsers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sclUsers.setBounds(10, 103, 861, 217);
		contentPane.add(sclUsers);
		tblUsers = new JTable(userModel);
		sclUsers.setViewportView(tblUsers);
		TableComparatorChooser.install(tblUsers, usersSorted, TableComparatorChooser.SINGLE_COLUMN);

		NumberCellRenderer numberRenderer = new NumberCellRenderer();
		PrettyTimeCellRenderer timeRenderer = new PrettyTimeCellRenderer();
		TableColumnModel userColumnModel = tblUsers.getColumnModel();
		for (int i = 6; i <= 10; i++) {
			userColumnModel.getColumn(i).setCellRenderer(numberRenderer);
		}
		userColumnModel.getColumn(11).setCellRenderer(timeRenderer);
		usersFormat.setTableWidths(userColumnModel);
		tblUsers.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		///

		CampingIntervalThread intervalThread = CampingIntervalThread.getInstance();

		TableFormatSecondsExecutionTimeTracker secondsFormat = new TableFormatSecondsExecutionTimeTracker();
		EventList<ExecutionTimeTracker<IntervalBySeconds>> bySecs = intervalThread.getRegularBySeconds();
		secModel = new DefaultEventTableModel<>(GlazedListsSwing.swingThreadProxyList(bySecs), secondsFormat);

		JScrollPane sclSec = new JScrollPane();
		sclSec.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclSec.setBounds(683, 363, 188, 98);
		contentPane.add(sclSec);

		tblSeconds = new JTable(secModel);
		sclSec.setViewportView(tblSeconds);

		TableColumnModel secColumnModel = tblSeconds.getColumnModel();
		secColumnModel.getColumn(2).setCellRenderer(timeRenderer);
		secondsFormat.setTableWidths(secColumnModel);

		///

		TableFormatTimeExecutionTimeTracker timeFormat = new TableFormatTimeExecutionTimeTracker();
		EventList<IntervalByExecutionTime> byTime = intervalThread.getRegularByExecutionTime();
		timeModel = new DefaultEventTableModel<>(GlazedListsSwing.swingThreadProxyList(byTime), timeFormat);

		JScrollPane sclTime = new JScrollPane();
		sclTime.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclTime.setBounds(683, 500, 188, 98);
		contentPane.add(sclTime);

		tblTime = new JTable(timeModel);
		sclTime.setViewportView(tblTime);

		TableColumnModel timeColumnModel = tblTime.getColumnModel();
		timeColumnModel.getColumn(1).setCellRenderer(timeRenderer);
		timeFormat.setTableWidths(timeColumnModel);
		///

		JLabel lblStatusL = new JLabel("Status:");
		lblStatusL.setHorizontalAlignment(SwingConstants.TRAILING);
		lblStatusL.setBounds(10, 11, 64, 20);
		contentPane.add(lblStatusL);

		lblStatus = new JLabel("Offline");
		lblStatus.setBounds(84, 10, 46, 23);
		contentPane.add(lblStatus);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CampingSystem system = CampingSystem.getInstance();
				String u = system.getBotUsername();
				String token = system.getToken();

				if (u == null || token == null) {
					JOptionPane.showMessageDialog(CampingBotUi.this,
							"Cannot connect without the bot's username and token", "Failure",
							JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						TelegramBotsApi api = new TelegramBotsApi();
						api.registerBot(bot);
						lblStatus.setText("Online");
						btnConnect.setEnabled(false);
					} catch (TelegramApiRequestException ex) {
						ex.printStackTrace();
						lblStatusL.setText("Connect Failed");
					}
				}
			}
		});
		btnConnect.setBounds(10, 31, 120, 23);
		contentPane.add(btnConnect);
		CampingChatManager chatMgr = CampingChatManager.getInstance();
		DefaultEventComboBoxModel<CampingChat> chatModel = new DefaultEventComboBoxModel<>(
				GlazedListsSwing.swingThreadProxyList(chatMgr.getChatList()));

		sclChats = new JScrollPane();
		sclChats.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclChats.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sclChats.setBounds(140, 33, 419, 59);
		contentPane.add(sclChats);
		lstChats = new JList<CampingChat>();
		sclChats.setViewportView(lstChats);
		lstChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstChats.setModel(chatModel);

		ActionListener sendChatListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chat();
			}
		};
		txtChat = new JTextField();
		txtChat.addActionListener(sendChatListener);
		txtChat.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtChat.setBounds(190, 5, 270, 26);
		contentPane.add(txtChat);
		txtChat.setColumns(10);

		JButton btnSay = new JButton("Say");
		btnSay.addActionListener(sendChatListener);
		btnSay.setBounds(465, 5, 94, 26);
		contentPane.add(btnSay);

		lblChats = new JLabel("Chat");
		lblChats.setBounds(140, 11, 46, 17);
		contentPane.add(lblChats);

		JScrollPane sclLog = new JScrollPane();
		sclLog.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sclLog.setBounds(10, 362, 663, 244);
		contentPane.add(sclLog);

		EventList<EventItem> recentLog = eventLogger.getUiLog();
		DefaultEventListModel<EventItem> logModel = new DefaultEventListModel<>(
				GlazedListsSwing.swingThreadProxyList(recentLog));

		JList<EventItem> lstLog = new JList<>();
		sclLog.setViewportView(lstLog);
		lstLog.setModel(logModel);
		lstLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblLog = new JLabel("Log");
		lblLog.setBounds(10, 331, 120, 20);
		contentPane.add(lblLog);

		JButton btnLoadMore = new JButton("Load More");
		btnLoadMore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnLoadMore.setBounds(53, 331, 123, 23);
		contentPane.add(btnLoadMore);

		JLabel lblBySeconds = new JLabel("By Seconds");
		lblBySeconds.setBounds(683, 334, 123, 14);
		contentPane.add(lblBySeconds);

		JLabel lblByExecutionTime = new JLabel("By Execution Time");
		lblByExecutionTime.setBounds(683, 470, 123, 20);
		contentPane.add(lblByExecutionTime);

		JLabel lblNewLabel = new JLabel("Category");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel.setBounds(569, 5, 61, 20);
		contentPane.add(lblNewLabel);

		cmbCategories = new JComboBox<String>();
		HasCategories[] hasCategoriess = bot.getCategories();
		Vector<String> categoriesList = new Vector<>();
		for (int i = 0; i < hasCategoriess.length; i++) {
			HasCategories hasCategories = hasCategoriess[i];
			String first = hasCategories.getContainerName();
			List<String> categories = hasCategories.getCategoryNames();
			for (String category : categories) {
				String categoryCapitalized = Character.toUpperCase(category.charAt(0)) + category.substring(1).toLowerCase();
				String display = first + " :: " + categoryCapitalized;
				categoriesList.add(display);
				categoriesMap.put(display, hasCategories);
				categoryMap.put(display, category);
			}
		}
		ComboBoxModel<String> aModel = new DefaultComboBoxModel<String>(categoriesList);
		cmbCategories.setModel(aModel);
		cmbCategories.setBounds(635, 5, 160, 20);
		contentPane.add(cmbCategories);

		txtCategoryValue = new JTextArea();
		txtCategoryValue.setBounds(569, 31, 302, 61);
		txtCategoryValue.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(txtCategoryValue);

		JButton btnAddToCategory = new JButton("Add");
		btnAddToCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object s = cmbCategories.getSelectedItem();
				if (s != null) {
					HasCategories categories = categoriesMap.get(s);
					String category = categoryMap.get(s);
					if (categories == null || category == null)
						return;

					String value = txtCategoryValue.getText();
					categories.addItem(category, value);
					txtCategoryValue.setText("");
				}
			}
		});
		btnAddToCategory.setBounds(800, 5, 70, 23);
		contentPane.add(btnAddToCategory);

		Image app = null;
		try {
			File f = CampingXmlSerializer.getFileNotInBinFolder("assets/app.png");
			URL appUrl = f.toURI().toURL();
			ImageIcon appIcon = new ImageIcon(appUrl, "app-icon");
			app = appIcon.getImage();
			setIconImage(app);
		} catch (Exception e3) {
		}
		if (SystemTray.isSupported()) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e2) {
			}
			SystemTray tray = SystemTray.getSystemTray();
			try {
				File fire = CampingXmlSerializer.getFileNotInBinFolder("assets/fire3.png");
				URL fireUrl = fire.toURI().toURL();
				ImageIcon fireIcon = new ImageIcon(fireUrl, "tray-icon");
				Image fireImg = fireIcon.getImage();
				TrayIcon trayIcon = new TrayIcon(fireImg, CAMPING_BOT);
				trayIcon.setImageAutoSize(true);
				trayIcon.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						CampingBotUi.this.setVisible(true);

						CampingBotUi.this.setExtendedState(JFrame.NORMAL);
					}
				});
				tray.add(trayIcon);
				addWindowStateListener(new WindowStateListener() {
					@Override
					public void windowStateChanged(WindowEvent e) {
						if (e.getNewState() == ICONIFIED) {
							setVisible(false);
						}
					}
				});
			} catch (Exception e1) {
			}
		}
		CampingIntervalThread.put(new UiTableRefresher());
		CampingIntervalThread.put(new UiDailyEvent());
	}

	public void chat() {
		CampingChat chat = lstChats.getSelectedValue();
		String msg = txtChat.getText().trim();
		if (chat != null && msg.length() > 0) {
			bot.sendMsg(chat.chatId, msg);
			txtChat.setText("");
		}
	}
}

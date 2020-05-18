package ca.hapke.campbinning.bot.ui;

import java.awt.Color;
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
import java.time.temporal.ChronoUnit;
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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.calendaring.monitor.TimerThreadWithKill;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSystem;
import ca.hapke.campbinning.bot.CampingXmlSerializer;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.channels.CampingChat;
import ca.hapke.campbinning.bot.channels.CampingChatManager;
import ca.hapke.campbinning.bot.commands.response.SendResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
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

	private class UiTableRefresher implements CalendaredEvent<Void> {

		private TimesProvider<Void> times = new TimesProvider<Void>(
				new ByFrequency<Void>(null, 10, ChronoUnit.SECONDS));

		@Override
		public void doWork(Void value) {
			userModel.fireTableDataChanged();
			calendaredModel.fireTableDataChanged();
		}

		@Override
		public boolean shouldRun() {
			return isVisible();
		}

		@Override
		public TimesProvider<Void> getTimeProvider() {
			return times;
		}

		@Override
		public StartupMode getStartupMode() {
			return StartupMode.Never;
		}
	}

	private static final String CAMPING_BOT = "Camping Bot";
	private static final long serialVersionUID = -4742415703187806424L;
	private JPanel contentPane;

	private JTable tblUsers;
	private DefaultEventTableModel<CampingUser> userModel;
	private JTable tblSeconds;
	@SuppressWarnings("rawtypes")
	private DefaultEventTableModel<CalendaredEvent> calendaredModel;

	private EventLogger eventLogger = EventLogger.getInstance();
	private CampingBot bot;
	private JScrollPane sclUsers;
	private JLabel lblStatus;
	private JButton btnConnect;
	private JTextField txtChat;
	private JList<CampingChat> lstChats;
	private JScrollPane sclChats;
	private JTextArea txtCategoryValue;
	private JComboBox<String> cmbCategories;
	private Map<String, HasCategories> categoriesMap = new HashMap<>();
	private Map<String, String> categoryMap = new HashMap<>();
	private TrayIcon trayIcon;

	private StatusUpdate statusUpdater = new StatusUpdate();
	private JScrollPane scrollPane;

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
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				TimerThreadWithKill.shutdownThreads();
			}
		});
		setBounds(100, 100, 1284, 654);
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
		sclUsers.setBounds(20, 103, 841, 217);
		contentPane.add(sclUsers);
		tblUsers = new JTable(userModel);
		sclUsers.setViewportView(tblUsers);
		TableComparatorChooser.install(tblUsers, usersSorted, TableComparatorChooser.SINGLE_COLUMN);

//		NumberCellRenderer numberRenderer = new NumberCellRenderer();
		PrettyTimeCellRenderer timeRenderer = new PrettyTimeCellRenderer();
		TableColumnModel userColumnModel = tblUsers.getColumnModel();
//		for (int i = 6; i <= 10; i++) {
//			userColumnModel.getColumn(i).setCellRenderer(numberRenderer);
//		}
		userColumnModel.getColumn(7).setCellRenderer(timeRenderer);
		usersFormat.setTableWidths(userColumnModel);
		tblUsers.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		///

//		CampingIntervalThread intervalThread = CampingIntervalThread.getInstance();
		CalendarMonitor intervalThread = CalendarMonitor.getInstance();

		TableFormatCalendaredEvent calendaredFormat = new TableFormatCalendaredEvent();
		@SuppressWarnings("rawtypes")
		EventList<CalendaredEvent> byCalendared = intervalThread.getEvents();
		calendaredModel = new DefaultEventTableModel<CalendaredEvent>(
				GlazedListsSwing.swingThreadProxyList(byCalendared), calendaredFormat);

		JScrollPane sclSec = new JScrollPane();
		sclSec.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclSec.setBounds(882, 103, 376, 217);
		contentPane.add(sclSec);

		tblSeconds = new JTable(calendaredModel);
		sclSec.setViewportView(tblSeconds);

		TableColumnModel secColumnModel = tblSeconds.getColumnModel();
		secColumnModel.getColumn(1).setCellRenderer(timeRenderer);
		secColumnModel.getColumn(2).setCellRenderer(timeRenderer);
		calendaredFormat.setTableWidths(secColumnModel);

		///

		lblStatus = new JLabel("Offline");
		lblStatus.setVerticalAlignment(SwingConstants.TOP);
		lblStatus.setBounds(22, 36, 116, 54);
		contentPane.add(lblStatus);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CampingSystem.getInstance().canConnect()) {
					bot.addStatusUpdate(statusUpdater);
					bot.connect();
				} else {
					JOptionPane.showMessageDialog(CampingBotUi.this,
							"Cannot connect without the bot's username and token", "Failure",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnConnect.setBounds(20, 7, 120, 23);
		contentPane.add(btnConnect);
		CampingChatManager chatMgr = CampingChatManager.getInstance(bot);
		DefaultEventComboBoxModel<CampingChat> chatModel = new DefaultEventComboBoxModel<>(
				GlazedListsSwing.swingThreadProxyList(chatMgr.getChatList()));

		sclChats = new JScrollPane();
		sclChats.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclChats.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sclChats.setBounds(161, 33, 474, 59);
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
		txtChat.setBounds(161, 5, 370, 26);
		contentPane.add(txtChat);
		txtChat.setColumns(10);

		JButton btnSay = new JButton("Say");
		btnSay.addActionListener(sendChatListener);
		btnSay.setBounds(541, 5, 94, 26);
		contentPane.add(btnSay);

		JLabel lblChats = new CategoryLabel("Chat", Color.cyan);
		lblChats.setHorizontalAlignment(SwingConstants.CENTER);
		lblChats.setBounds(140, 7, 19, 85);
		contentPane.add(lblChats);

		JScrollPane sclLog = new JScrollPane();
		sclLog.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sclLog.setBounds(20, 331, 1238, 275);
		contentPane.add(sclLog);

		EventList<EventItem> recentLog = eventLogger.getUiLog();
		DefaultEventListModel<EventItem> logModel = new DefaultEventListModel<>(
				GlazedListsSwing.swingThreadProxyList(recentLog));

		JList<EventItem> lstLog = new JList<>();
		sclLog.setViewportView(lstLog);
		lstLog.setModel(logModel);
		lstLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		CategoryLabel lblLog = new CategoryLabel("Log", Color.MAGENTA);
		lblLog.setBounds(0, 331, 19, 275);
		contentPane.add(lblLog);

		CategoryLabel lblBySeconds = new CategoryLabel("Calendared", Color.green);
		lblBySeconds.setBounds(859, 103, 22, 217);
		contentPane.add(lblBySeconds);

		CategoryLabel lblCategory = new CategoryLabel("Categories", Color.orange);
		lblCategory.setBounds(640, 7, 19, 85);
		contentPane.add(lblCategory);

		cmbCategories = new JComboBox<String>();
		List<HasCategories<String>> hasCategoriess = bot.getCategories();
		Vector<String> categoriesList = new Vector<>();
		for (int i = 0; i < hasCategoriess.size(); i++) {
			HasCategories<String> hasCategories = hasCategoriess.get(i);
			String first = hasCategories.getContainerName();
			List<String> categories = hasCategories.getCategoryNames();
			for (String category : categories) {
				String categoryCapitalized = Character.toUpperCase(category.charAt(0))
						+ category.substring(1).toLowerCase();
				String display = first + " :: " + categoryCapitalized;
				categoriesList.add(display);
				categoriesMap.put(display, hasCategories);
				categoryMap.put(display, category);
			}
		}
		ComboBoxModel<String> aModel = new DefaultComboBoxModel<String>(categoriesList);
		cmbCategories.setModel(aModel);
		cmbCategories.setBounds(660, 7, 525, 20);
		contentPane.add(cmbCategories);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(660, 31, 598, 61);
		contentPane.add(scrollPane);

		txtCategoryValue = new JTextArea();
		scrollPane.setViewportView(txtCategoryValue);
		txtCategoryValue.setLineWrap(true);
		txtCategoryValue.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JButton btnAddToCategory = new JButton("Add");
		btnAddToCategory.addActionListener(new ActionListener() {
			@Override
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
		btnAddToCategory.setBounds(1188, 7, 70, 23);
		contentPane.add(btnAddToCategory);

		CategoryLabel lblUsers = new CategoryLabel("Users", Color.blue);
		lblUsers.setBounds(0, 103, 19, 217);
		contentPane.add(lblUsers);

		CategoryLabel lblConnection = new CategoryLabel("Connection", Color.red);
		lblConnection.setBounds(0, 7, 19, 85);
		contentPane.add(lblConnection);

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
				trayIcon = new TrayIcon(fireImg, CAMPING_BOT);
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
		intervalThread.add(new UiTableRefresher());
		statusUpdater.statusOffline();
		statusUpdater.updateUserInfo(bot.getBotUsername(), -1);
	}

	public void chat() {
		CampingChat chat = lstChats.getSelectedValue();
		String msg = txtChat.getText().trim();
		if (chat != null && msg.length() > 0) {
			try {
				TextCommandResult cmd = new TextCommandResult(BotCommand.Talk, new TextFragment(msg));
				SendResult result = cmd.send(bot, chat.chatId);
				Message outgoingMsg = result.outgoingMsg;
				EventItem ei = new EventItem(BotCommand.Talk, bot.getMeCamping(), outgoingMsg.getDate(), chat,
						outgoingMsg.getMessageId(), msg, null);
				EventLogger.getInstance().add(ei);
			} catch (TelegramApiException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
			txtChat.setText("");
		}
	}

	private class StatusUpdate implements IStatus {
		private static final String ONLINE = "Online";
		private static final String OFFLINE = "Offline";
		private String connected = OFFLINE;
		private String username;
		private int id;

		private void updateUserInfo(String inUser, int inTId) {
			if (inUser != null)
				username = inUser;
			if (inTId >= 1)
				id = inTId;
			statusChanged();
		}

		@Override
		public void statusOffline() {
			btnConnect.setEnabled(true);
			updateUserInfo("", -1);
			connected = OFFLINE;
			statusChanged();
		}

		@Override
		public void statusOnline() {
			btnConnect.setEnabled(false);
			connected = ONLINE;
			statusChanged();
		}

		@Override
		public void statusMeProvided(CampingUser me) {
			if (me != null) {
				updateUserInfo(me.getUsername(), me.getTelegramId());
			}
		}

		@Override
		public void connectFailed(TelegramApiRequestException e) {
			btnConnect.setEnabled(true);
			connected = "Connect Failed: " + e.getMessage();
			statusChanged();
		}

		private void statusChanged() {

			String idStr;
			if (id >= 1) {
				idStr = "<br>" + id;
			} else {
				idStr = "";
			}
			lblStatus.setText("<html>" + connected + ": <br>" + username + idStr + "</html>");
			String tooltip = username + " :: " + connected + " | " + CAMPING_BOT;
			setTitle(tooltip);
			trayIcon.setToolTip(tooltip);
		}

	}
}

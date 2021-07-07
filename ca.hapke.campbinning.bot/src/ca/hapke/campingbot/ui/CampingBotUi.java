package ca.hapke.campingbot.ui;

import java.awt.Color;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.net.URL;
import java.security.ProtectionDomain;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.calendaring.monitor.TimerThreadWithKill;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.api.IStatus;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatDefaultComparator;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.channels.ChatAllowed;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserDefaultComparator;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.xml.AbstractLoader;
import ca.hapke.util.ui.CategoryLabel;
import ca.hapke.util.ui.PrettyTimeCellRenderer;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.swing.DefaultEventListModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;

/**
 * @author Nathan Hapke
 */
public abstract class CampingBotUi extends JFrame {

	private class UiTableRefresher implements CalendaredEvent<Void> {

		private TimesProvider<Void> times = new TimesProvider<Void>(
				new ByFrequency<Void>(null, 10, ChronoUnit.SECONDS));

		@Override
		public void doWork(ByCalendar<Void> event, Void value) {
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
	private DefaultEventTableModel<CalendaredEvent<?>> calendaredModel;

	private EventLogger eventLogger = EventLogger.getInstance();
	private CampingBotEngine bot;
	private JScrollPane sclUsers;
	private JLabel lblStatus;
	private JButton btnConnect;
	private JTextField txtChat;
//	private JList<CampingChat> lstChats;
	private JTable tblChats;
	private JScrollPane sclChats;
	private JTextArea txtCategoryValue;
	private JComboBox<String> cmbCategories;
	private Map<String, HasCategories<String>> categoriesMap = new HashMap<>();
	private Map<String, String> categoryMap = new HashMap<>();
	private TrayIcon trayIcon;

	private StatusUpdate statusUpdater = new StatusUpdate();
	private JScrollPane sclCategories;
	private JCheckBox chkFilterUsers;
	private SortedList<CampingChat> chatsSorted;
	private DefaultEventTableModel<CampingChat> chatModel;
	private JButton btnStatus;
	private JList<EventItem> lstLog;
	private JButton btnReply;
	private ProtectionDomain protectionDomain;

	/**
	 * Create the frame.
	 */
	public CampingBotUi() {

//		ApiContextInitializer.init();
		protectionDomain = provideProtectionDomain();
		this.bot = new CampingBot(protectionDomain);
		bot.init();
		bot.addStatusUpdate(statusUpdater);

		setTitle(CAMPING_BOT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				TimerThreadWithKill.shutdownThreads();
			}
		});
		setBounds(100, 100, 1284, 720);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		///
		chkFilterUsers = new JCheckBox("Filter Inactive");
		chkFilterUsers.setSelected(true);
		chkFilterUsers.setBounds(20, 115, 131, 23);
		contentPane.add(chkFilterUsers);

		TableFormatCampingUser usersFormat = new TableFormatCampingUser();
		EventList<CampingUser> usersEvents = CampingUserMonitor.getInstance().getUsers();
		SortedList<CampingUser> usersSorted = new SortedList<>(usersEvents, new CampingUserDefaultComparator());
		ActiveUserMatcherEditor filterMatcher = new ActiveUserMatcherEditor(true, chkFilterUsers);
		FilterList<CampingUser> usersFiltered = new FilterList<>(usersSorted, filterMatcher);
		TransformedList<CampingUser, CampingUser> userListForUi = GlazedListsSwing.swingThreadProxyList(usersFiltered);
		userModel = new DefaultEventTableModel<CampingUser>(userListForUi, usersFormat);

		sclUsers = new JScrollPane();
		sclUsers.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclUsers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sclUsers.setBounds(20, 145, 838, 217);
		contentPane.add(sclUsers);
		tblUsers = new JTable(userModel);
		sclUsers.setViewportView(tblUsers);
		TableComparatorChooser.install(tblUsers, usersSorted, TableComparatorChooser.SINGLE_COLUMN);

		PrettyTimeCellRenderer timeRenderer = new PrettyTimeCellRenderer();
		TableColumnModel userColumnModel = tblUsers.getColumnModel();
		usersFormat.setTableWidths(userColumnModel);
		tblUsers.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		///
		CampingChatManager chatMgr = CampingChatManager.getInstance(bot);
		TableFormatCampingChat chatFormat = new TableFormatCampingChat();
		EventList<CampingChat> chatEvents = chatMgr.getChatList();
		chatsSorted = new SortedList<>(chatEvents, new CampingChatDefaultComparator());

		TransformedList<CampingChat, CampingChat> chatListForUi = GlazedListsSwing.swingThreadProxyList(chatsSorted);
		chatModel = new DefaultEventTableModel<CampingChat>(chatListForUi, chatFormat);

		sclChats = new JScrollPane();
		sclChats.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclChats.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sclChats.setBounds(161, 33, 474, 101);
		contentPane.add(sclChats);

		tblChats = new JTable(chatModel);
		sclChats.setViewportView(tblChats);
		TableComparatorChooser.install(tblChats, chatsSorted, TableComparatorChooser.SINGLE_COLUMN);
		chatFormat.setTableWidths(tblChats.getColumnModel());
		tblChats.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					changeChatAccess();
				}
			}
		});
		///

		CalendarMonitor intervalThread = CalendarMonitor.getInstance();

		TableFormatCalendaredEvent calendaredFormat = new TableFormatCalendaredEvent();
		EventList<CalendaredEvent<?>> byCalendared = intervalThread.getEvents();
		calendaredModel = new DefaultEventTableModel<CalendaredEvent<?>>(
				GlazedListsSwing.swingThreadProxyList(byCalendared), calendaredFormat);

		JScrollPane sclSec = new JScrollPane();
		sclSec.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclSec.setBounds(882, 145, 376, 217);
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
		lblStatus.setBounds(22, 36, 116, 77);
		contentPane.add(lblStatus);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CampingSystem.getInstance().canConnect()) {
					bot.connect();
				} else {
					JOptionPane.showMessageDialog(CampingBotUi.this,
							"Cannot connect without the bot's username and token", "Failure",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnConnect.setBounds(20, 5, 120, 23);
		contentPane.add(btnConnect);

		ActionListener sendChatListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chat();
			}
		};
		txtChat = new JTextField();
		txtChat.addActionListener(sendChatListener);
		txtChat.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtChat.setBounds(161, 5, 474, 26);
		contentPane.add(txtChat);
		txtChat.setColumns(10);

		JButton btnSay = new JButton("Say");
		btnSay.addActionListener(sendChatListener);
		btnSay.setBounds(641, 3, 85, 26);
		contentPane.add(btnSay);

		btnReply = new JButton("Reply");
		btnReply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventItem event = lstLog.getSelectedValue();
				chat(event.telegramId, event.chat);
			}
		});
		btnReply.setBounds(641, 32, 85, 26);
		contentPane.add(btnReply);

		btnStatus = new JButton("Status...");
		btnStatus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeChatAccess();
			}
		});
		btnStatus.setBounds(641, 79, 86, 26);
		contentPane.add(btnStatus);

		JButton btnLeave = new JButton("Leave...");
		btnLeave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leaveChat();
			}
		});
		btnLeave.setBounds(641, 108, 86, 26);
		contentPane.add(btnLeave);

		JLabel lblChats = new CategoryLabel("Chat", Color.cyan);
		lblChats.setHorizontalAlignment(SwingConstants.CENTER);
		lblChats.setBounds(140, 7, 19, 127);
		contentPane.add(lblChats);

		JScrollPane sclLog = new JScrollPane();
		sclLog.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sclLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sclLog.setBounds(20, 373, 1238, 297);
		contentPane.add(sclLog);

		EventList<EventItem> recentLog = eventLogger.getUiLog();
		DefaultEventListModel<EventItem> logModel = new DefaultEventListModel<>(
				GlazedListsSwing.swingThreadProxyList(recentLog));

		lstLog = new JList<>();
		sclLog.setViewportView(lstLog);
		lstLog.setModel(logModel);
		lstLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		CategoryLabel lblLog = new CategoryLabel("Log", Color.MAGENTA);
		lblLog.setBounds(0, 373, 19, 297);
		contentPane.add(lblLog);

		CategoryLabel lblBySeconds = new CategoryLabel("Calendared", Color.green);
		lblBySeconds.setBounds(860, 145, 22, 217);
		contentPane.add(lblBySeconds);

		CategoryLabel lblCategory = new CategoryLabel("Categories", Color.orange);
		lblCategory.setBounds(765, 7, 19, 127);
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
		cmbCategories.setBounds(788, 5, 397, 26);
		contentPane.add(cmbCategories);

		sclCategories = new JScrollPane();
		sclCategories.setBounds(785, 33, 474, 101);
		contentPane.add(sclCategories);

		txtCategoryValue = new JTextArea();
		sclCategories.setViewportView(txtCategoryValue);
		txtCategoryValue.setLineWrap(true);
		txtCategoryValue.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JButton btnAddToCategory = new JButton("Add");
		btnAddToCategory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object s = cmbCategories.getSelectedItem();
				if (s != null) {
					HasCategories<String> categories = categoriesMap.get(s);
					String category = categoryMap.get(s);
					if (categories == null || category == null)
						return;

					String value = txtCategoryValue.getText();
					categories.addItem(category, value);
					txtCategoryValue.setText("");
				}
			}
		});
		btnAddToCategory.setBounds(1188, 5, 70, 26);
		contentPane.add(btnAddToCategory);

		CategoryLabel lblUsers = new CategoryLabel("Users", Color.blue);
		lblUsers.setBounds(0, 115, 19, 247);
		contentPane.add(lblUsers);

		CategoryLabel lblConnection = new CategoryLabel("Connection", Color.red);
		lblConnection.setBounds(0, 5, 19, 108);
		contentPane.add(lblConnection);

		Image app = null;
		try {
			File f = AbstractLoader.getFileNotInBinFolder(protectionDomain, "assets/app.png");
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
				File fire = AbstractLoader.getFileNotInBinFolder(protectionDomain, "assets/systray.png");
				URL fireUrl = fire.toURI().toURL();
				ImageIcon fireIcon = new ImageIcon(fireUrl, "tray-icon");
				Image fireImg = fireIcon.getImage();
				trayIcon = new TrayIcon(fireImg, CAMPING_BOT);
				trayIcon.setImageAutoSize(true);
				ActionListener showListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (CampingBotUi.this.getExtendedState() == ICONIFIED) {
							bringUp();
						} else {
							bringDown();
						}
					}
				};
				trayIcon.addActionListener(showListener);
				tray.add(trayIcon);

				PopupMenu popup = new PopupMenu();

				MenuItem show = new MenuItem("Show/Hide");
				show.addActionListener(showListener);
				popup.add(show);

				MenuItem exitItem = new MenuItem("Exit");
				exitItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispatchEvent(new WindowEvent(CampingBotUi.this, WindowEvent.WINDOW_CLOSING));
					}
				});
				popup.add(exitItem);
				trayIcon.setPopupMenu(popup);
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

		statusUpdater.updateUserInfo(bot.getBotUsername(), -1);
		intervalThread.add(new UiTableRefresher());
	}

	protected void bringDown() {
		this.setVisible(false);
		this.setExtendedState(ICONIFIED);
	}

	protected void bringUp() {
		this.setVisible(true);
		this.setExtendedState(NORMAL);
	}

	public void chat() {
		CampingChat chat;
		try {
			chat = getSelectedChat();
			chat(-1, chat);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(CampingBotUi.this, "Select a chat first", CAMPING_BOT,
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void chat(int replyToId, CampingChat chat) {
		String msg = txtChat.getText().trim();
		if (chat != null && msg.length() > 0) {
			try {
				TextCommandResult cmd = new TextCommandResult(CampingBot.TalkCommand, new TextFragment(msg));
				if (replyToId > 0)
					cmd.setReplyTo(replyToId);
				SendResult result = cmd.send(bot, chat.chatId);
				Message outgoingMsg = result.outgoingMsg;
				EventItem ei = new EventItem(CampingBot.TalkCommand, bot.getMeCamping(), outgoingMsg.getDate(), chat,
						outgoingMsg.getMessageId(), msg, null);
				EventLogger.getInstance().add(ei);
			} catch (TelegramApiException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
			txtChat.setText("");
		}
	}

	private CampingChat getSelectedChat() {
		return chatModel.getElementAt(tblChats.getSelectedRow());
	}

	private void changeChatAccess() {
		CampingChat chat = getSelectedChat();
		if (chat != null) {
			String[] possibleValues = { ChatAllowed.Allowed.toString(), ChatAllowed.Disallowed.toString() };
			String selectedValue = (String) JOptionPane.showInputDialog(CampingBotUi.this,
					"Choose access level for " + chat.getChatname(), "Input", JOptionPane.INFORMATION_MESSAGE, null,
					possibleValues, possibleValues[0]);
			if (selectedValue != null)
				chat.setAllowed(selectedValue);
		}
	}

	protected void leaveChat() {
		CampingChat chat = getSelectedChat();
		if (chat != null) {
			int result = JOptionPane.showConfirmDialog(CampingBotUi.this,
					"Do you want to leave: " + chat.getChatname() + " #" + chat.getChatId() + "?", "Are you sure?",
					JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				LeaveChat leave = new LeaveChat();
				leave.setChatId(Long.toString(chat.getChatId()));
				try {
					boolean success = bot.execute(leave);
					if (success) {
						JOptionPane.showMessageDialog(CampingBotUi.this, "Leave succeeded.", CAMPING_BOT,
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(CampingBotUi.this, "Leave failed.", CAMPING_BOT,
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (TelegramApiException e) {
					JOptionPane.showMessageDialog(CampingBotUi.this, "Leave failed: " + e.getLocalizedMessage(),
							CAMPING_BOT, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class StatusUpdate implements IStatus {
		private static final String ONLINE = "Online";
		private static final String OFFLINE = "Offline";
		private String connected = OFFLINE;
		private String username;
		private long id;

		private void updateUserInfo(String inUser, long inTId) {
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
		public void connectFailed(TelegramApiException e) {
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
			if (trayIcon != null)
				trayIcon.setToolTip(tooltip);
		}

	}

	/**
	 * Provide the ProtectionDomain for your subclass, so that the Serializer can find the config.xml file. This should
	 * be as simple as:
	 * 
	 * @return SubClass.class.getProtectionDomain();
	 */
	public abstract ProtectionDomain provideProtectionDomain();
}

package ca.hapke.campbinning.bot;

import ca.hapke.campbinning.bot.commands.SlashCommand;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * Singleton
 * 
 * @author Nathan Hapke
 */
public class CampingSystem implements CampingSerializable {

	private static CampingSystem instance = new CampingSystem();

	public static CampingSystem getInstance() {
		return instance;
	}

	private CampingSystem() {
	}

	private String dbHost, dbUser, dbPass;
	private int dbPort = -1;
	private int adminUser = -1;

	private String token;
	private String botUsername;
	private boolean dbEnabled = false;
	private boolean connectOnStartup = false;
	private String dbDb;

	public String getDbHost() {
		return dbHost;
	}

	public int getDbPort() {
		return dbPort;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public String getDbDb() {
		return dbDb;
	}

	public boolean isDbEnabled() {
		return dbEnabled;
	}

	public void enableDb(String host, int port, String user, String pass, String db) {
		if (this.dbHost == null && this.dbPort == -1 && this.dbUser == null && this.dbPass == null
				&& this.dbDb == null) {
			if (port <= 0)
				port = 5432;
			this.dbEnabled = true;
			this.dbHost = host;
			this.dbPort = port;
			this.dbUser = user;
			this.dbPass = pass;
			this.dbDb = db;
		}
	}

	public void setAdminUser(int adminUser) {
		if (this.adminUser == -1)
			this.adminUser = adminUser;
	}

	public boolean isAdmin(CampingUser fromUser) {
		if (adminUser == -1)
			return false;
		return fromUser.getTelegramId() == adminUser;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		if (this.token == null)
			this.token = token;
	}

	public String getBotUsername() {
		return botUsername;
	}

	public void setBotUsername(String botUsername) {
		if (this.botUsername == null)
			this.botUsername = botUsername;
	}

	public boolean isConnectOnStartup() {
		return connectOnStartup;
	}

	public void setConnectOnStartup(boolean connectOnStartup) {
		this.connectOnStartup = connectOnStartup;
	}

	@Override
	public void getXml(OutputFormatter of) {

		String outerTag = "system";
		of.start(outerTag);
		of.tagAndValue("token", token);
		of.tagAndValue("botUsername", botUsername);
		of.tagAndValue("adminUser", adminUser);
		of.tagAndValue("connectOnStartup", connectOnStartup);
		if (dbEnabled) {
			String dbTag = "db";
			of.start(dbTag);
			of.tagAndValue("dbHost", dbHost);
			of.tagAndValue("dbPort", dbPort);
			of.tagAndValue("dbUser", dbUser);
			of.tagAndValue("dbPass", dbPass);
			of.tagAndValue("dbDb", dbDb);
			of.finish(dbTag);
		}
		of.finish(outerTag);
	}

	public boolean canConnect() {
		return botUsername != null && token != null;
	}

	@Override
	public boolean shouldSave() {
		return false;
	}

	public boolean hasAccess(CampingUser campingFromUser, SlashCommand sc) {
		return sc.accessRequired() == AccessLevel.User || isAdmin(campingFromUser);
	}

}

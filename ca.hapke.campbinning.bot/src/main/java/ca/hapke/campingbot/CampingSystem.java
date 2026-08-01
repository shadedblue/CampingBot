package ca.hapke.campingbot;

import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.users.CampingUser;

/**
 * Singleton
 * 
 * @author Nathan Hapke
 */
public class CampingSystem {

	private static CampingSystem instance = new CampingSystem();

	public static CampingSystem getInstance() {
		return instance;
	}

	private CampingSystem() {
	}

	private String dbHost, dbUser, dbPass;
	private String dbDb;
	private String dbDriver;
	private int dbPort = -1;

	private int adminUser = -1;

	private String token;
	private String botUsername;
	private boolean dbEnabled = false;
	private boolean connectOnStartup = false;
	private String assetsFolder;

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

	public String getDbDriver() {
		return dbDriver;
	}

	public boolean isDbEnabled() {
		return dbEnabled;
	}

	public void enableDb(String host, int port, String user, String pass, String db, String driver) {
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
			this.dbDriver = driver;
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

	public String getAssetsFolder() {
		return assetsFolder;
	}

	public void setAssetsFolder(String assetsFolder) {
		if (this.assetsFolder == null)
			this.assetsFolder = assetsFolder;
	}

	public boolean isConnectOnStartup() {
		return connectOnStartup;
	}

	public void setConnectOnStartup(boolean connectOnStartup) {
		this.connectOnStartup = connectOnStartup;
	}

	public boolean canConnect() {
		return botUsername != null && token != null;
	}

	public boolean hasAccess(CampingUser campingFromUser, SlashCommand sc) {
		return sc.accessRequired() == AccessLevel.User || isAdmin(campingFromUser);
	}

}

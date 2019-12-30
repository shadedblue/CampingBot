package ca.hapke.campbinning.bot;

import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * Singleton
 * 
 * @author Nathan Hapke
 */
public class CampingSystem extends CampingSerializable {

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

	public String getDbHost() {
		return dbHost;
	}

	public int getDbPort() {
		return dbPort;
	}

	public String getDbUser() {
		return dbUser;
	}

	public boolean isDbEnabled() {
		return dbEnabled;
	}

	public void enableDb(String host, int port, String user, String pass) {
		if (this.dbHost == null && this.dbPort == -1 && this.dbUser == null && this.dbPass == null) {
			if (port <= 0)
				port = 5432;
			this.dbEnabled = true;
			this.dbHost = host;
			this.dbPort = port;
			this.dbUser = user;
			this.dbPass = pass;
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

	public String getDbPass() {
		return dbPass;
	}

	@Override
	public void getXml(OutputFormatter of) {

		String outerTag = "system";
		of.start(outerTag);
		of.tagAndValue("token", token);
		of.tagAndValue("botUsername", botUsername);
		of.tagAndValue("adminUser", adminUser);
		if (dbEnabled) {
			String dbTag = "db";
			of.start(dbTag);
			of.tagAndValue("dbHost", dbHost);
			of.tagAndValue("dbPort", dbPort);
			of.tagAndValue("dbUser", dbUser);
			of.tagAndValue("dbPass", dbPass);
			of.finish(dbTag);
		}
		of.finish(outerTag);
	}

}

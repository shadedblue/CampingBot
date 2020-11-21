package ca.hapke.campingbot.commands.api;

import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * This class exists only to solve the multiple-inheritance problem.
 * 
 * @author Nathan Hapke
 */
public abstract class InlineCommandBase extends AbstractCommand implements InlineCommand {

	protected CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();

}
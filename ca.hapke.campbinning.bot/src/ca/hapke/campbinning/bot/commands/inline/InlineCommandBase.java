package ca.hapke.campbinning.bot.commands.inline;

import ca.hapke.campbinning.bot.commands.AbstractCommand;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;

/**
 * This class exists only to solve the multiple-inheritance problem.
 * 
 * @author Nathan Hapke
 */
public abstract class InlineCommandBase extends AbstractCommand implements InlineCommand {

	protected CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();

}
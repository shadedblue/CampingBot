package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.api.BotCommandIds;
import ca.hapke.campbinning.bot.commands.api.SlashCommand;
import ca.hapke.campbinning.bot.commands.api.SlashCommandType;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class QuantityCommand extends AbstractCommand implements SlashCommand {

	private static final String prettyName = "Quantity";
	private static final String slashCommand = "quantity";
	private static final SlashCommandType[] SLASH_COMMAND_TYPES = new SlashCommandType[] {
			new SlashCommandType(prettyName, slashCommand, BotCommandIds.REGULAR_CHAT) };
	private List<HasCategories<String>> hasCategories;

	public QuantityCommand(List<HasCategories<String>> hasCategories) {
		this.hasCategories = hasCategories;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		CommandResult cr = new TextCommandResult(SLASH_COMMAND_TYPES[0]);
		cr.add("Quantities:\n");
		for (HasCategories<String> categories : hasCategories) {
			cr.add(categories.getContainerName(), TextStyle.Bold);
			cr.add("\n");
			List<String> cats = categories.getCategoryNames();
			for (String s : cats) {

				cr.add(s + ": " + categories.getCategory(s).size() + "\n");
			}
			cr.add("\n");
		}
		return cr;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMAND_TYPES;
	}

	@Override
	public String getCommandName() {
		return prettyName;
	}

}

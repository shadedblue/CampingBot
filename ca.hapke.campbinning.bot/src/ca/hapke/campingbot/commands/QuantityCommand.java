package ca.hapke.campingbot.commands;

import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.spell.SpellCommand;
import ca.hapke.campingbot.commands.spell.SpellPacks;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.util.StringUtil;

/**
 * @author Nathan Hapke
 */
public class QuantityCommand extends SlashCommand {

	private static final String prettyName = "Quantity";
	private static final String slashCommand = "quantity";
	private static final SlashCommandType SLASH_QUANTITY = new SlashCommandType(prettyName, slashCommand,
			BotCommandIds.REGULAR_CHAT);
	private static final SlashCommandType[] SLASH_COMMAND_TYPES = new SlashCommandType[] { SLASH_QUANTITY };
	private List<HasCategories<String>> hasCategories;

	public QuantityCommand(List<HasCategories<String>> hasCategories) {
		this.hasCategories = hasCategories;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		CommandResult cr = new TextCommandResult(SLASH_QUANTITY);
		cr.add("Quantities:\n");
		for (HasCategories<String> categories : hasCategories) {
			String container = categories.getContainerName();
			cr.add(container, TextStyle.Bold);
			cr.add("\n");
			if (categories instanceof SpellCommand) {
				SpellCommand sc = (SpellCommand) categories;
				displayPacks(cr, sc);
			} else {
				displayCategories(cr, categories);
			}
			cr.add("\n");
		}
		return cr;
	}

	private void displayPacks(CommandResult cr, SpellCommand sc) {
		SpellPacks packs = sc.getPacks();
		for (Map.Entry<String, CategoriedItems<String>> e : packs.entrySet()) {
			String genre = e.getKey();
			List<String> aliases = packs.getAliases(genre);
			CategoriedItems<String> data = e.getValue();
			cr.add(genre, TextStyle.Underline);
			if (aliases != null && aliases.size() > 0) {
				cr.add(" [" + StringUtil.join(aliases, ", ") + "]", TextStyle.Italic);
			}
			cr.add("\n");

			List<String> names = data.getCategoryNames();
			for (String name : names) {
				cr.add(">");
				cr.add(name);
				int size = data.getSize(name);
				cr.add(": " + size + "\n");
			}
		}
	}

	private void displayCategories(CommandResult cr, HasCategories<String> categories) {
		List<String> cats = categories.getCategoryNames();
		for (String s : cats) {
			cr.add(s);
			cr.add(": " + categories.getSize(s) + "\n");
		}
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMAND_TYPES;
	}

	@Override
	public String getCommandName() {
		return prettyName;
	}
	@Override
	protected void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		result.add("This command lists the number of items in each category of the bot.");
		result.add("It can be used to see how many items are available for each command.", TextStyle.Italic);
		result.newLine();
		result.add("You can also use it to see how many spells are available in each spell pack.");
		result.newLine();
		result.add("Use /quantity <command name> to see the number of items in a specific command's categories.");
		result.newLine();
		result.add("Use /quantity <spell pack name> to see the number of spells in a specific spell pack.");
	}
}

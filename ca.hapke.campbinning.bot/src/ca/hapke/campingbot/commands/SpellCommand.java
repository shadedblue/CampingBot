package ca.hapke.campingbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.api.CampingSerializable;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.commands.api.InlineCommandBase;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.xml.OutputFormatter;
import ca.hapke.util.CollectionUtil;
import ca.hapke.util.StringUtil;

/**
 * @author Nathan Hapke
 */
public class SpellCommand extends InlineCommandBase
		implements HasCategories<String>, CampingSerializable, SlashCommand {

	private static final String SPELL = "Spell";

	public static final SlashCommandType SlashSpellCommand = new SlashCommandType(SPELL, "spell",
			BotCommandIds.SPELL | BotCommandIds.USE);
	public static final ResponseCommandType SpellDipshitCommand = new ResponseCommandType("SpellDipshit",
			BotCommandIds.SPELL | BotCommandIds.FAILURE);

	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashSpellCommand };

	private static final TextFragment CAST_THE = new TextFragment("I cast the ");
	private static final TextFragment OF = new TextFragment(" of ");
	private static final TextFragment ON = new TextFragment(" on ");
	private static final TextFragment AND_YELL = new TextFragment(" and yell \"");
	private static final TextFragment END_QUOTE = new TextFragment("\"");
	private static final String ADJECTIVE_CATEGORY = "adjective";
	private static final String EXCLAMATION_CATEGORY = "exclamation";
	private static final String ITEM_CATEGORY = "item";
	private CampingBot bot;
	private boolean shouldSave = false;

	public SpellCommand(CampingBot bot) {
		this.bot = bot;
		categories = new CategoriedItems<String>(ADJECTIVE_CATEGORY, ITEM_CATEGORY, EXCLAMATION_CATEGORY);
		adjectives = categories.getList(ADJECTIVE_CATEGORY);
		items = categories.getList(ITEM_CATEGORY);
		exclamations = categories.getList(EXCLAMATION_CATEGORY);
	}

	private CategoriedItems<String> categories;
	private List<String> adjectives;
	private List<String> items;
	private List<String> exclamations;

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		CampingUser targetUser = bot.findTarget(message, false, true, BotChoicePriority.Last);
		SpellResult result = createSpell(campingFromUser, targetUser);
		return result.provideCommandResult();
	}

	public SpellResult createSpell(CampingUser campingFromUser, CampingUser targetUser) {
		SpellResult result;
		if (targetUser == null) {
			result = new SpellResult(campingFromUser, targetUser, SpellFailure.Dipshit);
		} else if (targetUser == bot.getMeCamping()) {
			result = new SpellResult(campingFromUser, targetUser, SpellFailure.NotMe);
		} else {
			result = new SpellResult(campingFromUser, targetUser, cast(targetUser));
		}
		return result;
	}

	public List<ResultFragment> cast(CampingUser target) {
		String adj = CollectionUtil.getRandom(adjectives);
		String item = CollectionUtil.getRandom(items);
		String excl = CollectionUtil.getRandom(exclamations);
		if (!StringUtil.endsWithPunctuation(excl)) {
			excl += "!";
		}

		List<ResultFragment> out = new ArrayList<ResultFragment>();
		out.add(CAST_THE);
		out.add(new TextFragment(adj, TextStyle.Bold));
		out.add(OF);
		out.add(new TextFragment(item, TextStyle.Bold));
		out.add(ON);
		out.add(new MentionFragment(target));
		out.add(AND_YELL);
		out.add(new TextFragment(excl, TextStyle.Bold));

		out.add(END_QUOTE);
		return out;
	}

	@Override
	public String getContainerName() {
		return SPELL;
	}

	@Override
	public String getCommandName() {
		return SPELL;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public EventItem chosenInlineQuery(Update update, CallbackId id, CampingUser campingFromUser, String resultText) {
		int[] ids = id.getIds();

		int targetUserId = ids[0];
		boolean success = ids[1] > 0;

		CampingUser targetUser = userMonitor.getUser(targetUserId);

		CommandType cmd = success ? SlashSpellCommand : SpellDipshitCommand;

		int campingId = targetUser != null ? targetUser.getCampingId() : -1;
		EventItem event = new EventItem(cmd, campingFromUser, null, null, id.getUpdateId(), resultText, campingId);
		return event;
	}

	@Override
	public List<InlineQueryResult> provideInlineQuery(Update update, String input, int updateId,
			MessageProcessor processor) {
		String[] words = input.split(" ");
		if (words.length == 0)
			return null;

		List<ResultFragment> outputSpell;
		CampingUser targetUser = userMonitor.getUser(words[0]);
		SpellResult spellResult = createSpell(null, targetUser);
		outputSpell = spellResult.provideInlineResult();
		String targetFirst;
		if (targetUser != null) {
			targetFirst = targetUser.getFirstname();
		} else {
			return null;
		}

		int targetId = -1;

		if (targetUser != null) {
			targetId = targetUser.getTelegramId();
		}

		InputTextMessageContent mcSpell = new InputTextMessageContent();
		mcSpell.setDisableWebPagePreview(true);
		String spell = processor.process(true, outputSpell);
		mcSpell.setMessageText(spell);
		mcSpell.setParseMode(BotConstants.MARKDOWN);

		InlineQueryResultArticle articleSpell = new InlineQueryResultArticle();
		articleSpell.setTitle("Spell on " + targetFirst);
		CallbackId fullId = createQueryId(updateId, targetId, spellResult.isSuccess() ? 1 : 0);
		articleSpell.setId(fullId.getResult());
		articleSpell.setInputMessageContent(mcSpell);
		return Collections.singletonList(articleSpell);
	}

	public void setAdjectives(List<String> adjectives) {
		if (categories.putAll(ADJECTIVE_CATEGORY, adjectives))
			shouldSave = true;
	}

	public void setItems(List<String> items) {
		if (categories.putAll(ITEM_CATEGORY, items))
			shouldSave = true;
	}

	public void setExclamations(List<String> exclamations) {
		if (categories.putAll(EXCLAMATION_CATEGORY, exclamations))
			shouldSave = true;
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public List<String> getCategory(String name) {
		return categories.getList(name);
	}

	@Override
	public void addItem(String category, String value) {
		if (categories.put(category, value))
			shouldSave = true;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String outerTag = "spell";
		of.start(outerTag);
		of.tagCategories(categories);
		of.finish(outerTag);

		shouldSave = false;
	}

}
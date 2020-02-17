package ca.hapke.campbinning.bot.commands.inline;

import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.SpellDipshitException;
import ca.hapke.campbinning.bot.commands.SpellGenerator;
import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 *
 */
public class SpellInlineCommand extends InlineCommand {

	private static final String INLINE_SPELL = "spell";
	private SpellGenerator spellGen;

	public SpellInlineCommand(SpellGenerator spellGen) {
		this.spellGen = spellGen;
	}

	@Override
	public String getCommandName() {
		return INLINE_SPELL;
	}

	@Override
	public EventItem chosenInlineQuery(String[] words, CampingUser campingFromUser, Integer inlineMessageId) {
		if (words.length < 3)
			return null;

		int targetUserId = Integer.parseInt(words[2]);

		CampingUser targetUser = userMonitor.getUser(targetUserId);
		SpellGenerator.countSpellActivation(campingFromUser, targetUser);
		String result = words[2];
		EventItem event = new EventItem(BotCommand.Spell, campingFromUser, null, null, inlineMessageId, result,
				targetUser.getCampingId());
		return event;
	}

	@Override
	public InlineQueryResult provideInlineQuery(String input, int updateId, MessageProcessor processor) {
		String[] words = input.split(" ");
		if (words.length == 0)
			return null;

		ResultFragment[] outputSpell;
		CampingUser targetUser = userMonitor.getUser(words[0]);
		String targetFirst;
		if (targetUser != null) {
			outputSpell = spellGen.cast(targetUser);
			targetFirst = targetUser.getFirstname();
		} else {
			outputSpell = new ResultFragment[] { new TextFragment(SpellDipshitException.IM_A_DIPSHIT) };
			targetFirst = CampingUser.UNKNOWN_TARGET;
		}

		int targetId = -1;

		if (targetUser != null) {
			targetId = targetUser.getTelegramId();
		}

		InputTextMessageContent mcSpell = new InputTextMessageContent();
		mcSpell.setDisableWebPagePreview(true);
		mcSpell.setMessageText(processor.process(outputSpell));
		mcSpell.setParseMode(CampingBotEngine.MARKDOWN);

		InlineQueryResultArticle articleSpell = new InlineQueryResultArticle();
		articleSpell.setTitle("spell on " + targetFirst);
		// TODO FIX
		articleSpell.setId(createQueryId(updateId, targetId));
		articleSpell.setInputMessageContent(mcSpell);
		return articleSpell;
	}

}

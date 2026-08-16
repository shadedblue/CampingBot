package ca.hapke.campingbot.commands.overlays;

import java.awt.Point;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommandType;

/**
 * @author Nathan Hapke
 */
public class OverlayJakeCommand extends AbstractOverlayBro {
	private static final String OVERLAY_JAKE = "OverlayJake";
	private static final SlashCommandType SlashJake = new SlashCommandType(OVERLAY_JAKE, "jake",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF);
	private static final String OVERLAY_FILENAME = "jake.png";

	public OverlayJakeCommand(CampingBot bot) {
		super(bot, OVERLAY_FILENAME, SlashJake);
	}

	@Override
	protected Point getResultLocation(int w, int h, int overlayW, int overlayH) {
		return new Point(w - overlayW, h - overlayH);
	}
}

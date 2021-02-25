package ca.hapke.campingbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class Resources {
	private final List<Emoji> faces = new ArrayList<>();
	private final Map<String, Emoji> facesMap = new HashMap<>();
	private final List<Emoji> balls = new ArrayList<>();
	private final Map<String, Emoji> ballsMap = new HashMap<>();
	private final List<Emoji> various = new ArrayList<>();
	private final Map<String, Emoji> variousMap = new HashMap<>();

	public void loadAllEmoji() {
		faces.clear();
		balls.clear();
		loadEmoji(faces, facesMap, "grinning", "relaxed", "heart_eyes", "stuck_out_tongue_closed_eyes", "flushed",
				"unamused", "persevere", "joy", "sleepy", "cold_sweat", "sweat", "tired_face", "scream", "rage",
				"confounded", "yum", "sunglasses", "dizzy_face", "imp", "grimacing", "confused", "no_mouth", "smirk",
				"smiley", "blush", "wink", "kissing_heart", "kissing", "stuck_out_tongue_winking_eye",
				"stuck_out_tongue", "grin", "cry", "sob", "disappointed_relieved", "sweat_smile", "weary", "fearful",
				"angry", "triumph", "laughing, satisfied", "astonished", "frowning", "smiling_imp", "open_mouth",
				"neutral_face", "innocent", "expressionless", "skull", "alien", "eye_roll", "thinking", "zipper_mouth",
				"head_bandage", "money_mouth", "flipped_face", "skull_crossbones", "face_with_cowboy_hat", "rofl",
				"shocked_face_with_exploding_head");
		loadEmoji(balls, ballsMap, "soccer", "rugby_football", "football", "basketball", "baseball", "8ball", "bowling",
				"cookie", "volleyball", "balloon", "crystal_ball", "boom", "bomb", "moneybag", "poop", "bulb",
				"jack_o_lantern", "trackball", "potato", "cucumber", "egg", "doughnut", "eggplant", "banana", "peach",
				"cherries", "corn", "fire", "zap", "volcano", "floppy_disk", "cd", "tennis", "beers", "fried_shrimp",
				"grapes", "melon", "tomato", "hot_dog", "taco", "cheese", "cloud_tornado", "peanuts");

		loadEmoji(various, variousMap, "cake", "white_check_mark", "x");
	}

	private static void loadEmoji(List<Emoji> emojis, Map<String, Emoji> emojisMap, String... aliases) {
		for (int i = 0; i < aliases.length; i++) {
			String a = aliases[i];
			loadEmoji(emojis, emojisMap, a);
		}
	}

	public static Emoji loadEmoji(List<Emoji> emojis, Map<String, Emoji> emojisMap, String a) {
		Emoji e = EmojiManager.getForAlias(a);
		if (e != null) {
			if (emojis != null)
				emojis.add(e);
			if (emojisMap != null)
				emojisMap.put(a, e);
		}
		return e;
	}

	public String listFaces() {
		return listAll(faces);
	}

	public String listBalls() {
		return listAll(balls);
	}

	public Emoji getFace(String key) {
		Emoji emoji = facesMap.get(key);
		if (emoji == null)
			loadEmoji(faces, facesMap, key);
		return emoji;
	}

	public Emoji getBall(String key) {
		Emoji emoji = ballsMap.get(key);
		if (emoji == null)
			loadEmoji(balls, ballsMap, key);
		return emoji;
	}

	private static String listAll(List<Emoji> list) {
		StringBuilder sb = new StringBuilder();
		for (Emoji f : list) {
			sb.append(f.getUnicode());
		}
		// Long chatId = message.getChatId();
		String msg = sb.toString();
		return msg;
		// sendMsg(chatId, msg);
	}

	public String getRandomBall() {
		return getRandom(balls);
	}

	public String getRandomFace() {
		return getRandom(faces);
	}

	public Emoji getRandomBallEmoji() {
		return CollectionUtil.getRandom(balls);
	}

	public Emoji getRandomFaceEmoji() {
		return CollectionUtil.getRandom(faces);
	}

	public Emoji getCake() {
		return variousMap.get("cake");
	}

	public Emoji getCheck() {
		return variousMap.get("white_check_mark");
	}

	public Emoji getX() {
		return variousMap.get("x");
	}

	private static String getRandom(List<Emoji> l) {
		return CollectionUtil.getRandom(l).getUnicode();
	}
}

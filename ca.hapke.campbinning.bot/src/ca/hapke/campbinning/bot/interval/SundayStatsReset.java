/// **
// *
// */
// package ca.hapke.campbinning.bot.interval;
//
// import java.text.SimpleDateFormat;
// import java.time.DayOfWeek;
// import java.time.Duration;
// import java.time.Instant;
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.time.ZoneId;
// import java.time.temporal.ChronoField;
// import java.time.temporal.ChronoUnit;
// import java.util.Date;
//
// import ca.hapke.campbinning.bot.BotCommand;
// import ca.hapke.campbinning.bot.CampingBot;
// import ca.hapke.campbinning.bot.CampingSerializable;
// import ca.hapke.campbinning.bot.channels.CampingChat;
// import ca.hapke.campbinning.bot.commands.StatsGenerator;
// import ca.hapke.campbinning.bot.users.CampingUserMonitor;
// import ca.hapke.campbinning.bot.xml.OutputFormatter;
//
/// **
// * @author Nathan Hapke
// */
// public class SundayStatsReset extends CampingSerializable implements
/// IntervalByExecutionTime {
//
// private long nextWeeklyStats;
// private CampingBot bot;
// private StatsGenerator stats;
//
// public SundayStatsReset(CampingBot campingBot, StatsGenerator stats) {
// this.bot = campingBot;
// this.stats = stats;
// nextWeeklyStats = -1;
// }
//
// @Override
// public long getNextExecutionTime() {
// return nextWeeklyStats;
// }
//
// /**
// * For parser only
// */
// public void setNextWeeklyStats(long nextWeeklyStats) {
// // if (nextWeeklyStats < 0)
// this.nextWeeklyStats = nextWeeklyStats;
// }
//
// @Override
// public void doWork() {
// if (nextWeeklyStats > 0) {
// statsEndOfWeekCommand();
// shouldSave = true;
// }
// setNextEvent();
// }
//
// public void statsEndOfWeekCommand() {
// long chatId = CampingBot.CAMPING_CHAT_ID;
// Date d = new Date();
// SimpleDateFormat sdf = new SimpleDateFormat("MMMM d");
// String banner = "#WeeklyStats for " + sdf.format(d);
// String result = stats.statsCommand(chatId, banner);
// // Message msg =
// bot.sendMsg(chatId, result);
// // PinChatMessage pin = new PinChatMessage(chatId, msg.getMessageId());
// CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();
// userMonitor.resetStats();
// // try {
// // bot.execute(pin);
// // } catch (TelegramApiException e) {
// // result = "Pin failed";
// // }
// CampingChat chat = bot.getChatManager().get(chatId, bot);
// bot.getEventLogger().add(BotCommand.StatsEndOfWeek, bot.getMeCamping(), chat,
/// result);
// }
//
// public void setNextEvent() {
// LocalDate ld;
// LocalTime lt;
// Duration oneDay = Duration.ofDays(1);
// Duration oneHour = Duration.ofHours(1);
// ZoneId zone = ZoneId.systemDefault();
//
// Instant next = Instant.now();
// next = next.truncatedTo(ChronoUnit.DAYS);
// next = next.plus(Duration.ofHours(12));
// do {
// next = next.plus(oneDay);
// ld = LocalDate.ofInstant(next, zone);
// } while (ld.getDayOfWeek() != DayOfWeek.SUNDAY);
//
// do {
// next = next.plus(oneHour);
// lt = LocalTime.ofInstant(next, zone);
// } while (lt.get(ChronoField.CLOCK_HOUR_OF_DAY) != 12);
//
// nextWeeklyStats = next.toEpochMilli();
// }
//
// @Override
// public void getXml(OutputFormatter of) {
// of.tagAndValue("nextWeeklyStats", nextWeeklyStats);
// }
//
// }

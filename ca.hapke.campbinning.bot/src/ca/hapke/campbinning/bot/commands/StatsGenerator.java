// package ca.hapke.campbinning.bot.commands;
//
// import java.text.NumberFormat;
// import java.util.Date;
//
// import org.ocpsoft.prettytime.PrettyTime;
//
// import com.vdurmont.emoji.Emoji;
//
// import ca.hapke.campbinning.bot.Resources;
// import ca.hapke.campbinning.bot.users.CampingUser;
// import ca.hapke.campbinning.bot.users.CampingUserMonitor;
// import ca.hapke.campbinning.bot.users.CampingUserStatsComparator;
// import ca.hapke.campbinning.bot.users.CampingUserVictimComparator;
// import ca.odell.glazedlists.EventList;
// import ca.odell.glazedlists.SortedList;
//
/// **
// * @author Nathan Hapke
// *
// */
// public class StatsGenerator {
// private Resources res;
// // private CampingUserMonitor userMonitor;
//
// private NumberFormat nf;
// private PrettyTime pt = new PrettyTime();
//
// private final SortedList<CampingUser> usersSortedByBalls;
// private final SortedList<CampingUser> usersSortedByVictim;
//
// public StatsGenerator(Resources res, CampingUserMonitor userMonitor) {
// this.res = res;
// // this.userMonitor = userMonitor;
//
// nf = NumberFormat.getInstance();
// nf.setMinimumFractionDigits(0);
// nf.setMaximumFractionDigits(1);
//
// EventList<CampingUser> users = userMonitor.getUsers();
// usersSortedByBalls = new SortedList<>(users, new
// CampingUserStatsComparator());
// usersSortedByVictim = new SortedList<>(users, new
// CampingUserVictimComparator());
// }
//
// public String statsCommand(Long chatId) {
// return statsCommand(chatId, "STATS");
// }
//
// public String statsCommand(Long chatId, String banner) {
// StringBuilder out = new StringBuilder();
//
// Emoji[] statsBanner = res.statsBanner;
// Emoji[] victimBanner = res.victimBanner;
// Emoji[] statsTrophy = res.statsTrophy;
// for (int i = 0; i < statsBanner.length; i++) {
// out.append(statsBanner[i].getUnicode());
// }
// out.append(" *");
// out.append(banner);
// out.append("* ");
//
// for (int i = statsBanner.length - 1; i >= 0; i--) {
// out.append(statsBanner[i].getUnicode());
// }
// out.append("\n--------------------------------------");
//
// int j = 0;
// boolean foundNegativeYet = false;
// boolean scoredSomebody = false;
// for (CampingUser e : usersSortedByBalls) {
// float score = e.getScore();
// if (score == 0)
// continue;
// if (score < 0 && !foundNegativeYet) {
// foundNegativeYet = true;
// out.append("\n--------------------------------------\n");
// out.append("*SUPER LOSERS WITH NEGATIVE POINTS*");
// out.append("\n--------------------------------------");
// }
// scoredSomebody = true;
// out.append("\n");
// if (j < statsTrophy.length && !foundNegativeYet) {
// out.append(statsTrophy[j].getUnicode());
// } else {
// out.append(res.getRandomFace());
// }
// out.append(" *");
// String displayName = e.getDisplayName();
// out.append(displayName);
//
// out.append("* with ");
// out.append(nf.format(score));
// long lastUpdate = e.getLastUpdate();
// if (lastUpdate > 0) {
// out.append(" _(");
// out.append(pt.format(new Date(lastUpdate)));
// out.append(")_");
// }
// j++;
// }
//
// j = 0;
// scoredSomebody = false;
//
// for (CampingUser e : usersSortedByVictim) {
// int score = e.getVictimCount();
// if (score == 0)
// continue;
// if (!scoredSomebody) {
// scoredSomebody = true;
//
// out.append("\n--------------------------------------\n");
// for (Emoji x : victimBanner)
// out.append(x.getUnicode());
// out.append(" *VICTIMS* ");
// for (int i = 0; i < victimBanner.length; i++) {
// out.append(res.getRandomBall());
// }
// out.append("\n--------------------------------------");
// }
//
// out.append("\n");
// if (j < statsTrophy.length) {
// out.append(statsTrophy[j].getUnicode());
// } else {
// out.append(res.getRandomBall());
// }
// out.append(" *");
// String displayName = e.getDisplayName();
// out.append(displayName);
//
// out.append("* with ");
// out.append(nf.format(score));
//
// j++;
// }
// if (!scoredSomebody) {
// out.append("\nNobody has any points");
// }
//
// return out.toString();
// }
// }

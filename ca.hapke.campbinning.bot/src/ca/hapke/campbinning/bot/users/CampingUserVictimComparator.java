// package ca.hapke.campbinning.bot.users;
//
// import java.util.Comparator;
//
/// **
// * @author Nathan Hapke
// */
// public class CampingUserVictimComparator implements Comparator<CampingUser> {
// @Override
// public int compare(CampingUser a, CampingUser b) {
// int score = b.getVictimCount() - a.getVictimCount();
// // if (score != 0)
// if (score < 0)
// return -1;
// if (score > 0)
// return 1;
//
// long diff = b.getLastUpdate() - a.getLastUpdate();
//
// if (diff < 0)
// return -1;
// if (diff > 0)
// return 1;
//
// return 0;
// }
// }
package lqw.aircombat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class HUDRadar implements Listener {
    private static final int N = 63;

    public static float signedAngle(Vector a, Vector b) {
        float angle = a.angle(b);
        float y = (float) a.getCrossProduct(b).getY();
        if (y < 0) return angle;
        else return -angle;
    }

    public static void start(AirCombat pluginTest) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : pluginTest.getServer().getOnlinePlayers()) {
                    String c[] = new String[N]; // 63
                    Vector direction = player.getLocation().getDirection().setY(0);
                    for (int i = 0; i < N; i++) {
                        c[i] = "-";
                    }

                    float degree = (signedAngle(direction, new Vector(1, 0, 0)));
                    int index = 31;
                    index += (int) (degree / (Math.PI / 60));
                    if (index >= 0 && index < 63) c[index] = "E";

                    degree = (signedAngle(direction, new Vector(-1, 0, 0)));
                    index = 31;
                    index += (int) (degree / (Math.PI / 60));
                    if (index >= 0 && index < 63) c[index] = "W";

                    degree = (signedAngle(direction, new Vector(0, 0, -1)));
                    index = 31;
                    index += (int) (degree / (Math.PI / 60));
                    if (index >= 0 && index < 63) c[index] = "N";

                    degree = (signedAngle(direction, new Vector(0, 0, 1)));
                    index = 31;
                    index += (int) (degree / (Math.PI / 60));
                    if (index >= 0 && index < 63) c[index] = "S";


                    for (Player otherPlayer : pluginTest.getServer().getOnlinePlayers()) {
                        if (otherPlayer.getUniqueId() == player.getUniqueId()) continue;
                        Location selfLoc = player.getLocation().clone(), othLoc = otherPlayer.getLocation().clone();
                        selfLoc.setY(0);
                        othLoc.setY(0);
                        int dis = (int) othLoc.subtract(selfLoc).length();

                        degree = (signedAngle(direction,
                                otherPlayer.getLocation().toVector().setY(0).subtract(player.getLocation().toVector().setY(0))));
                        index = 31;
                        index += (int) (degree / (Math.PI / 60));
                        if (index >= 0 && index < 63) {
//                            Team team = otherPlayer.getScoreboard().getEntryTeam(otherPlayer.getName());
//                            if(team == null)c[index] = ChatColor.RED + "△";
//                            TextColor textColor= team.color();
                            c[index] = ChatColor.RED + String.valueOf(dis) + ChatColor.RESET;
                        }
                        degree = (signedAngle(direction.clone().multiply(-1),
                                otherPlayer.getLocation().toVector().setY(0).subtract(player.getLocation().toVector().setY(0))));
                        index = 31;
                        index += (int) (degree / (Math.PI / 60));
                        if (index >= 0 && index < 63) {
//                            Team team = otherPlayer.getScoreboard().getEntryTeam(otherPlayer.getName());
//                            if(team == null)c[index] = ChatColor.RED + "△";
//                            TextColor textColor= team.color();
                            c[index] = ChatColor.GREEN + String.valueOf(dis) + ChatColor.RESET;
                        }
                    }

                    String str = "";
                    for (String a : c) {
                        str += a;
                    }

                    player.sendActionBar(str);
                }
            }
        }.runTaskTimer(pluginTest, 0, 1);
    }


}

package lqw.aircombat;

import com.destroystokyo.paper.Title;
import lqw.aircombat.AirCombat;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class HUDRadar implements Listener {
    private static final int N=63;

    public static float signedAngle(Vector a, Vector b){
        float angle = a.angle(b);
        float y = (float)a.getCrossProduct(b).getY();
        if(y < 0)return angle;
        else return -angle;
    }

    public static void start(AirCombat pluginTest){
        new BukkitRunnable(){
            @Override
            public void run(){
                for(Player player : pluginTest.getServer().getOnlinePlayers()){
                    String  c[] = new String[N]; // 63
                    Vector direction = player.getLocation().getDirection().setY(0);
                    for(int i = 0; i < N; i ++){
                        c[i] = "-";
                    }

                    float degree = (signedAngle(direction, new Vector(1, 0, 0)));
                    int index = 31;
                    index += (int)(degree / (Math.PI / 60));
                    if(index >= 0 && index <63) c[index] = "E";

                    degree = (signedAngle(direction, new Vector(-1, 0, 0)));
                    index = 31;
                    index += (int)(degree / (Math.PI / 60));
                    if(index >= 0 && index <63) c[index] = "W";

                    degree = (signedAngle(direction, new Vector(0, 0, -1)));
                    index = 31;
                    index += (int)(degree / (Math.PI / 60));
                    if(index >= 0 && index <63) c[index] = "N";

                    degree = (signedAngle(direction, new Vector(0, 0, 1)));
                    index = 31;
                    index += (int)(degree / (Math.PI / 60));
                    if(index >= 0 && index <63) c[index] = "S";


                    for(Player otherPlayer : pluginTest.getServer().getOnlinePlayers()){
                        if(otherPlayer.getUniqueId() == player.getUniqueId())continue;

                        degree = (signedAngle(direction, otherPlayer.getLocation().toVector().setY(0).subtract(player.getLocation().toVector().setY(0))));
                        index = 31;
                        index += (int)(degree / (Math.PI / 60));
                        if(index >= 0 && index <63){
//                            Team team = otherPlayer.getScoreboard().getEntryTeam(otherPlayer.getName());
//                            if(team == null)c[index] = ChatColor.RED + "△";
//                            TextColor textColor= team.color();
                            c[index] = ChatColor.RED + "△" + ChatColor.RESET;
                        }
                        degree = (signedAngle(direction.clone().multiply(-1), otherPlayer.getLocation().toVector().setY(0).subtract(player.getLocation().toVector().setY(0))));
                        index = 31;
                        index += (int)(degree / (Math.PI / 60));
                        if(index >= 0 && index <63){
//                            Team team = otherPlayer.getScoreboard().getEntryTeam(otherPlayer.getName());
//                            if(team == null)c[index] = ChatColor.RED + "△";
//                            TextColor textColor= team.color();
                            c[index] = ChatColor.GREEN + "▽" + ChatColor.RESET;
                        }
                    }

                    String str = "";
                    for(String a : c){
                        str += a;
                    }

                    player.sendActionBar(str);
                }
            }
        }.runTaskTimer(pluginTest, 0, 1);
    }


}

package lqw.aircombat;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Welcome implements Listener {
    @EventHandler
    public void welcomePlayer(PlayerJoinEvent event) {
        Player thisPlayer = event.getPlayer();
        for (Player player : event.getPlayer().getServer().getOnlinePlayers()) {
            int onlineCount = event.getPlayer().getServer().getOnlinePlayers().size();
            String subTitle = ChatColor.YELLOW + "Online Player Count: " + onlineCount;
            if (player.getName().equals(event.getPlayer().getName())) {
                player.sendTitle(ChatColor.GREEN + "Welcome!", subTitle, 10, 30, 10);
            } else {
                player.sendTitle(ChatColor.GREEN + event.getPlayer().getName() + " Joined!", subTitle, 5, 10, 5);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }
}

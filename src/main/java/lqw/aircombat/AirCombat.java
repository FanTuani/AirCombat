package lqw.aircombat;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AirCombat extends JavaPlugin {
    public final Plugin instance = this;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Dynamic(), this);
        for (Player player : getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }

    @Override
    public void onDisable() {
    }
}

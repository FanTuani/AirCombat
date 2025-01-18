package lqw.aircombat;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AirCombat extends JavaPlugin {
    public static AirCombat instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new Dynamic(), this);
        getServer().getPluginManager().registerEvents(new Missile(), this);
        getServer().getPluginManager().registerEvents(new DecoyMissile(), this);
        for (Player player : getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }

    @Override
    public void onDisable() {
    }
}

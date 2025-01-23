package lqw.aircombat;

import lqw.aircombat.combat.Bombing;
import lqw.aircombat.combat.Cannon;
import lqw.aircombat.move.Dynamic;
import lqw.aircombat.move.MAN;
import lqw.aircombat.props.BlockingRope;
import lqw.aircombat.props.DecoyMissile;
import lqw.aircombat.props.Missile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AirCombat extends JavaPlugin {
    public static AirCombat instance;

    @Override
    public void onEnable() {
        instance = this;
        HUDRadar.start(this);
        getServer().getPluginManager().registerEvents(new Dynamic(), this);
        getServer().getPluginManager().registerEvents(new Missile(), this);
        getServer().getPluginManager().registerEvents(new DecoyMissile(), this);
        getServer().getPluginManager().registerEvents(new Cannon(), this);
        getServer().getPluginManager().registerEvents(new Bombing(), this);
        getServer().getPluginManager().registerEvents(new MAN(), this);
        getServer().getPluginManager().registerEvents(new BlockingRope(), this);

        for (Player player : getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }

    @Override
    public void onDisable() {
    }
}

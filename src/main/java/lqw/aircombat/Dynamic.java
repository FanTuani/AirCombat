package lqw.aircombat;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Dynamic implements Listener {
    @EventHandler
    public void onPlayGlide(PlayerMoveEvent event) {
        if (!event.getPlayer().isGliding()) return;
        Player player = event.getPlayer();
        playEffects(player);
        player.sendMessage(String.valueOf(player.getVelocity().length()));
        if (player.getVelocity().length() < 2) {
            Vector force = player.getLocation().getDirection().multiply(0.02);
            force.setY(force.getY() * 0.5);
            player.setVelocity(player.getVelocity().add(force));
        }
    }

    private void playEffects(Player player) {
        player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 1);
        if (player.getVelocity().length() > 2) {
            player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 1);
        }
    }
}

package lqw.aircombat.move;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class Dynamic implements Listener {
    @EventHandler
    public void onPlayGlide(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        player.setLevel(0);
        player.setExp(0);
        if (!event.getPlayer().isGliding()) {
//            player.setLevel(Math.min(500, player.getLevel() + 1));
            return;
        }
//        if (player.getLevel() == 0) return;
        playEffects(player);
        player.setLevel((int) (player.getVelocity().length() * 1000));

//        player.setLevel(Math.max(0, player.getLevel() - 1));
        Vector velocity = player.getVelocity();
        double maxSpeed = 1.7;
        if (velocity.length() < maxSpeed) {
            double rate = 0.05;
            rate = rate + -rate * (velocity.length() / maxSpeed);
            player.setExp((float) Math.min((rate * 1.5 / 0.05), 1f));
            Vector force = player.getLocation().getDirection().multiply(rate);
            force.setY(force.getY() * 0.2);
            player.setVelocity(player.getVelocity().add(force));
        }
    }

    private void playEffects(Player player) {
//        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 1);
        if (player.getVelocity().length() > 1.5) {
            player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 1);
        }
        if (player.getVelocity().length() > 2) {
            player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 1);
        }
    }

    @EventHandler
    public void onPlayerAddForce(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK &&
                event.getAction() != Action.LEFT_CLICK_AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (!player.isGliding()) return;
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() != Material.FIREWORK_ROCKET) return;

        event.setCancelled(true);
        player.setVelocity(player.getLocation().getDirection().multiply(Math.max(player.getVelocity().length() * 2,
                1)));
    }

    @EventHandler
    public void onPlayerBreakSand(BlockBreakEvent event) {
        Player player = event.getPlayer();
//        if (event.getBlock().getType() == Material.SAND) {
//            event.getBlock().setType(Material.AIR);
//            player.setLevel(Math.min(500, player.getLevel() + 50));
//        }
    }
}
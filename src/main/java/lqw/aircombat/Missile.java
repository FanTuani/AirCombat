package lqw.aircombat;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Missile implements Listener {
    @EventHandler
    public void whenMissileLaunched(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() != Material.COMPASS) return;

        Arrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection());
        arrow.setGravity(false);
        arrow.setGlowing(true);
        arrow.setCustomName("Missile");
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1.5f);
        player.playSound(player.getLocation(), Sound.BLOCK_METAL_BREAK, 1, 1);
        player.playSound(player.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_BREAK, 1, 1);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                arrow.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, arrow.getLocation(), 1, 0, 0, 0, 0);
                if (ticks >= 20) {
                    List<Entity> list = arrow.getNearbyEntities(50, 50, 50);
                    double nearestDistance = 500000;
                    LivingEntity nearestTarget = null;
                    for (Entity target : list) {
                        if (!(target instanceof LivingEntity)) continue;
                        if (target == player) continue;
                        if (target.isDead()) return;
                        if (arrow.getLocation().distance(target.getLocation()) < nearestDistance) {
                            nearestDistance = arrow.getLocation().distance(target.getLocation());
                            nearestTarget = (LivingEntity) target;
                        }
                    }
                    Location arrowLoc = arrow.getLocation();
                    if (nearestTarget != null) { // target locked
                        if (nearestTarget instanceof Player) {
                            Player targetPlayer = (Player) nearestTarget;
                            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 0.5f,
                                    2);
                            if (ticks % 4 < 2)
                                targetPlayer.sendActionBar(ChatColor.RED + "YOU ARE LOCKED BY MISSILE!");
                            else targetPlayer.sendActionBar(ChatColor.BLUE + "YOU ARE LOCKED BY MISSILE!");
                        }

                        arrow.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, arrowLoc, 10, 0, 0, 0, 0.1);
                        arrow.getWorld().spawnParticle(Particle.FLAME, arrowLoc, 10, 0, 0, 0, 0.1);
                        arrow.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, arrowLoc, 5, 0.5, 0.5, 0.5, 0);

                        Vector vector = nearestTarget.getEyeLocation().subtract(arrow.getLocation()).toVector();
                        arrow.setVelocity(vector.normalize().multiply(1.5)); // missile speed
                        arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 2, 1);
                    }
                    if (!arrow.getNearbyEntities(1, 1, 1).isEmpty() || !arrow.isValid() || arrow.isInBlock()) {
                        arrow.getWorld().createExplosion(arrowLoc, 4); // boom

                        arrow.getWorld().spawnParticle(Particle.CRIT_MAGIC, arrowLoc, 100, 2, 2, 2, 1);
                        arrow.getWorld().spawnParticle(Particle.CRIT, arrowLoc, 100, 2, 2, 2, 1);
                        arrow.getWorld().spawnParticle(Particle.FALLING_LAVA, arrowLoc, 100, 2, 2, 2, 1);
                        arrow.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, arrowLoc, 100, 2, 2, 2, 1);

                        arrow.remove();
                        cancel();
                    }
                }
                if (ticks >= 600) {
                    arrow.remove();
                    cancel();
                }
                ticks++;
            }
        }.runTaskTimer(AirCombat.instance, 0, 1);
    }
}

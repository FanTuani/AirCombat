package lqw.aircombat.combat;

import lqw.aircombat.AirCombat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class Cannon implements Listener {
    @EventHandler
    public void whenLaunchCannon(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&
                event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() != Material.FLINT) return;

        new BukkitRunnable() {
            int cnt = 0;

            @Override
            public void run() {
                if (cnt++ > 3) cancel();
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 5, 1);
                Location startLoc = player.getEyeLocation();
                startLoc.setY(startLoc.getY() - 0.3);
                Arrow arrow = player.getWorld().spawn(startLoc, Arrow.class);
                arrow.setVelocity(player.getLocation().getDirection().multiply(5));
                arrow.setGravity(false);
                arrow.setShooter(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        arrow.remove();
                    }
                }.runTaskLater(AirCombat.instance, 60);
            }
        }.runTaskTimer(AirCombat.instance, 0, 1);
    }
}

package lqw.aircombat.combat;

import lqw.aircombat.AirCombat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Bombing implements Listener {
    @EventHandler
    public void onPlayerBombing(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK &&
                event.getAction() != Action.LEFT_CLICK_AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() != Material.FLINT) return;

        TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
        tnt.setVelocity(player.getVelocity().multiply(1.1));
        tnt.setVelocity(tnt.getVelocity().add(new Vector(0, -0.3, 0)));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 3, 0.6f);
        tnt.setFuseTicks(1000);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        Location loc = tnt.getLocation().clone();
                        loc.setX(tnt.getLocation().getX() + x);
                        loc.setY(tnt.getLocation().getY() + y);
                        if (loc.getBlock().getType() != Material.AIR) {
                            tnt.setFuseTicks(0);
                            cancel();
                            return;
                        }
                    }
                }
            }
        }.runTaskTimer(AirCombat.instance, 0, 1);
    }
}

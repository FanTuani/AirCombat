package lqw.aircombat.props;

import lqw.aircombat.AirCombat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
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

import java.util.ArrayList;
import java.util.Random;

public class DecoyMissile implements Listener {
    @EventHandler
    public void onDecoyEnabled(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() != Material.COMPASS) return;

        for (int i = 0; i < 15; i++) {
            Location loc = player.getLocation().clone();
            Vector rdVec = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);

            ArrayList<EntityType> entityTypes = new ArrayList<>();
            entityTypes.add(EntityType.VILLAGER);
            entityTypes.add(EntityType.CAT);

            EntityType decoyType = entityTypes.get(new Random().nextInt(entityTypes.size()));
            LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(loc.add(rdVec.normalize()), decoyType);

            Vector vector = entity.getLocation().subtract(player.getLocation()).toVector();
            entity.setVelocity(vector.normalize().multiply(1.5));

            entity.setGlowing(true);
//            entity.setFallDistance(2000);
            entity.setHealth(0.1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.remove();
                }
            }.runTaskLater(AirCombat.instance, 100);
        }
        player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 200, 2, 2, 2);
    }
}

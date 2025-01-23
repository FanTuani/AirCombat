package lqw.aircombat.move;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import lqw.aircombat.AirCombat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MAN implements Listener {


    Map<UUID, Boolean> map = new HashMap<>(), map2 = new HashMap<>(), isFlying = new HashMap<>();


    @EventHandler
    public void onPlayerNotFly(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(player.isGliding())isFlying.put(player.getUniqueId(), true);
        if(!player.isGliding() && isFlying.get(player.getUniqueId())){
            new BukkitRunnable(){
                @Override
                public void run() {
                    isFlying.put(player.getUniqueId(), false);
                }
            }.runTaskLater(AirCombat.instance, 1);
        }
    }


    @EventHandler
    public void onPlayerHit(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            if(event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION )
                map.put(event.getEntity().getUniqueId(), false);
            else{
                if(map2.get(event.getEntity().getUniqueId()) ){
                    map.put(event.getEntity().getUniqueId(), false);
                    map2.put(event.getEntity().getUniqueId(), false);
                }
                else{
                    map2.put(event.getEntity().getUniqueId(), true);
                }
            }
        }
        if(event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL ||
                (event.getCause() == EntityDamageEvent.DamageCause.FALL && isFlying.get(event.getEntity().getUniqueId()))
                && event.getDamage() > 3){
            Player player = (Player) event.getEntity();
            map.put(player.getUniqueId(), true);
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.getWorld().createExplosion(player.getLocation(), 50, false, false);
                }
            }.runTaskLater(AirCombat.instance, 1);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(map.get(event.getEntity().getUniqueId())){
            event.deathMessage(Component.text(event.getEntity().getName() + "坠机了"));
            map.put(event.getEntity().getUniqueId(), false);
        }
    }
}

package lqw.aircombat;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class LQW implements Listener {
    public static HashMap<Player, Vector> lastVecLoc = new HashMap<>();
    public static HashMap<Player, Vector> nowVelocity = new HashMap<>();

    private static class ItemKind{
        public Material material;
        public Integer index;
        public ItemKind(Material newMaterial, int newIndex){
            material = newMaterial;
            index = newIndex;
        }
    }

    private static HashMap<UUID, ItemKind> onlyItemIndex = new HashMap<>();
    private static HashMap<UUID, Material> onlyItem = new HashMap<>();
    private static Set<UUID>noInteract = new HashSet<>();

    @EventHandler
    public void calculatePlayerVelocity(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Vector nowVecLoc = player.getLocation().toVector();
        nowVelocity.put(player, nowVecLoc.subtract(lastVecLoc.get(player)));
        lastVecLoc.put(player, player.getLocation().toVector());
    }

    public static Vector getPlayerVelocity(Player player) {
        return nowVelocity.get(player);
    }

    public static Location aimSpace(Location startLoc, double maxDistance) {
        Vector dir = startLoc.getDirection(), loc = startLoc.toVector(), start = startLoc.toVector();
        World world = startLoc.getWorld();
        while (true) {
            loc.add(dir);
            if (!world.getBlockAt(loc.toLocation(world)).isPassable()) {
                loc.subtract(dir);
                return loc.toLocation(world);
            }
            if (start.distance(loc) >= maxDistance) {
                return loc.toLocation(world);
            }
        }
    }

    public static void indicatorCycle(Player player, Location centerLoc, double dis){
        Vector delta = new Vector(0, 0.1, 0);
        Location loc = centerLoc.clone();
        for(int i = 0;i < 50; i++, loc.add(delta)){
            player.spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0, 0.1);
        }
        delta.setY(0);delta.setX(dis);
        loc = centerLoc.clone();
        for(int i = 0; i <= 360; i ++){
            delta.rotateAroundY(Math.toRadians(i));
            player.spawnParticle(Particle.CRIT, loc.clone().add(delta), 1, 0, 0, 0, 0.2);
        }
    }

    public static void indicatorCycle(Player player, Location centerLoc, double dis,  Particle particle){
        Vector delta = new Vector(0, 0.1, 0);
        Location loc = centerLoc.clone();
        for(int i = 0;i < 50; i++, loc.add(delta)){
            player.spawnParticle(particle, loc, 1, 0, 0, 0, 0.1);
        }
        delta.setY(0);delta.setX(dis);
        loc = centerLoc.clone();
        for(int i = 0; i <= 360; i ++){
            delta.rotateAroundY(Math.toRadians(i));
            player.spawnParticle(particle, loc.clone().add(delta), 1, 0, 0, 0, 0.2);
        }
    }
    private static boolean onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if(noInteract.contains(id)){
            return true;
        }
        PlayerInventory inventory = player.getInventory();
        if(onlyItem.containsKey(id)){
            if(inventory.getItemInMainHand().getType() != onlyItem.get(id)){
                return true;
            }
        }
        if(onlyItemIndex.containsKey(id)){
            if(inventory.getItemInMainHand().getType() != onlyItemIndex.get(id).material
                    || inventory.getHeldItemSlot() != onlyItemIndex.get(id).index){
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

    public static boolean isNotUsing(PlayerInteractEvent event, String material){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() !=Action.RIGHT_CLICK_AIR)return true;
        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.valueOf(material))return true;
        if(event.getHand() != EquipmentSlot.HAND)return true;
        if(onPlayerInteract(event))return true;
        return false;
    }
    public static boolean isNotUsing(PlayerInteractEvent event, String material, int index){
        if(onPlayerInteract(event))return true;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() !=Action.RIGHT_CLICK_AIR)return true;
        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.valueOf(material))return true;
        if(event.getHand() != EquipmentSlot.HAND)return true;
        if(event.getPlayer().getInventory().getHeldItemSlot() != index)return true;
        return false;

    }

    public static void addNoInteractPlayer(Player player, int tick){
        UUID id = player.getUniqueId();
        noInteract.add(id);
        new BukkitRunnable(){
            @Override
            public void run() {
                noInteract.remove(id);
            }
        }.runTaskLater(AirCombat.instance, tick);
    }

    public static void addNoInteractPlayer(Player player){
        UUID id = player.getUniqueId();
        noInteract.add(id);
    }

    public static void addNoInteractPlayer(Player player, int tick,Material material){
        UUID id = player.getUniqueId();
        onlyItem.put(id, material);
        new BukkitRunnable(){
            @Override
            public void run() {
                onlyItem.remove(id);
            }
        }.runTaskLater(AirCombat.instance, tick);
    }

    public static void addNoInteractPlayer(Player player, Material material){
        UUID id = player.getUniqueId();
        onlyItem.put(id, material);
    }

    public static void addNoInteractPlayer(Player player, int tick, Material material, int index){
        UUID id = player.getUniqueId();
        onlyItemIndex.put(id, new ItemKind(material, index));
        new BukkitRunnable(){
            @Override
            public void run() {
                onlyItemIndex.remove(id);
            }
        }.runTaskLater(AirCombat.instance, tick);
    }

    public static void addNoInteractPlayer(Player player, Material material, int index){
        UUID id = player.getUniqueId();
        onlyItemIndex.put(id, new ItemKind(material, index));
    }

    public static void removeNoInteractPlayer(Player player){
        UUID id = player.getUniqueId();
        noInteract.remove(id);
    }

    public static void removeNoInteractPlayer(Player player, Material material){
        UUID id = player.getUniqueId();
        onlyItem.remove(id);
    }

    public static void removeNoInteractPlayer(Player player, Material material, int index){
        UUID id = player.getUniqueId();
        onlyItemIndex.remove(id);
    }

//    public static boolean canSee(Entity entity1, Entity entity2){
//        Vector dir = entity1.getLocation().toVector().subtract(entity2.getLocation().toVector()).normalize().multiply(0.5);
//        for(Location loc = entity2.getLocation(); loc.subtract(entity2.getLocation()).length()>1; loc.add(dir)){
//            if(!loc.getWorld().getBlockAt(loc).isPassable()){
//                return false;
//            }
//        }
//        return true;
//    }

    @EventHandler
    public void test(PlayerInteractEvent event) {
        if (isNotUsing(event, Material.STICK.name())) return;
        Player player = event.getPlayer();
        player.sendMessage(event.getAction().name());
        player.damage(1, player);
    }
    @EventHandler
    public void preMisHit(EntityDamageByEntityEvent event){
        Entity dam = event.getDamager(), en = event.getEntity();
        if(dam instanceof Player && en instanceof Player){
            Player damager = (Player) dam, entity = (Player) en;
//            if(damager.get)
        }
    }

    public static float signedAngle(Vector a, Vector b){
        float angle = a.angle(b);
        float y = (float)a.getCrossProduct(b).getY();
        if(y < 0)return angle;
        else return -angle;
    }
}

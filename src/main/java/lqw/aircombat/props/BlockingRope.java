package lqw.aircombat.props;

import lqw.aircombat.AirCombat;
import lqw.aircombat.LQW;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class BlockingRope implements Listener {
    class Rope{
        Vector start, end, d1;
        double length;

        public Player player;
        World world;
        public Rope(Vector a, Vector b, World w, Player p){
            start = a;
            end = b;
            length = a.clone().subtract(b).length();
            d1 = a.clone().subtract(b);
            world = w;
            player = p;
        }
        public boolean near(Vector loc, World w){
            if(w.getName() != world.getName())return false;
            Vector d2 = start.clone().subtract(loc);
            double sita = d1.angle(d2), sin;
            sin = Math.sin(sita);
            return (d2.length() * sin < 1 && loc.clone().subtract(start).angle(loc.clone().subtract(end)) > 2);
        }

        public void Remind(Player player){
            Random random = new Random();
            for(int i = 0; i < (int)length * 10; i ++){
                Vector loc = end.clone().add(d1.clone().multiply(random.nextDouble()));
                player.spawnParticle(Particle.SOUL_FIRE_FLAME, new Location(world, loc.getX(), loc.getY(), loc.getZ()), 2, 0.2, 0.2, 0.2, 0);
            }
        }

    }

    Queue<Rope>q = new ArrayDeque<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(LQW.isNotUsing(event, "WHEAT"))return;
        Player player = event.getPlayer();
        World  world = player.getWorld();
        Vector d = new Vector(0, -1, 0), loc = player.getEyeLocation().toVector(), loc1;
        for(int i = 0; i < 50; i ++){
            if((world.getBlockAt(new Location(world, loc.getX(), loc.getY(), loc.getZ())).getType().isSolid()))break;
            loc.add(d.clone().multiply(-1));
        }
        loc1 = loc.clone();
        for(int i = 0; i < 200; i ++){
            loc1.add(d);
            if((world.getBlockAt(new Location(world, loc1.getX(), loc1.getY(), loc1.getZ())).getType().isSolid()))break;
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                q.add(new Rope(loc, loc1, world, player));
            }
        }.runTaskLater(AirCombat.instance, 10);
        new BukkitRunnable(){
            @Override
            public void run() {
                q.remove();
            }
        }.runTaskLater(AirCombat.instance, 610);
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Vector loc = player.getLocation().toVector();
        for(Rope rope :q){
            if(rope.near(loc, player.getWorld()) || rope.near(player.getEyeLocation().toVector(), player.getWorld())){
                player.damage(5, rope.player);
                player.setVelocity(player.getVelocity().multiply(0.1));
            }
            rope.Remind(player);
        }
    }

}

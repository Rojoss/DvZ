package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Fireball extends MobAbility {

    public Fireball() {
        super();
        ability = Ability.FIREBALL;
        castItem = new DvzItem(Material.FIREBALL, 1, (short)0, 198, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        SmallFireball smallFireball = player.launchProjectile(SmallFireball.class);
        smallFireball.setVelocity(player.getVelocity());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof SmallFireball)) {
            return;
        }

        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if(!dvz.getPM().getPlayer(player).isDwarf()) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(getDoubleOption("damage"));
        player.setFireTicks(getIntOption("fireDuration"));
    }

    @EventHandler
    public void onEntityExplode(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof SmallFireball)) {
            return;
        }

        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        Location l = event.getEntity().getLocation();

        if (!getDvzClasses().contains(dvz.getPM().getPlayer(player).getPlayerClass())) {
            return;
        }

        int radius = getIntOption("fire-radius");

        for(int x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++) {
            for(int y = l.getBlockY() - radius; y <= l.getBlockY() + radius; y++) {
                for(int z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++) {
                    Block b = l.getWorld().getBlockAt(x, y, z);
                    if(b.getType().isSolid()) {
                        if (b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                            if (CWUtil.randomFloat() <= getDoubleOption("fire-chance")) {
                                b.getRelative(BlockFace.UP).setType(Material.FIRE);
                                //TODO: Add sound and particle effects.
                            }
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}

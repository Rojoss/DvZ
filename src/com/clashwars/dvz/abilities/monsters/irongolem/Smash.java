package com.clashwars.dvz.abilities.monsters.irongolem;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Smash extends BaseAbility {

    public Smash() {
        super();
        ability = Ability.SMASH;
        castItem = new DvzItem(Material.FIREWORK_CHARGE, 1, (short)0, displayName, 100, -1);

        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (player.getLocation().getPitch() >= 0) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou can't smash blocks below you! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        List<Material> undestroyableBlocks = dvz.getUndestroyableBlocks();
        Block block = triggerLoc.getBlock();
        Location loc = block.getLocation();

        int radius = 3;
        for (int x = loc.getBlockX() - radius; x < loc.getBlockX() + radius; x++) {
            for (int y = loc.getBlockY() - radius; y < loc.getBlockY() + radius; y++) {
                for (int z = loc.getBlockZ() - radius; z < loc.getBlockZ() + radius; z++) {
                    Block b = loc.getWorld().getBlockAt(x,y,z);
                    if (undestroyableBlocks.contains(block.getType())) {
                        continue;
                    }
                    double distance = b.getLocation().distance(loc);
                    if (distance > radius) {
                        continue;
                    }
                    if (distance > radius-1  && CWUtil.randomFloat() > 0.5f) {
                        continue;
                    }

                    FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
                    Vector dir = b.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                    fallingBlock.setVelocity(dir.multiply(1.2f));
                    fallingBlock.setMetadata("smashblock", new FixedMetadataValue(dvz, player.getName()));

                    b.getWorld().playSound(b.getLocation(), Sound.ZOMBIE_WOOD, 0.1f, 2.2f - CWUtil.randomFloat());
                    ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.5f, 0.5f, 0, 5, b.getLocation().add(0.5f, 0.5f, 0.5f));

                    b.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler
    private void fallingBlockLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (!event.getEntity().hasMetadata("smashblock")) {
            return;
        }

        Block block = event.getBlock();
        OfflinePlayer caster = dvz.getServer().getOfflinePlayer(event.getEntity().getMetadata("smashblock").get(0).asString());

        block.getWorld().playSound(block.getLocation(), Sound.IRONGOLEM_WALK, 1, 2);
        block.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOOD, 0.1f, 2);
        ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.5f, 0.5f, 0, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));

        List<Player> players = CWUtil.getNearbyPlayers(block.getLocation(), dvz.getGM().getMonsterPower(0.5f, 2f));
        for (Player p : players) {
            if(dvz.getPM().getPlayer(p).isDwarf()) {

                if (caster != null && caster.isOnline()) {
                    Player casterP = (Player)caster;
                    double distance = p.getLocation().distance(casterP.getLocation());
                    new AbilityDmg(p, CWUtil.lerp(0.05d, 1, (double) distance / 8), ability, caster);
                } else {
                    new AbilityDmg(p, 0.5d, ability);
                }
            }
        }

    }
}

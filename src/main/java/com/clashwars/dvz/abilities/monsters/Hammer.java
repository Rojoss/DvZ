package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Hammer extends MobAbility {

    public Hammer() {
        super();
        ability = Ability.SUICIDE;
        displayName = "&6Hammer";
        description = "&7Hammer stone to break it!";
        usage = "&7or spam left click on stone to hammer it.";
        castItem = new CWItem(Material.TRIPWIRE_HOOK, 1, (short)0, displayName);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //--
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isCastItem(player.getItemInHand())) {
            return;
        }

        if (!canCast(player)) {
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        //TODO: check for destroyable blocks.

        event.setCancelled(true);
        if (event.getBlockFace() == BlockFace.DOWN || event.getBlockFace() == BlockFace.UP) {
            ParticleEffect.SMOKE.display(CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()), 0.3f, 0.0f, 0.3f, 0.00001f, 3);
        }
        if (event.getBlockFace() == BlockFace.NORTH || event.getBlockFace() == BlockFace.SOUTH) {
            ParticleEffect.SMOKE.display(CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()), 0.3f, 0.3f, 0.0f, 0.00001f, 3);
        }
        if (event.getBlockFace() == BlockFace.EAST || event.getBlockFace() == BlockFace.WEST) {
            ParticleEffect.SMOKE.display(CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()), 0.0f, 0.3f, 0.3f, 0.00001f, 3);
        }
        player.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOOD, 0.2f, 2.4f - CWUtil.randomFloat());
        if (CWUtil.randomFloat() < 0.03f) {
            event.getClickedBlock().setType(Material.AIR);
            ParticleEffect.LARGE_SMOKE.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.3f, 0.3f, 0.3f, 0.00001f, 15);
            ParticleEffect.CRIT.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.6f, 0.6f, 0.6f, 0.1f, 10);
            player.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 0.8f);
        }
    }
}

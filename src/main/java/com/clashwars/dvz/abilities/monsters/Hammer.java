package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Hammer extends MobAbility {

    public Hammer() {
        super();
        ability = Ability.HAMMER;
        castItem = new DvzItem(Material.TRIPWIRE_HOOK, 1, (short)0, displayName, 10, -1);
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

        //Check if block can be destroyed
        Block block = event.getClickedBlock();
        //TODO: Check for destroyable blocks.


        //Shrine destroying stuff
        ParticleEffect effect = ParticleEffect.SMOKE;
        if (block.getType() == Material.ENDER_PORTAL_FRAME) {
            player.sendMessage("ENDER_PORTAL_FRAME");
            ShrineBlock shrineBlock = dvz.getGM().getShrineBlock(block.getLocation());
            if (shrineBlock == null) {
                player.sendMessage("Shrine block is null");
                return;
            }
            if (dvz.getGM().getState() == GameState.MONSTERS) {
                if (shrineBlock.getType() == ShrineType.KEEP) {
                    player.sendMessage(CWUtil.formatCWMsg("&cYou have to destroy the shrine at the wall first!"));
                    return;
                }
            }
            player.sendMessage("damage");
            shrineBlock.damage();
            effect = ParticleEffect.WITCH_MAGIC;
        }

        event.setCancelled(true);
        if (event.getBlockFace() == BlockFace.DOWN || event.getBlockFace() == BlockFace.UP) {
            effect.display(CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()), 0.3f, 0.0f, 0.3f, 0.00001f, 3);
        }
        if (event.getBlockFace() == BlockFace.NORTH || event.getBlockFace() == BlockFace.SOUTH) {
            effect.display(CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()), 0.3f, 0.3f, 0.0f, 0.00001f, 3);
        }
        if (event.getBlockFace() == BlockFace.EAST || event.getBlockFace() == BlockFace.WEST) {
            effect.display(CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()), 0.0f, 0.3f, 0.3f, 0.00001f, 3);
        }
        player.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOOD, 0.1f, 2.2f - CWUtil.randomFloat());
        if (effect == ParticleEffect.SMOKE) {
            if (CWUtil.randomFloat() < getFloatOption("chance")) {
                event.getClickedBlock().setType(Material.AIR);
                ParticleEffect.LARGE_SMOKE.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.3f, 0.3f, 0.3f, 0.00001f, 15);
                ParticleEffect.CRIT.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.6f, 0.6f, 0.6f, 0.1f, 10);
                player.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 0.8f);
            }
        }
    }
}

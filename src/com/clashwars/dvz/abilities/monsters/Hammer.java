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
        if (block.getType() == Material.BEDROCK || block.getType() == Material.OBSIDIAN) {
            return;
        }


        //Shrine destroying stuff
        ParticleEffect effect = ParticleEffect.SMOKE_NORMAL;
        int effectAmt = 3;
        if (block.getType() == Material.ENDER_PORTAL_FRAME) {
            ShrineBlock shrineBlock = dvz.getGM().getShrineBlock(block.getLocation());
            if (shrineBlock == null) {
                return;
            }
            if (dvz.getGM().getState() == GameState.MONSTERS) {
                if (shrineBlock.getType() == ShrineType.KEEP_1 || shrineBlock.getType() == ShrineType.KEEP_2) {
                    player.sendMessage(CWUtil.formatCWMsg("&cYou have to destroy the shrine at the wall first!"));
                    return;
                }
            } else if (dvz.getGM().getState() == GameState.MONSTERS_WALL) {
                if (shrineBlock.getType() == ShrineType.KEEP_2) {
                    player.sendMessage(CWUtil.formatCWMsg("&cYou have to destroy the shrine at the bottom of the keep first!"));
                    return;
                }
            }

            shrineBlock.damage();
            effect = ParticleEffect.SPELL_WITCH;
            effectAmt = 8;
        }

        if (onCooldown(player)) {
            return;
        }

        if (event.getBlockFace() == BlockFace.DOWN || event.getBlockFace() == BlockFace.UP) {
            effect.display(0.3f, 0.0f, 0.3f, 0.00001f, effectAmt, CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()));
        }
        if (event.getBlockFace() == BlockFace.NORTH || event.getBlockFace() == BlockFace.SOUTH) {
            effect.display(0.3f, 0.3f, 0.0f, 0.00001f, effectAmt, CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()));
        }
        if (event.getBlockFace() == BlockFace.EAST || event.getBlockFace() == BlockFace.WEST) {
            effect.display(0.0f, 0.3f, 0.3f, 0.00001f, effectAmt, CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()));
        }
        player.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOOD, 0.1f, 2.2f - CWUtil.randomFloat());
        if (effect == ParticleEffect.SMOKE_NORMAL) {
            if (CWUtil.randomFloat() < getFloatOption("chance")) {
                if (event.getClickedBlock().getType() == Material.SMOOTH_BRICK && event.getClickedBlock().getData() == 0) {
                    event.getClickedBlock().setData((byte)(2));
                } else {
                    event.getClickedBlock().setType(Material.AIR);
                }
                ParticleEffect.SMOKE_LARGE.display(0.3f, 0.3f, 0.3f, 0.00001f, 15, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f));
                ParticleEffect.CRIT.display(0.6f, 0.6f, 0.6f, 0.1f, 10, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f));
                player.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 0.8f);
            }
        }
    }
}

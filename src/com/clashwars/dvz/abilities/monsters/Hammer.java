package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Hammer extends BaseAbility {

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
    public void interact(DelayedPlayerInteractEvent event) {
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
        if (block.getType() != Material.ENDER_PORTAL_FRAME && !Util.isDestroyable(block.getType())) {
            return;
        }

        if (Util.isProtected(block.getLocation().toVector())) {
            return;
        }


        //Shrine destroying stuff
        ParticleEffect effect = ParticleEffect.SMOKE_NORMAL;
        int effectAmt = 3;
        if (block.getType() == Material.ENDER_PORTAL_FRAME) {
            if (!Util.damageShrine(block.getLocation(), player, dvz.getCfg().SHRINE__DAMAGE_PER_HIT)) {
                return;
            }

            effect = ParticleEffect.SPELL_WITCH;
            effectAmt = 8;
        }

        if (onCooldown(player)) {
            return;
        }

        if (event.getBlockFace() == BlockFace.DOWN || event.getBlockFace() == BlockFace.UP) {
            effect.display(0.2f, 0.0f, 0.2f, 0.00001f, effectAmt, CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()));
        }
        if (event.getBlockFace() == BlockFace.NORTH || event.getBlockFace() == BlockFace.SOUTH) {
            effect.display(0.2f, 0.2f, 0.0f, 0.00001f, effectAmt, CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()));
        }
        if (event.getBlockFace() == BlockFace.EAST || event.getBlockFace() == BlockFace.WEST) {
            effect.display(0.0f, 0.2f, 0.2f, 0.00001f, effectAmt, CWUtil.getBlockCenterByBlockface(event.getClickedBlock().getLocation(), event.getBlockFace()));
        }
        player.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOOD, 0.1f, 2.2f - CWUtil.randomFloat());
        if (effect == ParticleEffect.SMOKE_NORMAL) {
            if (CWUtil.randomFloat() < dvz.getGM().getMonsterPower(0.05f) + 0.04f) {
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

    @EventHandler
    private void onDamageByEntity(EntityDamageByEntityEvent event) {
        //Hitting shrine armorstands/holograms.
        if (!(event.getEntity() instanceof ArmorStand)) {
            return;
        }
        Block block = event.getEntity().getLocation().getBlock();
        if (block.getType() != Material.ENDER_PORTAL_FRAME) {
            block = block.getRelative(BlockFace.UP);
            if (block.getType() != Material.ENDER_PORTAL_FRAME) {
                return;
            }
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();

        if (CooldownManager.interactCooldown(damager.getUniqueId(), 200l)) {
            return;
        }

        if (!isCastItem(damager.getItemInHand())) {
            return;
        }

        if (!canCast(damager)) {
            return;
        }

        if (!Util.damageShrine(block.getLocation(), damager, dvz.getCfg().SHRINE__DAMAGE_PER_HIT)) {
            return;
        }

        if (onCooldown(damager)) {
            return;
        }

        ParticleEffect.SPELL_WITCH.display(0.2f, 0.0f, 0.2f, 0.00001f, 8, block.getLocation().add(0.5f, 1, 0.5f));
        damager.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOOD, 0.1f, 2.2f - CWUtil.randomFloat());
    }

}

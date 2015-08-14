package com.clashwars.dvz.abilities.monsters.slime;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.classes.monsters.Slime;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.player.PlayerData;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Split extends BaseAbility {

    public Split() {
        super();
        ability = Ability.SPLIT;
        castItem = new DvzItem(Material.SLIME_BLOCK, 3, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        PlayerData data = cwp.getPlayerData();
        if (data.getSlimeSize() == 1) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou are too small to split! &4&l<<"));
            return;
        }
        if (onCooldown(player)) {
            return;
        }

        data.setSlimeSize(data.getSlimeSize()-1);
        cwp.setPlayerData(data);

        CWEntity slime = CWEntity.create(EntityType.SLIME, player.getLocation().add(0, 0.2f, 0));
        slime.setSize(data.getSlimeSize());
        slime.setName("&c" + player.getName());
        slime.setNameVisible(true);
        slime.setMaxHealth(Slime.getHealth(data.getSlimeSize()));
        slime.setHealth(Slime.getHealth(data.getSlimeSize()));

        if (!Slime.slimes.containsKey(player.getUniqueId())) {
            Slime.slimes.put(player.getUniqueId(), new ArrayList<org.bukkit.entity.Slime>());
        }
        List<org.bukkit.entity.Slime> slimes = Slime.slimes.get(player.getUniqueId());
        slimes.add((org.bukkit.entity.Slime)slime.entity());
        Slime.slimes.put(player.getUniqueId(), slimes);

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, data.getSlimeSize() - 1, true, false), true);

        Util.disguisePlayer(player.getName(), cwp.getPlayerClass().getClassClass().getDisguise() + " setSize " + data.getSlimeSize());
        player.setMaxHealth(Slime.getHealth(data.getSlimeSize()));

        ItemStack hand = player.getItemInHand();
        hand.setAmount(data.getSlimeSize());
        player.setItemInHand(hand);

        ParticleEffect.SLIME.display(1, 1, 1, 0, 50, player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.SLIME_ATTACK, 0.8f, 1);
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&2&l>> &a&lSPLIT! &2&l<<"));
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler
    private void merge(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof org.bukkit.entity.Slime)) {
            return;
        }
        org.bukkit.entity.Slime slime = (org.bukkit.entity.Slime)event.getRightClicked();

        Player owner = dvz.getServer().getPlayer(CWUtil.stripAllColor(slime.getCustomName()));
        if (owner == null) {
            return;
        }
        CWPlayer cwp = dvz.getPM().getPlayer(owner);

        if (cwp.getPlayerClass() != DvzClass.SLIME) {
            return;
        }

        if (!isCastItem(owner.getItemInHand())) {
            return;
        }

        if (!Slime.slimes.containsKey(owner.getUniqueId())) {
            CWUtil.sendActionBar(owner, CWUtil.integrateColor("&4&l>> &cThis is not your slime! &4&l<<"));
            return;
        }

        List<org.bukkit.entity.Slime> slimes = Slime.slimes.get(owner.getUniqueId());
        if (!slimes.contains(slime)) {
            CWUtil.sendActionBar(owner, CWUtil.integrateColor("&4&l>> &cThis is not your slime! &4&l<<"));
            return;
        }

        if (cwp.getPlayerData().getSlimeSize() != slime.getSize()) {
            CWUtil.sendActionBar(owner, CWUtil.integrateColor("&4&l>> &cYou can only merge with slimes of the same size! &4&l<<"));
            return;
        }

        PlayerData data = cwp.getPlayerData();
        data.setSlimeSize(data.getSlimeSize()+1);
        cwp.setPlayerData(data);

        owner.setMaxHealth(Slime.getHealth(data.getSlimeSize()));
        owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth() + slime.getHealth()));

        slimes.remove(slime);
        Slime.slimes.put(owner.getUniqueId(), slimes);
        slime.remove();

        ItemStack hand = owner.getItemInHand();
        hand.setAmount(data.getSlimeSize());
        owner.setItemInHand(hand);

        for (int i = 0; i < 9; i++) {
            ItemStack item = owner.getInventory().getItem(i);
            if (!Ability.SWAP.getAbilityClass().isCastItem(item)) {
                continue;
            }
            item.setAmount(1);
            owner.getInventory().setItem(i, item);
        }
        owner.updateInventory();

        owner.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, data.getSlimeSize() - 1, true, false), true);

        ParticleEffect.SLIME.display(1, 1, 1, 0, 30, owner.getLocation());
        owner.getWorld().playSound(owner.getLocation(), Sound.SLIME_ATTACK, 0.6f, 1);
        Util.disguisePlayer(owner.getName(), cwp.getPlayerClass().getClassClass().getDisguise() + " setSize " + data.getSlimeSize());
    }
}

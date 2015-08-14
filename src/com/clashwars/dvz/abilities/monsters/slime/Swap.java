package com.clashwars.dvz.abilities.monsters.slime;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.player.PlayerData;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Swap extends BaseAbility {

    public Swap() {
        super();
        ability = Ability.SWAP;
        castItem = new DvzItem(Material.NAME_TAG, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (!com.clashwars.dvz.classes.monsters.Slime.slimes.containsKey(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou have no other slimes! &4&l<<"));
            return;
        }

        List<Slime> slimes = com.clashwars.dvz.classes.monsters.Slime.slimes.get(player.getUniqueId());
        if (slimes == null || slimes.isEmpty()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou have no other slimes! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        Slime slime = slimes.get(player.getItemInHand().getAmount() - 1);
        if (slime != null && !slime.isDead()) {
            Location playerLoc = player.getLocation();
            PlayerData data = cwp.getPlayerData();
            double playerHealth = player.getHealth();
            double slimeHealth = slime.getHealth();

            //Swap locations
            player.teleport(slime.getLocation());
            slime.teleport(playerLoc);

            //Swap sizes
            int playerSlimeSize = data.getSlimeSize();
            data.setSlimeSize(slime.getSize());
            cwp.setPlayerData(data);
            Util.disguisePlayer(player.getName(), cwp.getPlayerClass().getClassClass().getDisguise() + " setSize " + data.getSlimeSize());
            slime.setSize(playerSlimeSize);

            //Swap health
            player.setMaxHealth(com.clashwars.dvz.classes.monsters.Slime.getHealth(data.getSlimeSize()));
            slime.setMaxHealth(com.clashwars.dvz.classes.monsters.Slime.getHealth(slime.getSize()));
            player.setHealth(Math.min(slimeHealth, player.getMaxHealth()));
            slime.setHealth(Math.min(playerHealth, slime.getMaxHealth()));

            //Jump boost for new size
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, data.getSlimeSize() - 1, true, false), true);

            //Update slime block amount for new size
            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (!Ability.SPLIT.getAbilityClass().isCastItem(item)) {
                    continue;
                }
                item.setAmount(data.getSlimeSize());
                player.getInventory().setItem(i, item);
            }
        }

        //Cycle to next slime ID.
        ItemStack hand = player.getItemInHand();
        if (hand.getAmount() == slimes.size()) {
            hand.setAmount(1);
        } else {
            hand.setAmount(hand.getAmount() + 1);
        }
        player.updateInventory();
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}

package com.clashwars.dvz.abilities.monsters.slime;

import com.clashwars.cwcore.damage.types.CustomDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.monsters.Slime;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.player.PlayerData;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Splash extends BaseAbility {

    public Splash() {
        super();
        ability = Ability.SPLASH;
        castItem = new DvzItem(Material.PRISMARINE_CRYSTALS, 1, (short)0, displayName, 50, -1);
    }


    @Override
    public void castAbility(Player player, Location triggerLoc) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        PlayerData data = cwp.getPlayerData();

        if (onCooldown(player)) {
            return;
        }

        boolean lastSlime = false;
        if (!Slime.slimes.containsKey(player.getUniqueId())) {
            lastSlime = true;
        } else if (Slime.slimes.get(player.getUniqueId()).size() < 1) {
            lastSlime = true;
        }

        if (lastSlime && !player.isSneaking()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou don't have any clones! &4&lYOU WILL DIE! &cShift to splash! &4&l<<"));
            return;
        }

        final List<Item> items = new ArrayList<Item>();
        int slimes = 50 * data.getSlimeSize();
        for (int i = 0; i < slimes; i++) {
            Item item = player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.SLIME_BLOCK));
            item.setVelocity(RandomUtils.getRandomCircleVector().setY(CWUtil.randomFloat(0.1f, 2.5f)).multiply(CWUtil.randomFloat(0.1f, 0.6f)));
            item.setPickupDelay(9999);
            items.add(item);
        }

        float radius = data.getSlimeSize() * 3 + dvz.getGM().getMonsterPower(0, 4);
        List<Player> nearbyPlayers = CWUtil.getNearbyPlayers(player.getLocation(), radius);
        for (Player p : nearbyPlayers) {
            if (dvz.getPM().getPlayer(p).isDwarf()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int)dvz.getGM().getMonsterPower(40, 100), data.getSlimeSize()));
                new AbilityDmg(p, data.getSlimeSize() * 2 + dvz.getGM().getMonsterPower(0, 3), Ability.SPLASH);
            }
        }

        player.getWorld().playSound(player.getLocation(), Sound.SLIME_ATTACK, 1, 0);
        player.getWorld().playSound(player.getLocation(), Sound.LAVA_POP, 1, 0);
        ParticleEffect.SLIME.display(radius/4, radius/4, radius/4, 0, Math.round(radius * 50), player.getLocation());


        if (lastSlime) {
            new CustomDmg(player, player.getMaxHealth(), "{0} splashed to death", "splashed");
        } else {
            List<org.bukkit.entity.Slime> slimeList = Slime.slimes.get(player.getUniqueId());
            org.bukkit.entity.Slime slime = slimeList.get(0);

            player.setMaxHealth(slime.getMaxHealth());
            player.setHealth(slime.getHealth());
            player.teleport(slime);
            data.setSlimeSize(slime.getSize());
            cwp.setPlayerData(data);

            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, data.getSlimeSize() - 1, true, false), true);
            Util.disguisePlayer(player.getName(), cwp.getPlayerClass().getClassClass().getDisguise() + " setSize " + data.getSlimeSize());

            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (Ability.SPLIT.getAbilityClass().isCastItem(item)) {
                    item.setAmount(data.getSlimeSize());
                    player.getInventory().setItem(i, item);
                }
                if (Ability.SWAP.getAbilityClass().isCastItem(item)) {
                    item.setAmount(1);
                    player.getInventory().setItem(i, item);
                }
            }
            player.updateInventory();

            slimeList.remove(0);
            Slime.slimes.put(player.getUniqueId(), slimeList);
            slime.remove();
        }

        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                for (Item item : items) {
                    if (item != null && item.isValid() && item.isOnGround()) {
                        item.remove();
                    }
                }
                count++;
                if (count == 10) {
                    for (Item item : items) {
                        if (item != null) {
                            item.remove();
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(dvz, 10, 10);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}

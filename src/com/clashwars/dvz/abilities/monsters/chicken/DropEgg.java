package com.clashwars.dvz.abilities.monsters.chicken;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.monsters.Chicken;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class DropEgg extends BaseAbility {

    public DropEgg() {
        super();
        ability = Ability.DROP_EGG;
        castItem = new DvzItem(Material.HUGE_MUSHROOM_1, 1, (short) 0, displayName, 140, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (!Chicken.eggs.containsKey(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou don't have an egg! &8(&7Use the &8Lay Egg &7ability!&8) &4&l<<"));
            return;
        }

        CWEntity egg = Chicken.eggs.get(player.getUniqueId());
        if (egg == null || egg.entity() == null || !egg.entity().isValid() || egg.entity().isDead()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou don't have an egg! &8(&7Use the &8Lay Egg &7ability!&8) &4&l<<"));
            return;
        }

        if (egg.entity().getCustomName().contains("grounded")) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cHatch your egg first! &8(&7Sneak near it&8) &4&l<<"));
            return;
        }

        if (egg.entity().getCustomName().contains("hatched")) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou have to pick your egg up first! &8(&7Click it!&8) &4&l<<"));
            return;
        }

        if (egg.entity().getCustomName().contains("dropped")) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou already dropped your egg! &4&l<<"));
            return;
        }

        int power = 0;
        String[] split = egg.entity().getCustomName().split("_");
        if (split.length > 1) {
            power = Integer.parseInt(split[1]);
        }

        egg.setArmorstandGravity(true);
        egg.setName("dropped_" + power);
        player.playSound(player.getLocation(), Sound.CHICKEN_WALK, 3, 0);
        ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.EGG, (byte)0), 0.5f, 0.5f, 0.5f, 0.1f, 20, egg.entity().getLocation(), 500);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}

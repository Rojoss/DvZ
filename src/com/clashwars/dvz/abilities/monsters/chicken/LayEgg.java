package com.clashwars.dvz.abilities.monsters.chicken;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.helpers.EntityTag;
import com.clashwars.cwcore.helpers.PoseType;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.monsters.Chicken;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.EulerAngle;

public class LayEgg extends BaseAbility {

    public LayEgg() {
        super();
        ability = Ability.LAY_EGG;
        castItem = new DvzItem(Material.HUGE_MUSHROOM_1, 1, (short) 0, displayName, 160, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (Chicken.eggs.containsKey(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou already have an egg! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        CWEntity stand = CWEntity.create(EntityType.ARMOR_STAND, player.getLocation());
        stand.setArmorstandGravity(true);
        stand.setArmorstandVisibility(false);
        stand.setSmall(true);
        stand.setName("grounded");
        stand.setNameVisible(false);
        stand.setHelmet(new CWItem(Material.HUGE_MUSHROOM_1));
        stand.setPose(PoseType.HEAD, new EulerAngle(Math.PI, 0, 0));

        Chicken.eggs.put(player.getUniqueId(), stand);

        CWUtil.sendActionBar(player, CWUtil.integrateColor("&6&l>> &eYou layed an egg! &8Hatch it &7and &8pick it up &7to &8drop it&7! &6&l<<"));
        player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 0f);
    }

    @EventHandler
    public void interactEgg(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) {
            return;
        }

        ArmorStand armorStand = (ArmorStand)event.getRightClicked();
        if (armorStand.getHelmet() == null || armorStand.getHelmet().getType() != Material.HUGE_MUSHROOM_1) {
            return;
        }

        event.setCancelled(true);

        if (!Chicken.eggs.containsKey(event.getPlayer().getUniqueId())) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cThis is not your egg! &4&l<<"));
            return;
        }

        CWEntity stand = Chicken.eggs.get(event.getPlayer().getUniqueId());
        if (stand == null || stand.entity() == null || !stand.entity().isValid()) {
            return;
        }
        if (!stand.entity().getUniqueId().equals(event.getRightClicked().getUniqueId())) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cThis is not your egg! &4&l<<"));
            return;
        }

        if (stand.entity().getName().contains("grounded")) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cHatch your egg first! &8(&7Sneak near it&8) &4&l<<"));
            return;
        }

        if (stand.entity().getName().contains("lifted")) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cYou're already holding this egg! &4&l<<"));
            return;
        }

        if (stand.entity().getName().contains("hatched")) {
            int power = 0;
            String[] split = stand.entity().getName().split("_");
            if (split.length > 1) {
                power = Integer.parseInt(split[1]);
            }

            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&6&l>> &eYou're now holding the egg! &6&l<<"));
            stand.entity().setCustomName("lifted_" + power);
            stand.setTag(EntityTag.MARKER, 1);
            stand.setArmorstandGravity(false);
            return;
        }
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}

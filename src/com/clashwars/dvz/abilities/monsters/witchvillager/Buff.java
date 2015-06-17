package com.clashwars.dvz.abilities.monsters.witchvillager;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class Buff extends BaseAbility {

    public Buff() {
        super();
        ability = Ability.BUFF;
        castItem = new DvzItem(Material.EMERALD, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void onCastItemGiven(Player player) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (!cwp.getPlayerData().isBuffUsed()) {
            castItem.giveToPlayer(player);
        } else {
        }
    }

    @EventHandler
    public void interact(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        if (!isCastItem(event.getPlayer().getItemInHand())) {
            return;
        }

        if (!canCast(event.getPlayer())) {
            return;
        }

        Player target = (Player) event.getRightClicked();
        CWPlayer cwt = dvz.getPM().getPlayer(target);
        if (cwt.getPlayerClass().getType() != ClassType.MONSTER ) {
            CWUtil.sendActionBar(event.getPlayer(),  CWUtil.integrateColor("&4&l>> &cYou can only give buffs to monsters! &4&l<<"));
            return;
        }

        if (cwt.getPlayerData().isBuffed()) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cThis player is already buffed! &4&l<<"));
            return;
        }

        if (onCooldown(event.getPlayer())) {
            return;
        }

        CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&9&l>> &6Buff given to &5" + target.getDisplayName() + "&6! &9&l<<"));
        CWUtil.sendActionBar(target, CWUtil.integrateColor("&9&l>> &6You received a buff from &5" + event.getPlayer().getDisplayName() + "&6! &9&l<<"));
        target.sendMessage(Util.formatMsg("&6You received a buff from &5" + event.getPlayer().getDisplayName() + "&6!"));
        cwt.getPlayerData().setBuffed(true);
        dvz.getPM().getPlayer(event.getPlayer()).getPlayerData().setBuffUsed(true);
        CWUtil.removeItemsFromHand(event.getPlayer(), 1);

        target.getWorld().playSound(target.getLocation(), Sound.ORB_PICKUP, 1, 0);
        target.getWorld().playSound(target.getLocation(), Sound.VILLAGER_YES, 0.8f, 1);
        ParticleEffect.VILLAGER_HAPPY.display(0.5f, 1f, 0.5f, 0, 50, target.getLocation().add(0, 1.5f, 0));
        ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1f, 0.5f, 0, 50, target.getLocation().add(0, 1.5f, 0));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerDmg(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        CWPlayer cwd = dvz.getPM().getPlayer((Player)event.getDamager());
        if (cwd == null) {
            return;
        }
        if (!cwd.getPlayerData().isBuffed()) {
            return;
        }
        ParticleEffect.VILLAGER_ANGRY.display(0.3f, 0.5f, 0.3f, 0, 3, cwd.getLocation().add(0,2,0));
        event.setDamage(event.getDamage() + dvz.getGM().getMonsterPower(1, 2));
    }
}

package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
            CWUtil.sendActionBar(event.getPlayer(),  CWUtil.integrateColor("&cYou can only give buffs to monsters!"));
            return;
        }

        if (cwt.getPlayerData().isBuffed()) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&cThis player is already buffed!"));
            return;
        }

        if (onCooldown(event.getPlayer())) {
            return;
        }

        CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&6Buff given to &5" + target.getDisplayName() + "&6!"));
        CWUtil.sendActionBar(target, CWUtil.integrateColor("&6You received a buff from &5" + event.getPlayer().getDisplayName() + "&6!"));
        target.sendMessage(Util.formatMsg("&6You received a buff from &5" + event.getPlayer().getDisplayName() + "&6!"));
        //cwt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0));
        cwt.getPlayerData().setBuffed(true);
        dvz.getPM().getPlayer(event.getPlayer()).getPlayerData().setBuffUsed(true);
        CWUtil.removeItemsFromHand(event.getPlayer(), 1);
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
        event.setDamage(event.getDamage() + 2);
    }
}

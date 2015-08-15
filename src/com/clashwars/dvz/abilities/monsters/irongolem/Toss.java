package com.clashwars.dvz.abilities.monsters.irongolem;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

public class Toss extends BaseAbility {

    public Toss() {
        super();
        ability = Ability.TOSS;
        castItem = new DvzItem(Material.IRON_INGOT, 1, (short)0, displayName, 100, -1);
    }


    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();
        if (!canCast(player) || !isCastItem(player.getItemInHand())) {
            return;
        }

        if (!dvz.getPM().getPlayer(target).isDwarf()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't toss monsters! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        new AbilityDmg(target, 0, ability, player);
        target.setVelocity(new Vector(0, dvz.getGM().getMonsterPower(1f, 1f), 0));
        player.getWorld().playSound(player.getLocation(), Sound.IRONGOLEM_THROW, 1, 0.6f);
        ParticleEffect.CLOUD.display(0.3f, 0.5f, 0.3f, 0, 20, target.getLocation());
    }

}

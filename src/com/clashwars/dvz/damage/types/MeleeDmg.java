package com.clashwars.dvz.damage.types;

import com.clashwars.cwcore.Debug;
import com.clashwars.dvz.damage.BaseDmg;
import com.clashwars.dvz.damage.CustomDamageEvent;
import com.clashwars.dvz.damage.DmgType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MeleeDmg extends BaseDmg {

    private OfflinePlayer attacker;

    public MeleeDmg(Player player, double damage, OfflinePlayer attacker) {
        super(player, damage);

        this.attacker = attacker;
        type = DmgType.MELEE;
        damage();
    }

    /*
    @Override
    public void damage() {
        if (player != null && player.isOnline()) {
            CustomDamageEvent event = new CustomDamageEvent((Player)player, damage, type, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled() && player.isOnline()) {
                ((Player)player).setHealth(Math.max(((Player) player).getHealth() - event.getDamage(), 0));
            }
        }
    }
    */

    @Override
    public String getDeathMsg() {
        String deathMsg = "{0} was killed by {1}";
        return deathMsg.replace("{0}", player.getName()).replace("{1}", attacker.getName());
    }

    @Override
    public String getDmgMsg(boolean damageTaken) {
        if (damageTaken) {
            return "hit by " + attacker.getName();
        }
        return "hit " + player.getName();
    }

    public OfflinePlayer getAttacker() {
        return attacker;
    }
}
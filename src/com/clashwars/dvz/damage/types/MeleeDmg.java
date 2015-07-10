package com.clashwars.dvz.damage.types;

import com.clashwars.dvz.damage.BaseDmg;
import com.clashwars.dvz.damage.DmgType;
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
package com.clashwars.dvz.damage.types;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.damage.BaseDmg;
import com.clashwars.dvz.damage.DmgType;
import org.bukkit.OfflinePlayer;

public class AbilityDmg extends BaseDmg {

    private Ability ability;
    private OfflinePlayer caster;

    public AbilityDmg(OfflinePlayer player, double damage, Ability ability) {
        super(player, damage);

        this.ability = ability;
        type = DmgType.ABILITY;

        damage();
    }

    public AbilityDmg(OfflinePlayer player, double damage, Ability ability, OfflinePlayer caster) {
        super(player, damage);

        this.ability = ability;
        this.caster = caster;
        type = DmgType.ABILITY;

        damage();
    }

    @Override
    public String getDeathMsg() {
        String deathMsg = "{0} died by {2}";
        if (!ability.getDeathMsg().isEmpty()) {
            deathMsg = ability.getDeathMsg();
        }

        deathMsg = deathMsg.replace("{0}", player.getName()).replace("{2}", ability.name());
        if (hasCaster()) {
            deathMsg = deathMsg.replace("{1}", caster.getName());
        }
        return deathMsg;
    }

    @Override
    public String getDmgMsg(boolean damageTaken) {
        if (damageTaken) {
            if (caster != null) {
                return caster.getName() + "'s " + CWUtil.stripAllColor(ability.getAbilityClass().getDisplayName()).toLowerCase();
            }
            return CWUtil.stripAllColor(ability.getAbilityClass().getDisplayName()).toLowerCase();
        }
        return CWUtil.stripAllColor(ability.getAbilityClass().getDisplayName()).toLowerCase() + " hit " + player.getName();
    }

    public Ability getAbility() {
        return ability;
    }

    public OfflinePlayer getCaster() {
        return caster;
    }

    public boolean hasCaster() {
        return caster != null;
    }
}

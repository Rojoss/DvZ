package com.clashwars.dvz.damage.log;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.damage.CustomDamageEvent;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.damage.types.MeleeDmg;
import com.clashwars.dvz.damage.types.RangedDmg;

import java.util.*;

public class DamageLog {

    public UUID logOwner;
    public String deathMsg;
    public DvzClass deathClass;
    public Long deathTime;
    public List<DamageLogEntry> log = new ArrayList<DamageLogEntry>();

    public DamageLog(UUID logOwner) {
        this.logOwner = logOwner;
    }

    public void updateLog(CustomDamageEvent event) {
        if (event.getPlayer().getUniqueId().equals(logOwner)) {
            log.add(new DamageLogEntry(event.getDmgClass(), event.getPlayer().getHealth(), true));
        }

        if (event.getDmgClass() instanceof MeleeDmg) {
            MeleeDmg meleeDmg = (MeleeDmg)event.getDmgClass();
            if (meleeDmg.getAttacker().getUniqueId().equals(logOwner)) {
                log.add(new DamageLogEntry(event.getDmgClass(), event.getPlayer().getHealth(), false));
            }
        }
        if (event.getDmgClass() instanceof RangedDmg) {
            RangedDmg rangedDmg = (RangedDmg)event.getDmgClass();
            if (rangedDmg.getShooter().getUniqueId().equals(logOwner)) {
                log.add(new DamageLogEntry(event.getDmgClass(), event.getPlayer().getHealth(), false));
            }
        }
        if (event.getDmgClass() instanceof AbilityDmg) {
            AbilityDmg abilityDmg = (AbilityDmg)event.getDmgClass();
            if (abilityDmg.hasCaster() && abilityDmg.getCaster().getUniqueId().equals(logOwner)) {
                log.add(new DamageLogEntry(event.getDmgClass(), event.getPlayer().getHealth(), false));
            }
        }
    }

    public List<String> getDmgMessages() {
        List<String> logMessages = new ArrayList<String>();
        for (DamageLogEntry logEntry : log) {
            if (logEntry.dmgClass.getDmg() == 0) {
                continue;
            }
            if (logEntry.dmgTaken) {
                logMessages.add("&4&l-" + CWUtil.round((float)logEntry.dmgClass.getDmg(), 2) + " &c" + logEntry.dmgClass.getDmgMsg(logEntry.dmgTaken) + " &8[&6" +
                        CWUtil.round((float)logEntry.health, 2) + "&7>&e" + CWUtil.round((float)(logEntry.health - logEntry.dmgClass.getDmg()), 2) + "&8]");
            } else {
                logMessages.add("&4&l-" + CWUtil.round((float)logEntry.dmgClass.getDmg(), 2) + " &a" + logEntry.dmgClass.getDmgMsg(logEntry.dmgTaken).replace(" by", "") + " &8[&6" +
                        CWUtil.round((float)logEntry.health, 2) + "&7>&e" + CWUtil.round((float)(logEntry.health - logEntry.dmgClass.getDmg()), 2) + "&8]");
            }
        }
        return logMessages;
    }

}

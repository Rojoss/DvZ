package com.clashwars.dvz.damage.log;

import com.clashwars.cwcore.Debug;
import com.clashwars.dvz.damage.CustomDamageEvent;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.damage.types.MeleeDmg;
import com.clashwars.dvz.damage.types.RangedDmg;

import java.util.*;

public class DamageLog {

    public UUID logOwner;
    public String deathMsg;
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

    public Set<String> getDmgMessages() {
        Set<String> logMessages = new HashSet<String>();
        for (DamageLogEntry logEntry : log) {
            if (logEntry.dmgTaken) {
                logMessages.add("&c" + "&8[&c" + logEntry.health + "&4>&c" + (logEntry.health - logEntry.dmgClass.getDmg()) + "&8]");
            } else {
                logMessages.add("&a" + "&8[&a" + logEntry.health + "&2>&a" + (logEntry.health - logEntry.dmgClass.getDmg()) + "&8]");
            }
        }
        return null;
    }

}

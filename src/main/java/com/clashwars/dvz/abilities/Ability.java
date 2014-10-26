package com.clashwars.dvz.abilities;

import com.clashwars.dvz.abilities.monsters.Hammer;
import com.clashwars.dvz.abilities.monsters.Suicide;
import com.clashwars.dvz.classes.DvZClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Ability {
    BASE(DvZClass.DWARF, new BaseAbility(), new String[] {}),
    SUICIDE(DvZClass.MONSTER, new Suicide(), new String[] {}),
    HAMMER(DvZClass.MONSTER, new Hammer(), new String[] {});

    private DvZClass dvzClass;
    private BaseAbility abilityClass;
    private List<String> aliases = new ArrayList<String>();

    Ability(DvZClass dvzClass, BaseAbility abilityClass, String[] aliases) {
        this.dvzClass = dvzClass;
        this.abilityClass = abilityClass;
        abilityClass.setAbility(this);
        this.aliases = Arrays.asList(aliases);
    }

    public DvZClass getDvzClass() {
        return dvzClass;
    }

    public BaseAbility getAbilityClass() {
        return abilityClass;
    }

    public List<String> getAliases() {
        return aliases;
    }

    //Get class by name or alias and return null if no class was found.
    public static Ability fromString(String name) {
        //First check by name.
        for (Ability c : values()) {
            if (c.toString().equalsIgnoreCase(name)) {
                return c;
            }
        }

        //Check by alias
        name = name.toLowerCase();
        for (Ability c : values()) {
            if (c.getAliases().contains(name)) {
                return c;
            }
        }
        return null;
    }

}

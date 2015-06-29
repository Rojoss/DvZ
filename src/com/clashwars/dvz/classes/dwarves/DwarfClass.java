package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.SwapType;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class DwarfClass extends BaseClass {

    protected static List<Ability> bonusAbilities = new ArrayList<Ability>();

    public DwarfClass() {
        super();
        dvzClass = DvzClass.DWARF;

        abilities.add(Ability.HEAL_POTION);
        abilities.add(Ability.SPEED_POTION);

        abilities.add(Ability.HORN);

        for (Ability ability : Ability.values()) {
            if (ability.getSwapType() == SwapType.DWARF_ABILITY) {
                abilities.add(ability);
                bonusAbilities.add(ability);
            }
        }
    }

    @EventHandler
    private void levelUp(CWPlayer.ClassLevelupEvent event) {
        CWPlayer cwp = event.getCWPlayer();
        cwp.sendMessage(Util.formatMsg("&a&lLEVEL UP! &7You are now level &a&l" + cwp.getPlayerData().getClassLvl() + "&7!"));
        ParticleEffect.VILLAGER_HAPPY.display(1,1.5f,1, 0, 75, cwp.getLocation().add(0,0.5f,0));
        cwp.getWorld().playSound(cwp.getLocation(), Sound.LEVEL_UP, 1, 1.4f);
        Ability ability = CWUtil.random(bonusAbilities);
        ability.getAbilityClass().getCastItem().giveToPlayer(cwp.getPlayer());
        cwp.sendMessage(Util.formatMsg("&2You received the &a&l" + ability.getAbilityClass().getDisplayName() + " &2ability!"));
    }
}

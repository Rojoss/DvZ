package com.clashwars.dvz.abilities;

import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.SwapType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class BaseAbility implements Listener {

    protected DvZ dvz = DvZ.inst();
    protected Ability ability;

    protected String displayName = "&7Unknown";
    protected DvzItem castItem = null;
    protected List<Action> castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));
    protected int cooldown = 0;

    //You can put \n for new lines in these.
    protected String description = "";
    protected String usage = "";


    public BaseAbility() {
        //--
    }


    public String getStrOption(String option) {
        return dvz.getAbilityCfg().getOption(ability, option);
    }

    public boolean getBoolOption(String option) {
        return Boolean.valueOf(getStrOption(option));
    }

    public int getIntOption(String option) {
        return Integer.valueOf(getStrOption(option));
    }

    public float getFloatOption(String option) {
        return Float.valueOf(getStrOption(option));
    }

    public double getDoubleOption(String option) {
        return Double.valueOf(getStrOption(option));
    }


    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public List<DvzClass> getDvzClasses() {
        if (ability == null) {
            return null;
        }
        List<DvzClass> classes = new ArrayList<DvzClass>();
        for (DvzClass dvzClass : DvzClass.values()) {
            if (dvzClass.getClassClass().getAbilities().contains(ability)) {
                classes.add(dvzClass);
            }
        }
        return classes;
    }

    public DvzItem getCastItem() {
        if (castItem != null) {
            castItem.setLore(new String[]{}).addLore("&aDesc&8: &7&o" + getDesc().replace(". ", ".|&7&o").replace("! ", "!|&7&o")).addLore("&aUsage&8: &7&o" + getUsage().replace(". ", ".|&7&o").replace("! ", "!|&7&o"));
            castItem.setName(getDisplayName());
            castItem.replaceLoreNewLines();
        }
        return castItem;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean hasCooldown() {
        return cooldown > 0;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDesc() {
        if (description == null || description.isEmpty()) {
            return "&8&oNo description available.";
        } else {
            return description;
        }
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getUsage() {
        if (usage == null || usage.isEmpty()) {
            if (castItem != null) {
                return "&7&oClick while holding this item to use it.";
            } else {
                return "&8&oNo info available.";
            }
        }
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }


    //If a ability can be casted by clicking with a item then this doesn't have to be called (see below)
    //If it has a different usage like by sneaking or attacking a player then just call this manually.
    //If you call it manually make sure to do a canCast() check.
    public void castAbility(Player player, Location triggerLoc) {
        player.sendMessage(Util.formatMsg("&4Error&8: &cMissing ability implementation."));
    }

    public boolean canCast(Player player) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (dvz.getGM().getState() == GameState.ENDED) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe game has ended! &4&l<<"));
            return false;
        }

        if (!dvz.getGM().isStarted()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe game hasn't started! &4&l<<"));
            return false;
        }

        //Check if player class has this ability.
        if (!getDvzClasses().contains(cwp.getPlayerClass())) {
            return false;
        }

        return true;
    }

    public boolean onCooldown(Player player) {
        return onCooldown(player, "", getCooldown(), 0);
    }

    public boolean onCooldown(Player player, String extraPrefix) {
        return onCooldown(player, extraPrefix, getCooldown(), 0);
    }

    public boolean onCooldown(Player player, String extraPrefix, int cooldownTime) {
        return onCooldown(player, extraPrefix, cooldownTime, 0);
    }

    public boolean onCooldown(Player player, float cooldownReduction) {
        return onCooldown(player, "", getCooldown(), cooldownReduction);
    }

    public boolean onCooldown(Player player, String extraPrefix, int cooldownTime, float cooldownReduction) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        cooldownReduction = 1 - Math.max(Math.min(cooldownReduction, 1), 0);

        if (hasCooldown()) {
            String tag = cwp.getPlayerClass().toString().toLowerCase() +  "-" + ability.toString().toLowerCase();
            if (cwp.getPlayerClass() == DvzClass.WITCH || cwp.getPlayerClass() == DvzClass.VILLAGER) {
                tag = "witch_villager-" + ability.toString().toLowerCase();
            }
            if (extraPrefix != null && !extraPrefix.isEmpty()) {
                tag += "-" + extraPrefix;
            }
            CooldownManager.Cooldown cd = cwp.getCDM().getCooldown(tag);
            if (cd == null) {
                cwp.getCDM().createCooldown(tag, Math.round(cooldownTime * cooldownReduction));
                return false;
            }
            if (!cd.onCooldown()) {
                cd.setTime(Math.round(cooldownTime * cooldownReduction));
                return false;
            }

            if (cd.getTimeLeft() >= 60000) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor(getDisplayName() + " &4&l> &7" + CWUtil.formatTime(cd.getTimeLeft(), "&c%M&4:&c%S&4m")));
            } else {
                CWUtil.sendActionBar(player, CWUtil.integrateColor(getDisplayName() + " &4&l> &7" + CWUtil.formatTime(cd.getTimeLeft(), "&c%S&4.&c%%%&4s")));
            }
            return true;
        }
        return false;
    }

    public boolean isCastItem(ItemStack item) {
        DvzItem castI = getCastItem();
        if (item == null || castI == null) {
            return false;
        }
        if (item.getType() != castI.getType()) {
            return false;
        }
        if (castI.hasItemMeta() && !item.hasItemMeta()) {
            return false;
        }
        if (castI.hasItemMeta() && item.hasItemMeta() && castItem.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
            if (!castI.getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                return false;
            }
        }
        return true;
    }

    //If the ability has a cast item then cast ability when using that item.
    protected void interact(PlayerInteractEvent event) {
        //This is only for abilities with cast items.
        if (castItem == null) {
            return;
        }

        //Compare items.
        if (!isCastItem(event.getItem())) {
            return;
        }

        //Swap/cycle items
        Player player = event.getPlayer();
        if (ability.getSwapType() != SwapType.NONE && event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            PlayerInventory inv = player.getInventory();

            LinkedHashMap<Ability, CWItem> swapAbilities = Ability.getSwapItems(ability.getSwapType());
            List<Ability> abilities = new ArrayList<>(swapAbilities.keySet());

            //Get index of the current held ability.
            int startIndex = 0;
            for (Ability a : abilities) {
                if (a == ability) {
                    break;
                }
                startIndex++;
            }

            //Go through the abilities 1 by 1 starting from the current ability index.
            int index = startIndex+1;
            if (index >= abilities.size()) {
                index = 0;
            }
            for (int i = 0; i < abilities.size(); i++) {
                Ability a = abilities.get(index);
                index++;
                if (a == ability) {
                    continue;
                }

                for (int invIndex = 9; invIndex < 36; invIndex++) {
                    if (invIndex == inv.getHeldItemSlot()) {
                        continue;
                    }
                    if (a.getAbilityClass().isCastItem(inv.getItem(invIndex))) {
                        ItemStack swapItem = inv.getItem(invIndex);
                        inv.setItem(invIndex, event.getItem());
                        inv.setItem(inv.getHeldItemSlot(), swapItem);
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 0.2f, 2f);
                        return;
                    }
                }

                if (index >= abilities.size()) {
                    index = 0;
                }
            }
        }

        //Compare the click action with allowed actions.
        if (castActions == null || !castActions.contains(event.getAction())) {
            return;
        }

        //Make sure we can cast it. (same class)
        if (!canCast(player)) {
            return;
        }

        event.setCancelled(true);
        player.updateInventory();

        //CAST! (we need to get the actual ability class when casting and not this BaseAbility cast method)
        Location loc = player.getLocation();
        if (event.getClickedBlock() != null) {
            loc = event.getClickedBlock().getLocation();
        }
        ability.getAbilityClass().castAbility(player, loc);
    }

    public void onCastItemGiven(Player player) {
        //--
    }
}

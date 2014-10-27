package com.clashwars.dvz.abilities;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.reflection.ReflectionHandler;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseAbility implements Listener {

    protected DvZ dvz = DvZ.inst();
    protected Ability ability;

    protected String displayName = "&7Unknown ability";
    protected CWItem castItem = null;
    protected List<Action> castActions = new ArrayList<Action>(Arrays.asList(new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}));
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

    public DvzClass getDvzClass() {
        if (ability == null) {
            return null;
        }
        return ability.getDvzClass();
    }

    public CWItem getCastItem() {
        if (castItem != null && castItem.hasItemMeta() && !castItem.getItemMeta().hasLore()) {
            List<String> lore = new ArrayList<String>();
            lore.addAll(CWUtil.splitToList(getDesc(), "\n", true));
            lore.addAll(CWUtil.splitToList(getUsage(), "\n", true));
            castItem.setLore(lore);
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
            return "&a&lDESC&8&l: &cNo description available.";
        } else {
            return "&a&lDESC&8&l: " + description;
        }
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getUsage() {
        if (usage == null || usage.isEmpty()) {
            if (castItem != null) {
                return "&a&lUSAGE&8&l: &7Click while holding this item to use it.";
            } else {
                return "&a&lUSAGE&8&l: &cNo info available.";
            }
        }
        return "&a&lUSAGE&8&l: " + usage;
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
        //TODO: Compare class with player class to make sure he can cast it.
        if (getDvzClass() == null) {
            return false;
        }

        if (hasCooldown()) {
            //TODO: Check cooldown.
            player.sendMessage("On cooldown message...");
            return false;
        }

        return true;
    }

    public boolean isCastItem(ItemStack item) {
        CWItem castI = getCastItem();
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

        //Compare the click action with allowed actions.
        if (castActions == null || !castActions.contains(event.getAction())) {
            return;
        }

        //Compare items.
        if (!isCastItem(event.getItem())) {
            return;
        }

        //Make sure we can cast it. (same class, no cooldown etc)
        if (!canCast(event.getPlayer())) {
            return;
        }

        //CAST! (we need to get the actual ability class when casting and not this BaseAbility cast method)
        event.setCancelled(true);
        Location loc = event.getPlayer().getLocation();
        if (event.getClickedBlock() != null) {
            loc = event.getClickedBlock().getLocation();
        }
        ability.getAbilityClass().castAbility(event.getPlayer(), loc);
    }
}

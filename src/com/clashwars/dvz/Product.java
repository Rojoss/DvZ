package com.clashwars.dvz;

import com.clashwars.cwcore.helpers.CWItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

public enum Product {
    CRACKED_STONE(new CWItem(Material.SMOOTH_BRICK, 1, (byte)2).setName("&9Cracked Stone").setLore(new String[] {"&7Cracked stone bricks that will break quite fast.", "&7Reinforce with the &9dye &7after you place it to make it stronger.", "&7Or just take reinforced bricks from the storage!"}), false),

    DIAMOND_ORE(new CWItem(Material.DIAMOND_ORE).setName("&8Raw Diamond").setLore(new String[]{"&7Raw diamond that needs to be cooked and polished.", "&8Place it in the &7furnace &8to get diamonds."}), false),
    GOLD_ORE(new CWItem(Material.GOLD_ORE).setName("&8Gold Ore").setLore(new String[] {"&7Unrefined gold ore ready to be smelted.", "&8Place it in the &7furnace &8to get gold bars."}), false),
    IRON_ORE(new CWItem(Material.IRON_ORE).setName("&8Iron Ore").setLore(new String[] {"&7Ore with a high concentration of iron.", "&8Place it in the &7furnace &8to get iron bars."}), false),
    DIAMOND(new CWItem(Material.DIAMOND).setName("&8Diamond").setLore(new String[] {"&7A crystal clear diamond of high value.", "&8Combine with &7sticks &8in your workbench for a battleaxe."}), false),
    GOLD_INGOT(new CWItem(Material.GOLD_INGOT).setName("&8Gold Bar").setLore(new String[] {"&7Pure refined shinny gold.", "&8Combine with &7sticks &8in your workbench for a fiery flail."}), false),
    IRON_INGOT(new CWItem(Material.IRON_INGOT).setName("&8Iron Bar").setLore(new String[] {"&7Very strong world class iron.", "&8Combine with &7sticks &8in your workbench for a greatsword."}), false),
    BATTLEAXE(new CWItem(Material.IRON_SWORD).setName("&8Dwarven Battleaxe").setLore(new String[] {"&7Heavy dwarven battleaxe.", "&8Decent damage output and high defence."}), true),
    GREATSWORD(new CWItem(Material.DIAMOND_AXE).setName("&8Dwarven Greatsword").setLore(new String[] {"&7Mighty dwarven greatsword.", "&8Weapon with the highest damage output."}), true),
    FIERY_FLAIL(new CWItem(Material.GOLD_SWORD).setName("&8Fiery Flail").setLore(new String[] {"&7Burning flail.", "&8Utility weapon."}), true),
    STONE_BRICK(new CWItem(Material.SMOOTH_BRICK).setName("&8Reinforced Brick").setLore(new String[] {"A very strong stone that has been reinforced.", "&8Can be used to build reinforced walls!"}), true),
    MINER_STICK(new CWItem(Material.STICK).setName("&8Stick").setLore(new String[] {"&7Strong stick great for making weapons.", "&7It will never break!", "&8Combine with &7iron,gold or diamonds &8to make weapons!"}), false),

    FLINT(new CWItem(Material.FLINT).setName("&aFlint").setLore(new String[] {"&7Sharp and strong flint great for arrow heads!", "&aCombine with &2feathers &7and &3sticks &ato make arrows and bows!"}), false),
    FEATHER(new CWItem(Material.FEATHER).setName("&aFeathers").setLore(new String[] {"&7Strong and clean feathers.", "&aCombine with &2flint &7and &2sticks &ato make arrows and bows!"}), false),
    BOW(new CWItem(Material.BOW).setName("&aDwarven Bow").setLore(new String[] {"&7Flexible bow with a strong string.", "&7Great for killing dragons!"}), true),
    ARROW(new CWItem(Material.ARROW).setName("&aArrow").setLore(new String[] {"&7Light arrow with high accuracy and high damage!", "&7Makes your bow more useful!"}), true),
    FLETCHER_STICK(new CWItem(Material.STICK).setName("&aStick").setLore(new String[] {"&7Flexible stick great for making bows.", "&7It will never break!", "&aCombine with &2flint &7and &2feathers &7to make bows and arrows!"}), false),

    WOOL(new CWItem(Material.WOOL).setName("&bWool").setLore(new String[] {"&7Strong wool from Merino sheep!", "&7Great for making strong armor!", "&bCombine with &3blue &band &3white dye", "&bon your workbench to make armor!"}), false),
    DYE_1(new CWItem(Material.INK_SACK, 1, (byte)7).setName("&bWhite Dye").setLore(new String[] {"&7Clean white dye.", "&7Great to use as a base for dying armor!", "&bcombine with &3blue dye &band &3wool", "&bon your workbench to make armor!"}), false),
    DYE_2(new CWItem(Material.INK_SACK, 1, (byte)12).setName("&bBlue Dye").setLore(new String[] {"&7Clear blue dye.", "&7Can change into any color!", "&bcombine with &3white dye &band &3wool", "&bon your workbench to make armor!"}), false),
    HELMET(new CWItem(Material.LEATHER_HELMET).setName("&bDwarven Helmet").setLore(new String[]{"&7One of the strongest and finest helmets!", "&7Will protect you from almost everything!"}), true),
    CHESTPLATE(new CWItem(Material.LEATHER_CHESTPLATE).setName("&bDwarven Tunic").setLore(new String[]{"&7A very comfy tunic.", "&7One size fits all!"}), true),
    LEGGINGS(new CWItem(Material.LEATHER_LEGGINGS).setName("&bDwarven Greaves").setLore(new String[] {"&7Comfortable greaves with high defence!"}), true),
    BOOTS(new CWItem(Material.LEATHER_BOOTS).setName("&bDwarven Boots").setLore(new String[] {"&7The finest walking boots!", "&7Jewelery for your feet!"}), true),

    MELON(new CWItem(Material.MELON).setName("&dMelon").setLore(new String[] {"&7Very juicy melon slices.", "&7Great for making health potions!", "&dDrop them in boiling water to make &5health potions&d!"}), false),
    SUGAR(new CWItem(Material.SUGAR).setName("&dSugar").setLore(new String[] {"&7Sweet white freshly harvested sugar.", "&7Great for making speed potions!", "&dDrop them in boiling water to make &5speed potions&d!"}), false),

    WHEAT(new CWItem(Material.WHEAT).setName("&eWheat").setLore(new String[] {"&7Perfectly harvested common wheat!", "&eThrow it in the grinder(hopper) to make flour!"}), false),
    FLOUR(new CWItem(Material.SUGAR).setName("&eFlour").setLore(new String[] {"&7Dwarves finest flour, to make delicous bread!", "&ePlace in the furnace to create bread."}), false),
    BREAD(new CWItem(Material.BREAD).setName("&eBread").setLore(new String[] {"&7Delicious bread. (It's still warm!)", "&eRestores &65 hunger and 12 saturation&e!"}), true),
    SEED(new CWItem(Material.SEEDS).setName("&eGrain").setLore(new String[]{"&7Whole grain with lots of proteins!", "&eSow them in the soil to grow wheat!"}), false),

    XP(new CWItem(Material.EXP_BOTTLE).setName("&a&lExperience").setLore(new String[]{"&7Share your experience if you have too much!"}), false),

    VIP_BANNER(new CWItem(Material.BANNER).setName("&3&lVIP BANNER").setLore(new String[]{"&7Place it down and click the banner to customize it.", "&eYou only have to customize it once!"}).setBaseColor(DyeColor.GRAY).setPattern(0, new Pattern(DyeColor.RED, PatternType.MOJANG)), true);


    private CWItem item;
    private boolean canKeep;

    Product(CWItem item, boolean canKeep) {
        this.item = item;
        this.canKeep = canKeep;
    }

    public CWItem getItem() {
        return item.clone();
    }

    public boolean canKeep() {
        return canKeep;
    }

    public static boolean canKeep(Material mat) {
        ItemStack productItem;
        for (Product product : values()) {
            productItem = product.getItem();
            if (productItem.getType() == mat) {
                if (product.canKeep()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public CWItem getItem(int amount) {
        CWItem i = item.clone();
        i.setAmount(amount);
        return i;
    }

    //Get class by name or alias and return null if no class was found.
    public static Product fromString(String name) {
        name = name.toLowerCase().replace("_","");
        //First check by name.
        for (Product p : values()) {
            if (p.toString().toLowerCase().replace("_","").equals(name)) {
                return p;
            }
        }
        return null;
    }
}

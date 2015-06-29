package com.clashwars.dvz.structures;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.ExpUtil;
import com.clashwars.dvz.structures.data.EnchantData;
import com.clashwars.dvz.structures.extra.CustomEnchant;
import com.clashwars.dvz.structures.internal.Structure;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EnchantStruc extends Structure {

    private EnchantData data;
    private List<CustomEnchant> enchants = new ArrayList<CustomEnchant>();
    private ItemMenu menu;

    public EnchantStruc() {
        if (dvz.getStrucCfg().getEnchantData() == null) {
            dvz.getStrucCfg().setEnchantData(new EnchantData());
        }
        data = dvz.getStrucCfg().getEnchantData();
        menu = new ItemMenu("enchant", data.getGuiSize(), CWUtil.integrateColor(data.getGuiTitle()));
        populateEnchants();
    }


    @Override
    public void onUse(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                ItemStack item = player.getItemInHand();

                //Get list of all enchants with the item clicked.
                List<CustomEnchant> matchedEnchants = new ArrayList<CustomEnchant>();
                for (CustomEnchant enchant : enchants) {
                    if (enchant.getItems().contains(item.getType())) {
                        matchedEnchants.add(enchant);
                    }
                }

                if (matchedEnchants.size() <= 0) {
                    player.sendMessage(Util.formatMsg("&cThis item can't be enchanted."));
                    return;
                }

                menu.show(player);
                menu.clear(player);
                menu.setSlot(new CWItem(item), 0, player);
                int slotID = 1;
                for (CustomEnchant enchant : matchedEnchants) {
                    if (new ExpUtil(player).getCurrentExp() >= enchant.getXpNeeded()) {
                        if (item.getEnchantments().containsKey(enchant.getEnchant())) {
                            if (item.getEnchantmentLevel(enchant.getEnchant()) >= enchant.getLevel()) {
                                menu.setSlot(new CWItem(Material.BOOK).setName("&4&l" + enchant.getName()).setLore(enchant.getDesc()).addLore("&cYou already have this enchantment.")
                                        .addLore("&a&lExp Cost&8: &2" + enchant.getXpNeeded()), slotID, player);
                                slotID++;
                                continue;
                            }
                        }
                        menu.setSlot(new CWItem(Material.ENCHANTED_BOOK).setName("&a&l" + enchant.getName()).setLore(enchant.getDesc()).addLore("&2Click &7to purchase this enchantment.")
                                .addLore("&a&lExp Cost&8: &2" + enchant.getXpNeeded()), slotID, player);
                    } else {
                        menu.setSlot(new CWItem(Material.BOOK).setName("&4&l" + enchant.getName()).setLore(enchant.getDesc()).addLore("&cYou don't have enough experience.")
                                .addLore("&c&lExp Cost&8: &4" + enchant.getXpNeeded()), slotID, player);
                    }
                    slotID++;
                }
                dvz.logTimings("EnchantStruc.onUse()", t);
            }
        }.runTaskLater(dvz, 3);
    }


    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
        Long t = System.currentTimeMillis();
        ItemMenu itemMenu = event.getItemMenu();
        if (!itemMenu.getName().equals(menu.getName())) {
            return;
        }
        if (itemMenu.getID() != menu.getID()) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        CustomEnchant enchant = getEnchantByName(CWUtil.stripAllColor(item.getItemMeta().getDisplayName()));
        if (enchant == null) {
            player.sendMessage(Util.formatMsg("&cInvalid enchantment."));
            player.closeInventory();
            dvz.logTimings("EnchantStruc.menuClick()[can't enchant item]", t);
            return;
        }

        ExpUtil xpu = new ExpUtil(player);
        if (player.getItemInHand().getEnchantments().containsKey(enchant.getEnchant())) {
            if (player.getItemInHand().getEnchantmentLevel(enchant.getEnchant()) >= enchant.getLevel()) {
                player.sendMessage(Util.formatMsg("&cYou already have this enchantment!"));
                player.closeInventory();
                dvz.logTimings("EnchantStruc.menuClick()[already enchanted]", t);
                return;
            }
        }
        if (xpu.getCurrentExp() >= enchant.getXpNeeded()) {
            player.getItemInHand().addUnsafeEnchantment(enchant.getEnchant(), enchant.getLevel());
            xpu.changeExp(-enchant.getXpNeeded());
            player.sendMessage(Util.formatMsg("&6Enchanted your item with &5" + enchant.getName() + "&6!"));
            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 0.8f, 1.5f);
            ParticleEffect.ENCHANTMENT_TABLE.display(0.5f, 1f, 0.5f, 0.1f, 50, player.getLocation().add(0, 1.0f, 0));
            player.closeInventory();
        } else {
            player.sendMessage(Util.formatMsg("&cYou don't have enough experience for this enchant."));
            player.sendMessage(Util.formatMsg("&cGo do your tasks to earn XP."));
            player.closeInventory();
        }
        dvz.logTimings("EnchantStruc.menuClick()", t);
    }


    private CustomEnchant getEnchantByName(String name) {
        for (CustomEnchant enchant : enchants) {
            if (enchant.getName().equalsIgnoreCase(name)) {
                return enchant;
            }
        }
        return null;
    }


    private void populateEnchants() {
        //Bow
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, "Power 1", new String[] {"&7Increases the damage of arrows."}, 1, 100, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, "Power 2", new String[] {"&7Increases the damage of arrows."}, 2, 200, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_KNOCKBACK, "Punch 1", new String[] {"&7Arrows will knock back monsters."}, 1, 100, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_KNOCKBACK, "Punch 2", new String[] {"&7Arrows will knock back monsters."}, 2, 200, new Material[] {Material.BOW}));

        //Weapons
        enchants.add(new CustomEnchant(Enchantment.DAMAGE_ALL, "Sharpness 1", new String[] {"&7Your greatsword will deal more damage."}, 1, 200, new Material[] {Material.DIAMOND_AXE}));
        enchants.add(new CustomEnchant(Enchantment.DAMAGE_ALL, "Sharpness 2", new String[] {"&7Your greatsword will deal more damage."}, 2, 300, new Material[] {Material.DIAMOND_AXE}));
        enchants.add(new CustomEnchant(Enchantment.LOOT_BONUS_MOBS, "Lifesteal 1", new String[] {"&27% &7chance to lifesteal when attacking.", "&7You will steal 1 heart."}, 1, 100, new Material[] {Material.DIAMOND_AXE}));
        enchants.add(new CustomEnchant(Enchantment.LOOT_BONUS_MOBS, "Lifesteal 2", new String[] {"&214% &7chance to lifesteal when attacking.", "&7You will steal 1 heart."}, 1, 250, new Material[] {Material.DIAMOND_AXE}));
        enchants.add(new CustomEnchant(Enchantment.LOOT_BONUS_MOBS, "Lifesteal 3", new String[] {"&221% &7chance to lifesteal when attacking.", "&7You will steal 1 heart."}, 1, 500, new Material[] {Material.DIAMOND_AXE}));

        enchants.add(new CustomEnchant(Enchantment.DAMAGE_ALL, "Sharpness 1", new String[] {"&7Your battleaxe will deal more damage."}, 1, 300, new Material[] {Material.IRON_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.DURABILITY, "Block 1", new String[] {"&7When &ablocking &7damage is reduced by &20.5 &7hearts.",
                "&7When &ablocking &7and &asneaking &7it's reduced by &21 &7heart!"}, 1, 150, new Material[] {Material.IRON_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.DURABILITY, "Block 2", new String[] {"&7When &ablocking &7damage is reduced by &21 &7heart.",
                "&7When &ablocking &7and &asneaking &7it's reduced by &22 &7hearts!"}, 2, 300, new Material[] {Material.IRON_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.LUCK, "Swing 1", new String[] {"&28% &7chance to swing while attacking.",
                "&7When you swing all monsters nearby get", "&7damaged and pushed away from you."}, 1, 250, new Material[] {Material.IRON_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.LUCK, "Swing 2", new String[] {"&215% &7chance to swing while attacking.",
                "&7When you swing all monsters nearby get", "&7damaged and pushed away from you."}, 2, 500, new Material[] {Material.IRON_SWORD}));

        enchants.add(new CustomEnchant(Enchantment.DAMAGE_ALL, "Sharpness 1", new String[] {"&7Your fiery flail will deal more damage."}, 1, 200, new Material[] {Material.GOLD_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.FIRE_ASPECT, "Fire 1", new String[] {"&7Put your flail on fire!", "&7Burn all monsters you hit with the flail."}, 1, 400, new Material[] {Material.GOLD_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.LURE, "Chain 1", new String[] {"&7You will get the ability to chain monsters.", "&7Look at a monster you want to chain",
                "&7and hold right click to pull them towards you.", "&215s cooldown &7and &215 range"}, 1, 250, new Material[] {Material.GOLD_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.LURE, "Chain 2", new String[] {"&7You will get the ability to chain monsters.", "&7Look at a monster you want to chain",
                "&7and hold right click to pull them towards you.", "&27s cooldown &7and &220 range"}, 2, 250, new Material[] {Material.GOLD_SWORD}));

        //Armor
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 1", new String[] {"&7Give your armor some extra protection."}, 1, 50, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 2", new String[] {"&7Give your armor some extra protection."}, 2, 75, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 3", new String[] {"&7Give your armor some extra protection."}, 3, 100, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 4", new String[] {"&7Give your armor some extra protection."}, 4, 150, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));

        enchants.add(new CustomEnchant(Enchantment.PROTECTION_FALL, "Feather Falling 1", new String[] {"&7Reduces fall damage!", "&7You really need this for the air dragon!"}, 1, 200, new Material[] {Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_FALL, "Feather Falling 2", new String[] {"&7Reduces fall damage!", "&7You really need this for the air dragon!"}, 2, 400, new Material[] {Material.LEATHER_BOOTS}));

        enchants.add(new CustomEnchant(Enchantment.THORNS, "Thorns 1", new String[] {"&7Put thorns on your tunic", "&7so that monsters take damage when they hit you!"}, 1, 500, new Material[] {Material.LEATHER_CHESTPLATE}));
    }


    @Override
    public String getRegion() {
        return data.getRegion();
    }
}

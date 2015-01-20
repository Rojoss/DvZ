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
    public void onUse(Player player) {
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
                        menu.setSlot(new CWItem(Material.BOOK).setName("&4&l" + enchant.getName()).addLore("&a&lExp Cost&8: &2" + enchant.getXpNeeded())
                                .addLore("&7You already have this enchantment."), slotID, player);
                        slotID++;
                        continue;
                    }
                }
                menu.setSlot(new CWItem(Material.ENCHANTED_BOOK).setName("&a&l" + enchant.getName()).addLore("&a&lExp Cost&8: &2" + enchant.getXpNeeded())
                        .addLore("&7Click to purchase this enchantment."), slotID, player);
            } else {
                menu.setSlot(new CWItem(Material.BOOK).setName("&4&l" + enchant.getName()).addLore("&c&lExp Cost&8: &4" + enchant.getXpNeeded())
                        .addLore("&7You don't have enough experience."), slotID, player);
            }
            slotID++;
        }
    }


    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
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
            return;
        }

        ExpUtil xpu = new ExpUtil(player);
        if (player.getItemInHand().getEnchantments().containsKey(enchant.getEnchant())) {
            if (player.getItemInHand().getEnchantmentLevel(enchant.getEnchant()) >= enchant.getLevel()) {
                player.sendMessage(Util.formatMsg("&cYou already have this enchantment!"));
                player.closeInventory();
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
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, "Power 1", 1, 50, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, "Power 2", 2, 150, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, "Power 3", 3, 450, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_KNOCKBACK, "Punch 1", 1, 75, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_KNOCKBACK, "Punch 2", 2, 200, new Material[] {Material.BOW}));

        //Weapons
        enchants.add(new CustomEnchant(Enchantment.DAMAGE_ALL, "Sharpness 1", 1, 200, new Material[] {Material.IRON_SWORD, Material.GOLD_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.DAMAGE_ALL, "Sharpness 2", 2, 300, new Material[] {Material.IRON_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.DAMAGE_ALL, "Sharpness 3", 3, 400, new Material[] {Material.IRON_SWORD}));

        enchants.add(new CustomEnchant(Enchantment.DURABILITY, "Block 1 (custom)", 1, 250, new Material[] {Material.DIAMOND_SWORD}));
        enchants.add(new CustomEnchant(Enchantment.DURABILITY, "Block 2 (custom)", 2, 500, new Material[] {Material.DIAMOND_SWORD}));

        enchants.add(new CustomEnchant(Enchantment.FIRE_ASPECT, "Fire Aspect 1", 1, 400, new Material[] {Material.GOLD_SWORD}));

        //Armor
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 1", 1, 50, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 2", 2, 100, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 3", 3, 150, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 4", 4, 200, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection 5", 5, 250, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));

        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Arrow Protection 1", 1, 100, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Arrow Protection 2", 2, 150, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, "Arrow Protection 3", 3, 200, new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}));

        enchants.add(new CustomEnchant(Enchantment.PROTECTION_FALL, "Feather Falling 1", 1, 400, new Material[] {Material.LEATHER_BOOTS}));
        enchants.add(new CustomEnchant(Enchantment.PROTECTION_FALL, "Feather Falling 2", 2, 800, new Material[] {Material.LEATHER_BOOTS}));

        enchants.add(new CustomEnchant(Enchantment.THORNS, "Thorns 1", 1, 750, new Material[] {Material.LEATHER_CHESTPLATE}));
    }


    @Override
    public String getRegion() {
        return data.getRegion();
    }
}

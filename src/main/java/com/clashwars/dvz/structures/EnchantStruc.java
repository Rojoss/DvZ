package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.EnchantData;
import com.clashwars.dvz.structures.extra.CustomEnchant;
import com.clashwars.dvz.structures.internal.Structure;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class EnchantStruc extends Structure {

    private EnchantData data;
    private Set<CustomEnchant> enchants = new HashSet<CustomEnchant>();

    public EnchantStruc() {
        if (dvz.getStrucCfg().getEnchantData() == null) {
            dvz.getStrucCfg().setEnchantData(new EnchantData());
        }
        data = dvz.getStrucCfg().getEnchantData();
        populateEnchants();
    }

    @Override
    public void onUse(Player player) {
        ItemStack item = player.getItemInHand();

        //Get list of all enchants with the item clicked.
        Set<CustomEnchant> matchedEnchants = new HashSet<CustomEnchant>();
        for (CustomEnchant enchant : enchants) {
            if (enchant.getItems().contains(item.getType())) {
                matchedEnchants.add(enchant);
            }
        }

        if (matchedEnchants.size() <= 0) {
            player.sendMessage(Util.formatMsg("&cThis item can't be enchanted."));
            return;
        }
        player.sendMessage(Util.formatMsg("&6Pick a enchantment!"));
    }



    @Override
    public String getRegion() {
        return data.getRegion();
    }


    private void populateEnchants() {
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, 1, 50, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, 2, 150, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_DAMAGE, 3, 450, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_KNOCKBACK, 1, 75, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.ARROW_KNOCKBACK, 2, 200, new Material[] {Material.BOW}));
        enchants.add(new CustomEnchant(Enchantment.FIRE_ASPECT, 1, 500, new Material[] {Material.GOLD_SWORD}));
    }
}

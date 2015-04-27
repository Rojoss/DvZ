package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BuildingBrick extends DwarfAbility {

    public BuildingBrick() {
        super();
        ability = Ability.BUILDING_BRICK;
        castItem = new DvzItem(Material.CLAY_BRICK, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        List<Block> blocks = player.getLastTwoTargetBlocks((Set<Material>)null, getIntOption("range"));
        Block block = blocks.get(0);

        if (block == null || block.getType() != Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou can't build there! &4&l<<"));
            return;
        }

        if (blocks.get(1).getType() != Material.SMOOTH_BRICK) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan only build against stone bricks! &4&l<<"));
            return;
        }

        if (dvz.getMM().getActiveMap().getCuboid("innerwall").contains(blocks.get(0))) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild tools have to be used outside the keep! &4&l<<"));
            return;
        }

        if (CWUtil.getNearbyEntities(block.getLocation(), 1.5f, Arrays.asList(new EntityType[] {EntityType.PLAYER})).size() > 0) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't build where people stand! &4&l<<"));
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.SMOOTH_BRICK) {
                block.setType(item.getType());
                block.setData(item.getData().getData());

                CWUtil.removeItemsFromSlot(player.getInventory(), i, 1);
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(item.getType(), item.getData().getData()), 0.5f, 0.5f, 0.5f, 0.1f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
                block.getWorld().playSound(block.getLocation(), Sound.DIG_STONE, 0.8f, 1f);
                player.updateInventory();
                return;
            }
        }
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cNo more stone in your hotbar! &4&l<<"));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}

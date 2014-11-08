package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class Blast extends MobAbility {

    public Blast() {
        super();
        this.ability = Ability.BLAST;
        castItem = new DvzItem(Material.FIRE, 1, (short)0, 197, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        int radius = getIntOption("fire-radius");

        for(int x = triggerLoc.getBlockX() - radius; x <= triggerLoc.getBlockX() + radius; x++) {
            for(int y = triggerLoc.getBlockY() - radius; y <= triggerLoc.getBlockY() + radius; y++) {
                for(int z = triggerLoc.getBlockZ() - radius; z <= triggerLoc.getBlockZ() + radius; z++) {
                    Block b = triggerLoc.getWorld().getBlockAt(x, y, z);
                    if(b.getType().isSolid()) {
                        if (b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                            if (CWUtil.randomFloat() <= getDoubleOption("fire-chance")) {
                                b.getRelative(BlockFace.UP).setType(Material.FIRE);
                                //TODO: Add sound and particle effects.
                            }
                        }
                    }
                }
            }
        }
    }

}

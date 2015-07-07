package com.clashwars.dvz.abilities.monsters.silverfish;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;

public class InfectStone extends BaseAbility {

    public InfectStone() {
        super();
        ability = Ability.INFECT_STONE;
        castItem = new DvzItem(Material.MONSTER_EGG, 1, (short)60, displayName, 10, -1);

        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Block block = triggerLoc.getBlock();
        if (block.getType() == Material.MONSTER_EGGS) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis block is already infected! &4&l<<"));
            return;
        }

        if (block.getType() != Material.SMOOTH_BRICK && block.getType() != Material.COBBLESTONE && block.getType() != Material.STONE) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou can only infect stone blocks! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        byte data = block.getData();
        if (block.getType() == Material.STONE) {
            block.setType(Material.MONSTER_EGGS);
            block.setData((byte)0);
        } else if (block.getType() == Material.COBBLESTONE) {
            block.setType(Material.MONSTER_EGGS);
            block.setData((byte)1);
        } else {
            if (block.getData() == 0) {
                block.setType(Material.MONSTER_EGGS);
                block.setData((byte)2);
            } else if (block.getData() == 1) {
                block.setType(Material.MONSTER_EGGS);
                block.setData((byte)3);
            } else if (block.getData() == 2) {
                block.setType(Material.MONSTER_EGGS);
                block.setData((byte)4);
            } else if (block.getData() == 3) {
                block.setType(Material.MONSTER_EGGS);
                block.setData((byte)5);
            }
        }
        block.setMetadata("infected", new FixedMetadataValue(dvz, player.getName()));

        block.getWorld().playSound(block.getLocation(), Sound.SILVERFISH_IDLE, 1, 0);
        ParticleEffect.CLOUD.display(0.5f, 0.5f, 0.5f, 0, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}

package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class Blink extends MobAbility {

    public Blink() {
        super();
        this.ability = Ability.BLINK;
        castItem = new DvzItem(Material.ENDER_PEARL, 1, (short)0, 196, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        Block b = player.getTargetBlock((Set<Material>)null, (int)dvz.getGM().getMonsterPower(10, 25));
        Location l = player.getLocation().clone();
        Location l2 = b.getRelative(BlockFace.UP).getLocation().clone();
        l2.setPitch(l.getPitch());
        l2.setYaw(l.getYaw());

        if(b != null && b.getType() != Material.AIR) {
            if (onCooldown(player)) {
                return;
            }
            player.teleport(l2);

            CWEntity.create(EntityType.ENDERMITE, l, (int)dvz.getGM().getMonsterPower(1, 2));
            player.getWorld().playSound(l, Sound.ENDERMAN_TELEPORT, 0.5f, 0.6f);
            player.getWorld().playSound(l, Sound.ENDERMAN_TELEPORT, 1, 1.2f);
            player.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 1);
            player.getWorld().playEffect(l2, Effect.ENDER_SIGNAL, 1);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}

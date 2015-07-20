package com.clashwars.dvz.abilities.monsters.skeleton;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Mount extends BaseAbility {

    public static HashMap<UUID, Entity> mounts = new HashMap<UUID, Entity>();

    public Mount() {
        super();
        ability = Ability.MOUNT;
        castItem = new DvzItem(Material.SADDLE, 1, (short)0, displayName, 10, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        CWEntity mount = CWEntity.create(EntityType.HORSE, player.getLocation());
        mount.setVariant(Horse.Variant.SKELETON_HORSE);
        mount.setSaddle(true);
        mount.setTamed(true, player);
        mount.setHorseSpeed(0.35d);
        mount.setBaby(false);
        mount.entity().setPassenger(player);
        mounts.put(player.getUniqueId(), mount.entity());

        CWUtil.removeItemsFromHand(player , 1);

        player.getWorld().playSound(player.getLocation(), Sound.HORSE_ARMOR, 1, 1);
        ParticleEffect.CLOUD.display(1, 1, 1, 0, 50, player.getLocation().add(0,0.5f,0));

        new BukkitRunnable() {
            @Override
            public void run() {
                removeMount(player.getUniqueId());
            }
        }.runTaskLater(dvz, 400);
    }

    public void removeMount(UUID player) {
        if (!mounts.containsKey(player)) {
            return;
        }

        Entity entity = mounts.get(player);
        mounts.remove(player);
        if (entity == null || entity.isDead() || !entity.isValid()) {
            return;
        }

        ParticleEffect.CLOUD.display(1, 1, 1, 0, 50, entity.getLocation().add(0,0.5f,0));
        entity.getWorld().playSound(entity.getLocation(), Sound.HORSE_ANGRY, 1, 1);

        entity.eject();
        entity.remove();
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler
    private void logout(PlayerQuitEvent event) {
        removeMount(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        for (UUID player : mounts.keySet()) {
            removeMount(player);
        }
    }
}

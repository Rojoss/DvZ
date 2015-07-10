package com.clashwars.dvz.abilities.monsters.silverfish;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.listeners.custom.GameResetEvent;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.runnables.InfestRunnable;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Infest extends BaseAbility {

    public static HashMap<UUID, InfestRunnable> infestPlayers = new HashMap<UUID, InfestRunnable>();

    public Infest() {
        super();
        ability = Ability.INFEST;
        castItem = new DvzItem(Material.ROTTEN_FLESH, 1, (short)0, displayName, 5, -1);
    }

    @EventHandler
    public void interact(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        if (!isCastItem(player.getItemInHand())) {
            return;
        }

        if (!canCast(player)) {
            return;
        }

        Player target = (Player) event.getRightClicked();
        CWPlayer cwt = dvz.getPM().getPlayer(target);
        if (cwt.getPlayerClass().getType() != ClassType.MONSTER ) {
            CWUtil.sendActionBar(player,  CWUtil.integrateColor("&4&l>> &cYou can only infest in to monsters! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        target.getWorld().playSound(target.getLocation(), Sound.HORSE_SADDLE, 0.8f, 0);
        target.getWorld().playSound(target.getLocation(), Sound.SILVERFISH_HIT, 1, 0);

        CWUtil.sendActionBar(player, CWUtil.integrateColor("&8&l>> &dYou infested in to " + target.getName() + "'s body! &8&l<<"));

        player.setGameMode(GameMode.SPECTATOR);
        player.hidePlayer(target);

        InfestRunnable runnable = new InfestRunnable(player, target);
        runnable.runTaskTimer(dvz, 0, 1);
        infestPlayers.put(player.getUniqueId(), runnable);
    }

    public void leaveBody(UUID player) {
        if (!infestPlayers.containsKey(player)) {
            return;
        }

        InfestRunnable runnable = infestPlayers.get(player);
        runnable.player.setGameMode(GameMode.SURVIVAL);
        runnable.player.teleport(runnable.target.getLocation());
        runnable.player.showPlayer(runnable.target);

        CWUtil.sendActionBar(runnable.player, CWUtil.integrateColor("&8&l>> &dYou came out of " + runnable.target.getName() + "'s body! &8&l<<"));
        int spawnCount = Math.min(Math.round((System.currentTimeMillis() - runnable.startTime) / 1000), (int)dvz.getGM().getMonsterPower(5, 10));

        for (int i = 0; i < spawnCount; i++) {
            Location loc = runnable.target.getLocation().add(RandomUtils.getRandomCircleVector().multiply(0.5f).setY(1));
            CWEntity silverfish = CWEntity.create(EntityType.SILVERFISH, loc);
            silverfish.setVelocity(RandomUtils.getRandomCircleVector().multiply(0.5f).setY(0.2f));
            silverfish.entity().setMetadata("owner", new FixedMetadataValue(dvz, runnable.player.getName()));
        }

        runnable.cancel();
        infestPlayers.remove(player);
    }


    @EventHandler
    private void playerLeave(PlayerQuitEvent event) {
        if (infestPlayers.containsKey(event.getPlayer().getUniqueId())) {
            leaveBody(event.getPlayer().getUniqueId());
            return;
        }
        for (InfestRunnable runnable : infestPlayers.values()) {
            if (runnable.target.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                leaveBody(event.getPlayer().getUniqueId());
                return;
            }
        }
    }


    @EventHandler
    private void gameReset(GameResetEvent event) {
        for (Map.Entry<UUID, InfestRunnable> entry : infestPlayers.entrySet()) {
            entry.getValue().cancel();
            if (Bukkit.getPlayer(entry.getKey()) != null) {
                leaveBody(entry.getKey());
            }
        }

        infestPlayers.clear();
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        for (Map.Entry<UUID, InfestRunnable> entry : infestPlayers.entrySet()) {
            entry.getValue().cancel();
            if (Bukkit.getPlayer(entry.getKey()) != null) {
                leaveBody(entry.getKey());
            }
        }
        infestPlayers.clear();
    }

}

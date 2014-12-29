package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Explode extends MobAbility {

    public Explode() {
        super();
        ability = Ability.EXPLODE;
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //--
    }

    @EventHandler
    private void sneak(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();

        if (player.isSneaking()) {
            return;
        }

        if (!isCastItem(player.getItemInHand())) {
            return;
        }

        if (!canCast(player)) {
            return;
        }

        //TODO: Set player as powered creeper.

        new BukkitRunnable() {
            int ticks = 0;
            Double power = getDoubleOption("minpower");
            Location playerLoc = player.getLocation();

            @Override
            public void run() {
                ticks++;
                //Every second... +/-
                if (ticks % 20 == 0) {
                    //Check if player has moved
                    if (playerLoc.getBlockX() != player.getLocation().getBlockX() || playerLoc.getBlockY() != player.getLocation().getBlockY() || playerLoc.getBlockZ() != player.getLocation().getBlockZ()) {
                        player.sendMessage(Util.formatMsg("&cExplosion charge cancelled because you moved!)"));
                        //TODO: Set player as normal creeper again.
                        this.cancel();
                        return;
                    }
                    //Increase the power by powerpersec value and clamp it between min/max value.
                    power = Math.min(Math.max((ticks / 20) * getDoubleOption("powerpersec"), getDoubleOption("minpower")), getDoubleOption("maxpower"));
                    //TODO: Add sound effects and particles.
                }

                //If player died create small explosion.
                if (player.isDead()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + getDoubleOption("minpower") + " &cpower!"));
                    playerLoc.getWorld().createExplosion(playerLoc, (float)getDoubleOption("minpower"));
                    this.cancel();
                    return;
                }

                //If player stops sneaking then explode!
                if (!player.isSneaking()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + power + " &cpower!"));
                    playerLoc.getWorld().createExplosion(playerLoc, power.floatValue());
                    player.setHealth(0);
                    this.cancel();
                    return;
                }

                //After 30 seconds force explosion. [because this is quite a heavy task so it's just a waste running this forever]
                if (ticks > 600) {
                    player.sendMessage(Util.formatMsg("&cOvercharge!"));
                    playerLoc.getWorld().createExplosion(playerLoc, power.floatValue());
                    player.setHealth(0);
                    this.cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 1);
    }
}

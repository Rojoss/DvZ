package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.player.CWPlayer;
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
        castItem = new DvzItem(Material.SULPHUR, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //--
    }

    @EventHandler
    private void sneak(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        final CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (player.isSneaking()) {
            return;
        }

        if (!isCastItem(player.getItemInHand())) {
            return;
        }

        if (!canCast(player)) {
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        Util.disguisePlayer(player.getName(), (cwp.getPlayerClass().getClassClass().getDisguise() + " setPowered true"));

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
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cExplosion cancelled because you moved! &4&l<<"));
                        Util.disguisePlayer(player.getName(), cwp.getPlayerClass().getClassClass().getDisguise());
                        this.cancel();
                        return;
                    }
                    //Increase the power by powerpersec value and clamp it between min/max value.
                    power = Math.min(Math.max((ticks / 20) * getDoubleOption("powerpersec"), getDoubleOption("minpower")), getDoubleOption("maxpower"));
                    CWUtil.sendActionBar(player, CWUtil.integrateColor("&9&l>> &3Charge power&8: &6&l" + power + " &9&l<<"));
                    //TODO: Add sound effects and particles.
                }

                //If player died create small explosion.
                if (player.isDead()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + getDoubleOption("minpower") + " &cpower!"));
                    createExplosion(playerLoc, (float)getDoubleOption("minpower"));
                    this.cancel();
                    return;
                }

                //If player stops sneaking then explode!
                if (!player.isSneaking()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + power + " &cpower!"));
                    createExplosion(playerLoc, power.floatValue());
                    player.setHealth(0);
                    this.cancel();
                    return;
                }

                //After 30 seconds force explosion. [because this is quite a heavy task so it's just a waste running this forever]
                if (ticks > 600) {
                    player.sendMessage(Util.formatMsg("&cOvercharge!"));
                    createExplosion(playerLoc, power.floatValue());
                    player.setHealth(0);
                    this.cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 1);
    }

    private void createExplosion(Location loc, float power) {
        loc.getWorld().createExplosion(loc, power);

        //Damage shrine blocks.
        for (ShrineBlock shrineBlock : dvz.getGM().getShrineBlocks()) {
            if (shrineBlock != null && !shrineBlock.isDestroyed()) {
                if (shrineBlock.getLocation().distance(loc) < power) {
                    shrineBlock.damage(Math.round(power * 2));
                }
            }
        }

    }
}

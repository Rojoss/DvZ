package com.clashwars.dvz.damage.types;

import com.clashwars.cwcore.Debug;
import com.clashwars.dvz.damage.BaseDmg;
import com.clashwars.dvz.damage.CustomDamageEvent;
import com.clashwars.dvz.damage.DmgType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class RangedDmg extends BaseDmg {

    private OfflinePlayer shooter;
    private EntityType projectileType = EntityType.ARROW;

    public RangedDmg(OfflinePlayer player, double damage, OfflinePlayer shooter) {
        super(player, damage);

        this.shooter = shooter;
        type = DmgType.RANGED;
        damage();
    }

    public RangedDmg(OfflinePlayer player, double damage, OfflinePlayer shooter, EntityType projectileType) {
        super(player, damage);

        this.shooter = shooter;
        this.projectileType = projectileType;
        type = DmgType.RANGED;
        damage();
    }

    @Override
    public void damage() {
        if (player != null && player.isOnline()) {
            CustomDamageEvent event = new CustomDamageEvent((Player)player, damage, type, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled() && player.isOnline()) {
                ((Player)player).setHealth(Math.max(((Player) player).getHealth() - event.getDamage(), 0));
            }
        }
    }

    @Override
    public String getDeathMsg() {
        String deathMsg = "{0} was shot by {1}'s {2}";
        return deathMsg.replace("{0}", player.getName()).replace("{1}", shooter.getName()).replace("{2}", projectileType.toString().toLowerCase().replace("_", " "));
    }

    @Override
    public String getDmgMsg(boolean damageTaken) {
        if (damageTaken) {
            return "shot by " + shooter.getName() + "'s " + projectileType.toString().toLowerCase().replace("_", " ");
        }
        return "shot " + player.getName() + " with an " + projectileType.toString().toLowerCase().replace("_", " ");
    }

    public OfflinePlayer getShooter() {
        return shooter;
    }

    public EntityType getProjectileType() {
        return projectileType;
    }
}

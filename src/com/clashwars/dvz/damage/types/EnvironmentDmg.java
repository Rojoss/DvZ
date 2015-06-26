package com.clashwars.dvz.damage.types;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.damage.BaseDmg;
import com.clashwars.dvz.damage.CustomDamageEvent;
import com.clashwars.dvz.damage.DmgType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class EnvironmentDmg extends BaseDmg {

    private EntityDamageEvent.DamageCause cause;

    public EnvironmentDmg(OfflinePlayer player, double damage, EntityDamageEvent.DamageCause cause) {
        super(player, damage);

        this.cause = cause;
        type = DmgType.ENVIRONMENT;

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
        String deathMsg = "{0} died by {1}";
        if (cause == EntityDamageEvent.DamageCause.DROWNING) {
            deathMsg = "{0} drowned";
        } else if (cause == EntityDamageEvent.DamageCause.CONTACT) {
            deathMsg = "{0} was pricked to death";
        } else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            deathMsg = "{0} was killed by a mob";
        } else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            deathMsg = "{0} blew up from a creeper";
        } else if (cause == EntityDamageEvent.DamageCause.FALL) {
            deathMsg = "{0} fell to his death";
        } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
            deathMsg = "{0} was squashed by an anvil";
        } else if (cause == EntityDamageEvent.DamageCause.FIRE) {
            deathMsg = "{0} walked into fire";
        } else if (cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            deathMsg = "{0} burned to death";
        } else if (cause == EntityDamageEvent.DamageCause.LAVA) {
            deathMsg = "{0} tried to swim in lava";
        } else if (cause == EntityDamageEvent.DamageCause.LIGHTNING) {
            deathMsg = "{0} was struck by lightning";
        } else if (cause == EntityDamageEvent.DamageCause.MAGIC) {
            deathMsg = "{0} magically disappeared";
        } else if (cause == EntityDamageEvent.DamageCause.MELTING) {
            deathMsg = "{0} melted";
        } else if (cause == EntityDamageEvent.DamageCause.POISON) {
            deathMsg = "{0} died by poison";
        } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            deathMsg = "{0} was shot to death";
        } else if (cause == EntityDamageEvent.DamageCause.STARVATION) {
            deathMsg = "{0} starved to death";
        } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
            deathMsg = "{0} suffocated";
        } else if (cause == EntityDamageEvent.DamageCause.SUICIDE) {
            deathMsg = "{0} suicided";
        } else if (cause == EntityDamageEvent.DamageCause.THORNS) {
            deathMsg = "{0} died from thorns";
        } else if (cause == EntityDamageEvent.DamageCause.VOID) {
            deathMsg = "{0} fell out of the world";
        } else if (cause == EntityDamageEvent.DamageCause.WITHER) {
            deathMsg = "{0} withered away";
        }

        return deathMsg.replace("{0}", player.getName()).replace("{1}", cause.toString().toLowerCase().replace("_"," "));
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
}
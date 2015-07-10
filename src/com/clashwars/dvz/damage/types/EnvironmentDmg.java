package com.clashwars.dvz.damage.types;

import com.clashwars.dvz.damage.BaseDmg;
import com.clashwars.dvz.damage.DmgType;
import org.bukkit.OfflinePlayer;
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

    @Override
    public String getDmgMsg(boolean damageTaken) {
        String dmgMsg = cause.toString().toLowerCase().replace("_", " ");
        if (cause == EntityDamageEvent.DamageCause.CONTACT) {
            dmgMsg = "cactus";
        } else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            dmgMsg = "mob attack";
        } else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            dmgMsg = "creeper explosion";
        } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
            dmgMsg = "falling anvil";
        } else if (cause == EntityDamageEvent.DamageCause.FIRE) {
            dmgMsg = "walk in fire";
        } else if (cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            dmgMsg = "fire";
        }
        return dmgMsg;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
}
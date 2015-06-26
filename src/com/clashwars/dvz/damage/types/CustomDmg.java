package com.clashwars.dvz.damage.types;

import com.clashwars.dvz.damage.BaseDmg;
import com.clashwars.dvz.damage.CustomDamageEvent;
import com.clashwars.dvz.damage.DmgType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CustomDmg extends BaseDmg {

    private OfflinePlayer damageSource;
    private String deathMsg;

    public CustomDmg(Player player, double damage, String deathMessage) {
        super(player, damage);

        type = DmgType.CUSTOM;
        deathMsg = deathMessage;
        damage();
    }

    public CustomDmg(OfflinePlayer player, double damage, String deathMessage, OfflinePlayer damageSource) {
        super(player, damage);

        this.damageSource = damageSource;

        type = DmgType.CUSTOM;
        deathMsg = deathMessage;
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
        String msg = deathMsg;
        msg = msg.replace("{0}", player.getName());
        if (damageSource != null) {
            msg = msg.replace("{1}", damageSource.getName());
        }
        return msg;
    }

    public OfflinePlayer getDamageSource() {
        return damageSource;
    }
}
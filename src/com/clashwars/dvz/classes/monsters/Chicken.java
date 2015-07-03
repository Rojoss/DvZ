package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.dwarves.bonus.Camouflage;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.events.custom.GameResetEvent;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Chicken extends MobClass {

    public static Map<UUID, CWEntity> eggs = new HashMap<UUID, CWEntity>();
    public static int hatchMaxTicks = 100;

    public Chicken() {
        super();
        dvzClass = DvzClass.CHICKEN;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)93, displayName, 40, -1);

        abilities.add(Ability.FLY);
        abilities.add(Ability.EXPLOSIVE_EGG);
        abilities.add(Ability.LAY_EGG);
        abilities.add(Ability.DROP_EGG);

        //Egg stuff
        new BukkitRunnable() {
            Vector vectorZero = new Vector(0,0,0);

            @Override
            public void run() {
                Long t = System.currentTimeMillis();

                Map<UUID, CWEntity> eggsClone = new HashMap<UUID, CWEntity>(Chicken.eggs);
                for (Map.Entry<UUID, CWEntity> entry : eggsClone.entrySet()) {
                    Player owner = Bukkit.getPlayer(entry.getKey());
                    if (entry.getValue() == null || entry.getValue().entity() == null || !entry.getValue().entity().isValid() || entry.getValue().entity().isDead()) {
                        if (entry.getValue() != null && entry.getValue().entity() != null) {
                            entry.getValue().entity().remove();
                        }
                        Chicken.eggs.remove(entry.getKey());
                        continue;
                    }
                    if (owner == null || !owner.isValid() || !owner.isOnline() || owner.isDead()) {
                        entry.getValue().entity().remove();
                        Chicken.eggs.remove(entry.getKey());
                        continue;
                    }

                    String name = entry.getValue().entity().getCustomName();

                    //If egg is lifted move it to player.
                    if (name.contains("lifted")) {
                        entry.getValue().entity().teleport(owner.getLocation().add(0,-0.7f,0));
                        dvz.logTimings("Chicken.Runnable()[lifted]", t);
                        continue;
                    }

                    //If it's dropped check if it landed to create the explosion.
                    if (name.contains("dropped")) {
                        if (entry.getValue().entity().getLocation().getY() <= owner.getWorld().getHighestBlockYAt(entry.getValue().entity().getLocation()) + 1) {
                            ParticleEffect.EXPLOSION_LARGE.display(dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), 0, 10, entry.getValue().entity().getLocation(), 500);
                            owner.getWorld().playSound(entry.getValue().entity().getLocation(), Sound.EXPLODE, 1, 1.5f);

                            int power = 0;
                            String[] split = name.split("_");
                            if (split.length > 1) {
                                power = Integer.parseInt(split[1]);
                            }

                            List<Player> players = CWUtil.getNearbyPlayers(entry.getValue().entity().getLocation(), (int)dvz.getGM().getMonsterPower(2, 8));
                            for (Player p : players) {
                                if (dvz.getPM().getPlayer(p).isDwarf()) {
                                    if (Camouflage.blocks.containsKey(p.getUniqueId())) {
                                        CWUtil.sendActionBar(p, CWUtil.integrateColor("&4&l>> &cYou were hit by a rotten egg! &4You are no longer a block! &4&l<<"));
                                        Camouflage.removeBlock(p.getUniqueId());
                                    }
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, power / 2, 1));
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, power * 2, 1));
                                    new AbilityDmg(p, dvz.getGM().getMonsterPower(1, 5), Ability.DROP_EGG, owner);
                                }
                            }

                            entry.getValue().entity().remove();
                            Chicken.eggs.remove(entry.getKey());
                        }
                        dvz.logTimings("Chicken.Runnable()[dropped]", t);
                        continue;
                    }

                    //Hatch egg if sneaking nearby.
                    if (name.contains("grounded") || name.contains("hatched")) {
                        if (owner.isSneaking() && owner.getLocation().distance(entry.getValue().entity().getLocation()) < 0.5f) {
                            String[] split = name.split("_");
                            int value = 0;
                            if (split.length > 1) {
                                value = Integer.parseInt(split[1]);
                            }
                            //If hatched too long spawn a chicken etc.
                            if (value >= dvz.getGM().getMonsterPower(160, 140)) { /* 8<>15 seconds */
                                CWUtil.sendActionBar(owner, CWUtil.integrateColor("&4&l>> &cYou hatched your egg too long! &4&l<<"));
                                CWEntity.create(EntityType.CHICKEN, entry.getValue().entity().getLocation()).setBaby(true);
                                entry.getValue().entity().remove();
                                Chicken.eggs.remove(entry.getKey());
                                dvz.logTimings("Chicken.Runnable()[hatched too long]", t);
                                continue;
                            }
                            //Hatch it.
                            value++;
                            entry.getValue().setName("hatched_" + value);
                            CWUtil.sendActionBar(owner, CWUtil.integrateColor("&6&l>> &eHatching your egg! &8[&a" + value + "&8] &6&l<<"));
                        } else if (name.contains("grounded") && owner.isSneaking()) {
                            CWUtil.sendActionBar(owner, CWUtil.integrateColor("&4&l>> &cSneak near your dropped egg to hatch it! &4&l<<"));
                        }
                        dvz.logTimings("Chicken.Runnable()[grouned/hatched]", t);
                    }
                }
            }
        }.runTaskTimer(dvz, 0, 1);
    }

    @EventHandler
    private void gameReset(GameResetEvent event) {
        for (CWEntity egg : eggs.values()) {
            egg.entity().remove();
        }
        eggs.clear();
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        for (CWEntity egg : eggs.values()) {
            egg.entity().remove();
        }
        eggs.clear();
    }

}

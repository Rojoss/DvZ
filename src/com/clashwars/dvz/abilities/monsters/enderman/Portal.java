package com.clashwars.dvz.abilities.monsters.enderman;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.damage.types.CustomDmg;
import com.clashwars.dvz.events.custom.GameResetEvent;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.stats.internal.StatType;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.util.io.file.FilenameException;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.io.IOException;
import java.util.List;

//There can be multiple portal schematics.
//Name the schematics like portal-0, portal-1 etc..
public class Portal extends BaseAbility {

    public static PortalData activePortal = null;

    public Portal() {
        super();
        ability = Ability.PORTAL;
        castItem = new DvzItem(Material.DRAGON_EGG, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {

        //Only allow 1 portal at a time.
        if (activePortal != null) {
            player.sendMessage(Util.formatMsg("&cThere is already an active portal!"));
            player.sendMessage(Util.formatMsg("&cYou can ask &4" + activePortal.getOwner().getName() + " &cto destroy his portal."));
            return;
        }
        Location portalLoc = player.getLocation().add(0,10,0);

        //Make sure portal wont hit anything
        for (int x = portalLoc.getBlockX() - 4; x < portalLoc.getBlockX() + 4; x++) {
            for (int y = portalLoc.getBlockY() - 6; y < portalLoc.getBlockY() + 6; y++) {
                for (int z = portalLoc.getBlockZ() - 4; z < portalLoc.getBlockZ() + 4; z++) {
                    if (portalLoc.getWorld().getBlockAt(x,y,z).getType() != Material.AIR) {
                        player.sendMessage(Util.formatMsg("&cPortal can't be created here!"));
                        player.sendMessage(Util.formatMsg("&cThere has to be enough empty space above you."));
                        return;
                    }
                }
            }
        }

        //Try create the portal
        int typeID = CWUtil.random(0, dvz.getCfg().PORTAL_TYPES - 1);
        try {
            if (onCooldown(player)) {
                return;
            }
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(portalLoc.getWorld(), CWWorldGuard.getSchematicFile("portal-" + typeID), portalLoc, true, 0, true);

            Location min = new Location(portalLoc.getWorld(), portalLoc.getBlockX() + cc.getOffset().getBlockX(), portalLoc.getBlockY() + cc.getOffset().getBlockY(), portalLoc.getBlockZ() + cc.getOffset().getBlockZ());
            Cuboid cuboid = new Cuboid(min, cc.getWidth()-1, cc.getHeight()-1, cc.getLength()-1);

            portalLoc.getWorld().playSound(portalLoc, Sound.WITHER_SPAWN, 2, 0);
            List<Block> blocks = cuboid.getBlocks();
            for (Block block : blocks) {
                if (block.getType() != Material.AIR) {
                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), (byte)block.getData()), 0.5f, 0.5f, 0.5f, 0.1f, 10, block.getLocation(), 500);
                    ParticleEffect.PORTAL.display(1, 1, 1, 0, 5, block.getLocation().add(0.5f, 0.5f, 0.5f), 500);
                }
            }

            activePortal = new PortalData(player, portalLoc, cuboid);

            player.teleport(portalLoc);
            CWUtil.removeItemsFromHand(player, 1);
            dvz.getSM().changeLocalStatVal(player, StatType.MONSTER_PORTALS_CREATED, 1);

            Util.broadcast("&dPortal created by &5" + player.getName() + "&d!");
            Util.broadcast("&7Monsters can &dtp &7to it by using the &dportal block&7.");
            Util.broadcast("&7Dwarves can destroy it by &dbreaking the egg &7or &dkilling the enderman&7!");
            player.sendMessage(Util.formatMsg("&6Portal created! &7Click the egg if you want to deactivate the portal."));
            return;
        } catch (CommandException e) {
            e.printStackTrace();
        } catch (FilenameException e) {
            e.printStackTrace();
        } catch (com.sk89q.worldedit.world.DataException e) {
            e.printStackTrace();
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendMessage(Util.formatMsg("&cError while creating portal."));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);

        //Check for clicking the egg for downvoting as monster and destroying it as dwarf and for enderman to destroy it.
        Player player = event.getPlayer();
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock().getType() != Material.DRAGON_EGG) {
            return;
        }

        event.setCancelled(true);

        if (activePortal == null) {
            return;
        }

        Player portalOwner = activePortal.getOwner();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (cwp.isDwarf()) {
            dvz.getSM().changeLocalStatVal(player, StatType.DWARF_PORTALS_DESTROYED, 1);
            Util.broadcast("&cThe portal has been destroyed by &4" + player.getName());
            portalOwner.sendMessage(Util.formatMsg("&cYou have been killed because your portal was destroyed!"));
            destroyPortal(true);
            return;
        }

        if (portalOwner.getName().equals(player.getName())) {
            portalOwner.sendMessage(Util.formatMsg("&cYou have been killed because you deactivated your portal!"));
            destroyPortal(true);
            return;
        }

        if (cwp.isMonster()) {
            if (activePortal.downvotes.contains(player.getUniqueId())) {
                activePortal.downvotes.remove(player.getUniqueId());
                player.sendMessage(Util.formatMsg("&6Downvote for portal deactivation &cremoved&6."));
            } else {
                activePortal.downvotes.add(player.getUniqueId());
                cwp.getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ANVIL_LAND, 1, 2);
                ParticleEffect.SMOKE_LARGE.display(0.5f, 0.5f, 0.5f, 0, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f));
                player.sendMessage(Util.formatMsg("&6Downvote for portal deactivation &aadded&6. &8[&a" + activePortal.downvotes.size() + "&7/&25&8]"));

                //Enough votes so remove the portal.
                if (activePortal.downvotes.size() >= 5) {
                    dvz.getServer().broadcastMessage(Util.formatMsg("&cThe portal has been destroyed by downvoting!"));
                    portalOwner.sendMessage(Util.formatMsg("&cYou have been killed because your portal has been deactivated!"));
                    destroyPortal(true);
                }
            }
        }
    }

    @EventHandler
    private void projectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if(!(projectile.getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player)projectile.getShooter();
        World world = projectile.getWorld();

        BlockIterator iterator = new BlockIterator(world, projectile.getLocation().toVector(), projectile.getVelocity().normalize(), 0, 4);
        Block hitBlock = null;
        while(iterator.hasNext()) {
            hitBlock = iterator.next();
            if(hitBlock.getType() != Material.AIR) {
                break;
            }
        }

        if (hitBlock.getType() == Material.DRAGON_EGG) {
            if (activePortal == null) {
                return;
            }
            CWPlayer cwp = dvz.getPM().getPlayer(player);
            if (cwp.isDwarf()) {
                dvz.getSM().changeLocalStatVal(player, StatType.DWARF_PORTALS_DESTROYED, 1);
                Util.broadcast("&cThe portal has been destroyed by &4" + player.getName());
                activePortal.getOwner().sendMessage("&cYou have been killed because your portal was shot!");
                destroyPortal(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void endermanDeath(PlayerDeathEvent event) {
        if (dvz.getPM().getPlayer(event.getEntity()).getPlayerClass() == DvzClass.ENDERMAN) {
            if (activePortal == null || !activePortal.getOwner().equals(event.getEntity())) {
                return;
            }

            if (event.getEntity().getKiller() != null) {
                dvz.getSM().changeLocalStatVal(event.getEntity().getKiller(), StatType.DWARF_PORTALS_DESTROYED, 1);
            }

            Util.broadcast("&cThe portal has been destroyed!");
            destroyPortal(false);
        }
    }

    @EventHandler
    private void logout(PlayerQuitEvent event) {
        if (dvz.getPM().getPlayer(event.getPlayer()).getPlayerClass() == DvzClass.ENDERMAN) {
            if (activePortal == null || !activePortal.getOwner().equals(event.getPlayer())) {
                return;
            }

            Util.broadcast("&cThe portal has been destroyed because the enderman logged off!");
            destroyPortal(false);
        }
    }

    @EventHandler
    private void gameReset(GameResetEvent event) {
        destroyPortal(true);
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        destroyPortal(true);
    }

    public void destroyPortal(boolean kill) {
        if (activePortal == null || activePortal.getCuboid() == null) {
            return;
        }

        if (activePortal.getEgg() != null) {
            activePortal.getEgg().getBlock().setType(Material.AIR);
        }

        ParticleEffect.EXPLOSION_HUGE.display(0, 3, 0, 0, 5, activePortal.getEgg().getBlock().getLocation(), 500);
        new BukkitRunnable() {
            Location loc = activePortal.getLocation();

            int index = 0;
            @Override
            public void run() {
                index++;
                if (index >= 6) {
                    cancel();
                    return;
                }
                loc.getWorld().playSound(loc, Sound.EXPLODE, 5, 0.5f);
            }
        }.runTaskTimer(dvz, 0, 3);

        //Slowly remove blocks.
        new BukkitRunnable() {
            List<Block> portalBlocks = activePortal.getCuboid().getBlocks(new Material[] {Material.ENDER_STONE, Material.OBSIDIAN, Material.GLOWSTONE});

            @Override
            public void run() {
                if (portalBlocks.size() <= 0) {
                    cancel();
                    return;
                }
                Block b = CWUtil.random(portalBlocks);
                portalBlocks.remove(b);
                if (b.getType() == Material.ENDER_STONE || b.getType() == Material.OBSIDIAN || b.getType() == Material.GLOWSTONE) {
                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(b.getType(), (byte)b.getData()), 0.5f, 0.5f, 0.5f, 0.1f, 10, b.getLocation(), 500);
                    ParticleEffect.PORTAL.display(1, 1, 1, 0, 8, b.getLocation().add(0.5f, 0.5f, 0.5f), 500);
                    b.setType(Material.AIR);
                }
            }
        }.runTaskTimer(dvz, 0, 5);

        Player portalOwner = activePortal.getOwner();
        activePortal = null;

        //Kill owner if needed. (needs to be last because of the check of enderman death will also remove the portal)
        if (kill) {
            new CustomDmg(portalOwner, 20, "{0} died because the portal got destroyed", "portal destroy");
        }
    }
}

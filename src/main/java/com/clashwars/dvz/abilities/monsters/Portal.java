package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.ExpandingCircleEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.extra.PortalData;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.io.IOException;

//There can be multiple portal schematics.
//Name the schematics like portal-0, portal-1 etc..
public class Portal extends MobAbility {

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
        for (int x = portalLoc.getBlockX() - 5; x < portalLoc.getBlockX() + 5; x++) {
            for (int y = portalLoc.getBlockX() - 5; y < portalLoc.getBlockX() + 5; y++) {
                for (int z = portalLoc.getBlockX() - 5; z < portalLoc.getBlockX() + 5; z++) {
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
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(portalLoc.getWorld(), CWWorldGuard.getSchematicFile("portal-" + typeID), portalLoc, true, 0, true);

            Location min = new Location(portalLoc.getWorld(), portalLoc.getBlockX() + cc.getOffset().getBlockX(), portalLoc.getBlockY() + cc.getOffset().getBlockY(), portalLoc.getBlockZ() + cc.getOffset().getBlockZ());
            Cuboid cuboid = new Cuboid(min, cc.getWidth()-1, cc.getHeight()-1, cc.getLength()-1);

            activePortal = new PortalData(player, portalLoc, cuboid);

            dvz.getServer().broadcastMessage(Util.formatMsg("&dPortal created by &5" + player.getName() + "&d!"));
            dvz.getServer().broadcastMessage(Util.formatMsg("&7Monsters can &dtp &7to it by using the &dportal block&7."));
            dvz.getServer().broadcastMessage(Util.formatMsg("&7Dwarves can destroy it by &dbreaking the egg &7or &dkilling the enderman&7!"));
            player.sendMessage("&6Portal created! &7Click the egg if you want to deactivate the portal.");
            return;
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FilenameException e) {
            e.printStackTrace();
        } catch (CommandException e) {
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

        if (activePortal == null) {
            return;
        }

        Player portalOwner = activePortal.getOwner();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (cwp.isDwarf()) {
            dvz.getServer().broadcastMessage(Util.formatMsg("&cThe portal has been deactivated by &4" + player.getName()));
            portalOwner.sendMessage("&cYou have been killed because your portal was destroyed!");
            portalOwner.setHealth(0);
            activePortal.getEgg().getBlock().setType(Material.AIR);
            activePortal = null;
            return;
        }

        if (portalOwner.getName().equals(player.getName())) {
            portalOwner.sendMessage("&cYou have been killed because you deactivated your portal!");
            portalOwner.setHealth(0);
            activePortal.getEgg().getBlock().setType(Material.AIR);
            activePortal = null;
            return;
        }

        if (cwp.isMonster()) {
            if (activePortal.downvotes.contains(player.getUniqueId())) {
                activePortal.downvotes.remove(player.getUniqueId());
                player.sendMessage(Util.formatMsg("&6Downvote for portal deactivation &cremoved&6."));
            } else {
                activePortal.downvotes.add(player.getUniqueId());
                player.sendMessage(Util.formatMsg("&6Downvote for portal deactivation &aadded&6. &8[&a" + activePortal.downvotes.size() + "&7/&25&8]"));
                portalOwner.sendMessage(Util.formatMsg("&cYou received a downvote for your portal. &8[&c" + activePortal.downvotes.size() + "&7/&45&8]"));

                //Enough votes so remove the portal.
                if (activePortal.downvotes.size() >= 5) {
                    portalOwner.sendMessage("&cYou have been killed because your portal has been deactivated!");
                    portalOwner.setHealth(0);
                    activePortal.getEgg().getBlock().setType(Material.AIR);
                    activePortal = null;
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
                dvz.getServer().broadcastMessage(Util.formatMsg("&cThe portal has been deactivated by &4" + player.getName()));
                activePortal.getOwner().sendMessage("&cYou have been killed because your portal was destroyed!");
                activePortal.getOwner().setHealth(0);
                activePortal.getEgg().getBlock().setType(Material.AIR);
                activePortal = null;
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void endermanDeath(PlayerDeathEvent event) {
        if (dvz.getPM().getPlayer(event.getEntity()).getPlayerClass() == DvzClass.ENDERMAN) {
            dvz.getServer().broadcastMessage(Util.formatMsg("&cThe portal has been deactivated!"));
            activePortal.getEgg().getBlock().setType(Material.AIR);
            activePortal = null;
        }
    }
}

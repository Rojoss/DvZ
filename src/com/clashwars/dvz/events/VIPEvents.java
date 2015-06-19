package com.clashwars.dvz.events;

import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.VIP.BannerData;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class VIPEvents implements Listener {

    private DvZ dvz;

    public VIPEvents(DvZ dvz) {
        this.dvz = dvz;

        new BukkitRunnable() {
            @Override

            public void run() {
                for (Player player : DvZ.inst().getServer().getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        continue;
                    }
                    if (player.hasPermission("rank.admin")) {
                        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte)0), 0.3f, 0.1f, 0.3f, 0, 3, player.getLocation());
                    } else if (player.hasPermission("rank.mod")) {
                        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.EMERALD_BLOCK, (byte)0), 0.3f, 0.1f, 0.3f, 0, 3, player.getLocation());
                    } else if (player.hasPermission("rank.helper")) {
                        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.PRISMARINE, (byte)0), 0.3f, 0.1f, 0.3f, 0, 3, player.getLocation());
                    } else if (player.hasPermission("vip.diamond")) {
                        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.DIAMOND_BLOCK, (byte)0), 0.3f, 0.1f, 0.3f, 0, 3, player.getLocation());
                    } else if (player.hasPermission("vip.gold")) {
                        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.GOLD_BLOCK, (byte)0), 0.3f, 0.1f, 0.3f, 0, 2, player.getLocation());
                    } else if (player.hasPermission("vip.iron")) {
                        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.IRON_BLOCK, (byte)0), 0.3f, 0.1f, 0.3f, 0, 1, player.getLocation());
                    }
                }
            }
        }.runTaskTimer(dvz, 3, 3);
    }

    @EventHandler(ignoreCancelled = true)
    private void onDamageTake(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (player.hasPermission("rank.admin")) {
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte)0), 0.3f, 1f, 0.3f, 0, 20, player.getLocation().add(0,1,0));
        } else if (player.hasPermission("rank.mod")) {
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.EMERALD_BLOCK, (byte)0), 0.3f, 1f, 0.3f, 0, 20, player.getLocation().add(0, 1, 0));
        } else if (player.hasPermission("rank.helper")) {
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.PRISMARINE, (byte)0), 0.3f, 1f, 0.3f, 0, 20,player.getLocation().add(0, 1, 0));
        } else if (player.hasPermission("vip.diamond")) {
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.DIAMOND_BLOCK, (byte)0), 0.3f, 1f, 0.3f, 0, 20, player.getLocation().add(0, 1, 0));
        } else if (player.hasPermission("vip.gold")) {
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.GOLD_BLOCK, (byte)0), 0.3f, 1f, 0.3f, 0, 15, player.getLocation().add(0, 1, 0));
        } else if (player.hasPermission("vip.iron")) {
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.IRON_BLOCK, (byte)0), 0.3f, 1f, 0.3f, 0, 10, player.getLocation().add(0,1,0));
        }
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        Material blockType = Material.STONE;
        final Location loc = player.getLocation().add(0, 1, 0);
        if (player.hasPermission("rank.admin")) {
            blockType = Material.REDSTONE_BLOCK;
        } else if (player.hasPermission("rank.mod")) {
            blockType = Material.EMERALD_BLOCK;
        } else if (player.hasPermission("rank.helper")) {
            blockType = Material.PRISMARINE;
        } else if (player.hasPermission("vip.diamond")) {
            blockType = Material.DIAMOND_BLOCK;
        } else if (player.hasPermission("vip.gold")) {
            blockType = Material.GOLD_BLOCK;
        } else if (player.hasPermission("vip.iron")) {
            blockType = Material.IRON_BLOCK;
        }

        if (blockType == Material.STONE) {
            return;
        }

        final Material blockTypeF = blockType;
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i > 100) {
                    cancel();
                    return;
                }
                i++;
                ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(blockTypeF, (byte)0), 0.8f, 1.3f, 0.8f, 0, 15, loc);
            }
        }.runTaskTimer(dvz, 0, 1);

    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        //Clicking on dispenser to open armor coloring.
        if (block.getType() == Material.DISPENSER && block.getData() == 1) {
            event.setCancelled(true);
            dvz.getArmorMenu().showMenu(player);
            return;
        }

        //Clicking on banner to edit it.
        if (block.getType() == Material.WALL_BANNER || block.getType() == Material.STANDING_BANNER) {
            for (Map.Entry<UUID, BannerData> banner : dvz.getBannerCfg().getBanners().entrySet()) {
                if (banner.getValue().getBannerLocations() != null && banner.getValue().getBannerLocations().contains(block.getLocation().toVector())) {
                    if (!banner.getKey().equals(player.getUniqueId())) {
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&3&l>> &bThis is " + dvz.getServer().getPlayer(banner.getKey()).getDisplayName() + " &bhis banner! &3&l<<"));
                        event.setCancelled(true);
                        return;
                    }
                    dvz.getBannerMenu().showMenu(player);
                    return;
                }
            }
            if (player.hasPermission("banner.customization")) {
                dvz.getBannerMenu().showMenu(player);
            } else {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&3&l>> &bPurchase VIP to be able to place and modify banners! &3&l<<"));
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.STANDING_BANNER &&block.getType() != Material.WALL_BANNER) {
            return;
        }

        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        WorkShop ws = dvz.getWM().locGetWorkShop(block.getLocation());
        if (ws != null && !ws.isOwner(player)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't place banners in other people their workshop! &4&l<<"));
            return;
        }

        event.setCancelled(false);
        BannerData data = dvz.getBannerCfg().getBanner(player.getUniqueId());
        if (data == null) {
            data = new BannerData();
            data.setBaseColor(DyeColor.WHITE);
        }
        data.addBannerLocation(block.getLocation().toVector());
        dvz.getBannerCfg().setBanner(player.getUniqueId(), data);
        dvz.getBannerMenu().tempBanners.put(player.getUniqueId(), data);

        CWUtil.sendActionBar(player, CWUtil.integrateColor("&3&l>> &bBanner placed! &3&l<<"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() != Material.STANDING_BANNER && block.getType() != Material.WALL_BANNER && block.getRelative(BlockFace.UP).getType() != Material.STANDING_BANNER) {
            block = block.getRelative(BlockFace.UP);
            if (block.getType() != Material.BANNER) {
                return;
            }
        }

        for (Map.Entry<UUID, BannerData> banner : dvz.getBannerCfg().getBanners().entrySet()) {
            if (banner.getValue().getBannerLocations() != null && banner.getValue().getBannerLocations().contains(block.getLocation().toVector())) {
                if (!banner.getKey().equals(player.getUniqueId())) {
                    CWUtil.sendActionBar(player, CWUtil.integrateColor("&3&l>> &bThis is " + dvz.getServer().getPlayer(banner.getKey()).getDisplayName() + " &bhis banner! &3&l<<"));
                    event.setCancelled(true);
                    return;
                }

                event.setCancelled(false);
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&3&l>> &bBanner removed! &3&l<<"));
                banner.getValue().removeBannerLocation(block.getLocation().toVector());
                dvz.getBannerCfg().setBanner(banner.getKey(), banner.getValue());
                dvz.getBannerMenu().tempBanners.put(banner.getKey(), banner.getValue());
                Product.VIP_BANNER.getItem().setBaseColor(banner.getValue().getBaseColor()).setPatterns(banner.getValue().getPatterns()).giveToPlayer(player);
                return;
            }
        }
    }

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                equipHat(event.getPlayer());
            }
        }.runTaskLater(dvz, 20);
    }

    @EventHandler
     private void onEnable(PluginEnableEvent event) {
        for (Player player : dvz.getServer().getOnlinePlayers()) {
            equipHat(player);
        }
    }

    private void equipHat(Player player) {
        CWItem hatItem = null;
        if (player.hasPermission("hat.iron"))
            hatItem = new CWItem(Material.ANVIL);
        if (player.hasPermission("hat.gold"))
            hatItem = new CWItem(Material.GOLDEN_APPLE);
        if (player.hasPermission("hat.diamond"))
            hatItem = new CWItem(Material.DIAMOND);
        if (player.hasPermission("hat.helper"))
            hatItem = new CWItem(Material.PRISMARINE_SHARD);
        if (player.hasPermission("hat.mod"))
            hatItem = new CWItem(Material.EMERALD);
        if (player.hasPermission("hat.admin"))
            hatItem = new CWItem(Material.REDSTONE);

        if (hatItem != null) {
            new Hat(player, hatItem);
        }
    }

}

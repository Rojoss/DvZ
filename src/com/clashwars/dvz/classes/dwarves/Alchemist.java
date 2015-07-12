package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.BoilEffect;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.AlchemistWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Alchemist extends DwarfClass {

    public Alchemist() {
        super();
        dvzClass = DvzClass.ALCHEMIST;
        classItem = new DvzItem(Material.POTION, 1, (byte)0, "&5&lAlchemist", 60, -1);

        equipment.add(new DvzItem(Material.IRON_AXE, 1, -1, -1));
        equipment.add(new DvzItem(Material.IRON_PICKAXE, 1, -1, -1));

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&5&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        equipment.add(new DvzItem(Material.BUCKET, 1, -1, -1));
    }


    //Check for sneaking inside the pot to give player upward velocity to get out.
    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event) {
        Long t = System.currentTimeMillis();
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }

        WorkShop ws = dvz.getWM().locGetWorkShop(player.getLocation());
        if (ws == null || !(ws instanceof AlchemistWorkshop)) {
            dvz.logTimings("Alchemist.onSneak()[not in alch ws]", t);
            return;
        }

        if (((AlchemistWorkshop)ws).getPot().contains(player)) {
            player.setVelocity(player.getVelocity().setY(1.3f));
        }
        dvz.logTimings("Alchemist.onSneak()", t);
    }

    //Check for using buckets/bottles on cauldrons.
    @EventHandler(priority = EventPriority.HIGH)
    private void onInteract(DelayedPlayerInteractEvent event) {
        Long t = System.currentTimeMillis();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.CAULDRON && block.getType() != Material.CHEST) {
            return;
        }

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            if (block.getType() == Material.CAULDRON) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need to be an alchemist to empty cauldrons! &4&l<<"));
            }
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            return;
        }

        AlchemistWorkshop ws = (AlchemistWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.isBuild()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            dvz.logTimings("Alchemist.onInteract()[no workshop]", t);
            return;
        }

        if (!ws.getCuboid().contains(block.getLocation())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your " + block.getType().toString().toLowerCase() + "! &4&l<<"));
            dvz.logTimings("Alchemist.onInteract()[other workshop]", t);
            return;
        }

        event.setCancelled(false);
        if (block.getType() == Material.CAULDRON) {
            if (event.getItem() == null || event.getItem().getType() != Material.BUCKET) {
                event.setCancelled(true);
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cUse a empty bucket to empty the cauldron! &4&l<<"));
                dvz.logTimings("Alchemist.onInteract()[not holding bucket]", t);
                return;
            }
            if (block.getData() != 3) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis cauldron isn't full yet! &4&l<<"));
                event.setCancelled(true);
                dvz.logTimings("Alchemist.onInteract()[cauldron not full]", t);
                return;
            }

            player.playSound(block.getLocation(), Sound.SPLASH2, 1.0f, 2.0f);
            player.getItemInHand().setType(Material.WATER_BUCKET);
            block.setData((byte)0);
            dvz.getPM().getPlayer(player).addClassExp(1);
            dvz.getSM().changeLocalStatVal(player, StatType.ALCHEMIST_CAULDRONS_EMPTIED, 1);
        }
        dvz.logTimings("Alchemist.onInteract()", t);
    }

    //Check for emptying bucket in pot.
    @EventHandler(priority = EventPriority.HIGH)
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Long t = System.currentTimeMillis();
        if (event.getBucket() != Material.WATER_BUCKET) {
            return;
        }

        final Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            dvz.logTimings("Alchemist.onBucketEmpty()[no workshop]", t);
            return;
        }

        final AlchemistWorkshop ws = (AlchemistWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.isBuild()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            dvz.logTimings("Alchemist.onBucketEmpty()[ws not build]", t);
            return;
        }

        final Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
        if (!ws.getPot().contains(loc)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cWater has to be placed inside your pot! &4&l<<"));
            dvz.logTimings("Alchemist.onBucketEmpty()[outside pot]", t);
            return;
        }

        if (ws.potFilled) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cPot already filled! &7Add melons OR sugar now. &4&l<<"));
            dvz.logTimings("Alchemist.onBucketEmpty()[already filled]", t);
            return;
        }

        event.setCancelled(false);
        ws.runnables.add(new BukkitRunnable() {
            @Override
            public void run() {
                checkPotFilled(ws);
            }
        }.runTaskLater(dvz, 30));
        dvz.logTimings("Alchemist.onBucketEmpty()", t);
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        Long t = System.currentTimeMillis();
        final Block block = event.getBlock();
        if (block.getType() != Material.MELON_BLOCK && block.getType() != Material.SUGAR_CANE_BLOCK) {
            return;
        }

        if (block.getData() != 0) {
            return;
        }
        final Material originalType = block.getType();

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            if (block.getType() == Material.MELON_BLOCK) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need to be an alchemist to collect melons! &4&l<<"));
            } else {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need to be an alchemist to collect sugar(cane)! &4&l<<"));
            }
            return;
        }

        if (block.getType() == Material.MELON_BLOCK) {
            event.setCancelled(false);
            CWUtil.dropItemStack(block.getLocation(), Product.MELON.getItem(3), dvz, player);
            dvz.getPM().getPlayer(player).addClassExp(3);

            dvz.getSM().changeLocalStatVal(player, StatType.ALCHEMIST_MELONS_COLLECTED, 1);

            new BukkitRunnable() {
                @Override
                public void run()   {
                    if (block.getType() == Material.AIR) {
                        block.setType(originalType);
                        ParticleEffect.VILLAGER_HAPPY.display(0.7f, 0.7f, 0.7f, 0.0001f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
                        block.getWorld().playSound(block.getLocation(),Sound.DIG_GRASS, 1.0f, 1.3f);
                    }
                }

            }.runTaskLater(dvz, getIntOption("melon-respawn-time"));
            dvz.logTimings("Alchemist.blockBreak()[melon]", t);
        } else if (block.getType() == Material.SUGAR_CANE_BLOCK) {
            event.setCancelled(true);
            Block currentBlock = block;
            List<Block> sugarcane = new ArrayList<Block>();
            sugarcane.add(currentBlock);

            while (currentBlock.getRelative(BlockFace.UP).getType() == Material.SUGAR_CANE_BLOCK) {
                currentBlock = currentBlock.getRelative(BlockFace.UP);
                sugarcane.add(currentBlock);
            }

            CWUtil.dropItemStack(block.getLocation(), Product.SUGAR.getItem(sugarcane.size()), dvz, player);
            dvz.getPM().getPlayer(player).addClassExp(sugarcane.size());

            dvz.getSM().changeLocalStatVal(player, StatType.ALCHEMIST_SUGAR_COLLECTED, sugarcane.size());

            for (Block sugarcaneBlock : sugarcane) {
                sugarcaneBlock.setType(Material.AIR);
                sugarcaneBlock.getWorld().playEffect(sugarcaneBlock.getLocation(), Effect.STEP_SOUND, 83);
            }

            final Block floorBlock;
            currentBlock = block;
            while (currentBlock.getRelative(BlockFace.DOWN).getType() != Material.SAND && currentBlock.getRelative(BlockFace.DOWN).getType() != Material.GRASS)   {
                currentBlock = currentBlock.getRelative(BlockFace.DOWN);
            }
            floorBlock = currentBlock.getRelative(BlockFace.DOWN);

            new BukkitRunnable() {
                @Override
                public void run()   {
                    for (int i = 0; i < 3; i++) {
                        Block sugarcaneBlock = floorBlock.getRelative(0,i+1,0);
                        if (sugarcaneBlock.getType() == Material.AIR) {
                            sugarcaneBlock.setType(originalType);
                            ParticleEffect.VILLAGER_HAPPY.display(0.7f, 0.7f, 0.7f, 0.0001f, 5, sugarcaneBlock.getLocation().add(0.5f, 0.5f, 0.5f));
                            sugarcaneBlock.getWorld().playSound(sugarcaneBlock.getLocation(), Sound.DIG_GRASS, 1.0f, 1.3f);
                            return;
                        }
                    }
                    cancel();
                }
            }.runTaskTimer(dvz, getIntOption("sugarcane-respawn-time"), getIntOption("sugarcane-respawn-time"));
            dvz.logTimings("Alchemist.blockBreak()[sugarcane]", t);
        }
    }


    //Check for dropping ingredients in the pot.
    @EventHandler(priority = EventPriority.HIGH)
    private void onItemDrop(PlayerDropItemEvent event) {
        final Long t = System.currentTimeMillis();
        final Item item = event.getItemDrop();

        final Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            dvz.logTimings("Alchemist.itemDrop()[no workshop]", t);
            return;
        }

        final AlchemistWorkshop ws = (AlchemistWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.isBuild()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            dvz.logTimings("Alchemist.itemDrop()[ws not build]", t);
            return;
        }

        ws.runnables.add(new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = item.getLocation();
                Cuboid potClone = ws.getPot().clone();
                potClone.expand(Cuboid.Dir.UP, 3);

                if (potClone.contains(loc)) {
                    if (!ws.potFilled) {
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cFill your pot with water before adding ingredients! &4&l<<"));
                        dvz.logTimings("Alchemist.itemDrop()[not boiling]", t);
                        return;
                    }

                    ItemStack itemStack = item.getItemStack();
                    if (itemStack.getType() == Material.MELON) {
                        if (ws.sugar > 0) {
                            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBrewing failed! &7(You can't add both melons and sugar) &4&l<<"));
                            wrongIngredientAdded(ws);
                            ParticleEffect.VILLAGER_ANGRY.display(0.2f, 0.4f, 0.2f, 0.0001f, 5, item.getLocation());
                        } else {
                            player.playSound(item.getLocation(), Sound.SPLASH2, 0.8f, 1.0f);
                            ParticleEffect.SPELL_WITCH.display(0.2f, 0.4f, 0.2f, 0.0001f, 10, item.getLocation());

                            item.remove();
                            ws.melons += itemStack.getAmount();
                            if (ws.melons >= getIntOption("melons-needed")) {
                                brew(ws);
                            } else {
                                CWUtil.sendActionBar(player, CWUtil.integrateColor("&5&l>> &dMelons added! &8(" + ws.melons + "&7/" + getIntOption("melons-needed") + "&8) &5&l<<"));
                            }
                        }
                    } else if (itemStack.getType() == Material.SUGAR) {
                        if (ws.melons > 0) {
                            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBrewing failed! &7(You can't add both melons and sugar) &4&l<<"));
                            wrongIngredientAdded(ws);
                            ParticleEffect.VILLAGER_ANGRY.display(0.2f, 0.4f, 0.2f, 0.0001f, 5, item.getLocation());
                        } else {
                            player.playSound(item.getLocation(), Sound.SPLASH2, 0.8f, 1.0f);
                            ParticleEffect.SPELL_WITCH.display(0.2f, 0.4f, 0.2f, 0.0001f, 10, item.getLocation());

                            item.remove();
                            ws.sugar += itemStack.getAmount();
                            if (ws.sugar >= getIntOption("sugar-needed")) {
                                brew(ws);
                            } else {
                                CWUtil.sendActionBar(player, CWUtil.integrateColor("&5&l>> &dSugar added! &8(" + ws.sugar + "&7/" + getIntOption("sugar-needed") + "&8) &5&l<<"));
                            }
                        }
                    } else {
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBrewing failed! &7(You can't brew potions with" + item.getType().toString().toLowerCase().replace("_", " ") + ") &4&l<<"));
                        wrongIngredientAdded(ws);
                        ParticleEffect.VILLAGER_ANGRY.display(0.2f, 0.4f, 0.2f, 0.0001f, 5, item.getLocation());
                    }
                }
            }
        }.runTaskLater(dvz, 10));
        dvz.logTimings("Alchemist.itemDrop()", t);
    }

    private void brew(AlchemistWorkshop ws) {
        Long t = System.currentTimeMillis();
        CWUtil.sendActionBar(ws.getOwner(), CWUtil.integrateColor("&5&l>> &dPotion brewed! &5&l<<"));

        //Put item in chest and if chest is full drop it at chest.
        CWItem item;
        if (ws.melons > 0) {
            item = Ability.HEAL_POTION.getAbilityClass().getCastItem();
            dvz.getSM().changeLocalStatVal(ws.getOwner(), StatType.ALCHEMIST_HEALTH_POTS, 1);
        } else {
            item = Ability.SPEED_POTION.getAbilityClass().getCastItem();
            dvz.getSM().changeLocalStatVal(ws.getOwner(), StatType.ALCHEMIST_SPEED_POTS, 1);
        }
        if (item != null) {
            boolean added = false;
            for (int i = 0; i < ws.getChest().getSize(); i++) {
                if (ws.getChest().getItem(i) == null || ws.getChest().getItem(i).getType() == Material.AIR) {
                    ws.getChest().addItem(item);
                    added = true;
                    break;
                }
            }
            if (!added) {
                CWUtil.dropItemStack(ws.getChestLoc(), item, dvz, ws.getOwner());
            }
            ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 30, ws.getChestLoc().add(0.5f, 0.5f, 0.5f));
        }

        //Reset
        ws.boilEffect.cancel();
        ws.boilEffect = null;
        ws.potFilled = false;
        ws.melons = 0;
        ws.sugar = 0;

        dvz.getPM().getPlayer(ws.getOwner()).addClassExp(70);
        // + 3 per melon broken
        // + 1 per sugarcane broken
        // + 1 per bucket filled
        // = 84

        //Clear water/Effect
        List<Block> waterBlocks = ws.getPot().getBlocks(new Material[]{Material.STATIONARY_WATER});
        for (Block block : waterBlocks) {
            block.setType(Material.AIR);
            ParticleEffect.SMOKE_NORMAL.display(0.3f, 0.3f, 0.3f, 0.0001f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
        }
        dvz.logTimings("Alchemist.brew()", t);
    }

    private void checkPotFilled(AlchemistWorkshop ws) {
        List<Block> waterBlocks = ws.getPot().getBlocks(new Material[]{Material.STATIONARY_WATER});
        if (waterBlocks.size() >= 18) {
            for (Block block : waterBlocks) {
                block.setType(Material.STATIONARY_WATER);
            }

            ws.potFilled = true;
            CWUtil.sendActionBar(ws.getOwner(), CWUtil.integrateColor("&5&l>> &dPot filled! &7Now add sugar OR melons. &5&l<<"));

            createBoilEffect(ws);
        }
    }

    public void wrongIngredientAdded(AlchemistWorkshop ws) {
        Long t = System.currentTimeMillis();
        //Reset
        ws.boilEffect.cancel();
        ws.boilEffect = null;
        ws.potFilled = false;
        ws.melons = 0;
        ws.sugar = 0;

        //Clear water and Effect/Sound
        ws.getOwner().playSound(ws.getOwner().getLocation(), Sound.FIZZ, 1.0f, 1.0f);
        Cuboid potTop = ws.getPot().clone();
        potTop.contract(Cuboid.Dir.DOWN, 1);
        List<Block> waterBlocks = potTop.getBlocks(new Material[]{Material.STATIONARY_WATER});
        for (Block block : waterBlocks) {
            block.setType(Material.AIR);
            ParticleEffect.SMOKE_NORMAL.display(0.3f, 0.3f, 0.3f, 0.0001f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
        }
        dvz.logTimings("Alchemist.wrongIngredientAdded()", t);
    }

    private void createBoilEffect(AlchemistWorkshop ws) {
        ws.boilEffect = new BoilEffect(dvz.getEM());
        ws.boilEffect.setLocation(ws.getOrigin().add(0,1,0));
        ws.boilEffect.particleList.add(new Particle(ParticleEffect.WATER_BUBBLE, 1, 0.8f, 1, 0, 40));
        ws.boilEffect.particleList.add(new Particle(ParticleEffect.SMOKE_LARGE, 1, 1.5f, 1, 0, 4));
        ws.boilEffect.soundVolume = 0.15f;
        ws.boilEffect.soundPitch = 1.5f;
        ws.boilEffect.soundDelay = 10;
        ws.boilEffect.start();
    }

}

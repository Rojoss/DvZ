package com.clashwars.dvz.tips;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TipManager {

    List<Tip> tips = new ArrayList<Tip>();

    public TipManager() {

        //-- DWARVES
        //Builder
        addTip("&7You can reinforce cracked stone by clicking on it while holding another cracked stone brick!", DvzClass.BUILDER, new String[] {"reinforce|reinforcement", "stone|bricks|brick|blocks|wall|walls|stonebrick|stonebricks"});
        addTip("&7If you reinforce cracked stone it will have double durability!", DvzClass.BUILDER, new String[] {"reinforce|reinforcement"});
        addTip("&7Place the building block down to create a small pillar! (This will let you build faster!)", DvzClass.BUILDER, new String[] {"stone|build|building", "block"});
        addTip("&7You can use the building brick to build from a long distance. (Hold down your right mouse button to build faster!)", DvzClass.BUILDER, new String[] {"stone|build|building", "brick"});
        addTip("&7Right click your pickaxe to summon stone to build with.", DvzClass.BUILDER, new String[] {"build|spawn|summon|get|how|where", "with|material|materials|block|blocks"});
        addTip("&7You should build more walls between the keep and the monster spawn!.", DvzClass.BUILDER, new String[] {"where|what", "build|do"});

        //Miner
        addTip("&7Ores spawn randomly in your workshop, so make sure to keep mining!", DvzClass.MINER, new String[] {"collect|find|get|mine", "iron|gold|diamond|ore|ores|diamonds"});
        addTip("&7Place the mined ores in one of the furnace structures to let them smelt.", DvzClass.MINER, new String[] {"smelt|melt|cook", "iron|gold|diamond|ore|ores|diamonds"});
        addTip("&7You Mine stone and ores, smelt the ores in the furnace and craft weapons with the ingots.", DvzClass.MINER, new String[] {"what", "do"});

        //Fletcher
        addTip("&7Gravel can be found outside the keep! Dig it to collect flint.", DvzClass.FLETCHER, new String[] {"collect|find|get|gather", "gravel|flint"});
        addTip("&7You have a 50% chance to make a bow when you craft!", DvzClass.FLETCHER, new String[] {"collect|find|get|make", "bow|bows"});
        addTip("&7Chickens spawn above your workshop. Kill the chickens to gain feathers. Shoot them in the air to get an extra feather drop!", DvzClass.FLETCHER, new String[] {"collect|find|get|make", "feather|feathers|chicken|chickens"});
        addTip("&7You collect flint outside the keep, kill chickens for feathers and craft bows and arrows.", DvzClass.FLETCHER, new String[] {"what", "do"});

        //Tailor
        addTip("&7Flowers can be found outside the keep. Dyes will drop when you break them!", DvzClass.TAILOR, new String[] {"collect|find|get|gather|pick", "flowers|flower|dye|dyes"});
        addTip("&7Shear the sheep in your workshop to get wool. The sheep will regrow their wool!", DvzClass.TAILOR, new String[] {"collect|find|get|gather|shear", "sheep|sheeps|wool|cloth"});
        addTip("&7You collect flowers outside the keep, shear sheep and craft armor!", DvzClass.TAILOR, new String[] {"what", "do"});

        //Alchemist
        addTip("&7Melons can be found outside the keep!", DvzClass.ALCHEMIST, new String[] {"collect|find|get|gather", "melon|melons|melonslice|melonslices|melonblock|melonblocks"});
        addTip("&7Sugar cane can be found outside the keep! Break it to collect sugar.", DvzClass.ALCHEMIST, new String[] {"collect|find|get|gather", "sugar|sugarcane|sugarcanes"});
        addTip("&7SThe cauldrons next to your pot will slowly fill with water. You can empty them with your bucket when they are full.", DvzClass.ALCHEMIST, new String[] {"collect|find|get|gather", "water|bucket"});
        addTip("&7Your pot is the black box in your workshop. Add the water in here and add the ingredients.", DvzClass.ALCHEMIST, new String[] {"what|where", "pot|cookingpot|cookpot|brew|brewingpot"});
        addTip("&7You need to fill the bottom 2 layers of your pot with water before adding ingredients.", DvzClass.ALCHEMIST, new String[] {"fill", "pot|water"});
        addTip("&7When the water boils drop in melons to brew health potions and sugar to brew speed potions.", DvzClass.ALCHEMIST, new String[] {"add|put|drop|what", "ingredient|ingredients|items"});
        addTip("&7When you successfully brewed the potion will be added to the chest on the side!", DvzClass.ALCHEMIST, new String[] {"where", "pots|potions|potion|result|items"});
        addTip("&7If you're stuck in an alchemist pot you can just &asneak &7to get out. You can also use &a/dvz ws &7to get out!", DvzClass.ALCHEMIST, new String[] {"stuck|trapped|inside", "pot|cauldron|bucket"});
        addTip("&7You collect sugar and melons outside the keep, fill your pot with water from the cauldrons and add melons or sugar to brew potions!", DvzClass.ALCHEMIST, new String[] {"what", "do"});

        //Baker
        addTip("&7Place the flour in the furnace to bake bread!", DvzClass.BAKER, new String[] {"how|make|cook|bake|craft", "bread|food"});
        addTip("&7Sow the grain in the soil at your workshop to be able to harvest more wheat when it's grown.", DvzClass.BAKER, new String[] {"what|were|where", "grain"});
        addTip("&7Harvest the wheat wheat and throw it in the grinder (hopper) on the top of your mill.", DvzClass.BAKER, new String[] {"what|were|where", "wheat"});
        addTip("&7On the top of your workshop you can find a grinder(hopper). Just throw the wheat in by dropping it onto it to make flour!", DvzClass.BAKER, new String[] {"how|what|were|where", "grind|grinder"});
        addTip("&7You harvest wheat, sow the grain back, grind the wheat in the grinder(hopper) and bake bread from the flour in the furnace.", DvzClass.BAKER, new String[] {"what", "do"});

        //Dwarf
        addTip("&7Block is a custom enchantment that reduces damage even more while blocking with the axe. Per level it reduces 0.5 while blocking and 1.0 while blocking and sneaking. So if you have level 2 block and you sneak and block you take 2 hearts less damage!", DvzClass.DWARF, new String[] {"block|blocking|battleaxe|axe", "enchant|enchantment|enchantments"});
        addTip("&7To break the potion bomb you have to break the bomb (skull) or the block underneath it. If it explodes you get poison and blindness!", DvzClass.DWARF, new String[] {"break|destroy|delete|remove", "potion|bomb|potionbomb|poisonbomb|bombs"});
        addTip("&7To remove the enderman portal you can shoot/hit the egg or kill the enderman!", DvzClass.DWARF, new String[] {"break|destroy|delete|remove", "portal|enderportal|enderman|portals"});
        addTip("&7There are three furnaces in the map. Two in corners outside and one at the bottom of the keep inside.", DvzClass.DWARF, new String[] {"where", "furnace|furnaces"});
        addTip("&7To add items to the furnace just open it and click the items you want to smelt. Then wait till it's done (you can close it) and take it out again.", DvzClass.DWARF, new String[] {"how", "furnace|furnaces|smelt|melt|cook"});
        addTip("&7There are three storage structures in the map. Two in corners outside and one at the bottom of the keep inside.", DvzClass.DWARF, new String[] {"where", "storage|chest|chests|items"});
        addTip("&7When you open the storage you just click the items you want to store in your inventory and click the items you want to take from the storage. (Shift click to take/deposit more)", DvzClass.DWARF, new String[] {"how", "storage|chest|chests|items|store|share"});
        addTip("&7There are three enchanting places in the map. Two in corners outside and one at the bottom of the keep inside.", DvzClass.DWARF, new String[] {"where", "enchant|enchanting|enchantments|enchantment"});
        addTip("&7Hold the item you want to enchant, open the enchanting structure, click the enchant you want. (You pay full XP price for upgrades!)", DvzClass.DWARF, new String[] {"how", "enchant|enchanting|enchantments|enchantment"});
        addTip("&7Swords, bows and armor can be enchanted. Each item has different enchantments that can be applied!", DvzClass.DWARF, new String[] {"what", "enchant|enchanting|enchantments|enchantment"});
        addTip("&7You gain experience by doing your tasks. Experience is used for enchanting!", DvzClass.DWARF, new String[] {"how|get|where", "xp|exp|experience"});
        addTip("&7You can share your experience with other dwarves in the storage. Just sneak above the green glass to start draining it and move away from it to stop it.", DvzClass.DWARF, new String[] {"store|share|put|add", "xp|exp|experience"});
        addTip("&7You can teleport to your workshop (if you have one) by using &a/dvz ws&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back|help", "workshop|ws|stuck|trapped"});
        addTip("&7You can teleport to the keep by using &a/dvz keep&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "keep|mountain|fortress|dwarf"});
        addTip("&7You can teleport to the top shrine by using &a/dvz shrine&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "top|shrine"});
        addTip("&7You can teleport to the front wall by using &a/dvz wall&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "wall|bigwall|frontwall|outerwall"});
        addTip("&7Use &a/dvz {wall,shrine,keep,ws} &7for teleportation!", DvzClass.DWARF, new String[] {"tp|teleport|tele"});
        addTip("&7At the second night (mc time) the dragon will arise. When the dragon dies the monsters will come! You can see the current game state by using &a/dvz&7!", DvzClass.DWARF, new String[] {"time|long|when", "left|remaining|dragon|dragons"});
        addTip("&7To create your workshop place down the workbench on one of the pistons outside the keep!", DvzClass.DWARF, new String[] {"build|place|create|spawn", "workshop|ws"});
        addTip("&7You can switch to another class by using &a/dvz switch&7! You can only switch to the classes you received at the start and you will keep your items.", DvzClass.DWARF, new String[] {"switch|swap|change|become", "class|classes|dwarf"});
        addTip("&7The dragon slayer gets a horn which he can use every 5 minutes. It will give all dwarves a strength and resistance buff.", DvzClass.DWARF, new String[] {"horn"});


        //-- MONSTERS
        //Zombie
        addTip("&7When you hit dwarves as a zombie have a chance to infect them and make them lose hunger!", DvzClass.ZOMBIE, new String[] {"infect|hunger|hit|passive"});
        addTip("&7When you look at a dwarf and you use rush you will get a speed boost! When you get close to the target you also get a strength buff!", DvzClass.ZOMBIE, new String[] {"rush|speed|boost|run"});

        //Skeleton
        addTip("&7If you left click your bow you will shoot a bunch of arrows very rapidly.", DvzClass.SKELETON, new String[] {"rapidfire|rapid|barrage|barage|rain"});

        //Spider
        addTip("&7When you hit dwarves as a spider you have a chance to poison them.", DvzClass.SPIDER, new String[] {"poison|poisonattack|attack|hit|passive"});
        addTip("&7Webs can be placed down like blocks but you can also &athrow them &7by left clicking while aiming up/forward.", DvzClass.SPIDER, new String[] {"web|webs"});

        //Creeper
        addTip("&7To explode you need to hold the gunpowder in your hand and sneak. The longer you sneak the bigger the explosion.", DvzClass.CREEPER, new String[] {"explode|explosion|blow|blowup"});
        addTip("&7To cancel charging as a creeper move around a bit.", DvzClass.CREEPER, new String[] {"cancel|stop"});

        //Blaze
        addTip("&7You can get up using the blazerod &7and hold left click on the blazepowder to glide/fly.", DvzClass.BLAZE, new String[] {"fly|glide"});
        addTip("&7When you cast blast using flint and steel you will burn all nearby players!", DvzClass.BLAZE, new String[] {"blast"});
        addTip("&7You can shoot fireballs using the fireball ability! The fireballs will damage players and create fire.", DvzClass.BLAZE, new String[] {"fireball|fireballs"});

        //Enderman
        addTip("&7You need an open place above you to create the portal.", DvzClass.ENDERMAN, new String[] {"create|make|cast|use|place|activate", "portal|egg|dragonegg"});
        addTip("&7To destroy your own portal hit the dragon egg on the portal! Dwarves can destroy your portal by shooting or hitting the egg or killing you!", DvzClass.ENDERMAN, new String[] {"click|hit|break|destroy|remove", "portal|egg|dragonegg"});
        addTip("&7Once the portal is created it can't be moved! Click the egg to remove it..", DvzClass.ENDERMAN, new String[] {"move|replace|change", "portal|egg|dragonegg"});
        addTip("&7Other monsters can downvote your portal if it's in a bad location. When you get 5 downvotes it will be destroyed!", DvzClass.ENDERMAN, new String[] {"downvote|upvote|vote"});
        addTip("&7There can only be one portal active, so wait for the other portal to be destroyed.", DvzClass.ENDERMAN, new String[] {"another|already|other", "active"});
        addTip("&7When you use the hoe on a dwarf you can pick him up and move him around. Sneak to drop him again or wait for the timer!", DvzClass.ENDERMAN, new String[] {"pickup|pick|grab", "player|players|dwarf|dwarves"});
        addTip("&7When you use the hoe on a block you can pick the block up. When you click another block you will place the block again.", DvzClass.ENDERMAN, new String[] {"pickup|pick|grab", "block|blocks"});
        addTip("&7The golden hoe can be used to pick up blocks and dwarves!", DvzClass.ENDERMAN, new String[] {"pickup|pick|grab"});

        //Villager/Witch
        addTip("&7You can morph to a villager using the shard. Both classes have different abilities and potions.", DvzClass.WITCH, new String[] {"villager|switch|swap|morph|transform|change"});
        addTip("&7You can morph to a witch using the shard. Both classes have different abilities and potions.", DvzClass.VILLAGER, new String[] {"witch|switch|swap|morph|transform|change"});
        addTip("&7Potions will be refilled when you morph!", DvzClass.VILLAGER, new String[] {"potions|potion|pots"});
        addTip("&7Potions will be refilled when you morph!", DvzClass.WITCH, new String[] {"potions|potion|pots"});
        addTip("&7You can give a damage increase buff to another monster by giving them your emerald! (Buff lasts entire game)", DvzClass.VILLAGER, new String[] {"buff|buffed"});
        addTip("&7When you place down the bomb (green skull) it will explode after like 30 seconds and give poison and blindness to ALL dwarves!", DvzClass.WITCH, new String[] {"create|place|use|activate|make", "bomb"});
        addTip("&7Dwarves can destroy the bomb by breaking it! Protect it good!", DvzClass.WITCH, new String[] {"destroy|break|remove|delete", "bomb"});

        //Monster
        addTip("&7Monster get released when the dragon dies, and not before!", DvzClass.MONSTER, new String[] {"when|get", "release|released|out|free|spawn"});
        addTip("&7Once you die you get random new classes, but only when you die! Suicide only gives you the zombie class!", DvzClass.MONSTER, new String[] {"change|become|get|switch|swap", "class|mob|monster"});
        addTip("&7When you suicide you will only get the zombie class!", DvzClass.MONSTER, new String[] {"only|always|get", "zombie|zombies|same"});
        addTip("&7If there is a portal active use your eye of ender to teleport to it!", DvzClass.MONSTER, new String[] {"tp|teleport|tele|go", "portal"});
        addTip("&7If the portal is at a bad location click the egg to downvote it! (When a portal gets 5 downvotes it will be removed)", DvzClass.MONSTER, new String[] {"bad|terrible|horrible|shitty|shit|crap|crappy|vote|downvote|rate", "portal|location"});
        addTip("&7Use your hammer to destroy the shrine of the dwarves!", DvzClass.MONSTER, new String[] {"break|destroy|damage", "shrine|shrines|portal|portals|frame|end"});
        
        //-- ALL CLASSES
        addTip("&7When there is a (enemy) nearby the teleporting will have a delay and you have to stand still and not take any damage during that time. If there is no enemy near the tp will be instant!", null, new String[]{"tp|teleport|port|tped", "instant|delay|delayed|insta|cooldown"});
        addTip("&7To start playing you have to select a class by clicking on one of the items you get when you join or when the game starts.", null, new String[]{"how", "start|play"});
        addTip("&7Look at the tab list to see who's what class. &8[&9Builder&7, &8miner&7, &2fletcher&7, &3tailor&7, &5alchemist&7, &6baker&7, &cmonster&8]", null, new String[]{"what|which", "class|classes", "need|needed|pick"});
        addTip("&7If you have a suggestion or if you have feedback please put it on the forums else we might not remember it and it won't be implemented!", null, new String[]{"can|could|may|please", "add|remove|nerf|buff|tweak|change"});
        addTip("&7If you have a bug report or a other issue please put it on the forums so that we can fix it!", null, new String[]{"found|find|got|this|fix", "bug|issue|problem|fix"});
        addTip("&7You can &ahover over items &7for descriptions! You can also do /dvz abilities and &a/dvz ability [name] &7for ability info. And /dvz classes and &a/dvz class [name] &7for class info!", null, new String[]{"how|what", "ability|class|abilities|classes", "work|do|get"});
        addTip("&7Visit &9http://clashwars.com/info &7for all information!", null, new String[]{"how|what", "work|do|get"});
    }

    public String getRandomTip() {
        return CWUtil.random(tips).getTip();
    }

    public String getTipFromChat(String chatMsg, CWPlayer cwp) {
        Long t = System.currentTimeMillis();
        List<String> chatWords = Arrays.asList(chatMsg.toLowerCase().split(" "));
        if (chatWords.size() < 1) {
            return "";
        }
        for (Tip tip : tips) {
            //Class specific tips if class specified and game is started.
            if (tip.getDvzClass() != null && DvZ.inst().getGM().isStarted()) {
                boolean classMatch = false;
                if (tip.getDvzClass() == cwp.getPlayerClass()) {
                    classMatch = true;
                } else {
                    if (tip.getDvzClass() == DvzClass.MONSTER && cwp.getPlayerClass().getType() == ClassType.MONSTER) {
                        classMatch = true;
                    }
                    if (tip.getDvzClass() == DvzClass.DWARF && cwp.getPlayerClass().getType() == ClassType.DWARF) {
                        classMatch = true;
                    }
                }
                if (!classMatch) {
                    continue;
                }
            }


            //Go through all keywords and make sure ALL keywords exist in chat msg.
            boolean keyWordsMatch = true;
            List<Keyword> keywords = tip.getKeywords();
            for (Keyword keyword : keywords) {
                //Keywords can have multiple worlds and at least one of them needs to exist in chat msg.
                boolean wordMatch = false;
                String[] words = keyword.getWords();
                for (String word : words) {
                    if (chatWords.contains(word)) {
                        wordMatch = true;
                        break;
                    }
                }
                if (!wordMatch) {
                    keyWordsMatch = false;
                    break;
                }
            }
            if (keyWordsMatch) {
                DvZ.inst().logTimings("TipManager.getTipFromChat()[found]", t);
                return tip.getTip();
            }
        }
        DvZ.inst().logTimings("TipManager.getTipFromChat()", t);
        return "";
    }


    public void addTip(String msg, DvzClass dvzClass, String... keywords) {
        tips.add(new Tip(msg, dvzClass, keywords));
    }
}

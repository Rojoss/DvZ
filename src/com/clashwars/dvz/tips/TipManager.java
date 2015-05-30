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
        addTip("&7Kill pigs to get pork, cook the pork in the furnace and add it to the storage!", DvzClass.FLETCHER, new String[] {"what|do|where", "pork|porkchop|porkchops|pig|pigs"});
        addTip("&7You collect flint outside the keep, kill chickens for feathers and craft bows and arrows. You also kill pigs and cook their pork..", DvzClass.FLETCHER, new String[] {"what", "do"});

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
        addTip("&7You collect sugar and melons outside the keep, fill your pot with water from the cauldrons and add melons or sugar to brew potions!", DvzClass.ALCHEMIST, new String[] {"what", "do"});

        //Dwarf
        addTip("&7There are two furnaces in the map. One in the corner outside and one at the bottom of the keep inside.", DvzClass.DWARF, new String[] {"where", "furnace|furnaces"});
        addTip("&7To add items to the furnace just open it and click the items you want to smelt. Then wait till it's done (you can close it) and take it out again.", DvzClass.DWARF, new String[] {"how", "furnace|furnaces|smelt|melt|cook"});
        addTip("&7There are two storage structures in the map. One in the corner outside and one at the bottom of the keep inside.", DvzClass.DWARF, new String[] {"where", "storage|chest|chests|items"});
        addTip("&7When you open the storage you just click the items you want to store in your inventory and click the items you want to take from the storage. (Shift click to take/deposit more)", DvzClass.DWARF, new String[] {"how", "storage|chest|chests|items|store|share"});
        addTip("&7There are two enchanting places in the map. One in the corner outside and one at the bottom of the keep inside.", DvzClass.DWARF, new String[] {"where", "enchant|enchanting|enchantments|enchantment"});
        addTip("&7Hold the item you want to enchant, open the enchanting structure, click the enchant you want. (You pay full XP price for upgrades!)", DvzClass.DWARF, new String[] {"how", "enchant|enchanting|enchantments|enchantment"});
        addTip("&7Swords, bows and armor can be enchanted. Each item has different enchantments that can be applied!", DvzClass.DWARF, new String[] {"what", "enchant|enchanting|enchantments|enchantment"});
        addTip("&7You gain experience by doing your tasks. Experience is used for enchanting!", DvzClass.DWARF, new String[] {"how|get|where", "xp|exp|experience"});
        addTip("&7You can teleport to your workshop (if you have one) by using &a/dvz ws&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "workshop|ws"});
        addTip("&7You can teleport to the keep by using &a/dvz keep&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "keep|mountain|fortress|dwarf"});
        addTip("&7You can teleport to the top shrine by using &a/dvz shrine&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "top|shrine"});
        addTip("&7You can teleport to the front wall by using &a/dvz wall&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "wall|bigwall|frontwall|outerwall"});
        addTip("&7Use &a/dvz {wall,shrine,keep,ws} &7for teleportation!", DvzClass.DWARF, new String[] {"tp|teleport|tele"});
        addTip("&7At the second night (mc time) the dragon will arise. When the dragon dies the monsters will come!", DvzClass.DWARF, new String[] {"time|long|when", "left|remaining|dragon|dragons"});
        addTip("&7To create your workshop place down the workbench on one of the pistons outside the keep!", DvzClass.DWARF, new String[] {"build|place|create|spawn", "workshop|ws"});
        addTip("&7You can switch to another class by using &a/dvz switch&7! You can only switch to the classes you received at the start and you will keep your items.", DvzClass.DWARF, new String[] {"switch|swap|change|become", "class|classes|dwarf"});


        //-- MONSTERS
        //Zombie
        addTip("&7When you hit dwarves as a zombie have a chance to infect them and make them lose hunger!", DvzClass.ZOMBIE, new String[] {"infect|hunger|hit|passive"});
        addTip("&7When you look at a dwarf and you use rush you will get a speed boost!", DvzClass.ZOMBIE, new String[] {"rush|speed|boost|run"});

        //Skeleton
        addTip("&7If you left click your bow you will shoot a bunch of arrows very rapidly.", DvzClass.SKELETON, new String[] {"rapidfire|rapid|barrage|barage|rain"});

        //Spider
        addTip("&7When you hit dwarves as a spider you have a chance to poison them.", DvzClass.SPIDER, new String[] {"poison|poisonattack|attack|hit|passive"});
        addTip("&7Webs can be placed down like blocks but you can also throw them by left clicking while aiming up.", DvzClass.SPIDER, new String[] {"web|webs"});

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
        addTip("&7You can give a strength buff to another monster by giving them your emerald! (Buff lasts entire game)", DvzClass.VILLAGER, new String[] {"buff|buffed"});
        addTip("&7When you place down the bomb (green skull) it will explode after 30 seconds and give many negative effects to nearby dwarves!", DvzClass.WITCH, new String[] {"create|place|use|activate|make", "bomb"});
        addTip("&7Dwarves can destroy the bomb by breaking it! Protect it good!", DvzClass.WITCH, new String[] {"destroy|break|remove|delete", "bomb"});

        //Monster
        addTip("&7Monster get released when the dragon dies, and not before!", DvzClass.MONSTER, new String[] {"when|get", "release|released|out|free|spawn"});
        addTip("&7Once you die you get random new classes, but only when you die! Suicide only gives you the classes you already had!", DvzClass.MONSTER, new String[] {"change|become|get|switch|swap", "class|mob|monster"});
        addTip("&7If there is a portal active use your eye of ender to teleport to it!", DvzClass.MONSTER, new String[] {"tp|teleport|tele|go", "portal"});
        addTip("&7If the portal is at a bad location click the egg to downvote it! (When a portal gets 5 downvotes it will be removed)", DvzClass.MONSTER, new String[] {"bad|terrible|horrible|shitty|shit|crap|crappy|vote|downvote|rate", "portal|location"});
        addTip("&7Use your hammer to destroy the shrine of the dwarves!", DvzClass.MONSTER, new String[] {"break|destroy|damage", "shrine|shrines|portal|portals|frame|end"});

        
        //-- ALL CLASSES
        addTip("&7Visit &9http://clashwars.com/info &7for all information!", null, new String[]{"how|what", "work|do|get"});
    }

    public String getRandomTip() {
        return CWUtil.random(tips).getTip();
    }

    public String getTipFromChat(String chatMsg, CWPlayer cwp) {
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
            for (Keyword keyword : tip.getKeywords()) {
                //Keywords can have multiple worlds and at least one of them needs to exist in chat msg.
                boolean wordMatch = false;
                for (String word : keyword.getWords()) {
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
                return tip.getTip();
            }
        }
        return "";
    }


    public void addTip(String msg, DvzClass dvzClass, String... keywords) {
        tips.add(new Tip(msg, dvzClass, keywords));
    }
}

package com.clashwars.dvz.tips;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;

import java.util.ArrayList;
import java.util.List;

public class TipManager {

    List<Tip> tips = new ArrayList<Tip>();

    public TipManager() {
        addTip("&7You can teleport to your workshop by using &2/dvz ws&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "workshop|ws"});
        addTip("&7You can teleport to the keep by using &2/dvz keep&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "keep|mountain|fortress|dwarf"});
        addTip("&7You can teleport to the top shrine by using &2/dvz shrine&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "top|shrine"});
        addTip("&7You can teleport to the front wall by using &2/dvz wall&7!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go|back", "wall|bigwall|frontwall|outerwall"});
        addTip("&7Use &2/dvz {wall,shrine,keep,ws} &7for teleportation!", DvzClass.DWARF, new String[] {"tp|teleport|tele|go"});
        addTip("&7Gravel and flint can be found outside the keep!", DvzClass.FLETCHER, new String[] {"collect|find|get", "gravel|flint"});
        addTip("&7Melons can be found outside the keep!", DvzClass.ALCHEMIST, new String[] {"collect|find|get", "melon|melons"});
        addTip("&7Sugar canes can be found outside the keep!", DvzClass.ALCHEMIST, new String[] {"collect|find|get", "sugar|sugar canes"});
        addTip("&7You can reinforce cracked stone brick by clicking on it with another cracked stone brick!", DvzClass.BUILDER, new String[] {"make|use|reinforce", "stone|bricks|wall|stone bricks|stone brick"});
        addTip("&7Use your Eye of Ender to teleport to an active Ender portal!", DvzClass.MONSTER, new String[] {"tp|teleport|tele|go", "portal|wall|keep"});
        addTip("&7Melons can be found outside the keep!", DvzClass.ALCHEMIST, new String[] {"collect|find|get", "melon|melons"});
        addTip("&7Ores spawn randomly in your workshop, so make sure to keep mining!", DvzClass.MINER, new String[] {"collect|find|get|mine", "iron|gold|diamond|ore|ores|diamonds"});
        addTip("&7You have a 50% chance to make a bow evertime you craft arrows!", DvzClass.TAILOR, new String[] {"collect|find|get|make", "bow|bows"});
        addTip("&7You need to sneak and then right click to explode, hold right click for a more powerful explosion!", DvzClass.CREEPER, new String[] {"explode|blow up|detonate"});
        addTip("&7Use your hammer to destroy the shrine of the dwarves!", DvzClass.MONSTER, new String[] {"break|destroy", "shrine|portal|portals"});
        addTip("&7Left click with your bow to fire multiple arrows at once!", DvzClass.SKELETON, new String[] {"shoot|fire", "bow|arrow|arrows"});
        addTip("&7Right click your spider eye to unleash your poison!", DvzClass.SPIDER, new String[] {"use|activate", "spider eye|posion|ability"});
        addTip("&7You can fly using your &2SHOOT &7 and &2GLIDE &7abilities!", DvzClass.BLAZE, new String[] {"fly|glide"});
        addTip("&7You need an open place to place down your portal, in the air for instance!", DvzClass.ENDERMAN, new String[] {"place|activate|place down|make|use", "dragon egg|egg|portal"});
        addTip("&7You need to place the bomb on the ground near a &2Dwarf&7!", DvzClass.WITCH, new String[] {"use|place|place down|activate", "bomb|poison bomb"});
        addTip("&7Once you die you get random new classes, but only when you die! Suicide only gives you the classes you already had!", DvzClass.MONSTER, new String[] {"change|become|get", "class|mob|monster"});
        addTip("&7Once you chose your class for dwarf you're stuck with it!", DvzClass.DWARF, new String[] {"change|become|get", "class|dwarf|profession"});
        addTip("&7Monster get unleashed when the dragon dies, and not before! If you're really stuck try &2SUICIDE&7!", DvzClass.MONSTER, new String[] {"get|get out|leave|stuck", "spawn|beginning|monster spawn"});
        addTip("&7Go to the chests in the keep or in the corner of the wall! Those are Shared Storages!", DvzClass.DWARF, new String[] {"get|make|have", "armor|food|weapon|weapons|sword|bow"});
        addTip("&7Visit &9http://clashwars.com/info &7for all information!", null, new String[]{"how|what", "work|do|get"});
    }

    public String getRandomTip() {
        return CWUtil.random(tips).getTip();
    }

    public String getTipFromChat(String chatMsg, CWPlayer cwp) {
        String[] chatWords = chatMsg.toLowerCase().split(" ");
        if (chatWords.length < 1) {
            return "";
        }
        for (Tip tip : tips) {
            //Class specific tips.
            if (tip.getDvzClass() != null) {
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
                    for (String chatWord : chatWords) {
                        if (chatWord.equals(word)) {
                            wordMatch = true;
                            break;
                        }
                    }
                    if (wordMatch == true) {
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

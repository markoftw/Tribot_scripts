package scripts.redDragsSlayer;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@ScriptManifest(authors = {"Platinum Force Scripts and Yaw hide"}, name = "Has Tabs/Runes Helper", category = "Tools")
public class YawhideHelper extends Script {

    @Override
    public void run() {
        //Add tasks by location
        

        //println(hasReqTab());
    }

    /* Checks if the player has the required tab for a specific task */
    public boolean hasReqTab(boolean usingTabs, String currTask) {
        
        String Locations[] = {"Varrock", "Falador", "Camelot", "Ardougne", "Lumbridge"};
        Map<String, String[]> TASK = new LinkedHashMap<>();
        Map<String, Integer> TABS = new LinkedHashMap<>();
        Map<String, Integer[]> RUNES = new LinkedHashMap<>();

       int VTAB = 8007;
       int FTAB = 8009;
       int CTAB = 8010;
       int LTAB = 8008;
       int ATAB = 8011;

       int LAW = 563;
       int EARTH = 557;
       int AIR = 556;
       int WATER = 555;
       int FIRE = 554;

        
        TASK.put("Varrock", new String[]{"dwarves", "minotaurs", "rats", "skeletons", "zombies"});
        TASK.put("Falador", new String[]{"bats", "cows", "ghosts", "icefiends", "monkeys"});
        TASK.put("Camelot", new String[]{"cave_crawlers", "wolves", "birds"});
        TASK.put("Ardougne", new String[]{"bears", "dogs"});
        TASK.put("Lumbridge", new String[]{"cave_slimes", "cave_bugs", "goblins", "spiders"});

        //Add tabs by location
        TABS.put("Varrock", VTAB);
        TABS.put("Falador", FTAB);
        TABS.put("Camelot", CTAB);
        TABS.put("Lumbridge", LTAB);
        TABS.put("Ardougne", ATAB);

        //Add runes by location
        RUNES.put("Varrock", new Integer[]{LAW, AIR, FIRE});
        RUNES.put("Falador", new Integer[]{WATER, LAW, AIR});
        RUNES.put("Camelot", new Integer[]{AIR, LAW});
        RUNES.put("Lumbridge", new Integer[]{EARTH, LAW, AIR});
        RUNES.put("Ardougne", new Integer[]{WATER, LAW});
        
        for (String loc : Locations) {
            if (Arrays.asList(TASK.get(loc)).contains(currTask)) {
                if (usingTabs) {
                    RSItem[] tabs = Inventory.find(TABS.get(loc));
                    if (tabs.length > 0) {
                        return true;
                    }
                } else {
                    for (Integer i : RUNES.get(loc)) {
                        int count = Inventory.getCount(i);
                        if (count == 0) {
                            return false;
                        }
                    }
                    return true;
                }
                break;
            }
        }
        return false;
    }
}

package scripts.redDragsSlayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Magic;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.api2007.ext.Doors;

@ScriptManifest(authors = {"Yaw hide & Fixed by Marko"}, version = 0.62, category = "RedDrags", name = "3. Turael Slayer", description = "Slayer [START WITH ITEMS]")
public class TuraelSlayer extends Script implements MessageListening07, Painting, Ending {

    //food Ids
    //int[] foodID = {333, 379};
    int[] foodID = {333, 329, 379, 361, 7946, 1897};
    int[] junk = {886, 1539, 9003, 229, 1623, 1355, 440, 7767, 117,
        6963, 554, 556, 829, 1971, 687, 464, 1973, 1917, 808, 1454, 6180,
        6965, 1969, 6183, 6181};

    int[] loot = {563, 560, 561, 562, 564, 565, 566, 7937, 816, 1747, 536, 9142, 868, 563,
        1615, 1319, 1373, 1247, 1303, 1249, 1123, 1149, 1201,
        1186, 1113, 1079, 892, 565, 560, 561, 563, 2361, 2366, 443,
        985, 987, 2363, 1617, 1619, 574, 1442,
        /**
         * ** herbs ***
         */
        207, 257, 208, 258, 2481, 2482, 2485,
        2486, 209, 210, 211, 212, 213, 214, 217, 218, 219, 220,
        3049, 3050, 3051, 3052,
        /**
         * **seeds ***
         */
        5295, 5302, 5286, 5289, 5288,
        5289, 5290, 5296, 5297, 5298,
        5299, 5300, 5301, 5303, 5304,
        5321,
        /**
         * **stackable ***
         */
        554, 557, 556, 555
    };

    String[] names = {"Law rune", "Death rune", "Nature rune", "Chaos rune", "Cosmic rune",
        "Blood rune", "Soul rune", "Pure essence", "Adamant dart(p)",
        "Black dragonhide", "Dragon bones", "Mithril bolts",
        "Rune knife", "Law rune", "Dragonstone",
        "Rune 2h sword", "Rune battleaxe",
        "Rune spear", "Rune longsword", "Dragon spear",
        "Adamant platebody", "Dragon med helm",
        "Rune kiteshield", "Rune sq shield", "Rune chainbody",
        "Rune platelegs", "Rune arrow", "Blood rune", "Death rune",
        "Nature rune", "Law rune", "Adamantite bar", "Shield left half",
        "Silver ore", "Half of a key", "Half of a key",
        "Runite bar", "Uncut diamond", "Uncut ruby", "Air orb", "Fire talisman",
        /**
         * ** herbs ***
         */
        "Herb", "Herb", "Herb", "Herb", "Herb", "Herb",
        "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb", "Herb",
        "Herb", "Herb", "Herb", "Herb",
        /**
         * **seeds ***
         */
        "Ranarr seed", "Lantadyme seed", "Curry tree seed", "Pineapple seed", "Papaya tree seed",
        "Palm tree seed", "Calquat tree seed", "Toadflax seed", "Irit seed", "Avantoe seed",
        "Kwuarm seed", "Snapdragon seed", "Cadantine seed", "Dwarf weed seed", "Torstol seed",
        "Watermelon seed",
        /**
         * **stackables ***
         */
        "Fire rune", "Earth rune", "Air rune", "Water rune"
    };

    int[] lootProfit = {563, 560, 561, 562, 564, 565, 566, 7937, 816, 1747, 536, 9142, 868, 563,
        1615, 1319, 1373, 1247, 1303, 1249, 1123, 1149, 1201,
        1186, 1113, 1079, 892, 565, 560, 561, 563, 2361, 2366, 443,
        985, 987, 2363, 1617, 1619, 574, 1442,
        /**
         * ** herbs ***
         */
        207, 257, 208, 258, 2481, 2482, 2485,
        2486, 209, 210, 211, 212, 213, 214, 217, 218, 219, 220,
        3049, 3050, 3051, 3052,
        /**
         * **seeds ***
         */
        5295, 5302, 5286, 5289, 5288,
        5289, 5290, 5296, 5297, 5298,
        5299, 5300, 5301, 5303, 5304,
        5321
    };

    //TODO LOOT()
    public void LOOT() {
        RSGroundItem[] Nests = GroundItems.findNearest(loot);

        if (!Inventory.isFull() && Nests.length > 0) {
            if (checkLootInArea(Nests[0])) {
                if (!Nests[0].isOnScreen()) {
                    Walking.clickTileMM(Nests[0].getPosition(), 1);
                    sleep(100, 150);
                    Camera.turnToTile(Nests[0].getPosition());
                    sleep(200, 300);
                    waitIsMovin();
                }
                String str = map.get(Nests[0].getID());
                Nests[0].click("Take " + str);
                waitForInv(Nests[0].getID());
            }
        }
    }

    HashMap<Integer, String> map = new HashMap<Integer, String>(loot.length);

    //slayer items
    int slayGem = 4155;
    int COINS = 995;
    int VTAB = 8007;
    int FTAB = 8009;
    int CTAB = 8010;
    int LTAB = 8008;
    int ATAB = 8011;
    int EARMUFFS = 4166;
    int TINDER = 590;
    int SALT = 4161;
    int SPINY = 4551;
    int LAW = 563;
    int EARTH = 557;
    int AIR = 556;
    int WATER = 555;
    int FIRE = 554;
    int[] WATERSKIN = {1823, 1825, 1827, 1829};
    int COOLER = 6696;
    int ECTO = 4251;
    int rope = 954;
    int SHANTY = 1854;

    RSTile shantyBankTile = new RSTile(3309, 3120, 0);

    //antiban stuff
    private long antiban = System.currentTimeMillis();
    //private int[] evilchicken = { 2465, 2467 };
    //private int swarm = 407;

    // Slayer teleport items
    int[] gamesNecklace = {3853, 3855, 3857, 3859, 3861, 3863, 3865, 3867}; // goes from 8 to 1
    int[] rod = {2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566};
    int[] antiPoison = {2448, 181, 183, 185, 193, 2446, 175, 177, 179}; // start with super 4-1
    int[] lightsources = {4531, 4539, 4550, 36, 33};
    int[] depositAllExcept = {3853, 3855, 3857, 3859, 3861, 3863, 3865, slayGem, VTAB, FIRE, LAW, AIR};

    String lastServerMessage;
    int numLeftToKill;
    String currTask = "None";

    @Override
    public void run() {

        onStart();

        if (currTask.equals("None") && currTask != null) {

            if (pos().distanceTo(turaelT) <= 5) {
                if (getTask()) {
                    getNumSlayTask();
                }
            } else if (inArea(mainCastle[0], mainCastle[1], pos())) {
                gotoTurael();
            } else if (inArea(burthorpeArea[0], burthorpeArea[1], pos())) {
                WebWalking.walkTo(turaelT);
                waitIsMovin();
            } else {
                gotoTurael();
            }
        } else if (isAtTask()) {
            println("I am at my task");
            if (!haveReqEquip(1)) {
                GOTO_BANK();
            } else if (!Combat.isUnderAttack()) {
                FIGHT();
            }
        } else {
            println("I am NOT at my task");
            if (!haveReqEquip(0) || Inventory.getCount(lootProfit) > 0) {
                if (pos().distanceTo(varrockBankT) <= 8) {
                    BANKING();
                } else {
                    GOTO_BANK();
                }
            } else {
                GOTO_TASK();
            }
        }

        while (true) {

            current_xp = Skills.getXP(SKILLS.HITPOINTS) + Skills.getXP(SKILLS.STRENGTH)
                    + Skills.getXP(SKILLS.ATTACK) + Skills.getXP(SKILLS.DEFENCE) + Skills.getXP(SKILLS.RANGED);
            current_xpSlayer = Skills.getXP(SKILLS.SLAYER);
            XpToLVrange = Skills.getXPToNextLevel(SKILLS.RANGED);
            XpToLVhp = Skills.getXPToNextLevel(SKILLS.HITPOINTS);
            XpToLVstr = Skills.getXPToNextLevel(SKILLS.STRENGTH);
            XpToLVatt = Skills.getXPToNextLevel(SKILLS.ATTACK);
            XpToLVdef = Skills.getXPToNextLevel(SKILLS.DEFENCE);
            XpToLVslayer = Skills.getXPToNextLevel(SKILLS.SLAYER);

            currLv = Skills.getActualLevel(SKILLS.SLAYER);
            if (currLv > startLv) {
                lvsGained = Math.abs(currLv - startLv);
            }

            if (System.currentTimeMillis() - antiban > 150000) {
                antiban = System.currentTimeMillis();
                getNumSlayTask();
                println("Current xp gained: " + current_xpSlayer + "    starting xp: " + startXpSlayer);
            }

            if (getHp() < 30) {
                eatFood();
            }

            if (Game.getRunEnergy() > 50) {
                Options.setRunOn(true);
            }

            if (currTask.equals("None") && currTask != null) {

                if (pos().distanceTo(turaelT) <= 10) {
                    if (getTask()) {
                        getNumSlayTask();
                    }
                } else if (inArea(mainCastle[0], mainCastle[1], pos())) {
                    gotoTurael();
                } else if (inArea(burthorpeArea[0], burthorpeArea[1], pos())) {
                    WebWalking.walkTo(turaelT);
                    waitIsMovin();
                } else {
                    gotoTurael();
                }

            } else if (isAtTask()) {
                if (!haveReqEquip(1)) {
                    GOTO_BANK();
                } else if (!Combat.isUnderAttack()) {
                    FIGHT();
                }
            } else {
                println("I am NOT at my task");
                if (!haveReqEquip(0)) {
                    if (pos().distanceTo(varrockBankT) <= 8) {
                        BANKING();
                    } else {
                        GOTO_BANK();
                    }
                } else {
                    GOTO_TASK();
                }
            }

            sleep(50, 100);
        }

    }

    public boolean waitGUI = true;
    public boolean LootEverythingIncludingNonStacks = true;
    public boolean LootOnlyStacksAndHighValueDrops = false;
    public boolean LootOnlyHighValueDrops = false;
    public boolean DoNotLoot = false;
    public boolean UseRunesToTele = false;
    public boolean PureMode = false;

    //TODO onStart
    public void onStart() {
        println("Before gui");

        TuraelSlayerGUI g = new TuraelSlayerGUI();

        //EasySlayerGui g = new EasySlayerGui();
//		javax.swing.SwingUtilities.invokeLater(new Runnable() { 
//			public void run() {
//				
//			} 
//		}); 
        g.setVisible(true);
        println("After gui");
        while (waitGUI) {
            sleep(2500);
            println("waiting for gui to close");
        }
        g.setVisible(false);
        //println(LootOnlyStacksAndHighValueDrops);

        useTabs = !UseRunesToTele;

        Mouse.setSpeed(General.random(150, 170));
        sleep(200, 300);
        Walking.setControlClick(true);
        Walking.setWalkingTimeout(5000L);
        putMap();
        sleep(200, 250);

        checkMagicLevel();
        sleep(100, 150);

        getNumSlayTask();
        sleep(500, 600);

    }

    boolean useTabV = true;
    boolean useTabF = true;
    boolean useTabC = true;
    boolean useTabL = true;
    boolean useTabA = true;
    boolean useTabs;
    //TODO checkMagicLevel

    public void checkMagicLevel() {
        int lv = Skills.getCurrentLevel(SKILLS.MAGIC);
        if (!useTabs) {
            if (lv < 31 && lv >= 25) {
                useTabV = false;
            } else if (lv < 37) {
                useTabV = false;
                useTabL = false;
            } else if (lv < 45) {
                useTabV = false;
                useTabL = false;
                useTabF = false;
            } else if (lv < 51) {
                useTabV = false;
                useTabL = false;
                useTabF = false;
                useTabC = false;
            } else if (lv >= 51) {
                useTabV = false;
                useTabL = false;
                useTabF = false;
                useTabC = false;
                useTabA = false;
            } else {
                useTabs = true;
            }
        }
    }

    public void withdrawTabOrRune(int destination) { // 0 is V, 1 is F, 2 is C, 3 is L, 4 is A

        RSItem[] vtab = Inventory.find(VTAB);
        RSItem[] ftab = Inventory.find(FTAB);
        RSItem[] ctab = Inventory.find(CTAB);
        RSItem[] ltab = Inventory.find(LTAB);
        RSItem[] atab = Inventory.find(ATAB);

        if (useTabs) {
            switch (destination) {
                case 0:
                    if (vtab.length == 0) {
                        Clicking.hover(Banking.find(VTAB));
                        Banking.withdraw(10, VTAB);
                        sleep(200, 300);
                    }
                    break;
                case 1:
                    if (ftab.length == 0) {
                        Clicking.hover(Banking.find(FTAB));
                        Banking.withdraw(10, FTAB);
                        sleep(200, 300);
                    }
                    break;
                case 2:
                    if (ctab.length == 0) {
                        Clicking.hover(Banking.find(CTAB));
                        Banking.withdraw(10, CTAB);
                        sleep(200, 300);
                    }
                    break;
                case 3:
                    if (ltab.length == 0) {
                        Clicking.hover(Banking.find(LTAB));
                        Banking.withdraw(10, LTAB);
                        sleep(200, 300);
                    }
                    break;
                case 4:
                    if (atab.length == 0) {
                        Clicking.hover(Banking.find(ATAB));
                        Banking.withdraw(10, ATAB);
                        sleep(200, 300);
                    }
                    break;
            }
        } else {
            switch (destination) {
                case 0:
                    if (useTabV) {
                        if (vtab.length == 0) {
                            Clicking.hover(Banking.find(VTAB));
                            Banking.withdraw(10, VTAB);
                            sleep(200, 300);
                        }
                    } else {
                        if (!haveRune(1)) {
                            Clicking.hover(Banking.find(LAW));
                            Banking.withdraw(10, LAW);
                            sleep(200, 300);
                        }
                        if (!haveRune(2)) {
                            Clicking.hover(Banking.find(AIR));
                            Banking.withdraw(30, AIR);
                            sleep(200, 300);
                        }
                        if (!haveRune(3)) {
                            Clicking.hover(Banking.find(FIRE));
                            Banking.withdraw(10, FIRE);
                            sleep(200, 300);
                        }
                    }
                    break;
                case 1:
                    if (useTabF) {
                        if (ftab.length == 0) {
                            Clicking.hover(Banking.find(FTAB));
                            Banking.withdraw(10, FTAB);
                            sleep(200, 300);
                        }
                    } else {
                        if (!haveRune(1)) {
                            Clicking.hover(Banking.find(LAW));
                            Banking.withdraw(10, LAW);
                            sleep(200, 300);
                        }
                        if (!haveRune(2)) {
                            Clicking.hover(Banking.find(AIR));
                            Banking.withdraw(10, AIR);
                            sleep(200, 300);
                        }
                        if (!haveRune(4)) {
                            Clicking.hover(Banking.find(WATER));
                            Banking.withdraw(10, WATER);
                            sleep(200, 300);
                        }
                    }
                    break;
                case 2:
                    if (useTabC) {
                        if (ctab.length == 0) {
                            Clicking.hover(Banking.find(CTAB));
                            Banking.withdraw(10, CTAB);
                            sleep(200, 300);
                        }
                    } else {
                        if (!haveRune(1)) {
                            Clicking.hover(Banking.find(LAW));
                            Banking.withdraw(10, LAW);
                            sleep(200, 300);
                        }
                        if (!haveRune(2)) {
                            Clicking.hover(Banking.find(AIR));
                            Banking.withdraw(10, AIR);
                            sleep(200, 300);
                        }
                    }
                    break;
                case 3:
                    if (useTabL) {
                        if (ltab.length == 0) {
                            Banking.withdraw(10, LTAB);
                            sleep(200, 300);
                        }
                    } else {
                        if (!haveRune(1)) {
                            Clicking.hover(Banking.find(LAW));
                            Banking.withdraw(10, LAW);
                            sleep(200, 300);
                        }
                        if (!haveRune(2)) {
                            Clicking.hover(Banking.find(AIR));
                            Banking.withdraw(10, AIR);
                            sleep(200, 300);
                        }
                        if (!haveRune(0)) {
                            Clicking.hover(Banking.find(EARTH));
                            Banking.withdraw(10, EARTH);
                            sleep(200, 300);
                        }
                    }
                    break;
                case 4:

                    if (useTabA) {
                        if (atab.length == 0) {
                            Clicking.hover(Banking.find(ATAB));
                            Banking.withdraw(10, ATAB);
                            sleep(200, 300);
                        }
                    } else {
                        if (!haveRune(1)) {
                            Clicking.hover(Banking.find(LAW));
                            Banking.withdraw(10, LAW);
                            sleep(200, 300);
                        }
                        if (!haveRune(4)) {
                            Clicking.hover(Banking.find(WATER));
                            Banking.withdraw(10, WATER);
                            sleep(200, 300);
                        }
                    }
                    break;
            }
        }
    }

    public boolean haveRune(int option) {

        int earth = Inventory.getCount(EARTH);
        int law = Inventory.getCount(LAW);
        int air = Inventory.getCount(AIR);
        int fire = Inventory.getCount(FIRE);
        int water = Inventory.getCount(WATER);

        switch (option) {
            case 0:
                if (earth < 10) {
                    return false;
                }
                break;
            case 1:
                if (law < 10) {
                    return false;
                }
                break;
            case 2:
                if (air < 10) {
                    return false;
                }
                break;
            case 3:
                if (fire < 10) {
                    return false;
                }
                break;
            case 4:
                if (water < 10) {
                    return false;
                }
                break;
        }
        return true;

    }

    public void useGameNeck() {
        state = "Using games Neck";
        GameTab.open(org.tribot.api2007.GameTab.TABS.INVENTORY);
        sleep(200, 300);

        RSItem[] gamesNeck = Inventory.find(gamesNecklace);
        if (gamesNeck.length > 0) {

            if (gamesNeck[0].click("Rub")) {
                sleep(3500, 4750);
                RSInterface gamebox = Interfaces.get(219, 0);
                if (gamebox != null) {
                    RSInterface[] list = gamebox.getChildren();
                    if (list != null) {
                        list[1].click("Continue");
                        sleep(5200, 5250);
                    }
                }
            }
        }
    }
    //TODO useROD

    public void useROD() {
        state = "Using ROD";
        GameTab.open(org.tribot.api2007.GameTab.TABS.INVENTORY);
        sleep(200, 300);

        RSItem[] ROD = Inventory.find(rod);

        if (ROD.length > 0) {

            if (ROD[0].click("Rub")) {
                sleep(2500, 2750);
                RSInterface ringbox = Interfaces.get(219, 0);
                if (ringbox != null) {
                    RSInterface[] list = ringbox.getChildren();
                    if (list != null) {
                        list[1].click("Continue");
                        sleep(4200, 4250);
                    }
                }
            }
        }
    }

    // area RSTiles for gotoTurael
    RSTile[] gamesNeckTeleSpot = {new RSTile(2203, 4942, 0), new RSTile(2214, 4936, 0)};
    RSTile gamesNeckStairsObj = new RSTile(2207, 4935, 0);

    RSTile[] middleStairs = {new RSTile(2204, 4938, 1), new RSTile(2209, 4933, 1)};
    RSTile middleStairsObj = new RSTile(2205, 4935, 1);

    RSTile[] mainCastle = {new RSTile(2892, 3569, 0), new RSTile(2904, 3558, 0)};
    RSTile innerDoorsTile1 = new RSTile(2898, 3558, 0);
    RSTile innerDoorsTile2 = new RSTile(2898, 3561, 0);
    /*
	private RSTile[] toTuraelPath = { new RSTile(2899, 3553, 0), new RSTile(2903, 3546, 0), 
			new RSTile(2913, 3546, 0), new RSTile(2921, 3542, 0), new RSTile(2928, 3536, 0) };
     */
    RSTile stairAtGameNeckTele = new RSTile(2207, 4935, 0);

    public void gotoTurael() {
        state = "Going to Turael";
        RSItem[] gamesNeck = Inventory.find(gamesNecklace);
        RSObject[] GAMESNECKSTAIROBJ = Objects.getAt(stairAtGameNeckTele);
        Mouse.setSpeed(General.random(130, 150));

        if (inArea(gamesNeckTeleSpot[0], gamesNeckTeleSpot[1], pos())) {

            if (GAMESNECKSTAIROBJ.length > 0) {
                if (GAMESNECKSTAIROBJ[0].isOnScreen()) {
                    if (clickOBJ(GAMESNECKSTAIROBJ[0], "Climb-up")) {
                        waitIsMovin();
                    }
                } else {
                    Walking.blindWalkTo(gamesNeckStairsObj);
                }
            }

        } else if (inArea(middleStairs[0], middleStairs[1], pos())) {
            RSObject[] MIDDLESTAIRSOBJ = Objects.getAt(middleStairsObj);
            if (MIDDLESTAIRSOBJ.length > 0 && clickOBJ(MIDDLESTAIRSOBJ[0], "Climb-up")) {
                waitIsMovin();
            }
        } else if (inArea(mainCastle[0], mainCastle[1], pos())) {

            if (openDoor(innerDoorsTile1)) {
                WebWalking.walkTo(turaelT);
                waitIsMovin();
            } else {
                Walking.walkTo(innerDoorsTile2);
                waitIsMovin();
            }
        } else if (gamesNeck.length > 0) {

            if (!inCombat()) {
                println("Going to rub games Necklace");
                useGameNeck();
            }
        }
    }

    //getting a new Task
    //TODO getTask
    RSTile[] TuraelArea = {new RSTile(2925, 3542, 0), new RSTile(2932, 3533, 0)};

    public boolean getTask() {
        state = "Getting a new task";
        RSNPC[] turael = NPCs.findNearest("Turael"); // id 70

        if (clickNPC(turael[0], "Assignment")) {
            waitIsMovin();
            sleep(3150, 4500);
            if (NPCChat.getClickContinueInterface() != null) {
                if ("Who are you?".equals(NPCChat.getMessage())) {
                    if (NPCChat.clickContinue(true)) {
                        General.sleep(650, 850);
                        if (NPCChat.clickContinue(true)) {
                            General.sleep(650, 850);
                            if (NPCChat.selectOption("What's a slayer?", true)) {
                                General.sleep(650, 850);
                                if (NPCChat.clickContinue(true)) {
                                    General.sleep(650, 850);
                                    if (NPCChat.clickContinue(true)) {
                                        General.sleep(650, 850);
                                        if (NPCChat.clickContinue(true)) {
                                            General.sleep(650, 850);
                                            if (NPCChat.clickContinue(true)) {
                                                General.sleep(650, 850);
                                                if (NPCChat.selectOption("Wow, can you teach me?", true)) { //7x
                                                    General.sleep(650, 850);
                                                    if (NPCChat.clickContinue(true)) {
                                                        General.sleep(650, 850);
                                                        if (NPCChat.clickContinue(true)) {
                                                            General.sleep(650, 850);
                                                            if (NPCChat.clickContinue(true)) {
                                                                General.sleep(650, 850);
                                                                if (NPCChat.clickContinue(true)) {
                                                                    General.sleep(650, 850);
                                                                    if (NPCChat.clickContinue(true)) {
                                                                        General.sleep(650, 850);
                                                                        if (NPCChat.clickContinue(true)) {
                                                                            General.sleep(650, 850);
                                                                            if (NPCChat.clickContinue(true)) {
                                                                                General.sleep(650, 850);
                                                                                if (NPCChat.selectOption("Okay, great!", true)) {
                                                                                    if (NPCChat.clickContinue(true)) {
                                                                                        General.sleep(650, 850);
                                                                                        if (NPCChat.clickContinue(true)) {
                                                                                            General.sleep(650, 850);
                                                                                            return true;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                } else if (NPCChat.getMessage().startsWith("You're still hunting")) {
                    General.sleep(650, 850);
                    if (NPCChat.clickContinue(true)) {
                        General.sleep(650, 850);
                        if (NPCChat.selectOption("Okay, great!", true)) {
                            General.sleep(650, 850);
                            if (NPCChat.clickContinue(true)) {
                                General.sleep(650, 850);
                                if (NPCChat.clickContinue(true)) {
                                    General.sleep(650, 850);
                                    return true;
                                }
                            }
                        }
                    }
                } else if (NPCChat.clickContinue(true)) {
                    General.sleep(650, 850);
                    if (NPCChat.selectOption("Okay, great!", true)) {
                        General.sleep(650, 850);
                        if (NPCChat.clickContinue(true)) {
                            General.sleep(650, 850);
                            if (NPCChat.clickContinue(true)) {
                                General.sleep(650, 850);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        /*
        
         if (NPCChat.clickContinue(true)) {
                General.sleep(650, 850);
                if (NPCChat.selectOption("Okay, great!", true)) {
                    General.sleep(650, 850);
                    if (NPCChat.clickContinue(true)) {
                        General.sleep(650, 850);
                        if (NPCChat.clickContinue(true)) {
                            General.sleep(650, 850);
                        }
                    }
                }
         */
 /*if (inArea(TuraelArea[0], TuraelArea[1], pos())) {
            if (clickNPC(turael[0], "Talk-to")) {
                waitIsMovin();
                sleep(1150, 1200);
                if (NPCChat.clickContinue(true)) {
                    sleep(100, 150);
                    if (NPCChat.selectOption("I need another assignment", true)) {
                        sleep(100, 150);
                        if (NPCChat.clickContinue(true)) {
                            sleep(100, 150);
                            if (NPCChat.clickContinue(true)) {
                                sleep(100, 150);
                                if (NPCChat.selectOption("No that's okay, I'll take a task from you.", true)) {
                                    sleep(100, 150);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 600);
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }*/
        return false;
    }

    // get the number of monsters left to finish task
    int numLeftSlayTask;
    String currSlayTask;

    public void getNumSlayTask() {
        state = "Check task #";
        RSItem[] SLAYGEM = Inventory.find(slayGem);

        if (SLAYGEM.length > 0) {
            if (SLAYGEM[0].click("Check")) {
                sleep(1500, 1800);
                println("Current Task: " + currTask + "   Left to kill: " + numLeftToKill);
            }
            Mouse.setSpeed(General.random(150, 170));
        }
    }

    // goto zombie task, uses similarities in toSkeletons method
    private RSTile[] toZombies = {new RSTile(3105, 9909, 0), new RSTile(3115, 9909, 0),
        new RSTile(3126, 9909, 0), new RSTile(3131, 9911, 0), new RSTile(3139, 9907, 0),
        new RSTile(3141, 9900, 0), new RSTile(3139, 9889, 0), new RSTile(3147, 9883, 0)};

    RSTile zombieMiddle = new RSTile(3145, 9886, 0);

    // goto lumby bank
    //private RSTile[] toStairs1F = { new RSTile(3221, 3218, 0), new RSTile(3214, 3214, 0), new RSTile(3209, 3211, 0) };
    RSTile toStaiRSTile = new RSTile(3204, 3207, 0);
    RSTile toStairs1FTile = new RSTile(3204, 3207, 1);
    RSTile toStairs2FTile = new RSTile(3204, 3207, 2);
    RSTile lumbyBankTile = new RSTile(3208, 3218, 2);

    RSTile[] lumby2FArea = {new RSTile(3205, 3228, 2), new RSTile(3215, 3208, 2)};
    RSTile[] lumby1FArea = {new RSTile(3205, 3228, 1), new RSTile(3215, 3208, 1)};
    RSTile[] lumbyArea = {new RSTile(3205, 3228, 0), new RSTile(3225, 3208, 0)};

    // goto varrock bank after teleporting
    private RSTile[] toVWBankTeleport = {new RSTile(3212, 3423, 0), new RSTile(3202, 3428, 0),
        new RSTile(3190, 3429, 0), new RSTile(3184, 3436, 0)};
    RSTile[] varrockWBankArea = {new RSTile(3185, 3433, 0), new RSTile(3180, 3441, 0)};

    public void gotoVWBankTelespot() {

        Walking.walkPath(toVWBankTeleport);
    }

    RSTile alkaridGateTile = new RSTile(3267, 3227, 0);
    RSTile scorpionMiddle = new RSTile(3298, 3293, 0);
    RSTile darkHole = new RSTile(3169, 3172, 0);
    RSTile spidersMiddle = new RSTile(3165, 3244, 0);
    RSTile[] spiderLumby = {new RSTile(3130, 3262, 0), new RSTile(3230, 3200, 0)};

    RSTile kalphiteMiddle = new RSTile(3506, 9522, 2);
    RSTile edgeLadderTile = new RSTile(3097, 9867, 0);
    private RSTile[] toGate = {new RSTile(3096, 9867, 0), new RSTile(3096, 9877, 0),
        new RSTile(3095, 9887, 0), new RSTile(3095, 9895, 0), new RSTile(3096, 9904, 0),
        new RSTile(3102, 9909, 0)};
    private RSTile[] toSkeletons = {new RSTile(3105, 9909, 0), new RSTile(3115, 9909, 0),
        new RSTile(3126, 9909, 0), new RSTile(3131, 9911, 0)};
    RSTile edgeDungeonGateTile = new RSTile(3103, 9909, 0);
    RSTile[] toEdgeGateArea = {new RSTile(3094, 9915, 0), new RSTile(3103, 9867, 0)};
    RSTile[] afterEdgeGateArea = {new RSTile(3104, 9917, 0), new RSTile(3151, 9870, 0)};
    RSTile[] skeletonArea = {new RSTile(3104, 9917, 0), new RSTile(3141, 9141, 0)};
    RSTile skeletonMiddle = new RSTile(3131, 9911, 0);
    RSTile[] zombieArea = {new RSTile(3136, 9910, 0), new RSTile(3151, 9876, 0)};
    RSTile beforeIceFiends = new RSTile(3031, 3473, 0);

    /**
     * *** killing da monstas ****
     */
    //TODO FIGHT
    public void FIGHT() {

        state = "Fight";
        if (currTask.equals("banshees")) {
            if (!checkForHelm(1)) {
                equipHelm(1);
                sleep(1000, 1200);
                if (!checkForHelm(1)) {
                    equipHelm(1);
                    sleep(1000, 1200);
                }
            }
            if (!inArea(awkwardBansheeSq[0], awkwardBansheeSq[1], pos()) && inArea(bansheeArea[0], bansheeArea[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Banshee");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(bansheesT));
                waitIsMovin();
            }

        } else if (currTask.equals("bats")) {
            if (pos().distanceTo(batMiddle) <= 15) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Giant bat");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(toBat);
                waitIsMovin();
            }

        } else if (currTask.equals("bears")) {

            if (pos().distanceTo(bearsMiddle) <= 20) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Grizzly bear");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                WebWalking.walkTo(bearsMiddle);
                waitIsMovin();
            }
        } else if (currTask.equals("birds")) { // no idea atm
            if (pos().distanceTo(birdMiddle) <= 15) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Terrorbird");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(birdMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("cave_bugs")) {
            if (!checkForHelm(0)) {
                equipHelm(0);
            }

            if (inArea(caveBugArea[0], caveBugArea[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Cave bug");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(caveSlimeT));
                waitIsMovin();
            }
        } else if (currTask.equals("cave_crawlers")) {
            if (pos().distanceTo(caveCrawlerT) <= 15) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Cave crawler");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(caveCrawlerT));
                waitIsMovin();
            }
        } else if (currTask.equals("cave_slimes")) {
            if (!checkForHelm(0)) {
                equipHelm(0);
            }

            if (inArea(caveSlimeArea[0], caveSlimeArea[1], pos()) || inArea(caveSlimeArea2[0], caveSlimeArea2[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Cave slime");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(caveSlimeT));
                waitIsMovin();
            }
        } else if (currTask.equals("cows")) {
            if (pos().distanceTo(cowsMiddle) <= 10) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Cow");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(cowsMiddle));
                waitIsMovin();
            }
        } else if (currTask.equals("crawling_hands")) {

            if (pos().distanceTo(crawlingHandsT) <= 15) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Crawling Hand");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(toCrawlingHands);
                waitIsMovin();
            }

        } else if (currTask.equals("dogs")) {
            if (inArea(dogArea1[0], dogArea1[1], pos()) || inArea(dogArea2[0], dogArea2[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Guard dog");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(dogsMiddle));
                waitIsMovin();
            }
        } else if (currTask.equals("dwarves")) {
            if (pos().distanceTo(dwarfMiddle) <= 15) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Dwarf");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                println("Walking to the middle tile");
                WebWalking.walkTo(dwarfMiddle);
                waitIsMovin();
            }

        } else if (currTask.equals("ghosts")) {
            if (pos().distanceTo(ghostMiddle) <= 20) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Ghost");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(ghostMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("goblins")) {
            if (inArea(goblinArea[0], goblinArea[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Goblin");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(goblinMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("icefiends")) {
            if (distanceFromTile(iceFiendsMiddle, 15)) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Icefiend");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(iceFiendsMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("kalphite")) {

            if (distanceFromTile(kalphiteMiddle, 15)) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Kalphite Worker");

                Mouse.setSpeed(General.random(150, 170));

            } else {
                Walking.walkPath(Walking.generateStraightPath(kalphiteMiddle));
                waitIsMovin();
            }
        } else if (currTask.equals("minotaurs")) {

            if (distanceFromTile(minotaurT, 15)) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Minotaur");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(kalphiteMiddle));
                waitIsMovin();
            }
        } else if (currTask.equals("monkeys")) {
            if (pos().distanceTo(monkeyT) <= 15) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Monkey");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(monkeyT));
                waitIsMovin();
            }

        } else if (currTask.equals("rats")) {
            if (pos().distanceTo(ratsMiddle) <= 15) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Giant rat");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(ratsMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("scorpions")) {
            if (pos().distanceTo(scorpionMiddle) <= 15) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Scorpion");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(scorpionMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("skeletons")) {

            if (inArea(skeletonArea[0], skeletonArea[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Skeleton");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(skeletonMiddle));
                waitIsMovin();
            }
        } else if (currTask.equals("spiders")) {
            if (pos().distanceTo(spidersMiddle) <= 15) {
                Mouse.setSpeed(General.random(340, 360));

                attackInactiveMonster("Giant spider");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                WebWalking.walkTo(spidersMiddle);
                waitIsMovin();
            }

        } else if (currTask.equals("werewolves")) {
            if (inArea(canafisArea[0], canafisArea[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(zombieMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("wolves")) {
            if (distanceFromTile(wolfMiddle, 15)) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("White wolf");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(wolfMiddle));
                waitIsMovin();
            }

        } else if (currTask.equals("desert_lizards")) {
            if (distanceFromTile(toDesertLizards[toDesertLizards.length - 1], 15)) {
                Mouse.setSpeed(General.random(340, 360));
                if (Combat.getAttackingEntities().length > 0) {
                    for (int i = 0; i < Combat.getAttackingEntities().length; i++) {
                        if (Combat.getAttackingEntities()[i].getName().equals("Lizard")) {
                            attackInactiveMonster("Lizard");
                        }
                        break;
                    }
                } else {
                    attackInactiveMonster("Small Lizard");
                }
                Mouse.setSpeed(General.random(150, 170));
            } else {
                WebWalking.walkTo(toDesertLizards[toDesertLizards.length - 1]);
                waitIsMovin();
            }
        } else if (currTask.equals("zombies")) {
            if (inArea(zombieArea[0], zombieArea[1], pos())) {
                Mouse.setSpeed(General.random(340, 360));
                attackInactiveMonster("Zombie");
                Mouse.setSpeed(General.random(150, 170));
            } else {
                Walking.walkPath(Walking.generateStraightPath(zombieMiddle));
                waitIsMovin();
            }
        }

    }

    /**
     * *** City Areas ****
     */
    RSTile[] alkharidArea = {new RSTile(3265, 3322, 0), new RSTile(3324, 3117, 0)};
    RSTile[] desertArea = {new RSTile(3223, 3115, 0), new RSTile(3430, 3030, 0)};

    //TODO GOTO_BANK
    public void GOTO_BANK() {

        if (pos().distanceTo(varrockBankT) <= 7 || pos().distanceTo(new RSTile(3253, 3420)) <= 5) {
            BANKING();
        } else {
            state = "Going to bank";
            Mouse.setSpeed(General.random(140, 170));

            println("Going to closeset bank");
            if (pos().distanceTo(varrockTeleT) > 50) {
                emergTele();
            }
            sleep(200, 300);
            WebWalking.walkToBank();
            sleep(50, 100);

            println("done webwalking");
        }
    }

    public boolean BANKING() {
        state = "banking";
        if (Banking.isPinScreenOpen()) {
            Banking.inPin();
            sleep(200, 300);
            bank();
            sleep(200, 300);
            return true;
        } else if (Banking.isBankScreenOpen()) {
            bank();
            sleep(200, 300);
            return true;
        }
        if (!Banking.isBankScreenOpen()) {
            if (Banking.openBankBanker()) {
                if (Banking.isPinScreenOpen()) {
                    Banking.inPin();
                    sleep(200, 300);
                }

                if (Banking.isBankScreenOpen()) {
                    bank();
                    sleep(200, 300);
                }
                return true;
            } else if (Banking.openBankBooth()) {
                if (Banking.isPinScreenOpen()) {
                    Banking.inPin();
                    sleep(200, 300);
                }

                if (Banking.isBankScreenOpen()) {
                    sleep(200, 300);
                    bank();
                    sleep(200, 300);
                }
                return true;
            }
        }
        return false;
    }

    //TODO bank
    public void bank() {

        Banking.depositAllExcept(depositAllExcept);
        sleep(200, 300);

        RSItem[] coins = Inventory.find(COINS);
        //RSItem[] ltab = Inventory.find(LTAB);
        //RSItem[] atab = Inventory.find(ATAB);
        RSItem[] gem = Inventory.find(slayGem);
        RSItem[] earmuffs = Inventory.find(EARMUFFS);
        RSItem[] lightsource = Inventory.find(lightsources);

        RSItem[] tinder = Inventory.find(TINDER);
        //RSItem[] salt = Inventory.find(SALT);
        RSItem[] spiny = Inventory.find(SPINY);
        RSItem[] waterskin = Inventory.find(WATERSKIN);
        RSItem[] cooler = Inventory.find(COOLER);
        RSItem[] ecto = Inventory.find(ECTO);
        RSItem[] ROPE = Inventory.find(rope);
        RSItem[] ROD = Inventory.find(rod);
        RSItem[] games = Inventory.find(gamesNecklace);
        RSItem[] food = Inventory.find(foodID);
        RSItem[] anti = Inventory.find(antiPoison);
        RSItem[] shanty = Inventory.find(SHANTY);

        Mouse.clickBox(471, 78, 478, 84, 1);
        sleep(200, 300);
        println("step 1");
        if (games.length == 0) {
            Clicking.hover(Banking.find(gamesNecklace));
            Banking.withdraw(1, gamesNecklace);
            sleep(200, 300);
        }
        if (food.length < 10) {
            println("step 2");
            Clicking.hover(Banking.find(foodID));
            Banking.withdraw(10 - food.length, foodID);
            sleep(200, 300);
        }

        if (gem.length == 0) {
            Clicking.hover(Banking.find(slayGem));
            Banking.withdraw(1, slayGem);
            sleep(200, 300);
        }

        withdrawTabOrRune(0);
        sleep(200, 300);

        if (currTask.equals("banshees")) {

            if (ecto.length == 0) {

                Clicking.hover(Banking.find(ECTO));
                Banking.withdraw(1, ECTO);
                sleep(200, 300);
            }
            if (!checkForHelm(1) && earmuffs.length == 0) {
                Clicking.hover(Banking.find(EARMUFFS));
                Banking.withdraw(1, EARMUFFS);
                sleep(200, 300);
            }

        } else if (currTask.equals("bats")) {
            withdrawTabOrRune(1);
            sleep(200, 300);
        } else if (currTask.equals("bears")) {
            withdrawTabOrRune(4);
            sleep(200, 300);

        } else if (currTask.equals("birds")) { // no idea atm
            withdrawTabOrRune(2);
            sleep(200, 300);
        } else if (currTask.equals("cave_bugs")) {
            if (tinder.length == 0 && lightsource.length == 0 && spiny.length == 0) {

                Clicking.hover(Banking.find(lightsources));
                Banking.withdraw(1, lightsources);
                sleep(200, 300);
                Clicking.hover(Banking.find(SPINY));
                Banking.withdraw(1, SPINY);
                sleep(200, 300);
                Clicking.hover(Banking.find(TINDER));
                Banking.withdraw(1, TINDER);
                sleep(200, 300);
                Clicking.hover(Banking.find(rope));
                Banking.withdraw(1, rope);
                sleep(200, 300);
                Clicking.hover(Banking.find(rope));
                Banking.withdraw(1, rope);
                sleep(200, 300);
            }
            withdrawTabOrRune(3);
            sleep(200, 300);
        } else if (currTask.equals("cave_crawlers")) {

            withdrawTabOrRune(2);
            sleep(200, 300);

            if (anti.length < 2) {

                Clicking.hover(Banking.find(antiPoison));
                Banking.withdraw(1, antiPoison);
                sleep(200, 300);
                Clicking.hover(Banking.find(antiPoison));
                Banking.withdraw(1, antiPoison);
                sleep(200, 300);
            }
        } else if (currTask.equals("cave_slimes")) {
            if (tinder.length == 0 && lightsource.length == 0 && anti.length == 0 && spiny.length == 0 && ROPE.length == 0) {

                Clicking.hover(Banking.find(lightsources));
                Banking.withdraw(1, lightsources);
                sleep(200, 300);
                Clicking.hover(Banking.find(antiPoison));
                Banking.withdraw(1, antiPoison);
                sleep(200, 300);
                Clicking.hover(Banking.find(antiPoison));
                Banking.withdraw(1, antiPoison);
                sleep(200, 300);
                Clicking.hover(Banking.find(SPINY));
                Banking.withdraw(1, SPINY);
                sleep(200, 300);
                Clicking.hover(Banking.find(TINDER));
                Banking.withdraw(1, TINDER);
                sleep(200, 300);
                Clicking.hover(Banking.find(rope));
                Banking.withdraw(1, rope);
                sleep(200, 300);
                Clicking.hover(Banking.find(rope));
                Banking.withdraw(1, rope);
                sleep(200, 300);
            }
            withdrawTabOrRune(3);
            sleep(200, 300);
        } else if (currTask.equals("cows")) {

            withdrawTabOrRune(1);
            sleep(200, 300);

        } else if (currTask.equals("crawling_hands")) {
            if (ecto.length == 0) {
                Clicking.hover(Banking.find(ECTO));
                Banking.withdraw(1, ECTO);
                sleep(200, 300);
            }

        } else if (currTask.equals("dogs")) {

            withdrawTabOrRune(4);
            sleep(200, 300);

        } else if (currTask.equals("dwarves")) {

            withdrawTabOrRune(0);
            sleep(200, 300);

        } else if (currTask.equals("ghosts")) {

            withdrawTabOrRune(1);
            sleep(200, 300);

        } else if (currTask.equals("goblins")) {

            withdrawTabOrRune(3);
            sleep(200, 300);

        } else if (currTask.equals("icefiends")) {

            withdrawTabOrRune(1);
            sleep(200, 300);

        } else if (currTask.equals("kalphite")) {
            if (ROPE.length == 0 && waterskin.length == 0 && shanty.length == 0) {
                Clicking.hover(Banking.find(rope));
                Banking.withdraw(1, rope);
                sleep(200, 300);
                Clicking.hover(Banking.find(rope));
                Banking.withdraw(1, rope);
                sleep(200, 300);
                Clicking.hover(Banking.find(WATERSKIN));
                Banking.withdraw(1, WATERSKIN);
                sleep(200, 300);
                Clicking.hover(Banking.find(SHANTY));
                Banking.withdraw(5, SHANTY);
                sleep(200, 300);
                Clicking.hover(Banking.find(rod));
                Banking.withdraw(1, rod);
                sleep(200, 300);
            }

        } else if (currTask.equals("minotaurs")) {

            withdrawTabOrRune(0);
            sleep(200, 300);

        } else if (currTask.equals("monkeys")) {

            withdrawTabOrRune(1);
            sleep(200, 300);

            if (coins.length > 0) {
                if (coins[0].getStack() < 10000) {
                    Clicking.hover(Banking.find(COINS));
                    Banking.withdraw(10000, COINS);
                    sleep(200, 300);
                }
            } else {
                Clicking.hover(Banking.find(COINS));
                Banking.withdraw(10000, COINS);
                sleep(200, 300);
            }

        } else if (currTask.equals("rats")) {

            withdrawTabOrRune(0);
            sleep(200, 300);

        } else if (currTask.equals("scorpions")) {
            if (ROD.length == 0) {
                Clicking.hover(Banking.find(rod));
                Banking.withdraw(1, rod);
                sleep(200, 300);
            }

        } else if (currTask.equals("skeletons")) {

            withdrawTabOrRune(0);
            sleep(200, 300);

        } else if (currTask.equals("spiders")) {

            withdrawTabOrRune(3);
            sleep(200, 300);

        } else if (currTask.equals("werewolves")) {
            if (ecto.length == 0) {
                Clicking.hover(Banking.find(ECTO));
                Banking.withdraw(1, ECTO);
                sleep(200, 300);
            }

        } else if (currTask.equals("wolves")) {

            withdrawTabOrRune(2);
            sleep(200, 300);

        } else if (currTask.equals("desert_lizards")) {
            if (ROD.length == 0 && waterskin.length == 0 && shanty.length == 0 && cooler.length < numLeftToKill + 10) {
                Clicking.hover(Banking.find(rod));
                Banking.withdraw(1, rod);
                sleep(200, 300);
                Clicking.hover(Banking.find(WATERSKIN));
                Banking.withdraw(8, WATERSKIN);
                sleep(200, 300);
                Clicking.hover(Banking.find(SHANTY));
                Banking.withdraw(5, SHANTY);
                sleep(200, 300);
                Clicking.hover(Banking.find(COOLER));
                Banking.withdraw(numLeftToKill + 30, COOLER);
                sleep(200, 300);
            }

        } else if (currTask.equals("zombies")) {

            withdrawTabOrRune(0);
            sleep(200, 300);

        }

        Banking.close();
        sleep(200, 300);

    }

    YawhideHelper helper = new YawhideHelper();

    //TODO haveReqEquip
    public boolean haveReqEquip(int option) {
        state = "item check";
        sleep(300, 320);
        RSItem[] coins = Inventory.find(COINS);
        RSItem[] gem = Inventory.find(slayGem);
        RSItem[] earmuffs = Inventory.find(EARMUFFS);
        RSItem[] lightsource = Inventory.find(lightsources);

        RSItem[] tinder = Inventory.find(TINDER);
        //RSItem[] salt = Inventory.find(SALT);
        RSItem[] spiny = Inventory.find(SPINY);
        RSItem[] waterskin = Inventory.find(WATERSKIN);
        RSItem[] cooler = Inventory.find(COOLER);
        RSItem[] ecto = Inventory.find(ECTO);
        RSItem[] ROPE = Inventory.find(rope);
        RSItem[] ROD = Inventory.find(rod);
        RSItem[] games = Inventory.find(gamesNecklace);
        RSItem[] food = Inventory.find(foodID);
        RSItem[] anti = Inventory.find(antiPoison);
        RSItem[] shanty = Inventory.find(SHANTY);

        if (option == 0) {

            if (games.length == 0) {
                println("Option 0 check: Dont have either a games necklace");
                return false;
            } else if (food.length == 0) {
                println("Option 0 check: Dont have food");
                return false;
            } else if (gem.length == 0) {
                println("Option 0 check: Dont have slayer gem");
                return false;
            } else if (!helper.hasReqTab(useTabs, "dwarves")) {
                println("Option 0 check: dont have req tabs or runes!");
                return false;
            }

            println("Option 0 check: I have games/food/gem/varrock tele");

            if (currTask.equals("banshees")) {

                if (ecto.length > 0 && (checkForHelm(1) || earmuffs.length > 0)) {

                    println("we got our stuff");
                    return true;
                }
            } else if (currTask.equals("bats")) {

                if (helper.hasReqTab(useTabs, "bats")) {//!haveRune(1) || !haveRune(2) || !haveRune(4)){
                    return true;
                }
            } else if (currTask.equals("bears")) {
                if (helper.hasReqTab(useTabs, "bears")) {//!haveRune(1) || !haveRune(4)){

                    return true;
                }
            } else if (currTask.equals("birds")) { // no idea atm

                if (helper.hasReqTab(useTabs, "birds"))//!haveRune(1) || !haveRune(4))
                {
                    return true;
                }

            } else if (currTask.equals("cave_bugs")) {
                if (ROPE.length == 0 || tinder.length == 0 || lightsource.length == 0 || (!checkForHelm(0) && spiny.length == 0)) {
                    return false;
                }

                if (helper.hasReqTab(useTabs, "cave_bugs"))//!haveRune(1) || !haveRune(0) || !haveRune(2))
                {
                    return true;
                }

            } else if (currTask.equals("cave_crawlers")) {

                if (anti.length < 2) {
                    return false;
                }

                if (helper.hasReqTab(useTabs, "cave_crawlers"))//!haveRune(1) || !haveRune(2))
                {
                    return true;
                }

            } else if (currTask.equals("cave_slimes")) {
                if (ROPE.length == 0 || tinder.length == 0 || lightsource.length == 0 || anti.length == 0 || (!checkForHelm(0) && spiny.length == 0)) {
                    return false;
                }
                if (helper.hasReqTab(useTabs, "cave_slimes"))//!haveRune(1) || !haveRune(0) || !haveRune(2))
                {
                    return true;
                }
            } else if (currTask.equals("cows")) {

                if (helper.hasReqTab(useTabs, "cows"))//!haveRune(1) || !haveRune(2) || !haveRune(4))
                {
                    return true;
                }

            } else if (currTask.equals("crawling_hands")) {
                if (ecto.length > 0) {
                    return true;
                }
            } else if (currTask.equals("dogs")) {

                if (helper.hasReqTab(useTabs, "dogs"))//!haveRune(1) || !haveRune(4))
                {
                    return true;
                }

            } else if (currTask.equals("dwarves")) {

                if (helper.hasReqTab(useTabs, "dwarves")) {//!haveRune(1) || !haveRune(2) || !haveRune(3)){
                    println("Dont have  varrock runes");
                    return true;

                }

            } else if (currTask.equals("ghosts")) {

                if (helper.hasReqTab(useTabs, "ghosts"))//!haveRune(1) || !haveRune(2) || !haveRune(4))
                {
                    return true;
                }

            } else if (currTask.equals("goblins")) {

                if (helper.hasReqTab(useTabs, "goblins"))//!haveRune(1) || !haveRune(0) || !haveRune(2))
                {
                    return true;
                }

            } else if (currTask.equals("icefiends")) {

                if (helper.hasReqTab(useTabs, "icefiends"))//!haveRune(1) || !haveRune(2) || !haveRune(4))
                {
                    return true;
                }

            } else if (currTask.equals("kalphite")) {
                //println("chceking if I have all the stuff for kalphite");
                if (inArea(desertArea[0], desertArea[1], pos())) {
                    if (ROPE.length > 0 && waterskin.length > 0) {
                        return true;
                    }
                } else if (inArea(alkharidArea[0], alkharidArea[1], pos())) {
                    if (ROPE.length > 0 && waterskin.length > 0 && shanty.length > 0) {
                        return true;
                    }
                } else if (inArea(kalphiteTunnel[0], kalphiteTunnel[1], pos())) {
                    return true;
                } else if (ROPE.length > 0 && waterskin.length > 0 && shanty.length > 0 && ROD.length > 0) {
                    return true;
                }
            } else if (currTask.equals("minotaurs")) {

                if (helper.hasReqTab(useTabs, "minotaurs")) {//!haveRune(1) || !haveRune(2) || !haveRune(3)){
                    println("Dont have  varrock runes");
                    return true;

                }

            } else if (currTask.equals("monkeys")) {
                if (coins.length == 0) {
                    return false;
                }

                if (helper.hasReqTab(useTabs, "monkeys"))//!haveRune(1) || !haveRune(2) || !haveRune(4))
                {
                    return true;
                }

            } else if (currTask.equals("rats")) {

                if (helper.hasReqTab(useTabs, "rats")) {//!haveRune(1)|| !haveRune(2) || !haveRune(3)){
                    println("Dont have  varrock runes");
                    return true;
                }

            } else if (currTask.equals("scorpions")) {
                if (ROD.length > 0) {
                    return true;
                }
            } else if (currTask.equals("skeletons")) {

                if (helper.hasReqTab(useTabs, "skeletons")) {//!haveRune(1) || !haveRune(2) || !haveRune(3)){
                    println("Dont have  varrock runes");
                    return true;
                }

            } else if (currTask.equals("spiders")) {

                if (helper.hasReqTab(useTabs, "spiders"))//!haveRune(1) || !haveRune(0) || !haveRune(2))
                {
                    return true;
                }

            } else if (currTask.equals("werewolves")) {
                if (ecto.length > 0) {
                    return true;
                }
            } else if (currTask.equals("wolves")) {

                if (helper.hasReqTab(useTabs, "wolves"))//!haveRune(1) || !haveRune(2))
                {
                    return true;
                }

            } else if (currTask.equals("desert_lizards")) {
                if (inArea(desertArea[0], desertArea[1], pos())) {
                    if (waterskin.length > 0 && cooler.length > 0) {
                        return true;
                    }
                } else if (inArea(alkharidArea[0], alkharidArea[1], pos())) {
                    if (waterskin.length > 0 && shanty.length > 0 && cooler.length > 0) {
                        return true;
                    }
                } else if (ROD.length > 0 && waterskin.length > 0 && shanty.length > 0 && cooler.length > 0) {
                    return true;
                }
            } else if (currTask.equals("zombies")) {

                if (helper.hasReqTab(useTabs, "zombies")) {//!haveRune(1) || !haveRune(2) || !haveRune(3)){
                    println("Dont have  varrock runes");
                    return true;
                }

            }
            return false;
        } /**
         * *** option 1 **** I am at my task already
         *
         *
         *
         *
         *
         */
        else {
            if (games.length == 0) {
                println("Option 1 check: Dont have either a games necklace");
                return false;
            } else if (food.length == 0) {
                println("Option 1 check: Dont have food");
                return false;
            } else if (gem.length == 0) {
                println("Option 1 check: Dont have slayer gem");
                return false;
            } else if (!helper.hasReqTab(useTabs, "dwarves")) {//!haveRune(1) || !haveRune(2) || !haveRune(3)){
                println("Option 1 check: Dont have  varrock runes");
                return false;

            }
            //println("Option 1 check: I have games/food/gem/varrock tele");

            if (currTask.equals("banshees")) {
                if (ecto.length > 0 && (checkForHelm(1) || earmuffs.length > 0)) {
                    return true;
                }
                println("Dont have ecto or earmuffs");
            } else if (currTask.equals("bats")) {
                //if(ftab.length > 0)
                return true;
            } else if (currTask.equals("bears")) {
                //if(atab.length > 0)
                return true;

            } else if (currTask.equals("birds")) { // no idea atm
                //if(atab.length > 0)
                return true;
            } else if (currTask.equals("cave_bugs")) {
                if (ROPE.length > 0 && lightsource.length > 0 && (checkForHelm(0) || spiny.length > 0)) {
                    return true;
                }
            } else if (currTask.equals("cave_crawlers")) {
                if (anti.length > 0) {
                    return true;
                }
            } else if (currTask.equals("cave_slimes")) {
                if (ROPE.length > 0 && tinder.length > 0 && lightsource.length > 0 && anti.length > 0 && (checkForHelm(0) || spiny.length > 0)) {
                    return true;
                }
            } else if (currTask.equals("cows")) {
                //if(ftab.length > 0)
                return true;
            } else if (currTask.equals("crawling_hands")) {
                if (ecto.length > 0) {
                    return true;
                }
            } else if (currTask.equals("dogs")) {
                //if(atab.length > 0)
                return true;
            } else if (currTask.equals("dwarves")) {
                //if(vtab.length > 0)
                return true;
            } else if (currTask.equals("ghosts")) {
                //if(ftab.length > 0)
                return true;
            } else if (currTask.equals("goblins")) {
                //if(ltab.length > 0)
                return true;
            } else if (currTask.equals("icefiends")) {
                //if(ftab.length > 0)
                return true;
            } else if (currTask.equals("kalphite")) {
                return true;
            } else if (currTask.equals("minotaurs")) {
                //if(vtab.length > 0)
                return true;
            } else if (currTask.equals("monkeys")) {
                //if(ftab.length > 0)
                return true;
            } else if (currTask.equals("rats")) {
                //if(vtab.length > 0)
                return true;
            } else if (currTask.equals("scorpions")) {
                return true;
            } else if (currTask.equals("skeletons")) {
                //if(vtab.length > 0)
                return true;
            } else if (currTask.equals("spiders")) {
                //if(ltab.length > 0)
                return true;
            } else if (currTask.equals("werewolves")) {
                if (ecto.length > 0) {
                    return true;
                }
            } else if (currTask.equals("wolves")) {
                //if(ctab.length > 0)
                return true;
            } else if (currTask.equals("desert_lizards")) {
                if (waterskin.length > 0 && cooler.length > 0) {
                    return true;
                }
            } else if (currTask.equals("zombies")) {
                //if(vtab.length > 0)
                return true;
            }
            return false;
        }
    }

    //, Bat, Bear, Birds, , Cave Slimes, Cows, 
    //Dog, Dwarf, Ghost, Goblin, Icefiend, , Minotaur, Monkey, Giant rat or Rat, 
    //, Skeleton, Spider, , Wolf, Desert Lizard, and Zombie
    //Done
    //Banshee Cave Crawlers Crawling Hand Kalphite Scorpion Werewolf
    RSTile bansheesT = new RSTile(3442, 3545, 0); //673
    RSTile[] bansheeArea = {new RSTile(3431, 3567, 0), new RSTile(3453, 3531, 0)};
    RSTile[] slayerTowerArea = {new RSTile(3405, 3579, 0), new RSTile(3453, 3532, 0)};

    RSTile slayerTowerEntranceT = new RSTile(3428, 3535, 0);
    Positionable slayerTowerEntranceP = slayerTowerEntranceT;
    RSTile crawlingHandsT = new RSTile(3417, 3557, 0); //714
    RSTile[] toCanafis = {new RSTile(3659, 3524, 0), new RSTile(3657, 3531, 0),
        new RSTile(3651, 3532, 0), new RSTile(3645, 3533, 0), new RSTile(3639, 3533, 0),
        new RSTile(3630, 3534, 0), new RSTile(3623, 3534, 0), new RSTile(3616, 3534, 0),
        new RSTile(3610, 3534, 0), new RSTile(3604, 3534, 0), new RSTile(3597, 3534, 0),
        new RSTile(3591, 3532, 0), new RSTile(3584, 3530, 0), new RSTile(3578, 3529, 0),
        new RSTile(3570, 3529, 0), new RSTile(3562, 3528, 0), new RSTile(3553, 3528, 0),
        new RSTile(3544, 3528, 0), new RSTile(3537, 3527, 0), new RSTile(3529, 3527, 0),
        new RSTile(3520, 3523, 0), new RSTile(3513, 3520, 0), new RSTile(3511, 3512, 0),
        new RSTile(3507, 3503, 0), new RSTile(3504, 3500, 0), new RSTile(3501, 3496, 0),
        new RSTile(3498, 3492, 0), new RSTile(3494, 3489, 0)};
    RSTile[] toSlayerTower = {new RSTile(3659, 3524, 0), new RSTile(3657, 3531, 0),
        new RSTile(3651, 3532, 0), new RSTile(3645, 3533, 0), new RSTile(3639, 3533, 0),
        new RSTile(3630, 3534, 0), new RSTile(3623, 3534, 0), new RSTile(3616, 3534, 0),
        new RSTile(3610, 3534, 0), new RSTile(3604, 3534, 0), new RSTile(3597, 3534, 0),
        new RSTile(3591, 3532, 0), new RSTile(3584, 3530, 0), new RSTile(3578, 3529, 0),
        new RSTile(3570, 3529, 0), new RSTile(3562, 3528, 0), new RSTile(3553, 3528, 0),
        new RSTile(3544, 3528, 0), new RSTile(3537, 3527, 0), new RSTile(3529, 3527, 0),
        new RSTile(3520, 3523, 0), new RSTile(3513, 3520, 0), new RSTile(3511, 3512, 0),
        new RSTile(3507, 3503, 0), new RSTile(3504, 3500, 0), new RSTile(3501, 3496, 0),
        new RSTile(3498, 3492, 0), new RSTile(3494, 3489, 0), new RSTile(3488, 3483, 0),
        new RSTile(3482, 3478, 0), new RSTile(3476, 3477, 0), new RSTile(3468, 3475, 0),
        new RSTile(3463, 3478, 0), new RSTile(3457, 3486, 0), new RSTile(3452, 3492, 0),
        new RSTile(3446, 3497, 0), new RSTile(3442, 3505, 0), new RSTile(3436, 3511, 0),
        new RSTile(3432, 3518, 0), new RSTile(3429, 3524, 0), new RSTile(3428, 3532, 0),
        new RSTile(3428, 3535, 0)};
    RSTile[] toCrawlingHands = {new RSTile(3428, 3538, 0), new RSTile(3418, 3546, 0), new RSTile(3428, 3542, 0),
        new RSTile(3428, 3548, 0), new RSTile(3426, 3554, 0), new RSTile(3421, 3557, 0),
        new RSTile(3415, 3558, 0), new RSTile(3412, 3562, 0), new RSTile(3412, 3570, 0),
        new RSTile(3418, 3570, 0)};
    RSTile[] toBanshees = {new RSTile(3428, 3538, 0), new RSTile(3427, 3543, 0),
        new RSTile(3428, 3548, 0), new RSTile(3426, 3554, 0), new RSTile(3421, 3557, 0),
        new RSTile(3415, 3558, 0), new RSTile(3412, 3562, 0), new RSTile(3412, 3570, 0),
        new RSTile(3418, 3570, 0), new RSTile(3424, 3572, 0), new RSTile(3431, 3572, 0),
        new RSTile(3437, 3573, 0), new RSTile(3445, 3573, 0), new RSTile(3446, 3567, 0),
        new RSTile(3443, 3563, 0), new RSTile(3438, 3560, 0), new RSTile(3435, 3555, 0),
        new RSTile(3436, 3550, 0), new RSTile(3442, 3545, 0)};
    RSTile[] betweenCanafisEctoArea = {new RSTile(3475, 3537, 0), new RSTile(3665, 3470, 0)};
    RSTile[] canafisArea = {new RSTile(3470, 3510, 0), new RSTile(3520, 3466, 0)};
    RSTile[] betweenCanafisSlayerTowerArea = {new RSTile(3400, 3537, 0), new RSTile(3475, 3470, 0)};
    RSTile[] slayerTowerDoorArea = {new RSTile(3426, 3535, 0), new RSTile(3431, 3533, 0)};
    RSTile[] awkwardBansheeSq = {new RSTile(3430, 3546, 0), new RSTile(3436, 3541, 0)};
    RSTile[] awkwardBansheeSqPath = {new RSTile(3429, 3543, 0), new RSTile(3428, 3551, 0), new RSTile(3422, 3556, 0),
        new RSTile(3414, 3557, 0), new RSTile(3412, 3563, 0), new RSTile(3412, 3571, 0)};

    RSTile lumbySwampT = new RSTile(3169, 3173, 0);
    RSTile[] toSlimes = {new RSTile(3165, 9573, 0), new RSTile(3156, 9573, 0),
        new RSTile(3148, 9571, 0), new RSTile(3149, 9565, 0), new RSTile(3153, 9556, 0),
        new RSTile(3153, 9554, 0)};
    RSTile caveSlimeT = new RSTile(3153, 9554, 0);
    RSTile[] toLumbySwampArea = {new RSTile(3160, 3223, 0), new RSTile(3248, 3165, 0)};
    RSTile[] swampArea = {new RSTile(3144, 9578, 0), new RSTile(3170, 9543, 0)};
    RSTile[] caveSlimeArea = {new RSTile(9147, 9554, 0), new RSTile(9546, 9543, 0)};
    RSTile[] caveSlimeArea2 = {new RSTile(3145, 9567, 0), new RSTile(3163, 9551, 0)};

    RSTile lumbySwampEntranceT = new RSTile(3169, 3172, 0);
    RSTile afterLumbySwampEntranceT = new RSTile(3169, 9571, 0);
    RSTile[] toCaveSlimeT = {new RSTile(3165, 9573, 0), new RSTile(3157, 9573, 0),
        new RSTile(3147, 9567, 0), new RSTile(3152, 9561, 0), new RSTile(3153, 9554, 0)};
    RSTile nearedgeTrapdoorTile = new RSTile(3094, 3471, 0);
    //RSTile nearedgeTrapdoorTile = new RSTile(3096, 3468, 0);

    RSTile[] caveBugArea = {new RSTile(3144, 9583, 0), new RSTile(3160, 9568, 0)};
    RSTile[] toCaveBug = {new RSTile(3165, 9573, 0), new RSTile(3157, 9573, 0), new RSTile(3151, 9573, 0)};

    RSTile renekSlayerT = new RSTile(2796, 3614, 0);
    RSTile afterCaveEntrance = new RSTile(2807, 10001, 0);
    int CaveEntrance = 2292; // Enter
    RSTile caveEntranceT = new RSTile(2796, 3615, 0);
    RSTile renekSlayCave = new RSTile(2797, 3614, 0);
    RSTile[] toCCinside = {new RSTile(2804, 10001, 0), new RSTile(2798, 9997, 0), new RSTile(2792, 9996, 0),
        new RSTile(2788, 9996, 0)};
    RSTile[] toRenekSlayCaveArea = {new RSTile(2633, 3650, 0), new RSTile(2810, 3480, 0)};
    RSTile[] toRenekSlayCave = {new RSTile(2752, 3477, 0), new RSTile(2746, 3479, 0), new RSTile(2740, 3480, 0),
        new RSTile(2731, 3484, 0), new RSTile(2724, 3483, 0), new RSTile(2715, 3484, 0),
        new RSTile(2706, 3484, 0), new RSTile(2699, 3484, 0), new RSTile(2692, 3484, 0),
        new RSTile(2687, 3486, 0), new RSTile(2687, 3492, 0), new RSTile(2687, 3499, 0),
        new RSTile(2687, 3504, 0), new RSTile(2687, 3509, 0), new RSTile(2686, 3514, 0),
        new RSTile(2684, 3518, 0), new RSTile(2682, 3523, 0), new RSTile(2681, 3528, 0),
        new RSTile(2679, 3533, 0), new RSTile(2678, 3538, 0), new RSTile(2678, 3546, 0),
        new RSTile(2675, 3550, 0), new RSTile(2673, 3554, 0), new RSTile(2668, 3557, 0),
        new RSTile(2662, 3559, 0), new RSTile(2660, 3565, 0), new RSTile(2658, 3570, 0),
        new RSTile(2654, 3576, 0), new RSTile(2653, 3582, 0), new RSTile(2653, 3588, 0),
        new RSTile(2654, 3597, 0), new RSTile(2655, 3603, 0), new RSTile(2659, 3607, 0),
        new RSTile(2666, 3607, 0), new RSTile(2670, 3607, 0), new RSTile(2677, 3609, 0),
        new RSTile(2685, 3609, 0), new RSTile(2691, 3609, 0), new RSTile(2699, 3608, 0),
        new RSTile(2708, 3608, 0), new RSTile(2716, 3607, 0), new RSTile(2725, 3606, 0),
        new RSTile(2733, 3606, 0), new RSTile(2739, 3603, 0), new RSTile(2747, 3601, 0),
        new RSTile(2754, 3599, 0), new RSTile(2761, 3598, 0), new RSTile(2770, 3595, 0),
        new RSTile(2778, 3595, 0), new RSTile(2786, 3597, 0), new RSTile(2789, 3602, 0),
        new RSTile(2792, 3609, 0), new RSTile(2795, 3615, 0)};
    RSTile[] caveCrawlerArea = {new RSTile(2775, 10006, 0), new RSTile(2810, 9985, 0)};

    RSTile caveCrawlerT = new RSTile(2788, 9995, 0);
    String[] wereWolves = {"Yuri", "Zoja", "Lev", "Svetlana", "Boris", "Irina", "Sofiya",
        "Yadviga", "Nikita", "Fidelio", "Joseph", "Georgy"};
    RSTile wereWolvesT = new RSTile(3494, 3489, 0);
    RSTile duelAreaGateCloseT = new RSTile(3312, 3234, 0); //3197
    RSTile scorpionT = new RSTile(3298, 3194, 0);

    RSTile shantyPassT = new RSTile(3303, 3117, 0);
    RSTile[] afterShantyPassArea = {new RSTile(3297, 3115, 0), new RSTile(3315, 3106, 0)};
    RSTile bankChestShantyT = new RSTile(3308, 3120, 0); //id is 2693

    RSTile camelotTeleSpot = new RSTile(2757, 3476, 0);
    RSTile lumbyTeleT = new RSTile(3223, 3219, 0);
    RSTile varrockTeleT = new RSTile(3211, 3424, 0);
    RSTile ardougneTeleT = new RSTile(2661, 3303, 0);

    RSTile[] ectoTeleArea = {new RSTile(3651, 3529, 0), new RSTile(3667, 3510, 0)};

    RSTile[] rodTeleSpotArea = {new RSTile(3312, 3239, 0), new RSTile(3318, 3231, 0)};
    RSTile rodGateTile = new RSTile(3312, 3234, 0);

    RSTile[] toKalphite = {new RSTile(3304, 3113, 0), new RSTile(3297, 3107, 0),
        new RSTile(3290, 3107, 0), new RSTile(3282, 3106, 0), new RSTile(3272, 3107, 0),
        new RSTile(3262, 3108, 0), new RSTile(3256, 3108, 0), new RSTile(3248, 3108, 0),
        new RSTile(3239, 3105, 0), new RSTile(3232, 3103, 0), new RSTile(3227, 3107, 0)};
    RSTile tunnelTile = new RSTile(3227, 3108, 0);
    RSTile inTunnel = new RSTile(3484, 9510, 2);

    RSTile[] kalphiteTunnel = {new RSTile(3472, 9530, 2), new RSTile(3515, 9506, 2)};
    RSTile[] toKalphitePart2 = {new RSTile(3488, 9510, 2), new RSTile(3495, 9509, 2),
        new RSTile(3498, 9515, 2), new RSTile(3503, 9520, 2), new RSTile(3507, 9522, 2)};

    // desert lizards
    RSTile[] toDesertLizards = {new RSTile(3305, 3111, 0), new RSTile(3308, 3107, 0),
        new RSTile(3310, 3101, 0), new RSTile(3315, 3098, 0), new RSTile(3322, 3094, 0),
        new RSTile(3330, 3092, 0), new RSTile(3338, 3090, 0), new RSTile(3345, 3089, 0),
        new RSTile(3352, 3086, 0), new RSTile(3360, 3083, 0), new RSTile(3366, 3082, 0),
        new RSTile(3373, 3082, 0), new RSTile(3382, 3081, 0), new RSTile(3388, 3076, 0),
        new RSTile(3392, 3069, 0), new RSTile(3398, 3066, 0), new RSTile(3405, 3062, 0)};

    RSTile manHoleT = new RSTile(3237, 3457, 0);
    RSTile afterManHoleT = new RSTile(3237, 3457, 0);
    RSTile manHole = new RSTile(3237, 3458, 0);
    RSTile ratsMiddle = new RSTile(3237, 9867, 0);

    RSTile dwarfMiddle = new RSTile(3011, 3451, 0);
    RSTile[] dwarfArea = {new RSTile(3989, 3475, 0), new RSTile(3070, 3415, 0)};
    RSTile[] trickyDwarf = {new RSTile(3021, 3455, 0), new RSTile(3025, 3445, 0)};
    RSTile[] dwarfAttackArea = {new RSTile(3003, 3462, 0), new RSTile(3020, 3442, 0)};
    RSTile[] trickyDwarf1 = {new RSTile(3014, 3452, 0), new RSTile(3014, 3454, 0), new RSTile(3010, 3452, 0),
        new RSTile(3010, 3449, 0), new RSTile(3008, 3449, 0), new RSTile(3008, 3454, 0)};
    RSTile[] trickyDwarf2 = {new RSTile(3017, 3454, 0), new RSTile(3017, 3452, 0), new RSTile(3021, 3452, 0),
        new RSTile(3021, 3449, 0), new RSTile(3023, 3450, 0), new RSTile(3023, 3454, 0)};

    RSArea trickyDwarf1Area = new RSArea(trickyDwarf1);
    RSArea trickyDwarf2Area = new RSArea(trickyDwarf2);

    RSTile faladorTeleT = new RSTile(2965, 3376, 0);

    RSTile lowWallT = new RSTile(2935, 3355, 0);
    RSTile lowWall = new RSTile(2938, 3355, 0);
    RSTile afterLowWall = new RSTile(2934, 3355, 0);
    RSTile tavernlyLadder = new RSTile(2884, 3396, 0);
    RSTile tavernlyLadderT = new RSTile(2884, 3397, 0);
    RSTile afterTavernlyLadderT = new RSTile(2884, 9798, 0);
    RSTile[] toGhost = {new RSTile(2884, 9799, 0), new RSTile(2884, 9806, 0),
        new RSTile(2884, 9814, 0), new RSTile(2884, 9820, 0), new RSTile(2884, 9825, 0),
        new RSTile(2884, 9834, 0), new RSTile(2884, 9840, 0), new RSTile(2888, 9846, 0),
        new RSTile(2894, 9848, 0), new RSTile(2898, 9849, 0)};
    RSTile ghostMiddle = new RSTile(2898, 9849, 0);

    RSTile[] toBat = {new RSTile(2884, 9799, 0), new RSTile(2884, 9806, 0),
        new RSTile(2884, 9814, 0), new RSTile(2884, 9820, 0), new RSTile(2884, 9825, 0),
        new RSTile(2884, 9834, 0), new RSTile(2884, 9840, 0), new RSTile(2888, 9846, 0),
        new RSTile(2894, 9848, 0), new RSTile(2898, 9849, 0), new RSTile(2904, 9849, 0),
        new RSTile(2911, 9849, 0), new RSTile(2914, 9841, 0), new RSTile(2914, 9835, 0),
        new RSTile(2915, 9833, 0), new RSTile(2914, 9828)};
    RSTile batMiddle = new RSTile(2915, 9833, 0);
    RSTile[] toTavernlyDungPath = {new RSTile(2928, 3357, 0), new RSTile(2925, 3363, 0), new RSTile(2921, 3370, 0),
        new RSTile(2914, 3373, 0), new RSTile(2907, 3377, 0), new RSTile(2901, 3381, 0),
        new RSTile(2895, 3386, 0), new RSTile(2892, 3391, 0), new RSTile(2885, 3392, 0),
        new RSTile(2884, 3396, 0)};
    RSTile[] toTavLadderDown = {new RSTile(2877, 3403, 0), new RSTile(2935, 3346, 0)};
    RSTile[] insideTavBatArea = {new RSTile(2880, 9853, 0), new RSTile(2931, 9794, 0)};
    RSTile[] insideTavGhostArea = {new RSTile(2880, 9853, 0), new RSTile(2924, 9794, 0)};

    RSTile bearsMiddle = new RSTile(2699, 3333, 0);

    RSTile dogGate = new RSTile(2636, 3307, 0);
    RSTile dogsMiddle = new RSTile(2635, 3321, 0);
    RSTile[] dogArea1 = {new RSTile(2626, 3315, 0), new RSTile(2642, 3308, 0)};
    RSTile[] dogArea2 = {new RSTile(2624, 3331, 0), new RSTile(2642, 3315, 0)};
    RSTile[] ardyArea = {new RSTile(2602, 3340), new RSTile(2690, 3265, 0)};

    RSTile[] toWolfArea = {new RSTile(2735, 3495, 0), new RSTile(2866, 3428, 0)};
    RSTile wolfMiddle = new RSTile(2847, 3481, 0);
    RSTile[] wolfArea = {new RSTile(2837, 3491, 0), new RSTile(2850, 3468, 0)};

    RSTile varrockBankT = new RSTile(3183, 3437, 0);
    RSTile[] burthorpeArea = {new RSTile(2893, 3566, 0), new RSTile(2937, 3530, 0)};
    RSTile turaelT = new RSTile(2931, 3536, 0);
    RSTile[] varrockArea = {new RSTile(3176, 3448, 0), new RSTile(3255, 3386, 0)};
    RSTile[] toStrongholdArea = {new RSTile(3074, 3440, 0), new RSTile(3180, 3408, 0)};
    RSTile strongHoldHole = new RSTile(3081, 3420, 0);
    RSTile teleCenterT = new RSTile(1863, 5238, 0);
    RSTile[] afterPortal = {new RSTile(1905, 5229, 0), new RSTile(1916, 5210, 0)};
    RSTile[] afterPortal2 = {new RSTile(1911, 5209, 0), new RSTile(1912, 5207, 0)};
    RSTile[] afterPortal3 = {new RSTile(1907, 5206, 0), new RSTile(1912, 5202, 0)};
    RSTile[] afterPortal4 = {new RSTile(1904, 5204, 0), new RSTile(1906, 5203, 0)};
    RSTile strongHoldDoor = new RSTile(1911, 5209, 0);
    RSTile strongHoldDoor2 = new RSTile(1911, 5206, 0);
    RSTile strongHoldDoor3 = new RSTile(1907, 5204, 0);
    RSTile strongHoldDoor4 = new RSTile(1904, 5204, 0);
    RSTile minotaurT = new RSTile(1898, 5196, 0);
    RSTile[] minotaurArea = {new RSTile(1891, 5202, 0), new RSTile(1905, 5189, 0)};
    RSTile[] pathToDoor1 = {new RSTile(1912, 5215, 0), new RSTile(1912, 5210, 0)};
    RSTile afterStrongHoldHole = new RSTile(1859, 5243, 0);

    RSTile goblinDoor = new RSTile(3246, 3244, 0);
    RSTile goblinMiddle = new RSTile(3249, 3240, 0);
    RSTile[] goblinArea = {new RSTile(3239, 3254, 0), new RSTile(3267, 3220, 0)};
    RSTile[] goblinHut = {new RSTile(3243, 3248, 0), new RSTile(3248, 3244, 0)};

    RSTile iceFiendsMiddle = new RSTile(3004, 3474, 0);
    RSTile[] toIceFieldsArea = {iceFiendsMiddle, toStrongholdArea[1]};
    RSTile[] beforeToIceFiendsArea = {new RSTile(3002, 3488, 0), new RSTile(3033, 3462, 0)};
    RSTile[] beforeDwarfArea = {new RSTile(3000, 3485, 0), new RSTile(3080, 3419, 0)};

    RSTile[] edgeTrapDoorArea = {new RSTile(3050, 3486, 0), varrockArea[0]};
    RSTile edgeTrapdoorTile = new RSTile(3097, 3468, 0);

    RSTile cowGate = new RSTile(2924, 3292, 0);
    //private RSTile[] fromGateToCows = { new RSTile(3256, 3266, 0), new RSTile(3257, 3274, 0), 
    //		new RSTile(3256, 3279, 0), new RSTile(3254, 3287, 0) };
    RSTile cowsMiddle = new RSTile(2925, 3283, 0);

    RSTile inBoat = new RSTile(2956, 3143, 1);
    RSTile afterPortBoat = new RSTile(2958, 3146, 0);
    RSTile monkeyT = new RSTile(2878, 3158, 0);
    RSTile portBoatT = new RSTile(3027, 3217, 0);
    RSTile[] toMonkeyArea = {new RSTile(2861, 3180, 0), new RSTile(2959, 3138, 0)};
    RSTile[] fallyArea = {new RSTile(2936, 3390, 0), new RSTile(3030, 3323, 0)};
    RSTile[] toBoatFromFally = {fallyArea[0], new RSTile(3032, 3203, 0)};
    RSTile[] toMonkey = {new RSTile(2952, 3146, 0), new RSTile(2946, 3146, 0), new RSTile(2939, 3146, 0),
        new RSTile(2934, 3149, 0), new RSTile(2927, 3151, 0), new RSTile(2920, 3150, 0),
        new RSTile(2913, 3151, 0), new RSTile(2909, 3153, 0), new RSTile(2904, 3154, 0),
        new RSTile(2899, 3156, 0), new RSTile(2894, 3155, 0), new RSTile(2888, 3153, 0),
        new RSTile(2883, 3155, 0), new RSTile(2879, 3158, 0)};

    RSTile[] toGStrongholdArea = {new RSTile(2447, 3388, 0), ardyArea[1]}; // new RSTile(2690, 3265, 0)
    RSTile strongholdDoorT = new RSTile(2461, 3380, 0);
    RSTile afterStrongholdDoorT = new RSTile(2461, 3387, 0);

    RSTile birdMiddle = new RSTile(2378, 3433, 0);
    RSTile[] toBirds = {new RSTile(2459, 3393, 0), new RSTile(2453, 3396, 0), new RSTile(2447, 3397, 0),
        new RSTile(2441, 3398, 0), new RSTile(2433, 3400, 0), new RSTile(2428, 3404, 0),
        new RSTile(2424, 3410, 0), new RSTile(2418, 3416, 0), new RSTile(2412, 3420, 0),
        new RSTile(2408, 3426, 0), new RSTile(2402, 3428, 0), new RSTile(2396, 3428, 0),
        new RSTile(2388, 3427, 0), new RSTile(2382, 3429, 0), new RSTile(2378, 3433, 0)};
    RSTile[] gnomeArea = {new RSTile(2373, 3450, 0), new RSTile(2467, 3383, 0)};
    RSTile birdGateS = new RSTile(2380, 3425, 0);
    RSTile birdGateN = new RSTile(2384, 3436, 0);

    RSTile[] bearArea = {new RSTile(2602, 3350, 0), new RSTile(2730, 3265, 0)};

    public boolean isdoorclosed() {
        RSObject[] cowgate = Objects.find(3, 7158);
        if (cowgate.length > 0) {
            return true;
        }
        return false;
    }

    //TODO GOTO_TASK
    public boolean GOTO_TASK() {
        state = "Going to task";
        Mouse.setSpeed(General.random(140, 160));
        sleep(100, 150);

        if (currTask.equals("banshees")) {
            Walking.setWalkingTimeout(10000L);
            if (!inArea(awkwardBansheeSq[0], awkwardBansheeSq[1], pos()) && inArea(bansheeArea[0], bansheeArea[1], pos())) {
                Walking.setWalkingTimeout(5000L);
                return true;
            } else if (inArea(awkwardBansheeSq[0], awkwardBansheeSq[1], pos())) {
                Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
                Walking.walkPath(awkwardBansheeSqPath);
                waitIsMovin();
                Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
            } else if (inArea(slayerTowerDoorArea[0], slayerTowerDoorArea[1], pos())) {

                if (!checkForHelm(1)) {
                    equipHelm(1);
                }

                //RSObject[] slayerDoor = Objects.getAt(slayerTowerEntranceT);
                /*
				if(slayerDoor.length > 0){
					//if(DynamicClicking.clickRSObject(slayerDoor[0],  "Open"))
					clickRSObjectAt(slayerTowerEntranceT, "Open");
						sleep(1000,1200);
				}*/
                if (openDoor(slayerTowerEntranceP)) {
                    Walking.walkPath(toBanshees);
                }
            } else if (inArea(slayerTowerArea[0], slayerTowerArea[1], pos())) {

                Walking.walkPath(toBanshees);
                waitIsMovin();

            } else if (inArea(betweenCanafisEctoArea[0], betweenCanafisEctoArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else if (inArea(canafisArea[0], canafisArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else if (inArea(betweenCanafisSlayerTowerArea[0], betweenCanafisSlayerTowerArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else if (inArea(ectoTeleArea[0], ectoTeleArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else {
                useTeleport(5);
            }
            Walking.setWalkingTimeout(5000L);
        } else if (currTask.equals("bats")) {
            if (pos().distanceTo(faladorTeleT) <= 10) {
                WebWalking.walkTo(lowWall);
                waitIsMovin();
            } else if (pos().distanceTo(lowWall) <= 2) {
                RSObject[] LOWWALL = Objects.getAt(lowWallT);
                if (LOWWALL.length > 0) {
                    Camera.setCameraAngle(General.random(40, 60));
                    if (LOWWALL[0].click("Climb-over")) {
                        sleep(5000, 6000);
                        Camera.setCameraAngle(General.random(100, 110));
                        //WebWalking.walkTo(tavernlyLadder);
                    }
                }

            } else if (pos().distanceTo(afterLowWall) <= 5) {
                Walking.walkPath(toTavernlyDungPath);
                waitIsMovin();
            } else if (pos().distanceTo(tavernlyLadderT) <= 5) {
                RSObject[] TAVLAD = Objects.getAt(tavernlyLadderT);
                if (TAVLAD.length > 0) {
                    if (TAVLAD[0].click("Climb-down")) {
                        sleep(4000, 5000);
                    }
                }
            } else if (inArea(toTavLadderDown[0], toTavLadderDown[1], pos())) {
                Walking.walkPath(toTavernlyDungPath);
                waitIsMovin();
            } else if (pos().distanceTo(afterTavernlyLadderT) <= 5) {
                Walking.walkPath(toBat);
                waitIsMovin();
            } else if (inArea(fallyArea[0], fallyArea[1], pos())) {
                WebWalking.walkTo(lowWall);
                waitIsMovin();
            } else if (pos().distanceTo(batMiddle) <= 15) {
                return true;
            } else if (inArea(insideTavBatArea[0], insideTavBatArea[1], pos())) {
                Walking.walkPath(toBat);
                waitIsMovin();
            } else {
                useTeleport(1);
            }
        } else if (currTask.equals("bears")) {
            if (pos().distanceTo(bearsMiddle) <= 20) {
                return true;
            } else if (inArea(bearArea[0], bearArea[1], pos())) {
                WebWalking.walkTo(bearsMiddle);
                waitIsMovin();
            } else if (inArea(ardyArea[0], ardyArea[1], pos())) {
                WebWalking.walkTo(bearsMiddle);
                waitIsMovin();
            } else {
                useTeleport(4);
            }
        } else if (currTask.equals("birds")) { // no idea atm

            RSTile cammyNW = new RSTile(2488, 3527);
            RSTile cammySE = new RSTile(2763, 3343);

            if (pos().distanceTo(birdMiddle) <= 10) {
                return true;
            } else if (pos().distanceTo(afterStrongholdDoorT) <= 2) {
                Walking.walkPath(toBirds);
                waitIsMovin();
            } else if (pos().distanceTo(strongholdDoorT) <= 13) {
                RSObject[] GATE = Objects.find(13, "Gate");
                if (NPCChat.getClickContinueInterface() != null) {
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.selectOption("OK then.", true)) { //24x
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(9000, 10000);
                                            if (NPCChat.clickContinue(true)) {
                                                sleep(500, 650);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (GATE.length > 0) {
                    if (GATE[0].isOnScreen()) {
                        if (GATE[0].click("Open")) {
                            sleep(5000, 6000);
                        }
                    } else {
                        Walking.walkTo(GATE[0].getPosition());
                    }
                }
            } else if (inArea(gnomeArea[0], gnomeArea[1], pos())) { // RSTile[] gnomeArea = {new RSTile(2373, 3450, 0), new RSTile(2467, 3383, 0)};
                Walking.walkPath(toBirds);
                waitIsMovin();
            } else if (inArea(cammyNW, cammySE, pos())) {//} else if (inArea(toWolfArea[0], toWolfArea[1], pos())) { // } else if (inArea(toGStrongholdArea[0], toGStrongholdArea[1], pos())) {
                WebWalking.walkTo(strongholdDoorT);
                waitIsMovin();
            } else {
                useTeleport(2); //2 cammy //4 ardy
            }

        } else if (currTask.equals("cave_crawlers")) {
            if (inArea(caveCrawlerArea[0], caveCrawlerArea[1], pos())) {
                return true;
            } else if (pos().distanceTo(caveEntranceT) <= 5) {
                Camera.turnToTile(renekSlayCave);
                sleep(200, 300);
                RSObject[] caveE = Objects.getAt(renekSlayCave);
                if (caveE.length > 0) {
                    if (caveE[0].isOnScreen() && caveE[0].click("Enter")) {
                        sleep(1000, 1200);
                        Walking.walkPath(toCCinside);
                        waitIsMovin();
                    }
                }
            } else if (pos().distanceTo(camelotTeleSpot) <= 15) {
                //WebWalking.walkTo(caveCrawlerT);
                Walking.walkPath(toRenekSlayCave);
                waitIsMovin();
            } else if (inArea(toRenekSlayCaveArea[0], toRenekSlayCaveArea[1], pos())) {
                //WebWalking.walkTo(caveCrawlerT);
                Walking.walkPath(toRenekSlayCave);
                waitIsMovin();
            } else {
                useTeleport(2);
            }
        } else if (currTask.equals("cave_bugs")) {
            if (pos().distanceTo(lumbyTeleT) <= 7) {
                WebWalking.walkTo(lumbySwampT);
                waitIsMovin();
            } else if (pos().distanceTo(lumbySwampT) <= 6) {
                RSObject[] darkHole = Objects.getAt(lumbySwampEntranceT);
                if (darkHole.length > 0) {
                    if (darkHole[0].isOnScreen()) {
                        if (darkHole[0].click("Climb-down")) {
                            sleep(2000, 2300);
                        }
                    }
                }
            } else if (pos().distanceTo(afterLumbySwampEntranceT) <= 5) {
                if (!checkForHelm(0)) {
                    equipHelm(0);
                    sleep(200, 300);
                }
                Walking.walkPath(toCaveBug);
                waitIsMovin();
            } else if (inArea(swampArea[0], swampArea[1], pos())) {
                if (!checkForHelm(0)) {
                    equipHelm(0);
                    sleep(200, 300);
                }
                Walking.walkPath(toCaveBug);
                waitIsMovin();
            } else if (inArea(toLumbySwampArea[0], toLumbySwampArea[1], pos())) {
                WebWalking.walkTo(lumbySwampT);
                waitIsMovin();
            } else if (inArea(caveBugArea[0], caveBugArea[1], pos())) {
                return true;
            } else {
                useTeleport(3);
            }
        } else if (currTask.equals("cave_slimes")) {

            //5947 with no rope || 2745
            if (pos().distanceTo(lumbyTeleT) <= 7) {
                WebWalking.walkTo(lumbySwampT);
                waitIsMovin();
            } else if (pos().distanceTo(lumbySwampT) <= 6) {
                RSObject[] darkHole = Objects.getAt(lumbySwampEntranceT);
                RSItem[] rope = Inventory.find(this.rope);
                if (darkHole.length > 0) {
                    if (darkHole[0].getID() == 5947) {
                        if (rope[0].click("Use")) {
                            General.sleep(500, 750);
                            if (darkHole[0].click("Use Rope")) {
                                General.sleep(1500, 1750);
                            }
                        }
                    } else if (darkHole[0].isOnScreen()) {
                        if (darkHole[0].click("Climb-down")) {
                            sleep(2000, 2300);
                        }
                    }
                }
            } else if (pos().distanceTo(afterLumbySwampEntranceT) <= 5) {
                if (!checkForHelm(0)) {
                    equipHelm(0);
                    sleep(200, 300);
                }
                Walking.walkPath(toSlimes);
                waitIsMovin();
            } else if (inArea(swampArea[0], swampArea[1], pos())) {
                if (!checkForHelm(0)) {
                    equipHelm(0);
                    sleep(200, 300);
                }
                Walking.walkPath(toSlimes);
                waitIsMovin();
            } else if (inArea(toLumbySwampArea[0], toLumbySwampArea[1], pos())) {
                WebWalking.walkTo(lumbySwampT);
                waitIsMovin();
            } else if (inArea(caveSlimeArea[0], caveSlimeArea[1], pos()) || inArea(caveSlimeArea2[0], caveSlimeArea2[1], pos())) {
                return true;
            } else {
                useTeleport(3);
            }
        } else if (currTask.equals("cows")) {
            if (pos().distanceTo(faladorTeleT) > 15) { //faladorTeleT
                WebWalking.walkTo(cowsMiddle);
                waitIsMovin();
                println("1");
            } else if (pos().distanceTo(cowGate) <= 3 && pos().getY() >= cowGate.getY() && Doors.isDoorAt(cowGate, true)) {
                //} else if (pos().distanceTo(cowGate) <= 2 && isdoorclosed()) {
                if (openDoor(cowGate)) {
                    println("2");
                    WebWalking.walkTo(cowsMiddle);  //7158
                    waitIsMovin();
                }

                /*RSObject[] cowgate = Objects.getAt(cowGate); 
				if(cowgate.length > 0){
					if(cowgate[0].isOnScreen()){
						Camera.setCameraRotation(0);
						Camera.setCameraAngle(General.random(40, 60));
						if(cowgate[0].click("Open")){
							sleep(500,1000);
							Camera.setCameraAngle(General.random(100, 110));
							WebWalking.walkTo(cowsMiddle);
							waitIsMovin();
						
						}
					}
				}
				else{
					WebWalking.walkTo(cowsMiddle);
					//Walking.walkPath(path)
					waitIsMovin();
				}*/
 /*RSObject[] cowgate = Objects.find(5, 7158);
                if (cowgate.length > 0) {
                    if (cowgate[0].isOnScreen()) {
                        Camera.setCameraRotation(0);
                        Camera.setCameraAngle(General.random(40, 60));
                        if (cowgate[0].click("Open")) {
                            sleep(500, 1000);
                            Camera.setCameraAngle(General.random(100, 110));
                            WebWalking.walkTo(cowsMiddle);
                            waitIsMovin();
                        }
                    }
                } else {
                    WebWalking.walkTo(cowsMiddle);
                    //Walking.walkPath(path)
                    waitIsMovin();
                }*/
            } else if (pos().distanceTo(cowsMiddle) <= 10) {
                println("3");
                return true;
            } else {
                useTeleport(1);
            }
        } else if (currTask.equals("crawling_hands")) {
            Walking.setWalkingTimeout(10000L);
            if (pos().distanceTo(crawlingHandsT) <= 15) {
                Walking.setWalkingTimeout(5000L);
                return true;
            } else if (inArea(slayerTowerDoorArea[0], slayerTowerDoorArea[1], pos())) {

                //RSObject[] slayerDoor = Objects.getAt(slayerTowerEntranceT);
                /*if(slayerDoor.length > 0){
					//if(DynamicClicking.clickRSObject(slayerDoor[0],  "Open"))
					clickRSObjectAt(slayerTowerEntranceT, "Open");
						sleep(1000,1200);
				}
				Walking.walkPath(toCrawlingHands);*/
                if (openDoor(slayerTowerEntranceP)) {
                    Walking.walkPath(toBanshees);
                }
            } else if (inArea(slayerTowerArea[0], slayerTowerArea[1], pos())) {

                Walking.walkPath(toCrawlingHands);
                waitIsMovin();

            } else if (inArea(betweenCanafisEctoArea[0], betweenCanafisEctoArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else if (inArea(canafisArea[0], canafisArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else if (inArea(betweenCanafisSlayerTowerArea[0], betweenCanafisSlayerTowerArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else if (inArea(ectoTeleArea[0], ectoTeleArea[1], pos())) {
                Walking.walkPath(toSlayerTower);
                waitIsMovin();

            } else {
                useTeleport(5);
            }
            Walking.setWalkingTimeout(5000L);
        } else if (currTask.equals("dogs")) {
            if (pos().distanceTo(ardougneTeleT) <= 10) {
                WebWalking.walkTo(dogGate);
                waitIsMovin();
            } else if (pos().distanceTo(dogGate) <= 5) {
                RSObject[] doggate = Objects.getAt(dogGate);
                if (doggate.length > 0) {
                    if (doggate[0].isOnScreen()) {
                        Camera.setCameraRotation(0);
                        Camera.setCameraAngle(General.random(40, 60));
                        if (doggate[0].click("Open")) {
                            sleep(500, 1000);
                            Camera.setCameraAngle(General.random(100, 110));
                            WebWalking.walkTo(dogsMiddle);
                            waitIsMovin();

                        }
                    }
                } else {
                    WebWalking.walkTo(dogsMiddle);
                    waitIsMovin();
                }
            } else if (inArea(ardyArea[0], ardyArea[1], pos())) {
                WebWalking.walkTo(dogGate);
                waitIsMovin();
            } else if (inArea(dogArea1[0], dogArea1[1], pos()) || inArea(dogArea2[0], dogArea2[1], pos())) {
                return true;
            } else {
                useTeleport(4);
            }
        } else if (currTask.equals("dwarves")) {
            if (pos().distanceTo(dwarfMiddle) <= 16) {
                return true;
            } else if (inArea(dwarfArea[0], dwarfArea[1], pos())) {
                WebWalking.walkTo(dwarfMiddle);
                waitIsMovin();
            } else if (inArea(beforeDwarfArea[0], beforeDwarfArea[1], pos())) {
                WebWalking.walkTo(dwarfMiddle);
                waitIsMovin();
            } else if (inArea(varrockArea[0], varrockArea[1], pos())) {
                WebWalking.walkTo(beforeIceFiends);
                waitIsMovin();
            } else if (inArea(toStrongholdArea[0], toStrongholdArea[1], pos())) {
                WebWalking.walkTo(beforeIceFiends);
                waitIsMovin();
            } else {
                useTeleport(0);
            }

        } else if (currTask.equals("ghosts")) {
            if (pos().distanceTo(faladorTeleT) <= 10) {
                WebWalking.walkTo(lowWall);
                waitIsMovin();
            } else if (pos().distanceTo(lowWall) <= 2) {
                RSObject[] LOWWALL = Objects.getAt(lowWallT);
                if (LOWWALL.length > 0) {
                    Camera.setCameraAngle(General.random(40, 60));
                    if (LOWWALL[0].click("Climb-over")) {
                        sleep(5000, 6000);
                        Camera.setCameraAngle(General.random(100, 110));
                        //WebWalking.walkTo(tavernlyLadder);
                    }
                }

            } else if (pos().distanceTo(afterLowWall) <= 5) {
                Walking.walkPath(toTavernlyDungPath);
                waitIsMovin();
            } else if (pos().distanceTo(tavernlyLadderT) <= 5) {
                RSObject[] TAVLAD = Objects.getAt(tavernlyLadderT);
                if (TAVLAD.length > 0) {
                    if (TAVLAD[0].click("Climb-down")) {
                        sleep(4000, 5000);
                    }
                }
            } else if (pos().distanceTo(afterTavernlyLadderT) <= 5) {
                Walking.walkPath(toGhost);
                waitIsMovin();

            } else if (inArea(toTavLadderDown[0], toTavLadderDown[1], pos())) {
                Walking.walkPath(toTavernlyDungPath);
                waitIsMovin();
            } else if (inArea(fallyArea[0], fallyArea[1], pos())) {
                WebWalking.walkTo(lowWall);
                waitIsMovin();
            } else if (pos().distanceTo(ghostMiddle) <= 20) {
                return true;
            } else if (inArea(insideTavBatArea[0], insideTavBatArea[1], pos())) {
                Walking.walkPath(toGhost);
                waitIsMovin();
            } else if (pos().getX() >= insideTavBatArea[1].getX() && pos().getY() >= insideTavBatArea[1].getY()) {
                Walking.walkPath(Walking.generateStraightPath(ghostMiddle));
                waitIsMovin();
            } else {
                useTeleport(1);
            }
        } else if (currTask.equals("goblins")) {
            if (inArea(goblinArea[0], goblinArea[1], pos())) {
                return true;
            } else if (pos().distanceTo(lumbyTeleT) > 15) {
                useTeleport(3);
            } else {
                WebWalking.walkTo(goblinMiddle);
                waitIsMovin();
            }

        } else if (currTask.equals("icefiends")) {
            if (pos().distanceTo(iceFiendsMiddle) <= 15) {
                return true;
            } else if (inArea(beforeToIceFiendsArea[0], beforeToIceFiendsArea[1], pos())) {
                Walking.walkPath(Walking.generateStraightPath(iceFiendsMiddle));
                waitIsMovin();
            } else if (inArea(varrockArea[0], varrockArea[1], pos())) {
                WebWalking.walkTo(beforeIceFiends);
                waitIsMovin();
            } else if (inArea(toStrongholdArea[0], toStrongholdArea[1], pos())) {
                WebWalking.walkTo(beforeIceFiends);
                waitIsMovin();
            } else if (inArea(toIceFieldsArea[0], toIceFieldsArea[1], pos())) {
                WebWalking.walkTo(beforeIceFiends);
                waitIsMovin();
            } else {
                useTeleport(1);
            }

        } else if (currTask.equals("kalphite")) {

            if (inArea(rodTeleSpotArea[0], rodTeleSpotArea[1], pos())) {
                RSObject[] gate = Objects.findNearest(10, "Gate");
                if (gate.length > 0) {
                    if (clickOBJ(gate[0], "Open")) {
                        sleep(100, 150);
                        waitIsMovin();
                    }
                }
                WebWalking.walkTo(shantyPassT);
                waitIsMovin();

            } else if (inArea(afterShantyPassArea[0], afterShantyPassArea[1], pos())) {
                //WebWalking.walkTo(tunnelTile);
                Walking.walkPath(toKalphite);
                waitIsMovin();
            } else if (pos().distanceTo(shantyPassT) <= 6) {
                RSObject[] shanty = Objects.find(10, "Shantay pass");

                if (shanty.length > 0) {
                    if (distanceFromTile(shanty[0].getPosition(), 5)) {
                        if (clickOBJ(shanty[0], "Go-through")) {
                            sleep(1000, 1500);
                            waitIsMovin();
                            if (NPCChat.clickContinue(true)) {
                                sleep(100, 150);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(100, 150);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(100, 150);
                                    }
                                }
                            }
                        }
                    }
                    waitIsMovin();
                    sleep(2000, 2300);
                }
            } else if (pos().distanceTo(tunnelTile) <= 5) {
                RSObject[] tunnel = Objects.getAt(tunnelTile);
                RSItem[] ROPE = Inventory.find(rope);
                println("About to go down to kalphite");
                if (tunnel.length > 0 && ROPE.length > 0) {
                    if (tunnel[0].getID() == 3828) {
                        if (clickOBJ(tunnel[0], "Climb-down")) {
                            sleep(2200, 2500);
                        }
                    } else {
                        ROPE[0].click("Use");
                        sleep(100, 150);
                        if (clickOBJ(tunnel[0], "Use rope")) {
                            sleep(100, 150);
                            waitIsMovin();
                            sleep(100, 150);
                            tunnel = Objects.getAt(tunnelTile);
                            if (clickOBJ(tunnel[0], "Climb-down")) {
                                sleep(2200, 2500);
                            }
                        }
                    }
                }

            } else if (pos().distanceTo(inTunnel) <= 5) {
                println("Walking to kalphites");
                Walking.walkPath(toKalphitePart2);
                waitIsMovin();
            } else if (inArea(kalphiteTunnel[0], kalphiteTunnel[1], pos())) {
                if (pos().getY() > 9523) {

                    Walking.walkPath(Walking.generateStraightPath(new RSTile(2505, 9525, 2)));
                    waitIsMovin();
                } else {
                    Walking.walkPath(toKalphitePart2);
                    waitIsMovin();
                }
            } else if (inArea(alkharidArea[0], alkharidArea[1], pos())) {
                WebWalking.walkTo(shantyPassT);
                waitIsMovin();
            } else if (inArea(desertArea[0], desertArea[1], pos())) {
                Walking.walkPath(toKalphite);
                waitIsMovin();
            } else if (pos().distanceTo(kalphiteMiddle) <= 15) {
                return true;
            } else {
                useROD();
            }

        } else if (currTask.equals("minotaurs")) {

            if (pos().distanceTo(strongHoldHole) <= 5) {
                clickRSObjectAt(strongHoldHole, "Climb-down");
            } else if (pos().distanceTo(teleCenterT) <= 4) {
                clickRSObjectAt(teleCenterT, "Use");
                sleep(1000, 1200);
            } else if (pos().distanceTo(afterStrongHoldHole) <= 3) {
                Walking.blindWalkTo(teleCenterT);
                waitIsMovin();
            } else if (inArea(afterPortal4[0], afterPortal4[1], pos())) {
                clickRSObjectAt(strongHoldDoor4, "Open");
                if (chatup()) {
                    chatting();
                }
            } else if (inArea(afterPortal3[0], afterPortal3[1], pos())) {
                clickRSObjectAt(strongHoldDoor3, "Open");
                if (chatup()) {
                    chatting();
                }
            } else if (inArea(afterPortal2[0], afterPortal2[1], pos())) {
                clickRSObjectAt(strongHoldDoor2, "Open");
                if (chatup()) {
                    chatting();
                }
            } else if (pos().distanceTo(strongHoldDoor) <= 5) {
                clickRSObjectAt(strongHoldDoor, "Open");
                if (chatup()) {
                    chatting();
                }
            } else if (pos().distanceTo(new RSTile(1903, 5203, 0)) <= 3) {
                Walking.blindWalkTo(minotaurT);
            } else if (inArea(afterPortal[0], afterPortal[1], pos())) {

                Walking.walkPath(pathToDoor1);
                waitIsMovin();
            } else if (inArea(toStrongholdArea[0], toStrongholdArea[1], pos())
                    || inArea(varrockArea[0], varrockArea[1], pos())) {
                WebWalking.walkTo(strongHoldHole);
                waitIsMovin();
            } else if (pos().distanceTo(minotaurT) <= 15) {
                return true;
            } else if (pos().distanceTo(minotaurT) <= 20) {
                Walking.walkPath(Walking.generateStraightPath(minotaurT));
                waitIsMovin();
            } else {
                useTeleport(0);
            }
        } else if (currTask.equals("monkeys")) {
            RSNPC[] captain = NPCs.findNearest("Seaman Tobias");
            RSNPC[] captain2 = NPCs.findNearest("Seaman Thresnor");
            RSNPC[] captain3 = NPCs.findNearest("Seaman Lorris");

            if (pos().distanceTo(monkeyT) <= 15) {
                return true;
            } else if (pos().distanceTo(portBoatT) <= 5) {
                if (captain.length > 0 && captain[0].click("Pay-fare")) {
                    General.sleep(150, 250);
                    if (NPCChat.clickContinue(true)) {
                        General.sleep(150, 250);
                        if (NPCChat.selectOption("Yes please.", true)) {
                            General.sleep(150, 250);
                            if (NPCChat.clickContinue(true)) {
                                sleep(5000, 6000);
                            }
                        }
                    }
                } else if (captain2.length > 0 && captain2[0].click("Pay-fare")) {
                    General.sleep(150, 250);
                    if (NPCChat.clickContinue(true)) {
                        General.sleep(150, 250);
                        if (NPCChat.selectOption("Yes please.", true)) {
                            General.sleep(150, 250);
                            if (NPCChat.clickContinue(true)) {
                                sleep(5000, 6000);
                            }
                        }
                    }
                } else if (captain3.length > 0 && captain3[0].click("Pay-fare")) {
                    General.sleep(150, 250);
                    if (NPCChat.clickContinue(true)) {
                        General.sleep(150, 250);
                        if (NPCChat.selectOption("Yes please.", true)) {
                            General.sleep(150, 250);
                            if (NPCChat.clickContinue(true)) {
                                sleep(5000, 6000);
                            }
                        }
                    }
                }
            } else if (pos().distanceTo(inBoat) <= 10 && pos().getPlane() == 1) {
                RSObject[] gangplank = Objects.findNearest(10, "Gangplank");

                if (gangplank.length > 0) {
                    if (!gangplank[0].isOnScreen()) {
                        Walking.clickTileMM(gangplank[0].getPosition(), 1);
                        waitIsMovin();
                    } else if (gangplank[0].click("Cross")) {
                        sleep(3000, 3500);
                    }
                }
            } else if (pos().distanceTo(afterPortBoat) <= 10 && pos().getPlane() == 0) {
                WebWalking.walkTo(monkeyT);
                //Walking.walkPath(toMonkey);
                waitIsMovin();
            } else if (inArea(fallyArea[0], fallyArea[1], pos())) {
                WebWalking.walkTo(portBoatT);
                waitIsMovin();
            } else if (inArea(toBoatFromFally[0], toBoatFromFally[1], pos())) {
                WebWalking.walkTo(portBoatT);
                waitIsMovin();
            } else if (inArea(toMonkeyArea[0], toMonkeyArea[1], pos())) {
                WebWalking.walkTo(monkeyT);
                //Walking.walkPath(toMonkey);
                waitIsMovin();
            } else {
                useTeleport(1);
            }
        } else if (currTask.equals("rats")) {
            if (pos().distanceTo(varrockTeleT) <= 10) {
                WebWalking.walkTo(manHoleT);
                waitIsMovin();
            } else if (pos().distanceTo(manHole) <= 5) {
                RSObject[] MANHOLEclose = Objects.getAt(manHoleT); //881
                if (MANHOLEclose.length > 0) {
                    if (MANHOLEclose[0].isOnScreen()) {
                        if (MANHOLEclose[0].click("Open")) {
                            sleep(1000, 1200);
                            MANHOLEclose = Objects.getAt(manHoleT);
                            MANHOLEclose[0].click("Climb-down");
                        } else {
                            MANHOLEclose[0].click("Climb-down");
                        }
                        sleep(1000, 1200);
                    }
                }
            } else if (pos().distanceTo(afterManHoleT) <= 6) {
                Walking.blindWalkTo(ratsMiddle);
                waitIsMovin();

            } else if (pos().distanceTo(ratsMiddle) <= 20) {
                return true;
            } else {
                useTeleport(0);
            }
        } else if (currTask.equals("scorpions")) {
            if (inArea(rodTeleSpotArea[0], rodTeleSpotArea[1], pos())) {
                RSObject[] gate = Objects.findNearest(10, "Gate");
                if (gate.length > 0) {
                    if (clickOBJ(gate[0], "Open")) {
                        sleep(100, 150);
                        waitIsMovin();
                    }
                }
                WebWalking.walkTo(scorpionMiddle); //shantyPassT
                waitIsMovin();

            } else if (inArea(afterShantyPassArea[0], afterShantyPassArea[1], pos())) {
                WebWalking.walkTo(scorpionMiddle);
                waitIsMovin();

            } else if (pos().distanceTo(scorpionMiddle) <= 15) {
                return true;
            } else if (inArea(alkharidArea[0], alkharidArea[1], pos())) {
                WebWalking.walkTo(scorpionMiddle);
                waitIsMovin();
            } else {
                useROD();
            }
        } else if (currTask.equals("skeletons")) {
            if (pos().distanceTo(varrockTeleT) <= 10) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
                //} else if (pos().distanceTo(nearedgeTrapdoorTile) <= 5 && pos().getY() >= nearedgeTrapdoorTile.getPosition().getY()) {
                //} else if (Player.getPosition().distanceTo(nearedgeTrapdoorTile) == 0) {
            } else if (pos().distanceTo(nearedgeTrapdoorTile) <= 5 && pos().getY() >= nearedgeTrapdoorTile.getPosition().getY()) {
                Camera.setCameraRotation(General.random(192, 244));
                RSObject[] edgeTrapdoorOpen = Objects.findNearest(5, 1581);
                RSObject[] edgeTrapdoorClosed = Objects.findNearest(5, 1579);

                if (edgeTrapdoorClosed.length > 0) {
                    if (edgeTrapdoorClosed[0].isOnScreen()) {
                        if (edgeTrapdoorClosed[0].click("Open")) {
                            General.sleep(1500, 2000);
                        }
                    }
                } else if (edgeTrapdoorOpen.length > 0) {
                    if (edgeTrapdoorOpen[0].isOnScreen()) {
                        if (edgeTrapdoorOpen[0].click("Climb-down")) {
                            General.sleep(1500, 2000);
                        }
                    }
                }
                //RSObject[] edgeTrapdoorClosed = Objects.getAt(edgeTrapdoorTile);
                /*if (edgeTrapdoor.length > 0) {
                    if (edgeTrapdoor[0].isOnScreen()) {

                        Point p = Projection.tileToScreen(edgeTrapdoorTile, 0);
                        sleep(200, 300);
                        Mouse.move(p);
                        if (Game.getUptext().equals("Open Trapdoor")) {
                            Mouse.click(p, 1);
                            waitIsMovin();
                            sleep(1000, 1500);
                            Mouse.click(p, 1);
                            waitIsMovin();
                            sleep(1000, 1500);
                        } else {
                            Mouse.click(p, 1);
                            waitIsMovin();
                            sleep(1000, 1500);
                        }

                        
	                       if (edgeTrapdoor[0].click("Open")) {// clickOBJ(edgeTrapdoor[0],
                            // "Open"))
                            // {
                            waitIsMovin();
                            sleep(1000, 1500);
                            edgeTrapdoor = Objects.getAt(edgeTrapdoorTile);
                            // clickOBJ(edgeTrapdoor[0], "Climb-down");
                            edgeTrapdoor[0].click("Climb-down");
                            waitIsMovin();
                            sleep(1000, 1500);
                            Camera.setCameraRotation(General.random(0,
                                    10));
                        }
                    } else {
                        edgeTrapdoor[0].click("Climb-down");
                        waitIsMovin();
                        sleep(1000, 1500);
                        Camera.setCameraRotation(General.random(0, 10));
                    }//end
                    }
                }*/

            } else if (distanceFromTile(edgeLadderTile, 4)) {

                Walking.walkPath(toGate);
                waitIsMovin();

            } else if (distanceFromTile(edgeDungeonGateTile, 4)) {
                RSObject[] edgeGate = Objects.getAt(edgeDungeonGateTile);
                if (edgeGate.length > 0) {
                    Camera.setCameraAngle(General.random(40, 50));
                    if (clickOBJ(edgeGate[0], "Open")) {
                        sleep(100, 150);
                        Camera.setCameraAngle(General.random(100, 120));
                    }
                } else {
                    Walking.walkPath(toSkeletons);
                    waitIsMovin();
                }
            } else if (inArea(afterEdgeGateArea[0], afterEdgeGateArea[1], pos())) {
                Walking.walkPath(toSkeletons);
                waitIsMovin();
            } else if (inArea(toEdgeGateArea[0], toEdgeGateArea[1], pos())) {
                WebWalking.walkTo(edgeDungeonGateTile);
                waitIsMovin();
            } else if (inArea(edgeTrapDoorArea[0], edgeTrapDoorArea[1], pos())) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
            } else if (inArea(toStrongholdArea[0], toStrongholdArea[1], pos())) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
            } else if (inArea(varrockArea[0], varrockArea[1], pos())) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
            } else if (inArea(skeletonArea[0], skeletonArea[1], pos())) {
                return true;
            } else {
                useTeleport(0);
            }
        } else if (currTask.equals("spiders")) {
            if (pos().distanceTo(spidersMiddle) <= 15) {
                return true;
            } else if (inArea(spiderLumby[0], spiderLumby[1], pos())) {
                WebWalking.walkTo(spidersMiddle);
                waitIsMovin();
            } else {
                useTeleport(3);
            }
        } else if (currTask.equals("werewolves")) {

            if (inArea(canafisArea[0], canafisArea[1], pos())) {
                return true;
            } else if (inArea(betweenCanafisEctoArea[0], betweenCanafisEctoArea[1], pos())) {

                Walking.walkPath(toCanafis);
                waitIsMovin();

            } else if (inArea(ectoTeleArea[0], ectoTeleArea[1], pos())) {
                Walking.walkPath(toCanafis);
                waitIsMovin();
            } else {
                useTeleport(5);
            }

        } else if (currTask.equals("wolves")) {
            if (pos().distanceTo(wolfMiddle) <= 15) {
                return true;
            } else if (inArea(toWolfArea[0], toWolfArea[1], pos())) {
                WebWalking.walkTo(wolfMiddle);
                waitIsMovin();
            } else {
                useTeleport(2);
            }
        } else if (currTask.equals("desert_lizards")) {
            if (inArea(rodTeleSpotArea[0], rodTeleSpotArea[1], pos())) {
                RSObject[] gate = Objects.findNearest(10, "Gate");
                if (gate.length > 0) {
                    if (clickOBJ(gate[0], "Open")) {
                        sleep(100, 150);
                        waitIsMovin();
                    }
                }
                WebWalking.walkTo(shantyPassT);
                waitIsMovin();

            }/*
			else if (pos().distanceTo(new RSTile(3307, 3235, 0)) <=4){
				WebWalking.walkTo(shantyPassT);
				waitIsMovin();
			}*/ else if (inArea(afterShantyPassArea[0], afterShantyPassArea[1], pos())) {
                //WebWalking.walkTo(toDesertLizards[toDesertLizards.length]);
                Walking.walkPath(toDesertLizards);
                waitIsMovin();

            } else if (pos().distanceTo(shantyPassT) <= 6) {
                RSObject[] shanty = Objects.find(5, 4031);
                RSInterface shantyWarning = Interfaces.get(565);
                if (shanty.length > 0) {
                    if (shanty[0].click("Go-through")) {
                        General.sleep(2500, 3750);
                        if (shantyWarning != null) {
                            RSInterface[] shantyWarningz = shantyWarning.getChildren();
                            if (shantyWarningz != null) {
                                shantyWarningz[17].click("Yes");
                                sleep(2100, 2500);
                            }
                        }
                    }

                    /*if (distanceFromTile(shanty[0].getPosition(), 5)) {
                        if (clickOBJ(shanty[0], "Go-through")) {
                            General.sleep(2500, 3750);
                            //waitIsMovin();
                            
                            if (shantyWarning != null) {
                                RSInterface[] shantyWarningz = shantyWarning.getChildren();
                                if (shantyWarningz != null) {
                                    shantyWarningz[17].click("Yes");
                                    sleep(2100, 2500);
                                }
                            }*/
 /*if (NPCChat.clickContinue(true)) {
                                sleep(100, 150);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(100, 150);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(100, 150);
                                    }
                                }
                            }*/
                    //}
                    //}
                    /*waitIsMovin();
                    sleep(2000, 2300);*/
                }
            } else if (pos().distanceTo(toDesertLizards[toDesertLizards.length - 1]) <= 15) {
                return true;
            } else {
                useROD();
            }

        } else if (currTask.equals("zombies")) {
            if (pos().distanceTo(varrockTeleT) <= 10) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
            }
            if (pos().distanceTo(nearedgeTrapdoorTile) <= 5 && pos().getY() >= nearedgeTrapdoorTile.getPosition().getY()) {
                RSObject[] edgeTrapdoor = Objects.getAt(edgeTrapdoorTile);
                Camera.setCameraRotation(General.random(192, 244));

                if (distanceFromTile(nearedgeTrapdoorTile, 4)) {
                    if (edgeTrapdoor.length > 0) {
                        if (edgeTrapdoor[0].isOnScreen()) {
                            Point p = Projection.tileToScreen(edgeTrapdoorTile, 0);
                            sleep(200, 300);
                            Mouse.move(p);
                            if (Game.getUptext().equals("Open Trapdoor")) {
                                Mouse.click(p, 1);
                                waitIsMovin();
                                sleep(1000, 1500);
                                Mouse.click(p, 1);
                                waitIsMovin();
                                sleep(1000, 1500);
                            } else {
                                Mouse.click(p, 1);
                                waitIsMovin();
                                sleep(1000, 1500);
                            }

                            /*
								if (edgeTrapdoor[0].click("Open")) {// clickOBJ(edgeTrapdoor[0],
																	// "Open"))
																	// {
									waitIsMovin();
									sleep(1000, 1500);
									edgeTrapdoor = Objects.getAt(edgeTrapdoorTile);
									// clickOBJ(edgeTrapdoor[0], "Climb-down");
									edgeTrapdoor[0].click("Climb-down");
									waitIsMovin();
									sleep(1000, 1500);
									Camera.setCameraRotation(General.random(0,
											10));
								}
							}
							else {
								edgeTrapdoor[0].click("Climb-down");
								waitIsMovin();
								sleep(1000, 1500);
								Camera.setCameraRotation(General.random(0, 10));
							}*/
                        }
                    }
                }

            } else if (distanceFromTile(edgeLadderTile, 4)) {

                Walking.walkPath(toGate);
                waitIsMovin();

            } else if (distanceFromTile(edgeDungeonGateTile, 4)) {

                RSObject[] edgeGate = Objects.getAt(edgeDungeonGateTile);
                if (edgeGate.length > 0) {
                    Camera.setCameraAngle(General.random(40, 50));
                    if (clickOBJ(edgeGate[0], "Open")) {
                        sleep(100, 150);
                        Camera.setCameraAngle(General.random(100, 120));
                    }
                } else {
                    Walking.walkPath(toZombies);
                    waitIsMovin();
                }
            } else if (inArea(afterEdgeGateArea[0], afterEdgeGateArea[1], pos())) {
                Walking.walkPath(toZombies);
                waitIsMovin();
            } else if (inArea(toEdgeGateArea[0], toEdgeGateArea[1], pos())) {
                WebWalking.walkTo(edgeDungeonGateTile);
                waitIsMovin();
            } else if (inArea(edgeTrapDoorArea[0], edgeTrapDoorArea[1], pos())) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
            } else if (inArea(toStrongholdArea[0], toStrongholdArea[1], pos())) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
            } else if (inArea(varrockArea[0], varrockArea[1], pos())) {
                WebWalking.walkTo(nearedgeTrapdoorTile);
                waitIsMovin();
            } else if (inArea(zombieArea[0], zombieArea[1], pos())) {
                return true;
            } else {
                useTeleport(0);
            }
        }

        return false;
    }
    //TODO isAtTask

    public boolean isAtTask() {
        state = "Check for task area";
        if (currTask.equals("banshees")) {
            if (!inArea(awkwardBansheeSq[0], awkwardBansheeSq[1], pos())
                    && inArea(bansheeArea[0], bansheeArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("bats")) {
            if (pos().distanceTo(batMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("bears")) {
            if (pos().distanceTo(bearsMiddle) <= 20) {
                return true;
            }

        } else if (currTask.equals("birds")) { // no idea atm
            if (pos().distanceTo(birdMiddle) <= 10) {
                return true;
            }
        } else if (currTask.equals("cave_bugs")) {
            if (inArea(caveBugArea[0], caveBugArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("cave_crawlers")) {
            if (inArea(caveCrawlerArea[0], caveCrawlerArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("cave_slimes")) {
            if (inArea(caveSlimeArea[0], caveSlimeArea[1], pos()) || inArea(caveSlimeArea2[0], caveSlimeArea2[1], pos())) {
                return true;
            }
        } else if (currTask.equals("cows")) {
            if (pos().distanceTo(cowsMiddle) <= 10) {
                return true;
            }
        } else if (currTask.equals("crawling_hands")) {
            if (pos().distanceTo(crawlingHandsT) <= 15) {
                return true;
            }
        } else if (currTask.equals("dogs")) {
            if (inArea(dogArea1[0], dogArea1[1], pos()) || inArea(dogArea2[0], dogArea2[1], pos())) {
                return true;
            }
        } else if (currTask.equals("dwarves")) {
            if (inArea(dwarfAttackArea[0], dwarfAttackArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("ghosts")) {
            if (pos().distanceTo(ghostMiddle) <= 20) {
                return true;
            }
        } else if (currTask.equals("goblins")) {
            if (inArea(goblinArea[0], goblinArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("icefiends")) {
            if (pos().distanceTo(iceFiendsMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("kalphite")) {
            if (inArea(kalphiteTunnel[0], kalphiteTunnel[1], pos())) {
                return true;
            }
        } else if (currTask.equals("minotaurs")) {
            if (inArea(minotaurArea[0], minotaurArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("monkeys")) {

            if (pos().distanceTo(monkeyT) <= 15) {
                return true;
            }
        } else if (currTask.equals("rats")) {
            if (pos().distanceTo(ratsMiddle) <= 20) {
                return true;
            }
        } else if (currTask.equals("scorpions")) {
            if (pos().distanceTo(scorpionMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("skeletons")) {
            if (inArea(skeletonArea[0], skeletonArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("spiders")) {
            if (pos().distanceTo(spidersMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("werewolves")) {
            if (inArea(canafisArea[0], canafisArea[1], pos())) {
                return true;
            }
        } else if (currTask.equals("wolves")) {
            if (pos().distanceTo(wolfMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("desert_lizards")) {
            if (pos().distanceTo(toDesertLizards[toDesertLizards.length - 1]) <= 25) {
                return true;
            }
        } else if (currTask.equals("zombies")) {
            if (inArea(zombieArea[0], zombieArea[1], pos())) {
                return true;
            }
        }

        return false;
    }

    //TODO useTeleport
    public boolean useTeleport(int option) {
        state = "Teleporting...";
        RSItem[] vtab = Inventory.find(VTAB);
        RSItem[] ftab = Inventory.find(FTAB);
        RSItem[] ctab = Inventory.find(CTAB);
        RSItem[] ltab = Inventory.find(LTAB);
        RSItem[] atab = Inventory.find(ATAB);
        RSItem[] ecto = Inventory.find(ECTO);
        RSItem[] ROD = Inventory.find(rod);
        RSItem[] games = Inventory.find(gamesNecklace);

        int tabOrRune = 1;

        switch (option) {
            case 0:
                if (useTabV) {
                    tabOrRune = 0;
                }
                if (tabOrRune == 0) {
                    if (vtab.length > 0) {
                        if (vtab[0].click("Break")) {
                            sleep(4000, 5000);
                            return true;
                        }
                    }
                } else if (helper.hasReqTab(useTabs, "VARROCK")) {//haveRune(1) && haveRune(2) && haveRune(3)) {

                    if (!GameTab.TABS.MAGIC.isOpen()) {
                        GameTab.open(GameTab.TABS.MAGIC);
                    }
                    sleep(200, 300);
                    Magic.selectSpell("Varrock Teleport");
                    /*RSInterface vtele = Interfaces.get(192, 15);
					if (vtele.click("Cast Varrock Teleport"))
						sleep(3000, 3500);*/
                    sleep(3000, 3500);
                    return true;
                }
                break;
            case 1:
                if (useTabF) {
                    tabOrRune = 0;
                }
                if (tabOrRune == 0) {
                    if (ftab.length > 0) {
                        if (ftab[0].click("Break")) {
                            sleep(4000, 5000);
                            return true;
                        }
                    }
                } else if (helper.hasReqTab(useTabs, "FALADOR")) {//haveRune(1) && haveRune(2) && haveRune(4)) {
                    GameTab.open(org.tribot.api2007.GameTab.TABS.MAGIC);
                    sleep(200, 300);
                    Magic.selectSpell("Falador Teleport");/*
					RSInterface ftele = Interfaces.get(192, 21);
					if (ftele.click("Cast Falador Teleport"))
						sleep(3000, 3500);*/
                    sleep(3000, 3500);
                    return true;
                }
                break;
            case 2:
                if (useTabC) {
                    tabOrRune = 0;
                }
                if (tabOrRune == 0) {
                    if (ctab.length > 0) {
                        if (ctab[0].click("Break")) {
                            sleep(4000, 5000);
                            return true;
                        }
                    }
                } else if (helper.hasReqTab(useTabs, "CAMELOT")) {//haveRune(1) && haveRune(2)) {
                    GameTab.open(org.tribot.api2007.GameTab.TABS.MAGIC);
                    sleep(200, 300);
                    Magic.selectSpell("Camelot Teleport");/*
					RSInterface ctele = Interfaces.get(192, 26);
					if (ctele.click("Cast Camelot Teleport"))
						sleep(3000, 3500);*/
                    sleep(3000, 3500);
                    return true;
                }
                break;
            case 3:
                if (useTabL) {
                    tabOrRune = 0;
                }
                if (tabOrRune == 0) {
                    if (ltab.length > 0) {
                        if (ltab[0].click("Break")) {
                            sleep(4000, 5000);
                            return true;
                        }
                    }
                } else if (helper.hasReqTab(useTabs, "LUMBRIDGE")) {//haveRune(1) && haveRune(2) && haveRune(0)) {
                    GameTab.open(org.tribot.api2007.GameTab.TABS.MAGIC);
                    sleep(200, 300);
                    Magic.selectSpell("Lumbridge Teleport");/*
					RSInterface ltele = Interfaces.get(192, 18);
					if (ltele.click("Cast Lumbridge Teleport"))
						sleep(3000, 3500);*/
                    sleep(3000, 3500);
                    return true;
                }
                break;
            case 4:
                if (useTabA) {
                    tabOrRune = 0;
                }
                if (tabOrRune == 0) {
                    if (atab.length > 0) {
                        if (atab[0].click("Break")) {
                            sleep(4000, 5000);
                            return true;
                        }
                    }
                } else if (helper.hasReqTab(useTabs, "ARDOUGNE")) {//haveRune(1) && haveRune(4)) {
                    GameTab.open(org.tribot.api2007.GameTab.TABS.MAGIC);
                    sleep(200, 300);
                    Magic.selectSpell("Ardougne Teleport");/*
					RSInterface atele = Interfaces.get(192, 32);
					if (atele.click("Cast Ardougne Teleport"))
						sleep(3000, 3500);*/
                    sleep(3000, 3500);
                    return true;
                }
                break;
            case 5:
                if (ecto.length > 0) {
                    if (ecto[0].click("Empty")) {
                        sleep(7000, 8000);
                        waitIsMovin();
                        return true;
                    }
                }
                break;
            case 6:
                if (ROD.length > 0) {
                    useROD();
                    return true;
                }
                break;
            case 7:
                if (games.length > 0) {
                    useGameNeck();
                    return true;
                }
                break;
        }
        return false;
    }

    //TODO checkLootInArea
    public boolean checkLootInArea(RSGroundItem loot) {
        RSTile loots = loot.getPosition();

        if (currTask.equals("banshees")) {
            if (!inArea(awkwardBansheeSq[0], awkwardBansheeSq[1], loots)
                    && inArea(bansheeArea[0], bansheeArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("bats")) {
            if (loots.distanceTo(batMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("bears")) {
            if (loots.distanceTo(bearsMiddle) <= 20) {
                return true;
            }

        } else if (currTask.equals("birds")) {
            if (loots.distanceTo(birdMiddle) <= 10) {
                return true;
            }
        } else if (currTask.equals("cave_bugs")) {
            if (inArea(caveBugArea[0], caveBugArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("cave_crawlers")) {
            if (inArea(caveCrawlerArea[0], caveCrawlerArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("cave_slime")) {
            if (inArea(caveSlimeArea[0], caveSlimeArea[1], loots)
                    || inArea(caveSlimeArea2[0], caveSlimeArea2[1], loots)) {
                return true;
            }
        } else if (currTask.equals("cows")) {
            if (loots.distanceTo(cowsMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("crawling_hands")) {
            if (loots.distanceTo(crawlingHandsT) <= 15) {
                return true;
            }
        } else if (currTask.equals("dogs")) {
            if (inArea(dogArea1[0], dogArea1[1], loots)
                    || inArea(dogArea2[0], dogArea2[1], loots)) {
                return true;
            }
        } else if (currTask.equals("dwarves")) {
            if (!inArea(trickyDwarf[0], trickyDwarf[1], loots)
                    && inArea(dwarfAttackArea[0], dwarfAttackArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("ghosts")) {
            if (loots.distanceTo(ghostMiddle) <= 20) {
                return true;
            }
        } else if (currTask.equals("goblins")) {
            if (inArea(goblinArea[0], goblinArea[1], loots)
                    && !inArea(goblinHut[0], goblinHut[1], loots)) {
                return true;
            }
        } else if (currTask.equals("icefiends")) {
            if (loots.distanceTo(iceFiendsMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("kalphite")) {
            if (loots.distanceTo(kalphiteMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("minotaurs")) {
            if (inArea(minotaurArea[0], minotaurArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("monkeys")) {

            if (loots.distanceTo(monkeyT) <= 15) {
                return true;
            }
        } else if (currTask.equals("rats")) {
            if (loots.distanceTo(ratsMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("scorpions")) {
            if (loots.distanceTo(scorpionMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("skeletons")) {
            if (inArea(skeletonArea[0], skeletonArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("spiders")) {
            if (loots.distanceTo(spidersMiddle) <= 15) {
                return true;
            }
        } else if (currTask.equals("werewolves")) {
            if (inArea(canafisArea[0], canafisArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("wolves")) {
            if (inArea(wolfArea[0], wolfArea[1], loots)) {
                return true;
            }
        } else if (currTask.equals("desert_lizards")) {
            if (loots.distanceTo(toDesertLizards[toDesertLizards.length - 1]) <= 25) {
                return true;
            }
        } else if (currTask.equals("zombies")) {
            if (inArea(zombieArea[0], zombieArea[1], loots)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clanMessageReceived(String arg0, String arg1) {

    }

    @Override
    public void playerMessageReceived(String arg0, String arg1) {

    }

    @Override
    public void serverMessageReceived(String message) {
        // TODO serverMessaging
        int currLeftToKill;

        if (message.startsWith("You're assigned to kill")) {
            String[] arr = message.split(" ");
            if (arr.length == 10) {
                currLeftToKill = Integer.parseInt(arr[6]);
                currTask = arr[4].substring(0, arr[4].length() - 1);

                if (numLeftToKill > currLeftToKill) {

                    lastServerMessage = message;

                }
                numLeftToKill = currLeftToKill;
            } else { // cave crawlers, cave slimes, desert lizards
                currLeftToKill = Integer.parseInt(arr[7]);
                currTask = arr[4] + "_" + arr[5].substring(0, arr[5].length() - 1);

                if (numLeftToKill > currLeftToKill) {

                    lastServerMessage = message;

                }
                numLeftToKill = currLeftToKill;
            }
        } else if (message.contains("return to a Slayer master")) {//.endsWith("return to a Slayer master.")){
            currTask = "None";
            numLeftToKill = 0;
        } else if (message.equals("You need something new to hunt.")) {
            currTask = "None";
            numLeftToKill = 0;
        } else if (message.endsWith("You have been poisoned!")) {
            useAnti();
            sleep(1000, 1200);
        }

    }

    //TODO waitTillDead
    public void waitTillDead(RSNPC npc) {
        boolean hi = false;
        for (int i = 0; i < 800; i++, sleep(30, 40)) {
            if (getHp() < 40) {
                eatFood();
            }
            ///isValid
            if (Combat.isUnderAttack() && Combat.getAttackingEntities().length > 0 && currTask.equals("desert_lizards")) {
                if (Player.getRSPlayer().getInteractingCharacter().getHealth() <= 3 && Player.getRSPlayer().getInteractingCharacter().isInteractingWithMe()) {
                    RSItem[] cooler = Inventory.find(COOLER);
                    if (cooler.length > 0) {
                        if (cooler[0].click("Use")) {
                            sleep(300, 400);
                            npc.click("Use Ice cooler");
                            sleep(2000, 2200);
                            hi = true;
                            break;
                        }
                    }
                }
            }
            if (npc == null || Player.getRSPlayer().getInteractingCharacter() == null || !npc.isInteractingWithMe()) {// || npc[0].getCombatCycle() > 0)) {

                println("npc is dead");
                hi = true;

                break;
            }
        }
        if (!hi) {
            println("timeout");
        }
    }

    //TODO openDoor
    public boolean openDoor(Positionable doorTile) {
        println("Opening door");
        RSObject door = Doors.getDoorAt(doorTile);
        if (door != null) {
            if (door.isOnScreen()) {
                if (Doors.handleDoor(door, true)) {
                    waitIsMovin();
                    sleep(1000, 1200);
                    return true;
                }
            } else {
                Walking.walkTo(doorTile);
                Camera.turnToTile(doorTile);
                waitIsMovin();
                sleep(200, 300);
                return false;
            }
        }
        return true;
    }

    public int isNotFighting(RSNPC[] npc) {
        for (int i = 0; i < 57; i++, sleep(30, 40)) {
            if (npc.length > 0 && !npc[0].isInteractingWithMe() && !inCombat() && npc[0].getCombatCycle() <= 0) {
                return 0;
            } else if (npc.length > 1 && !npc[1].isInteractingWithMe() && !inCombat() && npc[1].getCombatCycle() <= 0) {
                return 1;
            }
        }
        return -1;
    }

    public void useAnti() {
        RSItem[] antiP = Inventory.find(antiPoison);
        if (antiP.length > 0) {
            if (antiP[0].click("Drink")) {
                println("Just drank a dose of antipoison");
                sleep(500, 1000);
            }
        }

    }

    public boolean isFull() {
        // Mouse.setSpeed(General.random(150, 160));
        sleep(5, 7);
        return Inventory.isFull();
    }

    public void cameraDrag() {
        Camera.setCameraRotation(General.random(88, 92));
    }

    public void cameraPortal() {
        Camera.setCameraRotation(General.random(178, 182));
    }

    public void cameraBank() {
        Camera.setCameraRotation(General.random(0, 2));
    }

    public boolean inCombat() {
        sleep(5, 7);
        return Player.getRSPlayer().isInCombat();
    }

    public int getHp() {
        //double x = Skills.getCurrentLevel(SKILLS.HITPOINTS);
        //double y = Skills.getActualLevel(SKILLS.HITPOINTS);
        //return (int) ((x / y) * 100);
        return Combat.getHPRatio();
    }

    public RSTile pos() {
        return Player.getPosition();
    }

    public boolean inArea(RSTile nw, RSTile se, RSTile pos) {

        int posX = pos.getPosition().getX();
        int posY = pos.getPosition().getY();
        int posP = pos.getPosition().getPlane();
        int nwX = nw.getPosition().getX();
        int nwY = nw.getPosition().getY();
        int seX = se.getPosition().getX();
        int seY = se.getPosition().getY();
        int nwP = nw.getPosition().getPlane();
        int seP = se.getPosition().getPlane();

        if (posX >= nwX && posX <= seX && posY >= seY && posY <= nwY
                && posP == nwP && posP == seP) {
            return true;
        } else {
            return false;
        }
    }

    public void checkXP() {

        GameTab.open(org.tribot.api2007.GameTab.TABS.STATS);
        sleep(80, 100);
        Mouse.moveBox(557, 307, 600, 321);
        sleep(1700, 2200);
        GameTab.open(org.tribot.api2007.GameTab.TABS.INVENTORY);
        sleep(80, 100);

    }

    public void checkFriends() {

        GameTab.open(org.tribot.api2007.GameTab.TABS.FRIENDS);
        sleep(80, 100);
        Mouse.moveBox(616, 240, 664, 261);
        sleep(1700, 2200);
        GameTab.open(org.tribot.api2007.GameTab.TABS.INVENTORY);
        sleep(80, 100);
    }

    private void ANTIBAN() {

        int x = General.random(1, 2);
        if (x == 1) {
            checkXP();
        } else {
            checkFriends();
        }
        antiban = System.currentTimeMillis();
    }

    private boolean clickNPC(RSNPC npc, String option) {
        //Mouse.setSpeed(General.random(100,120));
        RSTile loc = null;
        if (npc != null && npc.isOnScreen()) {
            loc = npc.getPosition();
            Mouse.move(Projection.tileToScreen(loc, 10));
            //println("step1 ");
            if (Game.isUptext(option)) {
                Mouse.click(1);
                //println("hello");
                return true;
            } else {
                Mouse.click(3);
                if (ChooseOption.isOpen()) {
                    if (ChooseOption.select(option)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean clickOBJ(RSObject npc, String option) {
        Camera.turnToTile(npc.getPosition());
        // Mouse.setSpeed(170);
        if (npc.isOnScreen()) {
            Point[] points = npc.getModel().getAllVisiblePoints();

            if (points.length > 0) {
                Mouse.move(points[points.length / 2]);
                // Mouse.move(new Point(points[0].x, points[0].y));
                if (Game.isUptext(option)) {
                    Mouse.click(1);
                    return true;
                } else {
                    Mouse.click(3);
                    if (ChooseOption.isOpen()) {
                        if (ChooseOption.select(option)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkActions(RSObject object, String action) {
        if (object != null) {
            for (String s : object.getDefinition().getActions()) {
                return s.contains(action);
            }
        }
        return false;
    }

    public void clickObject(int distance, String objects, String option) {
        RSObject[] object = Objects.findNearest(100, objects);
        if (object != null) {
            int x = (int) object[0].getModel().getEnclosedArea().getBounds()
                    .getCenterX();
            int y = (int) object[0].getModel().getEnclosedArea().getBounds()
                    .getCenterY();
            Point p = new Point(x + General.random(0, 16), y
                    + General.random(0, 4));
            if (object[0].getPosition().isOnScreen()) {
                Mouse.move(p);
                if (Game.getUptext().contains(option)
                        && (checkActions(object[0], option))) {
                    Mouse.click(1);
                }
                if (!Game.getUptext().contains(option)) {
                    Mouse.click(3);
                    if (ChooseOption.isOpen()
                            && ChooseOption.isOptionValid(option)) {
                        ChooseOption.select(option);
                    }
                    if (ChooseOption.isOpen()
                            && !ChooseOption.isOptionValid(option)) {
                        Camera.turnToTile(object[0].getPosition());
                    }
                }
            } else {
                if (Player.getPosition().distanceTo(object[0].getPosition()) > 4) {
                    WebWalking.walkTo(object[0].getPosition());
                }
                if (!object[0].getPosition().isOnScreen()) {
                    Camera.turnToTile(object[0].getPosition());
                }
                while (Player.isMoving()) {
                    sleep(50, 80);
                }
            }
        }
    }

    public void drawModel(RSModel model, Graphics g, boolean fill) {
        if (model.getAllVisiblePoints().length != 0) {
            if (fill) {
                // fill triangles
                for (Polygon p : model.getTriangles()) {
                    g.fillPolygon(p);
                }
            } else {
                // draw triangles
                for (Polygon p : model.getTriangles()) {
                    g.drawPolygon(p);
                }
            }
        }
    }

    public void drawPath(RSTile[] path, Graphics g, boolean minimap) {
        //Point[] points = new Point[path.length];
        if (minimap) {
            // Drawing on the other whatever
            for (int i = 0; i < path.length - 1; i++) {
                Point tilePoint = Projection.tileToMinimap(path[i]);
                if (Projection.isInMinimap(tilePoint)) {
                    if (i < path.length - 1) {
                        Point nextPoint = Projection.tileToMinimap(path[i + 1]);
                        if (Projection.isInMinimap(nextPoint)) {
                            g.drawLine(tilePoint.x, tilePoint.y, nextPoint.x,
                                    nextPoint.y);
                        }
                    }
                    g.fillOval(tilePoint.x, tilePoint.y, 3, 3);
                }
            }
        } else {
            for (int i = 0; i < path.length - 1; i++) {
                if (path[i].getPosition().isOnScreen()) {
                    if (i > 0) { // could just start index
                        Point currentTile = Projection.tileToScreen(path[i], 0);
                        Point lastTile = Projection
                                .tileToScreen(path[i - 1], 0);
                        g.drawLine(currentTile.x, currentTile.y, lastTile.x,
                                lastTile.y);
                    }
                    //g.setColor("#FF0000");
                    drawTile(path[i], g, false);
                }
            }
        }
    }

    public void drawTile(RSTile tile, Graphics g, boolean fill) {
        if (tile.getPosition().isOnScreen()) {
            if (fill) {
                g.fillPolygon(Projection.getTileBoundsPoly(tile, 0));
            } else {
                g.drawPolygon(Projection.getTileBoundsPoly(tile, 0));
            }
        }
    }

    public boolean isMovin() {

        return Player.isMoving();
    }

    public void waitIsMovin() {

        for (int i = 0; i < 57; i++, sleep(30, 40)) {
            if (!isMovin()) {
                break;
            }
        }
    }

    public boolean gotFood() {

        return Inventory.getCount(foodID) > 0;
    }

    public int lootCount(int ID) {

        return Inventory.getCount(ID);
    }

    // improved waiting for loot
    private void waitForInv(int lootID) {
        int k = 0;

        while (k < 200 && Player.isMoving()) {
            sleep(80);
            k++;
        }
    }

    public boolean clickModel(RSModel model, String option, boolean rightClick) {

        Point[] points = model.getAllVisiblePoints();
        int length = points.length;
        if (length != 0) {
            Point p = points[//General.random(0, length - 1)];
                    length / 2];
            Mouse.move(p);
            {
                String top = org.tribot.api2007.Game.getUptext();
                if (top.contains(option)) {
                    Mouse.click(p, 0);
                    return true;
                } else if (rightClick) {
                    Mouse.click(3);
                    Timer t = new Timer(500);
                    while (t.isRunning() && !ChooseOption.isOpen()) {
                        sleep(50, 100);
                    }
                    if (!ChooseOption.isOpen()) {
                        return false;
                    }
                    if (ChooseOption.select(option)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Make sure an option is valid *
     */
    private boolean selectOption(String option) {

        Point pos = Mouse.getPos();
        for (int i = 0; i < 1; i++) {
            Mouse.click(pos, 3);
            sleep(100);
            if (ChooseOption.isOptionValid(option) && ChooseOption.select(option)) {
                return true;
            } else {
                Mouse.move((int) Mouse.getPos().getX() - General.random(10, 20), (int) Mouse.getPos().getY() - General.random(50, 60));
            }
        }
        return false;
    }

    /**
     * Check color at image coordinate *
     */
    private boolean enterAmount() {

        BufferedImage asterisk = Screen.getGameImage().getSubimage(259, 429, 1, 1);
        int[] r = new int[]{0, 6};
        int[] g = new int[]{0, 6};
        int[] b = new int[]{122, 134};

        Color rgb = new Color(asterisk.getRGB(0, 0));
        int red = rgb.getRed();
        int green = rgb.getGreen();
        int blue = rgb.getBlue();

        //Check for colors between these values
        if (red >= r[0] && red <= r[1] && green >= g[0] && green <= g[1] && blue >= b[0] && blue <= b[1]) {
            return true;
        }

        return false;
    }

    /**
     * Open bank *
     */
    private boolean bankIsOpen() {

        //A method to check if the bank is open
        return Banking.isBankScreenOpen();
    }

    public boolean distanceFromTile(RSTile tile, int distance) {
        return pos().distanceTo(tile) <= distance;
    }

    public void runAndTurnCamera(RSNPC mob) {
        Walking.setWalkingTimeout(500L);
        Walking.clickTileMM(mob.getPosition(), 1);
        Camera.turnToTile(mob.getPosition());
    }

    public boolean mobNotOnScreen(RSNPC mob) {
        return !mob.isOnScreen();
    }

    public void emergTele() {
        RSItem[] vtab = Inventory.find(VTAB);

        if (!useTabs) {
            println("going to varrock using runes");
            useTeleport(0);
        } else if (vtab.length > 0) {
            vtab[0].click("Break");
        } else {
            //KillLoopException.a("No more Varracok teletabs, you must have vteletabs");
            println("");
        }
        sleep(4000, 5000);
    }

    private void chatting() {
        String ops[] = {
            "Don't give him my password.", "Don't tell them anything and inform Jagex through the game website.", "Nobody", "Game Inbox on the RuneScape website.", "The birthday of a famous person or event.", "Talk to any banker in RuneScape.", "Nowhere.", "Recovering your account if it is stolen.", "Use the 'Recover a Lost Password' section on the RuneScape website.", "Only on the RuneScape website.",
            "Memorable", "No", "Politely tell them no and then use the 'Report Abuse' button.", "No, it might steal my password.", "Don't tell them anything and click the 'Report Abuse' button.", "Don't give them the information and send an 'Abuse report'.", "Every couple of months", "Virus scan my computer then change my password and recoveries.", "No.", "To help me recover my password if I forget it or it is stolen."
        };
        if (NPCChat.getClickContinueInterface() != null) {
            NPCChat.clickContinue(true);
        }
        sleep(1000, 1200);
        if (NPCChat.getOptions() != null) {
            String ans[] = NPCChat.getOptions();
            if (ans.length > 0) {
                for (int a = 0; a < ans.length; a++) {
                    for (int i = 0; i < ops.length; i++) {
                        if (ans[a].toString().equals(ops[i])) {
                            NPCChat.selectOption(ops[i], true);
                        }
                    }

                }

            }
        }
        sleep(1000, 1200);
        if (NPCChat.getClickContinueInterface() != null) {
            NPCChat.clickContinue(true);
        }
        sleep(5000, 6000);
    }

    private boolean chatup() {
        println("chat is up to check if we are a bot");
        return Interfaces.get(231, 1) != null;
        //return Interfaces.get(242, 1) != null || Interfaces.get(244, 1) != null || Interfaces.get(243, 1) != null || Interfaces.get(230, 0) != null || Interfaces.get(228, 0) != null;
    }

    private boolean modsnear() {
        RSPlayer allPlayers[] = Players.getAll();
        if (allPlayers.length > 0) {
            RSPlayer arsplayer[];
            int k = (arsplayer = allPlayers).length;
            for (int j = 0; j < k; j++) {
                RSPlayer i = arsplayer[j];
                if (i != null && i.getName() != null && i.getName().contains("Mod ")) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }
    boolean specon;

    private int specialPercent() {
        if (specon) {
            return Game.getSettingsArray()[300] / 10;
        } else {
            return 0;
        }
    }

    private boolean specialSelected() {
        return Game.getSettingsArray()[301] == 1;
    }

    private void selectspecial() {
        GameTab.open(org.tribot.api2007.GameTab.TABS.COMBAT);
        if (GameTab.getOpen() == org.tribot.api2007.GameTab.TABS.COMBAT) {
            Mouse.clickBox(573, 412, 711, 432, 1);
            for (int f = 0; f < 2000 && !specialSelected() && specialPercent() == 100; f++) {
                sleep(1L);
            }

            for (int f = 0; f < 2000 && specialSelected() && specialPercent() == 100; f++) {
                sleep(1L);
            }

        }
    }

    /**
     * ** brian methods
     *
     ***
     * @param id
     * @return
     */
    //TODO getClosesetInactiveNPC
    public RSNPC getClosestInactiveNPC(String id) {
        RSNPC[] npcs = NPCs.findNearest(id);
        if (currTask.equals("werewolves")) {
            npcs = NPCs.findNearest(wereWolves);
        }
        if (npcs.length > 0) {
            for (int x = 0; x < npcs.length; x++) {
                if (npcs[x].isInteractingWithMe() && npcs[x].isValid()) { // not sure if this fixes anything
                    println("already in combat, we'll just choose the monster interacting with me");
                    return npcs[x];
                } else if (currTask.equals("werewolves")) {
                    if (inArea(canafisArea[0], canafisArea[1], npcs[x].getPosition())) {
                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("bats")) {
                    if (npcs[x].getPosition().distanceTo(batMiddle) <= 15) {

                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("wolves")) {
                    if (inArea(wolfArea[0], wolfArea[1], npcs[x].getPosition())) {

                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("dogs")) {
                    if (inArea(dogArea1[0], dogArea1[1], npcs[x].getPosition()) || inArea(dogArea2[0],
                            dogArea2[1], npcs[x].getPosition())) {

                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("goblins")) {
                    RSObject[] gDoor = Objects.getAt(goblinDoor);
                    if (inArea(goblinHut[0], goblinHut[1], npcs[x].getPosition()) && gDoor.length > 0) {
                        if (gDoor[0].isOnScreen()) {
                            clickRSObjectAt(goblinDoor, "Open");
                            sleep(1000, 1200);
                        }
                    }
                    if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                        return npcs[x];
                    }

                } else if (currTask.equals("birds")) {
                    RSObject[] gDoor1 = Objects.getAt(birdGateS);
                    RSObject[] gDoor2 = Objects.getAt(birdGateN);
                    if (gDoor1.length > 0 && pos().distanceTo(gDoor1[0].getPosition()) <= 3) {
                        if (gDoor1[0].isOnScreen()) {
                            if (pos().distanceTo(birdGateS) <= 3) {
                                clickRSObjectAt(birdGateS, "Open");
                                sleep(1000, 1200);
                            }
                        }
                    } else if (gDoor2.length > 0 && pos().distanceTo(gDoor2[0].getPosition()) <= 3) {
                        if (gDoor2[0].isOnScreen()) {
                            if (pos().distanceTo(birdGateN) <= 3) {
                                clickRSObjectAt(birdGateN, "Open");
                                sleep(1000, 1200);
                            }
                        }
                    }

                    if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                        return npcs[x];
                    }

                } else if (currTask.equals("cows")) { //testing cows
                    /*RSObject[] gDoor = Objects.getAt(cowGate);
                    if (gDoor.length > 0 && pos().distanceTo(gDoor[0].getPosition()) <= 3) {
                        if (gDoor[0].isOnScreen() && gDoor[0].getID() != 320) {
                            if (pos().distanceTo(cowGate) <= 3) {
                                clickRSObjectAt(cowGate, "Open");
                                sleep(1000, 1200);
                            }
                        }
                    }*/

                    RSObject[] cowgate = Objects.find(3, 7158);
                    if (cowgate.length > 0) {
                        if (cowgate[0].isOnScreen()) {
                            Camera.setCameraRotation(0);
                            Camera.setCameraAngle(General.random(40, 60));
                            if (cowgate[0].click("Open")) {
                                sleep(500, 1000);
                                Camera.setCameraAngle(General.random(100, 110));
                            }
                        }
                    }
                    if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                        return npcs[x];
                    }
                } else if (currTask.equals("dwarves")) {
                    if (!inArea(trickyDwarf[0], trickyDwarf[1], npcs[x].getPosition()) && inArea(dwarfAttackArea[0],
                            dwarfAttackArea[1], npcs[x].getPosition())
                            && !trickyDwarf1Area.contains(npcs[x].getPosition())
                            && !trickyDwarf2Area.contains(npcs[x].getPosition())) {
                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("skeletons")) {
                    if (inArea(skeletonArea[0], skeletonArea[1], npcs[x].getPosition())) {
                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("cave_slime")) {
                    if (inArea(caveSlimeArea[0], caveSlimeArea[1], npcs[x].getPosition()) || inArea(caveSlimeArea2[0], caveSlimeArea2[1],
                            npcs[x].getPosition())) {
                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("cave_bugs")) {
                    if (inArea(caveBugArea[0], caveBugArea[1], npcs[x].getPosition())) {
                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (currTask.equals("ghosts")) {
                    if (npcs[x].getPosition().distanceTo(ghostMiddle) <= 15) {
                        if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                            return npcs[x];
                        }
                    }
                } else if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                    return npcs[x];
                }
            }
        }
        return null;
    }

    public RSNPC attackInactiveMonster(String id) {
        RSNPC monster = getClosestInactiveNPC(id);
        if (monster != null) {
            if (monster.isOnScreen()) {
                //if(DynamicClicking.clickRSModel(monster.getModel(), "Attack")){
                //if(clickModel(monster.getModel(), "Attack", false)){
                if (monster.click("Attack")) {
                    waitIsMovin();
                    waitTillDead(monster);
                    //LOOT();
                    sleep(1000, 1200);
                }
            } else if (monster.getPosition().distanceTo(Player.getPosition()) <= 6) {
                Camera.turnToTile(monster.getPosition());
                sleep(2000, 2500);
            } else {

                Walking.blindWalkTo(monster.getPosition());
                sleep(2000, 2500);
            }
        }
        return monster;
    }

    public boolean needsToEat(int percToEat) {
        double currLevel = Skills.getCurrentLevel(SKILLS.HITPOINTS);
        int hitpointsLevel = Skills.getActualLevel(SKILLS.HITPOINTS);
        double decimal = currLevel / (double) (hitpointsLevel);
        int percent = (int) (decimal * 100);
        if (percent <= percToEat) {
            return true;
        }
        return false;
    }

    public void eatFood(int[] foodIds) {
        RSItem[] food = Inventory.find(foodIds);
        if (food.length > 0) {
            food[0].click("Eat");
            sleep(400, 800);
        }
    }

    public void clickRSObject(int distance, int id, String action) {
        boolean successful = false;
        while (!successful) {
            RSObject[] objects = Objects.find(distance, id);
            if (objects.length > 0) {
                int r = Projection.angleToTile(objects[0].getAnimablePosition());
                Camera.setCameraRotation(r + General.random(0, 90));
                sleep(600, 800);
                successful = DynamicClicking.clickRSObject(objects[0], action);
                waitIsMovin();
                sleep(1800, 2400);

            }
        }
    }

    public void clickRSObjectAt(RSTile tile, String action) {
        boolean successful = false;
        while (!successful) {
            RSObject[] objects = Objects.getAt(tile);
            if (objects.length > 0) {
                int r = Projection.angleToTile(objects[0].getAnimablePosition());
                Camera.setCameraRotation(r + General.random(0, 80));
                sleep(600, 800);
                successful = DynamicClicking.clickRSObject(objects[0], action);
                sleep(1800, 2400);
            }
        }
    }

    public boolean checkForHelm(int option) {

        //final int helmz[] = {SPINY,EARMUFFS}; 
        //RSItem[] helm = Inventory.getAll(); //get equipped
        RSItem[] helm = Equipment.getItems();

        //RSItem[] helm = Interfaces.get(387, 28).getItems();
        //RSItem[] helm = Interfaces.get(149, 0).getItems();
        int helmID = 0;
        if (PureMode && option == 0) {
            return true;
        } else if (option == 0) // spiny
        {
            helmID = SPINY;
        } else if (option == 1) //earmuffs
        {
            helmID = EARMUFFS;
        }
        for (RSItem helm1 : helm) {
            // looping threw items equiped
            if (helm1.getID() == helmID) {
                return true;
            }
        }
        return false;
    }

    public boolean equipHelm(int option) {

        RSItem[] helm0 = Inventory.find(SPINY);
        RSItem[] helm1 = Inventory.find(EARMUFFS);
        if (PureMode && option == 0) {
            return true;
        } else if (option == 0) { // spiny
            if (helm0.length > 0) {
                if (helm0[0].click("Wear")) {
                    return true;
                }
            }
        } else if (option == 1) { //earmuffs
            if (helm1.length > 0) {
                if (helm1[0].click("Wear")) {
                    return true;
                }
            }
        }

        return false;
    }

    //START: Code generated using Enfilade's Easel
    private final Color color1 = new Color(255, 255, 255);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(0, 204, 0);
    private final Color color4 = new Color(0, 0, 255);
    private final Color color5 = new Color(102, 102, 0);
    private final Color color6 = new Color(255, 0, 0);
    private final Color color7 = new Color(0, 0, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);
    private final BasicStroke stroke2 = new BasicStroke(3);

    private final Font font1 = new Font("Arial", 3, 24);
    private final Font font2 = new Font("Arial", 0, 14);
    public final int[] XP = {Skills.getXP(SKILLS.ATTACK), Skills.getXP(SKILLS.STRENGTH),
        Skills.getXP(SKILLS.DEFENCE), Skills.getXP(SKILLS.RANGED),
        Skills.getXP(SKILLS.PRAYER), Skills.getXP(SKILLS.MAGIC),
        Skills.getXP(SKILLS.HITPOINTS), Skills.getXP(SKILLS.SLAYER)};
    public final SKILLS[] Names = {SKILLS.ATTACK, SKILLS.STRENGTH,
        SKILLS.DEFENCE, SKILLS.RANGED,
        SKILLS.PRAYER, SKILLS.MAGIC,
        SKILLS.HITPOINTS, SKILLS.SLAYER};
    public int[] startLvs = {Skills.getActualLevel(SKILLS.ATTACK), Skills.getActualLevel(SKILLS.STRENGTH),
        Skills.getActualLevel(SKILLS.DEFENCE), Skills.getActualLevel(SKILLS.RANGED),
        Skills.getActualLevel(SKILLS.PRAYER), Skills.getActualLevel(SKILLS.MAGIC),
        Skills.getActualLevel(SKILLS.HITPOINTS), Skills.getActualLevel(SKILLS.SLAYER),};

    //TODO paint
    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(color1);
        g.fillRect(356, 370, 150, 120);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.setFont(font2);
        g.setColor(color4);
        g.drawString("State: ", 366, 425);
        g.drawString("Version: ", 366, 445);
        g.drawString("Time running: ", 366, 465);
        g.setColor(color7);

        timeRan = System.currentTimeMillis() - start_time;
        g.drawString("" + version, 425, 445);
        g.drawString("" + Timing.msToString(timeRan), 366, 485);
        g.setFont(font2);
        g.setColor(color7);

        g.drawString("" + state, 408, 425);

        g.drawString("NumLeft: " + numLeftToKill, 366, 405);
        g.drawString("CurrTask: " + currTask, 366, 385);

        g.setFont(new Font("Arial", 0, 11));

        long timeRan = getRunningTime();

        double secondsRan = (int) (timeRan / 1000);
        double hoursRan = secondsRan / 3600;
        int x = 0;
        for (int i = 0; i < XP.length; i++) {
            long xp_hr = Math.round(((Skills
                    .getXP(Names[i]) - XP[i])) / hoursRan);

            if (XP[i] != Skills.getXP(Names[i])) {
                g.setColor(new Color(0, 0, 0));
                g.fillRect(2, 326 - x, 515, 12);

                g.setColor(new Color(0, 255, 0, 255));
                g.drawString(Names[i]
                        + ": "
                        + NumberFormat.getNumberInstance(Locale.US).format(xp_hr)
                        + " Xp/h", 5, (337 - x));
                int x1 = 135, y1 = 327 - x;
                int CUR_LVL = Skills.getActualLevel(Names[i]);
                int NXT_LVL = (CUR_LVL + 1);
                int Percentage = Skills.getPercentToLevel(Names[i], NXT_LVL);

                double xp_to_lv = Skills.getXPToLevel(Names[i], NXT_LVL);
                long ttl = Math.round(xp_to_lv / xp_hr * 60);

                g.drawString("Current level: " + CUR_LVL + "   Levels gained: " + (CUR_LVL - startLvs[i]) + "  Ttl: " + ttl + " mins", 240, y1 + 10);

                g.setColor(new Color(255, 0, 0, 255));
                g.fillRect(x1, y1, 100, 10);
                g.setColor(new Color(0, 255, 0, 255));
                g.fillRect(x1, y1, Percentage, 10);

                g.setColor(new Color(0, 0, 0));
                g.drawString(Percentage + "% to " + NXT_LVL, x1 + 25, y1 + 9);

                x += 15;
            }
        }
    }

    @Override
    public void onPaint(Graphics arg0) {
        if (showPaint) {
            onRepaint(arg0);
        }
    }
    // ALL PAINT STUFF

    int startXp = Skills.getXP(SKILLS.HITPOINTS) + Skills.getXP(SKILLS.STRENGTH)
            + Skills.getXP(SKILLS.ATTACK) + Skills.getXP(SKILLS.DEFENCE) + Skills.getXP(SKILLS.RANGED);
    int startXpSlayer = Skills.getXP(SKILLS.SLAYER);

    double version;
    int current_xp = 0;
    int current_xpSlayer = 0;
    final long start_time = System.currentTimeMillis();

    double XpToLVslayer = Skills.getXPToNextLevel(SKILLS.SLAYER);
    double XpToLVhp = Skills.getXPToNextLevel(SKILLS.HITPOINTS);
    double XpToLVatt = Skills.getXPToNextLevel(SKILLS.ATTACK);
    double XpToLVstr = Skills.getXPToNextLevel(SKILLS.STRENGTH);
    double XpToLVdef = Skills.getXPToNextLevel(SKILLS.DEFENCE);//"Defense");
    double XpToLVrange = Skills.getXPToNextLevel(SKILLS.RANGED);
    long timeRan = System.currentTimeMillis() - start_time;
    int xpGained = current_xp - startXp;
    int xpGainedSlayer = current_xpSlayer - startXpSlayer;
    int lvsGained = 0;
    int startLv = Skills.getActualLevel(SKILLS.SLAYER);
    int currLv = Skills.getActualLevel(SKILLS.SLAYER);
    double multiplier = timeRan / 3600000D;
    int xpPerHour = (int) (xpGained / multiplier);
    int xpPerHourSlay = (int) (xpGainedSlayer / multiplier);
    double show_xp_to_lvrange = ((XpToLVrange / xpPerHour) * 60);
    double show_xp_to_lvstr = ((XpToLVstr / xpPerHour) * 60);
    double show_xp_to_lvatt = ((XpToLVatt / xpPerHour) * 60);
    double show_xp_to_lvdef = ((XpToLVdef / xpPerHour) * 60);
    double show_xp_to_lvhp = ((XpToLVhp / xpPerHour) * 60);
    double show_xp_to_lvslayer = ((XpToLVslayer / xpPerHourSlay) * 60);
    int timeToLv;

    String state = "null";
    boolean showPaint = true;
    Rectangle hideZone = new Rectangle(488, 348, 509, 368);

    public TuraelSlayer() {
        version = ((ScriptManifest) getClass().getAnnotation(
                ScriptManifest.class)).version();
    }

    public void putMap() {
        for (int i = 0; i < loot.length; i++) {
            map.put(loot[i], names[i]);
        }
    }

    public void eatFood() {
        RSItem[] food = Inventory.find(foodID);
        if (food.length > 0) {
            food[0].click("Eat");
            sleep(600, 700);
        } else {
            println("You are out of food in inventory, lets bank!");
            emergTele();
            sleep(600, 700);
        }

    }

    @Override
    public void duelRequestReceived(String arg0, String arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
	 * *
	 * * *
	 * * * * GUI STUFF * * * * * *
	 * * * 
	 * *
	 * 
     */
    public class TuraelSlayerGUI extends javax.swing.JFrame {

        /**
         * Creates new form TuraelSlayerGUI
         */
        public TuraelSlayerGUI() {
            initComponents();
            jRadioButton1.setSelected(true);
        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
        private void initComponents() {

            buttonGroup4 = new javax.swing.ButtonGroup();
            jTabbedPane1 = new javax.swing.JTabbedPane();
            jPanel1 = new javax.swing.JPanel();
            jRadioButton1 = new javax.swing.JRadioButton();
            jRadioButton2 = new javax.swing.JRadioButton();
            jRadioButton3 = new javax.swing.JRadioButton();
            jRadioButton4 = new javax.swing.JRadioButton();
            jLabel2 = new javax.swing.JLabel();
            jPanel2 = new javax.swing.JPanel();
            jRadioButton5 = new javax.swing.JRadioButton();
            jRadioButton6 = new javax.swing.JRadioButton();
            jLabel3 = new javax.swing.JLabel();
            jLabel4 = new javax.swing.JLabel();
            jLabel5 = new javax.swing.JLabel();
            jButton1 = new javax.swing.JButton();
            jLabel1 = new javax.swing.JLabel();

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            buttonGroup4.add(jRadioButton1);
            jRadioButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            jRadioButton1.setText("Loot Everything Including Non-Stackables");
            jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButton1ActionPerformed(evt);
                }
            });

            buttonGroup4.add(jRadioButton2);
            jRadioButton2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            jRadioButton2.setText("Loot Only Stackables and High Value Drops");
            jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButton2ActionPerformed(evt);
                }
            });

            buttonGroup4.add(jRadioButton3);
            jRadioButton3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            jRadioButton3.setText("Loot Only High Value Drops");
            jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButton3ActionPerformed(evt);
                }
            });

            buttonGroup4.add(jRadioButton4);
            jRadioButton4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            jRadioButton4.setText("Do Not Loot");
            jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButton4ActionPerformed(evt);
                }
            });

            jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
            jLabel2.setText("Only rune items, dragon items, etc");

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton1)
                                    .addComponent(jRadioButton2)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jRadioButton3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jRadioButton4))
                            .addContainerGap(20, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(15, 15, 15)
                            .addComponent(jRadioButton1)
                            .addGap(18, 18, 18)
                            .addComponent(jRadioButton2)
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jRadioButton3)
                                    .addComponent(jLabel2))
                            .addGap(18, 18, 18)
                            .addComponent(jRadioButton4)
                            .addContainerGap(17, Short.MAX_VALUE))
            );

            jTabbedPane1.addTab("Looting", jPanel1);

            jRadioButton5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            jRadioButton5.setText("Use Runes to Teleport");
            jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButton5ActionPerformed(evt);
                }
            });

            jRadioButton6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            jRadioButton6.setText("Pure Mode");
            jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButton6ActionPerformed(evt);
                }
            });

            jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
            jLabel3.setText("If you don't have the required magic level");

            jLabel4.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
            jLabel4.setText(" to use runes, it will use Tabs instead. Default");

            jLabel5.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
            jLabel5.setText(" is to use tabs.");

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton6)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(jRadioButton5)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel4)
                                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel5))))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jRadioButton5)
                                    .addComponent(jLabel3))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRadioButton6)
                            .addContainerGap())
            );

            jTabbedPane1.addTab("Misc", jPanel2);

            jButton1.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
            jButton1.setText("Start the Magic");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });

            jLabel1.setFont(new java.awt.Font("Tahoma", 3, 18)); // NOI18N
            jLabel1.setText("Yaw hide's Easy Slayer");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTabbedPane1)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addGap(0, 0, Short.MAX_VALUE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGap(93, 93, 93))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addComponent(jButton1)
                                                            .addGap(130, 130, 130))))))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton1)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>                         

        private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        }

        private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
            LootEverythingIncludingNonStacks = jRadioButton1.isSelected();
            LootOnlyStacksAndHighValueDrops = jRadioButton2.isSelected();
            LootOnlyHighValueDrops = jRadioButton3.isSelected();
            DoNotLoot = jRadioButton4.isSelected();
            UseRunesToTele = jRadioButton5.isSelected();
            PureMode = jRadioButton6.isSelected();

            waitGUI = false;
            sleep(200, 300);
        }

        private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        }

        private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        }

        private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        }

        private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        }

        private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {

        }

        // Variables declaration - do not modify                     
        private javax.swing.ButtonGroup buttonGroup4;
        private javax.swing.JButton jButton1;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JRadioButton jRadioButton1;
        private javax.swing.JRadioButton jRadioButton2;
        private javax.swing.JRadioButton jRadioButton3;
        private javax.swing.JRadioButton jRadioButton4;
        private javax.swing.JRadioButton jRadioButton5;
        private javax.swing.JRadioButton jRadioButton6;
        private javax.swing.JTabbedPane jTabbedPane1;
        // End of variables declaration                   
    }

    /**
     * **dynamic sig stuff ***
     */
    // Initialize variable
    @Override
    public void onEnd() {

    }

    @Override
    public void personalMessageReceived(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void tradeRequestReceived(String arg0) {
        // TODO Auto-generated method stub

    }

}

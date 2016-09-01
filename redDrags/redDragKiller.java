package scripts.redDrags;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.scene.input.KeyCode;
import javax.imageio.ImageIO;
import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.PreBreaking;

@ScriptManifest(authors = "Marko", category = "RedDrags", name = "7. RED DRAGONS", description = "Red dragons [START IN CATH BANK/CAMMY TP SPOT]", version = 0.1)
public class redDragKiller extends Script implements Painting, Ending, MessageListening07, Breaking, PreBreaking {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;

    private final RSArea CAMELOT_TP = new RSArea(new RSTile[]{
        new RSTile(2749, 3483, 0),
        new RSTile(2765, 3483, 0),
        new RSTile(2765, 3473, 0),
        new RSTile(2749, 3474, 0)
    });

    private final static RSArea SPOT3_AREA = new RSArea(new RSTile[]{
        new RSTile(2694, 9521, 0),
        new RSTile(2709, 9519, 0),
        new RSTile(2709, 9503, 0),
        new RSTile(2696, 9502, 0)
    });

    private final static RSArea SANIBOCH_AREA = new RSArea(new RSTile[]{
        new RSTile(2734, 3145, 0),
        new RSTile(2733, 3164, 0),
        new RSTile(2757, 3165, 0),
        new RSTile(2756, 3144, 0)
    });

    private final static RSArea STONES_AREA = new RSArea(new RSTile[]{
        new RSTile(2645, 9569, 0),
        new RSTile(2656, 9570, 0),
        new RSTile(2657, 9556, 0),
        new RSTile(2644, 9560, 0)
    });
    
    private final static RSArea BANK_AREA = new RSArea(new RSTile[] { 
        new RSTile(2802, 3446, 0), 
        new RSTile(2814, 3446, 0), 
        new RSTile(2816, 3434, 0), 
        new RSTile(2802, 3433, 0)
    });

    public int totalBones = 0;
    public int totalHides = 0;
    public int totalMoney = 0;
    public int totalBolts = 0;

    @Override
    public void run() {
        General.useAntiBanCompliance(true);
        this.abc_util = new ABCUtil();
        while (!stop_script) {
            switch (state()) {
                case LOGGING_IN:
                    botstatus = "Waiting for login...";
                    sleep(3000, 5000);
                    break;
                case MOVING:
                    botstatus = "Walking & hover";
                    heal();
                    antibans();
                    shouldEquipBolts();
                    dropVial();
                    hovering();
                    break;
                case WALK_TO_BANK:
                    botstatus = "Walking to bank";
                    WebWalking.walkTo(TILE_BANK);
                    break;
                case BANK:
                    botstatus = "Taking items";
                    camera();
                    grabItems();
                    break;
                case WALK_TO_SHIP:
                    botstatus = "Walking to ship";
                    WebWalking.walkTo(TILE_SHIP_1);
                    break;
                case TRAVEL_TO_BRIMHAVEN:
                    botstatus = "Traveling to Brimhaven";
                    travelToBrim();
                    break;
                case CROSS_PLANK:
                    botstatus = "Crossing plank";
                    crossplank();
                    break;
                case WALK_TO_SANIBOCH:
                    botstatus = "Walking to Saniboch";
                    WebWalking.walkTo(TILE_SANIBOCH);
                    break;
                case PAY_SANIBOCH:
                    botstatus = "Paying Saniboch 875gp";
                    paySaniboch();
                    break;
                case ENTER_DUNGEON:
                    botstatus = "Entering dungeon";
                    enterDungeon();
                    break;
                case WALK_TO_VINES_1:
                    botstatus = "Walking to vine: 1";
                    camera();
                    WebWalking.walkTo(TILE_VINES1);
                    break;
                case CHOP_VINE:
                    botstatus = "Chopping vines";
                    chopVines();
                    break;
                case WALK_TO_STONES:
                    botstatus = "Walking to Stepping stones";
                    camera();
                    WebWalking.walkTo(TILE_STONES);
                    break;
                case JUMP_STONES:
                    botstatus = "Jumping across stones";
                    jumpStones();
                    break;
                case WALK_TO_VINES_2:
                    botstatus = "Walking to vine: 2";
                    camera();
                    Walking.walkPath(TO_VINES_2);
                    break;
                case WALK_TO_LOG:
                    botstatus = "Walking to log";
                    camera();
                    Walking.clickTileMM(TILE_LOG, 1);
                    break;
                case CROSS_LOG:
                    botstatus = "Crossing log";
                    crossLog();
                    break;
                case WALK_TO_SPOT:
                    botstatus = "Walking to safe spot";
                    WebWalking.walkTo(TILE_SPOT_3);
                    break;
                case EQUIP_BOLTS:
                    botstatus = "Equipping bolts";
                    equipBolts();
                    break;
                case UNEQUIP_BOLTS:
                    botstatus = "Un-equipping bolts";
                    unequipBolts();
                    break;
                case ERROR:
                    botstatus = "Something went wrong...";
                    break;
                case WALK_TO_SPOT3:
                    botstatus = "Walking to spot 3";
                    Walking.clickTileMM(TILE_SPOT_3, 1);
                    break;
                case COMBAT_DOG:
                    botstatus = "Fighting Dog";
                    heal();
                    antibans();
                    moveMouse();
                    break;
                case COMBAT_BABY:
                    botstatus = "Fighting Baby dragon";
                    heal();
                    antibans();
                    moveMouse();
                    break;
                case COMBAT_DRAGON:
                    botstatus = "Fighting Red dragon";
                    heal();
                    antibans();
                    moveMouse();
                    break;
                case ATTACK:
                    botstatus = "Attacking";
                    heal();
                    attackDragon();
                    break;
                case WAITING_DRAGONS:
                    botstatus = "Waiting for dragon spawns";
                    antibans();
                    heal();
                    break;
                case LOOTING:
                    botstatus = "Looting items";
                    looting();
                    break;
                case TP_CAMELOT:
                    botstatus = "Teleporting to Camelot";
                    tpCammy();
                    break;
                case MAKE_SPACE:
                    botstatus = "Making inventory space";
                    equipBolts();
                    makeSpace();
                    break;
                case ATTACK_BACK:
                    botstatus = "Attempting to attack back";
                    //attackBack();
                    break;
                case START_COMBAT:
                    botstatus = "Starting combat";
                    startCombat();
                    break;
            }
            // control cpu usage
            General.sleep(100, 250);
        }
    }

    enum State {
        LOGGING_IN,
        ERROR,
        MOVING,
        COMBAT_DOG,
        COMBAT_BABY,
        COMBAT_DRAGON,
        ATTACK,
        WALK_TO_BANK,
        BANK,
        WALK_TO_SHIP,
        TRAVEL_TO_BRIMHAVEN,
        CROSS_PLANK,
        WALK_TO_SANIBOCH,
        PAY_SANIBOCH,
        ENTER_DUNGEON,
        WALK_TO_VINES_1,
        CHOP_VINE,
        WALK_TO_STONES,
        JUMP_STONES,
        WALK_TO_VINES_2,
        WALK_TO_LOG,
        CROSS_LOG,
        WALK_TO_SPOT,
        EQUIP_BOLTS,
        UNEQUIP_BOLTS,
        WALK_TO_SPOT3,
        WAITING_DRAGONS,
        LOOTING,
        TP_CAMELOT,
        MAKE_SPACE,
        ATTACK_BACK,
        START_COMBAT
    }

    public final int NPC_TRAVEL1 = 1334;
    public final int NPC_TRAVEL2 = 1331; //Chart
    public final int NPC_SANIBOCH = 2345; //Pay
    public final int ITEM_COINS = 995; //480 travel + 875 saniboch
    public final int ITEM_AXE = 1357;
    //public final int ITEM_FOOD = 385; //shark
    public final int ITEM_FOOD = 7946; //monkfish
    public final int ITEM_CAMTAB = 8010;
    public final int ITEM_ANTIFIRE = 2458;
    public final int ITEM_RANGEPOT = 169;
    public final int[] ITEM_RANGEPOT_ALL = {2444, 169, 171, 173};
    public final int[] BANK_BOOTHS = {6943, 6944};
    public final int ITEM_BOLTS = 9142;
    // loot 
    public final int ITEM_REDHIDE = 1749;
    public final int ITEM_DBONES = 536;

    public final int ITEM_ENSOULED = 13510; //13511
    public final int ITEM_ADDYPLATE = 1123;
    public final int ITEM_KEY_TEETH = 985;
    public final int ITEM_KEY_LOOP = 987;
    public final int ITEM_RUNE_DART = 811;
    public final int ITEM_RUNE_LONG = 1303;
    public final int ITEM_HERB_RANARR = 207;
    public final int ITEM_HERB_AVANTOE = 211;
    public final int ITEM_HERB_KWUARM = 213;
    public final int ITEM_HERB_CADANTINE = 215;
    public final int ITEM_ADDYBAR = 2361;
    // end-loot
    public final int ITEM_VIAL = 229;
    public final int OBJ_PLANK = 17401;
    public final int OBJ_DUNG = 10627;
    public final int OBJ_VINES1 = 21731;
    public final int OBJ_VINES2 = 21733;
    public final int OBJ_STONES = 21738;
    public final int OBJ_LOG = 20882;
    public final RSTile TILE_BANK = new RSTile(2809, 3440, 0);
    public final RSTile TILE_SHIP_1 = new RSTile(2797, 3414, 0);
    public final RSTile TILE_ONSHIP_1 = new RSTile(2763, 3238, 1);
    public final RSTile TILE_SHIP_2 = new RSTile(2760, 3238, 0);
    public final RSTile TILE_SANIBOCH = new RSTile(2745, 3152, 0);
    public final RSTile TILE_DUNG = new RSTile(2713, 9564, 0);
    public final RSTile TILE_VINES1 = new RSTile(2691, 9564, 0);
    public final RSTile TILE_STONES = new RSTile(2649, 9562, 0);
    public final RSTile TILE_VINES2 = new RSTile(2672, 9499, 0);
    public final RSTile TILE_LOG = new RSTile(2682, 9506, 0);
    public final RSTile[] TO_VINES_2 = {new RSTile(2647, 9557, 0), new RSTile(2647, 9550, 0), new RSTile(2647, 9542, 0), new RSTile(2647, 9533, 0), new RSTile(2642, 9528, 0), new RSTile(2642, 9519, 0),
        new RSTile(2646, 9511, 0), new RSTile(2652, 9504, 0), new RSTile(2659, 9500, 0), new RSTile(2666, 9502, 0), new RSTile(2672, 9499, 0)};
    public final RSTile TILE_SPOT_1 = new RSTile(2706, 9533, 0);
    public final RSTile TILE_SPOT_2 = new RSTile(2728, 9520, 0);
    public final RSTile TILE_SPOT_3 = new RSTile(2702, 9512, 0);
    public final RSTile TILE_SPOT_4 = new RSTile(2708, 9552, 0);

    public RSNPC target = null;

    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGING_IN;
        } else if (Player.isMoving()) {
            return State.MOVING;
        /*} else if (isAtCamelotSpot() && !hasItems() && Equipment.isEquipped(ITEM_BOLTS) && !hasItem(ITEM_BOLTS)) {
            return State.UNEQUIP_BOLTS;*/
        } else if ((isAtCamelotSpot() || (isAtBankArea() && !Banking.isInBank())) /*&& hasItem(ITEM_BOLTS)*/) {
            return State.WALK_TO_BANK;
        }  else if (Banking.isInBank() && !hasItems() /*&& !Equipment.isEquipped(ITEM_BOLTS)*/) {
            return State.BANK;
            /*} else if (Banking.isInBank() && hasItems() && hasItem(ITEM_MITH_BOLTS)) {
            return State.EQUIP_BOLTS;*/
        } else if (Banking.isInBank() && hasItems() /*&& hasItem(ITEM_BOLTS)*/) {
            return State.WALK_TO_SHIP;
        } else if (hasItems() && (isNPCnear(NPC_TRAVEL1) || isNPCnear(NPC_TRAVEL2)) && Player.getPosition().distanceTo(TILE_SHIP_1) < 7) {
            return State.TRAVEL_TO_BRIMHAVEN;
        } else if (hasItems() && Player.getPosition().distanceTo(TILE_ONSHIP_1) == 0) {
            return State.CROSS_PLANK;
        } else if (hasItems() && Player.getPosition().distanceTo(TILE_SHIP_2) == 0) {
            return State.WALK_TO_SANIBOCH;
        } else if (hasItems() && hasItem(ITEM_COINS) && (Player.getPosition().distanceTo(TILE_SANIBOCH) < 5 || isAtSanibochSpot())) {
            return State.PAY_SANIBOCH;
        } else if (isNPCnear(NPC_SANIBOCH) && Player.getPosition().distanceTo(TILE_SANIBOCH) < 5 && !hasItem(ITEM_COINS)) {
            return State.ENTER_DUNGEON;
        } else if (Player.getPosition().distanceTo(TILE_DUNG) == 0) {
            return State.WALK_TO_VINES_1;
        } else if (Player.getPosition().distanceTo(TILE_VINES1) == 0 || (Player.getPosition().distanceTo(TILE_VINES2) <= 2 && Player.getPosition().distanceTo(new RSTile(2674, 9499)) > 0)) {
            return State.CHOP_VINE;
        } else if (Player.getPosition().distanceTo(new RSTile(2689, 9564)) == 0) {
            return State.WALK_TO_STONES;
        } else if (Player.getPosition().distanceTo(TILE_STONES) <= 4 || isAtStonesSpot()) {
            return State.JUMP_STONES;
        } else if (Player.getPosition().distanceTo(new RSTile(2647, 9557)) <= 1) {
            return State.WALK_TO_VINES_2;
        } else if (Player.getPosition().distanceTo(new RSTile(2674, 9499)) == 0) {
            return State.WALK_TO_LOG;
        } else if (Player.getPosition().distanceTo(TILE_LOG) <= 2) {
            return State.CROSS_LOG;
        } else if (Player.getPosition().distanceTo(new RSTile(2687, 9506)) == 0) {
            return State.WALK_TO_SPOT;
        } else if (Inventory.isFull() && isAtSpot3() && Inventory.find(ITEM_FOOD).length < 1) {
            return State.TP_CAMELOT;
        } else if (Combat.getHP() <= (Combat.getMaxHP() / 2) && isAtSpot3() && Inventory.find(ITEM_FOOD).length < 1) {
            return State.TP_CAMELOT;
        } else if (Inventory.isFull() && isAtSpot3() && Inventory.find(ITEM_FOOD).length > 0) {
            return State.MAKE_SPACE;
        } else if (!Inventory.isFull() && isAtSpot3() && hasToLoot()) {
            return State.LOOTING;
        } else if (!Inventory.isFull() && isAtSpot3() && !hasToLoot()) {
            if (Player.getPosition().distanceTo(TILE_SPOT_3) == 0) {
                RSNPC[] dragons = NPCs.findNearest(Filters.NPCs.nameEquals("Red dragon").combine(Filters.NPCs.inArea(SPOT3_AREA), true));
                if (Player.getRSPlayer().isInCombat() && Player.getRSPlayer().getInteractingCharacter() != null) {
                    if ("Wild dog".equals(Player.getRSPlayer().getInteractingCharacter().getName())) {
                        return State.COMBAT_DOG;
                    } else if ("Baby dragon".equals(Player.getRSPlayer().getInteractingCharacter().getName())) {
                        return State.COMBAT_BABY;
                    }
                } else if (Combat.getAttackingEntities().length > 0 && !Combat.isUnderAttack()) {
                    debugg("ERROR @ Combat.getAttackingEntities().length > 0 && !Combat.isUnderAttack()");
                    return State.ERROR;
                } else if (Combat.getAttackingEntities().length <= 0 && Combat.isUnderAttack()) {
                    debugg("ERROR @ Combat.getAttackingEntities().length <= 0 && Combat.isUnderAttack()");
                    return State.ERROR;
                } else if (Combat.getAttackingEntities().length > 0 && target != null) {
                    return State.COMBAT_DRAGON;
                } else if (dragons.length > 0 && !Player.getRSPlayer().isInCombat()) {
                    if (SPOT3_AREA.contains(dragons[0].getPosition())) {
                        return State.ATTACK;
                    } else {
                        return State.WAITING_DRAGONS;
                    }
                } else if (Combat.getAttackingEntities().length <= 0 && Player.getRSPlayer().isInCombat()) {
                    debugg("Under attack! Attempting to attack NPC back");
                    return State.ATTACK_BACK;
                } else if (Combat.getAttackingEntities().length > 0 && Player.getRSPlayer().isInCombat()) {
                    debugg("Under attack by new NPC while still attacking! Attempting to attack back");
                    return State.ATTACK_BACK;
                } else {
                    debugg("ERROR @ COMBAT |UNKNOWN|");
                    return State.ERROR;
                }
            } else {
                return State.WALK_TO_SPOT3;
            }
        }
        debugg("ERROR @ FINISH STATE");
        return State.ERROR;
    }

    private void hovering() {
        if(Banking.isInBank() && !Banking.isBankScreenOpen()) {
            Banking.openBank();
        }
    }
    
    private boolean startCombat() {
        return false;
    }

    private boolean dropVial() {
        RSItem[] vial = Inventory.find(ITEM_VIAL);
        if (vial.length > 0) {
            botstatus = "Dropping " + vial[0].getDefinition().getName();
            debugg("Dropping " + vial[0].getDefinition().getName());
            if (vial[0].click("Drop")) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(250, 500);
                        return Inventory.find(ITEM_VIAL).length <= 0;
                    }
                }, 2000);
            }
        }
        return false;
    }

    private boolean drinkPot() {
        final int currLVL = Skills.getCurrentLevel(SKILLS.RANGED);
        final int actualLVL = Skills.getActualLevel(SKILLS.RANGED);
        final int LVL_DIFF = 5;
        RSItem[] pot = Inventory.find(ITEM_RANGEPOT_ALL);
        if (currLVL < actualLVL + LVL_DIFF) {
            if (pot.length > 0) {
                if (GameTab.TABS.INVENTORY.isOpen()) {
                    botstatus = "Drinking " + pot[0].getDefinition().getName();
                    debugg("Drinking " + pot[0].getDefinition().getName());
                    if (pot[0].click("Drink")) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Skills.getCurrentLevel(SKILLS.RANGED) > (actualLVL + LVL_DIFF);
                            }
                        }, 2000);
                    }
                } else {
                    GameTab.open(GameTab.TABS.INVENTORY);
                }
            }
        }
        return false;
    }

    private void makeSpace() {
        RSItem[] food = Inventory.find(ITEM_FOOD);
        if (food.length > 0) {
            botstatus = "Eating " + food[0].getDefinition().getName() + " for space";
            if (food[0].click("Eat")) {
                General.sleep(250, 500);
            }
        }
    }

    private boolean attackBack() {
        RSNPC[] monsters = NPCs.getAll();
        for (RSNPC monster : monsters) {
            if (monster.isInteractingWithMe() && monster.isValid()) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(250, 500);
                        return monster.isInCombat();
                    }
                }, 2000);
            }
        }
        return false;
    }

    private boolean tpCammy() {
        if (GameTab.TABS.INVENTORY.isOpen()) {
            RSItem[] tab = Inventory.find(ITEM_CAMTAB);
            if (tab.length > 0) {
                if (tab[0].click("Break")) {
                    return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(250, 500);
                            return isAtCamelotSpot();
                        }
                    }, 6000);
                }
            }
        } else {
            GameTab.open(GameTab.TABS.INVENTORY);
            return false;
        }
        return false;
    }

    public boolean looting() {
        final int ITEMZ[] = {ITEM_DBONES, ITEM_REDHIDE, ITEM_ENSOULED, ITEM_ADDYPLATE,ITEM_KEY_TEETH,ITEM_KEY_LOOP,ITEM_RUNE_DART,ITEM_RUNE_LONG,ITEM_HERB_AVANTOE,ITEM_HERB_CADANTINE,ITEM_HERB_KWUARM,ITEM_HERB_RANARR,ITEM_ADDYBAR}; //MITH
        RSGroundItem[] loot = GroundItems.findNearest(ITEMZ);
        if (loot.length > 0) {
            botstatus = "Looting: " + loot[0].getDefinition().getName();
            if (loot[0].isOnScreen()) {
                //int numberz = Inventory.find(loot[0].getID()).length;
                if (Clicking.click("Take " + loot[0].getDefinition().getName(), loot[0])) {
                    int price = (PriceChecker.getOSbuddyPrice(loot[0].getID()) * loot[0].getStack());
                    if (null != loot[0].getDefinition().getName()) {
                        switch (loot[0].getDefinition().getName()) {
                            case "Dragon bones":
                                totalBones++;
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_DBONES);
                            case "Red dragonhide":
                                totalHides++;
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_REDHIDE);
                            case "Ensouled dragon head":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_ENSOULED);
                            case "Adamant platebody":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_ADDYPLATE);
                            case "Tooth half of key":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_KEY_TEETH);
                            case "Loop half of key":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_KEY_TEETH);
                            case "Rune dart":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_RUNE_DART);
                            case "Rune longsword":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_RUNE_LONG);
                            case "Grimy ranarr weed":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_HERB_RANARR);
                            case "Grimy avantoe":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_HERB_AVANTOE);
                            case "Grimy kwuarm":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_HERB_KWUARM);
                            case "Grimy cadantine":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_HERB_CADANTINE);
                            case "Adamantite bar":
                                totalMoney += price;
                                debugg("Picked up: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID() + " (" + price + "gp)");
                                return hasLooted(ITEM_ADDYBAR);
                            case "Mithril bolts":
                                totalBolts += loot[0].getStack();
                                debugg("Recovered: " + loot[0].getStack() + "x " + loot[0].getDefinition().getName() + " with ID: " + loot[0].getID());
                                General.sleep(500, 750);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else {
                //Camera.turnToTile(loot[0].getPosition());
                Walking.walkTo(loot[0].getPosition());
            }
        }
        return false;
    }

    public boolean hasLooted(final int ITEM_ID) {
        final int numberz = Inventory.find(ITEM_ID).length;
        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(250, 500);
                return Inventory.find(ITEM_ID).length > numberz;
            }
        }, 4000);
    }

    public boolean hasToLoot() {
        RSGroundItem[] bones = GroundItems.findNearest(ITEM_DBONES);
        RSGroundItem[] hides = GroundItems.findNearest(ITEM_REDHIDE);
        RSGroundItem[] addybar = GroundItems.findNearest(ITEM_ADDYBAR);
        RSGroundItem[] addyplate = GroundItems.findNearest(ITEM_ADDYPLATE);
        RSGroundItem[] ensouled = GroundItems.findNearest(ITEM_ENSOULED);
        RSGroundItem[] avantoe = GroundItems.findNearest(ITEM_HERB_AVANTOE);
        RSGroundItem[] cadantine = GroundItems.findNearest(ITEM_HERB_CADANTINE);
        RSGroundItem[] kwuarm = GroundItems.findNearest(ITEM_HERB_KWUARM);
        RSGroundItem[] ranarr = GroundItems.findNearest(ITEM_HERB_RANARR);
        RSGroundItem[] loop = GroundItems.findNearest(ITEM_KEY_LOOP);
        RSGroundItem[] teeth = GroundItems.findNearest(ITEM_KEY_TEETH);
        RSGroundItem[] dart = GroundItems.findNearest(ITEM_RUNE_DART);
        RSGroundItem[] rlong = GroundItems.findNearest(ITEM_RUNE_LONG);
        return bones.length > 0 || hides.length > 0 || addybar.length > 0 || addyplate.length > 0 || ensouled.length > 0 || avantoe.length > 0 || cadantine.length > 0 || kwuarm.length > 0 || ranarr.length > 0 || loop.length > 0 || teeth.length > 0 || rlong.length > 0 || dart.length > 0;
    }

    public boolean attackDragon() {
        camera();
        drinkPot();
        RSNPC[] dragons = NPCs.findNearest(Filters.NPCs.nameEquals("Red dragon").combine(Filters.NPCs.inArea(SPOT3_AREA), true));
        //RSNPC[] goblins = NPCs.findNearest("Hobgoblin");
        if (dragons.length > 0 && !Player.isMoving()) {
            botstatus = "Attacking dragon";
            if (!dragons[0].isInCombat() && dragons[0].isValid()) {
                if(dragons[0].isOnScreen()) {
                    if (dragons[0].click("Attack")) {
                        target = dragons[0];
                        debugg("Successfully attacked " + dragons[0].getDefinition().getName());
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return target.isInCombat();
                            }
                        }, 7500);
                    }
                } else {
                    Camera.turnToTile(dragons[0].getPosition());
                }
            }
        }
        return false;
    }

    public void heal() {
        int eating;
        if (Skills.getActualLevel(SKILLS.HITPOINTS) >= 20 && Skills.getActualLevel(SKILLS.HITPOINTS) <= 40) {
            eating = 14;
        } else if (Skills.getActualLevel(SKILLS.HITPOINTS) >= 41) {
            //eating = 21; //shark
            eating = 17; //monkfish
        } else {
            eating = 7;
        }
        // While current HP <= minHealth and food exists
        while ((Combat.getHP() < (Skills.getActualLevel(SKILLS.HITPOINTS)) - eating) && foodExists()) {
            eat();
        }
    }

    public boolean foodExists() {
        return Inventory.find(ITEM_FOOD).length > 0;
    }

    public boolean eat() {
        botstatus = "Eating...";
        RSItem[] food = Inventory.find(ITEM_FOOD);
        final int currentHP = Skills.getCurrentLevel(SKILLS.HITPOINTS);
        if (food.length > 0) {
            if (food[0] != null) {
                if (GameTab.TABS.INVENTORY.isOpen()) {
                    if (Clicking.click("Eat", food[0])) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Combat.getHP() > currentHP;
                                //return Combat.getHP() > currHP;
                            }
                        }, 2500);
                    }
                } else {
                    GameTab.open(GameTab.TABS.INVENTORY);
                }
            }
        }
        return false;
    }

    private int getBoltsNum() {
        if(Equipment.isEquipped(ITEM_BOLTS)) {
            if(Equipment.getCount(ITEM_BOLTS) < 150) {
                return General.random(195, 225) - Equipment.getCount(ITEM_BOLTS);
            }  else {
                return 0;
            }
        }
        return 0;
    }
    
    private void shouldEquipBolts() {
        if(isAtSpot3()) {
            equipBolts();
        } else if(!isAtSpot3()) {
            int rng = General.random(1, 100);
            if(rng > 50 && rng < 55) {
                equipBolts();
            }
        }
    }
    
    private boolean equipBolts() {
        if(hasItem(ITEM_CAMTAB)) {
            if (GameTab.TABS.INVENTORY.isOpen()) {
                RSItem[] bolts = Inventory.find(ITEM_BOLTS);
                if (bolts.length > 0) {
                    if (bolts[0].click("Wield")) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Inventory.find(ITEM_BOLTS).length <= 0;
                            }
                        }, 2500);
                    }
                }
            } else {
                GameTab.open(GameTab.TABS.INVENTORY);
                return false;
            }
        }
        return false;
    }

    private boolean unequipBolts() {
        if (GameTab.TABS.EQUIPMENT.isOpen()) {
            Equipment.remove(ITEM_BOLTS);
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(250, 500);
                    return Inventory.find(ITEM_BOLTS).length > 0;
                }
            }, 2500);
        } else {
            GameTab.open(GameTab.TABS.EQUIPMENT);
        }
        return false;
    }

    private void crossLog() {
        RSObject[] log = Objects.findNearest(3, OBJ_LOG);
        if (log.length > 0) {
            if (log[0].click("Walk-across")) {
                General.sleep(5000, 6000);
            }
        }
    }

    private boolean jumpStones() {
        RSObject[] stones = Objects.findNearest(10, OBJ_STONES);
        if (stones.length > 0) {
            if (stones[0].isOnScreen()) {
                if (stones[0].click("Jump-from")) {
                    return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(250, 500);
                            return Player.getPosition().distanceTo(new RSTile(2647, 9557)) == 0;
                        }
                    }, 10000);
                }
            } else {
                Walking.clickTileMM(TILE_STONES, 1);
                return false;
            }
        }
        return false;
    }

    private void chopVines() {
        RSObject[] vines = Objects.findNearest(2, OBJ_VINES1, OBJ_VINES2);
        if (vines.length > 0) {
            if(vines[0].isOnScreen() && vines[0].isClickable()) {
                if (vines[0].click("Chop-down")) {
                    General.sleep(2500, 3500);
                }
            } else {
                Camera.turnToTile(vines[0].getPosition());
            }
        }
    }

    private void enterDungeon() {
        RSObject[] dung = Objects.findNearest(7, OBJ_DUNG);
        if (dung.length > 0) {
            botstatus = "Dung near";
            if (dung[0].isOnScreen()) {
                botstatus = "On screen";
                if (dung[0].isClickable()) {
                    botstatus = "Clickable";
                    debugg("Dungeon is clickable!");
                    if (DynamicClicking.clickRSObject(dung[0], "Enter")) {
                        General.sleep(1250, 2000);
                    }
                } else {
                    botstatus = "Not clickable...";
                    debugg("Dungeon is NOT clickable, using Mouse");
                    cameraEnterDung();
                    int x = Projection.tileToScreen(dung[0].getPosition(), 0).x;
                    int y = Projection.tileToScreen(dung[0].getPosition(), 0).y;
                    Mouse.click(x + General.random(5, 75), y + General.random(-5, 5), 1);
                    General.sleep(2000, 3000);
                }
            }
        }
    }

    private void paySaniboch() {
        RSNPC[] sani = NPCs.findNearest(NPC_SANIBOCH);
        if (sani.length > 0) {
            if (sani[0].isOnScreen()) {
                if (sani[0].click("Pay")) {
                    General.sleep(1250, 1500);
                    if (NPCChat.getClickContinueInterface() != null) {
                        if (NPCChat.clickContinue(true)) {
                            General.sleep(500, 750);
                        }
                    }
                }
            } else {
                WebWalking.walkTo(TILE_SANIBOCH);
            }
        }
    }

    private void crossplank() {
        RSObject[] plank = Objects.findNearest(3, OBJ_PLANK);
        if (plank.length > 0) {
            if (plank[0].click("Cross")) {
                General.sleep(1300, 2000);
            }
        }
    }

    private void travelToBrim() {
        RSNPC[] crew = NPCs.findNearest(NPC_TRAVEL1, NPC_TRAVEL2);
        RSInterface map = Interfaces.get(95);
        if (map != null) {
            RSInterface[] list = map.getChildren();
            if (list != null) {
                if (list[41].click("Ok")) { //41,42,6,26 (16)
                    General.sleep(800, 1000);
                    if (NPCChat.getClickContinueInterface() != null) {
                        if (NPCChat.clickContinue(true)) {
                            General.sleep(550, 700);
                            if (NPCChat.selectOption("Ok", true)) {
                                General.sleep(4250, 4900);
                            }
                        }
                    }
                }
            }
        } else if (crew.length > 0 && Inventory.find(ITEM_COINS).length > 0) {
            if (crew[0].click("Charter")) {
                General.sleep(2350, 2950);
            }
        }
    }

    public void moveMouse() {
        int x = General.random(0, 150);
        if (x == 51) {
            this.abc_util.moveMouse();
            debugg("Custom Antiban: moving mouse randomly");
        }
    }
    
    private void grabItems() {
        int BOLT_NUM = getBoltsNum();
        if (Banking.isBankScreenOpen()) {
            Banking.depositAll();
            //General.sleep(450, 700);
            //Banking.withdraw(1, ITEM_ANTIFIRE);
            General.sleep(550, 700);
            Banking.withdraw(1, ITEM_AXE);
            General.sleep(550, 700);
            Banking.withdraw(1, ITEM_CAMTAB);
            General.sleep(550, 700);
            Banking.withdraw(1, ITEM_RANGEPOT);
            General.sleep(550, 700);
            //Banking.withdraw(5, ITEM_FOOD); //shark
            Banking.withdraw(6, ITEM_FOOD); //monkfish
            General.sleep(550, 700);
            if(BOLT_NUM > 0) {
                Banking.withdraw(BOLT_NUM, ITEM_BOLTS);
                General.sleep(550, 700);
            }
            Banking.withdraw(1355, ITEM_COINS);
            General.sleep(750, 1000);
        } else {
            botstatus = "Opening bank";
            Banking.openBank();
            General.sleep(250, 500);
        }
    }

    private boolean isAtBankArea() {
        return BANK_AREA.contains(Player.getPosition());
    }
    
    private boolean isAtStonesSpot() {
        return STONES_AREA.contains(Player.getPosition());
    }

    private boolean isAtSanibochSpot() {
        return SANIBOCH_AREA.contains(Player.getPosition());
    }

    private boolean isAtCamelotSpot() {
        return CAMELOT_TP.contains(Player.getPosition());
    }

    private boolean isAtSpot3() {
        return SPOT3_AREA.contains(Player.getPosition());
    }

    private void cameraEnterDung() {
        if (Camera.getCameraAngle() < 65) {
            botstatus = "Rotating camera (dung)";
            int angleRNG = General.random(65, 100);
            Camera.setCameraAngle(angleRNG);
        }
        if (Camera.getCameraRotation() < 335) {
            botstatus = "Rotating camera (dung)";
            int rotationRNG = General.random(335, 359);
            Camera.setCameraRotation(rotationRNG);
        }
    }

    private void camera() {
        if (Camera.getCameraAngle() < 88) {
            botstatus = "Rotating camera";
            int angleRNG = General.random(88, 100);
            Camera.setCameraAngle(angleRNG);
        }
        /*if (Camera.getCameraRotation() < 260 || Camera.getCameraRotation() > 300) {
            botstatus = "Rotating camera";
            int rotationRNG = General.random(260, 300);
            Camera.setCameraRotation(rotationRNG);
        }*/
    }

    private boolean isObjectNear(final int OBJECT_ID) {
        RSObject[] objs = Objects.find(6, OBJECT_ID);
        return objs.length > 0;
    }

    private boolean isGroundObjectNear(final int OBJECT_ID) {
        RSGroundItem[] objs = GroundItems.findNearest(OBJECT_ID);
        if (objs.length > 0) {
            if (objs[0].isClickable() && objs[0].isOnScreen()) {
                return true;
            }
        }
        return false;
    }

    private boolean isNPCnear(final int NPC_ID) {
        RSNPC[] npcs = NPCs.findNearest(NPC_ID);
        if (npcs.length > 0) {
            if (npcs[0].isClickable() && npcs[0].isOnScreen() && npcs[0].isValid()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasItem(final int ITEM_ID) {
        RSItem[] items = Inventory.find(ITEM_ID);
        return items.length > 0;
    }

    private boolean hasItems() {
        return hasItem(ITEM_AXE) && hasItem(ITEM_CAMTAB) && hasItem(ITEM_COINS) && hasItem(ITEM_FOOD);
    }

    public void debugg(String msg) {
        if (debugging) {
            General.println(msg);
        }
    }

    @Override
    public void serverMessageReceived(String arg0) {
        General.println("Server message: " + arg0);
    }

    @Override
    public void clanMessageReceived(String arg0, String arg1) {
        General.println("Clan chat message: " + arg0 + ", " + arg1);
    }

    @Override
    public void duelRequestReceived(String arg0, String arg1) {
        General.println("Duel request: " + arg0 + ", " + arg1);
    }

    @Override
    public void personalMessageReceived(String arg0, String arg1) {
        General.println("Personal message: " + arg0 + ", " + arg1);
    }

    @Override
    public void tradeRequestReceived(String arg0) {
        General.println("Trade request: " + arg0);
    }

    @Override
    public void playerMessageReceived(String arg0, String arg1) {
        General.println("Player message: " + arg0 + ", " + arg1);
    }

    @Override
    public void onEnd() {
        long timeRan = System.currentTimeMillis() - START_TIME;
        int moneyH = (int) ((totalMoney * 3600000D) / timeRan);
        General.println("Total Bones collected: " + totalBones + ", Hides: " + totalHides + ", GP: " + NumberFormat.getNumberInstance(Locale.GERMAN).format(totalMoney) + " (" + NumberFormat.getNumberInstance(Locale.GERMAN).format(moneyH) + "gp/h), recovered " + totalBolts + "x mithril bolts in " + Timing.msToString(timeRan));
    }

    @Override
    public void onBreakEnd() {
        debugg("onBreakEnd initiated");
    }

    @Override
    public void onBreakStart(long break_time) {
        debugg("onBreakStart initiated");
    }

    @Override
    public void onPreBreakStart(long break_time) {
        debugg("onPreBreakStart initiated");
    }

    public void antibans() {
        if (this.abc_util.shouldCheckTabs()) {
            this.abc_util.checkTabs();
            debugg("Antiban: Checking tab");
        } else if (this.abc_util.shouldCheckXP()) {
            this.abc_util.checkXP();
            debugg("Antiban: Checking XP");
        } else if (this.abc_util.shouldExamineEntity()) {
            this.abc_util.examineEntity();
            debugg("Antiban: Examining entity");
        } else if (this.abc_util.shouldHover()) {
            //debugg("Antiban: Hovering *");
        } else if (this.abc_util.shouldLeaveGame()) {
            this.abc_util.leaveGame();
            debugg("Antiban: Mouse leaving game");
        } else if (this.abc_util.shouldMoveMouse()) {
            this.abc_util.moveMouse();
            debugg("Antiban: Moving mouse");
        } else if (this.abc_util.shouldMoveToAnticipated()) {
            //debugg("Antiban: MoveToAnticipated *");
        } else if (this.abc_util.shouldOpenMenu()) {
            //debugg("Antiban: Opening menu *");
        } else if (this.abc_util.shouldPickupMouse()) {
            this.abc_util.pickupMouse();
            debugg("Antiban: Picking up mouse");
        } else if (this.abc_util.shouldRightClick()) {
            this.abc_util.rightClick();
            debugg("Antiban: Right clicking");
        } else if (this.abc_util.shouldRotateCamera()) {
            this.abc_util.rotateCamera();
            debugg("Antiban: Rotating camera");
        } else if (Game.getRunEnergy() > this.abc_util.generateRunActivation() && !Game.isRunOn()) {
            Options.setRunOn(true);
            debugg("Antiban: Turning run on");
        }
    }

    // PAINT
    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    // private final Image img = getImage("http://i.imgur.com/1JBiwwy.png");
    private final Image img = getImage("");
    private static final long START_TIME = System.currentTimeMillis();
    private final int startLvl = Skills.getActualLevel(SKILLS.RANGED);
    private final int startLvlHP = Skills.getActualLevel(SKILLS.HITPOINTS);
    private final int startXP = Skills.getXP(SKILLS.RANGED);
    private final int startXPHP = Skills.getXP(SKILLS.HITPOINTS);
    Font font = new Font("Verdana", Font.BOLD, 14);

    @Override
    public void onPaint(Graphics g) {

        Graphics2D gg = (Graphics2D) g;
        gg.drawImage(img, 0, 304, null);

        long timeRan = System.currentTimeMillis() - START_TIME;
        int currentLvl = Skills.getActualLevel(SKILLS.RANGED);
        int currentLvlHP = Skills.getActualLevel(SKILLS.HITPOINTS);
        int gainedLvl = currentLvl - startLvl;
        int gainedLvlHP = currentLvl - startLvlHP;
        int gainedXP = Skills.getXP(SKILLS.RANGED) - startXP;
        int gainedXPHP = Skills.getXP(SKILLS.HITPOINTS) - startXPHP;
        int xpToLevel = Skills.getXPToNextLevel(SKILLS.RANGED);
        int xpToLevelHP = Skills.getXPToNextLevel(SKILLS.HITPOINTS);
        long xpPerHour = (long) (gainedXP * 3600000D / timeRan);
        long xpPerHourHP = (long) (gainedXPHP * 3600000D / timeRan);
        //int traps = getTrapNum();

        int bonesH = (int) ((totalBones * 3600000D) / timeRan);
        //int hidesH = (int) ((totalHides * 3600000D) / timeRan);
        int moneyH = (int) ((totalMoney * 3600000D) / timeRan);

        g.setFont(font);
        //g.setColor(new Color(0, 0, 204));
        g.setColor(new Color(255, 255, 255));
        g.drawString("Status: " + botstatus, 10, 275);
        g.drawString("Runtime: " + Timing.msToString(timeRan), 10, 295);
        g.drawString("Bones|Hides/h: " + bonesH + " (" + NumberFormat.getNumberInstance(Locale.GERMAN).format(moneyH) + "gp/h) Range boost: " + Skills.getCurrentLevel(SKILLS.RANGED) + "/" + currentLvl, 10, 315);
        g.drawString("RXP/h: " + xpPerHour + " HPXP/h: " + xpPerHourHP + " |R:" + xpToLevel + "|HP:" + xpToLevelHP, 10, 335);
        //g.drawString("OSBuddy price: " + osPrice + " (" + osPrice * perHR + "gp/h)", 200, 430);
        //g.drawString("GE price: " + gePrice + " (" + gePrice * perHR + "gp/h)", 200, 450);
        //g.drawString("XP TNL: " + xpToLevel, 225, 450);
        //g.drawString("Traps: " + traps, 225, 470);
        //g.drawString("XP/H: " + xpPerHour, 300, 450);

    }


    /* END PAINT */
    /**
     * START GUI
     *
     * @author Marko
     */
    public class GUI extends javax.swing.JFrame {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates new form GUI
         */
        public GUI() {
            initComponents();
        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            start = new javax.swing.JButton();

            // setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
            jLabel1.setText("AIO Private Hunter");

            start.setFont(new java.awt.Font("Sakkal Majalla", 0, 36)); // NOI18N
            start.setText("Start");
            start.addActionListener(this::startActionPerformed);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup().addGap(51, 51, 51).addComponent(jLabel1))
                                    .addGroup(layout.createSequentialGroup().addGap(94, 94, 94).addComponent(start,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 153,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap(58, Short.MAX_VALUE)));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47,
                                    Short.MAX_VALUE)
                            .addComponent(start, javax.swing.GroupLayout.PREFERRED_SIZE, 52,
                                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(31, 31, 31)));

            pack();
        }// </editor-fold>

        private void startActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            GUI_COMPLETE = true;
        }

        /**
         * @param args the command line arguments
         */
        // Variables declaration - do not modify
        private javax.swing.JLabel jLabel1;
        private javax.swing.JButton start;
        // End of variables declaration
    }
    // END GUI
}

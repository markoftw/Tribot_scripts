package scripts.redDragRange;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.tribot.api.*;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.PreBreaking;

@ScriptManifest(authors = "Marko", category = "RedDrags", name = "1. Def&Range", description = "Fish guild dungeon [START LUMBY WITH CAMMY TAB]", version = 1.3)
public class redDragRange extends Script implements Painting, Ending, MessageListening07, Breaking, PreBreaking {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;

    public final int FOOD_ID = 333;
    public int FOOD_NUM = 18;

    public final int CAM_TAB = 8010;
    public final int POWERAMMY_ID = 1731; // Wear
    public final int IRON_ARROW_ID = 884;
    public final int ADAMANT_SCIM_ID = 1331;
    public final int IRON_SCIM_ID = 1323;
    public final int MITHRIL_SCIM_ID = 1329;
    public final int BOW_ID = 841; // Wield
    public final int WILLOW_BOW_ID = 849;
    public final int MAPLE_BOW_ID = 853;
    public final int YEW_BOW_ID = 857;
    public final int COIF_ID = 1169;
    private final RSTile DUNGEON_TILE = new RSTile(2624, 3390);
    public final int DUNGEON_ID = 2745; //Enter | Climb-over
    private final RSTile INSIDE_DUNGEON = new RSTile(2619, 9797);
    private final RSTile INSIDE_DUNGEON_GOBLINS = new RSTile(2585, 9832);
    private final RSTile OUTSIDE_DUNGEON = new RSTile(2623, 3391);

    private final int[] DONT_DROP = {8010, 1731, 884, 1331, 841, 849, 853, 857, 1169, 333, 1323, 1329};

    public final int[] GOBLIN_ID = {5193, 5195, 5196, 5197, 5198, 5199, 5200, 5201, 5202, 5206};
    public final int BOOTH_ID = 6943;

    public int totalHerbs = 0;
    public int totalMoney = 0;
    public boolean SET_FOOD = false;
    public boolean HOPPING_SOON = false;
    public boolean WAS_IN_COMBAT = false;
    public boolean SLEEPER = false;
    public RSPlayer[] players_1 = null;
    public RSPlayer[] players_2 = null;
    public long timeRan_1 = 0;
    public long timeRan_2 = 0;
    public RSNPC target = null;

    private final static RSArea GOBLIN_AREA = new RSArea(new RSTile[]{
        new RSTile(2577, 9841, 0), new RSTile(2600, 9841, 0),
        new RSTile(2600, 9816, 0), new RSTile(2577, 9816, 0)});

    private final static Area LUMBRIDGE_AREA = new Area(new RSTile[]{
        new RSTile(3219, 3209), new RSTile(3235, 3209),
        new RSTile(3235, 3235), new RSTile(3219, 3235)});

    private final static Area CAMMY_AREA = new Area(new RSTile[]{
        new RSTile(2750, 3482), new RSTile(2764, 3482),
        new RSTile(2764, 3471), new RSTile(2750, 3471)});

    //Vzame s banke addy scim + 5k iron arrows + navadn shortbow + lvl 20 shortbow + 40 shortbow ( mapple) + ostalo food ...
    //Gre v lukno kole s mele do 40 deff pol ranga do 20 menja bow ranga do 40 menja bow ranga do 50
    //pobiranje 3+ arrow stacks
    @Override
    public void run() {
        General.useAntiBanCompliance(true);
        this.abc_util = new ABCUtil();
        while (!stop_script) {
            switch (state()) {
                case LOGGED_IN:
                    botstatus = "Waiting for login...";
                    sleep(3000, 5000);
                    break;
                case WALKING:
                    botstatus = "Walking & hover...";
                    //hovering();
                    break;
                case WALK_TO_BANK:
                    botstatus = "Walking to bank";
                    WebWalking.walkToBank();
                    break;
                case COMBAT:
                    botstatus = "Fighting";
                    heal();
                    antibans();
                    checkSleeping();
                    break;
                case ATTACK:
                    botstatus = "Attacking goblin";
                    hoppingWorldCheck();
                    pickupArrows();
                    equipArrows();
                    checkarrows();
                    checkGear();
                    heal();
                    checkNearbyPlayers();
                    attack();
                    break;
                case GET_START_ITEMS:
                    botstatus = "Taking starting items";
                    startingItems();
                    break;
                case TELEPORT_CAMMY:
                    botstatus = "Teleporting to cammy";
                    tpCammy();
                    break;
                case NO_TAB:
                    botstatus = "No cammy tab...";
                    break;
                case WALK_TO_DUNG:
                    botstatus = "Walking to cave";
                    WebWalking.walkTo(DUNGEON_TILE);
                    break;
                case ENTER_DUNG:
                    botstatus = "Entering cave";
                    equipArrows();
                    enterDung();
                    break;
                case WALK_TO_GOBLINS:
                    botstatus = "Walking to goblins";
                    debugg("Walking to goblins: " + Player.getPosition().toString());
                    Walking.blindWalkTo(INSIDE_DUNGEON_GOBLINS);
                    break;
                case ERROR:
                    debugg("ERROR...?");
                    heal();
                    antibans();
                    break;
                case GET_FOOD:
                    botstatus = "Taking food";
                    takeFood();
                    break;
                case START_COMBAT:
                    botstatus = "Combat";
                    combatTime();
                    break;
            }
            // control cpu usage
            General.sleep(100, 250);
        }
    }

    @Override
    public void onBreakStart(long break_time) {

    }

    @Override
    public void onBreakEnd() {
        General.sleep(11000, 12000);
    }

    @Override
    public void onPreBreakStart(long break_time) {

    }

    enum State {
        LOGGED_IN,
        WALKING,
        WALK_TO_BANK,
        ATTACK,
        GET_START_ITEMS,
        TELEPORT_CAMMY,
        NO_TAB,
        WALK_TO_DUNG,
        ENTER_DUNG,
        WALK_TO_GOBLINS,
        ERROR,
        COMBAT,
        GET_FOOD,
        START_COMBAT
    }

    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Skills.getActualLevel(SKILLS.RANGED) >= 50 && Skills.getActualLevel(SKILLS.DEFENCE) >= 40) {
            debugg("40 def, 50 range finished");
            General.sleep(11000, 12000);
            Login.logout();
            stop_script = true;
        } else if (!foodExists() && isInsideGoblinArea()) {
            botstatus = "Banking";
            RSItem[] tab = Inventory.find(CAM_TAB);
            if (tab.length > 0) {
                if (tab[0].click("Break")) {
                    General.sleep(2500, 3500);
                }
            }
        } /*else if (!foodExists() && Player.getPosition().distanceTo(INSIDE_DUNGEON) < 5 && isDungNear()) {
            botstatus = "Clicking exit";
            final RSObject[] exitObjects = Objects.findNearest(5, DUNGEON_ID);
            if(exitObjects.length > 0) {
                if(exitObjects[0].click("Climb-up")) {
                    General.sleep(2500, 3500);
                }
            }
        }*/ else if (Player.getPosition().distanceTo(DUNGEON_TILE) < 6 && isDungNear() && !foodExists()) {
            return State.WALK_TO_BANK;
        /*} else if (playerInCombat() || Player.getAnimation() == 426 || Player.getAnimation() == 390 || playerIsAttacking() || playerInCombatAndNotAttacking()) {
            return State.COMBAT;*/
            /*} else if (isInsideGoblinArea()) {
            if (target != null) {
                if (ATTACKING(true)) {
                    //debugg("NULL: FALSE -- IN COMBAT");
                    return State.COMBAT;
                } else if (NOT_ATTACKING(true)) {
                    //debugg("NULL: FALSE -- ATTACK");
                    return State.ATTACK;
                }
            } else if (ATTACKING(false)) {
                //debugg("NULL: TRUE -- IN COMBAT");
                return State.COMBAT;
            } else if (NOT_ATTACKING(false)) {
                //debugg("NULL: TRUE -- ATTACK");
                return State.ATTACK;
            } else {
                return State.ERROR;
            }*/
        /*} else if (isInsideGoblinArea() && !playerInCombat() && !playerIsAttacking() && Player.getAnimation() != 426 & Player.getAnimation() != 390) {
            return State.ATTACK;*/
        } else if (isInsideGoblinArea()) {
            return State.START_COMBAT;
        } else if (isInLumbArea()) {
            if (Inventory.getCount(CAM_TAB) > 0) {
                return State.TELEPORT_CAMMY;
            } else {
                return State.NO_TAB;
            }
        } else if (isInCammyArea()) {
            return State.WALK_TO_BANK;
        } else if (Banking.isInBank() && !hasItems()) {
            return State.GET_START_ITEMS;
        } else if (Banking.isInBank() && hasItems() && !foodExists()) {
            return State.GET_FOOD;
        } else if (Banking.isInBank() && hasItems() && foodExists()) {
            return State.WALK_TO_DUNG;
        } else if (Player.getPosition().distanceTo(DUNGEON_TILE) < 6 && isDungNear() && foodExists()) {
            return State.ENTER_DUNG;
        } else if (Player.getPosition().distanceTo(INSIDE_DUNGEON) == 0) {
            return State.WALK_TO_GOBLINS;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (!isInsideGoblinArea()) {
            return State.WALK_TO_GOBLINS;
        }
        return State.ERROR;
    }

    private boolean combatTime() {
        RSNPC[] goblins = NPCs.findNearest(Filters.NPCs.nameEquals("Goblin").combine(Filters.NPCs.inArea(GOBLIN_AREA), true));
        if (!HOPPING_SOON) {
            if (Player.getRSPlayer().isInCombat() && Player.getRSPlayer().getInteractingCharacter() != null) {
                if ("Goblin".equals(Player.getRSPlayer().getInteractingCharacter().getName())) {
                    botstatus = "In combat with Goblin";
                    heal();
                    antibans();
                    checkSleeping();
                } else if ("Giant bat".equals(Player.getRSPlayer().getInteractingCharacter().getName())) {
                    botstatus = "Avoiding Bat";
                    //debugg("Avoiding Bat");
                    heal();
                    WebWalking.walkTo(INSIDE_DUNGEON_GOBLINS);
                }
            } else if (Combat.getAttackingEntities().length > 0 && target != null) {
                botstatus = "Ranging? Goblin";
                heal();
                antibans();
                checkSleeping();
            } else if (goblins.length > 0 && !Player.getRSPlayer().isInCombat()) {
                hoppingWorldCheck();
                camera();
                pickupArrows();
                equipArrows();
                checkarrows();
                checkGear();
                heal();
                checkNearbyPlayers();
                if (GOBLIN_AREA.contains(goblins[0].getPosition())) {
                    //debugg("Attacking goblin");
                    target = goblins[0];
                    if(!target.isInCombat() && target.isValid() && target.isOnScreen()) {
                        botstatus = "Attacking";
                        if(target.click("Attack")){
                            WAS_IN_COMBAT = true;
                            sleeper();
                            return Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(250, 500);
                                    return target.isInCombat();
                                }
                            }, 7500);
                        }
                    }
                } else {
                    botstatus = "Waiting for goblins";
                    debugg("Waiting for goblins");
                    return false;
                }
            } else if (Combat.getAttackingEntities().length <= 0 && Player.getRSPlayer().isInCombat() && target != null) {
                hoppingWorldCheck();
                camera();
                pickupArrows();
                equipArrows();
                checkarrows();
                checkGear();
                heal();
                checkNearbyPlayers();
                if(target.getHealth() == 0) {
                    debugg("Target is dead");
                    if (GOBLIN_AREA.contains(goblins[0].getPosition())) {
                        if(goblins[0].isValid() && goblins[0].getHealth() > 0) {
                            target = goblins[0];
                        } else if(goblins[1].isValid() && goblins[1].getHealth() > 0) {
                                target = goblins[1];
                        } else {
                            target = goblins[0];
                        }
                        if(!target.isInCombat() && target.isValid() && target.isOnScreen()) {
                            botstatus = "Attacking new NPC";
                            if(target.click("Attack")){
                                WAS_IN_COMBAT = true;
                                sleeper();
                                return Timing.waitCondition(new Condition() {
                                    @Override
                                    public boolean active() {
                                        General.sleep(250, 500);
                                        return target.isInCombat();
                                    }
                                }, 7500);
                            }
                        }
                    } else {
                        botstatus = "Waiting for goblins";
                        debugg("Waiting for goblins |2|");
                        return false;
                    }
                } else {
                    debugg("Under attack! Attempting to attack NPC back");
                    return false;
                }
            } else if (Combat.getAttackingEntities().length > 0 && Player.getRSPlayer().isInCombat()) {
                debugg("Under attack by new NPC while still attacking! Attempting to attack back");
                return false;
            } else {
                botstatus = "No match";
                Mouse.leaveGame(true);
                return false;
            }
        } else {
            hoppingWorldCheck();
        }
        return false;
    }
    
    private boolean ATTACKING(final boolean trgt) {
        if (trgt) {
            return target.isInCombat() && (playerAttackingAndNotAttacked() || Combat.isUnderAttack() || target.isInteractingWithMe() || Player.getAnimation() == 426);
        } else {
            return playerAttackingAndNotAttacked() || Combat.isUnderAttack();
        }
    }

    private boolean NOT_ATTACKING(final boolean trgt) {
        if (trgt) {
            return Combat.getAttackingEntities().length < 1 && !playerAttackingAndNotAttacked() && Player.getRSPlayer().getInteractingCharacter() == null && !target.isInteractingWithMe() && target.getHealth() == 0;
        } else {
            return Combat.getAttackingEntities().length < 1 && !playerAttackingAndNotAttacked() && Player.getRSPlayer().getInteractingCharacter() == null;
        }
    }

    public void takeFood() {
        if (Banking.isBankScreenOpen()) {
            General.sleep(450, 700);
            Banking.depositAllExcept(DONT_DROP);
            General.sleep(450, 700);
            Banking.withdraw(16, FOOD_ID);
            General.sleep(450, 700);
            Banking.withdraw(1, CAM_TAB);
            General.sleep(450, 700);
        } else {
            Banking.openBank();
        }
    }

    private void checkarrows() {
        if (Equipment.getCount(IRON_ARROW_ID) < 20) {
            debugg("Low/No arrows left, stopping");
            General.sleep(11000, 12000);
            Login.logout();
            stop_script = true;
        }
    }

    private boolean equipArrows() {
        RSItem[] arrows = Inventory.find(IRON_ARROW_ID);
        if (arrows.length > 0) {
            int stack = General.random(100, 250);
            if (arrows[0] != null && arrows[0].getStack() > stack) {
                botstatus = "Equiping looted arrows";
                if (GameTab.TABS.INVENTORY.isOpen()) {
                    if (Clicking.click("Wield", arrows[0])) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Inventory.find(IRON_ARROW_ID).length == 0;
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

    private boolean pickupArrows() {
        if (GameTab.TABS.INVENTORY.open()) {
            RSGroundItem[] arrows = GroundItems.findNearest(IRON_ARROW_ID);
            if (arrows.length > 0) {
                if (!Inventory.isFull()) {
                    for (RSGroundItem arrow : arrows) {
                        if (arrow.getStack() > 2) {
                            int currInv = Inventory.getAll().length;
                            botstatus = "Picking up arrows";
                            if (Clicking.click("Take " + arrow.getDefinition().getName(), arrow)) {
                                return Timing.waitCondition(new Condition() {
                                    @Override
                                    public boolean active() {
                                        General.sleep(250, 500);
                                        return Inventory.getAll().length > currInv;
                                    }
                                }, 2000);
                            }
                        }
                    }
                } else {
                    Inventory.dropAllExcept(DONT_DROP);
                }
            }
        } else {
            GameTab.open(GameTab.TABS.INVENTORY);
        }
        return false;
    }

    private void checkGear() {
        //if bow is in inv..and lvl highger get it
        if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.ATTACK) < 10 && Skills.getActualLevel(SKILLS.STRENGTH) == 1 && Inventory.getCount(IRON_SCIM_ID) == 1) {
            wear(POWERAMMY_ID);
            wield(IRON_SCIM_ID);
            if (Combat.getSelectedStyleIndex() != 0) {
                Combat.selectIndex(0);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.ATTACK) == 10 && Skills.getActualLevel(SKILLS.STRENGTH) < 10) {
            if (Combat.getSelectedStyleIndex() != 1) {
                Combat.selectIndex(1);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.ATTACK) < 20 && Skills.getActualLevel(SKILLS.STRENGTH) == 10) {
            if (Combat.getSelectedStyleIndex() != 0) {
                Combat.selectIndex(0);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.ATTACK) == 20 && Skills.getActualLevel(SKILLS.STRENGTH) < 20 && Inventory.getCount(MITHRIL_SCIM_ID) == 1) {
            wield(MITHRIL_SCIM_ID);
            if (Combat.getSelectedStyleIndex() != 1) {
                Combat.selectIndex(1);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.ATTACK) == 20 && Skills.getActualLevel(SKILLS.STRENGTH) == 20 && Skills.getActualLevel(SKILLS.DEFENCE) < 15) {
            if (Combat.getSelectedStyleIndex() != 3) {
                Combat.selectIndex(3);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.ATTACK) < 30 && Skills.getActualLevel(SKILLS.STRENGTH) == 20 && Skills.getActualLevel(SKILLS.DEFENCE) == 15) {
            if (Combat.getSelectedStyleIndex() != 0) {
                Combat.selectIndex(0);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.ATTACK) == 30 && Skills.getActualLevel(SKILLS.STRENGTH) < 30  && Inventory.getCount(ADAMANT_SCIM_ID) == 1) {
            wield(ADAMANT_SCIM_ID);
            if (Combat.getSelectedStyleIndex() != 1) {
                Combat.selectIndex(1);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) == 1 && Skills.getActualLevel(SKILLS.DEFENCE) < 40 && Skills.getActualLevel(SKILLS.STRENGTH) >= 30) {
            wield(ADAMANT_SCIM_ID);
            if (Combat.getSelectedStyleIndex() != 3) {
                Combat.selectIndex(3);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) < 20 && Skills.getActualLevel(SKILLS.DEFENCE) >= 40 && Inventory.getCount(BOW_ID) == 1) {
            wield(BOW_ID);
            if (Combat.getSelectedStyleIndex() != 1) {
                Combat.selectIndex(1);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) >= 20 && Skills.getActualLevel(SKILLS.RANGED) < 30 && Skills.getActualLevel(SKILLS.DEFENCE) >= 40 && Inventory.getCount(WILLOW_BOW_ID) == 1) {
            wield(WILLOW_BOW_ID);
            wear(COIF_ID);
            if (Combat.getSelectedStyleIndex() != 1) {
                Combat.selectIndex(1);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) >= 30 && Skills.getActualLevel(SKILLS.RANGED) < 40 && Skills.getActualLevel(SKILLS.DEFENCE) >= 40 && Inventory.getCount(MAPLE_BOW_ID) == 1) {
            wield(MAPLE_BOW_ID);
            if (Combat.getSelectedStyleIndex() != 1) {
                Combat.selectIndex(1);
            }
        } else if (Skills.getActualLevel(SKILLS.RANGED) >= 40 && Skills.getActualLevel(SKILLS.DEFENCE) >= 40 && Inventory.getCount(YEW_BOW_ID) == 1) {
            wield(YEW_BOW_ID);
            if (Combat.getSelectedStyleIndex() != 1) {
                Combat.selectIndex(1);
            }
        }
    }

    private boolean wield(final int ITEM_ID) {
        botstatus = "Wielding item";
        RSItem[] item = Inventory.find(ITEM_ID);
        if (item.length > 0) {
            if (item[0] != null) {
                int currInv = Inventory.getAll().length;
                if (GameTab.TABS.INVENTORY.isOpen()) {
                    General.println("Wielding: " + item[0].getDefinition().getName());
                    if (Clicking.click("Wield", item[0])) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Inventory.getAll().length < currInv;
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

    private boolean wear(final int ITEM_ID) {
        botstatus = "Wearing item";
        RSItem[] item = Inventory.find(ITEM_ID);
        if (item.length > 0) {
            if (item[0] != null) {
                int currInv = Inventory.getAll().length;
                if (GameTab.TABS.INVENTORY.isOpen()) {
                    General.println("Wearing: " + item[0].getDefinition().getName());
                    if (Clicking.click("Wear", item[0])) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Inventory.getAll().length < currInv;
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

    public void hopWorld() { //27cb+
        if(!Player.getRSPlayer().isInCombat()) {
            int world = WorldHop.getRandomWorld(true);
            General.println("[WORLDHOP] Attempting world hopping to: " + world);
            //WorldHop.hopTo(world);
            FCInGameHopper.hop(world);
            General.sleep(1000, 1500);
            HOPPING_SOON = false;
            WAS_IN_COMBAT = false;
        } else {
            General.sleep(1000, 2000);
        }
    }

    public void checkNearbyPlayers() {
        if (Player.getRSPlayer().getCombatLevel() > 26) {
            RSPlayer[] players = Players.getAll();
            if (players.length > 1) {
                for (int i = 0; i < players.length; i++) {
                    if (Player.getPosition().distanceTo(players[i].getPosition()) < 5 && !players[i].getName().equalsIgnoreCase(Player.getRSPlayer().getName())) {
                        General.println("Player nearby: " + players[i].getName());
                        HOPPING_SOON = true;
                    }
                }
            }
        }
    }

    private void hoppingWorldCheck() {
        if (HOPPING_SOON) {
            if (WAS_IN_COMBAT) {
                General.sleep(12000, 13000);
            } else {
                General.sleep(1000, 1500);
            }
            hopWorld();
        }
    }

    private boolean attack() {
        if (!HOPPING_SOON) {
            camera();
            RSNPC[] goblins = NPCs.findNearest("Goblin");
            if (goblins.length > 0 && !Player.isMoving()) {
                if (!goblins[1].isInCombat() && goblins[1].isValid() && goblins[1].isOnScreen()) {
                    if (goblins[1].click("Attack")) {
                        target = goblins[1];
                        WAS_IN_COMBAT = true;
                        sleeper();
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(200, 400);
                                return target.isInCombat();
                            }
                        }, 7000);
                    }
                } else if (Player.getPosition().distanceTo(INSIDE_DUNGEON_GOBLINS) < 3) {
                    botstatus = "Waiting for goblins...";
                } else if (!goblins[1].isOnScreen()) {
                    botstatus = "Walking to mid";
                    int x = INSIDE_DUNGEON_GOBLINS.getX() + General.random(-1, 1);
                    int y = INSIDE_DUNGEON_GOBLINS.getY() + General.random(-1, 1);
                    Walking.blindWalkTo(new RSTile(x, y));
                }
            }
        }
        return false;
    }

    public void heal() {
        int eating;
        // While current HP <= minHealth and food exists
        if (Skills.getActualLevel(SKILLS.HITPOINTS) >= 20 && Skills.getActualLevel(SKILLS.HITPOINTS) <= 40) {
            eating = 14;
        } else if (Skills.getActualLevel(SKILLS.HITPOINTS) >= 41) {
            eating = 21;
        } else {
            eating = 7;
        }
        while ((Combat.getHP() < (Skills.getActualLevel(SKILLS.HITPOINTS)) - eating) && foodExists()) {
            eat();
        }
    }

    public boolean foodExists() {
        return Inventory.find(FOOD_ID).length > 0;
    }

    public boolean eat() {
        botstatus = "Eating...";
        RSItem[] food = Inventory.find(FOOD_ID);
        if (food.length > 0) {
            if (food[0] != null) {
                if (GameTab.TABS.INVENTORY.isOpen()) {
                    if (Clicking.click("Eat", food[0])) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Combat.getHP() == Skills.getActualLevel(SKILLS.HITPOINTS);
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

    public void sleeper() {
        int x = General.random(0, 100);
        if (x > 50 && x < 57) {
            SLEEPER = true;
        }
    }

    public void checkSleeping() {
        //if (SLEEPER && Player.getRSPlayer().getInteractingCharacter().getHealth() < 15) {
        if (SLEEPER && target.getHealth() <= 5) {
            botstatus = "Sleeping..";
            Mouse.leaveGame(true);
            int x = General.random(3000, 8000);
            General.println("Antiban: Sleeping for " + x + "ms");
            General.sleep(x);
            SLEEPER = false;
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
            int rotationRNG = General.random(260, 330);
            Camera.setCameraRotation(rotationRNG);
        }*/
    }

    private void enterDung() {
        RSObject[] dung = Objects.findNearest(10, 2);
        if (dung.length > 0) {
            dung[0].click("Enter");
            General.sleep(2000, 3000);
        }
    }

    private boolean isDungNear() {
        RSObject[] dung = Objects.findNearest(6, DUNGEON_ID);
        return dung.length > 0;
    }

    private boolean hasItems() {
        return Inventory.getCount(BOW_ID) > 0;
    }

    private void tpCammy() {
        RSItem[] TAB = Inventory.find(CAM_TAB);
        if (TAB.length > 0) {
            TAB[0].click("Break");
            General.sleep(1500, 2500);
        }
    }

    private void startingItems() {
        if (Banking.isBankScreenOpen()) {
            if (Inventory.getAll().length > 0) {
                Banking.depositAll();
                General.sleep(500, 800);
            } else if (Inventory.getAll().length == 0) {
                General.sleep(600, 900);
                Banking.withdraw(1, WILLOW_BOW_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, MAPLE_BOW_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, YEW_BOW_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, ADAMANT_SCIM_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, MITHRIL_SCIM_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, IRON_SCIM_ID);
                General.sleep(600, 900);
                Banking.withdraw(4000, IRON_ARROW_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, COIF_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, POWERAMMY_ID);
                General.sleep(600, 900);
                Banking.withdraw(15, FOOD_ID);
                General.sleep(600, 900);
                Banking.withdraw(1, CAM_TAB);
                General.sleep(600, 900);
                Banking.withdraw(1, BOW_ID);
                General.sleep(1000, 1200);
            }
        } else {
            Banking.openBank();
        }
    }

    private boolean isInsideGoblinArea() {
        return GOBLIN_AREA.contains(Player.getPosition());
    }

    private boolean isInLumbArea() {
        return LUMBRIDGE_AREA.contains(Player.getPosition());
    }

    private boolean isInCammyArea() {
        return CAMMY_AREA.contains(Player.getPosition());
    }

    /* Check if the player is in combat */
    public boolean playerInCombat() {
        return Combat.getAttackingEntities().length > 0;
    }

    /* Check if the player is attacking a target */
    public boolean playerIsAttacking() {
        return Combat.isUnderAttack();
    }

    /* Check if the target is attacking the user */
    public boolean targetIsAttackingMe(RSNPC target) {
        return target.isInCombat() && target.isInteractingWithMe();
    }

    /* Check if the target is in combat, but not attacking the user */
    public boolean targetNotAttackingMe(RSNPC target) {
        return target.isInCombat() && !target.isInteractingWithMe();
    }

    /* Check if the target engaged with the user but not attacking (stuck behind something or walking toward user) */
    public boolean targetEngagedWithMe(RSNPC target) {
        return !target.isInCombat() && target.isInteractingWithMe();
    }

    /* Check if the target is not in combat and not interacting with user (free to engage) */
    public boolean targetIsNotEngaged(RSNPC target) {
        return !target.isInCombat() && !target.isInteractingWithMe();
    }

    /* Check if the user is in combat and not attacking a target */
    public boolean playerInCombatAndNotAttacking() {
        return Combat.getAttackingEntities().length > 0 && !Combat.isUnderAttack();
    }

    /* Check if the user is attacking a target and not being attacked  */
    public boolean playerAttackingAndNotAttacked() {
        return Combat.getAttackingEntities().length < 1 && Combat.isUnderAttack();
    }

    // Check if the target has died
    public boolean targetIsDead(RSNPC target) {
        return target.getHealth() == 0;
    }

    public void debugg(String msg) {
        if (debugging) {
            General.println(msg);
        }
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
            debugg("Turning run on");
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
        General.println("Total herbs collected: " + totalHerbs + ", GP: " + totalMoney);
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
    /*private final int startLvl = Skills.getActualLevel(SKILLS.HUNTER);
    private final int startXP = Skills.getXP(SKILLS.HUNTER);
    private final int gePrice = PriceChecker.getGEPrice(SALTPETRE_ID);
    private final int osPrice = PriceChecker.getOSbuddyPrice(SALTPETRE_ID);*/
    Font font = new Font("Verdana", Font.BOLD, 14);

    @Override
    public void onPaint(Graphics g) {

        Graphics2D gg = (Graphics2D) g;
        gg.drawImage(img, 0, 304, null);

        long timeRan = System.currentTimeMillis() - START_TIME;
        /*int currentLvl = Skills.getActualLevel(SKILLS.HUNTER);
        int gainedLvl = currentLvl - startLvl;
        int gainedXP = Skills.getXP(SKILLS.HUNTER) - startXP;
        int xpToLevel = Skills.getXPToNextLevel(SKILLS.HUNTER);
        int perHR = (int) ((saltpetreH * 3600000D) / timeRan);*/
        // long xpPerHour = (long)(gainedXP * 3600000 / timeRan);
        //int traps = getTrapNum();

        int moneyH = (int) ((totalMoney * 3600000D) / timeRan);
        int herbsH = (int) ((totalHerbs * 3600000D) / timeRan);

        g.setFont(font);
        //g.setColor(new Color(0, 0, 204));
        g.setColor(new Color(255, 255, 255));
        g.drawString("Status: " + botstatus, 230, 300);
        g.drawString("Runtime: " + Timing.msToString(timeRan), 230, 320);
        //g.drawString("Herbs/h: " + herbsH + " (" + moneyH + "gp/h) ", 230, 340);
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

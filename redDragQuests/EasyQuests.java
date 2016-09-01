package scripts.redDragQuests;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.tribot.api.*;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = "Marko", category = "RedDrags", name = "4. Easy quests", description = "3 easy quests [START IN LUMBRIDGE WITH INVENTORY: SPADE, BUCKET, 5+ VTAB, BANK: 60+ PURE ESS]", version = 1.1)
public class EasyQuests extends Script implements Painting, Ending {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;
    public int QUEST = 1;

    /* -  -  -  -  -  -  -  - */
    public int totalHerbs = 0;
    public int totalMoney = 0;

    private final static Area LUMBRIDGE_AREA = new Area(new RSTile[]{
        new RSTile(3219, 3209), new RSTile(3235, 3209),
        new RSTile(3235, 3235), new RSTile(3219, 3235)});

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
                    antibans();
                    break;
                case WALK_TO_BANK:
                    botstatus = "Walking to bank";
                    WebWalking.walkToBank();
                    break;
                case FINISHED:
                    debugg("We're done");
                    Login.logout();
                    stop_script = true;
                    break;
                case ERROR:
                    debugg("We've got a problem...");
                    break;
                case GHOST:
                    botstatus = "The Restless Ghost";
                    startGhost();
                    break;
                case CHICKEN:
                    botstatus = "Ernest the Chicken";
                    startChicken();
                    break;
                case PRIEST:
                    botstatus = "Priest in Peril";
                    startPeril();
                    break;
                case COMBAT:
                    botstatus = "Under attack";
                    eating();
                    break;
            }
            // control cpu usage
            General.sleep(100, 250);
        }
    }

    enum State {
        LOGGED_IN,
        WALKING,
        WALK_TO_BANK,
        FINISHED,
        ERROR,
        GHOST,
        CHICKEN,
        PRIEST,
        COMBAT
    }

    //powerwc do 35, 19 craft GE full world, agility 30 grand tree empty w
    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (Combat.isUnderAttack()) {
            return State.COMBAT;
        } else if (QUEST == 1) {
            return State.GHOST;
        } else if (QUEST == 2) {
            return State.CHICKEN;
        } else if (QUEST == 3) {
            return State.PRIEST;
        }
        return State.ERROR;
    }

    public void eating() {
        if (Combat.getHP() <= (Combat.getMaxHP() / 2)) {
            botstatus = "Eating";
            RSItem[] food = Inventory.find(333);
            if (food.length > 0) {
                if (food[0].click("Eat")) {
                    General.sleep(500, 750);
                }
            }
        }
    }

    public boolean started_peril = false; //temp true
    public boolean talked_door1 = false; //temp true
    public boolean dog_killed = false; //temp true
    public boolean talked_door2 = false; //temp true
    public boolean talked_king2 = false; //temp true

    public boolean inside_temple = false; //temp true !

    public boolean killed_30 = false; //temp true
    public boolean talked_drezel1 = false; //temp true
    public boolean talked_drezel2 = false; //temp true
    public boolean items_used = false; //temp true
    public boolean talked_drezel3 = false; //temp true

    public boolean talked_drezzy1 = false; //temp true
    public boolean talked_drezzy2 = false; //temp true
    public boolean talked_drezzy3 = false; //temp true

    private final static Area VARROCK_AREA = new Area(new RSTile[]{
        new RSTile(3200, 3440), new RSTile(3225, 3440),
        new RSTile(3225, 3419), new RSTile(3200, 3419)});
    private final static Area TEMPLE_AREA = new Area(new RSTile[]{
        new RSTile(3407, 3495), new RSTile(3419, 3495),
        new RSTile(3419, 3482), new RSTile(3407, 3482)});
    private final static Area DREZE_AREA = new Area(new RSTile[]{
        new RSTile(3416, 3495, 2), new RSTile(3419, 3495, 2),
        new RSTile(3419, 3482, 2), new RSTile(3416, 3482, 2)});

    public void startPeril() {
        final int VARROCK_TAB = 8007;
        final int PROF_ID = 3562;
        final int KING_ID = 5215;
        final int BUCKET_ID = 1925;
        final int PURE_ESS_ID = 7936;
        final RSTile king_roald = new RSTile(3223, 3480);
        final RSTile temple_tile = new RSTile(3407, 3489);
        final RSTile drezel_tile = new RSTile(3439, 9897, 0);
        final int CASTLEDOOR_ID = 11773;
        final int TEMPLEDOOR_ID = 3489;
        final int DOG_ID = 3487;
        final int MONK_ID = 3486;
        final int KEY_ID = 2944;
        final int KEY2_ID = 2945;
        final int DREZEL_ID = 3488;
        final int DREZEL_ID_NEW = 3489;
        final int CELL_DOOR_ID = 3463;
        final int MURKY_ID = 2953;
        final int BLESSED_WATER = 2954;
        final int COFFIN_ID = 3480;
        final int WOLFBANE = 2952;
        if (hasItem(WOLFBANE)) {
            RSItem[] tab = Inventory.find(VARROCK_TAB);
            if (tab[0].click("Break")) {
                General.sleep(4500, 5500);
            }
            debugg("Priest in Peril: Quest completed!");
            Login.logout();
            stop_script = true;
        } else if (!started_peril) {
            RSNPC[] king = NPCs.findNearest(KING_ID);
            if (isNPCnear(PROF_ID) && hasItem(VARROCK_TAB)) {
                RSItem[] tab = Inventory.find(VARROCK_TAB);
                if (tab.length > 0) {
                    botstatus = "Teleporting to Varrock";
                    if (tab[0].click("Break")) {
                        General.sleep(3500, 4500);
                    }
                }
            } else if (isInsideVarrock()) {
                botstatus = "Walking to Castle";
                WebWalking.walkTo(king_roald);
            } else if (isObjectNear(CASTLEDOOR_ID)) {
                botstatus = "Opening door";
                RSObject[] door = Objects.find(6, CASTLEDOOR_ID);
                if (door.length > 0) {
                    if (door[0].click("Open")) {
                        General.sleep(2500, 3500);
                    }
                }
            } else if (king.length > 0) {
                if (king[0].isClickable() && king[0].isOnScreen() && king[0].isValid()) {
                    botstatus = "Talking to King Roald";
                    if (king[0].click("Talk-to")) {
                        General.sleep(2500, 3500);
                        talkToKing1();
                    }
                } else {
                    botstatus = "Walking to King";
                    Walking.walkTo(king[0].getPosition());
                }
            }
        } else if (started_peril && !talked_door1) {
            if (isNPCnear(KING_ID)) {
                botstatus = "Walking to bank";
                WebWalking.walkToBank();
            } else if (Banking.isInBank()) {
                if (!hasItem(BUCKET_ID)) {
                    if (Banking.isBankScreenOpen()) {
                        botstatus = "Getting items";
                        Banking.withdraw(1, BUCKET_ID);
                        General.sleep(350, 650);
                    } else {
                        botstatus = "Opening bank";
                        Banking.openBank();
                    }
                } else {
                    botstatus = "Walking to temple";
                    WebWalking.walkTo(temple_tile);
                }
            } else if (Player.getPosition().distanceTo(temple_tile) < 6 && isObjectNear(TEMPLEDOOR_ID)) {
                botstatus = "Knocking on doors";
                RSObject[] doors = Objects.find(6, TEMPLEDOOR_ID);
                if (doors.length > 0) {
                    if (doors[0].click("Knock-at")) {
                        General.sleep(350, 650);
                        knockOnDoor1();
                    }
                }
            }
        } else if (started_peril && talked_door1 && !dog_killed) {
            if (Player.getPosition().distanceTo(new RSTile(3408, 3489)) < 2) {
                botstatus = "Walking to trap door";
                //Walking.blindWalkTo(new RSTile(3405, 3505));
                WebWalking.walkTo(new RSTile(3405, 9905, 0));
            } else if (isNPCnear(DOG_ID)) {
                botstatus = "Attacking dog";
                RSNPC[] dog = NPCs.findNearest(DOG_ID);
                if (dog.length > 0 && !dog[0].isInCombat()) {
                    if (dog[0].click("Attack")) {
                        dog_killed = true;
                        General.sleep(2500, 3500);
                    }
                }
            }
        } else if (started_peril && talked_door1 && dog_killed && !talked_king2) {
            if (isNPCnear(DOG_ID)) {
                botstatus = "Walking back to temple";
                WebWalking.walkTo(temple_tile);
            } else if (Player.getPosition().distanceTo(temple_tile) < 6 && isObjectNear(TEMPLEDOOR_ID)) {
                if (!talked_door2) {
                    botstatus = "Knocking on doors 2";
                    RSObject[] doors = Objects.find(6, TEMPLEDOOR_ID);
                    if (doors.length > 0) {
                        if (doors[0].click("Knock-at")) {
                            General.sleep(350, 650);
                            knockOnDoor2();
                        }
                    }
                } else if (talked_door2 && hasItem(VARROCK_TAB)) {
                    botstatus = "Teleporting to Varrock";
                    RSItem[] tab = Inventory.find(VARROCK_TAB);
                    if (tab.length > 0) {
                        if (tab[0].click("Break")) {
                            General.sleep(3500, 4500);
                        }
                    }
                }
            } else if (talked_door2 && isInsideVarrock()) {
                botstatus = "Walking to King roald";
                WebWalking.walkTo(king_roald);
            } else if (talked_door2 && isNPCnear(KING_ID)) {
                RSNPC[] king = NPCs.findNearest(KING_ID);
                if (isObjectNear(CASTLEDOOR_ID)) {
                    botstatus = "Opening door";
                    RSObject[] door = Objects.find(6, CASTLEDOOR_ID);
                    if (door.length > 0) {
                        if (door[0].click("Open")) {
                            General.sleep(2500, 3500);
                        }
                    }
                } else if (king[0].isClickable() && king[0].isOnScreen() && king[0].isValid()) {
                    botstatus = "Talking to King Roald";
                    if (king[0].click("Talk-to")) {
                        General.sleep(2500, 3500);
                        talkToKing2(); //12x .. 8?
                    }
                } else {
                    botstatus = "Walking to King";
                    WebWalking.walkTo(king[0].getPosition());
                }
            }
        } else if (started_peril && talked_door1 && dog_killed && talked_king2 && !talked_drezel1) {
            if (isNPCnear(KING_ID)) {
                botstatus = "Walking back to temple";
                //WebWalking.walkTo(temple_tile);
                WebWalking.walkToBank();
            } else if (Banking.isInBank()) {
                botstatus = "Walking back to temple from bank";
                WebWalking.walkTo(temple_tile);
            } else if (Player.getPosition().distanceTo(temple_tile) < 6 && isObjectNear(TEMPLEDOOR_ID) && !inside_temple) {
                RSObject[] doors = Objects.find(6, TEMPLEDOOR_ID);
                botstatus = "Opening temple door";
                if (doors.length > 0) {
                    if (doors[0].click("Open")) {
                        inside_temple = true;
                        General.sleep(950, 1650);
                    }
                }
            } else if (inside_temple && !killed_30) {
                botstatus = "Killing monk for key";
                RSNPC[] monk = NPCs.findNearest(MONK_ID);
                if (monk.length > 0) {
                    if (monk[0].isClickable() && monk[0].isValid() && monk[0].isOnScreen() && !monk[0].isInCombat()) {
                        if (monk[0].click("Attack")) {
                            killed_30 = true;
                            General.sleep(4000, 5000);
                        }
                    } else {
                        Walking.walkTo(monk[0].getPosition());
                    }
                }
            } else if (inside_temple && killed_30 && !hasItem(KEY_ID) && isGroundObjectNear(KEY_ID)) {
                botstatus = "Picking up key";
                RSGroundItem[] key = GroundItems.findNearest(KEY_ID);
                if (key.length > 0) {
                    if (key[0].click("Take")) {
                        General.sleep(1000, 1300);
                    }
                }
            } else if (inside_temple && killed_30 && hasItem(KEY_ID)) {
                RSNPC[] drezel = NPCs.findNearest(DREZEL_ID);
                if (isInsideTemple() && !isObjectNear(16673) && !isObjectNear(16683) && drezel.length <= 0) {
                    RSObject[] stairs = Objects.findNearest(30, 16671);
                    if (stairs.length > 0) {
                        botstatus = "Climbing stairs";
                        if (stairs[0].isOnScreen() && stairs[0].isClickable()) {
                            if (stairs[0].click("Climb-up")) {
                                General.sleep(1500, 2500);
                            }
                        } else {
                            botstatus = "Walking to stairs";
                            Walking.walkTo(stairs[0].getPosition());
                        }
                    }
                } else if (isObjectNear(16673)) {
                    botstatus = "Walking to ladder";
                    Walking.walkTo(new RSTile(3409, 3484, 1));
                } else if (isObjectNear(16683)) {
                    RSObject[] ladder = Objects.findNearest(5, 16683);
                    if (ladder.length > 0) {
                        botstatus = "Climbing ladder";
                        if (ladder[0].isOnScreen() && ladder[0].isClickable()) {
                            if (ladder[0].click("Climb-up")) {
                                General.sleep(1500, 2500);
                            }
                        }
                    }
                } else if (drezel.length > 0 && Player.getPosition().distanceTo(new RSTile(3415, 3489, 2)) > 0) {
                    botstatus = "Walking to door";
                    Walking.walkTo(new RSTile(3415, 3489, 2));
                } else if (drezel.length > 0 && Player.getPosition().distanceTo(new RSTile(3415, 3489, 2)) == 0) {
                    botstatus = "Talking to Drezel";
                    RSObject[] celldoor = Objects.findNearest(5, CELL_DOOR_ID);
                    if (celldoor.length > 0) {
                        if (celldoor[0].click("Talk-through")) {
                            General.sleep(500, 750);
                            talkToDrezel1();
                        }
                    }
                }
            }
        } else if (started_peril && talked_door1 && dog_killed && talked_king2 && talked_drezel1 && !items_used && !talked_drezel3) {
            if (inside_temple && !hasItem(KEY2_ID) && !hasItem(MURKY_ID) && !hasItem(BLESSED_WATER)) {
                RSObject[] ladder = Objects.findNearest(15, 16679);
                RSObject[] stairs = Objects.findNearest(25, 16673);
                RSObject[] doors = Objects.findNearest(25, TEMPLEDOOR_ID);
                if (ladder.length > 0) {
                    botstatus = "Climbing Ladder";
                    if (ladder[0].isOnScreen() && ladder[0].isClickable()) {
                        if (ladder[0].click("Climb-down")) {
                            General.sleep(1500, 2500);
                        }
                    } else {
                        botstatus = "Walking to ladder.";
                        Walking.walkTo(ladder[0].getPosition());
                    }
                } else if (stairs.length > 0) {
                    botstatus = "Climbing stairs";
                    if (stairs[0].isOnScreen() && stairs[0].isClickable()) {
                        if (stairs[0].click("Climb-down")) {
                            General.sleep(1500, 2500);
                        }
                    } else {
                        botstatus = "Walking to stairs";
                        Walking.walkTo(stairs[0].getPosition());
                    }
                } else if (doors.length > 0) {
                    botstatus = "Opening doors";
                    if (doors[0].isOnScreen() && doors[0].isClickable()) {
                        if (doors[0].click("Open")) {
                            inside_temple = false;
                            General.sleep(1500, 2500);
                        }
                    } else {
                        botstatus = "Walking to doors";
                        Walking.walkTo(doors[0].getPosition());
                    }
                }
            } else if (!inside_temple) {
                RSObject[] monument = Objects.findNearest(6, 3494);
                RSObject[] well = Objects.findNearest(15, 3485);
                RSItem[] key = Inventory.find(KEY_ID);
                RSItem[] bucket = Inventory.find(BUCKET_ID);
                if (hasItem(KEY_ID) && Player.getPosition().distanceTo(new RSTile(3423, 9892, 0)) > 7) {
                    botstatus = "Walking to swap keys";
                    WebWalking.walkTo(new RSTile(3423, 9892, 0));
                } else if (monument.length > 0 && key.length > 0) {
                    botstatus = "Swapping keys";
                    if (key[0].click("Use")) {
                        General.sleep(500, 750);
                        if (monument[0].click("Use Golden key")) {
                            General.sleep(3500, 4550);
                        }
                    }
                } else if (hasItem(KEY2_ID) && bucket.length > 0 && well.length > 0) {
                    if (well[0].isClickable() && well[0].isOnScreen()) {
                        botstatus = "Grabbing Murky water";
                        if (bucket[0].click("Use")) {
                            General.sleep(250, 500);
                            if (well[0].click("Use Bucket")) {
                                General.sleep(4500, 5500);
                            }
                        }
                    } else {
                        botstatus = "Walking to Well";
                        Walking.walkTo(well[0].getPosition());
                    }
                } else if (hasItem(KEY2_ID) && hasItem(MURKY_ID) && Player.getPosition().distanceTo(temple_tile) > 6) {
                    botstatus = "Walking back to Temple";
                    WebWalking.walkTo(temple_tile);
                } else if (hasItem(KEY2_ID) && hasItem(MURKY_ID) && Player.getPosition().distanceTo(temple_tile) <= 6 && isObjectNear(TEMPLEDOOR_ID)) {
                    RSObject[] doors = Objects.find(6, TEMPLEDOOR_ID);
                    botstatus = "Opening temple door";
                    if (doors.length > 0) {
                        if (doors[0].click("Open")) {
                            inside_temple = true;
                            General.sleep(950, 1650);
                        }
                    }
                }
            } else if (inside_temple && hasItem(KEY2_ID) && hasItem(MURKY_ID)) {
                RSObject[] ladder = Objects.findNearest(15, 16683);
                RSObject[] stairs = Objects.findNearest(25, 16671);
                RSObject[] door = Objects.findNearest(9, 3463);
                RSItem[] key = Inventory.find(KEY2_ID);
                if (ladder.length > 0) {
                    botstatus = "Climbing Ladder";
                    if (ladder[0].isOnScreen() && ladder[0].isClickable()) {
                        if (ladder[0].click("Climb-up")) {
                            General.sleep(1500, 2500);
                        }
                    } else {
                        botstatus = "Walking to ladder";
                        Walking.walkTo(ladder[0].getPosition());
                    }
                } else if (stairs.length > 0) {
                    botstatus = "Climbing stairs";
                    if (stairs[0].isOnScreen() && stairs[0].isClickable()) {
                        if (stairs[0].click("Climb-up")) {
                            General.sleep(2900, 3500);
                            botstatus = "Walking to Drezel door";
                            Walking.walkTo(new RSTile(3415, 3489, 2));
                        }
                    } else {
                        botstatus = "Walking to stairs";
                        Walking.walkTo(stairs[0].getPosition());
                    }
                } else if (Player.getPosition().distanceTo(new RSTile(3415, 3489, 2)) == 0 && key.length > 0 && door.length > 0) {
                    botstatus = "Using key on door";
                    if (key[0].click("Use")) {
                        General.sleep(350, 650);
                        if (door[0].click("Use Iron key")) {
                            General.sleep(1350, 1650);
                        }
                    }
                } else if (Player.getPosition().distanceTo(new RSTile(3410, 3484, 2)) == 1) {
                    botstatus = "Walking to Drezel door";
                    Walking.walkTo(new RSTile(3415, 3489, 2));
                }
            } else if (inside_temple && !hasItem(KEY2_ID) && hasItem(MURKY_ID)) {
                RSObject[] door = Objects.findNearest(5, 3463);
                RSNPC[] drezel = NPCs.findNearest(DREZEL_ID);
                if (Player.getPosition().distanceTo(new RSTile(3415, 3489, 2)) == 0 && door.length > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(1500, 2500);
                    }
                } else if (Player.getPosition().distanceTo(new RSTile(3415, 3489, 2)) > 0 && drezel.length > 0) {
                    botstatus = "Talking do Drezel";
                    if (drezel[0].click("Talk-to")) {
                        General.sleep(350, 650);
                        if (NPCChat.getClickContinueInterface() != null) {
                            for (int j = 0; j < 4; j++) {
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                }
                            }
                        }
                    }
                }
            } else if (inside_temple && !hasItem(KEY2_ID) && !hasItem(MURKY_ID) && hasItem(BLESSED_WATER)) {
                botstatus = "water stuff #5";
                RSObject[] door = Objects.findNearest(8, 3463);
                RSObject[] coffin = Objects.findNearest(8, COFFIN_ID);
                RSItem[] water = Inventory.find(BLESSED_WATER);
                if (door.length > 0 && isInsideDrezel()) {
                    if (door[0].isClickable() && door[0].isOnScreen()) {
                        botstatus = "Opening door";
                        if (door[0].click("Open")) {
                            General.sleep(1250, 1600);
                        }
                    } else {
                        botstatus = "Walking to door";
                        Walking.walkTo(door[0].getPosition());
                    }
                } else if (coffin.length > 0 && water.length > 0 && !isInsideDrezel()) {
                    if (coffin[0].isClickable() && coffin[0].isOnScreen() && !Player.isMoving()) {
                        botstatus = "Using water on coffin";
                        if (water[0].click("Use")) {
                            General.sleep(250, 450);
                            if (coffin[0].click("Use Blessed water")) {
                                items_used = true;
                            }
                        }
                    } else {
                        botstatus = "Walking to coffin";
                        Walking.walkTo(coffin[0].getPosition());
                    }
                }
            }
        } else if (started_peril && talked_door1 && dog_killed && talked_king2 && talked_drezel2 && items_used && !talked_drezel3) {
            RSObject[] door = Objects.findNearest(8, 3463);
            RSNPC[] drezel = NPCs.findNearest(DREZEL_ID);
            if (!isInsideDrezel() && door.length > 0) {
                if (door[0].isClickable() && door[0].isOnScreen()) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(1250, 1600);
                    }
                } else {
                    botstatus = "Walking to door";
                    Walking.walkTo(door[0].getPosition());
                }
            } else if (isInsideDrezel() && drezel.length > 0) {
                botstatus = "Talking do Drezel";
                if (drezel[0].click("Talk-to")) {
                    General.sleep(350, 650);
                    if (NPCChat.getClickContinueInterface() != null) {
                        for (int j = 0; j < 3; j++) {
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                            }
                            if (j >= 2) {
                                talked_drezel3 = true;
                            }
                        }
                    }
                }
            }
        } else if (started_peril && talked_door1 && dog_killed && talked_king2 && talked_drezel2 && items_used && talked_drezel3) {
            RSItem[] tab = Inventory.find(VARROCK_TAB);
            if (isInsideDrezel() && tab.length > 0) {
                botstatus = "Teleporting to Varrock";
                if (tab[0].click("Break")) {
                    inside_temple = false;
                    General.sleep(4500, 5500);
                }
            } else if (isInsideVarrock() && Inventory.getCount(PURE_ESS_ID) < 20) {
                botstatus = "Walking to bank";
                WebWalking.walkToBank();
            } else if (Banking.isInBank() && Inventory.getCount(PURE_ESS_ID) < 20) {
                botstatus = "Grabbing items";
                if (Banking.isBankScreenOpen()) {
                    Banking.depositAll();
                    General.sleep(300, 600);
                    if (!hasItem(VARROCK_TAB)) {
                        Banking.withdraw(1, VARROCK_TAB);
                        General.sleep(300, 600);
                    }
                    if (!hasItem(PURE_ESS_ID)) {
                        Banking.withdraw(25, PURE_ESS_ID);
                        General.sleep(300, 600);
                    }
                } else {
                    Banking.openBank();
                }
            } else if (Banking.isInBank() && Inventory.getCount(PURE_ESS_ID) > 20) {
                botstatus = "Walking to Drezel";
                WebWalking.walkTo(drezel_tile);
            } else if (isNPCnear(DREZEL_ID_NEW)) {
                RSNPC[] drezel = NPCs.findNearest(DREZEL_ID_NEW);
                botstatus = "Talking to Drezel";
                if (!talked_drezzy1) {
                    botstatus = "Talking #1";
                    if (drezel.length > 0) {
                        if (drezel[0].click("Talk-to")) {
                            General.sleep(2500, 3500);
                            for (int i = 0; i < 17; i++) {
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                }
                                if (i >= 16) {
                                    talked_drezzy1 = true;
                                }
                            }
                        }
                    }
                } else if (talked_drezzy1 && !talked_drezel2) {
                    botstatus = "Talking #2";
                    if (Inventory.getCount(PURE_ESS_ID) > 20) {
                        if (drezel.length > 0) {
                            if (drezel[0].click("Talk-to")) {
                                General.sleep(2500, 3500);
                                if (NPCChat.getClickContinueInterface() != null) {
                                    for (int i = 0; i < 2; i++) {
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(500, 650);
                                        }
                                        if (i >= 1) {
                                            talked_drezzy2 = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (talked_drezzy1 && talked_drezel2 && !talked_drezzy3) {
                    botstatus = "Talking #3";
                    if (Inventory.getCount(PURE_ESS_ID) > 20) {
                        if (drezel.length > 0) {
                            if (drezel[0].click("Talk-to")) {
                                General.sleep(2500, 3500);
                                if (NPCChat.getClickContinueInterface() != null) {
                                    for (int i = 0; i < 5; i++) {
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(500, 650);
                                        }
                                        if (i >= 5) {
                                            talked_drezzy3 = true;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (tab.length > 0 && Inventory.getCount(PURE_ESS_ID) < 20) {
                        botstatus = "Teleporting to Varrock";
                        if (tab[0].click("Break")) {
                            inside_temple = false;
                            General.sleep(4500, 5500);
                        }
                    }
                }
            }
        }
    }

    public void talkToDrezel1() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                        if (NPCChat.selectOption("Tell me anyway.", true)) { //24x
                                            sleep(500, 650);
                                            for (int i = 0; i < 24; i++) {
                                                if (NPCChat.clickContinue(true)) {
                                                    sleep(500, 650);
                                                }
                                                if (i >= 23) {
                                                    if (NPCChat.selectOption("Yes.", true)) { //14x
                                                        sleep(500, 650);
                                                        for (int j = 0; j < 14; j++) {
                                                            if (NPCChat.clickContinue(true)) {
                                                                sleep(500, 650);
                                                            }
                                                            if (j >= 13) {
                                                                talked_drezel1 = true;
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

    public void talkToKing2() {//12 or 8
        if (NPCChat.getClickContinueInterface() != null) {
            for (int i = 0; i < 12; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 11) {
                    talked_king2 = true;
                }
            }
        }
    }

    public void knockOnDoor2() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                talked_door2 = true;
                                sleep(500, 650);
                            }
                        }
                    }
                }
            }
        }
    }

    public void knockOnDoor1() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                    if (NPCChat.selectOption("Roald sent me to check on Drezel.", true)) { //6x
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(500, 650);
                                            if (NPCChat.clickContinue(true)) {
                                                sleep(500, 650);
                                                if (NPCChat.selectOption("Sure.", true)) { //3x
                                                    sleep(500, 650);
                                                    if (NPCChat.clickContinue(true)) {
                                                        sleep(500, 650);
                                                        if (NPCChat.clickContinue(true)) {
                                                            sleep(500, 650);
                                                            if (NPCChat.clickContinue(true)) {
                                                                talked_door1 = true;
                                                                sleep(500, 650);
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

    public void talkToKing1() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(500, 650);
                                            if (NPCChat.selectOption("Sure.", true)) {
                                                sleep(500, 650);
                                                if (NPCChat.clickContinue(true)) {
                                                    sleep(500, 650);
                                                    if (NPCChat.clickContinue(true)) {
                                                        started_peril = true;
                                                        sleep(500, 650);
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

    public boolean started_chicken = false; //temp true
    public boolean inside_castle = false; //temp true
    public boolean inside_castle_again = false; //temp true
    public boolean talked_to_prof1 = false; //temp true
    private final static Area ERNEST_AREA = new Area(new RSTile[]{
        new RSTile(3089, 9770), new RSTile(3119, 9770),
        new RSTile(3119, 9744), new RSTile(3089, 9744)});
    private final static Area LEVER_AREA = new Area(new RSTile[]{
        new RSTile(3090, 3364), new RSTile(3097, 3364),
        new RSTile(3097, 3354), new RSTile(3090, 3354)});

    public boolean pulledE = false, pulledF = false, pulledC = false;
    public boolean step1 = false, step2 = false, step3 = false, step4 = false, step5 = false, step6 = false, step7 = false, step8 = false;

    public void startChicken() {

        final RSTile[] TO_DUMPSTER = {new RSTile(3123, 3361), new RSTile(3123, 3370), new RSTile(3120, 3376), new RSTile(3113, 3378), new RSTile(3103, 3380), new RSTile(3096, 3378),
            new RSTile(3090, 3372), new RSTile(3089, 3364), new RSTile(3087, 3361)};

        final int SPADE_ID = 952;
        final int LADY_ID = 3561;
        final int LARGE_DOOR_ID = 11542;
        final int DOOR1 = 11470;
        final int DOOR2 = 136;
        final int STAIRS1 = 11498;
        final int STAIRS2 = 11511;
        final int STAIRS1_DOWN = 11499;
        final int STAIRS2_DOWN = 9584;
        final int PROF_ID = 3562;
        final int BOOKCASE_ID = 156;
        final int LADDER_DOWN = 133;
        final int OILCAN_ID = 277;
        final int PRESSURE_ID = 271;
        final int POISON_ID = 273;
        final int RUBBER_ID = 276;
        final int KEY_ID = 275;
        final int FISHFOOD_ID = 272;
        final int POIS_FISH_ID = 274;
        final int COMPOST_ID = 152;
        final int FOUNTAIN_ID = 153;
        final int ERNEST_ID = 6142;
        final RSTile ghost_house = new RSTile(3246, 3193, 0);
        final RSTile lady_tile = new RSTile(3110, 3331, 0);
        final RSTile larde_door_tile = new RSTile(3109, 3353, 0);
        final RSTile bookcase_tile = new RSTile(3098, 3359, 0);
        RSInterface questbox = Interfaces.get(277, 2);

        if (questbox != null) {
            RSInterface closebox = Interfaces.get(277, 15);
            if (closebox != null) {
                closebox.click("Close");
                General.println("Ernest the Chicken: Quest completed!");
                QUEST = 3;
            }
        } else if (Player.getPosition().distanceTo(ghost_house) < 10 && !started_chicken && hasItem(SPADE_ID)) {
            camera();
            botstatus = "Finished Q1, starting Q2";
            debugg("Quest 1 finished, starting Quest 2");
            WebWalking.walkTo(lady_tile);
        } else if (!hasItem(OILCAN_ID) && !isInsideErnest()) {
            if (Player.getPosition().distanceTo(lady_tile) < 8 && isNPCnear(LADY_ID) && !started_chicken) {
                RSNPC[] npcs = NPCs.findNearest(LADY_ID);
                if (npcs.length > 0) {
                    if (npcs[0].isClickable() && npcs[0].isOnScreen() && npcs[0].isValid()) {
                        botstatus = "Talking to Veronica";
                        npcs[0].click("Talk-to");
                        General.sleep(500, 800);
                        if (NPCChat.getClickContinueInterface() != null) {
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.selectOption("Aha, sounds like a quest. I'll help.", true)) {
                                    sleep(500, 650);
                                    for (int i = 0; i < 7; i++) {
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(500, 650);
                                        }
                                        if (i >= 5) {
                                            started_chicken = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (Player.getPosition().distanceTo(lady_tile) < 3 && isNPCnear(LADY_ID) && started_chicken) {
                botstatus = "Walking to castle";
                Walking.blindWalkTo(larde_door_tile);
            } else if (Player.getPosition().distanceTo(larde_door_tile) < 3 && started_chicken && isObjectNear(LARGE_DOOR_ID) && !inside_castle) {
                botstatus = "Opening door";
                if (Camera.getCameraRotation() < 150 || Camera.getCameraRotation() > 200) {
                    botstatus = "Rotating camera for doors";
                    int rotationRNG = General.random(150, 200);
                    Camera.setCameraRotation(rotationRNG);
                } else {
                    RSObject[] doors = Objects.find(6, LARGE_DOOR_ID);
                    if (doors.length > 0) {
                        if (doors[0].click("Open")) {
                            inside_castle = true;
                            General.sleep(250, 500);
                        }
                    }
                }
            } else if (Player.getPosition().distanceTo(larde_door_tile) < 8 && started_chicken && isObjectNear(LARGE_DOOR_ID) && inside_castle) {
                botstatus = "We're inside";
                camera();
                RSObject[] door = Objects.find(6, DOOR1);
                RSObject[] stairs = Objects.find(6, STAIRS1);
                if (door.length > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(1500, 2500);
                    }
                }
                if (stairs.length > 0) {
                    botstatus = "Climbing upstairs";
                    if (stairs[0].click("Climb-up")) {
                        General.sleep(2500, 3000);
                    }
                }
                Walking.walkTo(new RSTile(3108, 3361));
            } else if (started_chicken && isObjectNear(STAIRS2) && inside_castle) {
                RSObject[] stairs = Objects.find(6, STAIRS2);
                RSObject[] stairsdown = Objects.find(6, STAIRS1_DOWN);
                if (!talked_to_prof1) {
                    if (stairs.length > 0) {
                        botstatus = "Climbing upstairs 2";
                        if (stairs[0].click("Climb-up")) {
                            General.sleep(2000, 2500);
                        }
                    }
                } else if (stairsdown.length > 0) {
                    botstatus = "Climbing downstairs 2";
                    if (stairsdown[0].click("Climb-down")) {
                        General.sleep(2000, 2500);
                    }
                }
            } else if (started_chicken && isNPCnear(PROF_ID) && inside_castle) {
                RSObject[] door = Objects.find(6, DOOR1);
                RSObject[] stairs = Objects.findNearest(9, STAIRS2_DOWN);
                RSNPC[] prof = NPCs.findNearest(9, PROF_ID);
                if (door.length > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(2000, 2500);
                    }
                }
                if (!talked_to_prof1) {
                    if (prof.length > 0) {
                        if (prof[0].isClickable() && prof[0].isOnScreen() && prof[0].isValid()) {
                            botstatus = "Talking to Professor";
                            prof[0].click("Talk-to");
                            General.sleep(2000, 2500);
                            talkToProf1();
                        } else {
                            Walking.walkTo(prof[0].getPosition());
                        }
                    }
                } else if (stairs.length > 0) {
                    if (stairs[0].isClickable() && stairs[0].isOnScreen()) {
                        botstatus = "Walking downstairs";
                        stairs[0].click("Climb-down");
                        General.sleep(2000, 2500);
                        talkToProf1();
                    } else {
                        Walking.walkTo(stairs[0].getPosition());
                    }
                }
            } else if (started_chicken && inside_castle && talked_to_prof1 && Player.getPosition().distanceTo(bookcase_tile) > 2 && !isInsideErnest()) {
                botstatus = "Walking to bookcase";
                if (Player.getPosition().distanceTo(new RSTile(3108, 3361)) == 0 || Player.getPosition().distanceTo(new RSTile(3109, 3361)) == 0) {
                    Walking.walkTo(new RSTile(3106, 3368));
                } else {
                    Walking.walkTo(bookcase_tile);
                }
                RSObject[] door = Objects.find(2, DOOR1);
                if (door.length > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(500, 750);
                    }
                }
            } else if (started_chicken && inside_castle && talked_to_prof1 && Player.getPosition().distanceTo(bookcase_tile) < 2) {
                botstatus = "Opening bookcase";
                RSObject[] bookcase = Objects.find(6, BOOKCASE_ID);
                if (bookcase.length > 0) {
                    if (bookcase[0].click("Search")) {
                        General.sleep(2000, 2500);
                    }
                }
            } else if (started_chicken && inside_castle && talked_to_prof1 && (Player.getPosition().distanceTo(new RSTile(3096, 3359)) == 0 || Player.getPosition().distanceTo(new RSTile(3096, 3358)) == 0)) {
                botstatus = "Climbing down ladder";
                RSObject[] ladder = Objects.find(6, LADDER_DOWN);
                if (ladder.length > 0) {
                    if (ladder[0].click("Climb-down")) {
                        General.sleep(2000, 2500);
                    }
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && isInsideErnest()) {
            botstatus = "Solving maze";
            solveMaze();
        } else if (started_chicken && inside_castle && talked_to_prof1 && isInsideLevers() && hasItem(OILCAN_ID)) {
            botstatus = "Pulling lever";
            RSObject[] lever = Objects.findNearest(10, 160);
            if (lever.length > 0) {
                if (lever[0].click("Pull")) {
                    General.sleep(6500, 7500);
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && !hasItem(POISON_ID) && !hasItem(POIS_FISH_ID) && !hasItem(PRESSURE_ID) && !hasItem(KEY_ID)) {
            RSObject[] door = Objects.findNearest(2, DOOR1);
            RSGroundItem[] poison = GroundItems.findNearest(4, POISON_ID);
            if (Player.getPosition().distanceTo(new RSTile(3098, 3358)) == 0) {
                botstatus = "Walking to door 1";
                Walking.walkTo(new RSTile(3103, 3363));
            } else if (door.length > 0) {
                botstatus = "Opening door";
                if (door[0].click("Open")) {
                    General.sleep(1200, 1600);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3103, 3363)) < 3) {
                botstatus = "Walking to door 2";
                Walking.walkTo(new RSTile(3102, 3371));
            } else if (Player.getPosition().distanceTo(new RSTile(3102, 3371)) < 3) {
                botstatus = "Walking to door 3";
                Walking.walkTo(new RSTile(3099, 3367));
            } else if (poison.length > 0) {
                botstatus = "Taking poison";
                if (poison[0].click("Take")) {
                    General.sleep(1200, 1600);
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && hasItem(POISON_ID) && !hasItem(FISHFOOD_ID) && !hasItem(POIS_FISH_ID)) {
            RSObject[] door = Objects.findNearest(2, DOOR1);
            RSObject[] stairs = Objects.findNearest(2, STAIRS1);
            RSGroundItem[] fishfood = GroundItems.findNearest(5, FISHFOOD_ID);
            if (door.length > 0) {
                botstatus = "Opening door";
                if (door[0].click("Open")) {
                    General.sleep(1200, 1600);
                }
            } else if (stairs.length > 0) {
                botstatus = "Climbing up stairs";
                if (stairs[0].click("Climb-up")) {
                    General.sleep(2500, 3500);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3108, 3366)) == 0 || Player.getPosition().distanceTo(new RSTile(3109, 3366)) == 0) {
                botstatus = "Walking to door 1";
                Walking.walkTo(new RSTile(3113, 3367, 1));
            } else if (Player.getPosition().distanceTo(new RSTile(3113, 3367, 1)) < 2) {
                botstatus = "Walking to door 2";
                Walking.walkTo(new RSTile(3116, 3362, 1));
            } else if (Player.getPosition().distanceTo(new RSTile(3116, 3362, 1)) < 2) {
                botstatus = "Walking to door 3";
                Walking.walkTo(new RSTile(3111, 3358, 1));
            } else if (fishfood.length > 0) {
                botstatus = "Picking up fish food";
                if (fishfood[0].click("Take")) {
                    General.sleep(2500, 3500);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3097, 3366)) == 0) {
                botstatus = "Walking to door tile";
                Walking.walkTo(new RSTile(3106, 3369, 0));
            } else {
                botstatus = "Walking to stairs";
                Walking.walkTo(new RSTile(3108, 3361, 0));
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && hasItem(POISON_ID) && hasItem(FISHFOOD_ID)) {
            RSItem[] poison = Inventory.find(POISON_ID);
            RSItem[] fishfood = Inventory.find(FISHFOOD_ID);
            if (poison.length > 0 && fishfood.length > 0) {
                botstatus = "Making Poisoned fish food";
                if (poison[0].click("Use")) {
                    General.sleep(250, 500);
                    if (fishfood[0].click("Use Poison")) {
                        General.sleep(250, 500);
                    }
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && hasItem(POIS_FISH_ID) && !hasItem(KEY_ID)) {
            RSObject[] stairs = Objects.findNearest(2, STAIRS1_DOWN);
            RSObject[] doors = Objects.findNearest(2, DOOR1);
            RSObject[] doors2 = Objects.findNearest(2, DOOR2);
            RSObject[] compost = Objects.findNearest(5, COMPOST_ID);
            RSItem[] spade = Inventory.find(SPADE_ID);
            if (Player.getPosition().distanceTo(new RSTile(3108, 3356, 1)) == 0) {
                botstatus = "Walking to stairs";
                Walking.walkTo(new RSTile(3109, 3366, 1));
            } else if (stairs.length > 0) {
                botstatus = "Climbing down stairs";
                if (stairs[0].click("Climb-down")) {
                    General.sleep(1000, 1500);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3109, 3361, 0)) == 0 || Player.getPosition().distanceTo(new RSTile(3108, 3361, 0)) == 0) {
                botstatus = "Walking to door 1";
                Walking.walkTo(new RSTile(3106, 3368, 0));
            } else if (doors.length > 0) {
                botstatus = "Opening door";
                if (doors[0].click("Open")) {
                    General.sleep(1200, 1600);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3106, 3368, 0)) < 2) {
                botstatus = "Walking to door 2";
                Walking.blindWalkTo(new RSTile(3119, 3356, 0));
            } else if (Player.getPosition().distanceTo(new RSTile(3119, 3356, 0)) < 2) {
                botstatus = "Walking to door 3";
                Walking.walkTo(new RSTile(3123, 3360, 0));
            } else if (doors2.length > 0 && Player.getPosition().distanceTo(new RSTile(3123, 3361, 0)) != 0) {
                botstatus = "Opening door 3";
                if (doors2[0].click("Open")) {
                    General.sleep(1200, 1600);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3123, 3361, 0)) == 0) {
                botstatus = "Walking to compost";
                //WebWalking.walkTo(new RSTile(3087, 3360));
                Walking.walkPath(TO_DUMPSTER);
            } else if (Player.getPosition().distanceTo(new RSTile(3087, 3360)) < 6) {
                botstatus = "Digging for key";
                if (compost.length > 0 && spade.length > 0) {
                    if (spade[0].click("Use")) {
                        General.sleep(350, 650);
                        if (compost[0].click("Use Spade")) {
                            General.sleep(3000, 4500);
                        }
                    }
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && hasItem(POIS_FISH_ID) && hasItem(KEY_ID)) {
            RSObject[] fountain = Objects.findNearest(2, FOUNTAIN_ID);
            RSItem[] fishfood = Inventory.find(POIS_FISH_ID);
            if (Player.getPosition().distanceTo(new RSTile(3087, 3360)) < 6) {
                botstatus = "Walking to Fountain";
                WebWalking.walkTo(new RSTile(3088, 3336));
            } else if (fountain.length > 0 && fishfood.length > 0) {
                botstatus = "Using fish food on fountain";
                if (fishfood[0].click("Use")) {
                    General.sleep(350, 650);
                    if (fountain[0].click("Use Poisoned fish food")) {
                        General.sleep(3500, 4500);
                    }
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && hasItem(KEY_ID) && !hasItem(PRESSURE_ID)) {
            RSObject[] fountain = Objects.findNearest(2, FOUNTAIN_ID);
            if (fountain.length > 0) {
                botstatus = "Searching for pressure gauge";
                if (fountain[0].click("Search")) {
                    General.sleep(3500, 4500);
                    if (NPCChat.getClickContinueInterface() != null) {
                        if (NPCChat.clickContinue(true)) {
                            General.sleep(250, 350);
                            if (NPCChat.clickContinue(true)) {
                                General.sleep(250, 350);
                            }
                        }
                    }
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && hasItem(KEY_ID) && hasItem(PRESSURE_ID) && !hasItem(RUBBER_ID)) {
            RSObject[] fountain = Objects.findNearest(2, FOUNTAIN_ID);
            if (fountain.length > 0) {
                botstatus = "Walking to castle";
                Walking.blindWalkTo(larde_door_tile);
            } else if (isObjectNear(LARGE_DOOR_ID) && Player.getPosition().distanceTo(new RSTile(3109, 3354)) > 0 && !inside_castle_again) {
                botstatus = "Opening door";
                if (Camera.getCameraRotation() < 150 || Camera.getCameraRotation() > 200) {
                    botstatus = "Rotating camera for doors";
                    int rotationRNG = General.random(150, 200);
                    Camera.setCameraRotation(rotationRNG);
                } else {
                    RSObject[] doors = Objects.find(6, LARGE_DOOR_ID);
                    if (doors.length > 0) {
                        if (doors[0].click("Open")) {
                            inside_castle_again = true;
                            General.sleep(250, 500);
                            camera();
                        }
                    }
                }
            } else if (isObjectNear(LARGE_DOOR_ID) && Player.getPosition().distanceTo(new RSTile(3109, 3354)) == 0 && inside_castle_again && !isGroundObjectNear(RUBBER_ID)) {
                RSObject[] door = Objects.find(5, DOOR1);
                if (door.length > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(1500, 2500);
                    }
                } else {
                    botstatus = "Walking to rubber door";
                    Walking.walkTo(new RSTile(3107, 3367, 0));
                }
            } else if (!isObjectNear(LARGE_DOOR_ID) && inside_castle_again && isGroundObjectNear(RUBBER_ID)) {
                RSGroundItem[] objs = GroundItems.findNearest(RUBBER_ID);
                RSObject[] door = Objects.find(2, 131);
                if (door.length > 0 && Player.getPosition().distanceTo(new RSTile(3108, 3367)) > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(1500, 2500);
                    }
                } else if (objs.length > 0) {
                    botstatus = "Picking up rubber";
                    if (objs[0].click("Take")) {
                        General.sleep(3500, 4500);
                    }
                }
            }
        } else if (started_chicken && inside_castle && talked_to_prof1 && hasItem(OILCAN_ID) && hasItem(KEY_ID) && hasItem(PRESSURE_ID) && hasItem(RUBBER_ID)) {
            RSObject[] door = Objects.find(7, 131);
            RSObject[] stairs = Objects.find(3, STAIRS1);
            RSObject[] stairs2 = Objects.find(7, STAIRS2);
            RSObject[] door2 = Objects.find(6, DOOR1);
            RSNPC[] prof = NPCs.findNearest(9, PROF_ID);
            RSNPC[] erny = NPCs.findNearest(9, ERNEST_ID);
            if (door.length > 0 && Player.getPosition().distanceTo(new RSTile(3111, 3367)) == 0) {
                botstatus = "Opening door";
                if (door[0].click("Open")) {
                    General.sleep(1500, 2500);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3107, 3367)) == 0) {
                botstatus = "Walking to stairs";
                Walking.walkTo(new RSTile(3108, 3361));
            } else if (stairs.length > 0) {
                botstatus = "Climbing up stairs 1";
                if (stairs[0].click("Climb-up")) {
                    General.sleep(2500, 3000);
                }
            } else if (stairs2.length > 0) {
                if (stairs2[0].isClickable() && stairs2[0].isOnScreen()) {
                    botstatus = "Climbing up stairs 2";
                    if (stairs2[0].click("Climb-up")) {
                        General.sleep(2500, 3000);
                    }
                } else {
                    botstatus = "Walking to stairs 2";
                    Walking.walkTo(new RSTile(3105, 3365, 1));
                }
            } else if (door2.length > 0) {
                botstatus = "Opening door";
                if (door2[0].isOnScreen()) {
                    if (door2[0].click("Open")) {
                        General.sleep(2000, 2500);
                    }
                } else {
                    Walking.walkTo(door2[0].getPosition());
                }
            } else if (prof.length > 0) {
                if (prof[0].isClickable() && prof[0].isOnScreen() && prof[0].isValid()) {
                    botstatus = "Talking to Professor";
                    prof[0].click("Talk-to");
                    General.sleep(2000, 2500);
                    if (NPCChat.getClickContinueInterface() != null) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(8000, 10000);
                                }
                            }
                        }
                    }
                } else {
                    Walking.walkTo(prof[0].getPosition());
                }
            } else if (erny.length > 0) {
                botstatus = "Talking to Ernest";
                if (NPCChat.getClickContinueInterface() != null) {
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(8000, 10000);
                            }
                        }
                    }
                }
            }
        }
    }

    private void solveMaze() {
        if (!step1) {
            RSObject[] leverA = Objects.find(4, 146);
            RSObject[] leverB = Objects.find(3, 147);
            if (leverB.length > 0) {
                if (leverB[0].isClickable() && leverB[0].isOnScreen()) {
                    botstatus = "Pulling lever B";
                    if (leverB[0].click("Pull")) {
                        General.sleep(1500, 1800);
                        botstatus = "Walking to lever A";
                        Walking.walkTo(new RSTile(3108, 9745));
                        General.sleep(1500, 1800);
                    }
                }
            } else if (leverA.length > 0) {
                if (leverA[0].isClickable() && leverA[0].isOnScreen()) {
                    botstatus = "Pulling lever A";
                    if (leverA[0].click("Pull")) {
                        step1 = true;
                        General.sleep(1500, 1800);
                        botstatus = "Walking to Door 1";
                        Walking.clickTileMM(new RSTile(3108, 9757), 1);
                        General.sleep(1500, 1800);
                    }
                }
            }
        } else if (step1 && !step2) {
            if (Player.getPosition().distanceTo(new RSTile(3108, 9757)) < 2) {
                botstatus = "Opening door 1";
                RSObject[] door1 = Objects.find(4, 144);
                if (door1.length > 0) {
                    if (door1[0].click("Open")) {
                        General.sleep(500, 750);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3108, 9759)) == 0) {
                botstatus = "Walking to lever D";
                Walking.clickTileMM(new RSTile(3108, 9767), 1);
            }
            RSObject[] leverD = Objects.find(3, 149);
            RSObject[] door2 = Objects.find(1, 139);
            RSObject[] door3 = Objects.find(1, 145);
            if (leverD.length > 0) {
                botstatus = "Pulling lever D";
                if (leverD[0].click("Pull")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to Door 2";
                    Walking.clickTileMM(new RSTile(3105, 9761), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door2.length > 0) {
                botstatus = "Opening door 2";
                if (door2[0].click("Open")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to Door 3";
                    Walking.clickTileMM(new RSTile(3102, 9759), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door3.length > 0) {
                botstatus = "Opening door 3";
                if (door3[0].click("Open")) {
                    step2 = true;
                    General.sleep(1500, 1800);
                }
            }
        } else if (step1 && step2 && !step3) {
            if (Player.getPosition().distanceTo(new RSTile(3102, 9757)) == 0) {
                botstatus = "Walking to lever B";
                Walking.clickTileMM(new RSTile(3118, 9752), 1);
                General.sleep(1500, 1800);
            }
            RSObject[] leverA = Objects.find(4, 146);
            RSObject[] leverB = Objects.find(3, 147);
            if (leverB.length > 0) {
                if (leverB[0].isClickable() && leverB[0].isOnScreen()) {
                    botstatus = "Pulling lever B";
                    if (leverB[0].click("Pull")) {
                        General.sleep(1500, 1800);
                        botstatus = "Walking to lever A";
                        Walking.walkTo(new RSTile(3108, 9745));
                        General.sleep(1500, 1800);
                    }
                }
            } else if (leverA.length > 0) {
                if (leverA[0].isClickable() && leverA[0].isOnScreen()) {
                    botstatus = "Pulling lever A";
                    if (leverA[0].click("Pull")) {
                        step3 = true;
                        General.sleep(1500, 1800);
                        botstatus = "Walking to Door 3";
                        Walking.clickTileMM(new RSTile(3102, 9757), 1);
                        General.sleep(1500, 1800);
                    }
                }
            }
        } else if (step1 && step2 && step3 && !step4) {
            RSObject[] door3 = Objects.find(1, 145);
            RSObject[] door4 = Objects.find(1, 140);
            RSObject[] door5 = Objects.find(1, 143);
            if (door3.length > 0) {
                botstatus = "Opening door 3";
                if (door3[0].click("Open")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to Door 4";
                    Walking.clickTileMM(new RSTile(3101, 9760), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door4.length > 0) {
                botstatus = "Opening door 4";
                if (door4[0].click("Open")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to Door 5";
                    Walking.clickTileMM(new RSTile(3097, 9762), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door5.length > 0) {
                botstatus = "Opening door 5";
                if (door5[0].click("Open")) {
                    step4 = true;
                    General.sleep(1500, 1800);
                }
            }
        } else if (step1 && step2 && step3 && step4 && !step5) {
            RSObject[] leverE = Objects.find(5, 150);
            RSObject[] leverF = Objects.find(5, 151);
            if (leverE.length > 0 && !pulledE) {
                botstatus = "Pulling lever E";
                if (leverE[0].click("Pull")) {
                    pulledE = true;
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 6";
                    Walking.clickTileMM(new RSTile(3099, 9765), 1);
                    General.sleep(1500, 1800);
                }
            } else if (leverF.length > 0 && !pulledF) {
                botstatus = "Pulling lever F";
                if (leverF[0].click("Pull")) {
                    pulledF = true;
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 6";
                    Walking.clickTileMM(new RSTile(3099, 9765), 1);
                    General.sleep(1500, 1800);
                }
            } else if (pulledE && pulledF) {
                RSObject[] door6 = Objects.find(1, 138);
                RSObject[] door7 = Objects.find(1, 137);
                if (door6.length > 0) {
                    botstatus = "Opening door 6";
                    if (door6[0].click("Open")) {
                        General.sleep(1500, 1800);
                        botstatus = "Walking to door 7";
                        Walking.clickTileMM(new RSTile(3104, 9765), 1);
                        General.sleep(1500, 1800);
                    }
                } else if (door7.length > 0) {
                    botstatus = "Opening door 7";
                    if (door7[0].click("Open")) {
                        step5 = true;
                        General.sleep(1500, 1800);
                    }
                }
            }
        } else if (step1 && step2 && step3 && step4 && step5 && !step6) {
            RSObject[] leverC = Objects.find(2, 148);
            RSObject[] door6 = Objects.find(2, 138);
            RSObject[] door7 = Objects.find(2, 137);
            RSObject[] leverE = Objects.find(2, 150);
            if (Player.getPosition().distanceTo(new RSTile(3106, 9765)) == 0 && !pulledC) {
                botstatus = "Walking to lever C";
                Walking.clickTileMM(new RSTile(3112, 9760), 1);
                General.sleep(1500, 1800);
            } else if (leverC.length > 0) {
                botstatus = "Pulling lever C";
                if (leverC[0].click("Pull")) {
                    pulledC = true;
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 7";
                    Walking.clickTileMM(new RSTile(3106, 9765), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door6.length > 0 && pulledC && Player.getPosition().distanceTo(new RSTile(3099, 9765)) != 0) {
                botstatus = "Opening door 6";
                if (door6[0].click("Open")) {
                    General.sleep(1500, 1800);
                }
            } else if (door7.length > 0 && pulledC) {
                botstatus = "Opening door 7";
                if (door7[0].click("Open")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 6";
                    Walking.clickTileMM(new RSTile(3101, 9765), 1);
                    General.sleep(1500, 1800);
                }
            } else if (leverE.length > 0 && pulledC) {
                botstatus = "Pulling lever E";
                if (leverE[0].click("Pull")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 6";
                    Walking.clickTileMM(new RSTile(3099, 9765), 1);
                    step6 = true;
                    General.sleep(1500, 1800);
                }
            }
        } else if (step1 && step2 && step3 && step4 && step5 && step6 && !step7) {
            RSObject[] door6 = Objects.find(2, 138);
            RSObject[] door8 = Objects.find(2, 142);
            RSObject[] door3 = Objects.find(2, 145);
            RSObject[] door9 = Objects.find(2, 141);
            if (door6.length > 0) {
                botstatus = "Opening door 6";
                if (door6[0].click("Open")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 8";
                    Walking.clickTileMM(new RSTile(3102, 9764), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door8.length > 0) {
                botstatus = "Opening door 8";
                if (door8[0].click("Open")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 3";
                    Walking.clickTileMM(new RSTile(3102, 9759), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door3.length > 0) {
                botstatus = "Opening door 3";
                if (door3[0].click("Open")) {
                    General.sleep(1500, 1800);
                    botstatus = "Walking to door 9";
                    Walking.clickTileMM(new RSTile(3101, 9755), 1);
                    General.sleep(1500, 1800);
                }
            } else if (door9.length > 0) {
                botstatus = "Opening door 9";
                if (door9[0].click("Open")) {
                    step7 = true;
                    General.sleep(1500, 1800);
                    botstatus = "Walking to Oil can";
                    Walking.clickTileMM(new RSTile(3092, 9755), 1);
                    General.sleep(1500, 1800);
                }
            }
        } else if (step1 && step2 && step3 && step4 && step5 && step6 && step7 && !step8) {
            RSObject[] door9 = Objects.find(2, 141);
            RSObject[] ladder = Objects.find(5, 132);
            if (!hasItem(277)) {
                botstatus = "Picking up Oil can";
                RSGroundItem[] oilcan = GroundItems.find(277);
                if (oilcan.length > 0) {
                    if (oilcan[0].click("Take")) {
                        General.sleep(1500, 1800);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3092, 9755)) == 0 && hasItem(277)) {
                botstatus = "Walking to door 9";
                Walking.clickTileMM(new RSTile(3099, 9755), 1);
            } else if (door9.length > 0) {
                botstatus = "Opening door 9";
                if (door9[0].click("Open")) {
                    General.sleep(1800, 2000);
                    botstatus = "Walking to Ladder";
                    Walking.blindWalkTo(new RSTile(9116, 9755));
                    General.sleep(1500, 1800);
                }
            } else if (ladder.length > 0) {
                botstatus = "Climbing ladder";
                if (ladder[0].click("Climb-up")) {
                    step8 = true;
                    General.sleep(2500, 2800);
                }
            }
        }
    }

    private void talkToProf1() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("I'm looking for a guy called Ernest.", true)) { //7x
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(200, 350);
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(200, 350);
                                            if (NPCChat.clickContinue(true)) {
                                                sleep(200, 350);
                                                if (NPCChat.selectOption("I'm glad Veronica didn't actually get engaged to a chicken.", true)) { //10x
                                                    sleep(500, 650);
                                                    for (int i = 0; i < 10; i++) {
                                                        if (NPCChat.clickContinue(true)) {
                                                            sleep(500, 650);
                                                        }
                                                        if (i >= 9) {
                                                            talked_to_prof1 = true;
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

    public boolean started = false; //temp true
    public boolean ghost_talked = false; //temp true

    private void startGhost() {
        camera();
        final RSTile church = new RSTile(3236, 3210, 0);
        final RSTile second_church = new RSTile(3147, 3172, 0);
        final RSTile ghost_house = new RSTile(3246, 3193, 0);
        final RSTile wiz_tower = new RSTile(3109, 3167, 0);
        final RSTile wiz_tower_mid = new RSTile(3108, 3163, 0);
        final RSTile wiz_tower_downstairs = new RSTile(3104, 9576, 0);
        final RSTile wiz_tower_downstairs_2 = new RSTile(3110, 9559, 0);
        final RSTile wiz_tower_altartile = new RSTile(3118, 9566, 0);
        final int church_father_id = 921;
        final int second_father_id = 923;
        final int church_door_id = 1540;
        final int second_door_id = 15056;
        final int ghost_ammy_id = 552;
        final int ghost_door_id = 1535;
        final int ghost_npc_id = 922;
        final int coffin_id = 2145;
        final int skull_id = 553;
        final int wiz_tower_doors = 23972;
        final int wiz_tower_ladder = 2147;
        final int wiz_tower_down_door = 1535;
        final int wiz_tower_altar = 2146;
        RSInterface questbox = Interfaces.get(277, 2);

        if (questbox != null) {
            RSInterface closebox = Interfaces.get(277, 15);
            if (closebox != null) {
                closebox.click("Close");
                General.println("Restless Ghost: Quest completed!");
                QUEST = 2;
            }
        } else if (isInsideLumby()) {
            if (!hasItem(skull_id)) {
                botstatus = "Walking to church";
                WebWalking.walkTo(church);
            } else if (hasItem(skull_id)) {
                botstatus = "Walking to finish quest";
                WebWalking.walkTo(ghost_house);
            }
        } else if (Player.getPosition().distanceTo(church) < 6 && isObjectNear(church_door_id)) {
            RSObject[] DOOR = Objects.find(6, church_door_id);
            if (DOOR.length > 0) {
                botstatus = "Opening door";
                if (DOOR[0].click("Open")) {
                    General.sleep(250, 500);
                    botstatus = "Walking to church mid point";
                    Walking.walkTo(new RSTile(3244, 3208, 0));
                }
            }
        } else if (Player.getPosition().distanceTo(church) < 6 && !isObjectNear(church_door_id)) {
            botstatus = "Walking to church mid point";
            Walking.walkTo(new RSTile(3244, 3208, 0));
        } else if (isNPCnear(church_father_id) && !started) {
            RSNPC[] npcs = NPCs.findNearest(church_father_id);
            if (npcs.length > 0) {
                if (npcs[0].isClickable() && npcs[0].isOnScreen() && npcs[0].isValid()) {
                    botstatus = "Talking to father";
                    npcs[0].click("Talk-to");
                    General.sleep(500, 800);
                    if (NPCChat.getClickContinueInterface() != null) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.selectOption("I'm looking for a quest!", true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                        if (NPCChat.selectOption("Ok, let me help then.", true)) { //8x
                                            sleep(500, 650);
                                            for (int i = 0; i < 9; i++) {
                                                if (NPCChat.clickContinue(true)) {
                                                    sleep(500, 650);
                                                }
                                                if (i >= 8) {
                                                    started = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    botstatus = "Walking to father";
                    Walking.walkTo(npcs[0].getPosition());
                }
            }
        } else if (isNPCnear(church_father_id) && started) {
            debugg("We've started the quest!");
            botstatus = "Walking to second father";
            WebWalking.walkTo(second_church);
        } else if (Player.getPosition().distanceTo(second_church) < 6 && isObjectNear(second_door_id)) {
            RSObject[] DOOR = Objects.find(6, second_door_id);
            if (DOOR.length > 0) {
                botstatus = "Opening door";
                if (DOOR[0].click("Open")) {
                    General.sleep(250, 500);
                }
            }
        } else if (isNPCnear(second_father_id) && started && !hasItem(ghost_ammy_id)) {
            RSNPC[] npcs = NPCs.findNearest(second_father_id);
            if (npcs.length > 0) {
                if (npcs[0].isClickable() && npcs[0].isOnScreen() && npcs[0].isValid()) {
                    botstatus = "Talking to father";
                    npcs[0].click("Talk-to");
                    General.sleep(500, 800);
                    if (NPCChat.getClickContinueInterface() != null) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.selectOption("Father Aereck sent me to talk to you.", true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                        if (NPCChat.selectOption("He's got a ghost haunting his graveyard.", true)) { //8x
                                            sleep(500, 650);
                                            for (int i = 0; i < 12; i++) {
                                                if (NPCChat.clickContinue(true)) {
                                                    sleep(500, 650);
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
        } else if (hasItem(ghost_ammy_id) && isNPCnear(second_father_id) && started) {
            debugg("We've got the Ghost amulet!");
            botstatus = "Walking to ghost house";
            WebWalking.walkTo(ghost_house);
        } else if (Player.getPosition().distanceTo(ghost_house) < 6 && started && !ghost_talked && !hasItem(skull_id)) {
            RSObject[] DOOR = Objects.find(6, ghost_door_id);
            RSObject[] COFFIN = Objects.find(8, coffin_id);
            RSItem[] ammy = Inventory.find(ghost_ammy_id);
            if (DOOR.length > 0) {
                botstatus = "Opening door";
                if (DOOR[0].click("Open")) {
                    General.sleep(250, 500);
                }
            } else if (COFFIN.length > 0 && !isObjectNear(ghost_door_id)) {
                if (ammy.length > 0) {
                    botstatus = "Equipping amulet";
                    ammy[0].click("Wear");
                    General.sleep(250, 500);
                }
                botstatus = "Searching coffin";
                if (COFFIN[0].click("Search")) {
                    General.sleep(4500, 6500);
                }
            } else if (!isObjectNear(ghost_door_id) && isNPCnear(ghost_npc_id)) {
                botstatus = "Talking to Ghost";
                talkToGhostFirst();
            }
        } else if (ghost_talked && started && isNPCnear(ghost_npc_id) && !hasItem(skull_id)) {
            debugg("Talked to ghost, walking to wizard tower");
            botstatus = "Walking to Wizard Tower";
            WebWalking.walkTo(wiz_tower);
        } else if (ghost_talked && started && Player.getPosition().distanceTo(wiz_tower) < 4 && !hasItem(skull_id)) {
            RSObject[] objs = Objects.findNearest(4, wiz_tower_doors);
            if (objs.length > 0) {
                botstatus = "Opening door 1";
                if (objs[0].click("Open")) {
                    General.sleep(250, 500);
                }
            }
            Walking.walkTo(wiz_tower_mid);
        } else if (ghost_talked && started && Player.getPosition().distanceTo(wiz_tower_mid) < 6 && !hasItem(skull_id)) {
            RSObject[] objs = Objects.findNearest(4, wiz_tower_doors);
            RSObject[] ladder = Objects.findNearest(8, wiz_tower_ladder);
            if (objs.length > 0) {
                botstatus = "Opening door 2";
                if (objs[0].click("Open")) {
                    General.sleep(250, 500);
                }
            }
            if (ladder.length > 0 && objs.length <= 0) {
                botstatus = "Walking down ladder";
                if (ladder[0].click("Climb-down")) {
                    General.sleep(250, 500);
                }
            }
        } else if (ghost_talked && started && Player.getPosition().distanceTo(wiz_tower_downstairs) == 0 && !hasItem(skull_id)) {
            debugg("We're downstairs, getting skull");
            botstatus = "Walking to door";
            Walking.blindWalkTo(wiz_tower_downstairs_2);
        } else if (ghost_talked && started && Player.getPosition().distanceTo(wiz_tower_downstairs_2) < 4 && !hasItem(skull_id)) {
            RSObject[] objs = Objects.findNearest(4, wiz_tower_down_door);
            if (objs.length > 0) {
                botstatus = "Opening downstair door";
                if (objs[0].click("Open")) {
                    General.sleep(2500, 5000);
                }
            }
            botstatus = "Walking to altar";
            Walking.walkTo(wiz_tower_altartile);
        } else if (ghost_talked && started && Player.getPosition().distanceTo(wiz_tower_altartile) < 4 && !hasItem(skull_id)) {
            RSObject[] objs = Objects.findNearest(4, wiz_tower_altar);
            if (objs.length > 0) {
                botstatus = "Searching altar";
                if (objs[0].click("Search")) {
                    General.sleep(250, 500);
                }
            }
        } else if (ghost_talked && started && Player.getPosition().distanceTo(wiz_tower_altartile) < 4 && hasItem(skull_id)) {
            botstatus = "Avoiding skeleton";
            Walking.walkTo(wiz_tower_downstairs_2);
        } else if (ghost_talked && started && Player.getPosition().distanceTo(wiz_tower_downstairs_2) < 4 && hasItem(skull_id)) {
            botstatus = "Casting home teleport";
            if (GameTab.TABS.MAGIC.isOpen()) {
                Magic.selectSpell("Lumbridge Home Teleport");
                General.sleep(16000, 17500);
                GameTab.open(GameTab.TABS.INVENTORY);
            } else {
                GameTab.open(GameTab.TABS.MAGIC);
            }
        } else if (Player.getPosition().distanceTo(ghost_house) < 6 && started && ghost_talked && hasItem(skull_id)) {
            RSObject[] DOOR = Objects.find(6, ghost_door_id);
            RSObject[] COFFIN = Objects.find(8, coffin_id);
            RSItem[] SKULL = Inventory.find(8, skull_id);
            RSObject[] COFFIN_OPEN = Objects.find(8, 15061);
            if (DOOR.length > 0) {
                botstatus = "Opening door";
                if (DOOR[0].click("Open")) {
                    General.sleep(250, 500);
                }
            } else if ((COFFIN.length > 0 || COFFIN_OPEN.length > 0) && !isObjectNear(ghost_door_id)) {
                if (COFFIN.length > 0) {
                    botstatus = "Opening coffin";
                    if (COFFIN[0].click("Open")) {
                        General.sleep(3500, 5000);
                    }
                } else if (COFFIN_OPEN.length > 0) {
                    botstatus = "Using skull on coffin";
                    if (SKULL[0].click("Use")) {
                        General.sleep(250, 500);
                        if (COFFIN_OPEN[0].click("Use Ghost's Skull")) {
                            General.sleep(25000, 50000);
                        }
                    }
                }
            }
        }
    }

    private void talkToGhostFirst() {
        RSNPC[] npcs = NPCs.findNearest(922);
        if (npcs.length > 0) {
            if (npcs[0].isClickable() && npcs[0].isOnScreen() && npcs[0].isValid()) {
                botstatus = "Talking to Ghost";
                npcs[0].click("Talk-to");
                General.sleep(500, 800);
                if (NPCChat.getClickContinueInterface() != null) {
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.selectOption("Wow, this amulet works!", true)) {
                                        sleep(500, 650);
                                        if (NPCChat.clickContinue(true)) {
                                            sleep(500, 650);
                                            if (NPCChat.clickContinue(true)) {
                                                sleep(500, 650);
                                                if (NPCChat.selectOption("Yes, ok. Do you know why you're a ghost?", true)) {
                                                    sleep(500, 650);
                                                    for (int i = 0; i < 9; i++) {
                                                        if (NPCChat.clickContinue(true)) {
                                                            sleep(500, 650);
                                                        }
                                                        if (i >= 8) {
                                                            ghost_talked = true;
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

    private void camera() {
        if (Camera.getCameraAngle() < 88) {
            botstatus = "Rotating camera";
            int angleRNG = General.random(88, 100);
            Camera.setCameraAngle(angleRNG);
        }
        if (Camera.getCameraRotation() < 315 || Camera.getCameraRotation() > 340) {
            botstatus = "Rotating camera";
            int rotationRNG = General.random(315, 340);
            Camera.setCameraRotation(rotationRNG);
        }
    }

    private boolean hasItem(final int ITEM_ID) {
        RSItem[] items = Inventory.find(ITEM_ID);
        return items.length > 0;
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

    private boolean isInsideLumby() {
        return LUMBRIDGE_AREA.contains(Player.getPosition());
    }

    private boolean isInsideErnest() {
        return ERNEST_AREA.contains(Player.getPosition());
    }

    private boolean isInsideLevers() {
        return LEVER_AREA.contains(Player.getPosition());
    }

    private boolean isInsideVarrock() {
        return VARROCK_AREA.contains(Player.getPosition());
    }

    private boolean isInsideTemple() {
        return TEMPLE_AREA.contains(Player.getPosition());
    }

    private boolean isInsideDrezel() {
        return DREZE_AREA.contains(Player.getPosition());
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

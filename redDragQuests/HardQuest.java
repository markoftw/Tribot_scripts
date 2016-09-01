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

@ScriptManifest(authors = "Marko", category = "RedDrags", name = "5. Hard quest", description = "Ava quest [START WITH ITEMS AT GRAND EXCHANGE!]", version = 1.1)
public class HardQuest extends Script implements Painting, Ending {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;


    /* -  -  -  -  -  -  -  - */
    public int totalHerbs = 0;
    public int totalMoney = 0;
    public boolean SET_FOOD = false;
    public boolean SLEEPER = false;
    public RSPlayer[] players_1 = null;
    public RSPlayer[] players_2 = null;
    public long timeRan_1 = 0;
    public long timeRan_2 = 0;

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
                case QUEST:
                    botstatus = "Animal Magnetism";
                    animalMagnetism();
                    break;
            }
            // control cpu usage
            General.sleep(100, 250);
        }
    }

    enum State {
        LOGGED_IN,
        WALKING,
        QUEST
    }

    //powerwc do 35, 19 craft GE full world, agility 30 grand tree empty w
    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Player.isMoving()) {
            return State.WALKING;
        }
        return State.QUEST;
    }

    private final static Area GE_AREA = new Area(new RSTile[]{
        new RSTile(3154, 3499, 0), new RSTile(3175, 3499, 0),
        new RSTile(3175, 3479, 0), new RSTile(3154, 3479, 0)});

    private final static Area VARROCK_AREA = new Area(new RSTile[]{
        new RSTile(3200, 3440), new RSTile(3225, 3440),
        new RSTile(3225, 3419), new RSTile(3200, 3419)});

    private final static Area DRAYNOR_AREA = new Area(new RSTile[]{
        new RSTile(3100, 3255), new RSTile(3108, 3255),
        new RSTile(3108, 3245), new RSTile(3100, 3245)});

    private final static Area ERNEST_AREA = new Area(new RSTile[]{
        new RSTile(3089, 3374), new RSTile(3119, 3374),
        new RSTile(3119, 3355), new RSTile(3089, 3355)});

    private final static Area TOWER_AREA = new Area(new RSTile[]{
        new RSTile(3651, 3527), new RSTile(3668, 3527),
        new RSTile(3668, 3511), new RSTile(3651, 3511)});

    private final static Area ALICE_AREA = new Area(new RSTile[]{
        new RSTile(3626, 3528), new RSTile(3631, 3528),
        new RSTile(3631, 3523), new RSTile(3626, 3523)});

    private boolean started_quest = false; //temp true
    private boolean talked_drezel = false; //temp true
    private boolean talked_alice1 = false; //temp true
    private boolean talked_alice2 = false; //temp true
    private boolean talked_alice3 = false; //temp true
    private boolean talked_alice4 = false; //temp true
    private boolean talked_husband1 = false; //temp true
    private boolean talked_husband2 = false; //temp true
    private boolean talked_husband3 = false; //temp true
    private boolean talked_husband4 = false; //temp true
    private boolean talked_husband5 = false; //temp true
    private boolean talked_husband6 = false; //temp true
    private boolean talked_crone1 = false; //temp true
    private boolean talked_crone2 = false; //temp true
    private boolean talked_ava1 = false; //temp true
    private boolean talked_ava2 = false; //temp true
    private boolean talked_ava3 = false; //temp true
    private boolean talked_ava4 = false; //temp true
    private boolean talked_ava5 = false; //temp true
    private boolean talked_ava6 = false; //temp true
    private boolean talked_ava7 = false; //temp true
    private boolean talked_witch1 = false; //temp true
    private boolean talked_witch2 = false; //temp true
    private boolean used_axe = false; //temp true
    private boolean talked_turael1 = false; //temp true
    private boolean talked_turael2 = false; //temp true

    private void animalMagnetism() {
        final int ITEM_HOLYSYMBOL = 1718;
        final int ITEM_MITHAXE = 1355;
        final int ITEM_HAMMER = 2347;
        final int[] ITEM_GAMESNECK = {3853, 3855, 3857, 3859, 3861, 3863, 3865, 3867};
        final int ITEM_POLISHED = 10496;
        final int ITEM_LEATHER = 1743;
        final int ITEM_IRONBAR = 2351;
        final int[] ITEM_GLORY = {11978, 11976, 1712, 1710, 1708, 1706};
        final int ITEM_POT = 1931;
        final int ITEM_BUCKET = 1925;
        final int ITEM_BONES = 526;
        final int ITEM_VTAB = 8007;
        final int ITEM_ECTO = 4278;
        final int ITEM_SLIME = 4286;
        final int ITEM_BONEMEAL = 4255;
        final int ITEM_GHOSTAMMY = 552;
        final int ITEM_GHOSTAMMY_CLONE = 10500;
        final int ITEM_CHICKEN = 10487;
        final int ITEM_MAGNET = 10488;
        final int ITEM_MAGNET_BAR = 10489;
        final int ITEM_MITHAXE_BLESSED = 10491;
        final int ITEM_TWIGS = 10490;
        final int ITEM_NOTE = 10492;
        final int ITEM_NOTE_COMPLETED = 10493;
        final int ITEM_NOTE_FINISHED = 10494;
        final int ITEM_CONTAINER = 10495;

        final int NPC_AVA = 4408;
        final int NPC_DREZEL = 3489;
        final int NPC_ALICE = 504;
        final int NPC_HUSBAND = 4415;
        final int NPC_CRONE = 2996;
        final int NPC_WITCH = 4410;
        final int NPC_TREE = 4418;

        final RSTile TILE_CASTLE = new RSTile(3108, 3361, 0);
        final RSTile TILE_BOOKCASE = new RSTile(3098, 3359, 0);
        final RSTile TILE_DREZEL = new RSTile(3439, 9896, 0);
        final RSTile TILE_TOWER = new RSTile(3657, 3519, 0);
        final RSTile TILE_ALICE = new RSTile(3629, 3525, 0);
        final RSTile TILE_HUSBAND = new RSTile(3615, 3527, 0);
        final RSTile TILE_CRONE = new RSTile(3463, 3558, 0);

        RSInterface questbox = Interfaces.get(277, 15);

        if (questbox != null) {
            questbox.click("Close");
            General.sleep(100, 250);
            General.println("Animal Magnetism: Quest completed!");
            Login.logout();
            stop_script = true;
        } else if (isInsideGE() && !started_quest) {
            camera();
            RSItem[] glory = Inventory.find(ITEM_GLORY);
            if (glory.length > 0) {
                botstatus = "Teleporting to Draynor";
                if (glory[0].click("Rub")) {
                    General.sleep(1000, 1500);
                    tpToDraynor();
                }
            }
        } else if (isInsideDraynor() && !started_quest) {
            botstatus = "Walking to Castle";
            WebWalking.walkTo(TILE_CASTLE);
        } else if (!started_quest && isInsideCastle()) {
            RSObject[] bookcase = Objects.find(2, 156);
            RSObject[] door = Objects.find(2, 11470);
            RSNPC[] ava = NPCs.findNearest(NPC_AVA);
            if (Player.getPosition().distanceTo(TILE_CASTLE) < 3) {
                botstatus = "Walking to Door 1";
                Walking.walkTo(new RSTile(3106, 3368));
            } else if (door.length > 0) {
                botstatus = "Opening door";
                if (door[0].click("Open")) {
                    General.sleep(500, 750);
                }
            } else if (bookcase.length > 0 && Player.getPosition().distanceTo(TILE_BOOKCASE) < 2) {
                botstatus = "Opening bookcase";
                if (bookcase[0].click("Search")) {
                    General.sleep(2000, 2500);
                }
            } else if (ava.length > 0 && isNPCnear(NPC_AVA)) {
                botstatus = "Talking to Ava";
                if (ava[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    startQuest();
                }
            } else {
                botstatus = "Walking to Bookcase";
                Walking.walkTo(TILE_BOOKCASE);
            }
        } else if (started_quest && isInsideCastle() && !talked_husband6) {
            RSItem[] tab = Inventory.find(ITEM_VTAB);
            if (tab.length > 0 && !isInsideVarrock()) {
                botstatus = "Teleporting to varrock";
                if (tab[0].click("Break")) {
                    General.sleep(3500, 4500);
                }
            }
        } else if (started_quest && isInsideVarrock() && !hasItem(ITEM_ECTO) && !isNPCnear(NPC_DREZEL)) {
            botstatus = "Walking to Ecto tower";
            WebWalking.walkTo(TILE_DREZEL);
        } else if (started_quest && !hasItem(ITEM_ECTO) && isNPCnear(NPC_DREZEL) && !talked_drezel) {
            //if door is near
            RSNPC[] drezel = NPCs.findNearest(NPC_DREZEL);
            botstatus = "Talking to Drezel";
            if (drezel.length > 0) {
                if (drezel[0].click("Talk-to")) {
                    General.sleep(2500, 3000);
                    talkDrezel();
                }
            }
        } else if (started_quest && !hasItem(ITEM_ECTO) && isNPCnear(NPC_DREZEL) && talked_drezel) {
            botstatus = "Walking to Ecto tower";
            WebWalking.walkTo(TILE_TOWER);
        } else if (started_quest && !hasItem(ITEM_ECTO) && talked_drezel && isInsideTower() && !hasItem(ITEM_SLIME) && hasItem(ITEM_BONES)) {
            RSObject[] trapdoor = Objects.find(8, 16113);
            RSObject[] trapladder = Objects.find(8, 16114);
            if (trapdoor.length > 0) {
                botstatus = "Opening trapdoor";
                if (trapdoor[0].click("Open")) {
                    General.sleep(2000, 3500);
                }
            } else if (trapladder.length > 0) {
                botstatus = "Climbing down trapdoor";
                if (trapladder[0].click("Climb-down")) {
                    General.sleep(2000, 3500);
                }
            }
        } else if (started_quest && !hasItem(ITEM_SLIME) && talked_drezel && !hasItem(ITEM_ECTO) && !isInsideTower() && hasItem(ITEM_BONES)) {
            if (Player.getPosition().distanceTo(new RSTile(3669, 9888, 3)) == 0 && isObjectNear(16110)) {
                botstatus = "Walking to stairs";
                Walking.blindWalkTo(new RSTile(3692, 9888, 3));
            } else if (Player.getPosition().distanceTo(new RSTile(3692, 9888, 3)) < 4 && isObjectNear(16109)) {
                RSObject[] stairs = Objects.find(8, 16109);
                if (stairs.length > 0) {
                    botstatus = "Climbing stairs";
                    if (stairs[0].click("Climb-down")) {
                        General.sleep(2000, 3500);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3688, 9888, 2)) < 4 && isObjectNear(16108)) {
                botstatus = "Walking to second stairs";
                Walking.blindWalkTo(new RSTile(3671, 9888, 2));
            } else if (Player.getPosition().distanceTo(new RSTile(3671, 9888, 2)) < 4 && isObjectNear(16109)) {
                RSObject[] stairs = Objects.find(8, 16109);
                if (stairs.length > 0) {
                    botstatus = "Climbing second stairs";
                    if (stairs[0].click("Climb-down")) {
                        General.sleep(2000, 3500);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3675, 9888, 1)) < 4 && isObjectNear(16108)) {
                botstatus = "Walking to third stairs";
                Walking.blindWalkTo(new RSTile(3687, 9888, 1));
            } else if (Player.getPosition().distanceTo(new RSTile(3687, 9888, 1)) < 4 && isObjectNear(16109)) {
                RSObject[] stairs = Objects.find(8, 16109);
                if (stairs.length > 0) {
                    botstatus = "Climbing third stairs";
                    if (stairs[0].click("Climb-down")) {
                        General.sleep(2000, 3500);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3683, 9888, 0)) < 4 && isObjectNear(16108) && isObjectNear(17119) && hasItem(ITEM_BUCKET)) {
                RSItem[] bucket = Inventory.find(ITEM_BUCKET);
                RSObject[] slime = Objects.findNearest(5, 17119);
                if (bucket.length > 0 && slime.length > 0) {
                    botstatus = "Grabbing slime";
                    if (bucket[0].click("Use")) {
                        General.sleep(250, 500);
                        if (slime[0].click("Use Bucket")) {
                            General.sleep(11000, 12500);
                        }
                    }
                }
            }
        } else if (started_quest && hasItem(ITEM_SLIME) && talked_drezel && !hasItem(ITEM_ECTO) && !isInsideTower() && hasItem(ITEM_BONES)) {
            if (Player.getPosition().distanceTo(new RSTile(3683, 9888, 0)) < 4 && isObjectNear(16108)) {
                RSObject[] stairs = Objects.find(8, 16108);
                if (stairs.length > 0) {
                    botstatus = "Climbing stairs";
                    if (stairs[0].click("Climb-up")) {
                        General.sleep(2000, 3500);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3687, 9888, 1)) < 4 && isObjectNear(16109)) {
                botstatus = "Walking to second stairs";
                Walking.blindWalkTo(new RSTile(3671, 9888, 1));
            } else if (Player.getPosition().distanceTo(new RSTile(3675, 9888, 1)) < 4 && isObjectNear(16108)) {
                RSObject[] stairs = Objects.find(8, 16108);
                if (stairs.length > 0) {
                    botstatus = "Climbing second stairs";
                    if (stairs[0].click("Climb-up")) {
                        General.sleep(2000, 3500);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3671, 9888, 2)) < 4 && isObjectNear(16109)) {
                botstatus = "Walking to third stairs";
                Walking.blindWalkTo(new RSTile(3688, 9888, 2));
            } else if (Player.getPosition().distanceTo(new RSTile(3688, 9888, 2)) < 4 && isObjectNear(16108)) {
                RSObject[] stairs = Objects.find(8, 16108);
                if (stairs.length > 0) {
                    botstatus = "Climbing third stairs";
                    if (stairs[0].click("Climb-up")) {
                        General.sleep(2000, 3500);
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3692, 9888, 3)) < 4 && isObjectNear(16109)) {
                botstatus = "Walking to ladder";
                Walking.blindWalkTo(new RSTile(3669, 9888, 3));
            } else if (Player.getPosition().distanceTo(new RSTile(3669, 9888, 3)) < 4 && isObjectNear(16110)) {
                RSObject[] stairs = Objects.find(8, 16110);
                if (stairs.length > 0) {
                    botstatus = "Climbing ladder";
                    if (stairs[0].click("Climb-up")) {
                        General.sleep(2000, 3500);
                    }
                }
            }
        } else if (started_quest && hasItem(ITEM_SLIME) && talked_drezel && !hasItem(ITEM_ECTO) && isInsideTower() && hasItem(ITEM_BONES)) {
            RSObject[] stairs = Objects.find(20, 16646);
            RSObject[] loader = Objects.find(20, 16654);
            RSObject[] grinder = Objects.find(20, 16655);
            RSObject[] bin = Objects.find(20, 16656);
            RSItem[] bones = Inventory.find(ITEM_BONES);
            if (stairs.length > 0) {
                botstatus = "Going to top of tower";
                if (stairs[0].isOnScreen() && stairs[0].isClickable()) {
                    if (stairs[0].click("Climb-up")) {
                        General.sleep(250, 500);
                    }
                } else {
                    Walking.walkTo(stairs[0].getPosition());
                }
            } else if (loader.length > 0 && grinder.length > 0 && bin.length > 0 && bones.length > 0) {
                botstatus = "Making Bonemeal";
                if (bin[0].isOnScreen() && bin[0].isClickable()) {
                    if (bones[0].click("Use")) {
                        General.sleep(350, 650);
                        if (loader[0].click("Use Bones")) {
                            General.sleep(50000, 55850);
                        }
                    }
                } else {
                    Walking.walkTo(bin[0].getPosition());
                }
            }
        } else if (started_quest && hasItem(ITEM_SLIME) && talked_drezel && !hasItem(ITEM_ECTO) && isInsideTower() && !hasItem(ITEM_BONES) && hasItem(ITEM_BONEMEAL)) {
            RSObject[] stairs = Objects.find(20, 16647);
            RSObject[] ectofuntus = Objects.find(20, 16648);
            if (stairs.length > 0) {
                botstatus = "Climbing down";
                if (stairs[0].isOnScreen() && stairs[0].isClickable()) {
                    if (stairs[0].click("Climb-down")) {
                        General.sleep(250, 500);
                    }
                } else {
                    Walking.walkTo(stairs[0].getPosition());
                }
            } else if (ectofuntus.length > 0) {
                botstatus = "Worshipping ecto";
                if (ectofuntus[0].isOnScreen() && ectofuntus[0].isClickable()) {
                    if (ectofuntus[0].click("Worship")) {
                        General.sleep(2500, 3500);
                    }
                } else {
                    Walking.walkTo(ectofuntus[0].getPosition());
                }
            }
        } else if (started_quest && !hasItem(ITEM_SLIME) && talked_drezel && !hasItem(ITEM_ECTO) && isInsideTower() && !hasItem(ITEM_BONES) && !hasItem(ITEM_BONEMEAL)) {
            botstatus = "Talking to Ghost";
            RSNPC[] ghost = NPCs.findNearest(2988);
            if (ghost.length > 0) {
                if (ghost[0].isClickable() && ghost[0].isOnScreen() && ghost[0].isValid()) {
                    if (ghost[0].click("Talk-to")) {
                        General.sleep(1500, 2500);
                        talkGhostToken();
                    }
                } else {
                    Walking.walkTo(ghost[0].getPosition());
                }
            }
        } else if (started_quest && !hasItem(ITEM_SLIME) && talked_drezel && hasItem(ITEM_ECTO) && !hasItem(ITEM_BONES) && !hasItem(ITEM_BONEMEAL) && !talked_alice4 && !talked_husband4) {
            RSNPC[] alice = NPCs.findNearest(NPC_ALICE);
            RSNPC[] husband = NPCs.findNearest(NPC_HUSBAND);
            if (isInsideTower()) {
                botstatus = "Walking to Alice";
                WebWalking.walkTo(TILE_ALICE);
            } else if (!talked_alice1 && alice.length > 0) {
                botstatus = "Talking to Alice";
                if (alice[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToAlice1();
                }
            } else if (talked_alice1 && isInsideAliceRoom() && !talked_husband1) {
                botstatus = "Walking to husband";
                Walking.blindWalkTo(TILE_HUSBAND);
            } else if (talked_alice1 && isNPCnear(NPC_HUSBAND) && !talked_husband1 && husband.length > 0) {
                botstatus = "Talking to husband";
                if (husband[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToHusband1();
                }
            } else if (talked_alice1 && isNPCnear(NPC_HUSBAND) && talked_husband1 && husband.length > 0 && !talked_alice2) {
                botstatus = "Walking to Alice";
                Walking.blindWalkTo(TILE_ALICE);
            } else if (talked_alice1 && talked_husband1 && alice.length > 0 && !talked_alice2) {
                botstatus = "Talking to Alice";
                if (alice[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToAlice2();
                }
            } else if (talked_alice1 && isInsideAliceRoom() && talked_husband1 && talked_alice2 && !talked_husband2) {
                botstatus = "Walking to husband";
                Walking.blindWalkTo(TILE_HUSBAND);
            } else if (talked_alice1 && isNPCnear(NPC_HUSBAND) && talked_husband1 && husband.length > 0 && talked_alice2 && !talked_husband2) {
                botstatus = "Talking to husband";
                if (husband[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToHusband2();
                }
            } else if (talked_alice1 && isNPCnear(NPC_HUSBAND) && talked_husband1 && husband.length > 0 && talked_alice2 && talked_husband2 && !talked_alice3) {
                botstatus = "Walking to Alice";
                Walking.blindWalkTo(TILE_ALICE);
            } else if (talked_alice1 && talked_husband1 && alice.length > 0 && talked_alice2 && talked_husband2 && !talked_alice3) {
                botstatus = "Talking to Alice";
                if (alice[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToAlice3();
                }
            } else if (talked_alice1 && isInsideAliceRoom() && talked_husband1 && talked_alice2 && talked_husband2 && talked_alice3 && !talked_husband3) {
                botstatus = "Walking to husband";
                Walking.blindWalkTo(TILE_HUSBAND);
            } else if (talked_alice1 && isNPCnear(NPC_HUSBAND) && talked_husband1 && husband.length > 0 && talked_alice2 && talked_husband2 && talked_alice3 && !talked_husband3) {
                botstatus = "Talking to husband";
                if (husband[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToHusband3();
                }
            } else if (talked_alice1 && isNPCnear(NPC_HUSBAND) && talked_husband1 && husband.length > 0 && talked_alice2 && talked_husband2 && talked_alice3 && talked_husband3 && !talked_alice4) {
                botstatus = "Walking to Alice";
                Walking.blindWalkTo(TILE_ALICE);
            } else if (talked_alice1 && talked_husband1 && alice.length > 0 && talked_alice2 && talked_husband2 && talked_alice3 && talked_husband3 && !talked_alice4) {
                botstatus = "Talking to Alice";
                if (alice[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToAlice4();
                }
            }
        } else if (started_quest && talked_drezel && talked_alice4 && !hasItem(ITEM_CHICKEN) && !talked_ava1) { //has ecto
            RSNPC[] crone = NPCs.findNearest(NPC_CRONE);
            RSNPC[] husband = NPCs.findNearest(NPC_HUSBAND);
            RSItem[] amulet = Equipment.find(ITEM_GHOSTAMMY);
            RSItem[] amuletinv = Inventory.find(ITEM_GHOSTAMMY);
            if (isInsideAliceRoom()) {
                botstatus = "Walking to Old crone";
                WebWalking.walkTo(TILE_CRONE);
            } else if (isNPCnear(NPC_CRONE) && crone.length > 0 && !talked_crone1) {
                if (!hasItem(ITEM_GHOSTAMMY)) {
                    if (amulet.length > 0) {
                        if (GameTab.open(GameTab.TABS.EQUIPMENT)) {
                            General.sleep(350, 650);
                            if (amulet[0].click("Remove")) {
                                General.sleep(350, 650);
                            }
                        }
                    }
                } else {
                    botstatus = "Talking to Old crone";
                    if (crone[0].click("Talk-to")) {
                        General.sleep(1500, 2000);
                        talkToCrone1();
                    }
                }
            } else if (isNPCnear(NPC_CRONE) && crone.length > 0 && talked_crone1 && !talked_crone2) {
                botstatus = "Talking to Old crone again";
                if (crone[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToCrone2();
                }
            } else if (isNPCnear(NPC_CRONE) && crone.length > 0 && talked_crone1 && talked_crone2 && !talked_husband4) {
                botstatus = "Walking to husband";
                WebWalking.walkTo(TILE_HUSBAND);
            } else if (isNPCnear(NPC_HUSBAND) && husband.length > 0 && talked_crone1 && talked_crone2 && !talked_husband4) {
                if (hasItem(ITEM_GHOSTAMMY)) {
                    botstatus = "Wearing amulet";
                    if (amuletinv.length > 0) {
                        if (GameTab.open(GameTab.TABS.INVENTORY)) {
                            General.sleep(350, 650);
                            if (amuletinv[0].click("Wear")) {
                                General.sleep(350, 650);
                            }
                        }
                    }
                } else {
                    botstatus = "Talking to husband";
                    if (husband[0].click("Talk-to")) {
                        General.sleep(1500, 2000);
                        talkToHusband4();
                    }
                }
            } else if (isNPCnear(NPC_HUSBAND) && husband.length > 0 && talked_crone1 && talked_crone2 && talked_husband4 && !talked_husband5) {
                botstatus = "Talking to husband again";
                if (husband[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToHusband5();
                }
            } else if (talked_husband5 && Camera.getCameraAngle() < 88) {
                botstatus = "Cutscene";
                if (NPCChat.getClickContinueInterface() != null) {
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                    }
                }
            } else if (talked_husband5 && Camera.getCameraAngle() > 88 && !talked_husband6 && husband.length > 0) {
                botstatus = "Talking to husband";
                if (husband[0].click("Talk-to")) {
                    General.sleep(1500, 2000);
                    talkToHusband6();
                }
            }
        } else if (started_quest && talked_drezel && hasItem(ITEM_CHICKEN) && talked_alice4 && talked_husband6) {
            if (isNPCnear(NPC_HUSBAND)) {
                camera();
                RSItem[] glory = Inventory.find(ITEM_GLORY);
                if (glory.length > 0) {
                    botstatus = "Teleporting to Draynor";
                    if (glory[0].click("Rub")) {
                        General.sleep(1000, 1500);
                        tpToDraynor();
                    }
                }
            } else if (isInsideDraynor() && !talked_ava1) {
                botstatus = "Walking to Castle";
                WebWalking.walkTo(TILE_CASTLE);
            } else if (isInsideCastle() && !talked_ava1) {
                RSObject[] bookcase = Objects.find(2, 156);
                RSObject[] door = Objects.find(2, 11470);
                RSNPC[] ava = NPCs.findNearest(NPC_AVA);
                if (Player.getPosition().distanceTo(TILE_CASTLE) < 3) {
                    botstatus = "Walking to Door 1";
                    Walking.walkTo(new RSTile(3106, 3368));
                } else if (door.length > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(500, 750);
                    }
                } else if (bookcase.length > 0 && Player.getPosition().distanceTo(TILE_BOOKCASE) < 2) {
                    botstatus = "Opening bookcase";
                    if (bookcase[0].click("Search")) {
                        General.sleep(2000, 2500);
                    }
                } else if (ava.length > 0 && isNPCnear(NPC_AVA)) {
                    botstatus = "Talking to Ava";
                    if (ava[0].click("Talk-to")) {
                        General.sleep(3000, 4500);
                        talkToAva1();
                    }
                } else {
                    botstatus = "Walking to Bookcase";
                    Walking.walkTo(TILE_BOOKCASE);
                }
            }
        } else if (isInsideCastle() && talked_ava1 && !talked_witch1 && started_quest && talked_drezel && talked_alice4 && talked_husband6) {
            RSObject[] lever = Objects.find(3, 160);
            RSObject[] door = Objects.find(2, 11470);
            RSNPC[] witch = NPCs.findNearest(NPC_WITCH);
            if (lever.length > 0 && Player.getPosition().distanceTo(new RSTile(3094, 3357)) == 0) {
                botstatus = "Pulling lever";
                if (lever[0].click("Pull")) {
                    General.sleep(3000, 4000);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3098, 3358)) == 0) {
                botstatus = "Walking to door 1";
                Walking.walkTo(new RSTile(3103, 3363));
            } else if (door.length > 0) {
                botstatus = "Opening door";
                if (door[0].click("Open")) {
                    General.sleep(1000, 1500);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3103, 3363)) < 3) {
                botstatus = "Walking to door 2";
                Walking.walkTo(new RSTile(3102, 3371));
            } else if (witch.length > 0 && isNPCnear(NPC_WITCH)) {
                botstatus = "Talking to Witch";
                if (witch[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToWitch1();
                }
            }
        } else if (isInsideCastle() && talked_ava1 && talked_witch1 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && !talked_witch2) {
            RSNPC[] witch = NPCs.findNearest(NPC_WITCH);
            if (witch.length > 0 && isNPCnear(NPC_WITCH)) {
                botstatus = "Talking to Witch again";
                if (witch[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToWitch2();
                }
            }
        } else if (talked_ava1 && talked_witch2 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && talked_witch2 && !talked_ava2) {
            if (isNPCnear(NPC_WITCH) && isInsideCastle() && !hasItem(ITEM_MAGNET_BAR)) {
                RSItem[] glory = Inventory.find(ITEM_GLORY);
                if (glory.length > 0) {
                    botstatus = "Teleporting to Draynor";
                    if (glory[0].click("Rub")) {
                        General.sleep(1000, 1500);
                        tpToDraynor();
                    }
                }
            } else if (isInsideDraynor() && !hasItem(ITEM_MAGNET_BAR)) {
                botstatus = "Walking to Rimmington mine";
                WebWalking.walkTo(new RSTile(2976, 3241));
            } else if (Player.getPosition().distanceTo(new RSTile(2976, 3241)) > 0 && Player.getPosition().distanceTo(new RSTile(2976, 3241)) < 6 && hasItem(ITEM_MAGNET) && hasItem(ITEM_HAMMER) && Player.getRSPlayer().getOrientation() >= 1020 && Player.getRSPlayer().getOrientation() <= 1030) {
                botstatus = "Walking to spot";
                Walking.clickTileMS(new RSTile(2976, 3241), 1);
            } else if (Player.getPosition().distanceTo(new RSTile(2976, 3241)) == 0 && hasItem(ITEM_MAGNET) && hasItem(ITEM_HAMMER)) {
                botstatus = "Facing North";
                Walking.clickTileMS(new RSTile(2976, 3242), 1);
            } else if (Player.getPosition().distanceTo(new RSTile(2976, 3242)) == 0 && hasItem(ITEM_MAGNET) && hasItem(ITEM_HAMMER)) {
                botstatus = "Making Bar magnet";
                RSItem[] hammer = Inventory.find(ITEM_HAMMER);
                RSItem[] magnet = Inventory.find(ITEM_MAGNET);
                if (hammer.length > 0 && magnet.length > 0) {
                    if (hammer[0].click("Use")) {
                        General.sleep(250, 500);
                        if (magnet[0].click("Use Hammer")) {
                            General.sleep(3500, 4500);
                        }
                    }
                }
            } else if (Player.getPosition().distanceTo(new RSTile(2976, 3242)) == 0 && hasItem(ITEM_MAGNET_BAR)) {
                RSItem[] glory = Inventory.find(ITEM_GLORY);
                if (glory.length > 0) {
                    botstatus = "Teleporting to Draynor";
                    if (glory[0].click("Rub")) {
                        General.sleep(1000, 1500);
                        tpToDraynor();
                    }
                }
            } else if (isInsideDraynor() && !talked_ava2 && hasItem(ITEM_MAGNET_BAR)) {
                botstatus = "Walking to Castle";
                WebWalking.walkTo(TILE_CASTLE);
            } else if (isInsideCastle() && !talked_ava2 && hasItem(ITEM_MAGNET_BAR)) {
                RSObject[] bookcase = Objects.find(2, 156);
                RSObject[] door = Objects.find(2, 11470);
                RSNPC[] ava = NPCs.findNearest(NPC_AVA);
                if (Player.getPosition().distanceTo(TILE_CASTLE) < 3) {
                    botstatus = "Walking to Door 1";
                    Walking.walkTo(new RSTile(3106, 3368));
                } else if (door.length > 0) {
                    botstatus = "Opening door";
                    if (door[0].click("Open")) {
                        General.sleep(500, 750);
                    }
                } else if (bookcase.length > 0 && Player.getPosition().distanceTo(TILE_BOOKCASE) < 2) {
                    botstatus = "Opening bookcase";
                    if (bookcase[0].click("Search")) {
                        General.sleep(2000, 2500);
                    }
                } else if (ava.length > 0 && isNPCnear(NPC_AVA)) {
                    botstatus = "Talking to Ava";
                    if (ava[0].click("Talk-to")) {
                        General.sleep(3000, 4500);
                        talkToAva2();
                    }
                } else {
                    botstatus = "Walking to Bookcase";
                    Walking.walkTo(TILE_BOOKCASE);
                }
            }
        } else if (talked_ava1 && talked_witch2 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && talked_ava2 && !used_axe) {
            RSNPC[] tree = NPCs.findNearest(NPC_TREE);
            if (isNPCnear(NPC_AVA)) {
                RSItem[] glory = Inventory.find(ITEM_GLORY);
                if (glory.length > 0) {
                    botstatus = "Teleporting to Draynor";
                    if (glory[0].click("Rub")) {
                        General.sleep(1000, 1500);
                        tpToDraynor();
                    }
                }
            } else if (isInsideDraynor()) {
                botstatus = "Walking to Veronica";
                WebWalking.walkTo(new RSTile(3109, 3329));
            } else if (isNPCnear(3561)) {
                botstatus = "Walking to Tree";
                Walking.blindWalkTo(new RSTile(3108, 3342));
            } else if (tree.length > 0) {
                botstatus = "Chopping Tree";
                if (tree[0].click("Chop")) {
                    used_axe = true;
                    General.sleep(500, 750);
                }
            }
        } else if (talked_witch2 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && talked_ava2 && used_axe && !talked_ava3) {
            RSObject[] bookcase = Objects.find(2, 156);
            RSObject[] door = Objects.find(2, 11470);
            RSNPC[] ava = NPCs.findNearest(NPC_AVA);
            if (Player.getPosition().distanceTo(new RSTile(3108, 3342)) == 0) {
                botstatus = "Walking to Castle";
                WebWalking.walkTo(TILE_CASTLE);
            } else if (Player.getPosition().distanceTo(TILE_CASTLE) < 3) {
                botstatus = "Walking to Door 1";
                Walking.walkTo(new RSTile(3106, 3368));
            } else if (door.length > 0) {
                botstatus = "Opening door";
                if (door[0].click("Open")) {
                    General.sleep(500, 750);
                }
            } else if (bookcase.length > 0 && Player.getPosition().distanceTo(TILE_BOOKCASE) < 2) {
                botstatus = "Opening bookcase";
                if (bookcase[0].click("Search")) {
                    General.sleep(2000, 2500);
                }
            } else if (ava.length > 0 && isNPCnear(NPC_AVA)) {
                botstatus = "Talking to Ava";
                if (ava[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToAva3();
                }
            } else {
                botstatus = "Walking to Bookcase";
                Walking.walkTo(TILE_BOOKCASE);
            }
        } else if (talked_witch2 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && used_axe && talked_ava3 && !talked_turael2) {
            RSNPC[] btRsnpcs = NPCs.findNearest(4095);
            RSNPC[] turael = NPCs.findNearest(401);
            if (isNPCnear(NPC_AVA)) {
                RSItem[] games = Inventory.find(ITEM_GAMESNECK);
                if (games.length > 0) {
                    botstatus = "Teleporting to Burthope";
                    if (games[0].click("Rub")) {
                        General.sleep(1000, 1500);
                        tpToBurthope();
                    }
                }
            } else if (btRsnpcs.length > 0) {
                botstatus = "Walking to Slayer master";
                WebWalking.walkTo(new RSTile(2931, 3536));
            } else if (isNPCnear(401) && turael.length > 0 && !talked_turael1) {
                botstatus = "Talking to Turael";
                if (turael[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToTurael1();
                }
            } else if (isNPCnear(401) && turael.length > 0 && !talked_turael2) {
                botstatus = "Talking to Turael again";
                if (turael[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToTurael2();
                }
            }
        } else if (talked_witch2 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && used_axe && talked_ava3 && talked_turael2 && !talked_ava4) {
            RSNPC[] tree = NPCs.findNearest(NPC_TREE);
            RSObject[] bookcase = Objects.find(2, 156);
            RSObject[] door = Objects.find(2, 11470);
            RSNPC[] ava = NPCs.findNearest(NPC_AVA);
            if (isNPCnear(401) && hasItem(ITEM_MITHAXE_BLESSED)) {
                RSItem[] glory = Inventory.find(ITEM_GLORY);
                if (glory.length > 0) {
                    botstatus = "Teleporting to Draynor";
                    if (glory[0].click("Rub")) {
                        General.sleep(1000, 1500);
                        tpToDraynor();
                    }
                }
            } else if (isInsideDraynor()) {
                botstatus = "Walking to Veronica";
                WebWalking.walkTo(new RSTile(3109, 3329));
            } else if (isNPCnear(3561)) {
                botstatus = "Walking to Tree";
                Walking.blindWalkTo(new RSTile(3108, 3342));
            } else if (tree.length > 0 && !hasItem(ITEM_TWIGS)) {
                botstatus = "Chopping Tree";
                if (tree[0].click("Chop")) {
                    General.sleep(1500, 1750);
                }
            } else if (Player.getPosition().distanceTo(new RSTile(3108, 3342)) == 0 && hasItem(ITEM_TWIGS)) {
                botstatus = "Walking to Castle";
                WebWalking.walkTo(TILE_CASTLE);
            } else if (Player.getPosition().distanceTo(TILE_CASTLE) < 3) {
                botstatus = "Walking to Door 1";
                Walking.walkTo(new RSTile(3106, 3368));
            } else if (door.length > 0) {
                botstatus = "Opening door";
                if (door[0].click("Open")) {
                    General.sleep(500, 750);
                }
            } else if (bookcase.length > 0 && Player.getPosition().distanceTo(TILE_BOOKCASE) < 2) {
                botstatus = "Opening bookcase";
                if (bookcase[0].click("Search")) {
                    General.sleep(2000, 2500);
                }
            } else if (ava.length > 0 && isNPCnear(NPC_AVA)) {
                botstatus = "Talking to Ava";
                if (ava[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToAva4();
                }
            } else {
                botstatus = "Walking to Bookcase";
                Walking.walkTo(TILE_BOOKCASE);
            }
        } else if (talked_witch2 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && talked_turael2 && talked_ava4 && !talked_ava5) {
            RSNPC[] ava = NPCs.findNearest(NPC_AVA);
            if (ava.length > 0 && isNPCnear(NPC_AVA)) {
                botstatus = "Talking to Ava again";
                if (ava[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToAva5();
                }
            }
        } else if (talked_witch2 && started_quest && talked_drezel && talked_alice4 && talked_husband6 && talked_turael2 && talked_ava5) {
            RSNPC[] ava = NPCs.findNearest(NPC_AVA);
            if (hasItem(ITEM_NOTE)) {
                RSItem[] note = Inventory.find(ITEM_NOTE);
                if (note.length > 0) {
                    if (note[0].click("Translate")) {
                        General.sleep(265, 645);
                        RSInterface box1 = Interfaces.get(480, 24);
                        RSInterface box2 = Interfaces.get(480, 30);
                        RSInterface box3 = Interfaces.get(480, 33);
                        RSInterface box4 = Interfaces.get(480, 39);
                        RSInterface box5 = Interfaces.get(480, 42);
                        RSInterface box6 = Interfaces.get(480, 45);
                        RSInterface close = Interfaces.get(480, 2);
                        if (box1 != null && box2 != null && box3 != null && box4 != null && box5 != null && box6 != null && close != null) {
                            botstatus = "Solving puzzle";
                            box1.click("Off");
                            General.sleep(355, 645);
                            box2.click("Off");
                            General.sleep(355, 645);
                            box3.click("Off");
                            General.sleep(355, 645);
                            box4.click("Off");
                            General.sleep(355, 645);
                            box5.click("Off");
                            General.sleep(355, 645);
                            box6.click("Off");
                            General.sleep(355, 645);
                            close.click("Close");
                            General.sleep(355, 645);
                        }
                    }
                }
            } else if (hasItem(ITEM_NOTE_COMPLETED) && ava.length > 0 && !talked_ava6) {
                botstatus = "Talking to Ava";
                if (ava[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    talkToAva6();
                }
            } else if (hasItem(ITEM_NOTE_FINISHED) && ava.length > 0 && talked_ava6 && !talked_ava7) {
                RSItem[] note2 = Inventory.find(ITEM_NOTE_FINISHED);
                RSItem[] leather = Inventory.find(ITEM_LEATHER);
                if (note2.length > 0 && leather.length > 0) {
                    botstatus = "Making container";
                    if (leather[0].click("Use")) {
                        General.sleep(250, 500);
                        if (note2[0].click("Use Hard leather")) {
                            General.sleep(250, 500);
                        }
                    }
                }
            } else if (hasItem(ITEM_CONTAINER) && ava.length > 0 && talked_ava6 && !talked_ava7) {
                botstatus = "Talking to Ava, finish";
                if (ava[0].click("Talk-to")) {
                    General.sleep(3000, 4500);
                    if (NPCChat.getClickContinueInterface() != null) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.clickContinue(true)) {
                                talked_ava7 = true;
                                sleep(500, 650);
                            }
                        }
                    }
                }
            }
        }
    }

    private void talkToAva6() {
        if (NPCChat.getClickContinueInterface() != null) { //8x
            for (int i = 0; i < 8; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 7) {
                    talked_ava6 = true;
                }
            }
        }
    }

    private void talkToAva5() {
        if (NPCChat.getClickContinueInterface() != null) { //5x
            for (int i = 0; i < 5; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 4) {
                    talked_ava5 = true;
                }
            }
        }
    }

    private void talkToAva4() {
        if (NPCChat.getClickContinueInterface() != null) { //5x
            for (int i = 0; i < 5; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 4) {
                    talked_ava4 = true;
                }
            }
        }
    }

    private void talkToTurael2() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("Hello, I'm here about those trees again.", true)) {
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.selectOption("I'd love one, thanks.", true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        talked_turael2 = true;
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

    private void talkToTurael1() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("I'm here about a quest.", true)) { //7
                    sleep(500, 650);
                    for (int i = 0; i < 7; i++) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                        }
                        if (i >= 6) {
                            talked_turael1 = true;
                        }
                    }
                }
            }
        }
    }

    private void talkToAva3() {
        if (NPCChat.getClickContinueInterface() != null) { //9x
            for (int i = 0; i < 9; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 8) {
                    talked_ava3 = true;
                }
            }
        }
    }

    private void talkToAva2() {
        if (NPCChat.getClickContinueInterface() != null) { //11x
            for (int i = 0; i < 11; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 10) {
                    talked_ava2 = true;
                }
            }
        }
    }

    private void talkToWitch2() {
        if (NPCChat.getClickContinueInterface() != null) { //4x
            for (int i = 0; i < 4; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 3) {
                    talked_witch2 = true;
                }
            }
        }
    }

    private void talkToWitch1() {
        if (NPCChat.getClickContinueInterface() != null) { //7x
            for (int i = 0; i < 7; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 6) {
                    talked_witch1 = true;
                }
            }
        }
    }

    private void talkToAva1() {
        if (NPCChat.getClickContinueInterface() != null) { //12x
            for (int i = 0; i < 12; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 11) {
                    talked_ava1 = true;
                }
            }
        }
    }

    private void talkToHusband6() {
        if (NPCChat.getClickContinueInterface() != null) { //5x
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("Could I buy those chickens now, then?", true)) {
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.selectOption("Could I buy 2 chickens?", true)) {
                                sleep(500, 650);
                                if (NPCChat.clickContinue(true)) {
                                    sleep(500, 650);
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                        talked_husband6 = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void talkToHusband5() {
        if (NPCChat.getClickContinueInterface() != null) { //5x
            for (int i = 0; i < 5; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 4) {
                    talked_husband5 = true;
                }
            }
        }
    }

    private void talkToHusband4() {
        if (NPCChat.getClickContinueInterface() != null) { //4x
            sleep(500, 650);
            if (NPCChat.clickContinue(true)) {
                sleep(800, 950);
                if (NPCChat.clickContinue(true)) {
                    sleep(800, 950);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.selectOption("Okay, you need it more than I do, I suppose.", true)) { //3x
                                sleep(500, 650);
                                for (int i = 0; i < 3; i++) {
                                    if (NPCChat.clickContinue(true)) {
                                        sleep(500, 650);
                                    }
                                    if (i >= 2) {
                                        talked_husband4 = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void talkToCrone2() {
        if (NPCChat.getClickContinueInterface() != null) { //5x
            for (int i = 0; i < 5; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 4) {
                    talked_crone2 = true;
                }
            }
        }
    }

    private void talkToCrone1() {
        if (NPCChat.getClickContinueInterface() != null) { //10x
            for (int i = 0; i < 10; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 9) {
                    talked_crone1 = true;
                }
            }
        }
    }

    private void talkToAlice4() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("I'm here about a quest.", true)) { //7x
                    sleep(500, 650);
                    for (int i = 0; i < 7; i++) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                        }
                        if (i >= 6) {
                            talked_alice4 = true;
                        }
                    }
                }
            }
        }
    }

    private void talkToHusband3() {
        if (NPCChat.getClickContinueInterface() != null) { //3x
            for (int i = 0; i < 3; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 2) {
                    talked_husband3 = true;
                }
            }
        }
    }

    private void talkToAlice3() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("I'm here about a quest.", true)) { //5x
                    sleep(500, 650);
                    for (int i = 0; i < 5; i++) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                        }
                        if (i >= 4) {
                            talked_alice3 = true;
                        }
                    }
                }
            }
        }
    }

    private void talkToHusband2() {
        if (NPCChat.getClickContinueInterface() != null) { //7x
            for (int i = 0; i < 7; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 6) {
                    talked_husband2 = true;
                }
            }
        }
    }

    private void talkToAlice2() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("I'm here about a quest.", true)) { //6x
                    sleep(500, 650);
                    for (int i = 0; i < 6; i++) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                        }
                        if (i >= 5) {
                            talked_alice2 = true;
                        }
                    }
                }
            }
        }
    }

    private void talkToHusband1() {
        if (NPCChat.getClickContinueInterface() != null) { //9x
            for (int i = 0; i < 9; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 8) {
                    talked_husband1 = true;
                }
            }
        }
    }

    private void talkToAlice1() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("I'm here about a quest.", true)) { //6x
                    sleep(500, 650);
                    for (int i = 0; i < 6; i++) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                        }
                        if (i >= 5) {
                            talked_alice1 = true;
                        }
                    }
                }
            }
        }
    }

    private void talkGhostToken() {
        if (NPCChat.getClickContinueInterface() != null) { //8x
            for (int i = 0; i < 2; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
            }
        }
    }

    private void talkDrezel() {
        if (NPCChat.getClickContinueInterface() != null) { //8x
            for (int i = 0; i < 9; i++) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                }
                if (i >= 8) {
                    talked_drezel = true;
                }
            }
        }
    }

    private void startQuest() {
        if (NPCChat.getClickContinueInterface() != null) {
            if (NPCChat.clickContinue(true)) {
                sleep(500, 650);
                if (NPCChat.selectOption("I would be happy to make your home a better place.", true)) { //10x
                    sleep(500, 650);
                    for (int i = 0; i < 10; i++) {
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                        }
                        if (i >= 9) {
                            started_quest = true;
                        }
                    }
                }
            }
        }
    }

    private void tpToDraynor() {
        RSInterface gamebox = Interfaces.get(219, 0);
        if (gamebox != null) {
            RSInterface[] list = gamebox.getChildren();
            if (list != null) {
                list[3].click("Continue");
                sleep(5200, 5250);
            }
        }
    }

    private void tpToBurthope() {
        RSInterface gamebox = Interfaces.get(219, 0);
        if (gamebox != null) {
            RSInterface[] list = gamebox.getChildren();
            if (list != null) {
                list[1].click("Continue");
                sleep(5200, 5250);
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

    private boolean isInsideGE() {
        return GE_AREA.contains(Player.getPosition());
    }

    private boolean isInsideTower() {
        return TOWER_AREA.contains(Player.getPosition());
    }

    private boolean isInsideDraynor() {
        return DRAYNOR_AREA.contains(Player.getPosition());
    }

    private boolean isInsideVarrock() {
        return VARROCK_AREA.contains(Player.getPosition());
    }

    private boolean isInsideCastle() {
        return ERNEST_AREA.contains(Player.getPosition());
    }

    private boolean isInsideAliceRoom() {
        return ALICE_AREA.contains(Player.getPosition());
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

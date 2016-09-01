package scripts.redDragSkills;

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
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = "Marko", category = "RedDrags", name = "2. Skills", description = "WC, CRAFT, AGILITY [START WITH AXES AT CASTLEWARS, LEATHER, ETC IN BANK]", version = 1.1)
public class redDragSkills extends Script implements Painting, Ending {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;
    public int bot_doing = 0;

    /* WC */
    public final int AXE_IRON = 1349;
    public final int AXE_STEEL = 1353;
    public final int AXE_MITH = 1355;
    public final int AXE_ADDY = 1357;
    public final int TREE_ID = 1276;
    public final int OAKTREE_ID = 1751;
    public final int TREE_LOG = 1511;
    public final int OAKTREE_LOG = 1521;
    public final int V_TAB = 8007;
    public final int[] ALL_AXES = {1349, 1353, 1355, 1357, 8007};

    /* CRAFT */
    public final int LEATHER = 1741;
    public final int THREAD = 1734;
    public final int NEEDLE = 1733;

    /* AGILITY */
    public final int CAMMY_TAB = 8010;
    public final int FOOD_ID = 333;
    public final int FOOD_NUM = 18;

    /* -  -  -  -  -  -  -  - */
    public int totalHerbs = 0;
    public int totalMoney = 0;
    public boolean SET_FOOD = false;
    public boolean SLEEPER = false;
    public RSPlayer[] players_1 = null;
    public RSPlayer[] players_2 = null;
    public long timeRan_1 = 0;
    public long timeRan_2 = 0;

    private final static Area GE_AREA = new Area(new RSTile[]{
        new RSTile(3158, 3494, 0), new RSTile(3171, 3494, 0),
        new RSTile(3171, 3485, 0), new RSTile(3158, 3485, 0)});

    private final static Area AGILITY_AREA = new Area(new RSTile[]{
        new RSTile(2469, 3440, 0), new RSTile(2491, 3440, 0),
        new RSTile(2491, 3414, 0), new RSTile(2469, 3414, 0)});

    /*private final RSArea WC_AREA = new RSArea(new RSTile[] { 
        new RSTile(2417, 3367, 0), 
        new RSTile(2421, 3388, 0), 
        new RSTile(2441, 3386, 0), 
        new RSTile(2441, 3366, 0)
        });*/
    private final RSArea WC_AREA = new RSArea(new RSTile[]{
        new RSTile(2457, 3195, 0), null,
        new RSTile(2456, 3197, 0),
        new RSTile(2456, 3212, 0),
        new RSTile(2482, 3213, 0),
        new RSTile(2479, 3189, 0)
    });

    private final static Area CAMMY_AREA = new Area(new RSTile[]{
        new RSTile(2750, 3482), new RSTile(2764, 3482),
        new RSTile(2764, 3471), new RSTile(2750, 3471)});

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
                    hoveringAgil();
                    antibans();
                    break;
                case WALK_TO_BANK:
                    botstatus = "Walking to bank";
                    WebWalking.walkToBank();
                    break;
                case FINISHED:
                    General.println("We're done");
                    Login.logout();
                    stop_script = true;
                    break;
                case ERROR:
                    General.println("We've got a problem...");
                    break;
                case WC:
                    botstatus = "Woodcutting...";
                    antibans();
                    startWC();
                    break;
                case CRAFTING:
                    botstatus = "Crafting...";
                    antibans();
                    startCrafting();
                    break;
                case AGILITY:
                    botstatus = "Agility...";
                    antibans();
                    startAgility();
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
        WC,
        CRAFTING,
        AGILITY,
        FINISHED,
        ERROR
    }

    //powerwc do 35, 19 craft GE full world, agility 30 grand tree empty w
    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (Skills.getActualLevel(SKILLS.WOODCUTTING) < 35 && Skills.getActualLevel(SKILLS.CRAFTING) < 19 && Skills.getActualLevel(SKILLS.AGILITY) < 30) {
            //} else if (Skills.getActualLevel(SKILLS.WOODCUTTING) < 90 && Skills.getActualLevel(SKILLS.CRAFTING) >== 1 && Skills.getActualLevel(SKILLS.AGILITY) >== 1) {
            //wc
            return State.WC;
        } else if (Skills.getActualLevel(SKILLS.WOODCUTTING) >= 35 && Skills.getActualLevel(SKILLS.CRAFTING) < 19 && Skills.getActualLevel(SKILLS.AGILITY) < 30) {
            //craft
            return State.CRAFTING;
        } else if (Skills.getActualLevel(SKILLS.WOODCUTTING) >= 35 && Skills.getActualLevel(SKILLS.CRAFTING) >= 19 && Skills.getActualLevel(SKILLS.AGILITY) < 30) {
            //agil
            return State.AGILITY;
        } else if (Skills.getActualLevel(SKILLS.WOODCUTTING) >= 35 && Skills.getActualLevel(SKILLS.CRAFTING) >= 19 && Skills.getActualLevel(SKILLS.AGILITY) >= 30) {
            return State.FINISHED;
        }
        return State.ERROR;
    }

    private void startWC() {
        if (hasAllAxes()) {
            if (isInsideWC()) {
                if (!checkNearbyPlayers()) { //players nearby
                    if (!Inventory.isFull() && !Player.isMoving() && Player.getAnimation() == -1) {
                        if (Skills.getActualLevel(SKILLS.WOODCUTTING) < 15) {
                            if (cutTree(TREE_ID)) {
                                General.sleep(350, 650);
                            }
                        } else if (cutTree(OAKTREE_ID)) {
                            General.sleep(350, 650);
                        }
                    } else if (Inventory.isFull() && Player.getAnimation() == -1) {//powerdrop
                        if (Skills.getActualLevel(SKILLS.WOODCUTTING) < 15) {
                            dropAllLogs(TREE_LOG);
                        } else {
                            dropAllLogs(OAKTREE_LOG);
                        }
                    }
                } else { //worldhop
                    hopWorld();
                }
            } else {
                botstatus = "Walking to WC area";
                WebWalking.walkTo(new RSTile(2466, 3201, 0));
            }
        } else {
            getAxes();
        }
    }

    public boolean cutTree(final int TYPE) {
        RSObject[] Tree = Objects.findNearest(20, TYPE);
        if (Tree.length > 0 && Tree[0] != null) {
            if (Tree[0].isOnScreen()) {
                if (Tree[0].click("Chop down")) {
                    botstatus = "Clicked tree";
                    return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100, 250);
                            return Player.getAnimation() == 869 || Player.getAnimation() == 877 || Player.getAnimation() == 875 || Player.getAnimation() == 871;
                        }
                    }, 2500);
                }
            } else if (Player.getPosition().distanceTo(Tree[0].getPosition()) >= 6) {
                botstatus = "Walking to tree";
                WebWalking.walkTo(Tree[0].getPosition());
                return false;
            } else if (Player.getPosition().distanceTo(Tree[0].getPosition()) <= 5 && !Tree[0].isOnScreen()) {
                Camera.turnToTile(Tree[0].getPosition());
                sleep(381, 791);
                if (!Tree[0].isOnScreen()) {
                    WebWalking.walkTo(Tree[0].getPosition());
                    if (Player.isMoving()) {
                        sleep(500, 900);
                    }
                }
                return false;
            }
        } else {
            botstatus = "Waiting for trees..";
            General.sleep(3500, 6000);
            return false;
        }
        return false;
    }

    private void dropAllLogs(final int LOG_TYPE) {
        if (Inventory.find(LOG_TYPE).length > 0) {
            Inventory.dropAllExcept(ALL_AXES);
        }
    }

    private void getAxes() {
        if (Banking.isInBank()) {
            if (Banking.isBankScreenOpen()) {
                Banking.withdraw(1, AXE_IRON);
                General.sleep(250, 650);
                Banking.withdraw(1, AXE_STEEL);
                General.sleep(250, 650);
                Banking.withdraw(1, AXE_MITH);
                General.sleep(250, 650);
                Banking.withdraw(1, AXE_ADDY);
                General.sleep(250, 650);
                Banking.withdraw(1, V_TAB);
                General.sleep(250, 650);
            } else {
                Banking.openBank();
            }
        } else {
            WebWalking.walkToBank();
        }
    }

    private boolean hasAllAxes() {
        return Inventory.getCount(AXE_IRON) > 0 && Inventory.getCount(AXE_STEEL) > 0 && Inventory.getCount(AXE_MITH) > 0 && Inventory.getCount(AXE_ADDY) > 0 && Inventory.getCount(V_TAB) > 0;
    }

    private void startAgility() {
        if (isInsideAGILITY()) {
            if (checkNearbyPlayers()) {
                hopWorld();
            }
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
            final int LOG_ID = 23145;
            final RSTile LOG_TILE = new RSTile(2474, 3436, 0);
            final int NETUP_ID = 23134;
            final RSTile NETUP_TILE = new RSTile(2474, 3429, 0);
            final int TREEUP_ID = 23559;
            final RSTile TREEUP_TILE0 = new RSTile(2471, 3423, 1);
            final RSTile TREEUP_TILE1 = new RSTile(2473, 3423, 1);
            final RSTile TREEUP_TILE2 = new RSTile(2475, 3423, 1);
            final int ROPE_ID = 23557;
            final RSTile ROPE_TILE = new RSTile(2473, 3420, 2);
            final int TREEDOWN_ID = 23560;
            final RSTile TREEDOWN_TILE = new RSTile(2483, 3420, 2);
            final int NETOVER_ID = 23135;
            final RSTile NETOVER_TILE = new RSTile(2487, 3420, 0);
            final int PIPE_ID = 23138;
            final RSTile PIPE_TILE0 = new RSTile(2483, 3428, 0);
            final RSTile PIPE_TILE1 = new RSTile(2485, 3428, 0);
            final RSTile PIPE_TILE2 = new RSTile(2487, 3428, 0);
            final RSTile FINISH_TILE = new RSTile(2484, 3437, 0);
            //course start
            if (Player.getPosition().distanceTo(LOG_TILE) < 5) {
                RSObject[] LOG = Objects.find(5, LOG_ID);
                if (LOG.length > 0 && Player.getAnimation() == -1 && !Player.isMoving()) {
                    botstatus = "Walking across log";
                    if (LOG[0].click("Walk-across")) {
                        General.sleep(4000, 4500);
                    }
                }
            } else if (Player.getPosition().distanceTo(NETUP_TILE) == 0 || Player.getPosition().distanceTo(new RSTile(2474, 3426, 0)) == 0 || Player.getPosition().distanceTo(new RSTile(2473, 3426, 0)) == 0) {
                RSObject[] NET = Objects.findNearest(7, NETUP_ID);
                if (NET.length > 0 && Player.getAnimation() == -1 && !Player.isMoving()) {
                    botstatus = "Climbing over net";
                    if (NET[0].click("Climb-over")) {
                        General.sleep(2900, 3300);
                    }
                }
            } else if (Player.getPosition().distanceTo(TREEUP_TILE0) == 0 || Player.getPosition().distanceTo(TREEUP_TILE1) == 0 || Player.getPosition().distanceTo(TREEUP_TILE2) == 0) {
                RSObject[] BRANCH = Objects.find(5, TREEUP_ID);
                if (BRANCH.length > 0 && Player.getAnimation() == -1 && !Player.isMoving()) {
                    botstatus = "Climbing tree";
                    if (BRANCH[0].click("Climb")) {
                        General.sleep(1300, 1500);
                    }
                }
            } else if (Player.getPosition().distanceTo(ROPE_TILE) == 0) {
                RSObject[] ROPE = Objects.find(8, ROPE_ID);
                if (ROPE.length > 0 && Player.getAnimation() == -1 && !Player.isMoving()) {
                    botstatus = "Walking on rope";
                    if (ROPE[0].click("Walk-on")) {
                        General.sleep(4500, 5000);
                    }
                }
            } else if (Player.getPosition().distanceTo(TREEDOWN_TILE) == 0) {
                RSObject[] ROPE = Objects.find(5, TREEDOWN_ID);
                if (ROPE.length > 0 && Player.getAnimation() == -1 && !Player.isMoving()) {
                    botstatus = "Climbing down tree";
                    if (ROPE[0].click("Climb-down")) {
                        General.sleep(1300, 1500);
                    }
                }
            } else if (Player.getPosition().distanceTo(NETOVER_TILE) == 0) {
                RSObject[] ROPE = Objects.findNearest(15, NETOVER_ID);
                if (ROPE.length > 0 && Player.getAnimation() == -1 && !Player.isMoving()) {
                    botstatus = "Climbing over net";
                    if (ROPE[0].isOnScreen() && ROPE[0].isClickable()) {
                        if (ROPE[0].click("Climb-over")) {
                            General.sleep(3200, 3500);
                        }
                    } else {
                        Camera.turnToTile(ROPE[0].getPosition());
                    }
                }
            } else if (Player.getPosition().distanceTo(PIPE_TILE0) == 0 || Player.getPosition().distanceTo(PIPE_TILE1) == 0 || Player.getPosition().distanceTo(PIPE_TILE2) == 0) {
                RSObject[] BRANCH = Objects.findNearest(5, PIPE_ID);
                if (BRANCH.length > 0 && Player.getAnimation() == -1 && !Player.isMoving()) {
                    if (BRANCH[0].isClickable() && BRANCH[0].isOnScreen()) {
                        RSModel model = BRANCH[0].getModel();
                        if (model != null) {
                            Polygon modelArea = model.getEnclosedArea();
                            if (!modelArea.contains(Mouse.getPos())) {
                                BRANCH[0].hover();
                                botstatus = "Squeezing through pipe";
                                if (BRANCH[0].click("Squeeze-through")) {
                                    General.sleep(1500, 1800);
                                }
                            }
                        }
                    }
                }
            } else if (Player.getPosition().distanceTo(FINISH_TILE) == 0) {
                botstatus = "Walking to start log";
                Walking.walkTo(LOG_TILE);
            }
        } else if (isInsideCammy()) {
            WebWalking.walkTo(new RSTile(2461, 3382));
        } else if (isDoorNear() && Player.getPosition().distanceTo(new RSTile(2461, 3385, 0)) != 0) {
            openDoor();
        } else if (isDoorNear() && Player.getPosition().distanceTo(new RSTile(2461, 3385, 0)) == 0) {
            WebWalking.walkTo(new RSTile(2474, 3437));
        } else if (Inventory.find(CAMMY_TAB).length > 0) {
            RSItem[] cammy = Inventory.find(CAMMY_TAB);
            if (cammy.length > 0) {
                if (Banking.isBankScreenOpen()) {
                    Banking.close();
                } else if (cammy[0].click("Break")) {
                    General.sleep(3500, 4500);
                }
            }
        } else {
            getTAB();
        }
    }

    private void hoveringAgil() {
        if (this.abc_util.shouldHover()) {
            if (Player.isMoving() && !Game.isRunOn()) {
                final RSTile LOG_TILE = new RSTile(2474, 3436, 0);
                final int LOG_ID = 23145;
                if (Player.getPosition().distanceTo(LOG_TILE) < 4) {
                    RSObject[] LOG = Objects.find(3, LOG_ID);
                    if (LOG.length > 0) {
                        if (LOG[0].isClickable() && LOG[0].isOnScreen()) {
                            if (LOG[0].click("Walk-across")) {
                                General.sleep(4000, 4500);
                            }
                        }
                    }
                }
            }
        }
    }

    private void openDoor() {
        RSObject[] door = Objects.findNearest(8, 190);
        if (door.length > 0) {
            door[0].click("Open");
            General.sleep(3500, 4500);
            if (NPCChat.getClickContinueInterface() != null) {
                if (NPCChat.clickContinue(true)) {
                    sleep(500, 650);
                    if (NPCChat.clickContinue(true)) {
                        sleep(500, 650);
                        if (NPCChat.clickContinue(true)) {
                            sleep(500, 650);
                            if (NPCChat.selectOption("OK then.", true)) {
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
            }
        }
    }

    private boolean isDoorNear() {
        RSObject[] door = Objects.findNearest(8, 190);
        return door.length > 0;
    }

    private void getTAB() {
        if (Banking.isInBank()) {
            if (Banking.isBankScreenOpen()) {
                Banking.depositAll();
                Banking.withdraw(1, CAMMY_TAB);
                General.sleep(250, 650);
                Banking.close();
            } else {
                Banking.openBank();
            }
        } else {
            WebWalking.walkToBank();
        }
    }

    private void startCrafting() {
        if (!isInsideWC()) {
            if (isInsideGE()) {
                if (hasAllCraftItems()) {
                    if (Inventory.find(LEATHER).length > 1) {
                        if (!isCurrentlyCrafting()) {
                            if (Skills.getActualLevel(SKILLS.CRAFTING) < 7) {
                                craft(1); //gloves
                            } else if (Skills.getActualLevel(SKILLS.CRAFTING) < 9 && Skills.getActualLevel(SKILLS.CRAFTING) >= 7) {
                                craft(2); //boots
                            } else if (Skills.getActualLevel(SKILLS.CRAFTING) < 11 && Skills.getActualLevel(SKILLS.CRAFTING) >= 9) {
                                craft(3); //cowl
                            } else if (Skills.getActualLevel(SKILLS.CRAFTING) < 14 && Skills.getActualLevel(SKILLS.CRAFTING) >= 11) {
                                craft(4); //vambs
                            } else if (Skills.getActualLevel(SKILLS.CRAFTING) >= 14) {
                                craft(5); //body
                            }
                        } else {
                            //General.sleep(4500, 6000);
                        }
                    } else {
                        getLeather();
                    }
                } else {
                    getCraftitems();
                }
            } else {
                WebWalking.walkTo(new RSTile(3167, 3489));
            }
        } else {
            RSItem[] vtab = Inventory.find(V_TAB);
            if (vtab.length > 0) {
                if (vtab[0].click("Break")) {
                    General.sleep(3900, 4800);
                }
            }
        }
    }

    private boolean isCurrentlyCrafting() {
        int startXp = Skills.getXP(Skills.SKILLS.CRAFTING);
        long t = System.currentTimeMillis();
        // Wait for 3 seconds before timing out...
        while (Timing.timeFromMark(t) < 4250) {
            if (Skills.getXP(Skills.SKILLS.CRAFTING) > startXp) {
                // If we gained exp...
                return true;
            }

            sleep(100);
        }

        return false;
    }

    private boolean isCrafting() {
        int total = 0;
        for (int i = 0; i < 10; i++, General.sleep(20, 30)) {
            if (Player.getAnimation() == 1249) {
                total++;
            }
        }
        return total > 3;
    }

    private void craft(final int type) {
        switch (type) {
            case 1:
                if (Inventory.find(LEATHER).length > 1) {
                    makeAllLeather(154, 93);
                }
                break;
            case 2:
                if (Inventory.find(LEATHER).length > 1) {
                    makeAllLeather(154, 96);
                }
                break;
            case 3:
                if (Inventory.find(LEATHER).length > 1) {
                    makeAllLeather(154, 122);
                }
                break;
            case 4:
                if (Inventory.find(LEATHER).length > 1) {
                    makeAllLeather(154, 99);
                }
                break;
            case 5:
                if (Inventory.find(LEATHER).length > 1) {
                    makeAllLeather(154, 90);
                }
                break;
            default:
                break;
        }
    }

    private void makeAllLeather(final int inter, final int child) {
        RSItem[] needle = Inventory.find(NEEDLE);
        RSItem[] leather = Inventory.find(LEATHER);
        needle[0].click("Use");
        General.sleep(200, 450);
        leather[0].click("Use needle");
        General.sleep(500, 950);
        RSInterface windowInterface = Interfaces.get(inter, child);
        if (windowInterface != null) {
            if (windowInterface.click("Make All")) {
                General.sleep(1500, 1950);
            }
        }
    }

    private boolean hasAllCraftItems() {
        return Inventory.find(NEEDLE).length > 0 && Inventory.find(THREAD).length > 0;
    }

    public void hopWorld() {
        int world = WorldHop.getRandomWorld(true);
        General.println("[WORLDHOP] Attempting world hopping to: " + world);
        //WorldHop.hopTo(world);
        FCInGameHopper.hop(world);
        General.sleep(1000, 1500);
        players_1 = null;
        players_2 = null;
        timeRan_1 = 0;
        timeRan_2 = 0;
    }

    public boolean checkNearbyPlayers() {
        RSPlayer[] players = Players.getAll();
        if (players.length > 1) {
            for (int i = 0; i < players.length; i++) {
                if (Player.getPosition().distanceTo(players[i].getPosition()) < 8 && !players[i].getName().equalsIgnoreCase(Player.getRSPlayer().getName())) {
                    General.println("Player nearby: " + players[i].getName());
                    return true;
                }
            }
        }
        return false;
    }

    private void getCraftitems() {
        if (Banking.isBankScreenOpen()) {
            Banking.depositAll();
            General.sleep(250, 650);
            Banking.withdraw(1, NEEDLE);
            General.sleep(250, 650);
            Banking.withdraw(100, THREAD);
            General.sleep(250, 650);
            Banking.close();
        } else {
            Banking.openBank();
        }
    }

    private void getLeather() {
        if (Banking.isInBank()) {
            if (Banking.isBankScreenOpen()) {
                int[] except = {NEEDLE, THREAD};
                Banking.depositAllExcept(except);
                General.sleep(250, 650);
                Banking.withdraw(26, LEATHER);
                General.sleep(250, 650);
                Banking.close();
            } else {
                Banking.openBank();
            }
        } else {
            WebWalking.walkToBank();
        }
    }

    private boolean isInsideGE() {
        return GE_AREA.contains(Player.getPosition());
    }

    private boolean isInsideWC() {
        return WC_AREA.contains(Player.getPosition());
    }

    private boolean isInsideAGILITY() {
        return AGILITY_AREA.contains(Player.getPosition());
    }

    private boolean isInsideCammy() {
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
        g.drawString("WC: " + Skills.getActualLevel(SKILLS.WOODCUTTING) + ", Craft: " + Skills.getActualLevel(SKILLS.CRAFTING) + ", Agility: " + Skills.getActualLevel(SKILLS.AGILITY), 230, 340);
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

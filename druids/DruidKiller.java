package scripts.druids;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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

@ScriptManifest(authors = "Marko", category = "Combat", name = "Druid Killer", description = "Ardougne druid killer", version = 1.2)
public class DruidKiller extends Script implements Painting, Ending {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;

    public final int FOOD_ID = 333;
    public int FOOD_NUM;
    //kwuarm, dwarf weed, ranarr, harra, lantadyme, toadflax, irit, avantoe, cadantine, torstol, snapdrag, law, chaos, mith bolts
    public final int[] HERB_IDS = {213, 217, 207, 205, 2485, 3049, 209, 211, 215, 219, 3051, 563, 562, 9142};
    public final int[] VALUE_IDS = {213, 217, 207, 205, 2485, 3049, 209, 211, 215, 219, 3051, 563, 562, 9142, 333};
    public final int[] JUNK_IDS = {526, 995, 556, 231, 227, 13471};
    public final int[] LADDER_IDS = {16685, 17385};
    public final int DOOR_ID = 176;
    public final int DRUID_ID = 2878;
    public final int BOOTH_ID = 6944;
    private final RSTile DRUIRSTile = new RSTile(2566, 3356);
    private final RSTile DOORSTile = new RSTile(2565, 3356);
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

    private final static Area DRUID_AREA = new Area(new RSTile[]{
        new RSTile(2559, 3359, 0), new RSTile(2565, 3359, 0),
        new RSTile(2565, 3353, 0), new RSTile(2559, 3353, 0)});

    private final static int[] PVP_WORLDS = new int[]{25,37,45,52,57};

    @Override
    public void run() {
        General.useAntiBanCompliance(true);
        this.abc_util = new ABCUtil();
        while (!stop_script) {
            if (!SET_FOOD && Login.getLoginState() == Login.STATE.INGAME) {
                setFoodNum();
                SET_FOOD = true;
            }
            switch (state()) {
                case LOGGED_IN:
                    botstatus = "Waiting for login...";
                    sleep(3000, 5000);
                    break;
                case WALKING:
                    botstatus = "Walking & hover...";
                    break;
                case WALK_TO_BANK:
                    botstatus = "Walking to bank";
                    WebWalking.walkToBank();
                    break;
                case DEPOSIT_ITEMS:
                    botstatus = "Depositing items";
                    deposit();
                    break;
                case WALK_TO_DRUIDS:
                    botstatus = "Walking to druids";
                    WebWalking.walkTo(DRUIRSTile);
                    break;
                case PICK_LOCK_DOOR:
                    botstatus = "Attempting to picklock...";
                    picklock();
                    break;
                case OPEN_DOOR:
                    botstatus = "Opening door";
                    opendoor();
                    break;
                case WALK_TO_DOOR:
                    botstatus = "Walking to door";
                    Walking.walkTo(DOORSTile);
                    break;
                case IN_COMBAT:
                    botstatus = "In combat";
                    heal();
                    checkSleeping();
                    pickups();
                    antibans();
                    break;
                case PICKUPS:
                    botstatus = "Picking up loot";
                    pickups();
                    break;
                case ATTACK:
                    if (Player.getRSPlayer().getCombatLevel() < 27) {
                        heal();
                    }
                    if (isInArray(PVP_WORLDS, WorldHopper.getWorld())) {
                        General.println("We're in a PVP world, logout & stopping script.");
                        Login.logout();
                        General.sleep(1500, 2000);
                        stop_script = true;
                    }
                    checkNearbyPlayers();
                    botstatus = "Attacking druid";
                    dropJunk();
                    attack();
                    break;
                case EAT_NOW:
                    botstatus = "Eating";
                    eat();
                    break;
                case LADDER:
                    botstatus = "Climbing back...";
                    useLadder();
                    break;
                case SOMETHING_WENT_WRONG:
                    General.println("Stopping script, something went wrong");
                    stop_script = true;
                    break;
            }
            // control cpu usage
            General.sleep(100, 250);
        }
    }

    public static boolean isInArray(int[] arr, int num) {
        return Arrays.asList(arr).contains(num);
    }

    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (isUpstairsOrDownStairs()) {
            return State.LADDER;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (isInsideDruidArea() && pickupsExist() && !Inventory.isFull()) {
            return State.PICKUPS;
        } else if (Combat.isUnderAttack()) {
            return State.IN_COMBAT;
        } else if ((Inventory.isFull() && Banking.isInBank()) || (Inventory.getCount(FOOD_ID) < FOOD_NUM && Banking.isInBank())) {
            //if inventory is full AND in bank || inventory has no food AND in bank
            return State.DEPOSIT_ITEMS;
        } else if ((Inventory.isFull() && !Banking.isInBank() && isInsideDruidArea()) || (!foodExists() && isInsideDruidArea())) {
            //if inside area with full inv or no food
            return State.OPEN_DOOR;
        } else if ((Inventory.isFull() && !Banking.isInBank() && !isInsideDruidArea()) || (!foodExists() && !isInsideDruidArea() && !Banking.isInBank())) {
            //if inventory is full || if we have no food
            return State.WALK_TO_BANK;
        } else if (Inventory.getCount(FOOD_ID) >= FOOD_NUM && Banking.isInBank()) {
            return State.WALK_TO_DRUIDS;
        } else if (!isInsideDruidArea() && isNearDruidDoors() && !isAtDruidDoors()) {
            return State.WALK_TO_DOOR;
        } else if (isAtDruidDoors() && !Inventory.isFull()) {
            return State.PICK_LOCK_DOOR;
        } else if (isInsideDruidArea() && !Inventory.isFull() && !Combat.isUnderAttack()) {
            return State.ATTACK;
        }
        // if we dont satisfy any of the above conditions, we may have a problem
        return State.WALK_TO_DRUIDS;
    }

    public void hopWorld() { //27cb+
        int world = WorldHop.getRandomWorld(true);
        General.println("[WORLDHOP] Attempting world hopping to: " + world);
        //WorldHop.hopTo(world);
        FCInGameHopper.hop(world);
        General.sleep(1000, 1500);
        HOPPING_SOON = false;
        WAS_IN_COMBAT = false;
        players_1 = null;
        players_2 = null;
        timeRan_1 = 0;
        timeRan_2 = 0;
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

    public void sleeper() {
        int x = General.random(0, 100);
        if (x > 50 && x < 57) {
            SLEEPER = true;
        }
    }

    public void checkSleeping() {
        //if (SLEEPER && Player.getRSPlayer().getInteractingCharacter().getHealth() < 15) {
        if (SLEEPER && target.getHealth() < 16) {
            botstatus = "Sleeping..";
            Mouse.leaveGame(true);
            int x = General.random(3000, 8000);
            General.println("Antiban: Sleeping for " + x + "ms");
            General.sleep(x);
            SLEEPER = false;
        }
    }

    public void setFoodNum() {
        if (Player.getRSPlayer().getCombatLevel() <= 30) {
            FOOD_NUM = 11;
        } else if (Player.getRSPlayer().getCombatLevel() >= 31 && Player.getRSPlayer().getCombatLevel() <= 40) {
            FOOD_NUM = 8;
        } else if (Player.getRSPlayer().getCombatLevel() >= 41 && Player.getRSPlayer().getCombatLevel() <= 50) {
            FOOD_NUM = 5;
        } else if (Player.getRSPlayer().getCombatLevel() >= 51 && Player.getRSPlayer().getCombatLevel() <= 60) {
            FOOD_NUM = 3;
        } else {
            FOOD_NUM = 2;
        }
        General.println("Food set to: " + FOOD_NUM);
    }

    public void hover() {
        if (this.abc_util.shouldHover()) {
            if (Banking.isInBank() && (Inventory.isFull() || !foodExists())) {
                RSObject[] booths = Objects.findNearest(5, BOOTH_ID);
                if (booths.length > 0 && booths[0].isClickable() && booths[0].isOnScreen()) {
                    booths[0].click("Bank");
                    debugg("Antiban [HOVER]: Clicking bank booth while moving");
                    General.sleep(1000, 1500);
                    deposit();
                }
            } else if (Player.getPosition().distanceTo(DOORSTile) < 4) {
                picklock();
                debugg("Antiban [HOVER]: Attempting to picklock while moving");
            }
        }
    }

    //////
    //LOOTING
    /////
    private boolean pickupsExist() {
        RSGroundItem[] herbs = GroundItems.findNearest(HERB_IDS);
        return herbs.length > 0;
    }

    private boolean pickups() {
        RSGroundItem[] herbs = GroundItems.findNearest(HERB_IDS);
        if (herbs.length > 0 && !playerInCombat()) {
            int currInv = Inventory.getAll().length;
            if (Clicking.click("Take " + herbs[0].getDefinition().getName(), herbs[0])) {
                totalHerbs++;
                totalMoney += (PriceChecker.getOSbuddyPrice(herbs[0].getID()) * herbs[0].getStack());
                General.println("Picked up: " + herbs[0].getStack() + "x " + herbs[0].getDefinition().getName() + " with ID: " + herbs[0].getID() + " (" + (PriceChecker.getOSbuddyPrice(herbs[0].getID()) * herbs[0].getStack()) + "gp)");
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(250, 500);
                        return Inventory.getAll().length > currInv;
                    }
                }, 2000);
            }
        }
        return false;
    }

    public boolean isUpstairsOrDownStairs() {
        RSObject[] ladders = Objects.findNearest(16, LADDER_IDS);
        return ladders.length > 0;
    }

    public void useLadder() {
        RSObject[] ladders = Objects.find(10, "Ladder");
        if (ladders.length > 0) {
            if (ladders[0].isOnScreen()) {
                if (ladders[0].getID() == LADDER_IDS[0]) {
                    ladders[0].click("Climb-down");
                } else {
                    ladders[0].click("Climb-up");
                }
                General.sleep(1500, 2000);
            } else {
                Walking.blindWalkTo(ladders[0].getPosition());
            }
        }
    }

    public void dropJunk() {
        if (hasJunk()) {
            if (GameTab.TABS.INVENTORY.isOpen()) {
                botstatus = "Dropping...";
                Inventory.dropAllExcept(VALUE_IDS);
                General.sleep(500, 750);
            } else {
                GameTab.open(GameTab.TABS.INVENTORY);
                botstatus = "Dropping...";
                Inventory.dropAllExcept(VALUE_IDS);
                General.sleep(500, 750);
            }
        }
    }

    public boolean hasJunk() {
        return Inventory.getCount(JUNK_IDS) > 0;
    }

    //---------------------------------------------------------------------------------------------------------------
    //////
    //EATING
    /////
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
        final int currHP = Combat.getHP();
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

    //--------------------------------------------------------------------------------------------------------------------
    private boolean attack() {
        if (!HOPPING_SOON) {
            camera();
            RSNPC[] druids = NPCs.findNearest(DRUID_ID);
            if (druids.length > 0 && !Player.isMoving()) {
                if (!druids[0].isInCombat() && druids[0].isValid()) {
                    if (druids[0].click("Attack")) {
                        target = druids[0];
                        WAS_IN_COMBAT = true;
                        sleeper();
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return playerInCombat();
                            }
                        }, 2000);
                    }
                }
            }
        } else {
            if (WAS_IN_COMBAT) {
                General.sleep(12000, 13000);
            } else {
                General.sleep(1000, 1500);
            }
            hopWorld();
        }
        return false;
    }

    private boolean isInsideDruidArea() {
        return DRUID_AREA.contains(Player.getPosition());
    }

    private void opendoor() {
        RSObject[] doors = Objects.findNearest(5, DOOR_ID);
        if (doors.length > 0) {
            doors[0].click("Open");
            General.sleep(500, 750);
        }
    }

    private void picklock() {
        camera();
        RSObject[] doors = Objects.findNearest(5, DOOR_ID);
        if (doors.length > 0) {
            doors[0].click("Pick-lock");
            General.sleep(500, 750);
        }
    }

    private void camera() {
        if (Camera.getCameraAngle() < 88) {
            botstatus = "Rotating camera";
            int angleRNG = General.random(88, 100);
            Camera.setCameraAngle(angleRNG);
        }
        if (Camera.getCameraRotation() < 260 || Camera.getCameraRotation() > 300) {
            botstatus = "Rotating camera";
            int rotationRNG = General.random(260, 330);
            Camera.setCameraRotation(rotationRNG);
        }
    }

    private boolean isAtDruidDoors() {
        return Player.getPosition().distanceTo(DOORSTile) == 0;
    }

    private boolean isNearDruidDoors() {
        return Player.getPosition().distanceTo(DRUIRSTile) < 3;
    }

    private void deposit() {
        if (Banking.isBankScreenOpen()) {
            Banking.depositAll();
            General.sleep(750, 1000);
            Banking.withdraw(FOOD_NUM, FOOD_ID);
            General.sleep(750, 1000);
        } else {
            Banking.openBank();
        }
    }

    enum State {
        WALK_TO_BANK,
        WALK_TO_DRUIDS,
        DEPOSIT_ITEMS,
        SOMETHING_WENT_WRONG,
        WALKING,
        ANTI_BAN,
        LOGGED_IN,
        PICK_LOCK_DOOR,
        IN_COMBAT,
        ATTACK,
        WALK_TO_DOOR,
        OPEN_DOOR,
        EAT_NOW,
        PICKUPS,
        LADDER
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

    private final Image img = getImage("");
    private static final long START_TIME = System.currentTimeMillis();
    Font font = new Font("Verdana", Font.BOLD, 14);

    @Override
    public void onPaint(Graphics g) {

        Graphics2D gg = (Graphics2D) g;
        gg.drawImage(img, 0, 304, null);

        long timeRan = System.currentTimeMillis() - START_TIME;

        int moneyH = (int) ((totalMoney * 3600000D) / timeRan);
        int herbsH = (int) ((totalHerbs * 3600000D) / timeRan);

        g.setFont(font);
        //g.setColor(new Color(0, 0, 204));
        g.setColor(new Color(255, 255, 255));
        g.drawString("Status: " + botstatus, 230, 300);
        g.drawString("Runtime: " + Timing.msToString(timeRan), 230, 320);
        g.drawString("Herbs/h: " + herbsH + " (" + moneyH + "gp/h) ", 230, 340);

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

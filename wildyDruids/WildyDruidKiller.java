package scripts.wildyDruids;

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

@ScriptManifest(authors = "Marko", category = "Combat", name = "Wildy Druid Killer", description = "Edgeville druid killer", version = 1.2)
public class WildyDruidKiller extends Script implements Painting, Ending {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;

    public final int FOOD_ID = 333;
    public int FOOD_NUM;
    //kwuarm, dwarf weed, ranarr, harra, lantadyme, toadflax, irit, avantoe, cadantine, torstol, snapdrag, law, chaos, mith bolts
    public final int[] HERB_IDS = {213, 217, 207, 205, 2485, 3049, 209, 211, 215, 219, 3051, 563, 562, 9142, 11941, 561};
    public final int[] VALUE_IDS = {213, 217, 207, 205, 2485, 3049, 209, 211, 215, 219, 3051, 563, 562, 9142, 333};
    public final int[] JUNK_IDS = {526, 995, 556, 231, 227, 13471};
    
    private final RSTile TRAPDOOR_TILE = new RSTile(3095, 3470);
    public final int TRAPDOOR_ID = 7179;
    public final int TRAPDOOR_DOWN_ID = 7181;
    public final int LADDER_ID = 17385;
    private final RSTile LADDER_TILE = new RSTile(3096, 9867);
    private final RSTile FENCE_1_TILE = new RSTile(3100, 9908);
    private final RSTile FENCE_1_TILE_NEXT = new RSTile(3103, 9909);
    public final int[] FENCE_1_CLOSED = {7168,7169}; //Open
    public final int[] FENCE_1_OPEN = {7171,7172}; //Close
    private final RSTile FENCE_2_TILE = new RSTile(3131, 9915);
    private final RSTile FENCE_2_TILE_NEXT = new RSTile(3131, 9917);
    public final int[] FENCE_2_CLOSED = {7407,7408};
    private final RSTile FENCE_2_TILE_INSIDE = new RSTile(3131, 9918);
    private final RSTile DRUID_AREA_TILE = new RSTile(3113, 9929);
    
    
    public final int DRUID_ID = 2878;
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

    private final static Area DRUID_AREA = new Area(new RSTile[]{
        new RSTile(3103, 9944, 0), new RSTile(3122, 9944, 0),
        new RSTile(3122, 9922, 0), new RSTile(3103, 9922, 0)});

    private final static int[] PVP_WORLDS = new int[]{25, 37, 45, 52, 57};

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
                    //hovering();
                    break;
                case WALK_TO_BANK:
                    botstatus = "Walking to bank";
                    WebWalking.walkToBank();
                    break;
                case ATTACK:
                    botstatus = "Attacking druids";
                    heal();
                    attack();
                    break;
                case WALK_TO_TRAP_DOOR:
                    botstatus = "Walking to trapdoor";
                    Walking.blindWalkTo(TRAPDOOR_TILE);
                    break;
                case CLIMB_DOWN_TRAPDOOR:
                    botstatus = "Trapdoor is open, climbing down";
                    climbDownTrapdoor();
                    break;
                case OPEN_TRAPDOOR:
                    botstatus = "Trapdoor is closed, opening";
                    openTrapdoor();
                    break;
                case WALK_TO_FENCE_1:
                    botstatus = "Walking to first fence";
                    Walking.blindWalkTo(FENCE_1_TILE);
                    break;
                case WALK_TO_FENCE_1_DOOR:
                    botstatus = "Walking next to first fence";
                    Walking.walkTo(FENCE_1_TILE_NEXT);
                    break;
                case OPEN_FENCE_1:
                    botstatus = "Opening first fence";
                    openFence(true);
                    break;
                case WALK_TO_FENCE_2:
                    botstatus = "Walking to second fence";
                    Walking.blindWalkTo(FENCE_2_TILE);
                    break;
                case WALK_TO_FENCE_2_DOOR:
                    botstatus = "Walking next to second fence";
                    Walking.walkTo(FENCE_2_TILE_NEXT);
                    break;
                case OPEN_FENCE_2:
                    botstatus = "Opening second fence";
                    openFence(false);
                    break;
                case WALK_TO_DRUID_AREA:
                    botstatus = "Walking to druid area";
                    Walking.blindWalkTo(DRUID_AREA_TILE);
                    break;
                case IN_COMBAT:
                    botstatus = "In combat";
                    heal();
                    checkSleeping();
                    antibans();
                    break;
                case PICKUP:
                    botstatus = "Looting...";
                    pickups();
                    break;
                case DROP_JUNK:
                    botstatus = "Dropping junk...";
                    dropJunk();
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
        WALK_TO_TRAP_DOOR,
        CLIMB_DOWN_TRAPDOOR,
        OPEN_TRAPDOOR, 
        WALK_TO_FENCE_1,
        WALK_TO_FENCE_1_DOOR,
        OPEN_FENCE_1,
        WALK_TO_FENCE_2,
        WALK_TO_FENCE_2_DOOR,
        OPEN_FENCE_2,
        WALK_TO_DRUID_AREA,
        ATTACK,
        IN_COMBAT,
        PICKUP,
        DROP_JUNK
    }
    
    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (Combat.isUnderAttack()) {
            return State.IN_COMBAT; 
        } else if (isInsideDruidArea() && !Inventory.isFull()) {
            if(pickupsExist()){
                return State.PICKUP;
            } else if(hasJunk()) {
                return State.DROP_JUNK;
            }
            return State.ATTACK;
        } else if (isInsideDruidArea() && Inventory.isFull()) {
            return State.WALK_TO_BANK;
        } else if (!isInsideDruidArea() && Banking.isInBank() && !Inventory.isFull()) {
            return State.WALK_TO_TRAP_DOOR;
        } else if (isTrapDoorNear()) { //trapdoor is near => click door
            return State.CLIMB_DOWN_TRAPDOOR;
        } else if (isTrapDoorNearClosed()) { //trapdoor is near but not open => open door
            return State.OPEN_TRAPDOOR;
        } else if (Player.getPosition().distanceTo(LADDER_TILE) == 0) { //under trap, near ladder => walk to fence
            return State.WALK_TO_FENCE_1;
        } else if (Player.getPosition().distanceTo(FENCE_1_TILE_NEXT) == 0 && !isFenceOpen()) { //fence is not open => open it
            return State.OPEN_FENCE_1;
        } else if (Player.getPosition().distanceTo(FENCE_1_TILE) < 10 && Player.getPosition().distanceTo(FENCE_1_TILE) > 0) {
            if(!isFenceOpen()) { //if fence is closed open it
                return State.WALK_TO_FENCE_1_DOOR;
            } else { //walk to second fence
                return State.WALK_TO_FENCE_2;
            }
        } else if (Player.getPosition().distanceTo(FENCE_2_TILE_INSIDE) == 0) { //2nd fence is near => open 2nd fence
            return State.WALK_TO_DRUID_AREA;
        } else if (Player.getPosition().distanceTo(FENCE_2_TILE_NEXT) == 0) { //2nd fence is near => open 2nd fence
            return State.OPEN_FENCE_2;
        } else if (Player.getPosition().distanceTo(FENCE_2_TILE) < 10 && Player.getPosition().distanceTo(FENCE_2_TILE) > 0) { //2nd fence is near => walk to it
            return State.WALK_TO_FENCE_2_DOOR;
        } 
        // if we dont satisfy any of the above conditions, we may have a problem
        return State.WALK_TO_BANK;
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
    
    private boolean attack() {
        if (!HOPPING_SOON) {
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
            //hopWorld();
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
        if (SLEEPER && target.getHealth() < 16) {
            botstatus = "Sleeping..";
            Mouse.leaveGame(true);
            int x = General.random(3000, 8000);
            General.println("Antiban: Sleeping for " + x + "ms");
            General.sleep(x);
            SLEEPER = false;
        }
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
    
    private boolean pickupsExist() {
        RSGroundItem[] herbs = GroundItems.findNearest(HERB_IDS);
        return herbs.length > 0;
    }

    private boolean pickups() {
        RSGroundItem[] herbs = GroundItems.findNearest(HERB_IDS);
        if (herbs.length > 0 && !playerInCombat()) {
            if(herbs[0].isOnScreen() && herbs[0].isClickable()) {
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
        }
        return false;
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
    
    private boolean isFenceOpen() {
        RSObject[] trapdoors = Objects.findNearest(10, FENCE_1_OPEN);
        return trapdoors.length > 0;
    }
    
    private void openFence(final boolean fence) {
        RSObject[] trapdoors;
        if(fence){
            trapdoors = Objects.findNearest(10, FENCE_1_CLOSED);
        } else {
            trapdoors = Objects.findNearest(10, FENCE_2_CLOSED);
        }
        if (trapdoors.length > 0) {
            trapdoors[0].click("Open");
            General.sleep(500, 750);
        }
    }
    
    private boolean isTrapDoorNearClosed() {
        RSObject[] trapdoors = Objects.findNearest(10, TRAPDOOR_ID);
        return trapdoors.length > 0;
    }
    
    private void openTrapdoor(){
        RSObject[] trapdoors = Objects.findNearest(10, TRAPDOOR_ID);
        if (trapdoors.length > 0) {
            trapdoors[0].click("Open");
            General.sleep(500, 750);
        }
    }
    
    private boolean isTrapDoorNear() {
        RSObject[] trapdoors = Objects.findNearest(10, TRAPDOOR_DOWN_ID);
        return trapdoors.length > 0;
    }
    
    private void climbDownTrapdoor(){
        RSObject[] trapdoors = Objects.findNearest(10, TRAPDOOR_DOWN_ID);
        if (trapdoors.length > 0) {
            trapdoors[0].click("Climb-down");
            General.sleep(500, 750);
        }
    }
    
    private boolean isInsideDruidArea() {
        return DRUID_AREA.contains(Player.getPosition());
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
        g.drawString("Herbs/h: " + herbsH + " (" + moneyH + "gp/h) ", 230, 340);
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

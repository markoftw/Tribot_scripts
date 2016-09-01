package scripts.newAccountWalker;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = "Marko", category = "New accounts", name = "Account walker", description = "New account walker", version = 1.3)
public class NewAccountWalker extends Script implements Painting {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    public boolean startup_vars = false;
    public boolean do_hunter = false;
    public boolean do_mining = false;
    public int trade_world_p2p = 0;
    public int trade_world_f2p = 0;
    public boolean TRADE_FIRST_FREE = false; //if traded in f2p first
    public boolean BOND_USED = false; //
    public String trade_with_p2p = "";
    public String trade_with_f2p = "";
    public final boolean running = true;
    public boolean stop_script = false;
    public final boolean debugging = true;
    public String destination;
    ABCUtil abc_util = null;

    public final int BOND_ID = 13192;
    public final int FALC_TRAPS_ID = 10006;
    public final int FALC_TRAPS_ID_NOTED = 10007;
    public final int CHIN_TRAPS_ID = 10008;
    public final int CHIN_TRAPS_ID_NOTED = 10009;
    public final int SPADE_ID = 952;
    public final int SPADE_ID_NOTED = 952;
    public final int DUEL_RING_ID = 2552;
    public final int DUEL_RING_ID_NOTED = 2553;
    public final int VEOS_ID = 2147;
    public final int PLANK_ID = 27778;
    public final RSTile OUTSIDERSTile = new RSTile(1824, 3691);

    private final static Area LUMBRIDGE_AREA = new Area(new RSTile[]{
        new RSTile(3219, 3209), new RSTile(3235, 3209),
        new RSTile(3235, 3235), new RSTile(3219, 3235)});

    private final static Area CASTLEWARS_AREA = new Area(new RSTile[]{
        new RSTile(2437, 3081), new RSTile(2447, 3081),
        new RSTile(2447, 3099), new RSTile(2437, 3099)});

    private final static Area HUNTPATH_AREA = new Area(new RSTile[]{
        new RSTile(2445, 3035), new RSTile(2462, 3035),
        new RSTile(2462, 3022), new RSTile(2445, 3022)});

    private final static Area VEOS_AREA = new Area(new RSTile[]{
        new RSTile(3051, 3195), new RSTile(3066, 3195),
        new RSTile(3066, 3191), new RSTile(3051, 3191)});
                
    private final static Area OUTSIDESHIP_AREA = new Area(new RSTile[]{
        new RSTile(1822, 3692), new RSTile(1827, 3692),
        new RSTile(1827, 3689), new RSTile(1822, 3689)});
    
    private final static Area ZEAHBANK_AREA = new Area(new RSTile[]{
        new RSTile(1667, 3566), new RSTile(1699, 3566),
        new RSTile(1699, 3543), new RSTile(1667, 3543)});
    
    private final static Area HUNTFINAL_AREA = new Area(new RSTile[]{
        new RSTile(2496, 2944), new RSTile(2507, 2944),
        new RSTile(2507, 2934), new RSTile(2496, 2934)});

    @Override
    public void run() {
        General.useAntiBanCompliance(true);
        this.abc_util = new ABCUtil();
        GUI GUI = new GUI();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenW = (screensize.width) / 2;
        int screenH = (screensize.height) / 2;
        Dimension dim = GUI.getSize();
        GUI.setVisible(true);
        GUI.setLocation((screenW / 2), (screenH / 2));

        while (!GUI_COMPLETE) {
            sleep(300);
        }

        GUI.setVisible(false);

        while (!stop_script) {
            if (!startup_vars) { //set gui vars
                if (GUI.hunterButton.isSelected()) {
                    do_hunter = true;
                    destination = "Hunter";
                    General.println("Hunter selected");
                } else if (GUI.minerButton.isSelected()) {
                    do_mining = true;
                    destination = "Mining";
                    General.println("Miner selected");
                } else {
                    General.println("ERROR: None selected");
                }

                if (GUI.tradeWith != null && !GUI.tradeWith.getText().equals("")) {
                    trade_with_f2p = GUI.tradeWith.getText();
                    General.println("Trading with (F2P): " + GUI.tradeWith.getText());
                } else {
                    General.println("ERROR: Trading with (F2P) error!");
                }

                if (GUI.tradeWith1 != null && !GUI.tradeWith1.getText().equals("")) {
                    trade_with_p2p = GUI.tradeWith1.getText();
                    General.println("Trading with (P2P): " + GUI.tradeWith1.getText());
                } else {
                    General.println("ERROR: Trading with (P2P) error!");
                }

                if (GUI.tradeWorld != null && !GUI.tradeWorld.getText().equals("")) {
                    int num = Integer.parseInt(GUI.tradeWorld.getText());
                    trade_world_f2p = num;
                    General.println("Trading in world (F2P): " + num);
                } else {
                    General.println("ERROR: Trading world (F2P) error!");
                }

                if (GUI.tradeWorld1 != null && !GUI.tradeWorld1.getText().equals("")) {
                    int num = Integer.parseInt(GUI.tradeWorld1.getText());
                    trade_world_p2p = num;
                    General.println("Trading in world (P2P): " + num);
                } else {
                    General.println("ERROR: Trading world (P2P) error!");
                }
                startup_vars = true;
            }

            startLoop();
            
            sleep(General.random(100, 200));
        }
    }

    private void startLoop() {
        if (Trading.getWindowState() != null && Trading.getWindowState().equals(Trading.WINDOW_STATE.FIRST_WINDOW)) {
            do {
                debugg("Attempting to accept trade (1ST WINDOW)");
                General.sleep(3000, 4000);
                Trading.accept();
            } while (Trading.getWindowState() != null && Trading.getWindowState().equals(Trading.WINDOW_STATE.SECOND_WINDOW));
        } else if (Trading.getWindowState() != null && Trading.getWindowState().equals(Trading.WINDOW_STATE.SECOND_WINDOW)) {
            do {
                debugg("Attempting to accept trade (2ND WINDOW)");
                General.sleep(3000, 4000);
                Trading.accept();
            } while (Trading.getWindowState() != null && Trading.getWindowState().equals(Trading.WINDOW_STATE.SECOND_WINDOW));
        } else if (Game.getCurrentWorld() != trade_world_f2p && !BOND_USED) {
            //hop to f2p trade world for bond
            debugg("Hopping to F2P trade world (" + trade_with_f2p + "), currently in " + Game.getCurrentWorld());
            if (!WorldHopper.isDeadman(trade_world_f2p) && !WorldHopper.isMembers(trade_world_f2p)) {
                botstatus = "Hopping to F2P trade world";
                WorldHopper.changeWorld(trade_world_f2p);
                sleep(General.random(1000, 2000));
            }
        } else if (Inventory.getAll().length == 18 && isInLumbridge() && Game.getCurrentWorld() == trade_world_f2p && !BOND_USED) {
            //fresh from tut island -> start trading
            //debugg("Trading in F2P world");
            trade(trade_with_f2p);
        } else if (Inventory.getAll().length == 19 && isInLumbridge() && Game.getCurrentWorld() == trade_world_f2p && !BOND_USED) {
            //use bond .. -> then hop to memb trade world
            debugg("Using bond");
            if (hasBond()) {
                botstatus = "Using bond";
                RSItem[] BONDItems = Inventory.find(BOND_ID);
                if (BONDItems.length > 0) {
                    if (BONDItems[0].click("Redeem")) {
                        General.sleep(1000, 1500);
                        Mouse.click(General.random(55, 159), General.random(90, 200), 1);
                        General.sleep(1000, 1500);
                        Mouse.click(General.random(340, 440), General.random(275, 300), 1);
                        BOND_USED = true;
                    }
                }
            }
        } else if (Inventory.getAll().length == 18 && isInLumbridge() && Game.getCurrentWorld() == trade_world_f2p && BOND_USED) {
            //hop to p2p trade world for items
            debugg("Hopping to P2P trade world (" + trade_with_p2p + "), currently in " + Game.getCurrentWorld());
            
                botstatus = "Hopping to P2P trade world";
                WorldHopper.changeWorld(trade_world_p2p);
                sleep(General.random(1000, 2000));
        } else if (Inventory.getAll().length == 18 && isInLumbridge() && Game.getCurrentWorld() == trade_world_p2p && BOND_USED && !hasDuelRing() && !hasFalcTraps() && !hasSpade()) {
            //hop to p2p
            //debugg("Trading in P2P world");
            trade(trade_with_p2p);
        } else if (do_mining && hasSpade() && BOND_USED && Game.getCurrentWorld() == trade_world_p2p && !isVeosNear() && !isInVeosArea() && !isOnShip() && !isOutsideShipArea() && !isNearZeahBank()) {
            //run to zeah bank & unnote -> logout
            debugg("Walking to Veos");
            botstatus = "Zeah journey";
            General.sleep(600, 800);
            // 3028 3204
            WebWalking.walkTo(new RSTile(3056, 3193));
        } else if (do_mining && hasSpade() && BOND_USED && Game.getCurrentWorld() == trade_world_p2p && isVeosNear() && isInVeosArea() && !isOutsideShipArea() && !isNearZeahBank()) {
            //run to zeah bank & unnote -> logout
            debugg("Walking to Zeah");
            botstatus = ("Walking to Zeah");
            General.sleep(600, 800);
            RSNPC[] veos = NPCs.findNearest(VEOS_ID);
            if (veos.length > 0) {
                veos[0].click("Travel");
                General.sleep(3500, 4000);
            }
        } else if (do_mining && hasSpade() && BOND_USED && Game.getCurrentWorld() == trade_world_p2p && isOnShip() && !isVeosNear() && !isInVeosArea() && !isOutsideShipArea() && !isNearZeahBank()) {
            //run to zeah bank & unnote -> logout
            debugg("Clicking crossplank");
            botstatus = ("Clicking crossplank");
            General.sleep(600, 800);
            RSObject[] plankObjects = Objects.findNearest(5, PLANK_ID);
            if (plankObjects.length > 0) {
                plankObjects[0].click("Cross");
                General.sleep(2000, 2500);
            }
        } else if (do_mining && hasSpade() && BOND_USED && Game.getCurrentWorld() == trade_world_p2p && !isOnShip() && isVeosNear() && !isInVeosArea() && isOutsideShipArea() && !isNearZeahBank()) {
            //run to zeah bank & unnote -> logout
            debugg("On zeah, walking to bank");
            botstatus = ("On zeah, walking to bank");
            //WebWalking.walkToBank();
            RSTile[] ZeahRSTiles = {new RSTile(1814, 3690), new RSTile(1801, 3690), new RSTile(1792, 3690), new RSTile(1788, 3677), new RSTile(1787, 3665), new RSTile(1788, 3651), new RSTile(1789, 3637), new RSTile(1793, 3623), new RSTile(1793, 3610), new RSTile(1792, 3597),
                                    new RSTile(1790, 3583), new RSTile(1784, 3573), new RSTile(1779, 3562), new RSTile(1767, 3560), new RSTile(1754, 3559), new RSTile(1742, 3559), new RSTile(1730, 3560), new RSTile(1719, 3555), new RSTile(1707, 3554), new RSTile(1694, 3554), new RSTile(1683, 3553), new RSTile(1676, 3560)};
            Walking.walkPath(ZeahRSTiles);
        }else if (do_hunter && hasDuelRing() && hasFalcTraps() && BOND_USED && Game.getCurrentWorld() == trade_world_p2p && !Banking.isInBank()) {
            //tp cw -> bank all -> unnote -> run to spot -> logout
            debugg("Going to hunter spot, banking first");
            botstatus = "Going to hunter spot, banking first";
            WebWalking.walkToBank();
        } else if (do_hunter && BOND_USED && Game.getCurrentWorld() == trade_world_p2p && Banking.isInBank()) {
            if (Banking.isBankScreenOpen()) {
                if (Inventory.getAll().length == 21) {
                    Banking.depositAll();
                    General.sleep(500, 800);
                } else if (Inventory.getAll().length == 0) {
                    Banking.withdraw(1, DUEL_RING_ID);
                    General.sleep(200, 500);
                    Banking.withdraw(22, FALC_TRAPS_ID);
                    General.sleep(600, 900);
                    Banking.close();
                    General.sleep(700, 900);
                    RSItem[] duelRSItems = Inventory.find(DUEL_RING_ID);
                    if (duelRSItems.length > 0) {
                        duelRSItems[0].click("Rub");
                        General.sleep(1000, 1200);
                        Mouse.click(General.random(209, 314), General.random(399, 409), 1);
                        General.sleep(1800, 2100);
                    }
                }
            } else {
                Banking.openBank();
            }
        } else if (isInCastlewars() && do_hunter) {
            debugg("In castlewars");
            botstatus = "Walking from Castle wars";
            WebWalking.walkTo(new RSTile(2450, 3028));
            General.sleep(1000, 1500);
        } else if (isInHuntPath() && do_hunter) {
            debugg("At hunt path");
            botstatus = "Walking from ogre area";
            General.sleep(800, 1000);
            RSTile[] huntRSTiles = {new RSTile(2456, 3026), new RSTile(2466, 3022), new RSTile(2476, 3015), new RSTile(2485, 3006), new RSTile(2488, 2995), new RSTile(2490, 2984), new RSTile(2490, 2973), new RSTile(2485, 2966), new RSTile(2488, 2956), new RSTile(2493, 2948), new RSTile(2500, 2940)};
            Walking.walkPath(huntRSTiles);
        } else if (do_mining && isNearZeahBank()) {
            botstatus = "Arrived! Logging out...";
            debugg("Arrived! Logging out...");
            Login.logout();
            General.sleep(1000,1500);
            stop_script = true;
        } else if (do_hunter && isAtFinalHunt()) {
            botstatus = "Arrived! Logging out...";
            debugg("Arrived! Logging out...");
            Login.logout();
            General.sleep(1000,1500);
            stop_script = true;
        } 
    }

    private void trade(String playerString) {
        if(Camera.getCameraAngle() < 85) {
            Camera.setCameraAngle(General.random(85, 100));
        }
        RSPlayer[] player = Players.findNearest(playerString);
        if (player.length > 0) {
            if (player[0].isClickable() && player[0].isOnScreen() && !Player.isMoving()) {
                botstatus = "Clicking trade with";
                if (player[0].click("Trade with " + playerString)) {
                    botstatus = "Trading " + playerString;
                    sleep(General.random(5000, 10000));
                }
            } else {
                botstatus = "Walking to " + playerString;
                int x = player[0].getPosition().getX();
                int y = player[0].getPosition().getY();
                Walking.walkTo(new RSTile(x + General.random(0, 2), y - General.random(0, 2)));
                sleep(General.random(1500, 1800));
            }
        } else {
            botstatus = playerString + " not found... Waiting..";
        }
    }

    private boolean isVeosNear() {
        RSNPC[] veos = NPCs.findNearest(VEOS_ID);
        if (veos.length > 0) {
            if (Player.getPosition().distanceTo(veos[0].getPosition()) < 8) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isOnShip() {
        RSObject[] plankObjects = Objects.findNearest(5, PLANK_ID);
        return plankObjects.length > 0;
    }

    private boolean hasBond() {
        return Inventory.getCount(BOND_ID) > 0;
    }

    private boolean hasSpade() {
        return Inventory.getCount(SPADE_ID) > 0 || Inventory.getCount(SPADE_ID_NOTED) > 0;
    }

    private boolean hasDuelRing() {
        return Inventory.getCount(DUEL_RING_ID) > 0 || Inventory.getCount(DUEL_RING_ID_NOTED) > 0;
    }

    private boolean hasChinTraps() {
        return Inventory.getCount(CHIN_TRAPS_ID) > 0 || Inventory.getCount(CHIN_TRAPS_ID_NOTED) > 0;
    }

    private boolean hasFalcTraps() {
        return Inventory.getCount(FALC_TRAPS_ID) > 0 || Inventory.getCount(FALC_TRAPS_ID_NOTED) > 0;
    }

    private boolean isInLumbridge() {
        return LUMBRIDGE_AREA.contains(Player.getPosition());
    }

    private boolean isInCastlewars() {
        return CASTLEWARS_AREA.contains(Player.getPosition());
    }

    private boolean isInHuntPath() {
        return HUNTPATH_AREA.contains(Player.getPosition());
    }

    private boolean isInVeosArea() {
        return VEOS_AREA.contains(Player.getPosition());
    }

    private boolean isOutsideShipArea() {
        return OUTSIDESHIP_AREA.contains(Player.getPosition());
    }
    
    private boolean isNearZeahBank() {
        return ZEAHBANK_AREA.contains(Player.getPosition());
    }
    
    private boolean isAtFinalHunt() {
        return HUNTFINAL_AREA.contains(Player.getPosition());
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

    private void debugg(String msg) {
        if (debugging) {
            General.println(msg);
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
    Font font = new Font("Verdana", Font.BOLD, 14);

    @Override
    public void onPaint(Graphics g) {
        Graphics2D gg = (Graphics2D) g;
        gg.drawImage(img, 0, 304, null);
        long timeRan = System.currentTimeMillis() - START_TIME;
        g.setFont(font);
        g.setColor(new Color(200, 0, 200));
        g.drawString("Runtime: " + Timing.msToString(timeRan), 225, 370);
        g.drawString("Status: " + botstatus, 225, 390);
        g.drawString("Destination: " + destination, 225, 410);
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

            javax.swing.ButtonGroup buttonGroup2 = new javax.swing.ButtonGroup();
            jLabel1 = new javax.swing.JLabel();
            jLabel2 = new javax.swing.JLabel();
            jButton2 = new javax.swing.JButton();
            hunterButton = new javax.swing.JRadioButton();
            minerButton = new javax.swing.JRadioButton();
            tradeWith = new javax.swing.JTextField();
            tradeWorld = new javax.swing.JTextField();
            jLabel3 = new javax.swing.JLabel();
            tradeWorld1 = new javax.swing.JTextField();
            jLabel4 = new javax.swing.JLabel();
            tradeWith1 = new javax.swing.JTextField();

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            jLabel1.setText("Trade with (F2P):");
            tradeWith.setText("illukc22");
            tradeWorld.setText("394");
            tradeWith1.setText("illukc231");
            tradeWorld1.setText("386");

            jLabel2.setText("Trade in world (F2P):");

            jButton2.setText("Start");
            jButton2.addActionListener(this::startActionPerformed);

            buttonGroup2.add(hunterButton);
            hunterButton.setText("Hunter");

            buttonGroup2.add(minerButton);
            minerButton.setText("Miner");

            jLabel3.setText("Trade with (P2P):");

            jLabel4.setText("Trade in world (P2P):");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(109, 109, 109)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(minerButton)
                                                    .addComponent(hunterButton)))
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(61, 61, 61)
                                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(0, 0, Short.MAX_VALUE)
                                            .addComponent(tradeWith1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(27, 27, 27)
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(tradeWith, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addGroup(layout.createSequentialGroup()
                                                                            .addGap(17, 17, 17)
                                                                            .addComponent(jLabel3))
                                                                    .addGroup(layout.createSequentialGroup()
                                                                            .addComponent(jLabel4)
                                                                            .addGap(0, 0, Short.MAX_VALUE)))
                                                            .addGap(29, 29, 29)
                                                            .addComponent(tradeWorld1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel2)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(tradeWorld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGap(110, 110, 110)))
                            .addGap(34, 34, 34))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addGap(50, 50, 50)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(tradeWith, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(tradeWorld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(tradeWith1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(tradeWorld1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(1, 1, 1)
                            .addComponent(hunterButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(minerButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(32, Short.MAX_VALUE))
            );

            jLabel1.getAccessibleContext().setAccessibleName("Trade with (F2P):");

            pack();
        }// </editor-fold>

        private void startActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            GUI_COMPLETE = true;
        }
        // Variables declaration - do not modify
        public javax.swing.JRadioButton hunterButton;
        private javax.swing.JButton jButton2;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        public javax.swing.JRadioButton minerButton;
        private javax.swing.JTextField tradeWith;
        private javax.swing.JTextField tradeWith1;
        private javax.swing.JTextField tradeWorld;
        private javax.swing.JTextField tradeWorld1;
        // End of variables declaration
    }
    // END GUI

}

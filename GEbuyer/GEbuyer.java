package scripts.GEbuyer;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.tribot.api.*;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = "Marko", category = "RedDrags", name = "0. GE BUYER", description = "Buying starting items @ GE [START @ GE WITH COINS]", version = 1.1)
public class GEbuyer extends Script implements Painting, Ending {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;

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
                case WALK_TO_GE:
                    botstatus = "Walking to GE";
                    WebWalking.walkToBank();
                    break;
                case BANKING:
                    botstatus = "Banking";
                    camera();
                    break;
                case FINISHED:
                    General.println("We're done");
                    Login.logout();
                    stop_script = true;
                    break;
                case BUYING:
                    botstatus = "Buying items";
                    camera();
                    buyItems();
                    break;
                case GET_MONEY:
                    botstatus = "Taking money from bank";
                    break;
            }
            // control cpu usage
            General.sleep(75, 150);
        }
    }

    enum State {
        LOGGED_IN,
        WALKING,
        WALK_TO_GE,
        BANKING,
        FINISHED,
        ERROR,
        BUYING,
        GET_MONEY
    }

    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Inventory.find("Amulet of glory(6)").length > 0) {
            return State.FINISHED;
        } else if (Inventory.find("Coins").length > 0 && Inventory.getCount("Coins") >= 50000) {
            return State.BUYING;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (Inventory.find("Coins").length > 0 && Inventory.getCount("Coins") < 50000) {
            return State.GET_MONEY;
        }
        return State.ERROR;
    }

    public boolean init = false;
    public ArrayList<String> itemList = new ArrayList();
    public ArrayList<Integer> itemIDList = new ArrayList<>();
    public ArrayList<Integer> itemAmountList = new ArrayList<>();
    public ArrayList<Integer> itemExtraList = new ArrayList<>();
    public ArrayList<Itemlist> allItems = new ArrayList<>();
    public Itemlist[] shuffled;

    public Map<String, Integer[]> itemMap = new HashMap<>();

    private void buyItems() {
        if (!init) {
            initList();
        }
        final RSObject[] GESTUFF = Objects.findNearest(6, 10061, 10060);
        final RSNPC[] BANKERS = NPCs.findNearest(6, 5453, 5454, 5455, 5456);
        RSGEOffer[] offers = GrandExchange.getOffers();
        RSInterface COLLECTInterface = Interfaces.get(402, 4);
        RSInterface COLLECTCLOSEInterface = Interfaces.get(402, 2);
        if (GESTUFF.length > 0 && offers.length > 0) {
            if (allItems.isEmpty()) {
                debugg("List is empty...");
                botstatus = "List is empty!";
                stop_script = true;
                Login.logout();
            } else if (GrandExchange.getWindowState() == null && EmptySpots() > 0) {
                botstatus = "Opening GE windows";
                if (GESTUFF[0].click("Exchange")) {
                    General.sleep(1500, 2500);
                }
            } else if (GrandExchange.getWindowState() != null && EmptySpots() > 0) {
                botstatus = "Buying ze items";
                for (int i = 0; i < offers.length; i++) {
                    //if itemlist is empty
                    Itemlist item = allItems.get(0);
                    int price = PriceChecker.getOSbuddyPrice(item.ID) + item.price;
                    if (offers[i].getStatus() == RSGEOffer.STATUS.EMPTY) {
                        if (GrandExchange.offer(item.name, price, item.amount, false)) {
                            debugg("Bought: " + item.name + ", ID: " + item.ID + ", Amount: " + item.amount + ", Price: " + price);
                            allItems.remove(item);
                            General.sleep(250, 500);
                        }
                    }
                }
            } else if (EmptySpots() == 0 && (GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW || GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.OFFER_WINDOW || GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW)) {
                botstatus = "Closing GE interface";
                debugg("Closing window");
                if (GrandExchange.close()) {
                    debugg("Closed window!");
                    General.sleep(500, 750);
                }
            } else if (EmptySpots() == 0 && BANKERS.length > 0 && COLLECTInterface == null && COLLECTCLOSEInterface == null) {
                botstatus = "Opening collect interface";
                debugg("Opening collect interface");
                if (BANKERS[0].click("Collect")) {
                    General.sleep(1500, 1750);
                }
            } else if (COLLECTInterface != null && COLLECTCLOSEInterface != null) {
                botstatus = "Collecting items";
                debugg("Collecting");
                if (COLLECTInterface.click("Collect to bank")) {
                    debugg("Collected to bank!");
                    General.sleep(1500, 1750);
                    RSInterface[] close = COLLECTCLOSEInterface.getChildren();
                    if (close != null) {
                        if (close[11].click("Close")) {
                            debugg("Closed interface");
                            General.sleep(1500, 1750);
                        }
                    }
                }
            }
        }
    }

    public void getCoins() {
        RSInterface COLLECTCLOSEInterface = Interfaces.get(402, 2);
        if (GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW || GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.OFFER_WINDOW || GrandExchange.getWindowState() == GrandExchange.WINDOW_STATE.SELECTION_WINDOW) {
            if (GrandExchange.close()) {
                debugg("Closed window!");
                General.sleep(500, 750);
            }
        } else if (COLLECTCLOSEInterface != null) {
            RSInterface[] close = COLLECTCLOSEInterface.getChildren();
            if (close != null) {
                if (close[11].click("Close")) {
                    debugg("Closed interface");
                    General.sleep(1500, 1750);
                }
            }
        } else {
            if(Banking.isBankScreenOpen()) {
                Banking.withdraw(-1, "Coins");
                General.sleep(750,900);
                Banking.close();
                General.sleep(750,900);
            } else {
                Banking.openBank();
            }
        }
    }

    public void shuffle() {

        Itemlist[] arr = new Itemlist[allItems.size()];
        arr = allItems.toArray(arr);
        int num = arr.length;

        for (int i = 0; i < num; i++) {
            int s = i + (int) (Math.random() * (num - i));
            Itemlist temp = arr[s];
            arr[s] = arr[i];
            arr[i] = temp;
        }

        allItems = new ArrayList<>(Arrays.asList(arr));
    }

    public void initList() { //700k
        allItems.add(new Itemlist("Iron scimitar", 1323, 1, 500));
        allItems.add(new Itemlist("Mithril scimitar", 1329, 1, 1000));
        allItems.add(new Itemlist("Adamant scimitar", 1331, 1, 3000));
        allItems.add(new Itemlist("Iron arrow", 884, 15000, 5));
        //allItems.add(new Itemlist("Shortbow", 841, 1, 1500)); ze ma
        allItems.add(new Itemlist("Maple shortbow", 853, 1, 500));
        allItems.add(new Itemlist("Willow shortbow", 849, 1, 300));
        allItems.add(new Itemlist("Yew shortbow", 857, 1, 400));
        allItems.add(new Itemlist("Magic shortbow", 861, 1, 300));
        //addToList("Coif", 1169, 1, 300);
        //allItems.add(new Itemlist("Trout", 333, 1000, 40)); //Raw trout
        allItems.add(new Itemlist("Amulet of power", 1731, 1, 1000));
        allItems.add(new Itemlist("Camelot teleport", 8010, 20, 400));
        allItems.add(new Itemlist("Falador teleport", 8009, 20, 300));
        allItems.add(new Itemlist("Lumbridge teleport", 8008, 20, 400));
        allItems.add(new Itemlist("Varrock teleport", 8007, 50, 400));
        allItems.add(new Itemlist("Iron axe", 1349, 1, 900));
        allItems.add(new Itemlist("Steel axe", 1353, 1, 1500));
        allItems.add(new Itemlist("Mithril axe", 1355, 1, 1400));
        allItems.add(new Itemlist("Adamant axe", 1357, 1, 1600));
        allItems.add(new Itemlist("Water rune", 555, 20, 10));
        allItems.add(new Itemlist("Air rune", 556, 20, 10));
        allItems.add(new Itemlist("Earth rune", 557, 20, 10));
        allItems.add(new Itemlist("Rope", 954, 10, 1000));
        allItems.add(new Itemlist("Games necklace(8)", 3853, 8, 900));
        allItems.add(new Itemlist("Ring of dueling(8)", 2552, 5, 1200));
        allItems.add(new Itemlist("Spade", 952, 1, 3000));
        allItems.add(new Itemlist("Pure essence", 7936, 60, 30));
        allItems.add(new Itemlist("Iron bar", 2351, 5, 500));
        //allItems.add(new Itemlist("Pot", 1931, 4, 1000));
        allItems.add(new Itemlist("Bucket", 1925, 5, 1200));
        //allItems.add(new Itemlist("Bones", 526, 4, 300));
        //allItems.add(new Itemlist("Hammer", 2347, 1, 600));
        allItems.add(new Itemlist("Holy symbol", 1718, 1, 2000));
        allItems.add(new Itemlist("Polished buttons", 10496, 1, 10000));
        allItems.add(new Itemlist("Hard leather", 1743, 1, 300));
        allItems.add(new Itemlist("Amulet of glory(6)", 11978, 1, 10000));
        allItems.add(new Itemlist("Blue d'hide body", 2499, 1, 3000));
        allItems.add(new Itemlist("Blue d'hide chaps", 2493, 1, 3000));
        allItems.add(new Itemlist("Blue d'hide vamb", 2487, 1, 2000));
        allItems.add(new Itemlist("Snakeskin boots", 6328, 5, 1000));
        allItems.add(new Itemlist("Rune full helm", 1163, 1, 5000));
        //allItems.add(new Itemlist("Leather", 1741, 250, 100));
        allItems.add(new Itemlist("Thread", 1734, 100, 200));
        allItems.add(new Itemlist("Needle", 1733, 1, 700));
        allItems.add(new Itemlist("Spiny helmet", 4551, 1, 3000));
        allItems.add(new Itemlist("Shantay pass", 1854, 3, 1000));
        allItems.add(new Itemlist("Waterskin(4)", 1823, 10, 600));
        allItems.add(new Itemlist("Earmuffs", 4166, 1, 1000));
        allItems.add(new Itemlist("Candle", 36, 1, 3000));
        allItems.add(new Itemlist("Superantipoison(4)", 2448, 3, 1000));
        shuffle();
        init = true;
    }

    public void addToList(String name, int ID, int amount, int bonus) {
        itemList.add(name);
        itemIDList.add(ID);
        itemAmountList.add(amount);
        itemExtraList.add(bonus);
    }

    public int EmptySpots() {
        RSGEOffer[] offers = GrandExchange.getOffers();
        int empty = 0;
        if (offers.length > 0) {
            for (RSGEOffer offer : offers) {
                if (offer.getStatus() == RSGEOffer.STATUS.EMPTY) {
                    empty++;
                }
            }
        }
        return empty;
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
        General.println("Finished buying items!");
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

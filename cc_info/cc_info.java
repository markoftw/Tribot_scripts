package scripts.cc_info;

import java.io.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.tribot.api.*;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = "Marko", category = "Tools", name = "CC INFO", description = "Clan chat information", version = 0.3)
public class cc_info extends Script implements Painting, Ending {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;

    public final RSTile TILE_SPOT = new RSTile(3167, 3489, 0);

    @Override
    public void run() {
        General.useAntiBanCompliance(true);
        this.abc_util = new ABCUtil();
        while (!stop_script) {
            try {
                switch (state()) {
                    case LOGGING_IN:
                        botstatus = "Waiting for login...";
                        sleep(3000, 5000);
                        break;
                    case WALK_TO_SPOT:
                        botstatus = "Walking to spot";
                        WebWalking.walkTo(TILE_SPOT);
                        break;
                    case WORKING:
                        antibans();
                        customAntiban();
                        working();
                        break;
                    case JOIN_CLANCHAT:
                        botstatus = "Attempting to join clan chat";
                        debugg("Attempting to join clan chat...");
                        joinCC();
                        break;
                    case ERROR:
                        debugg("Stopping bot due to error.");
                        Login.logout();
                        stop_script = true;
                        break;
                }
                // control cpu usage
                General.sleep(100, 250);
            } catch (IOException ex) {
                Logger.getLogger(cc_info.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class UploadList {

        int RankID;
        String Username;

        public UploadList(int rank, String name) {
            this.RankID = rank;
            this.Username = name;
        }
    }

    public void working() throws MalformedURLException, IOException {
        if (GameTab.getOpen() == GameTab.TABS.CLAN) {
            botstatus = "Doin' some shiz";
            List<UploadList> pairs = new ArrayList<>();
            
            botstatus = "Grabbing player list";

            for (String player : ClanChat.getPlayerList()) {
                if (null != ClanChat.getPlayerRank(player)) {
                    switch (ClanChat.getPlayerRank(player)) {
                        case OWNER:
                            pairs.add(new UploadList(1, player));
                            break;
                        case GENERAL:
                            pairs.add(new UploadList(2, player));
                            break;
                        case CAPTAIN:
                            pairs.add(new UploadList(3, player));
                            break;
                        case LIEUTENANT:
                            pairs.add(new UploadList(4, player));
                            break;
                        case SERGEANT:
                            pairs.add(new UploadList(5, player));
                            break;
                        case CORPORAL:
                            pairs.add(new UploadList(6, player));
                            break;
                        case RECRUIT:
                            pairs.add(new UploadList(7, player));
                            break;
                        case FRIEND:
                            pairs.add(new UploadList(8, player));
                            break;
                        default:
                            //pairs.add(new UploadList(9, player));
                            break;
                    }
                }

            }

            String queryString = "https://CHANGEME.com/store/players/";
            
            if(pairs.size() > 0) {
               botstatus = "Sending data...";
                

                queryString = pairs.stream().map((pair) -> pair.RankID + ":" + pair.Username + ",").reduce(queryString, String::concat);

                queryString = queryString.substring(0, queryString.length() - 1);
                queryString = queryString.replaceAll("\\xa0", "_");

                //debugg(queryString);

                sendMessage(queryString); 
            } else {
                queryString += "0:EMPTY";
                //debugg(queryString);
                sendMessage(queryString);
                debugg("No ranks in clan chat.");
            }
            
            
            botstatus = "Waiting...";
            General.sleep(3000);
            
        } else {
            GameTab.open(GameTab.TABS.CLAN);
        }
    }

    public long LAST_UPDATE_ANTI = System.currentTimeMillis();

    public void customAntiban() {
        if (LAST_UPDATE_ANTI <= System.currentTimeMillis()) {
            int num = General.random(1, 3);

            switch (num) {
                case 1:
                    Camera.setCameraAngle(General.random(88, 100));
                    break;
                case 2:
                    Camera.setCameraRotation(General.random(260, 335));
                    break;
                case 3:
                    Mouse.randomRightClick();
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                default:
                    break;
            }

            LAST_UPDATE_ANTI = System.currentTimeMillis() + General.random(120 * 1000, 250 * 1000);
        }
    }

    public void sendMessage(String urlString) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (IOException e) {

        }

    }

    public void joinCC() {
        if (ClanChat.isTabOpen()) {
            //ClanChat.join("True_2k8");
            ClanChat.join("Ghjjf");
        } else {
            ClanChat.openTab();
        }
    }

    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGING_IN;
        } else if (Player.getPosition().distanceTo(TILE_SPOT) < 5 && ClanChat.isInClanChat()) {
            return State.WORKING;
        } else if (Player.getPosition().distanceTo(TILE_SPOT) > 5 && ClanChat.isInClanChat()) {
            return State.WALK_TO_SPOT;
        } else if (Login.getLoginState() != Login.STATE.LOGINSCREEN && Login.getLoginState() != Login.STATE.WELCOMESCREEN && !ClanChat.isInClanChat()) {
            return State.JOIN_CLANCHAT;
        }
        // if we dont satisfy any of the above conditions, we may have a problem
        return State.ERROR;
    }

    public void hopWorld() { //27cb+
        int world = WorldHop.getRandomWorld(true);
        General.println("[WORLDHOP] Attempting world hopping to: " + world);
        //WorldHop.hopTo(world);
        FCInGameHopper.hop(world);
        General.sleep(1000, 1500);
    }

    enum State {
        LOGGING_IN,
        WALK_TO_SPOT,
        ERROR,
        WORKING,
        JOIN_CLANCHAT
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
        General.println("Clan chat bot stopped.");
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
        g.setFont(font);
        g.setColor(new Color(255, 255, 255));
        g.drawString("Status: " + botstatus, 230, 300);
        g.drawString("Runtime: " + Timing.msToString(timeRan), 230, 320);

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

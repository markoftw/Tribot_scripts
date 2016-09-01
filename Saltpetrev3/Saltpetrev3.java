package scripts.Saltpetrev3;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = "Marko", category = "Money", name = "Saltpetre v3", description = "Collecting saltpetre on Zeah", version = 1.1)
public class Saltpetrev3 extends Script implements Painting {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    public final boolean running = true;
    public boolean stop_script = false;
    private final RSTile SALTPETRE_1 = new RSTile(1671, 3544); // (WEST - DESNO) druga +4
    private final RSTile SALTPETRE_2 = new RSTile(1689, 3532); // (NORTH) prva -4
    private final RSTile SALTPETRE_3 = new RSTile(1690, 3515); // (SOUTH) prva -4
    private final RSTile SALTPETRE_4 = new RSTile(1711, 3548); // (EAST - LEVO) druga -4
    private final RSTile BANK_TILE = new RSTile(1676, 3559);
    private final int SPADE_ID = 952;
    private final int SALTPETRE_ID = 13421;
    private final int[] SALTPETRE_IDS = {27433, 27434, 27435, 27436};
    private final int[] BOOTH_ID = {25808, 25809};
    private final int KONOO_ID = 6922;
    private boolean currently_mining = false;
    private boolean salt1_empty = false, salt2_empty = false, salt3_empty = false, salt4_empty = false;
    protected int walk_from = 0, saltpetreH = 0;
    protected int current_world = 0;
    private int globalna = 0;

    ArrayList<Integer> worldList = new ArrayList<>();
    ArrayList<Integer> visitedList = new ArrayList<>();

    RSTile[] WalkFrom_1 = {new RSTile(1670, 3549), new RSTile(1675, 3552), new RSTile(1675, 3561)};
    RSTile[] WalkTo_1 = {new RSTile(1676, 3561), new RSTile(1675, 3552), new RSTile(1670, 3548)};

    RSTile[] WalkFrom_2 = {new RSTile(1686, 3533), new RSTile(1679, 3541), new RSTile(1676, 3553), new RSTile(1674, 3561)};
    RSTile[] WalkTo_2 = {new RSTile(1676, 3561), new RSTile(1677, 3551), new RSTile(1680, 3539), new RSTile(1686, 3532)};

    RSTile[] WalkFrom_3 = {new RSTile(1687, 3516), new RSTile(1684, 3521), new RSTile(1679, 3534), new RSTile(1679, 3543), new RSTile(1678, 3551), new RSTile(1675, 3561)};
    RSTile[] WalkTo_3 = {new RSTile(1676, 3561), new RSTile(1676, 3550), new RSTile(1677, 3538), new RSTile(1678, 3526), new RSTile(1683, 3519), new RSTile(1686, 3513)};

    RSTile[] WalkFrom_4 = {new RSTile(1706, 3546), new RSTile(1699, 3546), new RSTile(1691, 3546), new RSTile(1682, 3551), new RSTile(1676, 3556), new RSTile(1676, 3561)};
    RSTile[] WalkTo_4 = {new RSTile(1676, 3561), new RSTile(1679, 3549), new RSTile(1687, 3548), new RSTile(1696, 3546), new RSTile(1699, 3546), new RSTile(1707, 3546)};

    /*RSTile[] WalkTo_2_from_1 = {new RSTile(1672, 3547), new RSTile(1679, 3540), new RSTile(1686, 3532)};
    RSTile[] WalkTo_3_from_2 = {new RSTile(1684, 3532), new RSTile(1685, 3521), new RSTile(1688, 3515)};
    RSTile[] WalkTo_4_from_3 = {new RSTile(1690, 3516), new RSTile(1696, 3523), new RSTile(1705, 3530), new RSTile(1706, 3540), new RSTile(1707, 3545)};
    RSTile[] WalkTo_1_from_4 = {new RSTile(1705, 3546), new RSTile(1693, 3545), new RSTile(1680, 3549), new RSTile(1671, 3547)};*/

    RSTile[] WalkTo_2_from_1 = {new RSTile(1672, 3547), new RSTile(1679, 3540), new RSTile(1686, 3532)};
    RSTile[] WalkTo_3_from_2 = {new RSTile(1684, 3530), new RSTile(1688, 3517)};
    RSTile[] WalkTo_4_from_3 = {new RSTile(1690, 3516), new RSTile(1698, 3524), new RSTile(1702, 3534), new RSTile(1704, 3543)};
    RSTile[] WalkTo_1_from_4 = {new RSTile(1705, 3546), new RSTile(1693, 3545), new RSTile(1680, 3549), new RSTile(1674, 3547)};
    
    private final static Area SALTPETRE1_AREA = new Area(new RSTile[]{
        new RSTile(1667, 3550), new RSTile(1673, 3550),
        new RSTile(1673, 3542), new RSTile(1667, 3542)});
    private final static Area SALTPETRE2_AREA = new Area(new RSTile[]{
        new RSTile(1683, 3534), new RSTile(1691, 3534),
        new RSTile(1691, 3526), new RSTile(1683, 3526)});
    private final static Area SALTPETRE3_AREA = new Area(new RSTile[]{
        new RSTile(1684, 3517), new RSTile(1692, 3517),
        new RSTile(1692, 3509), new RSTile(1684, 3509)});
    private final static Area SALTPETRE4_AREA = new Area(new RSTile[]{
        new RSTile(1705, 3550), new RSTile(1713, 3550),
        new RSTile(1713, 3542), new RSTile(1705, 3542)});
    private final static Area SALTPETRE5_AREA = new Area(new RSTile[]{
        new RSTile(1713, 3523), new RSTile(1720, 3523),
        new RSTile(1720, 3516), new RSTile(1713, 3516)});
    /*private final static Area SALTPETRE1_AREA = new Area(new RSTile[]{
        new RSTile(1665, 3550), new RSTile(1675, 3550),
        new RSTile(1675, 3540), new RSTile(1665, 3540)});
    private final static Area SALTPETRE2_AREA = new Area(new RSTile[]{
        new RSTile(1681, 3536), new RSTile(1695, 3536),
        new RSTile(1695, 3524), new RSTile(1681, 3524)});
    private final static Area SALTPETRE3_AREA = new Area(new RSTile[]{
        new RSTile(1683, 3518), new RSTile(1693, 3518),
        new RSTile(1693, 3509), new RSTile(1683, 3509)});
    private final static Area SALTPETRE4_AREA = new Area(new RSTile[]{
        new RSTile(1701, 3551), new RSTile(1714, 3551),
        new RSTile(1714, 3538), new RSTile(1701, 3538)});*/

    ABCUtil abc_util = null;

    @Override
    public void run() {
        GUI GUI = new GUI();
        GUI.setVisible(false);
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
                    hoverAnti();
                    break;
                case START_DIGGING:
                    botstatus = "Starting to dig";
                    startDigging();
                    break;
                case WALK_TO_BANK:
                    botstatus = "Walking to bank";
                    walkToBank();
                    break;
                case DEPOSIT_ITEMS:
                    botstatus = "Depositing items";
                    depositAll();
                    break;
                case GET_SPADE:
                    botstatus = "Taking spade";
                    getSpade();
                    break;
                case WALK_TO_SALTPETRE:
                    botstatus = "Searching for saltpetre";
                    checkRun();
                    searchSalt();
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

    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (Inventory.isFull() && Banking.isInBank()) {
            return State.DEPOSIT_ITEMS;
        } else if (Inventory.isFull() && !Banking.isInBank()) {
            currently_mining = false;
            if (isAtSalt_1()) {
                walk_from = 1;
                General.println("Walking from spot 1");
            } else if (isAtSalt_2()) {
                walk_from = 2;
                General.println("Walking from spot 2");
            } else if (isAtSalt_3()) {
                walk_from = 3;
                General.println("Walking from spot 3");
            } else if (isAtSalt_4()) {
                walk_from = 4;
                General.println("Walking from spot 4");
            }
            if (isKonooNear()) {
                //set current tile/loc to walk back
            }
            return State.WALK_TO_BANK;
        } else if (Inventory.getCount(SPADE_ID) < 1 && Banking.isInBank()) {
            return State.GET_SPADE;
        } else if (Inventory.getCount(SPADE_ID) < 1 && !Banking.isInBank()) {
            return State.WALK_TO_BANK;
        } else if ((isAtSalt_1() || isAtSalt_2() || isAtSalt_3() || isAtSalt_4()) && isKonooNear() && isKonooOnScreen()) {
            return State.START_DIGGING;
        } else if ((((isAtSalt_1() || isAtSalt_2() || isAtSalt_3() || isAtSalt_4()) && !isKonooNear()) || !Inventory.isFull())) {
            //is at one of the salts && konoo not near OR inventory is not full && not mining
            return State.WALK_TO_SALTPETRE;
        }
        // if we dont satisfy any of the above conditions, we may have a problem
        return State.SOMETHING_WENT_WRONG;
    }

    private void searchSalt() {
        if (Camera.getCameraAngle() < 85) { 
            botstatus = "Rotating camera";
            int angleRNG = General.random(85, 100);
            Camera.setCameraAngle(angleRNG);
        } else if (Camera.getCameraRotation() > 130 || Camera.getCameraRotation() < 145) {
            botstatus = "Rotating camera";
            int rotationRNG = General.random(130, 145);
            Camera.setCameraRotation(rotationRNG);
        }
        if (walk_from != 0) {
            //walk back to old one
            botstatus = "Walking back to previous rock";
            switch (walk_from) {
                case 1:
                    Walking.walkPath(WalkTo_1);
                    if (!isKonooNear() && isAtSalt_1()) {
                        botstatus = "Someone else mined it!";
                        General.println("Someone else mined it!");
                        walk_from = 0;
                    }
                    break;
                case 2:
                    Walking.walkPath(WalkTo_2);
                    if (!isKonooNear() && isAtSalt_2()) {
                        botstatus = "Someone else mined it!";
                        General.println("Someone else mined it!");
                        walk_from = 0;
                    }
                    break;
                case 3:
                    Walking.walkPath(WalkTo_3);
                    if (!isKonooNear() && isAtSalt_3()) {
                        botstatus = "Someone else mined it!";
                        General.println("Someone else mined it!");
                        walk_from = 0;
                    }
                    break;
                case 4:
                    Walking.walkPath(WalkTo_4);
                    if (!isKonooNear() && isAtSalt_4()) {
                        botstatus = "Someone else mined it!";
                        General.println("Someone else mined it!");
                        walk_from = 0;
                    }
                    break;
            }
        } else {
            botstatus = "Starting a new search..";
            //fresh search //bitwise operator

            if (isAtSalt_1() && !salt1_empty) {
                salt1_empty = true;
                globalna = globalna | 1;
                General.println("Salt 1 is empty");
            } else if (isAtSalt_2() && !salt2_empty) {
                salt2_empty = true;
                globalna = globalna | 2;
                General.println("Salt 2 is empty");
            } else if (isAtSalt_3() && !salt3_empty) {
                salt3_empty = true;
                globalna = globalna | 4;
                General.println("Salt 3 is empty");
            } else if (isAtSalt_4() && !salt4_empty) {
                salt4_empty = true;
                globalna = globalna | 8;
                General.println("Salt 4 is empty");
            } else if (globalna == 1) { // globalna&1 > 0
                General.println("Walk to 2");
                Walking.walkPath(WalkTo_2_from_1);
            } else if (globalna == 3) {
                General.println("Walk to 3");
                Walking.walkPath(WalkTo_3_from_2);
            } else if (globalna == 7) {
                General.println("Walk to 4");
                Walking.walkPath(WalkTo_4_from_3);
            } else if (globalna == 2) {
                General.println("Walk to 3");
                Walking.walkPath(WalkTo_3_from_2);
            } else if (globalna == 6) {
                General.println("Walk to 4");
                Walking.walkPath(WalkTo_4_from_3);
            } else if (globalna == 14) {
                General.println("Walk to 1");
                Walking.walkPath(WalkTo_1_from_4);
            } else if (globalna == 4) {
                General.println("Walk to 4");
                Walking.walkPath(WalkTo_4_from_3);
            } else if (globalna == 12) {
                General.println("Walk to 1");
                Walking.walkPath(WalkTo_1_from_4);
            } else if (globalna == 13) {
                General.println("Walk to 2");
                Walking.walkPath(WalkTo_2_from_1);
            } else if (globalna == 8) {
                General.println("Walk to 1");
                Walking.walkPath(WalkTo_1_from_4);
            } else if (globalna == 9) {
                General.println("Walk to 2");
                Walking.walkPath(WalkTo_2_from_1);
            } else if (globalna == 11) {
                General.println("Walk to 3");
                Walking.walkPath(WalkTo_3_from_2);
            } else if (globalna == 5) {
                //1 + 3
            } else if (globalna == 10) {
                //2+4
            } else if (globalna == 15) {
                //vse true == hopat world
                General.println("Hop world");
            } else if (globalna == 0) {
                //dodaj celotno areo && ni pri rock areji
                General.println("ERROR: Start script at a Saltpetre Rock!");
                stop_script = true;
            }

            // 2 * KOLICINA - 1
            /*else if (salt1_empty && !salt2_empty && !salt3_empty & !salt4_empty) { //normal start at 1 +
                //walk to salt 2
                General.println("Walk to 2");
                Walking.walkPath(WalkTo_2_from_1);
            } else if (salt1_empty && salt2_empty && !salt3_empty & !salt4_empty) { //normal start at 2
                //walk to salt 3
                General.println("Walk to 3");
                Walking.walkPath(WalkTo_3_from_2);
            } else if (salt1_empty && salt2_empty && salt3_empty & !salt4_empty) { //normal start at 3
                //walk to salt 4
                General.println("Walk to 4");
                Walking.walkPath(WalkTo_4_from_3);
            } else if (!salt1_empty && salt2_empty && !salt3_empty & !salt4_empty) { //start at 4
                //walk to salt 2
                General.println("Walk to 3");
                Walking.walkPath(WalkTo_3_from_2);
            } else if (!salt1_empty && salt2_empty && salt3_empty & !salt4_empty) { //start at 5
                //walk to salt 2
                General.println("Walk to 4");
                Walking.walkPath(WalkTo_4_from_3);
            } else if (!salt1_empty && salt2_empty && salt3_empty & salt4_empty) { //start at 6
                //walk to salt 2
                General.println("Walk to 1");
                Walking.walkPath(WalkTo_1_from_4);
            } else if (!salt1_empty && !salt2_empty && salt3_empty & !salt4_empty) { //start at 7
                //walk to salt 2
                General.println("Walk to 4");
                Walking.walkPath(WalkTo_4_from_3);
            } else if (!salt1_empty && !salt2_empty && salt3_empty & salt4_empty) { //start at 8
                //walk to salt 2
                General.println("Walk to 1");
                Walking.walkPath(WalkTo_1_from_4);
            } else if (salt1_empty && !salt2_empty && salt3_empty & salt4_empty) { //start at 9
                //walk to salt 2
                General.println("Walk to 2");
                Walking.walkPath(WalkTo_2_from_1);
            } else if (!salt1_empty && !salt2_empty && !salt3_empty & salt4_empty) { //start at 10
                //walk to salt 2
                General.println("Walk to 1");
                Walking.walkPath(WalkTo_1_from_4);
            } else if (salt1_empty && !salt2_empty && !salt3_empty & salt4_empty) { //start at 11
                //walk to salt 2
                General.println("Walk to 2");
                Walking.walkPath(WalkTo_2_from_1);
            } else if (salt1_empty && salt2_empty && !salt3_empty & salt4_empty) { //start at 12
                //walk to salt 2
                General.println("Walk to 3");
                Walking.walkPath(WalkTo_3_from_2);
            } else if (salt1_empty && salt2_empty && salt3_empty & salt4_empty) { //normal -- all empty -- need to hop
                //hop world
                General.println("Hop world");
                //set all to false?
            }*/
 /*if (current_world == 0) {
                //start search from 0
            } else {
                //hop world and set to 0 -> search
                botstatus = "Hopping world";
                if(!WorldHopper.isDeadman(41) && WorldHopper.isMembers(41) && notInArray(41)) {
                    WorldHopper.changeWorld(41);
                    sleep(1000,2000);
                    current_world = 0;
                }
            } */
        }
    }

    private void hoverAnti() {
        if (this.abc_util.shouldHover()) {
            if (Banking.isInBank() && Inventory.isFull()) {
                RSObject[] booths = Objects.findNearest(5, BOOTH_ID);
                if (booths.length > 0) {
                    booths[0].click("Bank");
                    sleep(1000, 1500);
                    if (Banking.isBankScreenOpen()) {
                        Banking.depositAllExcept(SPADE_ID);
                        saltpetreH += 27;
                        sleep(1000,1500);
                    }
                }
            } else if ((isAtSalt_1() || isAtSalt_2() || isAtSalt_3() || isAtSalt_4()) && isKonooNear()) {
                startDigging();
            }
        }
    }

    private void walkToBank() {
        switch (walk_from) {
            case 1:
                Walking.walkPath(WalkFrom_1);
                break;
            case 2:
                Walking.walkPath(WalkFrom_2);
                break;
            case 3:
                Walking.walkPath(WalkFrom_3);
                break;
            case 4:
                Walking.walkPath(WalkFrom_4);
                break;
        }
    }

    private void checkRun() {
        if (Game.getRunEnergy() > 50 && !Game.isRunOn()) {
            Options.setRunOn(true);
        }
    }

    private boolean isKonooNear() {
        RSNPC[] konoo = NPCs.findNearest(KONOO_ID);
        return konoo.length > 0;
    }
    
    private boolean isKonooOnScreen() {
        RSNPC[] konoo = NPCs.findNearest(KONOO_ID);
        if(konoo.length > 0){
            if(konoo[0].isOnScreen()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void getSpade() {
        if (!Banking.isBankScreenOpen()) {
            Banking.openBank();
        } else {
            Banking.withdraw(1, SPADE_ID);
        }
    }

    private void startDigging() {
        RSObject[] salt = Objects.findNearest(5, SALTPETRE_IDS);
        if (salt.length > 0) {
            if (!isKonooNear()) {
                currently_mining = false;
            } else if (currently_mining) {
                botstatus = "Digging...";
                antibans();
                if (!checkDigging()) {
                    currently_mining = false;
                }
            } else if (isKonooNear()) {
                botstatus = "Konoo found!";
                if (salt[0].isOnScreen() && salt[0].isClickable() && !Player.isMoving()) {
                    salt[0].click("Dig");
                    currently_mining = true;
                    current_world = WorldHopper.getWorld();
                    General.println("Currently in world: " + current_world);
                    salt4_empty = false;
                    salt3_empty = false;
                    salt2_empty = false;
                    salt1_empty = false;
                    globalna = 0;
                }
            }
        } else {
            walk_from = 0;
            currently_mining = false;
        }
    }

    private boolean checkDigging() {
        int a = 0;
        for (int x = 0; x < 6; x++) {
            if (Player.getAnimation() == 830) {
                a += 1;
            }
            sleep(750);
        }
        return a > 0;
    }

    private void depositAll() {
        if (!Banking.isBankScreenOpen()) {
            Banking.openBank();
        } else {
            Banking.depositAllExcept(SPADE_ID);
            saltpetreH += 27;
            sleep(1000, 1500);
        }
    }

    private boolean isAtSalt_1() {
        return SALTPETRE1_AREA.contains(Player.getPosition());
    }

    private boolean isAtSalt_2() {
        return SALTPETRE2_AREA.contains(Player.getPosition());
    }

    private boolean isAtSalt_3() {
        return SALTPETRE3_AREA.contains(Player.getPosition());
    }

    private boolean isAtSalt_4() {
        return SALTPETRE4_AREA.contains(Player.getPosition());
    }

    private boolean notInArray(int i) {
        return true;
    }

    enum State {
        WALK_TO_BANK,
        WALK_TO_SALTPETRE,
        MINE_ROCKS,
        DEPOSIT_ITEMS,
        SOMETHING_WENT_WRONG,
        WALKING,
        GET_SPADE,
        ANTI_BAN,
        LOGGED_IN,
        AT_SPOT,
        START_DIGGING
    }

    private long start;

    public void Stopwatch() {
        start = System.currentTimeMillis();
    }

    // return time (in seconds) since this object was created
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }

    public void CustomWalkPath(RSTile[] Array) {
        for (int x = 1; x < Array.length; x++) {
            Walking.walkTo(Array[x]);
            sleep(500, 650);
            int random = General.random(3, 5);
            Stopwatch();
            while (Player.isMoving() && Player.getPosition().distanceTo(Array[x]) > random) {
                sleep(10, 25);
                if (elapsedTime() > 12) {
                    break;
                }
            }
            if (x == (Array.length - 1) && Player.getPosition().distanceTo(Array[Array.length - 1]) > 15) {
                x = 0;
            }
        }
    }

    public void antibans() {
        if (this.abc_util.shouldCheckTabs()) {
            this.abc_util.checkTabs();
            General.println("Antiban: Checking tab");
        } else if (this.abc_util.shouldCheckXP()) {
            this.abc_util.checkXP();
            General.println("Antiban: Checking XP");
        } else if (this.abc_util.shouldExamineEntity()) {
            this.abc_util.examineEntity();
            General.println("Antiban: Examining entity");
        } else if (this.abc_util.shouldHover()) {
            //General.println("Antiban: Hovering *");
        } else if (this.abc_util.shouldLeaveGame()) {
            this.abc_util.leaveGame();
            General.println("Antiban: Mouse leaving game");
        } else if (this.abc_util.shouldMoveMouse()) {
            this.abc_util.moveMouse();
            General.println("Antiban: Moving mouse");
        } else if (this.abc_util.shouldMoveToAnticipated()) {
            //General.println("Antiban: MoveToAnticipated *");
        } else if (this.abc_util.shouldOpenMenu()) {
            //General.println("Antiban: Opening menu *");
        } else if (this.abc_util.shouldPickupMouse()) {
            this.abc_util.pickupMouse();
            General.println("Antiban: Picking up mouse");
        } else if (this.abc_util.shouldRightClick()) {
            this.abc_util.rightClick();
            General.println("Antiban: Right clicking");
        } else if (this.abc_util.shouldRotateCamera()) {
            this.abc_util.rotateCamera();
            General.println("Antiban: Rotating camera");
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
    private final int startLvl = Skills.getActualLevel(SKILLS.HUNTER);
    private final int startXP = Skills.getXP(SKILLS.HUNTER);
    private final int gePrice = PriceChecker.getGEPrice(SALTPETRE_ID);
    private final int osPrice = PriceChecker.getOSbuddyPrice(SALTPETRE_ID);
    Font font = new Font("Verdana", Font.BOLD, 14);

    @Override
    public void onPaint(Graphics g) {

        Graphics2D gg = (Graphics2D) g;
        gg.drawImage(img, 0, 304, null);

        long timeRan = System.currentTimeMillis() - START_TIME;
        int currentLvl = Skills.getActualLevel(SKILLS.HUNTER);
        int gainedLvl = currentLvl - startLvl;
        int gainedXP = Skills.getXP(SKILLS.HUNTER) - startXP;
        int xpToLevel = Skills.getXPToNextLevel(SKILLS.HUNTER);
        int perHR = (int) ((saltpetreH * 3600000) / timeRan);
        // long xpPerHour = (long)(gainedXP * 3600000 / timeRan);
        //int traps = getTrapNum();

        g.setFont(font);
        g.setColor(new Color(200, 0, 200));
        g.drawString("Status: " + botstatus, 200, 370);
        g.drawString("Runtime: " + Timing.msToString(timeRan), 200, 390);
        g.drawString("Saltpetre: " + saltpetreH + " (" + perHR + "/h) ", 200, 410);
        g.drawString("OSBuddy price: " + osPrice + " (" + osPrice * perHR + "gp/h)", 200, 430);
        g.drawString("GE price: " + gePrice + " (" + gePrice * perHR + "gp/h)", 200, 450);
        //g.drawString("XP TNL: " + xpToLevel, 225, 450);
        //g.drawString("Traps: " + traps, 225, 470);
        // g.drawString("XP/H: " + xpPerHour, 300, 450);

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

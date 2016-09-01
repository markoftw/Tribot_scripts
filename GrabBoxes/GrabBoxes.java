package scripts.GrabBoxes;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = "Marko", category = "New accounts", name = "Grab chin boxes", description = "Grab chin boxes from bank", version = 1.1)
public class GrabBoxes extends Script {

    private final int BIRD_TRAP_ID = 10006;
    private final int CHIN_TRAP_ID = 10008;
    private boolean stop_script = false;
    private boolean teleported = false;

    private final static Area BIRD_AREA = new Area(new RSTile[]{
        new RSTile(2496, 2944), new RSTile(2507, 2944),
        new RSTile(2507, 2934), new RSTile(2496, 2934)});

    private final static Area CASTLEWARS_AREA = new Area(new RSTile[]{
        new RSTile(2437, 3081), new RSTile(2447, 3081),
        new RSTile(2447, 3099), new RSTile(2437, 3099)});

    private final static Area HUNTPATH_AREA = new Area(new RSTile[]{
        new RSTile(2445, 3035), new RSTile(2462, 3035),
        new RSTile(2462, 3022), new RSTile(2445, 3022)});

    private final static Area HUNTFINAL_AREA = new Area(new RSTile[]{
        new RSTile(2536, 2894), new RSTile(2554, 2894),
        new RSTile(2554, 2881), new RSTile(2536, 2881)});

    @Override
    public void run() {
        while (!stop_script) {
            startLoop();
            sleep(100, 200);
        }
    }

    private void startLoop() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
           General.sleep(3000,4000); 
        } else if (isAtBirdArea() && !teleported) {
            //tp
            GameTab.open(GameTab.TABS.EQUIPMENT);
            General.sleep(200, 400);
            RSInterfaceChild ringSlot = Interfaces.get(387, 15);
            ringSlot.click("Castle Wars");
            General.sleep(2500, 3500);
            teleported = true;
        } else if (isInCastlewars() && teleported) {
            if (Inventory.getCount(BIRD_TRAP_ID) > 0) {
                //bank && take chin traps
                if(Banking.isBankScreenOpen()) {
                    Banking.depositAll();
                    General.sleep(800, 1000);
                    Banking.withdraw(24, CHIN_TRAP_ID);
                    General.sleep(800,1000);
                } else {
                    Banking.openBank();
                }
            } else if (Inventory.getCount(CHIN_TRAP_ID) > 0) {
                //walk to huntPath
                WebWalking.walkTo(new RSTile(2450, 3028));
                General.sleep(1000, 1500);
            }
        } else if (isInHuntPath() && teleported) {
            General.sleep(800, 1000);
            RSTile[] huntRSTiles = {new RSTile(2456, 3026), new RSTile(2466, 3022), new RSTile(2476, 3015), new RSTile(2485, 3006), new RSTile(2488, 2995), new RSTile(2490, 2984), new RSTile(2490, 2973), new RSTile(2485, 2966), new RSTile(2488, 2956), new RSTile(2493, 2948), new RSTile(2500, 2940)};
            Walking.walkPath(huntRSTiles);
        } else if (isAtFinalHunt() && teleported) {
            Login.logout();
            General.println("Arrived!");
            stop_script = true;
        } else {
            stop_script = true;
            General.println("ERROR");
        }
    }

    private boolean isAtBirdArea() {
        return BIRD_AREA.contains(Player.getPosition());
    }

    private boolean isInCastlewars() {
        return CASTLEWARS_AREA.contains(Player.getPosition());
    }

    private boolean isInHuntPath() {
        return HUNTPATH_AREA.contains(Player.getPosition());
    }

    private boolean isAtFinalHunt() {
        return HUNTFINAL_AREA.contains(Player.getPosition());
    }

}

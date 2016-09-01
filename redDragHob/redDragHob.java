package scripts.redDragHob;

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
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = "Marko", category = "RedDrags", name = "6. Hobgoblins", description = "50-70 ranging [START IN ANY BANK]", version = 1.1)
public class redDragHob extends Script implements Painting, Ending, MessageListening07 {

    public boolean GUI_COMPLETE = false;
    public static String botstatus = "";
    ABCUtil abc_util = null;
    public boolean stop_script = false;
    public final boolean debugging = true;

    public final int FOOD_ID = 333;
    public final int IRON_ARROW_ID = 884;

    public RSNPC target = null;
    public boolean HOPPING_SOON = false;
    public boolean WAS_IN_COMBAT = false;
    public boolean SLEEPER = false;

    public final RSTile HOBGOBLIN_TILE = new RSTile(2906, 3292, 0);

    /* -  -  -  -  -  -  -  - */
    public int totalHerbs = 0;
    public int totalMoney = 0;
    public boolean SET_FOOD = false;
    public RSPlayer[] players_1 = null;
    public RSPlayer[] players_2 = null;
    public long timeRan_1 = 0;
    public long timeRan_2 = 0;

    /*private final static Area GOBLIN_AREA = new Area(new RSTile[]{
        new RSTile(3158, 3494, 0), new RSTile(3171, 3494, 0),
        new RSTile(3171, 3485, 0), new RSTile(3158, 3485, 0)});*/
    private final RSArea GOBLIN_AREA = new RSArea(new RSTile[]{
        new RSTile(2901, 3300, 0),
        new RSTile(2912, 3299, 0),
        new RSTile(2914, 3284, 0),
        new RSTile(2900, 3284, 0)
    });

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
                case BANKING:
                    botstatus = "Banking";
                    takeFood();
                    break;
                case FINISHED:
                    General.println("We're done");
                    Login.logout();
                    stop_script = true;
                    break;
                case WALK_TO_GOBLINS:
                    botstatus = "Walking to Hobgoblins";
                    debugg("Walking to Hobgoblins: " + Player.getPosition().toString());
                    //random.nextInt(30 + 1 + 10) - 10;
                    //max = 30; min = -10;
                    int x = HOBGOBLIN_TILE.getX() + General.random(-1, 1);
                    int y = HOBGOBLIN_TILE.getY() + General.random(-1, 1);
                    WebWalking.walkTo(new RSTile(x, y));
                    break;
                case COMBAT:
                    botstatus = "Fighting";
                    heal();
                    antibans();
                    checkSleeping();
                    break;
                case ATTACK:
                    botstatus = "Attacking nearest Hobgoblin";
                    checkarrows();
                    modsnear();
                    hoppingWorldCheck();
                    camera();
                    heal();
                    antibans();
                    pickupArrows();
                    equipArrows();
                    checkNearbyPlayers();
                    //attackInactiveMonster();
                    if (attack()) {
                        debugg("Successfully attacked Hobgoblin");
                    }
                    break;
                case ERROR:
                    botstatus = "ERROR...";
                    debugg("ERROR...?");
                    break;
                case NEW_COMBAT:
                    botstatus = "Combat";
                    combatTime();
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
        WALK_TO_GOBLINS,
        ATTACK,
        COMBAT,
        FINISHED,
        ERROR,
        BANKING,
        NEW_COMBAT
    }

    private State state() {
        if (Login.getLoginState() != Login.STATE.INGAME) {
            return State.LOGGED_IN;
        } else if (Skills.getActualLevel(SKILLS.RANGED) >= 70) {
            return State.FINISHED;
        } else if (!foodExists() && !Banking.isInBank()) {
            return State.WALK_TO_BANK;
        } else if (!foodExists() && Banking.isInBank()) {
            return State.BANKING;
            /*} else if (playerInCombat() || Player.getAnimation() == 426 || Player.getAnimation() == 390 || playerIsAttacking()) {
            return State.COMBAT;
            } else if (isInsideGoblins() && !playerInCombat() && !playerIsAttacking() && Player.getAnimation() != 426 & Player.getAnimation() != 390) {
            return State.ATTACK;
            } else if (isInsideGoblins() && !playerIsAttacking() && !playerInCombat() && (target == null || Player.getRSPlayer().getInteractingCharacter() == null || !target.isInteractingWithMe())) {
            return State.ATTACK;*/
        } else if (isInsideGoblins()) { //if player in combat && target != player interacted npc
            /*if (target != null) {
                if (ATTACKING(true)) {
                    //debugg("NULL: FALSE -- IN COMBAT");
                    return State.COMBAT;
                } else if (NOT_ATTACKING(true)) {
                    //debugg("NULL: FALSE -- ATTACK");
                    return State.ATTACK;
                }
            } else if (ATTACKING(false)) {
                //debugg("NULL: TRUE -- IN COMBAT");
                return State.COMBAT;
            } else if (NOT_ATTACKING(false)) {
                //debugg("NULL: TRUE -- ATTACK");
                return State.ATTACK;
            } else {
                return State.ERROR;
            }*/
            return State.NEW_COMBAT;
        } else if (Player.isMoving()) {
            return State.WALKING;
        } else if (!isInsideGoblins()) {
            return State.WALK_TO_GOBLINS;
        }
        return State.ERROR;
        //return State.WALK_TO_GOBLINS;
    }

    private boolean combatTime() {
        RSNPC[] goblins = NPCs.findNearest(Filters.NPCs.nameEquals("Hobgoblin").combine(Filters.NPCs.inArea(GOBLIN_AREA), true));
        if (!HOPPING_SOON) {
            if (Player.getRSPlayer().isInCombat() && Player.getRSPlayer().getInteractingCharacter() != null) {
                if ("Hobgoblin".equals(Player.getRSPlayer().getInteractingCharacter().getName())) {
                    botstatus = "Combat with Hobgoblin";
                    target = (RSNPC) Player.getRSPlayer().getInteractingCharacter();
                    heal();
                    antibans();
                    checkSleeping();
                    moveMouse();
                }
            } else if (Combat.getAttackingEntities().length > 0 && target != null) {
                botstatus = "Ranging Hobgoblin";
                heal();
                antibans();
                checkSleeping();
                moveMouse();
            } else if (goblins.length > 0 && !Player.getRSPlayer().isInCombat()) {
                checkarrows();
                modsnear();
                hoppingWorldCheck();
                camera();
                heal();
                antibans();
                pickupArrows();
                equipArrows();
                checkNearbyPlayers();
                if (GOBLIN_AREA.contains(goblins[0].getPosition())) {
                    //debugg("Attacking goblin");
                    target = goblins[0];
                    if(!target.isInCombat() && target.isValid() && target.isOnScreen()) {
                        botstatus = "Attacking";
                        if(target.click("Attack")){
                            WAS_IN_COMBAT = true;
                            sleeper();
                            return Timing.waitCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    General.sleep(250, 500);
                                    return target.isInCombat();
                                }
                            }, 7500);
                        }
                    }
                } else {
                    botstatus = "Waiting for goblins";
                    debugg("Waiting for goblins");
                    return false;
                }
            } else if (Combat.getAttackingEntities().length <= 0 && Player.getRSPlayer().isInCombat() && target != null) {
                checkarrows();
                modsnear();
                hoppingWorldCheck();
                camera();
                heal();
                antibans();
                pickupArrows();
                equipArrows();
                checkNearbyPlayers();
                if(target.getHealth() == 0) {
                    debugg("Target is dead");
                    if(goblins.length > 1) {
                        if (GOBLIN_AREA.contains(goblins[0].getPosition())) {
                            if(goblins[0].isValid() && goblins[0].getHealth() > 0) {
                                target = goblins[0];
                            } else if(goblins[1].isValid() && goblins[1].getHealth() > 0) {
                                target = goblins[1];
                            } else {
                                target = goblins[0];
                            }
                            if(!target.isInCombat() && target.isValid() && target.isOnScreen()) {
                                botstatus = "Attacking new NPC";
                                if(target.click("Attack")){
                                    WAS_IN_COMBAT = true;
                                    sleeper();
                                    return Timing.waitCondition(new Condition() {
                                        @Override
                                        public boolean active() {
                                            General.sleep(250, 500);
                                            return target.isInCombat();
                                        }
                                    }, 7500);
                                }
                            }
                        } else {
                            botstatus = "Waiting for goblins";
                            debugg("Waiting for goblins |2|");
                            return false;
                        }
                     }else {
                        botstatus = "Waiting for goblins";
                        debugg("Waiting for goblins |3|");
                        return false;
                    }
                } else {
                    debugg("Under attack! Attempting to attack NPC back");
                    return false;
                }
            } else if (Combat.getAttackingEntities().length > 0 && Player.getRSPlayer().isInCombat()) {
                debugg("Under attack by new NPC while still attacking! Attempting to attack back");
                return false;
            } else {
                botstatus = "No match";
                Mouse.leaveGame(true);
                return false;
            }
        } else {
            hoppingWorldCheck();
        }
        return false;
    }
    
    private boolean ATTACKING(final boolean trgt) {
        if (trgt) {
            return target.isInCombat() && (playerAttackingAndNotAttacked() || Combat.isUnderAttack() || target.isInteractingWithMe() || Player.getAnimation() == 426 || Player.getAnimation() == 424);
        } else {
            return Combat.getAttackingEntities().length > 0 || playerAttackingAndNotAttacked();
        }
    }

    private boolean NOT_ATTACKING(final boolean trgt) {
        if (trgt) {
            return Combat.getAttackingEntities().length < 1 && !playerAttackingAndNotAttacked() && Player.getRSPlayer().getInteractingCharacter() == null && !target.isInteractingWithMe();
        } else {
            return Combat.getAttackingEntities().length < 1 && !playerAttackingAndNotAttacked() && Player.getRSPlayer().getInteractingCharacter() == null;
        }
    }

    private void checkarrows() {
        if (Equipment.getCount(IRON_ARROW_ID) < 20) {
            debugg("Low/No arrows left, stopping");
            General.sleep(11000, 12000);
            Login.logout();
            stop_script = true;
        }
    }

    public void waitIsMovin() {
        for (int i = 0; i < 57; i++, sleep(30, 40)) {
            if (!Player.isMoving()) {
                break;
            }
        }
    }

    public void waitTillDead(RSNPC npc) {
        boolean hi = false;
        for (int i = 0; i < 800; i++, sleep(30, 40)) {
            heal();
            if (npc == null || Player.getRSPlayer().getInteractingCharacter() == null || !npc.isInteractingWithMe()) {// || npc[0].getCombatCycle() > 0)) {
                println("npc is dead");
                hi = true;
                break;
            }
        }
        if (!hi) {
            println("timeout");
        }
    }

    //RSNPC
    public RSNPC attackInactiveMonster() {
        RSNPC monster = getClosestInactiveNPC();
        if (monster != null) {
            if (monster.isOnScreen()) {
                //if(DynamicClicking.clickRSModel(monster.getModel(), "Attack")){
                //if(clickModel(monster.getModel(), "Attack", false)){
                if (monster.click("Attack")) {
                    target = monster;
                    WAS_IN_COMBAT = true;
                    sleeper();
                    waitIsMovin();
                    waitTillDead(monster);
                    General.sleep(1000, 1250);
                    /*return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(250, 500);
                            return target.isInCombat();
                        }
                    }, 5500);*/
                }
            }
            /*else if (monster.getPosition().distanceTo(Player.getPosition()) <= 6) {
                Camera.turnToTile(monster.getPosition());
                sleep(2000, 2500);
            } else {
                Walking.blindWalkTo(monster.getPosition());
                sleep(2000, 2500);
            }*/
        }
        return monster;
        //return false;
    }

    private RSNPC getClosestInactiveNPC() {
        RSNPC[] npcs = NPCs.findNearest("Hobgoblin");
        if (npcs.length > 0) {
            for (int x = 0; x < npcs.length; x++) {
                if (npcs[x].isInteractingWithMe() && npcs[x].isValid()) {
                    debugg("Already in combat, attacking back");
                    return npcs[x];
                } else if (!npcs[x].isInCombat() && PathFinding.isTileWalkable(npcs[x].getAnimablePosition())) {
                    return npcs[x];
                }
            }
        }
        return null;
    }

    private boolean modsnear() {
        RSPlayer allPlayers[] = Players.getAll();
        if (allPlayers.length > 0) {
            RSPlayer arsplayer[];
            int k = (arsplayer = allPlayers).length;
            for (int j = 0; j < k; j++) {
                RSPlayer i = arsplayer[j];
                if (i != null && i.getName() != null && i.getName().contains("Mod ")) {
                    debugg("ALERT! Jagex mod naerby: " + i.getName());
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private boolean attack() {
        if (!HOPPING_SOON) {
            camera();
            //RSNPC[] goblins = NPCs.findNearest(Filters.NPCs.nameEquals("Hobgoblin").combine(Filters.NPCs.inArea(GOBLIN_AREA), true));
            RSNPC[] goblins = NPCs.findNearest("Hobgoblin");
            if (goblins.length > 0 && !Player.isMoving()) {
                if (!goblins[1].isInCombat() && goblins[1].isValid() && goblins[1].isOnScreen()) {
                    if (goblins[1].click("Attack")) {
                        target = goblins[1];
                        WAS_IN_COMBAT = true;
                        sleeper();
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return target.isInCombat();
                            }
                        }, 7500);
                    }
                } else if (Player.getPosition().distanceTo(HOBGOBLIN_TILE) == 0) {
                    botstatus = "Waiting for goblins...";
                } else if (!goblins[0].isOnScreen()) {
                    botstatus = "Walking to mid";
                    Walking.blindWalkTo(HOBGOBLIN_TILE);
                }
            }
        }
        return false;
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

    public void takeFood() {
        if (Banking.isBankScreenOpen()) {
            Banking.depositAllExcept(IRON_ARROW_ID);
            General.sleep(250, 500);
            Banking.withdraw(26, FOOD_ID);
            General.sleep(250, 500);
        } else {
            Banking.openBank();
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

    private void hoppingWorldCheck() {
        if (HOPPING_SOON) {
            if (WAS_IN_COMBAT) {
                General.sleep(12000, 13000);
            } else {
                General.sleep(1000, 1500);
            }
            hopWorld();
        }
    }

    /*private boolean attack() {
        if (!HOPPING_SOON) {
            camera();
            RSNPC[] goblins = NPCs.findNearest(Filters.NPCs.nameEquals("Hobgoblin").combine(Filters.NPCs.inArea(GOBLIN_AREA), true));
            
            if (goblins.length > 0 && !Player.isMoving()) {
                for(RSNPC goblin : goblins) {
                    if(!goblin.isInCombat() && goblin.isValid() && goblin.isOnScreen()) {
                        if(goblin.click("Attack")) {
                            target = goblin;
                            WAS_IN_COMBAT = true;
                            sleeper();
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }*/

 /* private boolean attack() {
        if (!Game.isRunOn() && Game.getRunEnergy() > this.abc_util.generateRunActivation()) {
            botstatus = "Turning run on";
            Options.setRunOn(true);
        }
        if (!HOPPING_SOON) {
            camera();
            RSNPC[] goblins = NPCs.findNearest(Filters.NPCs.nameEquals("Hobgoblin").combine(Filters.NPCs.inArea(GOBLIN_AREA), true));
            if (goblins.length > 0 && !Player.isMoving()) {
                if (!goblins[1].isInCombat() && goblins[1].isValid() && goblins[1].isOnScreen()) {
                    if (goblins[1].click("Attack")) {
                        target = goblins[1];
                        WAS_IN_COMBAT = true;
                        sleeper();
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Player.getAnimation() == 426 || Player.getAnimation() == 390 || playerInCombat() || Player.isMoving() || targetEngagedWithMe(target) || targetIsAttackingMe(target);
                            }
                        }, 3500);
                    }
                } else if (Player.getPosition().distanceTo(HOBGOBLIN_TILE) == 0) {
                    botstatus = "Waiting for goblins...";
                } else if (!goblins[1].isOnScreen()) {
                    botstatus = "Walking to mid";
                    Walking.blindWalkTo(HOBGOBLIN_TILE);
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
    }*/
    public void hopWorld() { //27cb+
        int world = WorldHop.getRandomWorld(true);
        General.println("[WORLDHOP] Attempting world hopping from:" + WorldHopper.getWorld() + " to: " + world);
        //WorldHop.hopTo(world);
        FCInGameHopper.hop(world);
        General.sleep(1000, 1500);
        HOPPING_SOON = false;
        WAS_IN_COMBAT = false;
    }

    public void checkNearbyPlayers() {
        if (Player.getRSPlayer().getCombatLevel() > 26) {
            RSPlayer[] players = Players.getAll();
            if (players.length > 1) {
                for (int i = 0; i < players.length; i++) {
                    if (Player.getPosition().distanceTo(players[i].getPosition()) < 7 && !players[i].getName().equalsIgnoreCase(Player.getRSPlayer().getName())) {
                        General.println("Player nearby: " + players[i].getName());
                        HOPPING_SOON = true;
                    }
                }
            }
        }
    }

    private boolean equipArrows() {
        RSItem[] arrows = Inventory.find(IRON_ARROW_ID);
        if (arrows.length > 0) {
            int stack = General.random(100, 250);
            if (arrows[0] != null && arrows[0].getStack() > stack) {
                botstatus = "Equiping looted arrows";
                if (GameTab.TABS.INVENTORY.isOpen()) {
                    if (Clicking.click("Wield", arrows[0])) {
                        return Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(250, 500);
                                return Inventory.find(IRON_ARROW_ID).length == 0;
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

    private boolean pickupArrows() {
        RSGroundItem[] arrows = GroundItems.findNearest(IRON_ARROW_ID);
        if (arrows.length > 0) {
            for (RSGroundItem arrow : arrows) {
                if (arrow.getStack() > 2) {
                    int currInv = Inventory.getAll().length;
                    botstatus = "Picking up arrows";
                    if (Clicking.click("Take " + arrow.getDefinition().getName(), arrow)) {
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
        }
        return false;
    }

    public void moveMouse() {
        int x = General.random(0, 150);
        if (x > 50 && x < 53) {
            this.abc_util.moveMouse();
            debugg("Antiban: moving mouse randomly");
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
        if (SLEEPER && target.getHealth() <= 5) {
            botstatus = "Sleeping..";
            Mouse.leaveGame(true);
            int x = General.random(3000, 8000);
            General.println("Antiban: Sleeping for " + x + "ms");
            General.sleep(x);
            SLEEPER = false;
        }
    }

    private boolean isInsideGoblins() {
        return GOBLIN_AREA.contains(Player.getPosition());
    }

    /* Check if the player is in combat */
    public boolean playerInCombat() {
        return Combat.getAttackingEntities().length > 0;
    }

    /* Check if the player is attacking a target */
    public boolean playerIsAttacking() {
        return Combat.isUnderAttack();
    }

    /* Check if the user is in combat and not attacking a target */
    public boolean playerInCombatAndNotAttacking() {
        return Combat.getAttackingEntities().length > 0 && !Combat.isUnderAttack();
    }

    /* Check if the user is attacking a target and not being attacked  */
    public boolean playerAttackingAndNotAttacked() {
        return Combat.getAttackingEntities().length < 1 && Combat.isUnderAttack();
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

    @Override
    public void serverMessageReceived(String arg0) {
        General.println("Server message: " + arg0);
    }

    @Override
    public void clanMessageReceived(String arg0, String arg1) {
        General.println("Clan chat message: " + arg0 + ", " + arg1);
    }

    @Override
    public void duelRequestReceived(String arg0, String arg1) {
        General.println("Duel request: " + arg0 + ", " + arg1);
    }

    @Override
    public void personalMessageReceived(String arg0, String arg1) {
        General.println("Personal message: " + arg0 + ", " + arg1);
    }

    @Override
    public void tradeRequestReceived(String arg0) {
        General.println("Trade request: " + arg0);
    }

    @Override
    public void playerMessageReceived(String arg0, String arg1) {
        General.println("Player message: " + arg0 + ", " + arg1);
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

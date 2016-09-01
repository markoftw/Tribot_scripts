package scripts.cc_info;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClanChat {

    private final static RSInterface clan = Interfaces.get(589);
    private final static RSInterfaceChild clanList = Interfaces.get(589, 5);

    public enum Rank {
        NONE,
        FRIEND,
        RECRUIT,
        CORPORAL,
        SERGEANT,
        LIEUTENANT,
        CAPTAIN,
        GENERAL,
        OWNER
    }

    public static String[] getPlayerList() {
        List<String> players = new ArrayList<>();
        General.sleep(1000);
        if (clanList != null) {
            for (int i = 0; i < clanList.getChildren().length; i += 3) {
                players.add(clanList.getChildren()[i].getText());
            }
            return players.stream().toArray(String[]::new);
        }

        return null;
    }

    public static boolean hasPlayer(String player) {
        return getPlayerComponent(player) != null;
    }

    public static int getPlayerWorld(String player) {
        RSInterfaceComponent playerComponent = getPlayerComponent(player);

        if (playerComponent != null) {
            return Integer.parseInt(cleanString(clanList.getChildren()[playerComponent.getIndex() + 1].getText()).split(" ")[1]);
        }

        return -1;
    }

    public static Rank getPlayerRank(String player) {
        RSInterfaceComponent playerComponent = getPlayerComponent(player);

        if (playerComponent != null) {
            //General.println(clanList.getChildren()[playerComponent.getIndex() + 2].getTextureID());
            return getRankByTexture(clanList.getChildren()[playerComponent.getIndex() + 2].getTextureID());
        }

        return Rank.NONE;
    }

    public void debugg(String msg) {
        General.println(msg);
    }

    public static boolean kick(String player) {
        return playerAction(player, "Kick user", 0);
    }

    public static boolean addFriend(String player) {
        return playerAction(player, "Add friend", 0);
    }

    public static boolean hopTo(String player) {
        playerAction(player, "Hop-to", 0);

        Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                return Login.getLoginState().equals(Login.STATE.WELCOMESCREEN);
            }
        }, General.random(4000, 6000));

        if (Login.getLoginState().equals(Login.STATE.WELCOMESCREEN)) {
            Interfaces.get(378, 17).click();

            return true;
        }

        return false;
    }

    public static void join(String clanChat) {
        if (!isInClanChat()) {
            if (clan.getChild(8) != null) {
                clan.getChild(8).click("Join Chat");
                General.sleep(700, 1000);
                Keyboard.typeSend(clanChat);
            }
            /*Timing.waitCondition(new Condition()
            {
                @Override
                public boolean active()
                {
                    return Interfaces.get(162, 32) != null;
                }
            }, General.random(900, 1500));*/

            General.sleep(3000, 4000);
        }
    }

    public static void leave() {
        if (isInClanChat()) {
            if (clan.getChild(8) != null) {
                clan.getChild(8).click("Leave Chat");
            }
        }

    }

    public static void openTab() {
        if (!GameTab.getOpen().equals(GameTab.TABS.CLAN)) {
            GameTab.open(GameTab.TABS.CLAN);
            General.sleep(5000, 7000);
        }
    }

    public static boolean isTabOpen() {
        return GameTab.getOpen().equals(GameTab.TABS.CLAN);
    }

    public static String getTitle() {
        if (isInClanChat()) {
            return cleanString(clan.getChild(0).getText().split(">")[2]);
        }

        return null;
    }

    public static String getOwner() {
        if (isInClanChat()) {
            return cleanString(clan.getChild(1).getText().split(">")[2]);
        }

        return null;
    }

    public static boolean isInClanChat() {
        if (clan != null) {
            if (clan.getChild(8) != null) {
                return clan.getChild(8).getText().equals("Leave Chat");
            }
            return false;
        }

        return false;
    }

    private static boolean playerAction(String player, String action, int retries) {
        if (isTabOpen()) {
            Rectangle playerList = clan.getChild(4).getAbsoluteBounds();
            RSInterfaceComponent playerComponent = getPlayerComponent(player);

            if (playerComponent != null && playerList != null) {
                if (playerList.contains(playerComponent.getAbsoluteBounds())) {
                    playerComponent.hover();

                    if (Game.getUptext() != null) {
                        if (cleanString(Game.getUptext()).toLowerCase().contains(player.toLowerCase())) {
                            Mouse.clickBox(playerComponent.getAbsoluteBounds(), 3);

                            Timing.waitChooseOption(action + " " + player.replaceAll(" ", "\u00A0"), General.random(1000, 2000));

                            return true;
                        }
                    }

                    if (retries >= 3) {
                        return false;
                    }

                    playerAction(player, action, retries + 1);
                } else {
                    scrollToPlayerComponent(playerList, playerComponent);
                    playerAction(player, action, retries);
                }
            }
        }

        return false;
    }

    private static RSInterfaceComponent getPlayerComponent(String player) {
        if (clanList != null) {
            for (RSInterfaceComponent c : clanList.getChildren()) {
                //if (cleanString(c.getText().toLowerCase()).equals(player.toLowerCase()))
                if (c.getText().toLowerCase().equals(player.toLowerCase())) {
                    return c;
                }
            }
        }

        return null;
    }

    private static void scrollToPlayerComponent(Rectangle playerList, RSInterfaceComponent playerComponent) {
        while (!playerList.contains(Mouse.getPos())) {
            Mouse.moveBox(playerList);
        }

        while (!playerList.contains(playerComponent.getAbsoluteBounds())) {
            Mouse.scroll(playerComponent.getAbsoluteBounds().y < playerList.y);
            General.sleep(50, 100);
        }

        if (playerComponent.getAbsoluteBounds().y > playerList.y + playerList.getHeight() - playerComponent.getHeight()) {
            Mouse.scroll(false);
            General.sleep(50, 100);
        }
    }

    private static Rank getRankByTexture(int textureID) {
        Rank rank = Rank.NONE;

        switch (textureID) {
            case 1004:
                rank = Rank.FRIEND;
                break;
            case 1012:
                rank = Rank.RECRUIT;
                break;
            case 1011:
                rank = Rank.CORPORAL;
                break;
            case 1010:
                rank = Rank.SERGEANT;
                break;
            case 1009:
                rank = Rank.LIEUTENANT;
                break;
            case 1008:
                rank = Rank.CAPTAIN;
                break;
            case 1007:
                rank = Rank.GENERAL;
                break;
            case 1006:
                rank = Rank.OWNER;
                break;
        }

        return rank;
    }

    private static String cleanString(String string) {
        return string.replaceAll("\u00A0", " ");
    }
}

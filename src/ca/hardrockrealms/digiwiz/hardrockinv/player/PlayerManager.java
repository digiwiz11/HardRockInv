package ca.hardrockrealms.digiwiz.hardrockinv.player;

import ca.hardrockrealms.digiwiz.hardrockinv.HardRockInv;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: DAD
 * Date: 11/21/13
 * Time: 9:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerManager {
    private HardRockInv m_Plugin = null;
    private HashMap<String, PlayerData> m_ActivePlayers = new HashMap<String, PlayerData>();

    public PlayerManager(HardRockInv parent) {
        m_Plugin = parent;
    }

    public void loadPlayerFile(Player player) {
        if (m_ActivePlayers.containsKey(player.getName()) == false) {
            PlayerData file = new PlayerData(player, m_Plugin);
            file.loadConfig();
            m_ActivePlayers.put(player.getName(), file);
        }
    }

    public void unloadPlayerFile(Player player) {
        m_ActivePlayers.remove(player.getName());
    }

    public void playerLogin(Player player) {
        if (m_ActivePlayers.containsKey(player.getName()) == true) {
            PlayerData data = m_ActivePlayers.get(player.getName());
            String currentWorld = player.getWorld().getName();
            String logoutWorld = data.logoutWorld();

            if (currentWorld.equals(logoutWorld) == false) {
                World logout = m_Plugin.getServer().getWorld(logoutWorld);

                playerWorldChange(player, logout, player.getWorld());
            }
        }
    }

    public boolean isSameGroup(World world1, World world2) {
        boolean bSame = false;

        if (world1 != null && world2 != null) {
            String currentGroup = m_Plugin.configSettings().getGroup(world1);
            String newGroup = m_Plugin.configSettings().getGroup(world2);

            if (currentGroup != null && newGroup != null) {
                bSame = currentGroup.equals(newGroup);
            }
        }

        return bSame;
    }

    public void playerWorldChange(Player player, World originalWorld, World newWorld) {
        if (m_ActivePlayers.containsKey(player.getName()) == true) {
            if (isSameGroup(originalWorld, newWorld) == false) {
                PlayerData data = m_ActivePlayers.get(player.getName());
                data.storeInventory(originalWorld);
                data.restoreInventory(newWorld);
                data.setLogoutWorld(newWorld);
                data.saveConfig();
            }
        }
    }
}

package ca.hardrockrealms.digiwiz.hardrockinv.listeners;

import ca.hardrockrealms.digiwiz.hardrockinv.HardRockInv;
import ca.hardrockrealms.digiwiz.hardrockinv.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created with IntelliJ IDEA.
 * User: DAD
 * Date: 11/21/13
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerListener implements Listener {
    private HardRockInv m_Plugin = null;
    private PlayerManager m_PlayerManager = null;

    public PlayerListener(PlayerManager playerManager, HardRockInv parent) {
        m_Plugin = parent;
        m_PlayerManager = playerManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        m_Plugin.getLogger().info("Player onPlayerLogin");
        m_PlayerManager.loadPlayerFile(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogout(PlayerQuitEvent event) {
        m_PlayerManager.unloadPlayerFile(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        m_Plugin.getLogger().info("Player onPlayerChangedWorld");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        m_Plugin.getLogger().info("Player onPlayerTeleport");

    }

}

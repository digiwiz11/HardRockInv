package ca.hardrockrealms.digiwiz.hardrockinv.listeners;

import ca.hardrockrealms.digiwiz.hardrockinv.HardRockInv;
import ca.hardrockrealms.digiwiz.hardrockinv.player.PlayerManager;
import ca.hardrockrealms.digiwiz.hardrockinv.runnables.PlayerLoginRunnable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Digiwiz
 * Date: 11/21/13
 * Time: 8:36 PM
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
        m_PlayerManager.loadPlayerFile(event.getPlayer());

        //Wait until all the plugins / systems finish handling the PlayerLoginEvent, then we can process the inventory.
        //Schedule the player login code for later.
        m_Plugin.getServer().getScheduler().scheduleSyncDelayedTask(m_Plugin, new PlayerLoginRunnable(event.getPlayer(), m_PlayerManager), 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogout(PlayerQuitEvent event) {
        m_PlayerManager.unloadPlayerFile(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World originalWorld = event.getFrom();
        World newWorld = player.getWorld();

        m_PlayerManager.playerWorldChange(player, originalWorld, newWorld);
    }
}

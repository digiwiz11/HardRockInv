package ca.hardrockrealms.digiwiz.hardrockinv.runnables;

import ca.hardrockrealms.digiwiz.hardrockinv.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 11/22/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */

public class PlayerLoginRunnable extends BukkitRunnable {
    private Player m_Player = null;
    private PlayerManager m_PlayerManager = null;

    public PlayerLoginRunnable(Player Player, PlayerManager manager) {
        m_Player = Player;
        m_PlayerManager = manager;
    }

    @Override
    public void run() {
        if (m_Player != null && m_Player.isOnline() == true) {
            m_PlayerManager.playerLogin(m_Player);
        }
    }
}

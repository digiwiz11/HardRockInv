package ca.hardrockrealms.digiwiz.hardrockinv;

import ca.hardrockrealms.digiwiz.hardrockinv.generator.EmptyGenerator;
import ca.hardrockrealms.digiwiz.hardrockinv.listeners.PlayerListener;
import ca.hardrockrealms.digiwiz.hardrockinv.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: DAD
 * Date: 11/21/13
 * Time: 8:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class HardRockInv extends JavaPlugin {


    //	private static WorldInventory m_Instance = null;
    private World m_InventoryWorld = null;
    private PlayerListener m_PlayerListener = null;
    private ConfigSettings m_ConfigSettings = null;
    private LastChestSettings m_LastChestSettings = null;
    private PlayerManager m_PlayerManager = null;

    public YamlConfiguration m_Config = null;

    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");

        m_ConfigSettings = new ConfigSettings("config.yml", this);
        m_ConfigSettings.loadConfig();

        m_LastChestSettings = new LastChestSettings("lastchest.yml", this);
        m_LastChestSettings.loadConfig();

        m_PlayerManager = new PlayerManager(this);

        m_PlayerListener = new PlayerListener(m_PlayerManager, this);

        getInventoryWorld();

        //Register the listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(m_PlayerListener, this);

    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
    }

    /**
     * Get the inventory world, or create it, if it does not exist.
     *
     * @return Returns a reference to the inventory world.
     */

    public World getInventoryWorld() {
        if (m_InventoryWorld == null) {
            m_InventoryWorld = WorldCreator.name(m_ConfigSettings.worldName).type(WorldType.FLAT).environment(org.bukkit.World.Environment.NORMAL).generator(new EmptyGenerator()).createWorld();

            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), (new StringBuilder("mv import ")).append(m_ConfigSettings.worldName).append(" normal -g ").append(m_ConfigSettings.worldName).toString());
            }
        }

        return m_InventoryWorld;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String uid) {
        return new EmptyGenerator();
    }

    public LastChestSettings lastChestSettings()
    {
        return m_LastChestSettings;
    }

    public ConfigSettings configSettings()
    {
        return m_ConfigSettings;
    }

}

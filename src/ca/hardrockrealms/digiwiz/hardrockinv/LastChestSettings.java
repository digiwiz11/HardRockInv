package ca.hardrockrealms.digiwiz.hardrockinv;

import ca.hardrockrealms.digiwiz.hardrockinv.player.ChestPosition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: DAD
 * Date: 11/21/13
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class LastChestSettings {
    private HardRockInv m_Plugin = null;
    private YamlConfiguration m_ConfigData = null;
    private String m_ConfigFile = null;
    private int m_nMaxX = 128;
    private int m_nGapSize = 4;

    // Settings
    public int m_LastXPos = 0;
    public int m_LastYPos = 1;
    public int m_LastZPos = 0;
    // Settings

    public LastChestSettings(String filename, HardRockInv parent){
        m_ConfigFile = filename;
        m_ConfigData = new YamlConfiguration();
        m_Plugin = parent;
    }

    public ChestPosition claimNextChestPosition(){
        ChestPosition pos = new ChestPosition();

        m_LastXPos += m_nGapSize;

        if ((m_LastXPos + m_nGapSize) > m_nMaxX)
        {
            m_LastXPos = 0;
            m_LastZPos += m_nGapSize;
        }

        pos.xPos = m_LastXPos;
        pos.yPos = m_LastYPos;
        pos.zPos = m_LastZPos;
        pos.nSize = m_nGapSize;

        saveConfig();
        return pos;
    }

    public void loadConfig() {
        boolean bSaveFile = false;

        if (m_Plugin.getDataFolder().exists() == false) {
            m_Plugin.getDataFolder().mkdir();
        }

        File configFile = new File(m_Plugin.getDataFolder(), m_ConfigFile);

        if (configFile.exists() == true) {
            try {
                m_ConfigData.load(configFile);
            } catch (Exception e) {
                bSaveFile = true;
                e.printStackTrace();
            }
        }
        else
        {
            bSaveFile = true;
        }

        if (m_ConfigData.contains("LastXPos") == true)
            m_LastXPos = m_ConfigData.getInt("LastXPos");

        if (m_ConfigData.contains("LastYPos") == true)
            m_LastYPos = m_ConfigData.getInt("LastYPos");

        if (m_ConfigData.contains("LastZPos") == true)
            m_LastZPos = m_ConfigData.getInt("LastZPos");

        if (bSaveFile == true)
        {
            saveConfig();
        }
    }

    public void saveConfig() {
        File configFile = new File(m_Plugin.getDataFolder(), m_ConfigFile);

        try {
            if (m_ConfigData != null)
            {
                m_ConfigData.set("LastXPos", m_LastXPos);
                m_ConfigData.set("LastYPos", m_LastYPos);
                m_ConfigData.set("LastZPos", m_LastZPos);
                m_ConfigData.save(configFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

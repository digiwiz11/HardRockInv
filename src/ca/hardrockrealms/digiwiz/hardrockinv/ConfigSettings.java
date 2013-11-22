package ca.hardrockrealms.hardrockinv;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: DAD
 * Date: 11/21/13
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigSettings {
    private HardRockInv m_Plugin = null;
    private YamlConfiguration m_ConfigData = null;
    private String m_ConfigFile = null;

    // Settings
    public String worldName = "HardRockInv";
    private HashMap<String, String> m_GroupList = new HashMap<String, String>();
    // Settings

    public ConfigSettings(String filename, HardRockInv parent){
        m_ConfigFile = filename;
        m_ConfigData = new YamlConfiguration();
        m_Plugin = parent;
    }

    public List<String> groupList() {
        List<String> list = new ArrayList<String>();
        Iterator iter = m_GroupList.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();

            if (list.contains(entry.getValue().toString()) == false)
            {
                list.add(entry.getValue().toString());
            }
        }

        return list;
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

        if (m_ConfigData.contains("WorldName") == true)
            worldName = m_ConfigData.getString("WorldName");

        //Read in world groups
        ConfigurationSection groupSection = m_ConfigData.getConfigurationSection("WorldGroups");
        if (groupSection != null)
        {
            Set<String> groups = groupSection.getKeys(false);
            for(String group: groups)
            {
                List<String> worlds = groupSection.getStringList(group);
                for (String world: worlds)
                {
                    m_GroupList.put(world, group);
                }
            }
        }

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
                m_ConfigData.set("WorldName", worldName);
                m_ConfigData.save(configFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

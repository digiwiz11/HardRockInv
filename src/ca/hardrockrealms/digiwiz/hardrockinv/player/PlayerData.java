package ca.hardrockrealms.digiwiz.hardrockinv.player;

import ca.hardrockrealms.digiwiz.hardrockinv.HardRockInv;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: DAD
 * Date: 11/21/13
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */

public class PlayerData {
    private HardRockInv m_Plugin = null;
    private YamlConfiguration m_ConfigData = null;
    private Player m_Player = null;
    private File m_PlayerFile = null;
    private ChestPosition m_ChestPlotPosition = new ChestPosition();
    private int m_LastYPosition = 1;
    private HashMap<String, Location> m_ChestLocations = new HashMap<String, Location>();

    public PlayerData(Player player, HardRockInv parent){
        m_Player = player;
        m_ConfigData = new YamlConfiguration();
        m_Plugin = parent;
    }

    public void loadConfig() {
        boolean bSaveFile = false;
        File playerFolder = new File(m_Plugin.getDataFolder(), "players");
        World invWorld = m_Plugin.getInventoryWorld();

        if (playerFolder.exists() == false) {
            playerFolder.mkdirs();
        }

        m_PlayerFile = new File(playerFolder, m_Player.getName() + ".yml");

        if (m_PlayerFile.exists() == true) {
            try {
                m_ConfigData.load(m_PlayerFile);
            } catch (Exception e) {
                bSaveFile = true;
                e.printStackTrace();
            }

            if (m_ConfigData.contains("ChestPlotX") == true && m_ConfigData.contains("ChestPlotY") == true && m_ConfigData.contains("ChestPlotZ") == true && m_ConfigData.contains("ChestPlotSize") == true)
            {
                m_ChestPlotPosition.xPos = m_ConfigData.getInt("ChestPlotX");
                m_ChestPlotPosition.yPos = m_ConfigData.getInt("ChestPlotY");
                m_ChestPlotPosition.zPos = m_ConfigData.getInt("ChestPlotZ");
                m_ChestPlotPosition.nSize = m_ConfigData.getInt("ChestPlotSize");
            }
            else
            {
                m_Plugin.getLogger().warning("Player chest data location missing, creating new location.");
                m_ChestPlotPosition = m_Plugin.lastChestSettings().claimNextChestPosition();
                bSaveFile = true;
            }
        }
        else
        {
            m_ChestPlotPosition = m_Plugin.lastChestSettings().claimNextChestPosition();

//            //TEMP
//            Location chestLocation = new Location(invWorld, m_ChestPlotPosition.xPos, m_ChestPlotPosition.yPos, m_ChestPlotPosition.zPos);
//            Block chest = invWorld.getBlockAt(chestLocation);
//
//            chest.setType(Material.CHEST);
//
//            //TEMP

            bSaveFile = true;
        }

        if (m_ConfigData.contains("LastYPosition") == true)
            m_LastYPosition = m_ConfigData.getInt("LastYPosition");
        else
            m_LastYPosition = m_ChestPlotPosition.yPos;

        //Read in group chest locations
        ConfigurationSection groupSection = m_ConfigData.getConfigurationSection("WorldGroups");
        List<String> groupList = m_Plugin.configSettings().groupList();

        for (String group: groupList)
        {
            int xPos = m_ChestPlotPosition.xPos;
            int yPos = m_LastYPosition;
            int zPos = m_ChestPlotPosition.zPos;

            if (groupSection != null)
            {
                if (groupSection.contains(group) == true)
                {
                    yPos = groupSection.getInt(group + ".ChestPosY");
                }
                else
                {
                    m_LastYPosition += 1;
                    yPos = m_LastYPosition;
                    bSaveFile = true;
                }
            }
            else
            {
                m_LastYPosition += 1;
                yPos = m_LastYPosition;
                bSaveFile = true;
            }

            Location loc = new Location(invWorld, xPos, yPos, zPos);
            m_ChestLocations.put(group, loc);
        }

        //Make sure the chest locations have chests placed.
        Iterator iter = m_ChestLocations.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Location loc = (Location) entry.getValue();
            Location loc2 = new Location(loc.getWorld(), loc.getX() + 1, loc.getY(), loc.getZ());
            Location signLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 1);

            Block chest = invWorld.getBlockAt(loc);
            Block chest2 = invWorld.getBlockAt(loc2);
            Block playerSign = invWorld.getBlockAt(signLoc);

            if (chest.getType() != Material.CHEST)
                chest.setType(Material.CHEST);

            if (chest2.getType() != Material.CHEST)
                chest2.setType(Material.CHEST);

            if (playerSign.getType() != Material.WALL_SIGN)
            {
                playerSign.setType(Material.WALL_SIGN);
                playerSign.setData((byte) 0x03); //South Facing.
                Sign sign = (Sign) (playerSign.getState());
                sign.setLine(0, m_Player.getName());
                sign.setLine(2, "[Group]");
                sign.setLine(3, entry.getKey().toString());
                sign.update();
            }
        }


        if (bSaveFile == true)
        {
            saveConfig();
        }
    }

    public void saveConfig() {
        try {
            if (m_PlayerFile != null)
            {
                m_ConfigData.set("ChestPlotX", m_ChestPlotPosition.xPos);
                m_ConfigData.set("ChestPlotY", m_ChestPlotPosition.yPos);
                m_ConfigData.set("ChestPlotZ", m_ChestPlotPosition.zPos);
                m_ConfigData.set("ChestPlotSize", m_ChestPlotPosition.nSize);
                m_ConfigData.save(m_PlayerFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

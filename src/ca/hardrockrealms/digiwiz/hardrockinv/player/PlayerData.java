package ca.hardrockrealms.digiwiz.hardrockinv.player;

import ca.hardrockrealms.digiwiz.hardrockinv.HardRockInv;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private String m_LogoutWorld = "";
    private int m_LastYPosition = 5;
    private HashMap<String, Location> m_ChestLocations = new HashMap<String, Location>();

    public PlayerData(Player player, HardRockInv parent) {
        m_Player = player;
        m_ConfigData = new YamlConfiguration();
        m_Plugin = parent;
    }

    public void setLogoutWorld(String stWorld) {
        m_LogoutWorld = stWorld;
    }

    public void setLogoutWorld(World world) {
        m_LogoutWorld = world.getName();
    }

    public String logoutWorld() {
        return m_LogoutWorld;
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

            if (m_ConfigData.contains("LogoutWorld") == true) {
                m_LogoutWorld = m_ConfigData.getString("LogoutWorld");
            }

            if (m_ConfigData.contains("ChestPlotX") == true && m_ConfigData.contains("ChestPlotY") == true && m_ConfigData.contains("ChestPlotZ") == true && m_ConfigData.contains("ChestPlotSize") == true) {
                m_ChestPlotPosition.xPos = m_ConfigData.getInt("ChestPlotX");
                m_ChestPlotPosition.yPos = m_ConfigData.getInt("ChestPlotY");
                m_ChestPlotPosition.zPos = m_ConfigData.getInt("ChestPlotZ");
                m_ChestPlotPosition.nSize = m_ConfigData.getInt("ChestPlotSize");
            } else {
                m_Plugin.getLogger().warning("Player chest data location missing, creating new location.");
                m_ChestPlotPosition = m_Plugin.lastChestSettings().claimNextChestPosition();
                bSaveFile = true;
            }
        } else {
            m_ChestPlotPosition = m_Plugin.lastChestSettings().claimNextChestPosition();
            bSaveFile = true;
        }

        if (m_ConfigData.contains("LastYPosition") == true)
            m_LastYPosition = m_ConfigData.getInt("LastYPosition");
        else
            m_LastYPosition = m_ChestPlotPosition.yPos;

        //Read in group chest locations
        ConfigurationSection groupSection = m_ConfigData.getConfigurationSection("WorldGroups");
        List<String> groupList = m_Plugin.configSettings().groupList();

        for (String group : groupList) {
            int xPos = m_ChestPlotPosition.xPos;
            int yPos = m_LastYPosition;
            int zPos = m_ChestPlotPosition.zPos;

            if (groupSection != null) {
                if (groupSection.contains(group) == true) {
                    yPos = groupSection.getInt(group + ".ChestPosY");
                } else {
                    m_LastYPosition += 1;
                    yPos = m_LastYPosition;
                    bSaveFile = true;
                }
            } else {
                m_LastYPosition += 1;
                yPos = m_LastYPosition;
                bSaveFile = true;
            }

            Location loc = new Location(invWorld, xPos, yPos, zPos);
            m_ChestLocations.put(group, loc);
        }

        //Make sure the chest locations have chests placed.
        Iterator iter = m_ChestLocations.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            setupInventoryChests((Location) entry.getValue(), entry.getKey().toString());
        }

        if (bSaveFile == true) {
            saveConfig();
        }
    }

    private void setupInventoryChests(Location loc, String stGroupName) {
        World invWorld = m_Plugin.getInventoryWorld();
        Location loc2 = new Location(loc.getWorld(), loc.getX() + 1, loc.getY(), loc.getZ());
        Location enderLoc = new Location(loc.getWorld(), loc.getX() + 3, loc.getY(), loc.getZ());
        Location signLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 1);

        Block chest = invWorld.getBlockAt(loc);
        Block chest2 = invWorld.getBlockAt(loc2);
        Block enderChest = invWorld.getBlockAt(enderLoc);
        Block playerSign = invWorld.getBlockAt(signLoc);

        if (chest.getType() != Material.CHEST)
            chest.setType(Material.CHEST);

        if (chest2.getType() != Material.CHEST)
            chest2.setType(Material.CHEST);

        if (enderChest.getType() != Material.CHEST)
            enderChest.setType(Material.CHEST);

        if (playerSign.getType() != Material.WALL_SIGN) {
            playerSign.setType(Material.WALL_SIGN);
            playerSign.setData((byte) 0x03); //South Facing.
            Sign sign = (Sign) (playerSign.getState());
            sign.setLine(0, m_Player.getName());
            sign.setLine(2, "[Group]");
            sign.setLine(3, stGroupName);
            sign.update();
        }
    }

    public void saveConfig() {
        try {
            if (m_PlayerFile != null) {
                m_ConfigData.set("ChestPlotX", m_ChestPlotPosition.xPos);
                m_ConfigData.set("ChestPlotY", m_ChestPlotPosition.yPos);
                m_ConfigData.set("ChestPlotZ", m_ChestPlotPosition.zPos);
                m_ConfigData.set("ChestPlotSize", m_ChestPlotPosition.nSize);
                m_ConfigData.set("LastYPosition", m_LastYPosition);
                m_ConfigData.set("LogoutWorld", m_LogoutWorld);

                //Store the chest locations
                ConfigurationSection groups = m_ConfigData.createSection("WorldGroups");
                Iterator iter = m_ChestLocations.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    ConfigurationSection groupSection = groups.createSection(entry.getKey().toString());
                    Location loc = (Location) entry.getValue();
                    groupSection.set("ChestPosY", (int) loc.getY());
                }
                m_ConfigData.save(m_PlayerFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreInventory(World world) {
        if (world == null) {
            return;
        }

        String currentGroup = m_Plugin.configSettings().getGroup(world);

        if (currentGroup != null) {
            if (m_ChestLocations.containsKey(currentGroup) == true) {
                World invWorld = m_Plugin.getInventoryWorld();
                Location chestLoc = m_ChestLocations.get(currentGroup);
                Block chestBlock = invWorld.getBlockAt(chestLoc);

                if (chestBlock.getType() == Material.CHEST) {
                    Chest chest = (Chest) chestBlock.getState();
                    Inventory chestInv = chest.getInventory();

                    //Retrieve the armor
                    m_Player.getInventory().setHelmet(chestInv.getItem(0));
                    m_Player.getInventory().setChestplate(chestInv.getItem(1));
                    m_Player.getInventory().setLeggings(chestInv.getItem(2));
                    m_Player.getInventory().setBoots(chestInv.getItem(3));

                    //Restore the inventory
                    int nNumItems = m_Player.getInventory().getSize();
                    ItemStack[] items = new ItemStack[nNumItems];
                    for (int index = 0; index < nNumItems; index++) {
                        m_Player.getInventory().setItem(index, chestInv.getItem(9 + index));
                    }

                    //Clear the chest
                    chestInv.clear();
                } else {
                    m_Plugin.getLogger().warning("Chest for player [" + m_Player.getName() + "] was not found in inventory world!");
                }

                //Restore the ender chest
                Location enderChestLoc = new Location(chestLoc.getWorld(), chestLoc.getX() + 3, chestLoc.getY(), chestLoc.getZ());
                Block enderChestBlock = invWorld.getBlockAt(enderChestLoc);
                if (enderChestBlock.getType() == Material.CHEST) {
                    Chest chest = (Chest) enderChestBlock.getState();

                    m_Player.getEnderChest().clear();
                    m_Player.getEnderChest().setContents(chest.getInventory().getContents());

                    chest.getInventory().clear();
                } else {
                    m_Plugin.getLogger().warning("Ender chest for player [" + m_Player.getName() + "] was not found in inventory world!");
                }
            }
        }
    }

    public void storeInventory(World world) {
        if (world == null) {
            return;
        }

        String currentGroup = m_Plugin.configSettings().getGroup(world);

        if (currentGroup != null) {
            if (m_ChestLocations.containsKey(currentGroup) == true) {
                ItemStack[] playerInventory = m_Player.getInventory().getContents();
                World invWorld = m_Plugin.getInventoryWorld();

                Location chestLoc = m_ChestLocations.get(currentGroup);
                Location enderChestLoc = new Location(chestLoc.getWorld(), chestLoc.getX() + 3, chestLoc.getY(), chestLoc.getZ());

                Block chestBlock = invWorld.getBlockAt(chestLoc);
                Block enderChestBlock = invWorld.getBlockAt(enderChestLoc);

                if (chestBlock.getType() == Material.CHEST) {
                    Chest chest = (Chest) chestBlock.getState();
                    Inventory chestInv = chest.getInventory();

                    chestInv.clear();

                    //Store the armor
                    chestInv.setItem(0, m_Player.getInventory().getHelmet());
                    chestInv.setItem(1, m_Player.getInventory().getChestplate());
                    chestInv.setItem(2, m_Player.getInventory().getLeggings());
                    chestInv.setItem(3, m_Player.getInventory().getBoots());

                    //Store the inventory
                    int index = 9; //9 Gives the first row of the chest to the armor
                    for (ItemStack item : playerInventory) {
                        chestInv.setItem(index++, item);
                    }

                    //Clear the player inventory and armor
                    m_Player.getInventory().clear();
                    m_Player.getInventory().setHelmet(null);
                    m_Player.getInventory().setChestplate(null);
                    m_Player.getInventory().setLeggings(null);
                    m_Player.getInventory().setBoots(null);
                } else {
                    //ERROR Chest not found.
                }

                if (enderChestBlock.getType() == Material.CHEST) {
                    ItemStack[] playerEnderChest = m_Player.getEnderChest().getContents();
                    Chest chest = (Chest) enderChestBlock.getState();
                    Inventory chestInv = chest.getInventory();

                    chestInv.setContents(playerEnderChest);
                    m_Player.getEnderChest().clear();
                } else {
                    //ERROR Chest not found.
                }
            }
        }
    }

}

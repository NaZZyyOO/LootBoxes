package ink.anh.nazzyyoo.lootboxes.lootbox;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import ink.anh.api.items.ItemStackSerializer;
import ink.anh.nazzyyoo.lootboxes.LootBoxes;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LootTableManager {

    private static LootTableManager instance;

    private Map<String, LootTable> lootTableCache;

    private LootTableManager() {
        lootTableCache = new ConcurrentHashMap<>();
        loadLootTables();
    }

    public static LootTableManager getInstance() {
        if (instance == null) {
            instance = new LootTableManager();
        }
        return instance;
    }

    public LootTable getLootTable(String name) {
        return lootTableCache.get(name);
    }

    public void addLootTable(LootTable lootTable) {
        lootTableCache.put(lootTable.getLootTableName(), lootTable);
        saveLootTable(lootTable);
    }

    public void removeLootTable(String name) {
        lootTableCache.remove(name);
        saveLootTable(name);
    }

    public Map<String, LootTable> getLootTableCache() {
        return lootTableCache;
    }

    public void reloadLootTables() {
        lootTableCache.clear();
        loadLootTables();
    }

    private void loadLootTables() {
        File file = new File(LootBoxes.getInstance().getDataFolder(), "loottables.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (config != null && config.contains("loot_tables")) {
            ConfigurationSection lootTablesSection = config.getConfigurationSection("loot_tables");
            for (String key : lootTablesSection.getKeys(false)) {
                ConfigurationSection tableSection = lootTablesSection.getConfigurationSection(key);

                // Читання предметів таблиці луту
                LootItem[] lootItems = loadLootItems(tableSection);
                LootTable lootTable = new LootTable(key, lootItems);
                lootTableCache.put(key, lootTable);
            }
        }
    }

    private LootItem[] loadLootItems(ConfigurationSection tableSection) {
    	if (tableSection == null) return new LootItem[0];
        
    	LootItem[] lootItems = new LootItem[tableSection.getKeys(false).size()];
        int index = 0;

        for (String itemKey : tableSection.getKeys(false)) {
            ConfigurationSection itemSection = tableSection.getConfigurationSection(itemKey);
            
            ItemStack item = ItemStackSerializer.deserializeItemStackFromYaml(itemKey);
            int chance = itemSection.getInt("chance");
            int minQuantity = itemSection.getInt("min_quantity");
            int maxQuantity = itemSection.getInt("max_quantity");

            lootItems[index++] = new LootItem(item, chance, minQuantity, maxQuantity);
        }

        return lootItems;
    }

    public void saveLootTables() {
        File file = new File(LootBoxes.getInstance().getDataFolder(), "loottables.yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, LootTable> entry : lootTableCache.entrySet()) {
            String tableName = entry.getKey();
            LootTable lootTable = entry.getValue();

            ConfigurationSection tableSection = config.createSection("loot_tables." + tableName);
            saveLootItems(tableSection, lootTable.getLootItems());
        }

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLootItems(ConfigurationSection tableSection, LootItem[] lootItems) {
        for (int i = 0; i < lootItems.length; i++) {
            LootItem lootItem = lootItems[i];
            ConfigurationSection itemSection = tableSection.createSection("items.item" + i);
            itemSection.set("item", ItemStackSerializer.serializeItemStackToYaml(lootItem.getItem()));
            itemSection.set("chance", lootItem.getChance());
            itemSection.set("min_quantity", lootItem.getMinQuantity());
            itemSection.set("max_quantity", lootItem.getMaxQuantity());
        }
    }

    private void saveLootTable(LootTable lootTable) {
        File file = new File(LootBoxes.getInstance().getDataFolder(), "loottables.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection tableSection = config.createSection("loot_tables." + lootTable.getLootTableName());
        saveLootItems(tableSection, lootTable.getLootItems());

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLootTable(String name) {
        File file = new File(LootBoxes.getInstance().getDataFolder(), "loottables.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (lootTableCache.containsKey(name)) {
            LootTable lootTable = lootTableCache.get(name);
            ConfigurationSection tableSection = config.createSection("loot_tables." + lootTable.getLootTableName());
            saveLootItems(tableSection, lootTable.getLootItems());
        } else {
            config.set("loot_tables." + name, null);
        }

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

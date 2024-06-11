package ink.anh.nazzyyoo.lootboxes.lootbox;

import ink.anh.api.database.AbstractTableRegistrar;
import ink.anh.api.database.DatabaseManager;
import ink.anh.api.database.SQLiteDatabaseManager;
import ink.anh.nazzyyoo.lootboxes.LootBoxes;

public class TableRegistry extends AbstractTableRegistrar {
    private LootBoxes plugin;

    public TableRegistry(LootBoxes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerAllTables(DatabaseManager dbManager) {
        
        // Реєстрація таблиць для SQLite
        if (dbManager instanceof SQLiteDatabaseManager) {
            dbManager.registerTable(LootBox.class, new LootBoxTable(plugin.getGlobalManager()));
        }
    }
}
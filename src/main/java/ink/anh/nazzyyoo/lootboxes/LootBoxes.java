package ink.anh.nazzyyoo.lootboxes;

import org.bukkit.plugin.java.JavaPlugin;
import ink.anh.nazzyyoo.lootboxes.commands.ConfigCreatingCommands;
import ink.anh.nazzyyoo.lootboxes.commands.ConfigCreatingTabCompleter;
import ink.anh.nazzyyoo.lootboxes.events.LootBoxOpenEvent;
import ink.anh.nazzyyoo.lootboxes.events.LootBoxesMarking;
import ink.anh.nazzyyoo.lootboxes.gui.LootBoxGuiListener;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBoxManager;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTableManager;

public class LootBoxes extends JavaPlugin {

    private static LootBoxes instance;
    private GlobalManager manager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        manager = GlobalManager.getManager(this);
        manager.setDatabaseManager();
        manager.getDatabaseManager().initialize();
        manager.getDatabaseManager().initializeTables();

        LootBoxManager.getInstance(manager);
        LootTableManager.getInstance();
        
        this.getCommand("lootboxes").setExecutor(new ConfigCreatingCommands(this));
        this.getCommand("lootboxes").setTabCompleter(new ConfigCreatingTabCompleter());
        
        getServer().getPluginManager().registerEvents(new LootBoxOpenEvent(this), this);
        getServer().getPluginManager().registerEvents(new LootBoxesMarking(this), this);
        getServer().getPluginManager().registerEvents(new LootBoxGuiListener(), this);
    }

    public GlobalManager getGlobalManager() {
        return manager;
    }

    public static LootBoxes getInstance() {
        return instance;
    }
}
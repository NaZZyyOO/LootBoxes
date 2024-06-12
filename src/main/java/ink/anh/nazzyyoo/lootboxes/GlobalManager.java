package ink.anh.nazzyyoo.lootboxes;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import ink.anh.api.LibraryManager;
import ink.anh.api.database.DatabaseManager;
import ink.anh.api.database.MySQLConfig;
import ink.anh.api.database.SQLiteDatabaseManager;
import ink.anh.api.lingo.Translator;
import ink.anh.api.lingo.lang.LanguageManager;
import ink.anh.api.messages.Logger;
import ink.anh.api.utils.SyncExecutor;
import ink.anh.nazzyyoo.lootboxes.lootbox.TableRegistry;
import net.md_5.bungee.api.ChatColor;

// Manages global functionalities of the plugin, extending LibraryManager.
public class GlobalManager extends LibraryManager {

    // Singleton instance of this class.
    private static GlobalManager instance;

    // Reference to the main plugin instance.
    private LootBoxes plugin;

    // Manager for handling language translations.
    private LanguageManager langManager;

    // Name of the plugin.
    private String pluginName;

    // Default language setting.
    private String defaultLang;

    // Flag to enable or disable debug mode.
    private boolean debug;    

    private DatabaseManager dbManager;

    // Private constructor to enforce singleton pattern.
    private GlobalManager(LootBoxes plugin) {
        super(plugin);
        this.plugin = plugin;
        this.saveDefaultConfig(); // Saves the default configuration.
        this.loadFields(plugin);  // Loads fields from the config.
    }

    // Static method to get the singleton instance of GlobalManager.
    public static synchronized GlobalManager getManager(LootBoxes plugin) {
        if (instance == null) {
            instance = new GlobalManager(plugin);
        }
        return instance;
    }

    // Overrides getPlugin method from LibraryManager.
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    // Gets the name of the plugin.
    @Override
    public String getPluginName() {
        return pluginName;
    }

    // Gets the LanguageManager instance.
    @Override
    public LanguageManager getLanguageManager() {
        return this.langManager;
    }

    // Gets the default language.
    @Override
    public String getDefaultLang() {
        return defaultLang;
    }

    // Checks if debug mode is enabled.
    @Override
    public boolean isDebug() {
        return debug;
    }
    
    // Loads fields from the plugin's configuration.
    private void loadFields(LootBoxes plugin) {
        defaultLang = plugin.getConfig().getString("language", "en");
        pluginName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("plugin_name", "LootBoxes"));
        debug = plugin.getConfig().getBoolean("debug", false);
        setLanguageManager();
    }

    // Saves the default configuration if it doesn't exist.
    private void saveDefaultConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            YamlConfiguration defaultConfig = new YamlConfiguration();
            defaultConfig.set("plugin_name", "LootBoxes");
            defaultConfig.set("language", "en");
            defaultConfig.set("debug", false);
            try {
                defaultConfig.save(configFile);
                Logger.warn(plugin, "Default configuration created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Sets the language manager, initializing or reloading languages.
    private void setLanguageManager() {
        if (this.langManager == null) {
            this.langManager = LangMessage.getInstance(this);
        } else {
            this.langManager.reloadLanguages();
        }
    }

    // Reloads the plugin's configuration.
    public boolean reload() {
        // Asynchronously reloads the plugin configuration.
    	SyncExecutor.runAsync(() -> {
            try {
                saveDefaultConfig();
                plugin.reloadConfig();
                loadFields(plugin);
                Logger.info(plugin, Translator.translateKyeWorld(instance, "configuration_reloaded", new String[]{defaultLang}));
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error(plugin, Translator.translateKyeWorld(instance, "err_reloading_configuration", new String[]{defaultLang}));
            }
        });
        return true;
    }

    @Override
    public MySQLConfig getMySQLConfig() {
        return new MySQLConfig("db4free.net", 3306, "LootBoxes", "root", "password", "prefix_", false, true, false);
    }

    public void setDatabaseManager() {
        TableRegistry registry = new TableRegistry(plugin);
        dbManager = new SQLiteDatabaseManager(this, registry);
    }

	@Override
	public DatabaseManager getDatabaseManager() {
		return dbManager;
	}
}	
	
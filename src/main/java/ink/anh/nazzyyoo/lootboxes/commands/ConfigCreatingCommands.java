package ink.anh.nazzyyoo.lootboxes.commands;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import ink.anh.api.items.ItemStackSerializer;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Sender;
import ink.anh.api.utils.SyncExecutor;
import ink.anh.nazzyyoo.lootboxes.LootBoxes;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTableManager;

import java.util.HashMap;
import java.io.File;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ConfigCreatingCommands extends Sender implements CommandExecutor {

	private final LootBoxes LootBoxes;
    private final Map<String, Map<String, Object>> lootTables;

    public ConfigCreatingCommands(LootBoxes LootBoxes) {
    	super(LootBoxes.getGlobalManager());
    	this.LootBoxes = LootBoxes;
        this.lootTables = new HashMap<>();
        loadLootTables();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	SyncExecutor.runAsync(() -> {
    		try {
    			if (args.length > 0) {
    	            switch (args[0].toLowerCase()) {
    	                
    	                case "create":
    	                    createLootTable(sender, args);
    	                    break;
    	                
    	                case "delete":
    	                    deleteLootTable(sender, args);
    	                    break;
    	                
    	                case "add":
    	                    addItemToLootTable(sender, args);
    	                    break;
    	                
    	                case "remove":
    	                    removeItemFromLootTable(sender, args);
    	                    break;
    	                
    	                case "tool":
    	                	giveItemMarker(sender, args);
    	                	break;
    	                
    	                case "reload":
    	                    reload(sender);
    	                    break;
    	            }
    	        }
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	});
        return false;
    }
    
    private int checkPlayerPermissions(CommandSender sender, String permission) {
        // Перевірка, чи команду виконує консоль
        if (sender instanceof ConsoleCommandSender) {
            return 0;
        }

        if (sender instanceof Player) {

            // Перевіряємо наявність дозволу у гравця
            if (sender.hasPermission(permission)) {
                return 1;
            }
        }

        return 2;
    }
    
    private boolean reload(CommandSender sender) {
    	int perm = checkPlayerPermissions(sender, Permissions.RELOAD);
	    if (perm != 0 && perm != 1) {
            return false;
	    }
        LootTableManager.getInstance().reloadLootTables();
        sendMessage(new MessageForFormatting("lootboxes_loottable_reload", new String[] {}), MessageType.NORMAL, sender);
        return true;
    }
    
    private void loadLootTables() {
        File file = new File(LootBoxes.getDataFolder(), "loottables.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("loot_tables")) {
            ConfigurationSection lootTablesSection = config.getConfigurationSection("loot_tables");
            for (String key : lootTablesSection.getKeys(false)) {
                Map<String, Object> lootTable = lootTablesSection.getConfigurationSection(key).getValues(false);
                lootTables.put(key, lootTable);
            }
        }
    }

    private void saveLootTables() {
        File file = new File(LootBoxes.getDataFolder(), "loottables.yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, Map<String, Object>> entry : lootTables.entrySet()) {
            config.set("loot_tables." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
            loadLootTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean createLootTable(CommandSender sender, String[] args) {
    	int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
	    if (perm != 0 && perm != 1) return false;
    	
	    if (args.length == 2) {
            String name = args[1];
            lootTables.put(name, new HashMap<>());
            saveLootTables();
            
            sendMessage(new MessageForFormatting("lootboxes_loottable_created " + name, new String[] {}), MessageType.NORMAL, sender);
            return true;
        }
        return false;
    }

    private boolean deleteLootTable(CommandSender sender, String[] args) {
    	int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
	    if (perm != 0 && perm != 1) return false;
    	
	    if (args.length == 2) {
            String name = args[1];
            lootTables.remove(name);
            saveLootTables();
            
            sendMessage(new MessageForFormatting("lootboxes_loottable_deleted " + name, new String[] {}), MessageType.NORMAL, sender);
            return true;
        }
        return false;
    }

    private boolean addItemToLootTable(CommandSender sender, String[] args) {
    	
    	int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
	    if (perm != 1) {
            return false;
	    }
    	
	    if (args.length != 6) {
	    	return false;
	    }
        
	    String tableName = args[1];
        Map<String, Object> lootTableData = lootTables.get(tableName);
        
        if (lootTableData != null) {
            Map<String, Object> itemData = new HashMap<>();
            String itemSaveMethod = args[2];

            ItemStack itemStack = null;
            
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (itemSaveMethod.equals("hand")) {
                    itemStack = player.getInventory().getItemInMainHand();
                } else if (itemSaveMethod.equals("offhand")) {
                    itemStack = player.getInventory().getItemInOffHand();
                }
            }

            if (itemStack != null) {
                String serializedItem = ItemStackSerializer.serializeItemStack(itemStack);
                
                Double chance = Double.parseDouble(args[3]);
                itemData.put("chance", chance);
                
                int min_quantity = Integer.parseInt(args[4]);
                itemData.put("min_quantity", min_quantity);
                
                int max_quantity = Integer.parseInt(args[5]);
                itemData.put("max_quantity", max_quantity);
                
                lootTableData.put(serializedItem, itemData);
                saveLootTables();
                
                String item_name = itemStack.getItemMeta().getDisplayName();
                if (item_name == null ) {
                	item_name = itemStack.getType().getItemTranslationKey();
                	if (item_name == null) {
                		item_name = itemSaveMethod;
                	}
                }
                sendMessage(new MessageForFormatting("lootboxes_loottable_item_added", new String[] {tableName, item_name }), MessageType.NORMAL, sender);
                return true;
            }
        } else {
        	sendMessage(new MessageForFormatting("lootboxes_loottable_doesnt_exist", new String[]  {tableName}), MessageType.NORMAL, sender);
        	return false;
        }
        return false;
    }

    private boolean removeItemFromLootTable(CommandSender sender, String[] args) {
        
    	int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
	    if (perm != 1) {
            return false;
	    }
    	
    	if (args.length != 3) {
    		return false;
    	}
        
    	
    	String tableName = args[1];
        Map<String, Object> lootTable = lootTables.get(tableName);
        
        
        if (lootTable != null) {
            ItemStack itemStack = null;
            String itemSaveMethod = args[2];
            
            if (sender instanceof Player) {
                Player player = (Player) sender;
                
                if (itemSaveMethod.equals("hand")) {
                    itemStack = player.getInventory().getItemInMainHand();
                } else if (itemSaveMethod.equals("offhand")) {
                    itemStack = player.getInventory().getItemInOffHand();
                }
            }

            if (itemStack != null) {
                String serializedItem = ItemStackSerializer.serializeItemStack(itemStack);
                lootTable.remove(serializedItem);
                saveLootTables();
                
                String item_name = itemStack.getItemMeta().getDisplayName();
                
                if (item_name == null ) {
                	item_name = itemStack.getType().getItemTranslationKey();
                	if (item_name == null) {
                		item_name = itemSaveMethod;
                	}
                }
                sendMessage(new MessageForFormatting("lootboxes_loottable_item_deleted", new String[] {tableName, item_name }), MessageType.NORMAL, sender);
                return true;
            }
        } else {
        	sendMessage(new MessageForFormatting("lootboxes_loottable_doesnt_exist", new String[]  {tableName}), MessageType.NORMAL, sender);
        	return false;
        }
        return false;
    }

    private boolean giveItemMarker(CommandSender sender, String[] args) {
    	
    	int perm = checkPlayerPermissions(sender, Permissions.LT_TOOL);
	    if (perm != 1) {
            return false;
	    }
	    
    	if (args.length != 3) {
    		return false;
    	}
        
    	String lootTable = args[1];
        String cooldown = args[2];

        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey(LootBoxes, "lootTable"), PersistentDataType.STRING, lootTable);
        meta.getPersistentDataContainer().set(new NamespacedKey(LootBoxes, "cooldownSeconds"), PersistentDataType.STRING, cooldown);

        item.setItemMeta(meta);

        if (sender instanceof Player) {	
            Player player = (Player) sender;
            player.getInventory().addItem(item);
            sendMessage(new MessageForFormatting("lootboxes_loottable_tool", new String[] {lootTable, cooldown }), MessageType.NORMAL, sender);
            return true;
        }
        return false;
    }
}
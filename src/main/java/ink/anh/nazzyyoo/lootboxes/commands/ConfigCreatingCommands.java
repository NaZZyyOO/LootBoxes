package ink.anh.nazzyyoo.lootboxes.commands;

import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Sender;
import ink.anh.api.utils.SyncExecutor;
import ink.anh.nazzyyoo.lootboxes.LootBoxes;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootItem;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTable;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTableManager;

import java.util.HashSet;
import java.util.Set;
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

    public ConfigCreatingCommands(LootBoxes LootBoxes) {
        super(LootBoxes.getGlobalManager());
        this.LootBoxes = LootBoxes;
        LootTableManager.getInstance().loadLootTables();
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
        sendMessage(new MessageForFormatting("lootboxes_loottable_reload", new String[]{}), MessageType.NORMAL, sender);
        return true;
    }

    private boolean createLootTable(CommandSender sender, String[] args) {
        int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
        if (perm != 0 && perm != 1) return false;

        if (args.length == 2) {
            String name = args[1];
            Set<LootItem> lootItems = new HashSet<>();
            LootTable lootTable = new LootTable(name, lootItems);

            LootTableManager.getInstance().addLootTable(lootTable);
            LootTableManager.getInstance().saveLootTable(lootTable);

            sendMessage(new MessageForFormatting("lootboxes_loottable_created " + name, new String[]{}), MessageType.NORMAL, sender);
            return true;
        }
        return false;
    }

    private boolean deleteLootTable(CommandSender sender, String[] args) {
        int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
        if (perm != 0 && perm != 1) return false;

        if (args.length == 2) {
            String name = args[1];
            LootTableManager.getInstance().removeLootTable(name);

            sendMessage(new MessageForFormatting("lootboxes_loottable_deleted " + name, new String[]{}), MessageType.NORMAL, sender);
            return true;
        }
        return false;
    }

    private boolean addItemToLootTable(CommandSender sender, String[] args) {
        int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
        if (perm != 0 && perm != 1) return false;

        if (args.length == 6) {
            String tableName = args[1];
            LootTable lootTable = LootTableManager.getInstance().getLootTable(tableName);
            
            if (lootTable == null) return false;
            
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

            double chance;
            int minQuantity;
            int maxQuantity;
            try {
                chance = Double.parseDouble(args[3]);
                minQuantity = Integer.parseInt(args[4]);
                maxQuantity = Integer.parseInt(args[5]);
            } catch (NumberFormatException e) {
                System.err.print("Numbers invalid format.");
                return false;
            }

            LootItem lootItem = new LootItem(itemStack, chance, minQuantity, maxQuantity);
            lootTable.getLootItems().add(lootItem);

            LootTableManager.getInstance().saveLootTable(lootTable);
            
            String item_name = itemStack.getItemMeta().getDisplayName();
            if (item_name == null) {
                item_name = itemStack.getType().getKey().toString();
                if (item_name == null) {
                    item_name = itemSaveMethod;
                }
            }
            sendMessage(new MessageForFormatting("lootboxes_loottable_item_added", new String[]{tableName, item_name}), MessageType.NORMAL, sender);
            return true;
        }
        return false;
    }

    private boolean removeItemFromLootTable(CommandSender sender, String[] args) {
        
    	int perm = checkPlayerPermissions(sender, Permissions.LT_EDITING);
	    if (perm != 0 && perm != 1) {
            return false;
	    }
    	
    	if (args.length == 3) {
    		String tableName = args[1];
        	LootTable lootTable = LootTableManager.getInstance().getLootTable(tableName);
            
            
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
                	
                	LootItem lootItem = new LootItem(itemStack, 0, 0, 0);
                	
                	if (lootTable.getLootItems().contains(lootItem)) {
                		lootTable.getLootItems().remove(lootItem);

                        LootTableManager.getInstance().saveLootTable(lootTable);

                        String item_name = itemStack.getItemMeta().getDisplayName();
                        if (item_name == null ) {
                        	item_name = itemStack.getType().getKey().getKey();
                        	if (item_name == null) {
                        		item_name = itemSaveMethod;
                        	}
                        }
                        sendMessage(new MessageForFormatting("lootboxes_loottable_item_deleted", new String[] {tableName, item_name }), MessageType.NORMAL, sender);
                        return true;
                	} else {
                		sendMessage(new MessageForFormatting("lootboxes_loottable_doesnt_contain_item", new String[] {tableName}), MessageType.NORMAL, sender);
                		return false;
                	}
                }
            } else {
            	sendMessage(new MessageForFormatting("lootboxes_loottable_doesnt_exist", new String[]  {tableName}), MessageType.NORMAL, sender);
            	return false;
            }
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

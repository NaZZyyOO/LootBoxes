package ink.anh.nazzyyoo.lootboxes.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ink.anh.api.utils.SyncExecutor;
import ink.anh.nazzyyoo.lootboxes.LootBoxes;
import ink.anh.nazzyyoo.lootboxes.gui.LootBoxHolder;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBox;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootItem;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTable;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTableManager;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LootBoxesDrop {
	
	// Мапа для збереження інформації який лутбокс був відкритий гравцем(для лутбокса типу true)
	private static Map<UUID, LootBox> playerLootBox = new ConcurrentHashMap<>();

    public static void openingLootBox(Player player, LootBox lootBox, Location loc) {
    	
    	if (lootBox == null || loc == null) {
    		LootBoxes.getInstance().getLogger().warning("LootBox or Location is null.");
            return;
        }
    	
    	String lootTableName = lootBox.getLootTableName();
    	LootTableManager tableManager = LootTableManager.getInstance();
    	
    	LootTable lootTable = tableManager.getLootTable(lootTableName);
    	// Якщо таблиці луту не існує то закінчити метод
    	if (lootTable == null) {
    		LootBoxes.getInstance().getLogger().warning("Loot table not found: " + lootTableName);
            return;
        }
    	// Асинхронно запускаємо обробку таблиці луту
    	SyncExecutor.runAsync(() -> {
    		boolean type = lootBox.getType();
    		
    		for (LootItem lootItem : lootTable.getLootItems()) {
        		double chance = lootItem.getChance();
                int minQuantity = lootItem.getMinQuantity();
                int maxQuantity = lootItem.getMaxQuantity();
                
                Random random = new Random();
                double randomNumber = random.nextDouble() * 100;
                // Якщо шанс справджується
                if (randomNumber <= chance) {
                	ItemStack itemStack = lootItem.getItem();
                	if (itemStack != null) {
                        
                		int quantity = random.nextInt(maxQuantity - minQuantity + 1) + minQuantity;
                		itemStack.setAmount(quantity);
                		
                		// Якщо тип лутбокса false(випадіння предметів на над лутбоксом)
                		if (type == false) {
                			SyncExecutor.runSync(() -> {
                                loc.getWorld().dropItem(loc, itemStack);
                    		});
                			
                	    // Якщо тип лутбокса true(відкривання кастомного інвентаря)
                		} else if (type == true) {
                			UUID playerId = player.getUniqueId();
                		    addPlayerLootBox(playerId, lootBox);
                			
                			SyncExecutor.runSync(() -> {
                				LootBoxHolder holder = new LootBoxHolder("LootBox");
                	            ItemStack[] items = new ItemStack[27];
                	            int slot;
                                do {
                                    slot = random.nextInt(27);
                                } while (items[slot] != null);
                                items[slot] = itemStack;
                                holder.addItems(items);
                                player.openInventory(holder.getInventory());
                			});
                		}

                    }
                }
                
        	}
    	});
    }
    // Методи для додавання, отримання та видалення інформації про відкритий лутбокс
    public static void addPlayerLootBox(UUID playerId, LootBox lootBox) {
    	playerLootBox.put(playerId, lootBox);
    }
    
    public static LootBox getPlayerLootBox(UUID playerId) {
    	return playerLootBox.get(playerId);
    }
    public static void removePlayerLootBox(UUID playerID) {
    	playerLootBox.remove(playerID);
    }
}

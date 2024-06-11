package ink.anh.nazzyyoo.lootboxes.utils;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import ink.anh.nazzyyoo.lootboxes.LootBoxes;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBox;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootItem;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTable;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootTableManager;

import java.util.Random;

public class LootBoxesDrop {

    public static void dropLootBoxContents(LootBox lootBox, Location loc) {
    	
    	if (lootBox == null || loc == null) {
    		LootBoxes.getInstance().getLogger().warning("LootBox or Location is null.");
            return;
        }
    	
    	String lootTableName = lootBox.getLootTableName();
    	LootTableManager tableManager = LootTableManager.getInstance();
    	
    	LootTable lootTable = tableManager.getLootTable(lootTableName);
    	
    	if (lootTable == null) {
    		LootBoxes.getInstance().getLogger().warning("Loot table not found: " + lootTableName);
            return;
        }
    	
    	for (LootItem lootItem : lootTable.getLootItems()) {
    		double chance = lootItem.getChance();
            int minQuantity = lootItem.getMinQuantity();
            int maxQuantity = lootItem.getMaxQuantity();
            
            Random random = new Random();
            double randomNumber = random.nextDouble() * 100;
            
            if (randomNumber <= chance) {
            	ItemStack itemStack = lootItem.getItem();
            	if (itemStack != null) {
                    
            		int quantity = random.nextInt(maxQuantity - minQuantity + 1) + minQuantity;
                    itemStack.setAmount(quantity);
                    loc.getWorld().dropItem(loc, itemStack);
                }
            }
            
    	}
    }
}

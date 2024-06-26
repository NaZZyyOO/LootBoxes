package ink.anh.nazzyyoo.lootboxes.utils;

import java.util.Random;

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

public class LootBoxesDrop {

    public static void openingLootBox(Player player, LootBox lootBox, Location loc) {
        if (lootBox == null || loc == null) {
            LootBoxes.getInstance().getLogger().warning("LootBox or Location is null.");
            return;
        }

        String lootTableName = lootBox.getLootTableName();
        LootTable lootTable = getLootTable(lootTableName);
        if (lootTable == null) return;

        SyncExecutor.runAsync(() -> {
        	boolean type = lootBox.getType();
            ItemStack[] items = new ItemStack[27];
            fillItemsArray(lootTable, loc, items, type);

            if (type == true) {
                openCustomInventory(player, lootBox, items);
            }
        });
    }

    private static LootTable getLootTable(String lootTableName) {
        LootTableManager tableManager = LootTableManager.getInstance();
        LootTable lootTable = tableManager.getLootTable(lootTableName);
        if (lootTable == null) {
            LootBoxes.getInstance().getLogger().warning("Loot table not found: " + lootTableName);
        }
        return lootTable;
    }

    private static void fillItemsArray(LootTable lootTable, Location loc, ItemStack[] items, boolean type) {
        Random random = new Random();
        for (LootItem lootItem : lootTable.getLootItems()) {
            if (random.nextDouble() * 100 <= lootItem.getChance()) {
                ItemStack itemStack = lootItem.getItem();
                if (itemStack != null) {
                    itemStack.setAmount(random.nextInt(lootItem.getMaxQuantity() - lootItem.getMinQuantity() + 1) + lootItem.getMinQuantity());
                    if (type == false) {
                    	SyncExecutor.runSync(() -> {
                            loc.getWorld().dropItem(loc, itemStack);
                         });
                    } else {
                        addItemToRandomSlot(items, itemStack);
                    }
                }
            }
        }
    }

    private static void addItemToRandomSlot(ItemStack[] items, ItemStack itemStack) {
        Random random = new Random();
        int slot;
        do {
            slot = random.nextInt(items.length);
        } while (items[slot] != null);
        items[slot] = itemStack;
    }

    private static void openCustomInventory(Player player, LootBox lootBox, ItemStack[] items) {
        LootBoxHolder holder = new LootBoxHolder("LootBox", lootBox.getLocation());
        holder.addItems(items);
        SyncExecutor.runSync(() -> {
        	
           player.openInventory(holder.getInventory());
        });
    }
}

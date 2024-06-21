package ink.anh.nazzyyoo.lootboxes.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class LootBoxHolder implements InventoryHolder {
	 
	private Inventory inventory;

	public LootBoxHolder(String name) {
		inventory = Bukkit.createInventory(this, 27, name);
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	public void addItems(ItemStack[] content) {
    	inventory.setContents(content);
    }

}

package ink.anh.nazzyyoo.lootboxes.gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class LootBoxHolder implements InventoryHolder {
	 
	private Inventory inventory;
	private Location loc;

	public LootBoxHolder(String name, Location loc) {
		inventory = Bukkit.createInventory(this, 27, name);
		this.loc = loc;
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public void addItems(ItemStack[] content) {
    	inventory.setContents(content);
    }

}

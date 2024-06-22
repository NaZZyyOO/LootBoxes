package ink.anh.nazzyyoo.lootboxes.gui;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class LootBoxGuiListener implements Listener {
	
	 @EventHandler
	 public void onInventoryClick(InventoryClickEvent event) {
		 
		 if (event.getInventory().getHolder() instanceof LootBoxHolder) {
			// Prevent adding items
			 
			 int clickedSlot = event.getRawSlot();
			 ItemStack item = event.getView().getItem(clickedSlot);
			 
			 if (item.getType() == Material.AIR) {
				 if (event.getCursor().getType() != Material.AIR) {
					 event.setCancelled(true); 
				 }
			 }
	     }
	 }

	 @EventHandler
	 public void onInventoryClose(InventoryCloseEvent event) {
		 
		 if (event.getInventory().getHolder() instanceof LootBoxHolder) {
			 
	         Inventory inventory = event.getInventory();
	         
	         Location locBox = ((LootBoxHolder) event.getInventory().getHolder()).getLoc();
	         
	         if (locBox != null) {
		         Location loc = new Location(locBox.getWorld(), locBox.getX(), locBox.getY() + 1, locBox.getZ());
		         for (ItemStack item : inventory.getContents()) {
		        	 
		             if (item != null) {
		                	
		                    loc.getWorld().dropItem(loc, item);
		             }
		         }
	         }
	     }
	 }
}
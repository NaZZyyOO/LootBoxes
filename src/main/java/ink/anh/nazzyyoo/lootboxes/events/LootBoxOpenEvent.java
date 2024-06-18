package ink.anh.nazzyyoo.lootboxes.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.MessageComponents;
import ink.anh.api.messages.Messenger;
import ink.anh.api.messages.Sender;
import ink.anh.api.utils.LangUtils;
import ink.anh.nazzyyoo.lootboxes.LootBoxes;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBox;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBoxManager;
import ink.anh.nazzyyoo.lootboxes.utils.LootBoxesDrop;
import ink.anh.nazzyyoo.lootboxes.utils.LootBoxCooldown;

public class LootBoxOpenEvent extends Sender implements Listener {
    
	private LootBoxes lootBoxes;

    public LootBoxOpenEvent(LootBoxes lootBoxes) {
    	super(lootBoxes.getGlobalManager());
    	this.lootBoxes = lootBoxes;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	LootBoxManager boxManager = LootBoxManager.getInstance(libraryManager);
    	
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
        	return;
        }
        
        Location loc = block.getLocation();
        
        if (loc == null || block.getType() == Material.AIR) {
            return;
        }

        LootBox lootBox = boxManager.getLootBox(loc);
        
        if (lootBox != null) {
    		
    		// Якщо в руках спец предмет, то зупинити цей код
    		ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta itemMeta = item.getItemMeta();
            
            if (itemMeta != null) {
                String loottable = itemMeta.getPersistentDataContainer().get(new NamespacedKey(lootBoxes, "lootTable"), PersistentDataType.STRING);
                if (loottable == null) return;
                
                String cooldownSeconds = itemMeta.getPersistentDataContainer().get(new NamespacedKey(lootBoxes, "cooldownSeconds"), PersistentDataType.STRING);
                if (cooldownSeconds != null) return;
            }
           
            event.setCancelled(true);
            
            if (LootBoxCooldown.CanBeLooted(player, lootBox) == true) {
        		
        		lootBox.addLootedPlayer(player.getUniqueId());
            	
                LootBoxesDrop.dropLootBoxContents(lootBox, loc);
                
                Sound sound = Sound.BLOCK_CHEST_OPEN;
                float volume = 1;
                float pitch = 0.2f;
                loc.getWorld().playSound(loc, sound, volume, pitch);
                
                return;
            
            } else {
                String message = Translator.translateKyeWorld(libraryManager, "lootboxes_player_cant_open_cd", LangUtils.getPlayerLanguage(player));
                MessageComponents messageComponents = MessageComponents.builder()
                        .content(message)
                        .hexColor("#FFD700")
                        .build();
                    
                Messenger.sendActionBar(LootBoxes.getInstance(), player, messageComponents, message);
                
                return;
            }
        } else {
        	return;
        }
    }
}
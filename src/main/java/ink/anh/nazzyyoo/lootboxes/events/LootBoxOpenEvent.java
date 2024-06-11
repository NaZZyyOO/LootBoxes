package ink.anh.nazzyyoo.lootboxes.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.SoundCategory;
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
	
	public LootBoxOpenEvent(LootBoxes LootBoxes) {
    	super(LootBoxes.getGlobalManager());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	LootBoxManager boxManager = LootBoxManager.getInstance(libraryManager);
    	
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        
        if (block == null || block.getType() == Material.AIR) {
            return;
        }

        Location loc = block.getLocation();
        LootBox lootBox = boxManager.getLootBox(loc);
        
        if (player.isSneaking() == false) {
        	
        	if (lootBox != null) {
        		
        		// Якщо в руках спец предмет, то зупинити цей код
        		ItemStack item = player.getInventory().getItemInMainHand();
                ItemMeta itemMeta = item.getItemMeta();
                
                if (itemMeta != null) {
                    String loottable = itemMeta.getPersistentDataContainer().get(new NamespacedKey(lootBoxes, "lootTable"), PersistentDataType.STRING);
                    String cooldownSeconds = itemMeta.getPersistentDataContainer().get(new NamespacedKey(lootBoxes, "cooldownSeconds"), PersistentDataType.STRING);
                    if (loottable != null && cooldownSeconds != null) {
                    	return;
                    }
                }
            	
                if (LootBoxCooldown.isCooldownExpired(player, loc) == true) {
            		event.setCancelled(true);
            		
            		lootBox.addLootedPlayer(player.getUniqueId());
                	
                    LootBoxesDrop.dropLootBoxContents(lootBox, loc);
                    
                    String sound = "BLOCK_CHEST_OPEN";
                    SoundCategory category = SoundCategory.MASTER;
                    float volume = 1;
                    float pitch = 0.2f;
                    loc.getWorld().playSound(loc, sound, category, volume, pitch);
                    
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
            } else return;
        }
    }
}
package ink.anh.nazzyyoo.lootboxes.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Sender;
import ink.anh.api.messages.MessageForFormatting;

import ink.anh.nazzyyoo.lootboxes.LootBoxes;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBox;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBoxManager;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.inventory.meta.ItemMeta;

public class LootBoxesMarking extends Sender implements Listener {

	private LootBoxes lootBoxes;

    public LootBoxesMarking(LootBoxes lootBoxes) {
    	super(lootBoxes.getGlobalManager());
    	this.lootBoxes = lootBoxes;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	LootBoxManager boxManager = LootBoxManager.getInstance(libraryManager);
    	
    	Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
        	Location loc = event.getClickedBlock().getLocation();
        	
        	if (player.isSneaking()) {
            	LootBox lootBox = boxManager.getLootBox(loc);
            	
            	if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            		
            		if (lootBox == null) {
                		handleRightClick(event, player, loc);
            		}
                
            	} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                	
                	if (lootBox != null) {
                		handleLeftClick(player, loc, lootBox);
                	}
                }
            } else {
            	if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            		event.setCancelled(true);
            	}
            }
        }
    }

    private void handleRightClick(PlayerInteractEvent event, Player player, Location loc) {
        LootBoxManager boxManager = LootBoxManager.getInstance(libraryManager);
        
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta != null) {
            String loottable = itemMeta.getPersistentDataContainer().get(new NamespacedKey(lootBoxes, "lootTable"), PersistentDataType.STRING);
            String cooldownSeconds = itemMeta.getPersistentDataContainer().get(new NamespacedKey(lootBoxes, "cooldownSeconds"), PersistentDataType.STRING);

            if (loottable != null && cooldownSeconds != null) {
                try {
                    int cooldown = Integer.parseInt(cooldownSeconds);
                    Map<UUID, Integer> lootedPlayers = new HashMap<>();
                    LootBox newLootBox = new LootBox(loc, loottable, cooldown, lootedPlayers);
                    boxManager.addLootBox(newLootBox);
                    sendMessage(new MessageForFormatting("lootboxes_box_marked", new String[]{}), MessageType.NORMAL, player);
                    
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GREEN, 1.4f);
                    player.spawnParticle(Particle.REDSTONE, loc, 60, 1f, 0f, 1f, dustOptions);
                    event.setCancelled(true);
                   
                    
                } catch (NumberFormatException e) {
                    sendMessage(new MessageForFormatting("lootboxes_invalid_cooldown", new String[]{}), MessageType.ERROR, player);
                    e.printStackTrace();
                }
            }
        }
    }


    private void handleLeftClick(Player player, Location loc, LootBox lootBox) {
    	LootBoxManager boxManager = LootBoxManager.getInstance(libraryManager);
    	
    	boxManager.removeLootBox(lootBox);
        sendMessage(new MessageForFormatting("lootboxes_box_dismarked", new String[]{}), MessageType.NORMAL, player);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.4f);
        player.spawnParticle(Particle.REDSTONE, loc, 60, 1f, 0f, 1f, dustOptions);
        
    }
}
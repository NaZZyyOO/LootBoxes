package ink.anh.nazzyyoo.lootboxes.utils;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import ink.anh.nazzyyoo.lootboxes.GlobalManager;
import ink.anh.nazzyyoo.lootboxes.LootBoxes;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBox;
import ink.anh.nazzyyoo.lootboxes.lootbox.LootBoxManager;

public class LootBoxCooldown {

    public static boolean isCooldownExpired(Player player, LootBox lootBox) {
        LootBoxManager lootBoxManager = LootBoxManager.getInstance(GlobalManager.getManager(LootBoxes.getInstance()));
        
        UUID playerUUID = player.getUniqueId();

        if (lootBox == null) {
        	System.err.println("LootBox is null.");
            return false;
        }

        if (lootBox.hasLooted(playerUUID)) {
            int currentTimeSecs = (int) (System.currentTimeMillis() / 1000);
            
            Map<UUID, Integer> lootedPlayers = lootBox.getLootedPlayers();
            int lootedTime = lootedPlayers.get(playerUUID);
            int cooldownSeconds = lootBox.getCooldown();
            int lootTime = lootedTime + cooldownSeconds;
            
            System.err.println(lootTime + lootedTime);
            
            if (currentTimeSecs >= lootTime) {
            	System.err.println(currentTimeSecs + "more than" + lootTime );
            	lootBox.getLootedPlayers().remove(playerUUID);
                lootBoxManager.updateLootBox(lootBox);
                return true;
            }
            return false;
        }

        return false;
    }
}
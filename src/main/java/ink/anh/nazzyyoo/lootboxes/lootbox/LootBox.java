package ink.anh.nazzyyoo.lootboxes.lootbox;

import org.bukkit.Location;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class LootBox {
	
	private Location location;
    private String lootTableName;
    private boolean type;
    private Map<UUID, Integer> lootedPlayers;
    private int cooldown;

    public LootBox(Location location, String lootTableName, boolean type ,int cooldown, Map<UUID, Integer> lootedPlayers) {
        this.location = location;
        this.lootTableName = lootTableName;
        this.type = type;
        this.lootedPlayers = lootedPlayers;
        this.cooldown = cooldown;
    }

    public Location getLocation() {
        return location;
    }

    public String getLootTableName() {
        return lootTableName;
    }
    public boolean getType() {
    	return type;
    }

    public Map<UUID, Integer> getLootedPlayers() {
        return lootedPlayers;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean hasLooted(UUID playerUUID) {
        if (lootedPlayers.containsKey(playerUUID) == true) {
        	return true;
        } else {
        	return false;
        }
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public void addLootedPlayer(UUID playerUUID) {
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        lootedPlayers.put(playerUUID, currentTime);
    }
    public void setType(boolean type) {
    	this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LootBox lootBox = (LootBox) o;
        return Objects.equals(location, lootBox.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }
}

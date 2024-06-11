package ink.anh.nazzyyoo.lootboxes.lootbox;

import ink.anh.api.LibraryManager;
import org.bukkit.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LootBoxManager {

    private static LootBoxManager instance;
    private Map<Integer, LootBox> lootBoxMap;
    private LootBoxTable lootBoxTable;

    private LootBoxManager(LibraryManager manager) {
        lootBoxMap = new ConcurrentHashMap<>();
        lootBoxTable = new LootBoxTable(manager);
        // Завантаження всіх лутбоксів з бд в хеш мапу
        lootBoxMap.putAll(lootBoxTable.getAllLootBoxes());
    }

    public static synchronized LootBoxManager getInstance(LibraryManager manager) {
        if (instance == null) {
            instance = new LootBoxManager(manager);
        }
        return instance;
    }

    public void addLootBox(LootBox lootBox) {
        lootBoxMap.put(lootBox.getLocation().hashCode(), lootBox);
        lootBoxTable.insert(lootBox);
    }

    public LootBox getLootBox(int hashCode) {
        return lootBoxMap.get(hashCode);
    }

    public LootBox getLootBox(Location location) {
        int hashCode = location.hashCode();
        return getLootBox(hashCode);
    }

    public void removeLootBox(int hashCode) {
        LootBox lootBox = getLootBox(hashCode);
        
        if (lootBox != null) {
            lootBoxTable.delete(lootBox);
            lootBoxMap.remove(hashCode);
        
        } else {
            System.err.println("LootBox with hashCode " + hashCode + " not found.");
        }
    }

    public void removeLootBox(LootBox lootBox) {
        int hashCode = lootBox.getLocation().hashCode();
        removeLootBox(hashCode);
    }

    public void updateLootBox(LootBox lootBox) {
        lootBoxMap.put(lootBox.getLocation().hashCode(), lootBox);
        lootBoxTable.update(lootBox);
    }
}

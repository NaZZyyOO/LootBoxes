package ink.anh.nazzyyoo.lootboxes.lootbox;

import java.util.HashSet;
import java.util.Set;

public class LootTable {
    
    private String lootTableName;
    private Set<LootItem> lootItems = new HashSet<>();

    public LootTable(String lootTableName, Set<LootItem> lootItems) {
        this.lootTableName = lootTableName;
        this.lootItems = lootItems;
    }

    public String getLootTableName() {
        return lootTableName;
    }

    public void setLootTableName(String lootTableName) {
        this.lootTableName = lootTableName;
    }

    public Set<LootItem> getLootItems() {
        return lootItems;
    }

    public void setLootItems(Set<LootItem> lootItems) {
        this.lootItems = lootItems;
    }
}
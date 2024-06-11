package ink.anh.nazzyyoo.lootboxes.lootbox;

public class LootTable {
    
	private String lootTableName;
    private LootItem[] lootItems;

    public LootTable(String lootTableName, LootItem[] lootItems) {
        this.lootTableName = lootTableName;
        this.lootItems = lootItems;
    }

    public String getLootTableName() {
        return lootTableName;
    }

    public void setLootTableName(String lootTableName) {
        this.lootTableName = lootTableName;
    }

    public LootItem[] getLootItems() {
        return lootItems;
    }

    public void setLootItems(LootItem[] lootItems) {
        this.lootItems = lootItems;
    }
}

package ink.anh.nazzyyoo.lootboxes.lootbox;

import org.bukkit.inventory.ItemStack;

public class LootItem {
    
	private ItemStack item;
    private double chance;
    private int minQuantity;
    private int maxQuantity;

    public LootItem(ItemStack item, double chance, int minQuantity, int maxQuantity) {
        this.item = item;
        this.chance = chance;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}

package ink.anh.nazzyyoo.lootboxes.lootbox;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LootItem other = (LootItem) obj;
        return Objects.equals(item, other.item);
    }
}
package de.eiscoding.content;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BackpackItemInventory implements Inventory {
    private final ItemStack stack;
    private final BackpackType type;
    private final DefaultedList<ItemStack> items;

    public BackpackItemInventory(ItemStack stack, BackpackType type) {
        this.stack = stack;
        this.type = type;
        this.items = DefaultedList.ofSize(type.getSlotCount(), ItemStack.EMPTY);
        read();
    }

    private void read() {
        DefaultedList<ItemStack> stored = BackpackItem.readItems(stack, type);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, stored.get(i));
        }
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = Inventories.splitStack(items, slot, amount);
        if (!removed.isEmpty()) {
            markDirty();
        }
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removed = Inventories.removeStack(items, slot);
        if (!removed.isEmpty()) {
            markDirty();
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public void markDirty() {
        save();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
        markDirty();
    }

    public void save() {
        BackpackItem.writeItems(stack, items);
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }
}

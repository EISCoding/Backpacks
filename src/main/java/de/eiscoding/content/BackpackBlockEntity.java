package de.eiscoding.content;

import de.eiscoding.registry.BackpackRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BackpackBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {
    private final BackpackType type;
    private final DefaultedList<ItemStack> items;

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(BackpackRegistries.BACKPACK_BLOCK_ENTITY, pos, state);
        this.type = ((BackpackBlock) state.getBlock()).getBackpackType();
        this.items = DefaultedList.ofSize(type.getSlotCount(), ItemStack.EMPTY);
    }

    public BackpackType getType() {
        return type;
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
        ItemStack stack = Inventories.splitStack(items, slot, amount);
        if (!stack.isEmpty()) {
            markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = Inventories.removeStack(items, slot);
        if (!stack.isEmpty()) {
            markDirty();
        }
        return stack;
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
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null) {
            return false;
        }
        if (world.getBlockEntity(pos) != this) {
            return false;
        }
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        items.clear();
        markDirty();
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, items, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, items, registryLookup);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.backpacks." + type.getName());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BackpackScreenHandler(syncId, playerInventory, this, type, this::markDirty);
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void readItemsFromStack(ItemStack stack) {
        DefaultedList<ItemStack> stored = BackpackItem.readItems(stack, type);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, stored.get(i));
        }
        markDirty();
    }

    public void writeItemsToStack(ItemStack stack) {
        BackpackItem.writeItems(stack, items);
    }
}

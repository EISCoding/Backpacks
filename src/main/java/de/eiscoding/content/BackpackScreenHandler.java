package de.eiscoding.content;

import de.eiscoding.registry.BackpackRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BackpackScreenHandler extends ScreenHandler {
    private Inventory inventory;
    private BackpackType type;
    private int columns;
    private int rows;
    private Runnable saveCallback;

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        BackpackType type = buf.readEnumConstant(BackpackType.class);
        this(syncId, playerInventory, new SimpleInventory(type.getSlotCount()), type, () -> {});
    }

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, BackpackType type, Runnable saveCallback) {
        super(BackpackRegistries.BACKPACK_SCREEN_HANDLER, syncId);
        init(playerInventory, inventory, type, saveCallback);
    }

    private void init(PlayerInventory playerInventory, Inventory inventory, BackpackType type, Runnable saveCallback) {
        this.inventory = inventory;
        this.type = type;
        this.columns = type.getColumns();
        this.rows = type.getRows();
        this.saveCallback = saveCallback;

        inventory.onOpen(playerInventory.player);
        setupSlots(playerInventory, inventory);
    }

    private void setupSlots(PlayerInventory playerInventory, Inventory inventory) {
        int startX = 8 + Math.max(0, (9 - columns) * 18 / 2);
        int startY = 18;

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (index >= inventory.size()) {
                    break;
                }
                this.addSlot(new Slot(inventory, index, startX + column * 18, startY + row * 18));
                index++;
            }
        }

        int playerInventoryY = startY + rows * 18 + 14;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, playerInventoryY + row * 18));
            }
        }

        int hotbarY = playerInventoryY + 58;
        for (int slot = 0; slot < 9; slot++) {
            this.addSlot(new Slot(playerInventory, slot, 8 + slot * 18, hotbarY));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            newStack = stack.copy();
            int backpackSlotCount = this.inventory.size();
            if (index < backpackSlotCount) {
                if (!this.insertItem(stack, backpackSlotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack, 0, backpackSlotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
        saveCallback.run();
    }

    public BackpackType getBackpackType() {
        return type;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }
}

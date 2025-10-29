package de.eiscoding.registry;

import de.eiscoding.Backpacks;
import de.eiscoding.content.BackpackBlock;
import de.eiscoding.content.BackpackBlockEntity;
import de.eiscoding.content.BackpackItem;
import de.eiscoding.content.BackpackScreenHandler;
import de.eiscoding.content.BackpackType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

public final class BackpackRegistries {
    public static final Map<BackpackType, BackpackItem> BACKPACK_ITEMS = new EnumMap<>(BackpackType.class);
    public static final Map<BackpackType, BackpackBlock> BACKPACK_BLOCKS = new EnumMap<>(BackpackType.class);
    public static final Map<BackpackType, BlockItem> BACKPACK_BLOCK_ITEMS = new EnumMap<>(BackpackType.class);

    public static BlockEntityType<BackpackBlockEntity> BACKPACK_BLOCK_ENTITY;
    public static ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER;

    private BackpackRegistries() {
    }

    public static void registerAll() {
        for (BackpackType type : BackpackType.values()) {
            BackpackBlock block = new BackpackBlock(type);
            BackpackItem item = new BackpackItem(type, block);
            BlockItem blockItem = new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey());

            Identifier itemId = Backpacks.id(type.getName() + "_backpack");
            Identifier blockId = Backpacks.id(type.getName() + "_backpack_block");

            BACKPACK_BLOCKS.put(type, Registry.register(Registries.BLOCK, blockId, block));
            BACKPACK_ITEMS.put(type, Registry.register(Registries.ITEM, itemId, item));
            BACKPACK_BLOCK_ITEMS.put(type, Registry.register(Registries.ITEM, blockId, blockItem));
        }

        Block[] blocks = BACKPACK_BLOCKS.values().toArray(new Block[0]);
        BACKPACK_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Backpacks.id("backpack"),
                FabricBlockEntityTypeBuilder.create(BackpackBlockEntity::new, blocks).build());

        BACKPACK_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER,
                Backpacks.id("backpack"),
                new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) ->
                        new BackpackScreenHandler(syncId, playerInventory, buf)));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->
                BACKPACK_ITEMS.values().forEach(item -> entries.add(item.getDefaultStack())));
    }
}

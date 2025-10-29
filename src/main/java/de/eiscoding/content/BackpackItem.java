package de.eiscoding.content;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

public class BackpackItem extends ArmorItem {
    private final BackpackType type;
    private final BackpackBlock block;

    public BackpackItem(BackpackType type, BackpackBlock block) {
        super(new BackpackArmorMaterial(type), Type.CHESTPLATE, new Settings().maxCount(1));
        this.type = type;
        this.block = block;
    }

    public BackpackType getType() {
        return type;
    }

    public BackpackBlock getBlock() {
        return block;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.shouldCancelInteraction()) {
            ActionResult result = placeBackpack(world, user, hand);
            return new TypedActionResult<>(result, stack);
        }

        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            openScreen(serverPlayer, stack);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        return TypedActionResult.consume(stack);
    }

    private ActionResult placeBackpack(World world, PlayerEntity player, Hand hand) {
        HitResult hitResult = raycast(world, player, RaycastContext.FluidHandling.NONE);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            return ActionResult.FAIL;
        }

        ItemStack stack = player.getStackInHand(hand);
        ItemPlacementContext context = new ItemPlacementContext(world, player, hand, stack, blockHitResult);
        if (!context.canPlace()) {
            return ActionResult.FAIL;
        }

        BlockPos pos = context.getBlockPos();
        BlockState state = block.getPlacementState(context);
        if (state == null) {
            return ActionResult.FAIL;
        }

        if (!state.canPlaceAt(world, pos)) {
            return ActionResult.FAIL;
        }

        if (!world.setBlockState(pos, state, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD)) {
            return ActionResult.FAIL;
        }

        block.onPlaced(world, pos, state, player, stack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BackpackBlockEntity backpackEntity) {
            backpackEntity.readItemsFromStack(stack);
        }

        world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, Emitter.of(player, state));
        world.playSound(null, pos, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);

        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return ActionResult.SUCCESS;
    }

    private void openScreen(ServerPlayerEntity player, ItemStack stack) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity playerEntity) {
                BackpackItemInventory backpackInventory = new BackpackItemInventory(stack, type);
                return new BackpackScreenHandler(syncId, inventory, backpackInventory, type, backpackInventory::save);
            }

            @Override
            public Text getDisplayName() {
                return stack.getName();
            }

            @Override
            public PacketByteBuf getScreenOpeningData(ServerPlayerEntity serverPlayer) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeEnumConstant(type);
                return buf;
            }
        });
    }

    public static DefaultedList<ItemStack> readItems(ItemStack stack, BackpackType type) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(type.getSlotCount(), ItemStack.EMPTY);
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            container.copyTo(list);
        }
        return list;
    }

    public static void writeItems(ItemStack stack, DefaultedList<ItemStack> items) {
        boolean empty = true;
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                empty = false;
                break;
            }
        }

        if (empty) {
            stack.remove(DataComponentTypes.CONTAINER);
        } else {
            stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(items));
        }
    }

}

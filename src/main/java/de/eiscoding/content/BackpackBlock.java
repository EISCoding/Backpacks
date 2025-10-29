package de.eiscoding.content;

import de.eiscoding.registry.BackpackRegistries;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import org.jetbrains.annotations.Nullable;

public class BackpackBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.createCuboidShape(1, 0, 1, 15, 12, 15);

    private final BackpackType type;

    public BackpackBlock(BackpackType type) {
        super(AbstractBlock.Settings.copy(Blocks.CHEST));
        this.type = type;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    public BackpackType getBackpackType() {
        return type;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BackpackBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof BackpackBlockEntity backpack && stack.contains(DataComponentTypes.CONTAINER)) {
            backpack.readItemsFromStack(stack);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (moved) {
            super.onStateReplaced(state, world, pos, moved);
            return;
        }

        BlockState current = world.getBlockState(pos);
        if (current.isOf(this)) {
            return;
        }

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof BackpackBlockEntity backpack) {
            ItemScatterer.spawn(world, pos, backpack.getItems());
            world.updateComparators(pos, this);
        }

        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof BackpackBlockEntity backpack)) {
            return ActionResult.PASS;
        }

        if (player.shouldCancelInteraction()) {
            if (!world.isClient) {
                ItemStack stack = new ItemStack(BackpackRegistries.BACKPACK_ITEMS.get(type));
                backpack.writeItemsToStack(stack);
                backpack.clear();
                if (!player.getInventory().insertStack(stack)) {
                    player.dropItem(stack, false);
                }
                world.removeBlock(pos, false);
                world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, Emitter.of(player, state));
            }
            return ActionResult.SUCCESS;
        }

        if (!world.isClient) {
            player.openHandledScreen(backpack);
        }
        return ActionResult.SUCCESS;
    }
}

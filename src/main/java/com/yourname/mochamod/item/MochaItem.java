package com.yourname.mochamod.item;

import com.yourname.mochamod.entity.MochaEntity;
import com.yourname.mochamod.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * A craftable item that spawns a {@link MochaEntity} when right-clicked on the ground.
 * Intentionally not a spawn egg.
 */
public class MochaItem extends Item {
    public MochaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        Direction face = context.getClickedFace();

        BlockPos spawnPos = level.getBlockState(clickedPos).getCollisionShape(level, clickedPos).isEmpty()
                ? clickedPos
                : clickedPos.relative(face);

        MochaEntity mocha = ModEntities.MOCHA.get().spawn(
                serverLevel,
                stack,
                context.getPlayer(),
                spawnPos,
                MobSpawnType.SPAWN_EGG,
                true,
                !clickedPos.equals(spawnPos) && face == Direction.UP);

        if (mocha != null) {
            stack.shrink(1);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, spawnPos);
        }

        return InteractionResult.CONSUME;
    }
}

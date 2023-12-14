package com.mrbysco.mimicworld.block;

import com.mrbysco.mimicworld.MimicWorldMod;
import com.mrbysco.mimicworld.util.MimicTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MimicPortalBlock extends Block {
	protected static final VoxelShape SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 12.0D, 16.0D);

	public MimicPortalBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level instanceof ServerLevel && entity.canChangeDimensions() &&
				Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move((double) (-pos.getX()), (double) (-pos.getY()), (double) (-pos.getZ()))),
						state.getShape(level, pos), BooleanOp.AND)) {
			ResourceKey<Level> resourcekey = level.dimension() == MimicWorldMod.MIMIC_WORLD_KEY ? Level.OVERWORLD : MimicWorldMod.MIMIC_WORLD_KEY;
			ServerLevel serverLevel = ((ServerLevel) level).getServer().getLevel(resourcekey);
			if (serverLevel == null)
				return;

			CompoundTag persistentData = entity.getPersistentData();
			if (!persistentData.contains("MimicWorldPortalCooldown")) {
				persistentData.putLong("MimicWorldPortalCooldown", level.getGameTime());
				entity.changeDimension(serverLevel, new MimicTeleporter());
			} else {
				long cooldown = persistentData.getLong("MimicWorldPortalCooldown");
				if (level.getGameTime() - cooldown > 80) {
					persistentData.remove("MimicWorldPortalCooldown");
				}
			}
		}

	}

	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
		double d0 = (double) pos.getX() + randomSource.nextDouble();
		double d1 = (double) pos.getY() + 0.8D;
		double d2 = (double) pos.getZ() + randomSource.nextDouble();
		level.addParticle(ParticleTypes.WARPED_SPORE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	public boolean canBeReplaced(BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean isPossibleToRespawnInThis(BlockState state) {
		return false;
	}
}
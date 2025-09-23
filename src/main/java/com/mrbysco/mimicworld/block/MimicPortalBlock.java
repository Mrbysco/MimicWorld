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
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class MimicPortalBlock extends Block {
	protected static final VoxelShape SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 12.0D, 16.0D);

	public MimicPortalBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@NotNull
	@Override
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity, @NotNull InsideBlockEffectApplier effectApplier) {
		if (level instanceof ServerLevel &&
				Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())),
						state.getShape(level, pos), BooleanOp.AND)) {
			ResourceKey<Level> resourcekey = level.dimension() == MimicWorldMod.MIMIC_WORLD_KEY ? Level.OVERWORLD : MimicWorldMod.MIMIC_WORLD_KEY;
			ServerLevel serverLevel = ((ServerLevel) level).getServer().getLevel(resourcekey);
			if (serverLevel == null)
				return;

			if (!entity.canTeleport(level, serverLevel)) return;
			CompoundTag persistentData = entity.getPersistentData();
			if (!persistentData.contains("MimicWorldPortalCooldown")) {
				persistentData.putLong("MimicWorldPortalCooldown", level.getGameTime());
				entity.teleport(MimicTeleporter.getPortalInfo(entity, serverLevel));
			} else {
				long cooldown = persistentData.getLongOr("MimicWorldPortalCooldown", 0);
				if (level.getGameTime() - cooldown > 80) {
					persistentData.remove("MimicWorldPortalCooldown");
				}
			}
		}

	}

	@Override
	public void animateTick(@NotNull BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
		double d0 = (double) pos.getX() + randomSource.nextDouble();
		double d1 = (double) pos.getY() + 0.8D;
		double d2 = (double) pos.getZ() + randomSource.nextDouble();
		level.addParticle(ParticleTypes.WARPED_SPORE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	@NotNull
	@Override
	public ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData, @NotNull Player player) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canBeReplaced(@NotNull BlockState state, @NotNull Fluid fluid) {
		return false;
	}

	@Override
	public boolean isPossibleToRespawnInThis(@NotNull BlockState state) {
		return false;
	}
}
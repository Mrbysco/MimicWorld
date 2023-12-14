package com.mrbysco.mimicworld.mixin;

import com.mrbysco.mimicworld.util.PortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkShriekerBlockEntity.class)
public class SculkShriekerBlockEntityMixin extends BlockEntity {

	public SculkShriekerBlockEntityMixin(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
		super(entityType, pos, state);
	}

	@Inject(method = "shriek(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)V",
			at = @At(value = "HEAD"))
	public void shriek(ServerLevel level, @Nullable Entity entity, CallbackInfo ci) {
		BlockState blockstate = this.getBlockState();
		if (!blockstate.getValue(SculkShriekerBlock.CAN_SUMMON)) {
			boolean validPortal = PortalChecker.checkPortal(level, this.getBlockPos());
			if (validPortal) {
				PortalChecker.activatePortal(level, this.getBlockPos());
			}
		}
	}
}

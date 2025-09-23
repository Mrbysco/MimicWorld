package com.mrbysco.mimicworld.handler;

import com.mrbysco.mimicworld.data.PortalCache;
import com.mrbysco.mimicworld.registry.MimicRegistry;
import com.mrbysco.mimicworld.util.PortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class PortalHandler {

	@SubscribeEvent
	public void onGameEvent(VanillaGameEvent event) {
		if (event.getVanillaEvent() == GameEvent.SHRIEK && event.getLevel() instanceof ServerLevel serverLevel) {
			BlockPos pos = BlockPos.containing(event.getEventPosition());
			BlockState blockstate = serverLevel.getBlockState(pos);
			if (blockstate.is(Blocks.SCULK_SHRIEKER) && !blockstate.getValue(SculkShriekerBlock.CAN_SUMMON)) {
				boolean validPortal = PortalChecker.checkPortal(serverLevel, pos);
				if (validPortal) {
					PortalChecker.activatePortal(serverLevel, pos);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBreak(BlockEvent.BreakEvent event) {
		LevelAccessor level = event.getLevel();
		BlockPos blockpos = event.getPos();
		if (level.isClientSide()) return; //Don't run on client

		ServerLevel serverLevel = (ServerLevel) level;
		if (event.getState().is(Blocks.SCULK_SHRIEKER) && level.getBlockState(blockpos.above()).is(MimicRegistry.MIMIC_PORTAL.get())) {
			List<BlockPos> centerList = BlockPos.betweenClosedStream(
							blockpos.above().north().west(),
							blockpos.above().south().east())
					.map(BlockPos::immutable).toList();

			for (BlockPos center : centerList) {
				if (level.getBlockState(center).is(MimicRegistry.MIMIC_PORTAL.get()))
					level.removeBlock(center, false);
			}

			PortalCache.get(serverLevel).removePortal((GlobalPos.of(serverLevel.dimension(), blockpos)));
		}

		if (event.getState().is(Blocks.SCULK) || event.getState().is(MimicRegistry.MIMIC_PORTAL.get())) {
			List<BlockPos> centerList = BlockPos.betweenClosedStream(
							blockpos.north().west(),
							blockpos.south().east())
					.map(BlockPos::immutable).toList();

			boolean partOfPortal = false;
			for (BlockPos center : centerList) {
				if (level.getBlockState(center).is(MimicRegistry.MIMIC_PORTAL.get())) {
					partOfPortal = true;
					break;
				}
			}

			if (partOfPortal) {
				List<BlockPos> corners = BlockPos.betweenClosedStream(
								blockpos.north(4).west(4),
								blockpos.south(4).east(4))
						.map(BlockPos::immutable).toList();

				for (BlockPos corner : corners) {
					if (level.getBlockState(corner).is(MimicRegistry.MIMIC_PORTAL.get())) {
						level.playSound(null, corner, Blocks.NETHER_PORTAL.getSoundType(level.getBlockState(corner), level, corner, null).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
						level.removeBlock(corner, false);
					}
				}
				PortalCache.get(serverLevel).removeNearestPortal(new GlobalPos(serverLevel.dimension(), blockpos.below()));
			}

		}
	}
}

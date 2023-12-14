package com.mrbysco.mimicworld.util;

import com.mrbysco.mimicworld.data.PortalCache;
import com.mrbysco.mimicworld.registry.MimicRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class PortalChecker {
	public static boolean checkPortal(ServerLevel level, BlockPos blockpos) {
		//Check above 3x3x1 flat area for air
		List<BlockPos> centerList = BlockPos.betweenClosedStream(
						blockpos.above().north().west(),
						blockpos.above().south().east())
				.map(BlockPos::immutable).toList();

		List<BlockPos> corners = new ArrayList<>(BlockPos.betweenClosedStream(
						blockpos.above().north(2).west(2),
						blockpos.above().south(2).east(2))
				.map(BlockPos::immutable).toList());
		corners.removeAll(centerList);

		boolean airCorrect = true;
		for (BlockPos center : centerList) {
			if (!level.getBlockState(center).isAir()) {
				airCorrect = false;
				break;
			}
		}
		if (!airCorrect) return false;

		boolean frameCorrect = true;
		for (BlockPos corner : corners) {
			if (!level.getBlockState(corner).is(Blocks.SCULK)) {
				frameCorrect = false;
				break;
			}
		}
		if (!frameCorrect) return false;

		return true;
	}

	public static void activatePortal(ServerLevel level, BlockPos blockpos) {
		List<BlockPos> centerList = BlockPos.betweenClosedStream(
						blockpos.above().north().west(),
						blockpos.above().south().east())
				.map(BlockPos::immutable).toList();

		for (BlockPos center : centerList) {
			level.setBlock(center, MimicRegistry.MIMIC_PORTAL.get().defaultBlockState(), 3);
			level.playSound(null, center, Blocks.NETHER_PORTAL.getSoundType(level.getBlockState(center), level, center, null).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}

		PortalCache.get(level).addPortal(level.dimension().location(), blockpos);
	}

	public static void placePortal(ServerLevel level, BlockPos blockpos) {
		level.setBlock(blockpos, Blocks.SCULK_SHRIEKER.defaultBlockState(), 3);

		List<BlockPos> centerList = BlockPos.betweenClosedStream(
						blockpos.above().north().west(),
						blockpos.above().south().east())
				.map(BlockPos::immutable).toList();

		for (BlockPos center : centerList) {
			level.removeBlock(center, false);
		}

		List<BlockPos> corners = new ArrayList<>(BlockPos.betweenClosedStream(
						blockpos.above().north(2).west(2),
						blockpos.above().south(2).east(2))
				.map(BlockPos::immutable).toList());
		corners.removeAll(centerList);

		for (BlockPos corner : corners) {
			level.setBlock(corner, Blocks.SCULK.defaultBlockState(), 3);
		}

		PortalCache.get(level).addPortal(level.dimension().location(), blockpos);
	}
}

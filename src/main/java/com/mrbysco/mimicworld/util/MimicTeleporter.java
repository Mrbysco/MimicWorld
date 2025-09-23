package com.mrbysco.mimicworld.util;

import com.mrbysco.mimicworld.data.PortalCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MimicTeleporter {

	public static TeleportTransition getPortalInfo(Entity entity, ServerLevel destWorld) {
		PortalCache cache = PortalCache.get(destWorld);
		BlockPos originalPos = entity.blockPosition();

		//Validate nearest portals
		cache.validateNearestPortals(destWorld, originalPos);
		List<BlockPos> portalList = cache.getPortalPositions(destWorld.dimension()).stream().filter(pos -> pos.distManhattan(originalPos) < 16).toList();
		if (portalList.isEmpty()) {
			PortalChecker.placePortal(destWorld, entity.blockPosition().below());
		}

		// No spawn position or isn't valid, so loop around location
		for (var checkPos : BlockPos.spiralAround(originalPos, 16, Direction.EAST, Direction.SOUTH)) {
			// Load chunk to actually check the location
			destWorld.getChunk(checkPos);

			// Since we are checking going down, we want to verify the player is on the floor
			// Check the player position afterward
			if (!destWorld.getBlockState(checkPos.immutable().relative(Direction.DOWN)).isSolid()
					|| !isPositionSafe(entity, destWorld, checkPos, destWorld.getMinY())
			) continue;

			// All positions the entity is in is safe, so spawn in that location
			return new TeleportTransition(destWorld, new Vec3(checkPos.getX() + 0.5, checkPos.getY(), checkPos.getZ() + 0.5), Vec3.ZERO, entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING);
		}

		return new TeleportTransition(destWorld, entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING);
	}

	private static boolean isPositionSafe(Entity entity, ServerLevel destWorld, BlockPos checkPos, int minY) {
		var halfWidth = entity.getBbWidth() / 2;
		// We construct the position based on the entity radius
		// We could use the AABB method; however we want to account fo edge cases where the entity is touching a corner with
		// the box, causing the safety check to fail and change the spawn position.
		// This is also why we round to the higher or lower value
		for (var entityBoxPos : BlockPos.betweenClosed(
				Math.round(checkPos.getX() - halfWidth),
				checkPos.getY(),
				Math.round(checkPos.getZ() - halfWidth),
				Math.round(checkPos.getX() + halfWidth),
				Math.round(checkPos.getY() + entity.getBbHeight()),
				Math.round(checkPos.getZ() + halfWidth)
		)) {
			// If a safe position isn't found in the entity is in, move to next spot to check

			// Check if position is within bounds or that the position's min build height is higher than the spawning position
			if (!destWorld.getWorldBorder().isWithinBounds(entityBoxPos)) return false;
			// Get block state and check if it is possible to respawn
			BlockState entityBoxState = destWorld.getBlockState(entityBoxPos);
			return entityBoxState.getBlock().isPossibleToRespawnInThis(entityBoxState);
		}

		// If nothing fails, it is a safe location
		return true;
	}
}

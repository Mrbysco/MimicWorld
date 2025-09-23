package com.mrbysco.mimicworld.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.mimicworld.MimicWorldMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.List;

public class PortalCache extends SavedData {
	private static final String DATA_NAME = MimicWorldMod.MOD_ID + "_portal_data";
	private final List<GlobalPos> portalPositions = new ArrayList<>();

	public static final Codec<PortalCache> CODEC = RecordCodecBuilder.create(inst -> inst.group(
					GlobalPos.CODEC.listOf().fieldOf("portalPositions").forGetter(data -> data.portalPositions))
			.apply(inst, PortalCache::new));

	public PortalCache(List<GlobalPos> positions) {
		this.portalPositions.clear();
		this.portalPositions.addAll(positions);
	}

	public PortalCache() {
		this(new ArrayList<>());
	}

	public List<BlockPos> getPortalPositions(ResourceKey<Level> dimension) {
		return portalPositions.stream()
				.filter(globalPos -> globalPos.dimension().equals(dimension))
				.map(GlobalPos::pos)
				.toList();
	}

	public void validateNearestPortals(ServerLevel destLevel, BlockPos nearestPos) {
		List<BlockPos> portalList = getPortalPositions(destLevel.dimension());
		for (BlockPos pos : portalList) {
			//Only check portals that are near the given position
			if (pos.distManhattan(nearestPos) < 16) {
				//Load chunk to check the location of the portal
				destLevel.getChunk(pos);
				//Check if the portal is still valid and remove it if it isn't
				if (!destLevel.getBlockState(pos).is(Blocks.SCULK_SHRIEKER)) {
					portalList.remove(pos);
				}
			}
		}
		setDirty();
	}

	public void addPortal(GlobalPos globalPos) {
		List<BlockPos> nearestPortals = portalPositions.stream().map(GlobalPos::pos)
				.filter(pos -> pos.distManhattan(globalPos.pos()) < 5).toList();
		if (nearestPortals.isEmpty()) {
			portalPositions.add(globalPos);
		}
		setDirty();
	}

	public void removePortal(GlobalPos globalPos) {
		portalPositions.removeIf(loc -> loc.pos().distManhattan(globalPos.pos()) < 3);
		setDirty();
	}

	public void removeNearestPortal(GlobalPos globalPos) {
		BlockPos blockpos = globalPos.pos().below();
		portalPositions.removeIf(loc -> loc.pos().distManhattan(blockpos) < 5);
		setDirty();
	}

	public static SavedDataType<PortalCache> type() {
		return new SavedDataType<>(DATA_NAME, PortalCache::new, CODEC, null);
	}

	public static PortalCache get(Level world) {
		if (!(world instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);

		assert overworld != null;
		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(type());
	}
}
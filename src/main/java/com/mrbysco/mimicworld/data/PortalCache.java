package com.mrbysco.mimicworld.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mrbysco.mimicworld.MimicWorldMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.List;

public class PortalCache extends SavedData {
	private static final String DATA_NAME = MimicWorldMod.MOD_ID + "_portal_data";

	public PortalCache(ListMultimap<ResourceLocation, BlockPos> paintingMap) {
		this.paintingPositionMap.clear();
		if (!paintingMap.isEmpty()) {
			this.paintingPositionMap.putAll(paintingMap);
		}
	}

	public PortalCache() {
		this(ArrayListMultimap.create());
	}

	private final ListMultimap<ResourceLocation, BlockPos> paintingPositionMap = ArrayListMultimap.create();

	public static PortalCache load(CompoundTag tag) {
		ListMultimap<ResourceLocation, BlockPos> paintingMap = ArrayListMultimap.create();
		for (String nbtName : tag.getAllKeys()) {
			ListTag dimensionNBTList = new ListTag();
			if (tag.getTagType(nbtName) == 9) {
				Tag nbt = tag.get(nbtName);
				if (nbt instanceof ListTag listNBT) {
					if (!listNBT.isEmpty() && listNBT.getElementType() != CompoundTag.TAG_COMPOUND) {
						continue;
					}

					dimensionNBTList = listNBT;
				}
			}
			if (!dimensionNBTList.isEmpty()) {
				List<BlockPos> posList = new ArrayList<>();
				for (int i = 0; i < dimensionNBTList.size(); ++i) {
					CompoundTag dimTag = dimensionNBTList.getCompound(i);
					if (dimTag.contains("BlockPos")) {
						BlockPos blockPos = BlockPos.of(dimTag.getLong("BlockPos"));
						posList.add(blockPos);
					}
				}
				paintingMap.putAll(ResourceLocation.tryParse(nbtName), posList);
			}
		}
		return new PortalCache(paintingMap);
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		for (ResourceLocation dimensionLocation : paintingPositionMap.keySet()) {
			List<BlockPos> globalPosList = paintingPositionMap.get(dimensionLocation);

			ListTag dimensionStorage = new ListTag();
			for (BlockPos portalLoc : globalPosList) {
				CompoundTag positionTag = new CompoundTag();
				positionTag.putLong("BlockPos", portalLoc.asLong());
				dimensionStorage.add(positionTag);
			}
			compound.put(dimensionLocation.toString(), dimensionStorage);
		}
		return compound;
	}

	public List<BlockPos> getPortals(ResourceLocation dimensionLocation) {
		return paintingPositionMap.get(dimensionLocation);
	}

	public void addPortal(ResourceLocation dimensionLocation, BlockPos pos) {
		List<BlockPos> nearestPortals = paintingPositionMap.get(dimensionLocation).stream().filter((loc) -> loc.distManhattan(pos) < 5).toList();
		if (nearestPortals.isEmpty()) {
			paintingPositionMap.get(dimensionLocation)
					.add(pos);
		}
		setDirty();
	}

	public void removePortal(ResourceLocation dimensionLocation, BlockPos pos) {
		paintingPositionMap.get(dimensionLocation).removeIf((loc) -> loc.distManhattan(pos) < 3);
		setDirty();
	}

	public void removeNearestPortal(ResourceLocation dimensionLocation, BlockPos pos) {
		BlockPos blockpos = pos.below();
		paintingPositionMap.get(dimensionLocation).removeIf((loc) -> loc.distManhattan(blockpos) < 5);
		setDirty();
	}

	public static PortalCache get(Level world) {
		if (!(world instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(PortalCache::load, PortalCache::new, DATA_NAME);
	}
}
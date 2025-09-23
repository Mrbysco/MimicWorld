package com.mrbysco.mimicworld.registry;

import com.google.common.collect.ImmutableSet;
import com.mrbysco.mimicworld.MimicWorldMod;
import com.mrbysco.mimicworld.block.MimicPortalBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MimicRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MimicWorldMod.MOD_ID);
	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MimicWorldMod.MOD_ID);

	public static final DeferredBlock<MimicPortalBlock> MIMIC_PORTAL = BLOCKS.registerBlock("mimic_portal", MimicPortalBlock::new, Block.Properties.ofFullCopy(Blocks.NETHER_PORTAL));

	public static final Supplier<PoiType> MIMIC_PORTAL_POI = POI_TYPES.register("mimic_portal_poi", () ->
			new PoiType(ImmutableSet.copyOf(MIMIC_PORTAL.get().getStateDefinition().getPossibleStates()), 0, 3));
}

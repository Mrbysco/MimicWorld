package com.mrbysco.mimicworld.registry;

import com.google.common.collect.ImmutableSet;
import com.mrbysco.mimicworld.MimicWorldMod;
import com.mrbysco.mimicworld.block.MimicPortalBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MimicRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MimicWorldMod.MOD_ID);
	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MimicWorldMod.MOD_ID);

	public static final RegistryObject<MimicPortalBlock> MIMIC_PORTAL = BLOCKS.register("mimic_portal", () ->
			new MimicPortalBlock(Block.Properties.copy(Blocks.NETHER_PORTAL)));

	public static final RegistryObject<PoiType> MIMIC_PORTAL_POI = POI_TYPES.register("mimic_portal_poi", () ->
			new PoiType(ImmutableSet.copyOf(MIMIC_PORTAL.get().getStateDefinition().getPossibleStates()), 0, 3));
}

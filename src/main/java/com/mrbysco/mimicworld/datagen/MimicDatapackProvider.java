package com.mrbysco.mimicworld.datagen;

import com.mrbysco.mimicworld.worldgen.MimicDimensionTypes;
import com.mrbysco.mimicworld.worldgen.MimicWorldClocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MimicDatapackProvider extends DatapackBuiltinEntriesProvider {
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.DIMENSION_TYPE, MimicDimensionTypes::bootstrap)
			.add(Registries.WORLD_CLOCK, MimicWorldClocks::bootstrap);

	public MimicDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, Set<String> modIds) {
		super(output, registries, BUILDER, modIds);
	}
}

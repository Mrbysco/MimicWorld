package com.mrbysco.mimicworld.worldgen;

import com.mrbysco.mimicworld.MimicWorldMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.WorldClock;

public class MimicWorldClocks {
	protected static final ResourceKey<WorldClock> MIMIC_WORLD = ResourceKey.create(Registries.WORLD_CLOCK, Identifier.fromNamespaceAndPath(MimicWorldMod.MOD_ID, "mimic_world"));

	public static void bootstrap(BootstrapContext<WorldClock> context) {
		context.register(MIMIC_WORLD, new WorldClock());
	}
}

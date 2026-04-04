package com.mrbysco.mimicworld.worldgen;

import com.mrbysco.mimicworld.MimicWorldMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.timeline.Timeline;

import java.util.Optional;

public class MimicDimensionTypes {
	public static final ResourceKey<DimensionType> MIMIC_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(MimicWorldMod.MOD_ID, "mimic"));

	public static void bootstrap(BootstrapContext<DimensionType> context) {
		HolderGetter<Timeline> timelines = context.lookup(Registries.TIMELINE);
		HolderGetter<WorldClock> clocks = context.lookup(Registries.WORLD_CLOCK);
		EnvironmentAttributeMap overworldAttributes = EnvironmentAttributeMap.builder()
				.set(EnvironmentAttributes.FOG_COLOR, -4138753)
				.set(EnvironmentAttributes.SKY_COLOR, OverworldBiomes.calculateSkyColor(0.8F))
				.set(EnvironmentAttributes.AMBIENT_LIGHT_COLOR, -16119286)
				.set(EnvironmentAttributes.CLOUD_COLOR, ARGB.white(0.8F))
				.set(EnvironmentAttributes.CLOUD_HEIGHT, 192.33F)
				.set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
				.set(EnvironmentAttributes.BED_RULE, new BedRule(BedRule.Rule.NEVER, BedRule.Rule.NEVER, false, Optional.empty()))
				.set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
				.set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, true)
				.set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS).build();
		context.register(MIMIC_DIMENSION_TYPE,
				new DimensionType(false, true, false, false,
						(double) 1.0F, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, 0.0F,
						new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
						DimensionType.Skybox.OVERWORLD, CardinalLighting.Type.DEFAULT, overworldAttributes,
						timelines.getOrThrow(MimicWorldMod.IN_MIMIC_WORLD),
						Optional.of(clocks.getOrThrow(MimicWorldClocks.MIMIC_WORLD))));

	}
}

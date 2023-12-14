package com.mrbysco.mimicworld;

import com.mojang.logging.LogUtils;
import com.mrbysco.mimicworld.handler.PortalHandler;
import com.mrbysco.mimicworld.registry.MimicRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MimicWorldMod.MOD_ID)
public class MimicWorldMod {
	public static final String MOD_ID = "mimicworld";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final ResourceKey<Level> MIMIC_WORLD_KEY = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MOD_ID, "mimic_world"));

	public MimicWorldMod() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		MimicRegistry.BLOCKS.register(eventBus);
		MimicRegistry.POI_TYPES.register(eventBus);

		MinecraftForge.EVENT_BUS.register(new PortalHandler());
	}
}

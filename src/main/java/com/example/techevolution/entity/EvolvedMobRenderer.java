package com.example.techevolution.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EvolvedMobRenderer extends HumanoidMobRenderer<EvolvedMob, ZombieModel<EvolvedMob>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/zombie/zombie.png");

    public EvolvedMobRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR))));
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.EVOLVED_MOB.get(), EvolvedMobRenderer::new);
    }

    @Override
    public ResourceLocation getTextureLocation(EvolvedMob mob) {
        return TEXTURE;
    }
}

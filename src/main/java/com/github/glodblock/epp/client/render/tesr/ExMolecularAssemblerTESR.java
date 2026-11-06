package com.github.glodblock.epp.client.render.tesr;

import appeng.client.render.crafting.AssemblerAnimationStatus;
import appeng.client.render.effects.ParticleTypes;
import appeng.core.AppEng;
import appeng.core.AppEngClient;
import com.github.glodblock.epp.common.tileentities.TileExMolecularAssembler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ExMolecularAssemblerTESR implements BlockEntityRenderer<TileExMolecularAssembler> {

    public static final ResourceLocation LIGHTS_MODEL = AppEng.makeId("block/molecular_assembler_lights");

    private final Random particleRandom = new Random();

    public ExMolecularAssemblerTESR(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileExMolecularAssembler molecularAssembler, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        AssemblerAnimationStatus status = molecularAssembler.getAnimationStatus();
        if (status != null) {
            if (!Minecraft.getInstance().isPaused()) {
                if (status.isExpired()) {
                    molecularAssembler.setAnimationStatus(null);
                }
                status.setAccumulatedTicks(status.getAccumulatedTicks() + partialTicks);
                status.setTicksUntilParticles(status.getTicksUntilParticles() - partialTicks);
            }

            renderStatus(molecularAssembler, ms, bufferIn, combinedLightIn, status);
        }

        if (molecularAssembler.isPowered()) {
            renderPowerLight(ms, bufferIn, combinedLightIn, combinedOverlayIn);
        }
    }

    private void renderPowerLight(PoseStack ms, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel lightsModel = minecraft.getModelManager().getModel(LIGHTS_MODEL);
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.tripwire());
        minecraft.getBlockRenderer().getModelRenderer().renderModel(
                ms.last(), buffer, null, lightsModel, 1, 1, 1, combinedLightIn, combinedOverlayIn);
    }

    private void renderStatus(TileExMolecularAssembler molecularAssembler, PoseStack ms, MultiBufferSource bufferIn, int combinedLightIn, AssemblerAnimationStatus status) {
        double centerX = molecularAssembler.getBlockPos().getX() + 0.5;
        double centerY = molecularAssembler.getBlockPos().getY() + 0.5;
        double centerZ = molecularAssembler.getBlockPos().getZ() + 0.5;
        Minecraft minecraft = Minecraft.getInstance();
        if (status.getTicksUntilParticles() <= 0) {
            status.setTicksUntilParticles(4);
            if (AppEngClient.instance().shouldAddParticles(particleRandom)) {
                for (int x = 0; x < (int) Math.ceil(status.getSpeed() / 5.0); x++) {
                    minecraft.particleEngine.createParticle(ParticleTypes.CRAFTING, centerX, centerY, centerZ, 0, 0, 0);
                }
            }
        }

        ItemStack is = status.getIs();

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);

        if (!(is.getItem() instanceof net.minecraft.world.item.BlockItem)) {
            ms.translate(0, -0.3, 0);
        } else {
            ms.translate(0, -0.2, 0);
        }

        itemRenderer.renderStatic(is, ItemTransforms.TransformType.GROUND, combinedLightIn,
                OverlayTexture.NO_OVERLAY, ms, bufferIn, 0);
        ms.popPose();
    }

}

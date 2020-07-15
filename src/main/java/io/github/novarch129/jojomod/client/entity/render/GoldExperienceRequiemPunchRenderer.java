package io.github.novarch129.jojomod.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.novarch129.jojomod.client.entity.model.GoldExperienceRequiemPunchModel;
import io.github.novarch129.jojomod.entity.stand.attack.GoldExperienceRequiemPunchEntity;
import io.github.novarch129.jojomod.JojoBizarreSurvival;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GoldExperienceRequiemPunchRenderer extends StandPunchRenderer<GoldExperienceRequiemPunchEntity> {
	protected static final ResourceLocation TEXTURE = new ResourceLocation(JojoBizarreSurvival.MOD_ID, "textures/stands/ger_punch.png");

	public GoldExperienceRequiemPunchRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void render(@Nonnull GoldExperienceRequiemPunchEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn, new GoldExperienceRequiemPunchModel());
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(final GoldExperienceRequiemPunchEntity entity) {
		return TEXTURE;
	}
}

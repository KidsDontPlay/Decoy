package mrriegel.decoy;

import java.awt.Color;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderDecoy extends RenderLiving<EntityDecoy> {

	private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");

	public RenderDecoy(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
	}

	@Override
	protected int getColorMultiplier(EntityDecoy entitylivingbaseIn, float lightBrightness, float partialTickTime) {
		int fluidColor = entitylivingbaseIn.getColor();
		int x = new Color((fluidColor >> 16) & 0xFF, (fluidColor >> 8) & 0xFF, (fluidColor) & 0xFF, 128).getRGB();
		return x;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDecoy entity) {
		return PIG_TEXTURES;
	}

	public static class Factory implements IRenderFactory<EntityDecoy> {

		@Override
		public Render<? super EntityDecoy> createRenderFor(RenderManager manager) {
			return new RenderDecoy(manager, new ModelPig(), 0.7f);
		}

	}

}

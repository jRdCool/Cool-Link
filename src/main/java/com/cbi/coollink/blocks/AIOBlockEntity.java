import net.minecraft.block.entity.BlockEntity;

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class AIOBlockEntity extends BlockEntity {
	private final ModelPart bb_main;
	public AIOBlockEntity(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}
	/*public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new Dilation(0.0F))
		.uv(0, 15).cuboid(-7.0F, -4.0F, -7.0F, 14.0F, 2.0F, 14.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-4.5F, -2.5F, -6.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-1.5F, -2.5F, -6.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 5).cuboid(3.5F, -1.5F, -7.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}*/
}
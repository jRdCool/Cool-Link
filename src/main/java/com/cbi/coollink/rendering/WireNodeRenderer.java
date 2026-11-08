package com.cbi.coollink.rendering;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.blockentities.state.WireNodeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


public class WireNodeRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, WireNodeRenderState> {//extends BlockEntityRenderer<T> {

	public WireNodeRenderer(BlockEntityRendererProvider.Context context) {
		super();
	}

	private static final float HANG = 0.5f;



	@Override
	public @NonNull WireNodeRenderState createRenderState() {
		return new WireNodeRenderState();
	}

	@Override
	public void submit(WireNodeRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
		poseStack.pushPose();
		IWireNode te = (IWireNode) state.blockEntity;
		Level world = state.blockEntity.getLevel();

		if(world==null) return;



		IWireNode otherBlock;
		for (int i = 0; i < te.getNodeCount(); i++) {
			if (!te.hasConnection(i)) continue;//check that this node is connected
			if (world.getBlockEntity(te.getLocalNode(i).getTargetPos())instanceof IWireNode){//check the target has wire nodes
				otherBlock =(IWireNode) world.getBlockEntity(te.getLocalNode(i).getTargetPos());
				if(otherBlock==null)continue;//????? what why is this necessary, would instance of not take care of this?
			}else{continue;}
			Vec3 d1 = te.getNodeOffset(i);
			float ox1 = ((float) d1.x);
			float oy1 = ((float) d1.y);
			float oz1 = ((float) d1.z);

			IWireNode wn = otherBlock.getWireNode(i); //get the wire node of a given node index
			if (wn == null) {//if that returned null then stop rendering further
				//TODO make this logging toggleable
				Main.LOGGER.warn("getWireNode("+i+") returned null when attempting to render wire for "+otherBlock.getBlockPos());
				continue;
			}

			Vec3 d2 = wn.getNodeOffset(te.getOtherNodeIndex(i)); // get other
			float ox2 = ((float) d2.x);
			float oy2 = ((float) d2.y);
			float oz2 = ((float) d2.z);
			BlockPos other = te.getNodePos(i);

			float tx = other.getX() - te.getBlockPos().getX();
			float ty = other.getY() - te.getBlockPos().getY();
			float tz = other.getZ() - te.getBlockPos().getZ();
			poseStack.pushPose();



			float dis = distanceFromZero(tx, ty, tz);

			poseStack.translate(tx + ox2, ty + oy2, tz + oz2);
			final int lambdaTypeIndex = i;
			//this is what actually submits the vertices for rendering
			submitNodeCollector.submitCustomGeometry( poseStack,RenderType.WIRE ,((pose, buffer) -> {
				wireRender(
						state.blockEntity,
						other,
						pose,
						buffer,
						-tx - ox2 + ox1,
						-ty - oy2 + oy1,
						-tz - oz2 + oz1,
						te.getNodeType(lambdaTypeIndex),
						dis
				);
			}) );

			poseStack.popPose();
		}


		poseStack.popPose();
	}


	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}


	public AABB getRenderBoundingBox(BlockEntity be) {
		IWireNode te = (IWireNode) be;
		BlockPos pos = be.getBlockPos();
		int minX = pos.getX(), minY = pos.getY(), minZ = pos.getZ();
		int maxX = minX, maxY = minY, maxZ = minZ;
		for (int i = 0; i < te.getNodeCount(); i++) {
			BlockPos other = te.getNodePos(i);
			if (other == null) continue;
			minX = Math.min(minX, other.getX());
			minY = Math.min(minY, other.getY());
			minZ = Math.min(minZ, other.getZ());
			maxX = Math.max(maxX, other.getX());
			maxY = Math.max(maxY, other.getY());
			maxZ = Math.max(maxZ, other.getZ());
		}
		// One block of margin covers the sag.
		AABB box = new AABB(minX - 1, minY - 1, minZ - 1, maxX + 2, maxY + 2, maxZ + 2);
		//Wire running to player's hand while being placed ToDo: Implement later
		//if (ClientEventHandler.clientRenderHeldWire) {//NOTE: ClientEventHandler is a propritary system in create additions
		//	// A wire being placed runs to the player.
		//	LocalPlayer player = ClientMinecraftWrapper.getPlayer();
		//	Util.Triple<BlockPos, Integer, WireType> wireNode = player == null ? null : Util.getWireNodeOfSpools(player.getInventory().getSelected());
		//	if (wireNode != null && wireNode.a.equals(pos)) box = box.minmax(player.getBoundingBox());
		//}
		return box;
	}

	@Override
	public boolean shouldRender(@NonNull BlockEntity blockEntity, @NonNull Vec3 cameraPosition) {
		double distance = getViewDistance();

		return getRenderBoundingBox(blockEntity).distanceToSqr(cameraPosition) < distance * distance;
	}

	@Override
	public void extractRenderState(T blockEntity, WireNodeRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {

		IWireNode te = (IWireNode) blockEntity;
		Level world = blockEntity.getLevel();
		if(world==null) return;
		state.blockEntity = blockEntity;
		state.nodeCount = te.getNodeCount();
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
	}


	private static float divf(int a, int b) {
		return (float) a / (float) b;
	}

	private static float hang(float f, float dis) {
		return (float) Math.sin(-f * (float) Math.PI) * (WireNodeRenderer.HANG * dis / (float) 16);
	}

	public static float distanceFromZero(float x, float y, float z) {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}


	public static void wireRender(BlockEntity tileEntityIn, BlockPos other, PoseStack.Pose stack, VertexConsumer ivertexbuilder, float x, float y, float z,
			WireType type, float dis) {
		//matrix.pushPose();

		Matrix4f matrix4f = stack.pose();
		float f = Mth.invSqrt(x * x + z * z) * 0.025F / 2.0F;//fastInvSqrt
		float o1 = z * f;
		float o2 = x * f;
		//BlockPos blockpos1 = tileEntityIn.getBlockPos();
		var world = tileEntityIn.getLevel();
		if(world==null){return;}

		//int i = world.getBrightness(LightLayer.SKY,blockpos1);
		//int j = world.getBrightness(LightLayer.SKY,other);
		//int k = world.getBrightness(LightLayer.SKY,blockpos1);
		//int l = world.getBrightness(LightLayer.SKY,other);
		wirePart(ivertexbuilder, matrix4f, x, y, z, 0.025F, 0.025F, o1, o2, type, dis, 1f);
		wirePart(ivertexbuilder, matrix4f, x, y, z, 0.025F, 0.0F, o1, o2, type, dis, 1f);
		//light
		//matrix.popPose();
	}

	public static void wirePart(VertexConsumer vertBuilder, Matrix4f matrix, float x, float y, float z, float a, float b, float o1, float o2, WireType type, float dis, float hangFactor) {
		for (int j = 0; j < 24; ++j) {
			//float f = (float) j / 23.0F;
			//int k = (int) Mth.lerp(f, (float) l1, (float) l2);
			//int l = (int) Mth.lerp(f, (float) l3, (float) l4);
			int light = 1;//LightmapTextureManager.pack(k, l);

			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j, false, o1, o2, type, dis, hangFactor);
			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j + 1, true, o1, o2, type, dis, hangFactor);
			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j, true, o1, o2, type, dis, hangFactor);
			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j + 1, false, o1, o2, type, dis, hangFactor);

		}

	}

	static Color[] colors = {Color.RED, Color.GREEN, new Color(0f, 0f, 1f, 1f)};
	//static float LIGHT_Y_OFFSET = -0.03f;


	public static void wireVert(VertexConsumer vertBuilder, Matrix4f matrix, int light, float x, float y, float z,
			float a, float b, int count, int index, boolean sw, float o1, float o2, WireType type, float dis, float hangFactor) {
		int cr = type.getRed();
		int cg = type.getGreen();
		int cb = type.getBlue();
		if (index % 2 == 0) {
			cr *= 0.7F;
			cg *= 0.7F;
			cb *= 0.7F;
		}

		float part = (float) index / (float) count;
		float fx = x * part;
		float fy = (y > 0.0F ? y * part * part : y - y * (1.0F - part) * (1.0F - part)) + (hangFactor*hang(divf(index, count), dis));
		float fz = z * part;

		//System.out.println((fx + o1) +":"+ (fy + n1 - n2) +":"+ (fz - o2));

		int singleColor = 0xFF_000000 | cr << 16 | cg << 8 | cb;
		if(Math.abs(x) + Math.abs(z) < Math.abs(y)) {
			boolean p = b > 0;
			float c = 0.015f;

			if (!sw) {
				vertBuilder.addVertex(matrix, fx -c, fy, fz + (p?-c:c)).setColor(singleColor).setLight(light);//Changes May be necessary
			}

			vertBuilder.addVertex(matrix, fx + c, fy, fz + (p?c:-c)).setColor(singleColor).setLight(light);
			if (sw) {
				vertBuilder.addVertex(matrix, fx -c, fy, fz + (p?-c:c)).setColor(singleColor).setLight(light);
			}
		}
		else {
			if (!sw) {
				vertBuilder.addVertex(matrix, fx + o1, fy + a - b, fz - o2).setColor(singleColor).setLight(light);
			}

			vertBuilder.addVertex(matrix, fx - o1, fy + b, fz + o2).setColor(singleColor).setLight(light);
			if (sw) {
				vertBuilder.addVertex(matrix, fx + o1, fy + a - b, fz - o2).setColor(singleColor).setLight(light);
			}
		}
	}
}


//This is part Creates Color class for compatibility
@SuppressWarnings("unused")
class Color {
	public final static Color TRANSPARENT_BLACK = new Color(0, 0, 0, 0).setImmutable();
	public final static Color BLACK = new Color(0, 0, 0).setImmutable();
	public final static Color WHITE = new Color(255, 255, 255).setImmutable();
	public final static Color RED = new Color(255, 0, 0).setImmutable();
	public final static Color GREEN = new Color(0, 255, 0).setImmutable();
	public final static Color SPRING_GREEN = new Color(0, 255, 187).setImmutable();

	protected boolean mutable = true;
	protected int value;

	public Color(int r, int g, int b) {
		this(r, g, b, 0xff);
	}

	public Color(int r, int g, int b, int a) {
		value = ((a & 0xff) << 24) |
				((r & 0xff) << 16) |
				((g & 0xff) << 8)  |
				((b & 0xff));
	}

	public Color(float r, float g, float b, float a) {
		this(
				(int) (0.5 + 0xff * Mth.clamp(r, 0, 1)),
				(int) (0.5 + 0xff * Mth.clamp(g, 0, 1)),
				(int) (0.5 + 0xff * Mth.clamp(b, 0, 1)),
				(int) (0.5 + 0xff * Mth.clamp(a, 0, 1))
		);
	}

	public Color(int rgba) {
		value = rgba;
	}


	public Color(int rgb, boolean hasAlpha) {
		if (hasAlpha) {
			value = rgb;
		} else {
			value = rgb | 0xff_000000;
		}
	}

	public Color setImmutable() {
		this.mutable = false;
		return this;
	}





	public record Submit(Matrix4f pose, EntityRenderState.LeashState leashState) implements SubmitNode {
		@Override
		public @NonNull FeatureRendererType<LeashFeatureRenderer.Submit> featureType() {
			return LeashFeatureRenderer.TYPE;
		}
	}
}

package com.cbi.coollink.rendering;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
//import net.minecraft.client.player.LocalPlayer;//Need to be replaced to get working
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import net.minecraft.client.render.LightmapTextureManager;


public class WireNodeRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {//extends BlockEntityRenderer<T> {
	public WireNodeRenderer(BlockEntityRendererFactory.Context context) {
		super();
	}

	private static final float HANG = 0.5f;


	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn,
					   int combinedLightIn, int combinedOverlayIn) {
		IWireNode te = (IWireNode) tileEntityIn;

		//time += partialTicks;

		for (int i = 0; i < 1; i++) {
			if (!te.hasConnection(i)) continue;
			Vec3d d1 = te.getNodeOffset(i);
			float ox1 = ((float) d1.x);
			float oy1 = ((float) d1.y);
			float oz1 = ((float) d1.z);

			IWireNode wn = te.getWireNode(i);
			if (wn == null) return;

			Vec3d d2 = wn.getNodeOffset(te.getOtherNodeIndex(i)); // get other
			float ox2 = ((float) d2.x);
			float oy2 = ((float) d2.y);
			float oz2 = ((float) d2.z);
			BlockPos other = te.getNodePos(i);

			float tx = other.getX() - te.getPos().getX();
			float ty = other.getY() - te.getPos().getY();
			float tz = other.getZ() - te.getPos().getZ();
			matrixStackIn.push();

			float dis = distanceFromZero(tx, ty, tz);

			matrixStackIn.translate(tx + .5f + ox2, ty + .5f + oy2, tz + .5f + oz2);
			wireRender(
					tileEntityIn,
					other,
					matrixStackIn,
					bufferIn,
					-tx - ox2 + ox1,
					-ty - oy2 + oy1,
					-tz - oz2 + oz1,
					te.getNodeType(i),
					dis
				);
				matrixStackIn.pop();
			}

		/*if(ClientEventHandler.clientRenderHeldWire) {
			LocalPlayer player = ClientMinecraftWrapper.getPlayer();
			Util.Triple<BlockPos, Integer, WireType> wireNode = Util.getWireNodeOfSpools(player.getInventory().getSelected());
			if(wireNode == null) return;

			BlockPos nodePos = wireNode.a;
			int nodeIndex = wireNode.b;
			WireType wireType = wireNode.c;
			if(!nodePos.equals(te.getPos())) return;

			Vec3d d1 = te.getNodeOffset(nodeIndex);
			float ox1 = ((float) d1.x());
			float oy1 = ((float) d1.y());
			float oz1 = ((float) d1.z());

			Vec3d playerPos = player.getPosition(partialTicks);
			float tx = (float)playerPos.x - te.getPos().getX();
			float ty = (float)playerPos.y - te.getPos().getY();
			float tz = (float)playerPos.z - te.getPos().getZ();
			matrixStackIn.pushPose();

			float dis = distanceFromZero(tx, ty, tz);

			matrixStackIn.translate(tx + .5f, ty + .5f, tz + .5f);
			wireRender(
					tileEntityIn,
					player.blockPosition(),
					matrixStackIn,
					bufferIn,
					-tx + ox1,
					-ty + oy1,
					-tz + oz1,
					wireType,
					dis
			);
			matrixStackIn.popPose();
		}*/
	}



	private static float divf(int a, int b) {
		return (float) a / (float) b;
	}

	private static float hang(float f, float dis) {
		return (float) Math.sin(-f * (float) Math.PI) * (HANG * dis / (float) 16);
	}

	public static float distanceFromZero(float x, float y, float z) {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	@SuppressWarnings("deprecation")
	public static void wireRender(BlockEntity tileEntityIn, BlockPos other, MatrixStack stack, VertexConsumerProvider buffer, float x, float y, float z,
			WireType type, float dis) {
		//matrix.pushPose();

		VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.WIRE);
		Matrix4f matrix4f = stack.peek().getPositionMatrix();
		float f = MathHelper.inverseSqrt(x * x + z * z) * 0.025F / 2.0F;//fastInvSqrt
		float o1 = z * f;
		float o2 = x * f;
		BlockPos blockpos1 = tileEntityIn.getPos();
		var world = tileEntityIn.getWorld();
		if(world==null){return;}

		int i = (int)world.getBrightness(blockpos1);
		int j = (int)world.getBrightness(other);
		int k = (int)world.getBrightness(blockpos1);
		int l = (int)world.getBrightness(other);
		wirePart(ivertexbuilder, matrix4f, x, y, z, j, i, l, k, 0.025F, 0.025F, o1, o2, type, dis, tileEntityIn.getCachedState(), stack, 0, 1f);
		wirePart(ivertexbuilder, matrix4f, x, y, z, j, i, l, k, 0.025F, 0.0F, o1, o2, type, dis, tileEntityIn.getCachedState(), stack, 1, 1f);
		//light
		//matrix.popPose();
	}

	public static void wirePart(VertexConsumer vertBuilder, Matrix4f matrix, float x, float y, float z, int l1, int l2,
			int l3, int l4, float a, float b, float o1, float o2, WireType type, float dis, BlockState state, MatrixStack stack, int lightOffset, float hangFactor) {
		for (int j = 0; j < 24; ++j) {
			float f = (float) j / 23.0F;
			int k = (int) MathHelper.lerp(f, (float) l1, (float) l2);
			int l = (int) MathHelper.lerp(f, (float) l3, (float) l4);
			int light = LightmapTextureManager.pack(k, l);

			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j, false, o1, o2, type, dis, state, stack, lightOffset, hangFactor);
			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j + 1, true, o1, o2, type, dis, state, stack, lightOffset+1, hangFactor);

		}

	}
	@SuppressWarnings("unused")
	static Color[] colors = {Color.RED, Color.GREEN, new Color(0f, 0f, 1f, 1f)};
	//static float LIGHT_Y_OFFSET = -0.03f;


	@SuppressWarnings("unused")
	public static void wireVert(VertexConsumer vertBuilder, Matrix4f matrix, int light, float x, float y, float z,
			float a, float b, int count, int index, boolean sw, float o1, float o2, WireType type, float dis, BlockState state, MatrixStack stack, int lightOffset, float hangFactor) {
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


		if(Math.abs(x) + Math.abs(z) < Math.abs(y)) {
			boolean p = b > 0;
			float c = 0.015f;

			if (!sw) {
				vertBuilder.vertex(matrix, fx -c, fy, fz + (p?-c:c)).color(cr, cg, cb, 255).light(light);//Changes May be necessary
			}

			vertBuilder.vertex(matrix, fx + c, fy, fz + (p?c:-c)).color(cr, cg, cb, 255).light(light);
			if (sw) {
				vertBuilder.vertex(matrix, fx -c, fy, fz + (p?-c:c)).color(cr, cg, cb, 255).light(light);
			}
		}
		else {
			if (!sw) {
				vertBuilder.vertex(matrix, fx + o1, fy + a - b, fz - o2).color(cr, cg, cb, 255).light(light);
			}

			vertBuilder.vertex(matrix, fx - o1, fy + b, fz + o2).color(cr, cg, cb, 255).light(light);
			if (sw) {
				vertBuilder.vertex(matrix, fx + o1, fy + a - b, fz - o2).color(cr, cg, cb, 255).light(light);
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
				(int) (0.5 + 0xff * MathHelper.clamp(r, 0, 1)),
				(int) (0.5 + 0xff * MathHelper.clamp(g, 0, 1)),
				(int) (0.5 + 0xff * MathHelper.clamp(b, 0, 1)),
				(int) (0.5 + 0xff * MathHelper.clamp(a, 0, 1))
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
}

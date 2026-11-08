package com.cbi.coollink.rendering.blockentities;

import com.cbi.coollink.blocks.ServerRack;
import com.cbi.coollink.blocks.blockentities.ServerRackBlockEntity;
import com.cbi.coollink.rendering.RenderType;
import com.cbi.coollink.rendering.blockentities.state.ServerRackRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ServerRackBlockEntityRenderer implements BlockEntityRenderer<ServerRackBlockEntity, ServerRackRenderState> {

//    private static final Identifier SERVER_RACK_TEXTURE = Identifier.of(Main.namespace, "textures/test.png");

    public ServerRackBlockEntityRenderer(BlockEntityRendererProvider.Context context){}

    @Override
    public @NonNull ServerRackRenderState createRenderState() {
        return new ServerRackRenderState();
    }

    @Override
    public void submit(@NonNull ServerRackRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        ServerRackBlockEntity entity = state.blockEntity;
        if(entity.getBlockState().getValue(ServerRack.half)== ServerRack.Half.BOTTOM) {
            poseStack.pushPose();
            if(entity.getBlockState().getValue(ServerRack.direction) == ServerRack.Direction.EAST_WEST) {
                poseStack.rotate(Axis.YN,(float)Math.toRadians(90));
                poseStack.translate(0,0,-1);
            }
            submitNodeCollector.submitCustomGeometry(poseStack,RenderType.SERVER_RACK, (pose, vertexConsumer) -> {
                //TODO: check the installed servers and only render on bays that have servers installed
                renderServer(pose,vertexConsumer,0);
                renderServer(pose,vertexConsumer,1);
                renderServer(pose,vertexConsumer,2);
                renderServer(pose,vertexConsumer,3);
                renderServer(pose,vertexConsumer,4);
                renderServer(pose,vertexConsumer,5);
            });
            poseStack.popPose();
        }
    }

    @Override
    public void extractRenderState(@NonNull ServerRackBlockEntity blockEntity, ServerRackRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        state.blockEntity = blockEntity;
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }

    /**Renders a server in a slot on the server rack
     * note: this should only be called for the bottom rack
     * @param pose the transformation matrix for converting the vertexes into world coordinates, note this should already be tuned for any rack rotations
     * @param vertexConsumer the vertexConsumer/renderBuffer to wright to
     * @param slot the rack slot to render to, range between 0-5
     TODO: add additional params for animations and stuff
     */
    void renderServer(PoseStack.Pose pose, VertexConsumer vertexConsumer, int slot){
        //apparently the slots are not evenly spaced!
        switch(slot) {
            case 0 -> pose.translate(0, 1.0f / 4.6f, 0);
            case 1 -> pose.translate(0, 0.595f, 0);
            case 2 -> pose.translate(0, 4.16f / 4.6f, 0);
            case 3 -> pose.translate(0, 5.6f / 4.6f, 0);
            case 4 -> pose.translate(0, 1.53f, 0);
            case 5 -> pose.translate(0, 8.48f / 4.6f, 0);
        }

        Matrix4f matrix = pose.pose();
        //center rect
        //quadY(matrix,vertexConsumer,0,0,0,1,1,0xFF_00AF00,FACE_FRONT | FACE_BACK);

        float serverHeight = 0.28f;

        //rail thing
        //west rail
        quadX(matrix,vertexConsumer,0.8f/16,-0.014f,0,0.028f,1,0xFF_9E9A95,FACE_BACK | FACE_FRONT);//rail side
        quadY(matrix,vertexConsumer,0.8f/16,-0.014f,0,0.035f,1,0xFF_909090,FACE_BACK | FACE_FRONT);//rail bottom
        quadY(matrix,vertexConsumer,0.8f/16,0.014f,0,0.035f,1,0xFF_909090,FACE_BACK | FACE_FRONT);//rail top
        quadZ(matrix,vertexConsumer,0.8f/16,-0.014f,0,0.035f,0.028f,0xFF_9E9A95,FACE_BACK | FACE_FRONT);//end cap
        quadZ(matrix,vertexConsumer,0.8f/16,-0.014f,1,0.035f,0.028f,0xFF_9E9A95,FACE_BACK | FACE_FRONT);//end cap
        //east rail
        quadX(matrix,vertexConsumer,1-0.8f/16,-0.014f,0,0.028f,1,0xFF_9E9A95,FACE_BACK | FACE_FRONT);//rail side
        quadY(matrix,vertexConsumer,1-0.8f/16,-0.014f,0,-0.035f,1,0xFF_909090,FACE_BACK | FACE_FRONT);//rail bottom
        quadY(matrix,vertexConsumer,1-0.8f/16,0.014f,0,-0.035f,1,0xFF_909090,FACE_BACK | FACE_FRONT);//rail top
        quadZ(matrix,vertexConsumer,1-0.8f/16,-0.014f,0,-0.035f,0.028f,0xFF_9E9A95,FACE_BACK | FACE_FRONT);//end cap
        quadZ(matrix,vertexConsumer,1-0.8f/16,-0.014f,1,-0.035f,0.028f,0xFF_9E9A95,FACE_BACK | FACE_FRONT);//end cap

        //west wall
        quadX(matrix,vertexConsumer,0.085f,-serverHeight/2,0,serverHeight,1,0xFF_909090,FACE_BACK | FACE_FRONT);
        //east wall
        quadX(matrix,vertexConsumer,1-0.085f,-serverHeight/2,0,serverHeight,1,0xFF_909090,FACE_BACK | FACE_FRONT);
        //floor
        quadY(matrix,vertexConsumer,0.085f,-serverHeight/2,0,0.83f,1,0xFF_9E9A95,FACE_BACK | FACE_FRONT);
        //top
        quadY(matrix,vertexConsumer,0.085f, serverHeight/2,0,0.83f,1,0xFF_8E8A85,FACE_BACK | FACE_FRONT);

        //front
        quadZ(matrix,vertexConsumer, 0.085f, -serverHeight/2,0,0.83f,serverHeight,0xFF_c4c1bc,FACE_BACK | FACE_FRONT);

        //back
        quadZ(matrix,vertexConsumer, 0.085f, -serverHeight/2,1,0.83f,serverHeight,0xFF_c4c1bc,FACE_BACK | FACE_FRONT);

        //un translate
        switch(slot) {
            case 0 -> pose.translate(0, -1.0f / 4.6f, 0);
            case 1 -> pose.translate(0, -0.595f, 0);
            case 2 -> pose.translate(0, -4.16f / 4.6f, 0);
            case 3 -> pose.translate(0, -5.6f / 4.6f, 0);
            case 4 -> pose.translate(0, -1.53f, 0);
            case 5 -> pose.translate(0, -8.48f / 4.6f, 0);
        }
    }

    static final int FACE_FRONT = 1;
    static final int FACE_BACK = 2;

    void quadX(Matrix4f matrix, VertexConsumer vertexConsumer, float x, float y, float z, float dy, float dz, int color, int facesBits){
        if((facesBits & 1) != 0){
            vertexConsumer
                    .addVertex(matrix, x, y, z).setColor(color).setNormal(x,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y+dy, z).setColor(color).setNormal(x+dy,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y+dy, z+dz).setColor(color).setNormal(x+dy,y+dy,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y, z+dz).setColor(color).setNormal(x,y+dy,z).setLight(255).setOverlay(255);
        }
        if((facesBits & 2) != 0){
            vertexConsumer
                    .addVertex(matrix, x, y, z).setColor(color).setNormal(x,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y, z+dz).setColor(color).setNormal(x+dy,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y+dy, z+dz).setColor(color).setNormal(x+dy,y+dy,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y+dy, z).setColor(color).setNormal(x,y+dy,z).setLight(255).setOverlay(255);
        }
    }
    void quadY(Matrix4f matrix, VertexConsumer vertexConsumer, float x,float y, float z, float dx, float dz, int color, int facesBits){
        if((facesBits & 1) == 1){
            vertexConsumer
                    .addVertex(matrix, x, y, z).setColor(color).setNormal(x,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y, z).setColor(color).setNormal(x+dx,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y, z+dz).setColor(color).setNormal(x+dx, y, z+dz).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y, z+dz).setColor(color).setNormal(x, y, z+dz).setLight(255).setOverlay(255);
        }
        if((facesBits & 2) != 0){
            vertexConsumer
                    .addVertex(matrix, x, y, z).setColor(color).setNormal(x,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y, z+dz).setColor(color).setNormal(x,y,z+dz).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y, z+dz).setColor(color).setNormal(x+dx, y, z+dz).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y, z).setColor(color).setNormal(x+dx, y, z).setLight(255).setOverlay(255);
        }
    }
    void quadZ(Matrix4f matrix, VertexConsumer vertexConsumer, float x, float y, float z, float dx, float dy, int color, int facesBits){
        if((facesBits & 1) == 1){
            vertexConsumer
                    .addVertex(matrix, x, y, z).setColor(color).setNormal(x,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y, z).setColor(color).setNormal(x+dx,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y+dy, z).setColor(color).setNormal(x+dx, y+dy, z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y+dy, z).setColor(color).setNormal(x, y+dy, z).setLight(255).setOverlay(255);
        }
        if((facesBits & 2) != 0){
            vertexConsumer
                    .addVertex(matrix, x, y, z).setColor(color).setNormal(x,y,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x, y+dy, z).setColor(color).setNormal(x,y+dy,z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y+dy, z).setColor(color).setNormal(x+dx, y+dy, z).setLight(255).setOverlay(255)
                    .addVertex(matrix, x+dx, y, z).setColor(color).setNormal(x+dx, y, z).setLight(255).setOverlay(255);
        }
    }
}

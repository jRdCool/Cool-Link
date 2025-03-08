package com.cbi.coollink.rendering.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.ServerRack;
import com.cbi.coollink.blocks.blockentities.ServerRackBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ServerRackBlockEntityRenderer implements BlockEntityRenderer<ServerRackBlockEntity> {
    public ServerRackBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    private static final Identifier SERVER_RACK_TEXTURE = Identifier.of(Main.namespace, "textures/test.png");
    @Override
    public void render(ServerRackBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(entity.getCachedState().get(ServerRack.half)== ServerRack.Half.BOTTOM) {
            RenderLayer layer = RenderLayer.of("serverrack", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, RenderLayer.of(RenderPhase.TRANSLUCENT_PROGRAM));


            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, SERVER_RACK_TEXTURE);
            RenderLayer.getEntityTranslucent(SERVER_RACK_TEXTURE);
            SpriteIdentifier spriteIdentifier = new SpriteIdentifier(SERVER_RACK_TEXTURE,Identifier.of(Main.namespace,"textures/test"));
            //spriteIdentifier.getVertexConsumer(vertexConsumers,layer);

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(layer);
            MatrixStack.Entry matrix = matrices.peek();

            //TODO: check the installed servers and only render on bays that have servers installed
            renderServer(matrices,vertexConsumer,0);
            renderServer(matrices,vertexConsumer,1);
            renderServer(matrices,vertexConsumer,2);
            renderServer(matrices,vertexConsumer,3);
            renderServer(matrices,vertexConsumer,4);
            renderServer(matrices,vertexConsumer,5);
        }
    }

    /**Renders a server in a slot on the server rack
     * note: this should only be called for the bottom rack
     * @param matrices the transformation matrix for converting the vertexes into world coordinates, note this should already be tuned for any rack rotations
     * @param vertexConsumer the vertexConsumer/renderBuffer to wright to
     * @param slot the rack slot to render to, range between 0-5
     TODO: add additional params for animations and stuff
     */
    void renderServer(MatrixStack matrices, VertexConsumer vertexConsumer,int slot){
        matrices.push();
        //apparently the slots are not evenly spaced!
        switch(slot) {
            case 0 -> matrices.translate(0, 1.0 / 4.6, 0);
            case 1 -> matrices.translate(0, 0.595, 0);
            case 2 -> matrices.translate(0, 4.16 / 4.6, 0);
            case 3 -> matrices.translate(0, 5.6 / 4.6, 0);
            case 4 -> matrices.translate(0, 1.53, 0);
            case 5 -> matrices.translate(0, 8.48 / 4.6, 0);
        }

        MatrixStack.Entry matrix = matrices.peek();
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

        matrices.pop();
    }

    static final int FACE_FRONT = 1;
    static final int FACE_BACK = 2;

    void quadX(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x,float y, float z, float dy, float dz, int color, int facesBits){
        if((facesBits & 1) != 0){
            vertexConsumer
                    .vertex(matrix, x, y, z).color(color).normal(x,y,z).texture(0,0).light(255).overlay(255)
                    .vertex(matrix, x, y+dy, z).color(color).normal(x+dy,y,z).texture(0,1).light(255).overlay(255)
                    .vertex(matrix, x, y+dy, z+dz).color(color).normal(x+dy,y+dy,z).texture(1,1).light(255).overlay(255)
                    .vertex(matrix, x, y, z+dz).color(color).normal(x,y+dy,z).texture(1,0).light(255).overlay(255);
        }
        if((facesBits & 2) != 0){
            vertexConsumer
                    .vertex(matrix, x, y, z).color(color).normal(x,y,z).texture(0,0).light(255).overlay(255)
                    .vertex(matrix, x, y, z+dz).color(color).normal(x+dy,y,z).texture(0,1).light(255).overlay(255)
                    .vertex(matrix, x, y+dy, z+dz).color(color).normal(x+dy,y+dy,z).texture(1,1).light(255).overlay(255)
                    .vertex(matrix, x, y+dy, z).color(color).normal(x,y+dy,z).texture(1,0).light(255).overlay(255);
        }
    }
    void quadY(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x,float y, float z, float dx, float dz, int color, int facesBits){
        if((facesBits & 1) == 1){
            vertexConsumer
                    .vertex(matrix, x, y, z).color(color).normal(x,y,z).texture(0,0).light(255).overlay(255)
                    .vertex(matrix, x+dx, y, z).color(color).normal(x+dx,y,z).texture(0,1).light(255).overlay(255)
                    .vertex(matrix, x+dx, y, z+dz).color(color).normal(x+dx, y, z+dz).texture(1,1).light(255).overlay(255)
                    .vertex(matrix, x, y, z+dz).color(color).normal(x, y, z+dz).texture(1,0).light(255).overlay(255);
        }
        if((facesBits & 2) != 0){
            vertexConsumer
                    .vertex(matrix, x, y, z).color(color).normal(x,y,z).texture(0,0).light(255).overlay(255)
                    .vertex(matrix, x, y, z+dz).color(color).normal(x,y,z+dz).texture(0,1).light(255).overlay(255)
                    .vertex(matrix, x+dx, y, z+dz).color(color).normal(x+dx, y, z+dz).texture(1,1).light(255).overlay(255)
                    .vertex(matrix, x+dx, y, z).color(color).normal(x+dx, y, z).texture(1,0).light(255).overlay(255);
        }
    }
    void quadZ(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float z, float dx, float dy, int color, int facesBits){
        if((facesBits & 1) == 1){
            vertexConsumer
                    .vertex(matrix, x, y, z).color(color).normal(x,y,z).texture(0,0).light(255).overlay(255)
                    .vertex(matrix, x+dx, y, z).color(color).normal(x+dx,y,z).texture(0,1).light(255).overlay(255)
                    .vertex(matrix, x+dx, y+dy, z).color(color).normal(x+dx, y+dy, z).texture(1,1).light(255).overlay(255)
                    .vertex(matrix, x, y+dy, z).color(color).normal(x, y+dy, z).texture(1,0).light(255).overlay(255);
        }
        if((facesBits & 2) != 0){
            vertexConsumer
                    .vertex(matrix, x, y, z).color(color).normal(x,y,z).texture(0,0).light(255).overlay(255)
                    .vertex(matrix, x, y+dy, z).color(color).normal(x,y+dy,z).texture(0,1).light(255).overlay(255)
                    .vertex(matrix, x+dx, y+dy, z).color(color).normal(x+dx, y+dy, z).texture(1,1).light(255).overlay(255)
                    .vertex(matrix, x+dx, y, z).color(color).normal(x+dx, y, z).texture(1,0).light(255).overlay(255);
        }
    }
}

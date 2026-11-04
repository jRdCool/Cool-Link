package com.cbi.coollink.guis;

import com.cbi.coollink.app.SnakeGameApp;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.minecraft.util.Identifier;

public class JunctionBoxGui extends ConduitGUI {


    /*
    todo:
        Draw all ports
        Select start port
        Select End port
        Only tubes will be connectable
        Store connected indexes in an array containing all indexes EX 01000010 <> 00000000
                                                                           S02 <> N00
        Draw lines between connected ports Sequentially colored by lower tube number
        type connections rectification(reject if opposing types are attempted to be connected
        Hover unconnected tube show wires and types
        Hover connected tube highlight the connection/connections
        If wallport is used only tube 13 can be used
     */

    public JunctionBoxGui(){
        super();
    }

    private static int[] colors ={0xFF_FF0000,0xFF_FF6600,0xFF_FFEE00,0xFF_00FF00,0xFF_00FF66,0xFF_00BFB9,0xFF_00FFFF,0xFF_0044FF,0xFF_0000FF,0xFF_6600FF,0xFF_FF00BB,0xFF_CC00FF,0xFF_000000};
    //                                Red        Orange     Yellow       Green    Light green     Cyan     light Blue     Blue      Dark Blue    Purple      Pink        Magenta     Black
    private static int[] dimColors ={0x77_FF0000,0x77_FF6600,0x77_FFEE00,0x77_00FF00,0x77_00FF66,0x77_00BFB9,0x77_00FFFF,0x77_0044FF,0x77_0000FF,0x77_6600FF,0x77_FF00BB,0x77_CC00FF,0x77_000000};
    //                                   Red        Orange     Yellow       Green    Light green     Cyan     light Blue     Blue      Dark Blue    Purple      Pink        Magenta     Black

/*
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) ->{
            ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_16A7FF);
            int cellSize = (int)((phoneHeight-phoneHeight*0.1)/15);
            int start =phoneWidth/2-cellSize*15/2;

            for(int i=0;i<15;i++){
                for(int j=0;j<15;j++){
                    ScreenDrawing.coloredRect(matrices,left+i*cellSize+start,top+j*cellSize,cellSize,cellSize,((i+j)%2==0)?0xFF_00FF00:0xFF_00AA00);
                }
            }

            for (SnakeGameApp.SnakePart s : snake) {
                //ScreenDrawing.coloredRect(matrices,left+pos[0]*cellSize+start,top+pos[1]*cellSize,cellSize,cellSize,0xFF_00A0FF);
                s.draw(matrices,left+start,top,cellSize);
            }
            int[] headPos = snake.get(snake.size()-1).pos();
            //ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start,top+headPos[1]*cellSize,2,2,0xFF_FFFFFF);
            switch(nextDirection){
                case 0 ->{
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+2,top+headPos[1]*cellSize+1,3,4,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+7,top+headPos[1]*cellSize+1,3,4,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+3,top+headPos[1]*cellSize+1,1,3,0xFF_000000);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+8,top+headPos[1]*cellSize+1,1,3,0xFF_000000);
                }
                case 1 ->{
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+1,top+headPos[1]*cellSize+3,4,3,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+1,top+headPos[1]*cellSize+7,4,3,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+1,top+headPos[1]*cellSize+4,3,1,0xFF_000000);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+1,top+headPos[1]*cellSize+8,3,1,0xFF_000000);
                }
                case 2 -> {
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+2,top+headPos[1]*cellSize+7,3,4,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+7,top+headPos[1]*cellSize+7,3,4,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+3,top+headPos[1]*cellSize+8,1,3,0xFF_000000);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+8,top+headPos[1]*cellSize+8,1,3,0xFF_000000);
                }
                case 3 -> {
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+7,top+headPos[1]*cellSize+3,4,3,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+7,top+headPos[1]*cellSize+7,4,3,0xFF_FFFFFF);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+8,top+headPos[1]*cellSize+4,3,1,0xFF_000000);
                    ScreenDrawing.coloredRect(matrices,left+headPos[0]*cellSize+start+8,top+headPos[1]*cellSize+8,3,1,0xFF_000000);
                }
            }

            Identifier apple = Identifier.of("minecraft","textures/item/apple.png");
            for(int[] pos:apples){
                ScreenDrawing.texturedRect(matrices,left+pos[0]*cellSize+start,top+pos[1]*cellSize,cellSize,cellSize,apple,0,0,1,1,0xFF_FFFFFF);
            }
        });
    }


*/


}

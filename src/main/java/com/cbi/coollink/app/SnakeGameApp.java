package com.cbi.coollink.app;

import com.cbi.coollink.terminal.CommandContext;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class SnakeGameApp extends AbstractPhoneApp{
    public static final Identifier appID = Identifier.of("cool-link","snake-app");
    public static final Identifier ICON = Identifier.of("cool-link","textures/gui/app_snake_game.png");

    ArrayList<SnakePart> snake = new ArrayList<>();
    ArrayList<int[]> apples = new ArrayList<>();
    int directionOfTravel,nextDirection;
    int tickNum;
    Random r = new Random();

    WLabel gameOverText = new WLabel(MutableText.of(new PlainTextContent.Literal("Game OVER")).setStyle(Style.EMPTY.withBold(true).withUnderline(true).withColor(0xFFFF0000)));
    boolean gameOver =false,gameOverShown=false;

    public SnakeGameApp(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, CommandContext commandRunner){
        super(appID);//the id of the app
        icon = ICON;
        root=new WPlainPanel();//create the panel witch all widget will sit on
        timeColor=TIME_COLOR_BLACK;//set the color of the clock if necessary
        snake.add(new SnakePart());
        apples.add(new int[]{r.nextInt(0,14),r.nextInt(0,14)});
        apples.add(new int[]{r.nextInt(0,14),r.nextInt(0,14)});
        apples.add(new int[]{r.nextInt(0,14),r.nextInt(0,14)});
        gameOverText.setHorizontalAlignment(HorizontalAlignment.CENTER);
        gameOverText.setVerticalAlignment(VerticalAlignment.CENTER);

    }
    @Override
    public void tick() {
        tickNum++;
        if(!gameOver) {
            if (tickNum % 5 == 0) {
                directionOfTravel=nextDirection;
                move1();
                if(apples.isEmpty()){
                    addApple();
                }
            }
        }else{
            if(!gameOverShown) {
                ((WPlainPanel) root).add(gameOverText, phoneWidth / 2, phoneHeight / 2);
                gameOverShown = true;
            }
        }
    }

//    @Override
//    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
//        return new SnakeGameApp(world,clickedOnBlockEntity);
//    }

    @Override
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

            for (SnakePart s : snake) {
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
    /*directions:
    0 = up
    1 = left
    2 = down
    3 = right

     */
    void move1(){
        SnakePart curPart = snake.get(snake.size()-1);
        int[] curPos = curPart.pos();
        SnakePart newPart;
        int[] newPos;
        switch (directionOfTravel){
            case 0 -> newPart = curPart.newUp();
            case 1 -> newPart = curPart.newLeft();
            case 2 -> newPart = curPart.newDown();
            case 3 -> newPart = curPart.newRight();
            default -> newPart = new SnakePart();
        }
        newPos = newPart.pos();
        if(newPos[0] < 0 || newPos[0]>14 || newPos[1] < 0 || newPos[1]>14){
            gameOver = true;
            return;
        }
        if(snakeAtPos(newPos)){
            gameOver = true;
            return;
        }
        int appleID = isPosApple(newPos);
        snake.add(newPart);

        if(appleID==-1)
            snake.remove(0);
        else {
            apples.remove(appleID);
            addApple();
        }
    }

    int isPosApple(int[] pos){
        for(int i=0;i<apples.size();i++){
            int[] a = apples.get(i);
            if(a[0]==pos[0]&&a[1]==pos[1]){
                return i;
            }
        }
        return -1;
    }

    void addApple(){
        int[] pos = new int[0];
        int iteration =0;
        boolean posValid=false;
        while(!posValid&&iteration<10){
            posValid=true;
            iteration++;
            pos = new int[]{r.nextInt(0,14),r.nextInt(0,14)};
            if (snakeAtPos(pos)) {
                posValid = false;
            }

        }
        if(posValid)
            apples.add(pos);
    }

    boolean snakeAtPos(int[] pos){
        for(SnakePart s:snake){
            int[] sPart = s.pos();
            if ((sPart[0] == pos[0] && sPart[1] == pos[1])) {
                return true;
            }
        }
        return false;
    }


    /*
    265         328        UP
    263         331        LEFT
    264         336        DOWN
    262         333        RIGHT
     */
    @Override
    public void keyPressed(int ch, int keyCode, int modifiers) {
        if(ch == 265){
            if(directionOfTravel!=2)
                nextDirection=0;
        }else if(ch == 263){
            if(directionOfTravel!=3)
               nextDirection=1;;
        }else if(ch ==264){
            if(directionOfTravel!=0)
               nextDirection=2;
        }else if(ch == 262){
            if(directionOfTravel!=1)
               nextDirection=3;
        }
    }

    static class SnakePart{
        private int[] data;
        public SnakePart(){
            data = new int[]{7,7,-1,-1};
        }
        private SnakePart(int x,int y,int in,int out){
            data = new int[]{x,y,in,out};
        }

        public void draw(DrawContext matrices,int left,int top,int celWidth){
            ScreenDrawing.coloredRect(matrices,(int)Math.round(left+celWidth*0.1+celWidth*data[0]),(int)Math.round(top+celWidth*0.1+celWidth*data[1]),(int)Math.round(celWidth*0.8),(int)Math.round(celWidth*0.8),0xFF_00A0FF);
            if(data[2]==0||data[3]==0){//up
                ScreenDrawing.coloredRect(matrices,(int)Math.round(left+celWidth*0.1+celWidth*data[0]),top+celWidth*data[1],(int)Math.round(celWidth*0.8),(int)(celWidth*0.1),0xFF_00A0FF);
            }
            if(data[2]==1||data[3]==1){//left
                ScreenDrawing.coloredRect(matrices,left+celWidth*data[0],(int)Math.round(top+celWidth*0.1+celWidth*data[1]),(int)Math.round(celWidth*0.1),(int)Math.round(celWidth*0.8),0xFF_00A0FF);
            }
            if(data[2]==2||data[3]==2){//down
                ScreenDrawing.coloredRect(matrices,(int)Math.round(left+celWidth*0.1+celWidth*data[0]),(int)Math.round(top+celWidth*0.9+celWidth*data[1]),(int)Math.round(celWidth*0.8),(int)Math.round(celWidth*0.1),0xFF_00A0FF);
            }
            if(data[2]==3||data[3]==3){//right
                ScreenDrawing.coloredRect(matrices,(int)Math.round(left+celWidth*0.9+celWidth*data[0]),(int)Math.round(top+celWidth*0.1+celWidth*data[1]),(int)Math.round(celWidth*0.1),(int)Math.round(celWidth*0.8),0xFF_00A0FF);
            }
        }

        public SnakePart newUp(){
            data[3]=0;
            return new SnakePart(data[0], data[1] - 1, 2, -1);
        }
        public SnakePart newLeft(){
            data[3]=1;
            return new SnakePart(data[0] - 1, data[1], 3, -1);
        }
        public SnakePart newDown(){
            data[3]=2;
            return new SnakePart(data[0], data[1] + 1, 0, -1);
        }

        public SnakePart newRight(){
            data[3]=3;
            return new SnakePart(data[0] + 1, data[1], 1, -1);
        }

        public int[] pos(){
            return new int[]{data[0],data[1]};
        }

    }
}

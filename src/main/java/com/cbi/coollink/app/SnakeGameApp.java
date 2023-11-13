package com.cbi.coollink.app;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;

public class SnakeGameApp extends AbstractPhoneApp{
    static final Identifier appID = new Identifier("cool-link","snake-app");

    ArrayList<int[]> snake = new ArrayList<>();
    ArrayList<int[]> apples = new ArrayList<>();
    int directionOfTravel,nextDirection;
    int tickNum;

    boolean gameOver =false;
    public SnakeGameApp() {//constructor used to create a dummy instance of the class used for app registration
        super(appID);//the id of the app

        //set app icon here

        //set app description here (this will be displayed in the app shop)
        description= Text.of("Snake Game!");

    }

    public SnakeGameApp(World world, BlockEntity clickedOnBlockEntity){
        super(appID);//the id of the app
        root=new WPlainPanel();//create the panel witch all widget will sit on
        timeColor=TIME_COLOR_BLACK;//set the color of the clock if necessary
        snake.add(new int[]{7,7});

    }
    @Override
    public void tick() {
        tickNum++;
        if(!gameOver) {
            if (tickNum % 10 == 0) {
                directionOfTravel=nextDirection;
                move1();
            }
        }
    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
        return new SnakeGameApp(world,clickedOnBlockEntity);
    }

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

            for (int[] pos : snake) {
                ScreenDrawing.coloredRect(matrices,left+pos[0]*cellSize+start,top+pos[1]*cellSize,cellSize,cellSize,0xFF_00A0FF);
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
        int[] curPos = snake.get(snake.size()-1);
        int[] newPos;
        switch (directionOfTravel){
            case 0 -> newPos = new int[]{curPos[0],curPos[1]-1};
            case 1 -> newPos = new int[]{curPos[0]-1,curPos[1]};
            case 2 -> newPos = new int[]{curPos[0],curPos[1]+1};
            case 3 -> newPos = new int[]{curPos[0]+1,curPos[1]};
            default -> newPos = new int[]{0,0};
        }
        if(newPos[0] < 0 || newPos[0]>14 || newPos[1] < 0 || newPos[1]>14){
            gameOver = true;
            return;
        }
        snake.add(newPos);
        snake.remove(0);
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
}

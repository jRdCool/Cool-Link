package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SnakeGameApp extends AbstractPhoneApp{
    static final Identifier appID = new Identifier("cool-link","snake-app");
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


    }
    @Override
    public void tick() {

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
        });
    }
}

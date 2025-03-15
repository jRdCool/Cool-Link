package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ExampleApp extends AbstractPhoneApp{
    public ExampleApp() {//constructor used to create a dummy instance of the class used for app registration
        super(Identifier.of("cool-link","example-app"));//the id of the app

        //set app icon here

        //set app description here (this will be displayed in the app shop)
        description= Text.of("this is a test app\nlets try 2 lines");

    }

    public ExampleApp(World world, BlockEntity clickedOnBlockEntity){
        super(Identifier.of("cool-link","example-app"));//the id of the app
        root=new WPlainPanel();//create the panel witch all widget will sit on
        timeColor=TIME_COLOR_BLACK;//set the color of the clock if necessary
        requestSave=true;//requests the phone to save data

    }

    @Override
    public void tick() {//this function gets called every tick

    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {//this function is called by the phone when attempting to open the app
        Main.LOGGER.info(appData.asString());
        return new ExampleApp(world,clickedOnBlockEntity);//this should return a new instance of this class with all necessary arguments
    }

    @Override
    public void addPainters() {//this function gets called by the phone to set the background of the phone while the app is open
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));//this lambda expression creates a white box as the background of the app. the function texturedRect can slo be used to display images as a part of the background
    }

    @Override
    public NbtCompound saveData(){//NEVER CALL THIS METHOD YOUR SELF. this method is called by the phone whenever this app is open and the phone attempts to save
        NbtCompound nb=new NbtCompound();
        nb.putString("im the key","im the data");
        return nb;
    }

}

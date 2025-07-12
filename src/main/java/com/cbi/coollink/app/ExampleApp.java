package com.cbi.coollink.app;

import com.cbi.coollink.terminal.CommandContext;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ExampleApp extends AbstractPhoneApp{
    public static Identifier ID = Identifier.of("cool-link","example-app");

    public ExampleApp(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, CommandContext commandRunner){
        super(ID);//the id of the app
        root=new WPlainPanel();//create the panel witch all widget will sit on
        timeColor=TIME_COLOR_BLACK;//set the color of the clock if necessary
        requestSave=true;//requests the phone to save data

    }

    @Override
    public void tick() {//this function gets called every tick

    }


    @Override
    public void addPainters() {//this function gets called by the phone to set the background of the phone while the app is open
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));//this lambda expression creates a white box as the background of the app. the function texturedRect can also be used to display images as a part of the background
    }

    @Override
    public NbtCompound saveData(){//NEVER CALL THIS METHOD YOUR SELF. this method is called by the phone whenever this app is open and the phone attempts to save
        NbtCompound nb=new NbtCompound();
        nb.putString("im the key","im the data");
        return nb;
    }

}

package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ExampleApp extends AbstractPhoneApp{
    public static Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"example-app");

    public ExampleApp(Level world, BlockEntity clickedOnBlockEntity, CompoundTag appData, ProgramNetworkInterface networkInterface){
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
    public CompoundTag saveData(){//NEVER CALL THIS METHOD YOUR SELF. this method is called by the phone whenever this app is open and the phone attempts to save
        CompoundTag nb=new CompoundTag();
        nb.putString("im the key","im the data");
        return nb;
    }

}

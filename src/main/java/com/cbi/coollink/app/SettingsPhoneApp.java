package com.cbi.coollink.app;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SettingsPhoneApp extends AbstractPhoneApp{
    /**do not use this constructor to initialize the app
     * only use to get an instance of this app
     */
    private SettingsPhoneApp(){
        icon=new Identifier("cool-link","textures/gui/setting_app_icon.png");
    }
    public SettingsPhoneApp(World world, BlockEntity clickedOnBlockEntity){
        icon=new Identifier("cool-link","textures/gui/setting_app_icon.png");
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;
    }


    @Override
    public void tick() {

    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity) {
        return new SettingsPhoneApp(world,clickedOnBlockEntity);
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));
    }

    public static SettingsPhoneApp getDummyInstance(){
        return new SettingsPhoneApp();
    }
}

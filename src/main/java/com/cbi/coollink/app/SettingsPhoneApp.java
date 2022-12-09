package com.cbi.coollink.app;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SettingsPhoneApp extends AbstractPhoneApp{

    WLabel title=new WLabel(Text.of("Settings"));

    /**do not use this constructor to initialize the app
     * only use to get an instance of this app
     */
    private SettingsPhoneApp(){
        super(new Identifier("cool-link","settings"));
        icon=new Identifier("cool-link","textures/gui/setting_app_icon.png");
    }
    public SettingsPhoneApp(World world, BlockEntity clickedOnBlockEntity){
        super(new Identifier("cool-link","settings"));
        icon=new Identifier("cool-link","textures/gui/setting_app_icon.png");
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WPlainPanel panel = (WPlainPanel)root;
        panel.add(title,phoneWidth/2,5);

    }


    @Override
    public void tick() {

    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
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

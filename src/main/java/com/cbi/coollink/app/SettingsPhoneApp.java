package com.cbi.coollink.app;

import com.cbi.coollink.guis.PhoneGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SettingsPhoneApp extends AbstractRootApp{

    WLabel title=new WLabel(Text.of("Settings"));
    PhoneGui phoneInstance;
    WButton prevBackground,nextBackground;
    WLabel currentBackground,backgroundLabel;

    /**do not use this constructor to initialize the app
     * only use to get an instance of this app
     */
    private SettingsPhoneApp(){
        super(new Identifier("cool-link","settings"));
        icon=new Identifier("cool-link","textures/gui/setting_app_icon.png");
    }
    public SettingsPhoneApp(World world, BlockEntity clickedOnBlockEntity,PhoneGui phone){
        super(new Identifier("cool-link","settings"));
        icon=new Identifier("cool-link","textures/gui/setting_app_icon.png");
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WPlainPanel panel = (WPlainPanel)root;
        panel.add(title,phoneWidth/2,5);
        phoneInstance=phone;
        prevBackground=new WButton(Text.of("<"));
        nextBackground=new WButton(Text.of(">"));
        currentBackground=new WLabel(Text.of(phone.backgroundNumber+""));
        panel.add(prevBackground,25,20);
        panel.add(nextBackground,65,20);
        currentBackground.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        panel.add(currentBackground,45,22);
        backgroundLabel=new WLabel(Text.of("Background"));
        panel.add(backgroundLabel,88,26);

        prevBackground.setOnClick(()->{
           if(phone.backgroundNumber>0){
               phone.backgroundNumber--;
               currentBackground.setText(Text.of(phone.backgroundNumber+""));
           }
        });
        nextBackground.setOnClick(()->{
            if(phone.backgroundNumber<3){
                phone.backgroundNumber++;
                currentBackground.setText(Text.of(phone.backgroundNumber+""));
            }
        });


    }


    @Override
    public void tick() {

    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
        return null;
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));
    }

    public static SettingsPhoneApp getDummyInstance(){
        return new SettingsPhoneApp();
    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, PhoneGui phone) {
        return new SettingsPhoneApp(world,clickedOnBlockEntity,phone);
    }
}

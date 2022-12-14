package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.guis.PhoneGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class AppStore extends AbstractRootApp{
    WScrollBar scrollBar;
    WText descriptions[]=new WText[4];
    WButton installButtons[] =new WButton[4];
    PhoneGui phone;
    public AppStore() {
        super(new Identifier("cool-link","app-store"));
        icon=new Identifier("cool-link","textures/gui/app_shop_icon.png");
    }

    public AppStore(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData,PhoneGui phone) {
        super(new Identifier("cool-link","app-store"));
        this.phone=phone;
        icon=new Identifier("cool-link","textures/gui/app_shop_icon.png");
        root=new WPlainPanel();
        WPlainPanel panel=(WPlainPanel)root;
        WLabel title=new WLabel(Text.of("App Shop"));
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        panel.add(title,phoneWidth/2,3);
        timeColor=TIME_COLOR_BLACK;
        scrollBar = new WScrollBar(Axis.VERTICAL);
        //set this value to 16 + number of apps that don't fit on screen
        scrollBar.setMaxValue(AppRegistry.getApps().length+12);
        panel.add(scrollBar,380,15,20,170);
        for(int i=0;i<4;i++) {
            descriptions[i]=new WText(Text.empty());
            panel.add(descriptions[i],70,12+50*i,phoneWidth-150,40);
            installButtons[i]=new WButton(Text.of("Install"));
            panel.add(installButtons[i],318,13+50*i,60,20);
            int n = i;
            installButtons[i].setOnClick(()->{handleAppInstallation(n);});

        }

    }

    @Override
    public void tick() {
        //Main.LOGGER.info(scrollBar.getValue()+"");
        AbstractPhoneApp[] storeApps =AppRegistry.getApps();
        for(int i=0; i < 4 && i + scrollBar.getValue() < storeApps.length; i++){
            if(storeApps[i+scrollBar.getValue()].description!=null) {
                descriptions[i].setText(storeApps[i + scrollBar.getValue()].description);
            }else {
                descriptions[i].setText(Text.empty());
            }
        }
        for(int i=0;i<4;i++){
            installButtons[i].setEnabled(i + scrollBar.getValue() < storeApps.length);
            if(installButtons[i].isEnabled()){
                AbstractPhoneApp thisApp=storeApps[i+scrollBar.getValue()];
                installButtons[i].setLabel( (phone.apps.contains(thisApp)) ? Text.of("Uninstall") : Text.of("Install"));
            }
        }
    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
        return null;
    }
    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, PhoneGui phone) {
        return new AppStore(world,clickedOnBlockEntity,appData,phone);
    }
    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> {
            ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_EEEEEE);
            ScreenDrawing.coloredRect(matrices,left+30,top+phoneHeight/4-5,phoneWidth-60,1,0XFF_000000);
            ScreenDrawing.coloredRect(matrices,left+30,top+phoneHeight/2-5,phoneWidth-60,1,0XFF_000000);
            ScreenDrawing.coloredRect(matrices,left+30,top+(int)(phoneHeight*3.0/4)-5,phoneWidth-60,1,0XFF_000000);
            AbstractPhoneApp storeApps[]=AppRegistry.getApps();
            for(int i=0; i < 4 && i + scrollBar.getValue() < storeApps.length; i++){
                ScreenDrawing.texturedRect(matrices,left+30,top+5+50*i,35,35,storeApps[i+scrollBar.getValue()].icon,0XFF_FFFFFF);
            }
        });
    }

    /**handles the installation and uninstallation of apps
     *
     * @param buttonNum the button that was clicked
     */
    void handleAppInstallation(int buttonNum){
        AbstractPhoneApp[] storeApps =AppRegistry.getApps();
        AbstractPhoneApp selApp=storeApps[buttonNum+scrollBar.getValue()];
        if(phone.apps.contains(selApp)){
            phone.apps.remove(selApp);//uninstall the app from the phone
            phone.appData.remove(selApp.appId.toString());//remove the apps data from the phone
        }else{
            phone.apps.add(selApp);//install the app to the phone
        }
        requestSave=true;//save nbt data
    }


}

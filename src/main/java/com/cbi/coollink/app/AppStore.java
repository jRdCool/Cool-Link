package com.cbi.coollink.app;

import com.cbi.coollink.guis.PhoneGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class AppStore extends AbstractRootApp{
    WScrollBar scrollBar;
    WText[] descriptions =new WText[4];
    WButton[] installButtons =new WButton[4];
    PhoneGui phone;

    public static final Identifier ID = Identifier.of("cool-link","app-store");
    public static final Identifier ICON = Identifier.of("cool-link","textures/gui/app_shop_icon.png");

    public AppStore(PhoneGui phone) {
        super(ID);
        this.phone=phone;
        icon = ICON;
        root = new WPlainPanel();
        WPlainPanel panel=(WPlainPanel)root;
        WLabel title=new WLabel(Text.of("App Shop"));
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        panel.add(title,phoneWidth/2,3);
        timeColor=TIME_COLOR_BLACK;
        scrollBar = new WScrollBar(Axis.VERTICAL);
        //set this value to 16 + number of apps that don't fit on screen
        scrollBar.setMaxValue(AppRegistry.getAppIds().length+12);
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
        for(int i=0; i < 4 && i + scrollBar.getValue() < AppRegistry.size(); i++){
            if(AppRegistry.getDescription(i+scrollBar.getValue())!=null) {
                descriptions[i].setText(AppRegistry.getDescription(i+scrollBar.getValue()));
            }else {
                descriptions[i].setText(Text.empty());
            }
        }
        for(int i=0;i<4;i++){
            installButtons[i].setEnabled(i + scrollBar.getValue() < AppRegistry.size() && (phone.installedApps.size()<105 || phone.isAppInstalled(AppRegistry.getId(i+scrollBar.getValue()))));
            if(installButtons[i].isEnabled()){
                installButtons[i].setLabel(phone.isAppInstalled(AppRegistry.getId(i+scrollBar.getValue())) ? Text.of("Uninstall") : Text.of("Install"));
            }
        }
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> {
            ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_EEEEEE);
            ScreenDrawing.coloredRect(matrices,left+30,top+phoneHeight/4-5,phoneWidth-60,1,0XFF_000000);
            ScreenDrawing.coloredRect(matrices,left+30,top+phoneHeight/2-5,phoneWidth-60,1,0XFF_000000);
            ScreenDrawing.coloredRect(matrices,left+30,top+(int)(phoneHeight*3.0/4)-5,phoneWidth-60,1,0XFF_000000);
            for(int i=0; i < 4 && i + scrollBar.getValue() < AppRegistry.size(); i++){
                ScreenDrawing.texturedRect(matrices,left+30,top+5+50*i,35,35,AppRegistry.getIcon(i+scrollBar.getValue()),0XFF_FFFFFF);
            }
        });
    }

    /**handles the installation and uninstallation of apps
     *
     * @param buttonNum the button that was clicked
     */
    void handleAppInstallation(int buttonNum){
        Identifier selectedApp=AppRegistry.getId(buttonNum+scrollBar.getValue());
        if(phone.isAppInstalled(selectedApp)){
            phone.uninstallApp(selectedApp);//uninstall the app from the phone
            phone.appData.remove(selectedApp.toString());//remove the apps data from the phone
        }else{
            phone.installApp(selectedApp);//install the app to the phone
        }
        requestSave=true;//save nbt data
    }


}

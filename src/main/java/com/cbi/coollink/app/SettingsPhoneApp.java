package com.cbi.coollink.app;

import com.cbi.coollink.blocks.AIOBlockEntity;
import com.cbi.coollink.guis.PhoneGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SettingsPhoneApp extends AbstractRootApp{

    WLabel title=new WLabel(Text.of("Settings"));
    PhoneGui phoneInstance;
    WButton prevBackground,nextBackground, amPmClock,hour24Clock,setPhoneName,aPSearch;
    WLabel currentBackground,backgroundLabel,clockSetting=new WLabel(Text.of("Clock")),phoneName,mac;
    WTextField phoneNameField;
    WLabel networks= new WLabel(Text.of("Networks"));
    boolean settingPhoneName=false;

    /**do not use this constructor to initialize the app
     * only use to get an instance of this app
     */
    private SettingsPhoneApp(){
        super(Identifier.of("cool-link","settings"));
        icon=Identifier.of("cool-link","textures/gui/setting_app_icon.png");
    }
    public SettingsPhoneApp(World world, BlockEntity clickedOnBlockEntity,PhoneGui phone){
        super(Identifier.of("cool-link","settings"));
        icon=Identifier.of("cool-link","textures/gui/setting_app_icon.png");
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
        amPmClock =new WButton(Text.of("AM/PM"));
        hour24Clock=new WButton(Text.of("24 Hour"));
        panel.add(amPmClock,25,60,60,20);
        panel.add(hour24Clock,90,60,60,20);
        if(phone.clockTimeType== PhoneGui.ClockTimeType.AMPM){
            amPmClock.setEnabled(false);
        }
        if(phone.clockTimeType== PhoneGui.ClockTimeType.HOUR24){
            hour24Clock.setEnabled(false);
        }
        amPmClock.setOnClick(()->{
            phone.clockTimeType= PhoneGui.ClockTimeType.AMPM;
            phone.dtf=DateTimeFormatter.ofPattern("hh:mm a");
            amPmClock.setEnabled(false);
            hour24Clock.setEnabled(true);
        });
        hour24Clock.setOnClick(()->{
            phone.clockTimeType= PhoneGui.ClockTimeType.HOUR24;
            phone.dtf=DateTimeFormatter.ofPattern("HH:mm");
            amPmClock.setEnabled(true);
            hour24Clock.setEnabled(false);
        });
        panel.add(clockSetting,25,50);

        phoneName=new WLabel(Text.of(phone.phoneName));
        setPhoneName=new WButton(Text.of("Set Phone Name"));
        phoneNameField=new WTextField();
        phoneNameField.setText(phone.phoneName);

        panel.add(phoneName,29,96);
        panel.add(setPhoneName,140,90,90,20);
        setPhoneName.setOnClick(()->{
           if(settingPhoneName){
               settingPhoneName=false;
               panel.remove(phoneNameField);
               panel.add(phoneName,29,96);
               if(!phoneNameField.getText().equals("")){
                   phone.phoneName=phoneNameField.getText();
                   phoneName.setText(Text.of(phone.phoneName));
               }
           }else{
               settingPhoneName=true;
               panel.remove(phoneName);
               panel.add(phoneNameField,25,90,110,20);
               phoneNameField.setText(phone.phoneName);

           }
        });
        mac=new WLabel(Text.of("Mac:"));
        aPSearch=new WButton(Text.of("Search For Networks"));
        panel.add(mac,29, 111);
        panel.add(aPSearch,80,126,150,20);
        int playerX,playerY,playerZ;
        ArrayList<String> availNets=new ArrayList<>();

        aPSearch.setOnClick(()->{
            for (int i=-30;i<=30;i++){
                for (int j=-10;j<=10;j++){
                    for (int k=-30;k<=30;k++){
                        BlockEntity target = world.getBlockEntity(new BlockPos((int)Math.floor(phone.playerPosition.x)+i,(int)Math.floor(phone.playerPosition.y)+j,(int)Math.floor(phone.playerPosition.z)+k));
                        if(target instanceof AIOBlockEntity){
                            String lSSID=((AIOBlockEntity) target).ssid;
                            if(availNets.isEmpty()|| !(availNets.contains(lSSID))){
                                availNets.add(lSSID);
                            }
                        }
                    }
                }
            }
        });

        panel.add(networks,265,30);
   /*     WListPanel<String,AIODeviceList> accessPoints=new WListPanel<>(availNets, AIODeviceList::new, configurator);
        accessPoints.setListItemHeight(2*18);
        panel.add(accessPoints,220,40,190,155);
*/

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

package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.Util;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.blocks.networkdevices.AccessPoint;
import com.cbi.coollink.guis.PhoneGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class SettingsPhoneApp extends AbstractRootApp{

    WLabel title=new WLabel(Text.of("Settings"));
    PhoneGui phoneInstance;
    WButton prevBackground,nextBackground, amPmClock,hour24Clock,setPhoneName,aPSearch;
    WLabel currentBackground,backgroundLabel,clockSetting=new WLabel(Text.of("Clock")),phoneName,mac;
    WTextField phoneNameField;
    WLabel networks= new WLabel(Text.of("Networks"));

    WListPanel<DiscoveredNetwork,AIODeviceList> accessPoints;

    World world;

    ArrayList<DiscoveredNetwork> discoveredNetworks = new ArrayList<>();
    boolean settingPhoneName=false;

    boolean searchingForWifi = false;
    int wifiSearchProgress = 0;

    public static final Identifier ID = Identifier.of("cool-link","settings");
    public static final Identifier ICON = Identifier.of("cool-link","textures/gui/setting_app_icon.png");

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
        mac=new WLabel(Text.of("Mac:   "+phone.getMac()));
        aPSearch=new WButton(Text.of("Search For Networks"));
        panel.add(mac,29, 111);
        panel.add(aPSearch,80,126,150,20);



        BiConsumer<DiscoveredNetwork, AIODeviceList> configurator = (DiscoveredNetwork network, AIODeviceList destination) -> {
            if(network.ssid.equals(phone.getWifiSsid())){//the currently connected network is displayed as green
                destination.device.setLabel(Text.literal("§a"+network.ssid));
            }else {
                destination.device.setLabel(Text.literal(network.ssid));
            }
            destination.device.setOnClick(() -> {
               //TODO enter the password and then the connect button here
            });
        };

        panel.add(networks,265,30);
        accessPoints = new WListPanel<>(discoveredNetworks, AIODeviceList::new, configurator);
        accessPoints.setListItemHeight(18);
        panel.add(accessPoints,230,40,170,155);

        aPSearch.setOnClick(()->{
            if(!searchingForWifi){
                searchingForWifi = true;
                discoveredNetworks.clear();
                aPSearch.setEnabled(false);
            }
        });

        this.world = world;
    }


    @Override
    public void tick() {
        if(searchingForWifi){
            final int maxSearchDiameter = 31;
            final int searchPerTick = 200;
            for(int startIndex = wifiSearchProgress; wifiSearchProgress < startIndex + searchPerTick && wifiSearchProgress < maxSearchDiameter*maxSearchDiameter*maxSearchDiameter;wifiSearchProgress++){
                BlockPos searchBlock = Util.getCubePos(wifiSearchProgress, new BlockPos((int)phoneInstance.playerPosition.x,(int)phoneInstance.playerPosition.y,(int)phoneInstance.playerPosition.z));
                BlockEntity worldEntity = world.getBlockEntity(searchBlock);
                if(worldEntity instanceof AccessPoint ap){
                    String networkName = ap.getSsid();
                    if(networkName == null){
                        continue;
                    }
                    boolean alreadyFound = false;
                    for(DiscoveredNetwork net: discoveredNetworks){
                        if(net.ssid.equals(networkName)){
                            alreadyFound = true;
                            break;
                        }
                    }
                    if(alreadyFound){
                        continue;
                    }
                    //TODO also calculate distance to player
                    discoveredNetworks.add(new DiscoveredNetwork(networkName,searchBlock));
                }
            }
            if(wifiSearchProgress >= maxSearchDiameter*maxSearchDiameter*maxSearchDiameter){
                searchingForWifi = false;
                wifiSearchProgress = 0;
                aPSearch.setEnabled(true);
            }
            accessPoints.layout();
        }
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));
    }

    private record DiscoveredNetwork(String ssid, BlockPos accessPoint){}
}

package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.Util;
import com.cbi.coollink.blocks.networkdevices.AccessPoint;
import com.cbi.coollink.guis.PhoneGui;
import com.cbi.coollink.net.ConnectToWifiNetworkRequestPacket;
import com.cbi.coollink.net.mic.WPasswordField;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
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
    WLabel currentBackground,backgroundLabel,clockSetting=new WLabel(Text.of("Clock")),phoneName,mac,connectingStatus;
    WTextField phoneNameField;
    WLabel networks= new WLabel(Text.of("Networks"));

    WListPanel<DiscoveredNetwork,AIODeviceList> accessPoints;

    WPlainPanel connectToNetworkPanel, primaryPanel = new WPlainPanel(), connectingPanel;

    World world;

    private DiscoveredNetwork connectingToNetwork;

    ArrayList<DiscoveredNetwork> discoveredNetworks = new ArrayList<>();
    boolean settingPhoneName=false;
    boolean connectingToWifi = false;
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
        panel.add(primaryPanel,0,0);
        panel.add(title,phoneWidth/2,5);
        phoneInstance=phone;
        prevBackground=new WButton(Text.of("<"));
        nextBackground=new WButton(Text.of(">"));
        currentBackground=new WLabel(Text.of(phone.backgroundNumber+""));
        primaryPanel.add(prevBackground,25,20);
        primaryPanel.add(nextBackground,65,20);
        currentBackground.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        primaryPanel.add(currentBackground,45,22);
        backgroundLabel=new WLabel(Text.of("Background"));
        primaryPanel.add(backgroundLabel,88,26);

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
        primaryPanel.add(amPmClock,25,60,60,20);
        primaryPanel.add(hour24Clock,90,60,60,20);
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
        primaryPanel.add(clockSetting,25,50);

        phoneName=new WLabel(Text.of(phone.phoneName));
        setPhoneName=new WButton(Text.of("Set Phone Name"));
        phoneNameField=new WTextField();
        phoneNameField.setText(phone.phoneName);

        primaryPanel.add(phoneName,29,96);
        primaryPanel.add(setPhoneName,140,90,90,20);
        setPhoneName.setOnClick(()->{
           if(settingPhoneName){
               settingPhoneName=false;
               primaryPanel.remove(phoneNameField);
               primaryPanel.add(phoneName,29,96);
               if(!phoneNameField.getText().isEmpty()){
                   phone.phoneName=phoneNameField.getText();
                   phoneName.setText(Text.of(phone.phoneName));
               }
           }else{
               settingPhoneName=true;
               primaryPanel.remove(phoneName);
               primaryPanel.add(phoneNameField,25,90,110,20);
               phoneNameField.setText(phone.phoneName);

           }
        });
        mac=new WLabel(Text.of("Mac:   "+phone.getMac()));
        aPSearch=new WButton(Text.of("Search For Networks"));
        primaryPanel.add(mac,29, 111);
        primaryPanel.add(aPSearch,80,126,150,20);



        BiConsumer<DiscoveredNetwork, AIODeviceList> configurator = (DiscoveredNetwork network, AIODeviceList destination) -> {
            if(network.ssid.equals(phone.getWifiSsid())){//the currently connected network is displayed as green
                destination.device.setLabel(Text.literal("§a"+network.ssid));
            }else {
                destination.device.setLabel(Text.literal(network.ssid));
            }

            destination.device.setOnClick(() -> {
                connectToNetworkScreen(network,"");
            });
        };

        primaryPanel.add(networks,265,30);
        accessPoints = new WListPanel<>(discoveredNetworks, AIODeviceList::new, configurator);
        accessPoints.setListItemHeight(18);
        primaryPanel.add(accessPoints,230,40,170,155);

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
        if(connectingToWifi){
            if(phoneInstance.isWifiConnectFinished()){
                //chech for errors
                int error = phoneInstance.getWifiConnectError();
                if(error!=0){
                    switch (error) {
                        case 1 ->{
                            //password incorrect, go back to password screen
                            root.remove(connectingPanel);
                            connectToNetworkScreen(connectingToNetwork,"Incorrect Password");
                        }
                        case 2->{
                            //network full, display error
                            connectingStatus.setText(Text.of("§cNetwork Full!"));
                        }
                        //error, display the error if the network is full
                        //or go back to the password page if invalid password
                    }
                }else{
                    //success, go back to the main settings page
                    root.remove(connectingPanel);
                    ((WPlainPanel)root).add(primaryPanel,0,0);
                    primaryPanel.layout();
                }
            }
        }
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));
    }

    private void connectToNetworkScreen(DiscoveredNetwork network,String error){
        root.remove(primaryPanel);
        connectToNetworkPanel = new WPlainPanel();
        ((WPlainPanel)root).add(connectToNetworkPanel,0,0);
        WLabel instruction = new WLabel(Text.of("Enter password for §1§l"+network.ssid()));
        connectToNetworkPanel.add(instruction,100,50);
        WPasswordField passwordField = new WPasswordField(Text.of("admins may be able to see text entered here"));
        passwordField.setMaxLength(96);
        connectToNetworkPanel.add(passwordField,50,85);
        passwordField.setSize(300, 20);
        WToggleButton showPassword = new WToggleButton();
        connectToNetworkPanel.add(showPassword, 355, 85);
        showPassword.setOnToggle(passwordField::setShown);

        //pre-fill password for known network
        for(PhoneGui.WifiNetworkInfo wifi: phoneInstance.getSavedNetworks()){
            if(wifi.ssid().equals(network.ssid)){
                passwordField.setText(wifi.password());
                break;
            }
        }

        WButton connectButton = new WButton(MutableText.of(new PlainTextContent.Literal("Connect")).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        phoneInstance.setWifiConnectFinished(false);
        connectToNetworkPanel.add(connectButton, 180, 120,60,20);

        connectButton.setOnClick(() -> {
           if(passwordField.getText() != null){
               //store the network info assuming the password is correct
                phoneInstance.updateSavedNetwork(new PhoneGui.WifiNetworkInfo(network.ssid(), passwordField.getText(),world.getRegistryKey().getValue(),network.accessPoint().getX(),network.accessPoint().getY(),network.accessPoint().getZ()));
                phoneInstance.apBlockPos = network.accessPoint();

               //send the request to try and connect to the network
               ClientPlayNetworking.send(new ConnectToWifiNetworkRequestPacket(world.getRegistryKey(),network.accessPoint(),passwordField.getText(),phoneInstance.getMac(),phoneInstance.phoneName));
               connectingToNetwork = network;
               connectingToNetworkScreen();
           }
        });

        if(!error.isEmpty()){
            WLabel errorText = new WLabel(Text.of("§c"+error));
            connectToNetworkPanel.add(errorText,192,30);
            errorText.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        }

        connectToNetworkPanel.layout();
    }

    private void connectingToNetworkScreen(){
        root.remove(connectToNetworkPanel);
        connectingPanel = new WPlainPanel();
        ((WPlainPanel)root).add(connectingPanel,0,0);
        connectingStatus = new WLabel(Text.of("Connecting..."));
        connectingPanel.add(connectingStatus,192,85);
        connectingStatus.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        connectingToWifi=true;
    }

    private record DiscoveredNetwork(String ssid, BlockPos accessPoint){}
}

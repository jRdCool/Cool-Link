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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class SettingsPhoneApp extends AbstractRootApp{

    WLabel title=new WLabel(Component.literal("Settings"));
    PhoneGui phoneInstance;
    WButton prevBackground,nextBackground, amPmClock,hour24Clock,setPhoneName,aPSearch;
    WLabel currentBackground,backgroundLabel,clockSetting=new WLabel(Component.literal("Clock")),phoneName,mac,connectingStatus;
    WTextField phoneNameField;
    WLabel networks= new WLabel(Component.literal("Networks"));

    WListPanel<DiscoveredNetwork,AIODeviceList> accessPoints;

    WPlainPanel connectToNetworkPanel, primaryPanel = new WPlainPanel(), connectingPanel;

    Level world;

    private DiscoveredNetwork connectingToNetwork;

    ArrayList<DiscoveredNetwork> discoveredNetworks = new ArrayList<>();
    boolean settingPhoneName=false;
    boolean connectingToWifi = false;
    boolean searchingForWifi = false;
    int wifiSearchProgress = 0;

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"settings");
    public static final Identifier ICON = Identifier.fromNamespaceAndPath(Main.namespace,"textures/gui/setting_app_icon.png");

    public SettingsPhoneApp(Level world, BlockEntity clickedOnBlockEntity, PhoneGui phone){
        super(Identifier.fromNamespaceAndPath("cool-link","settings"));
        icon=Identifier.fromNamespaceAndPath("cool-link","textures/gui/setting_app_icon.png");
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WPlainPanel panel = (WPlainPanel)root;
        panel.add(primaryPanel,0,0);
        panel.add(title,phoneWidth/2,5);
        phoneInstance=phone;
        prevBackground=new WButton(Component.literal("<"));
        nextBackground=new WButton(Component.literal(">"));
        currentBackground=new WLabel(Component.literal(phone.backgroundNumber+""));
        primaryPanel.add(prevBackground,25,20);
        primaryPanel.add(nextBackground,65,20);
        currentBackground.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        primaryPanel.add(currentBackground,45,22);
        backgroundLabel=new WLabel(Component.literal("Background"));
        primaryPanel.add(backgroundLabel,88,26);

        prevBackground.setOnClick(()->{
           if(phone.backgroundNumber>0){
               phone.backgroundNumber--;
               currentBackground.setText(Component.literal(phone.backgroundNumber+""));
           }
        });
        nextBackground.setOnClick(()->{
            if(phone.backgroundNumber<3){
                phone.backgroundNumber++;
                currentBackground.setText(Component.literal(phone.backgroundNumber+""));
            }
        });
        amPmClock =new WButton(Component.literal("AM/PM"));
        hour24Clock=new WButton(Component.literal("24 Hour"));
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

        phoneName=new WLabel(Component.literal(phone.phoneName));
        setPhoneName=new WButton(Component.literal("Set Phone Name"));
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
                   phoneName.setText(Component.literal(phone.phoneName));
               }
           }else{
               settingPhoneName=true;
               primaryPanel.remove(phoneName);
               primaryPanel.add(phoneNameField,25,90,110,20);
               phoneNameField.setText(phone.phoneName);

           }
        });
        mac=new WLabel(Component.literal("Mac:   "+phone.getMac()));
        aPSearch=new WButton(Component.literal("Search For Networks"));
        primaryPanel.add(mac,29, 111);
        primaryPanel.add(aPSearch,80,126,150,20);



        BiConsumer<DiscoveredNetwork, AIODeviceList> configurator = (DiscoveredNetwork network, AIODeviceList destination) -> {
            if(network.ssid.equals(phone.getWifiSsid())){//the currently connected network is displayed as green
                destination.device.setLabel(Component.literal("§a"+network.ssid));
            }else {
                destination.device.setLabel(Component.literal(network.ssid));
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
                            connectingStatus.setText(Component.literal("§cNetwork Full!"));
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
        WLabel instruction = new WLabel(Component.literal("Enter password for §1§l"+network.ssid()));
        connectToNetworkPanel.add(instruction,100,50);
        WPasswordField passwordField = new WPasswordField(Component.literal("admins may be able to see text entered here"));
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

        WButton connectButton = new WButton(MutableComponent.create(new PlainTextContents.LiteralContents("Connect")).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        phoneInstance.setWifiConnectFinished(false);
        connectToNetworkPanel.add(connectButton, 180, 120,60,20);

        connectButton.setOnClick(() -> {
           if(passwordField.getText() != null){
               //store the network info assuming the password is correct
                phoneInstance.updateSavedNetwork(new PhoneGui.WifiNetworkInfo(network.ssid(), passwordField.getText(),world.dimension().identifier(),network.accessPoint().getX(),network.accessPoint().getY(),network.accessPoint().getZ()));
                phoneInstance.apBlockPos = network.accessPoint();

               //send the request to try and connect to the network
               ClientPlayNetworking.send(new ConnectToWifiNetworkRequestPacket(world.dimension(),network.accessPoint(),passwordField.getText(),phoneInstance.getMac(),phoneInstance.phoneName));
               connectingToNetwork = network;
               connectingToNetworkScreen();
           }
        });

        if(!error.isEmpty()){
            WLabel errorText = new WLabel(Component.literal("§c"+error));
            connectToNetworkPanel.add(errorText,192,30);
            errorText.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        }

        WButton forgetNetwork = new WButton(Component.literal("Forget network"));
        connectToNetworkPanel.add(forgetNetwork,170,150,90,20);
        boolean hasNetwork = false;
        for(PhoneGui.WifiNetworkInfo wifi:phoneInstance.getSavedNetworks()){
            if(wifi.ssid().equals(network.ssid())){
                hasNetwork = true;
                break;
            }
        }
        forgetNetwork.setEnabled(hasNetwork);
        forgetNetwork.setOnClick(() -> {
            phoneInstance.updateSavedNetwork(new PhoneGui.WifiNetworkInfo(network.ssid(),"",Identifier.withDefaultNamespace("delete-this"),Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE));
            root.remove(connectToNetworkPanel);
            ((WPlainPanel)root).add(primaryPanel,0,0);
            primaryPanel.layout();
        });

        connectToNetworkPanel.layout();
    }

    private void connectingToNetworkScreen(){
        root.remove(connectToNetworkPanel);
        connectingPanel = new WPlainPanel();
        ((WPlainPanel)root).add(connectingPanel,0,0);
        connectingStatus = new WLabel(Component.literal("Connecting..."));
        connectingPanel.add(connectingStatus,192,85);
        connectingStatus.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        connectingToWifi=true;
    }

    private record DiscoveredNetwork(String ssid, BlockPos accessPoint){}
}

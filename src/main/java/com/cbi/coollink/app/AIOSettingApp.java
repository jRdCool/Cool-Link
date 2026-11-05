package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.net.mic.WPasswordField;
import com.cbi.coollink.net.AioSetAdminPasswordPacket;
import com.cbi.coollink.net.AioSetNetPasswordPacket;
import com.cbi.coollink.net.AioSetSSIDPacket;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiConsumer;

public class AIOSettingApp extends AbstractPhoneApp{

    public Boolean uplinkStatus = false;
    WPasswordField adminPasswordField;
    WToggleButton passwordVisibleButton;
    WLabel title=new WLabel(Component.literal("AIO Link"));
    WLabel errorMsg=new WLabel(Component.literal(""));
    Boolean passAccepted=false;
    WPlainPanel logInPanel=new WPlainPanel();
    WPlainPanel aioSettingsPanel=new WPlainPanel();
    WPasswordField netPassField;
    String netName;
    Level world;
    WTextField sSIDName;
    String netPassL;
    Boolean passVisible = false,settingsScreen=false;
    String hiddenText = "";
    WPlainPanel changeAdminPassPanel=new WPlainPanel();


    public static final Identifier ID = Identifier.fromNamespaceAndPath("cool-link","aio-app");
    public static final Identifier ICON = Identifier.fromNamespaceAndPath("cool-link","textures/gui/aio_app_icon.png");

    public AIOSettingApp(Level world, BlockEntity clickedOnBlockEntity){
        super(ID);
        this.world=world;
        icon = ICON;
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;

        WPlainPanel panel = (WPlainPanel)root;
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        panel.add(title,phoneWidth/2,5);
        if(clickedOnBlockEntity instanceof AIOBlockEntity aio) {
            logInScreen(aio,false);
        }else{
            panel.add(new WLabel(MutableComponent.create(new PlainTextContents.LiteralContents("AIO not detected"))), 100, 100);
        }
    }

//    /**do not use this constructor to initialize the app
//     * only use to get an instance of this app
//     */
//    private AIOSettingApp(){
//        super(Identifier.fromNamespaceAndPath("cool-link","aio-app"));
//        icon=Identifier.of("cool-link","textures/gui/aio_app_icon.png");
//    }
    @Override
    public void tick() {

    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> {
            ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF);
            if(settingsScreen) {
                ScreenDrawing.coloredRect(matrices, left + 95, top + 161, 26, 26, 0xFF_333333);
                if(uplinkStatus) {
                    ScreenDrawing.coloredRect(matrices, left + 98, top + 164, 20, 20, 0xFF_00FF00);
                } else {
                    ScreenDrawing.coloredRect(matrices, left + 98, top + 164, 20, 20, 0xFF_FF0000);
                }
            }
        });

    }

    public static boolean openOnBlockEntity(BlockEntity blockEntity){
        return blockEntity instanceof AIOBlockEntity;
    }

    private void logInScreen(AIOBlockEntity blockA,boolean changePass)
    {
        //Main.LOGGER.info("LOGIN SCREEEN: "+blockA+" change pass: "+changePass);
        ((WPlainPanel)root).add(logInPanel,0,0);
        adminPasswordField = new WPasswordField(MutableComponent.create(new PlainTextContents.LiteralContents("admins may be able to see text entered here")));
        adminPasswordField.setMaxLength(96);
        passwordVisibleButton = new WToggleButton();
        WButton checkPassB = new WButton(MutableComponent.create(new PlainTextContents.LiteralContents("Submit")).setStyle(Style.EMPTY.withColor(0xFFFFFF)));

        logInPanel.add(new WLabel(MutableComponent.create(new PlainTextContents.LiteralContents("enter admin password for this AIO"))), 100, 50);
        logInPanel.add(adminPasswordField, 50, 85);
        adminPasswordField.setSize(300, 20);
        logInPanel.add(passwordVisibleButton, 355, 85);
        passwordVisibleButton.setOnToggle(on -> {
            adminPasswordField.setShown(on);
            //Main.LOGGER.info("on="+on);
        });
        logInPanel.add(checkPassB, 180, 120,40,20);
        //Main.LOGGER.info(blockA.password);
        checkPassB.setOnClick(() -> {
//
            if(blockA.password.equals(adminPasswordField.getText())) {
                passAccepted=true;
                root.remove(logInPanel);

                if(changePass){
                    adminPassChangeScreen(blockA);
                }else{
                    aioSettingsScreen(blockA);
                }
            } else {
                errorMsg.setText(Component.literal("Invalid password"));
            }
            //how about we do not do this here
            //ClientPlayNetworking.send(new AioSetAdminPasswordPacket(blockA.getPos(),adminPasswordField.getText(),world.getRegistryKey()));
//
        });
        errorMsg.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        logInPanel.add(errorMsg,192,105);
    }

    private void aioSettingsScreen(AIOBlockEntity blockA)
    {
        BiConsumer<AIOBlockEntity.ConnectedDevice, AIODeviceList> configurator = (device, destination) -> {
            destination.device.setLabel(Component.literal(device.deviceName()));
            //destination.sprite.setImage(new Identifier("libgui-test:portal1.png"));
        };
        settingsScreen=true;
        if(blockA.ssid==null)
        {netName="Network Name";}
        else{netName=blockA.ssid;}
        if(blockA.netPass==null){
            netPassL="Password Unset";
        }else{
                //Main.LOGGER.info("pass length: "+blockA.netPass.length());
                //Main.LOGGER.info("netPass: "+blockA.netPass);
                hiddenText = "";
                for (int i = 0; i < blockA.netPass.length(); i++) {
                    hiddenText += "*";
                }
                netPassL=hiddenText;


        }

        ((WPlainPanel)root).add(aioSettingsPanel,0,0);
        WButton changeAdminPass=new WButton(Component.literal("Change Admin Password"));
        WLabel connectedDevices=new WLabel(Component.literal("Connected Devices"));
        WLabel uLStatus=new WLabel(Component.literal("Uplink Status:"));
        WLabel sSID=new WLabel(Component.literal("SSID:"));
        WLabel wifiPass=new WLabel(Component.literal("WIFI Password:"));
        netPassField=new WPasswordField(MutableComponent.create(new PlainTextContents.LiteralContents(netPassL)));
        WButton setNetPass=new WButton(Component.literal("set"));
        passwordVisibleButton = new WToggleButton();
        sSIDName=new WTextField(MutableComponent.create(new PlainTextContents.LiteralContents(netName)));
        WButton setSSID=new WButton(Component.literal("set"));
        WLabel mAC1=new WLabel(Component.literal("MAC1: "+blockA.mac1.toString()));
        WLabel mAC2=new WLabel(Component.literal("MAC2: "+blockA.mac2.toString()));



        //ssid line
        aioSettingsPanel.add(sSID,25,30);
        aioSettingsPanel.add(sSIDName,52,24);
        sSIDName.setSize(100,20);
        sSIDName.setMaxLength(15);
        aioSettingsPanel.add(setSSID,157,24);
        setSSID.setSize(25,20);

        //wi-fi password line
        aioSettingsPanel.add(wifiPass,25,60);
        aioSettingsPanel.add(netPassField,102,54);
        netPassField.setSize(100,20);
        netPassField.setMaxLength(30);
        aioSettingsPanel.add(setNetPass,127,77);
        setNetPass.setSize(25,20);
        aioSettingsPanel.add(passwordVisibleButton,156,78);

        //change admin password button
        aioSettingsPanel.add(changeAdminPass,25,104);
        changeAdminPass.setSize(150,20);

        //Device Mac Address
        aioSettingsPanel.add(mAC1,25,135);
        aioSettingsPanel.add(mAC2,25,145);

        //uplink status
        aioSettingsPanel.add(uLStatus,25,170);

        //connected devices panel
        aioSettingsPanel.add(connectedDevices,265,30);
        WListPanel<AIOBlockEntity.ConnectedDevice,AIODeviceList> connectedDevicesPanel=new WListPanel<>(blockA.connectedDevices, AIODeviceList::new, configurator);
        connectedDevicesPanel.setListItemHeight(2*18);
        aioSettingsPanel.add(connectedDevicesPanel,220,40,190,155);



        passwordVisibleButton.setOnToggle(on -> {
            netPassField.setShown(on);
            passVisible=on;
            if(on) {netPassL=blockA.netPass;}
            else {netPassL=hiddenText;}
            netPassField.setSuggestion(Component.literal(netPassL));
        });

        setSSID.setOnClick(() -> {
            netName=sSIDName.getText();
            blockA.ssid=sSIDName.getText();
            ClientPlayNetworking.send(new AioSetSSIDPacket(blockA.getBlockPos(),sSIDName.getText(),world.dimension()));
        });
        setNetPass.setOnClick(() -> {
            netPassL=netPassField.getText();
            blockA.netPass=netPassField.getText();
            Main.LOGGER.info("setting netPass to:"+blockA.netPass);
            if(netPassL == null){
                return;
            }
            Main.LOGGER.info("Setting AIO password to: "+netPassL);
            ClientPlayNetworking.send(new AioSetNetPasswordPacket(blockA.getBlockPos(),netPassL,world.dimension()));
        });

        changeAdminPass.setOnClick(() -> {
            settingsScreen=false;
            root.remove(aioSettingsPanel);
            logInScreen(blockA,true);
        });



    }







    private void adminPassChangeScreen(AIOBlockEntity blockA)
    {
        ((WPlainPanel)root).add(changeAdminPassPanel,0,0);
        adminPasswordField = new WPasswordField(MutableComponent.create(new PlainTextContents.LiteralContents("admins may be able to see text entered here")));
        adminPasswordField.setMaxLength(96);
        changeAdminPassPanel.add(adminPasswordField, 50, 85);
        adminPasswordField.setSize(300, 20);
        changeAdminPassPanel.add(passwordVisibleButton, 355, 85);
        changeAdminPassPanel.add(new WLabel(MutableComponent.create(new PlainTextContents.LiteralContents("set admin password for this AIO"))), 100, 50);
        WButton setAdminPass=new WButton(Component.literal("set"));
        changeAdminPassPanel.add(setAdminPass,180, 120,40,1);

        passwordVisibleButton.setOnToggle(on -> {
            adminPasswordField.setShown(on);
            //Main.LOGGER.info("on="+on);
        });

        setAdminPass.setOnClick(() -> {
            if(adminPasswordField.getText()!=null) {
                blockA.password = adminPasswordField.getText();
                Main.LOGGER.info("setting password to: " + blockA.password);

                ClientPlayNetworking.send(new AioSetAdminPasswordPacket(blockA.getBlockPos(),adminPasswordField.getText(),world.dimension()));

                root.remove(changeAdminPassPanel);
                aioSettingsScreen(blockA);
            }
        });
    }

}
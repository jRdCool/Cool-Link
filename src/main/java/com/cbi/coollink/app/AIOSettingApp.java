package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.AIOBlockEntity;
import com.cbi.coollink.mic.WPasswordField;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.PlainTextContent.Literal;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class AIOSettingApp extends AbstractPhoneApp{

    public Boolean uplinkStatus = false;
    WPasswordField adminPasswordField;
    WToggleButton passwordVisibleButton;
    WLabel title=new WLabel(Text.of("AIO Link"));
    WLabel errorMsg=new WLabel(Text.of(""));
    Boolean passAccepted=false;
    WPlainPanel logInPanel=new WPlainPanel();
    WPlainPanel aioSettingsPanel=new WPlainPanel();
    WPasswordField netPassField;
    String netName;
    World world;
    WTextField sSIDName;
    String netPassL;
    Boolean passVisible = false,settingsScreen=false;
    String hiddenText = "";
    WPlainPanel changeAdminPassPanel=new WPlainPanel();


    public AIOSettingApp(World world, BlockEntity clickedOnBlockEntity){
        super(Identifier.of("cool-link","aio-app"));
        this.world=world;
        icon=Identifier.of("cool-link","textures/gui/aio_app_icon.png");
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;

        WPlainPanel panel = (WPlainPanel)root;
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        panel.add(title,phoneWidth/2,5);
        if(clickedOnBlockEntity instanceof AIOBlockEntity aio) {
            logInScreen(aio,false);
        }else{
            panel.add(new WLabel(MutableText.of(new Literal("AIO not detected"))), 100, 100);
        }
    }

    /**do not use this constructor to initialize the app
     * only use to get an instance of this app
     */
    private AIOSettingApp(){
        super(Identifier.of("cool-link","aio-app"));
        icon=Identifier.of("cool-link","textures/gui/aio_app_icon.png");
    }
    @Override
    public void tick() {

    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
        return new AIOSettingApp(world,clickedOnBlockEntity);
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> {
            ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF);
            if(settingsScreen) {
                ScreenDrawing.coloredRect(matrices, left + 95, top + 161, 26, 26, 0xFF_333333);
                if(uplinkStatus)
                {
                    ScreenDrawing.coloredRect(matrices, left + 98, top + 164, 20, 20, 0xFF_00FF00);
                }
                else
                {
                    ScreenDrawing.coloredRect(matrices, left + 98, top + 164, 20, 20, 0xFF_FF0000);
                }
            }
        });

    }

    public static AIOSettingApp getDummyInstance(){
        return new AIOSettingApp();
    }

    public boolean openOnBlockEntity(BlockEntity blockEntity){
        return blockEntity instanceof AIOBlockEntity;
    }

    private void logInScreen(AIOBlockEntity blockA,Boolean changePass)
    {
        ((WPlainPanel)root).add(logInPanel,0,0);
        adminPasswordField = new WPasswordField(MutableText.of(new Literal("admins may be able to see text entered here")));
        adminPasswordField.setMaxLength(96);
        passwordVisibleButton = new WToggleButton();
        WButton checkPassB = new WButton(MutableText.of(new Literal("Submit")).setStyle(Style.EMPTY.withColor(0xFFFFFF)));

        logInPanel.add(new WLabel(MutableText.of(new Literal("enter admin password for this AIO"))), 100, 50);
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
            if(blockA.password.equals(adminPasswordField.getText()))
            {
                passAccepted=true;
                root.remove(logInPanel);

                if(changePass){adminPassChangeScreen(blockA);}
                else{aioSettingsScreen(blockA);}
            }
            else
            {
                errorMsg.setText(Text.of("Invalid password"));
            }
            //Main.LOGGER.info("setting password to: "+aio.password);
                /*PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(aio.getPos());
                buf.writeString(networkPasswordField.getText());
                buf.writeRegistryKey(world.getRegistryKey());
                ClientPlayNetworking.send(new Identifier("cool-link", "aio-set-password"), buf);*/
//
        });
        errorMsg.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.CENTER);
        logInPanel.add(errorMsg,192,105);
    }

    private void aioSettingsScreen(AIOBlockEntity blockA)
    {
        BiConsumer<String, AIODeviceList> configurator = (String name, AIODeviceList destination) -> {
            destination.device.setLabel(Text.literal(name));
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
        WButton changeAdminPass=new WButton(Text.of("Change Admin Password"));
        WLabel connectedDevices=new WLabel(Text.of("Connected Devices"));
        WLabel uLStatus=new WLabel(Text.of("Uplink Status:"));
        WLabel sSID=new WLabel(Text.of("SSID:"));
        WLabel wifiPass=new WLabel(Text.of("WIFI Password:"));
        netPassField=new WPasswordField(MutableText.of(new Literal(netPassL)));
        WButton setNetPass=new WButton(Text.of("set"));
        passwordVisibleButton = new WToggleButton();
        sSIDName=new WTextField(MutableText.of(new Literal(netName)));
        WButton setSSID=new WButton(Text.of("set"));



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

        //uplink status
        aioSettingsPanel.add(uLStatus,25,170);

        //connected devices panel
        aioSettingsPanel.add(connectedDevices,265,30);
        WListPanel<String,AIODeviceList> connectedDevicesPanel=new WListPanel<>(blockA.connectedDevices, AIODeviceList::new, configurator);
        connectedDevicesPanel.setListItemHeight(2*18);
        aioSettingsPanel.add(connectedDevicesPanel,220,40,190,155);



        passwordVisibleButton.setOnToggle(on -> {
            netPassField.setShown(on);
            passVisible=on;
            if(on) {netPassL=blockA.netPass;}
            else {netPassL=hiddenText;}
            netPassField.setSuggestion(Text.of(netPassL));
        });

        setSSID.setOnClick(() -> {
            netName=sSIDName.getText();
            blockA.ssid=sSIDName.getText();
            //Main.LOGGER.info("setting SSID to: "+blockA.ssid);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockA.getPos());
                buf.writeString(sSIDName.getText());
                buf.writeRegistryKey(world.getRegistryKey());
                //TODO figure the networking out
            Main.LOGGER.error("NETWORKING NOT COMPLETED WIP");
                //ClientPlayNetworking.send(Identifier.of("cool-link", "aio-set-ssid"), buf);
        });
        setNetPass.setOnClick(() -> {
            netPassL=netPassField.getText();
            blockA.netPass=netPassField.getText();
            Main.LOGGER.info("setting netPass to:"+blockA.netPass);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(blockA.getPos());
            buf.writeString(netPassField.getText());
            buf.writeRegistryKey(world.getRegistryKey());
            Main.LOGGER.error("NETWORKING NOT COMPLETED WIP");
            //ClientPlayNetworking.send(Identifier.of("cool-link", "aio-set-net-password"), buf);
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
        adminPasswordField = new WPasswordField(MutableText.of(new Literal("admins may be able to see text entered here")));
        adminPasswordField.setMaxLength(96);
        changeAdminPassPanel.add(adminPasswordField, 50, 85);
        adminPasswordField.setSize(300, 20);
        changeAdminPassPanel.add(passwordVisibleButton, 355, 85);
        changeAdminPassPanel.add(new WLabel(MutableText.of(new Literal("set admin password for this AIO"))), 100, 50);
        WButton setAdminPass=new WButton(Text.of("set"));
        changeAdminPassPanel.add(setAdminPass,180, 120,40,1);

        passwordVisibleButton.setOnToggle(on -> {
            adminPasswordField.setShown(on);
            //Main.LOGGER.info("on="+on);
        });

        setAdminPass.setOnClick(() -> {
            if(adminPasswordField.getText()!=null) {
                blockA.password = adminPasswordField.getText();
                Main.LOGGER.info("setting password to: " + blockA.password);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockA.getPos());
                buf.writeString(adminPasswordField.getText());
                buf.writeRegistryKey(world.getRegistryKey());
                //TODO more networking
                Main.LOGGER.error("NETWORKING NOT COMPLETED WIP");
                //ClientPlayNetworking.send(Identifier.of("cool-link", "aio-set-password"), buf);

                root.remove(changeAdminPassPanel);
                aioSettingsScreen(blockA);
            }
        });
    }

}
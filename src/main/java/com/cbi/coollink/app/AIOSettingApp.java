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
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AIOSettingApp extends AbstractPhoneApp{

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
    Boolean passVisible = false;


    public AIOSettingApp(World world, BlockEntity clickedOnBlockEntity){
        super(new Identifier("cool-link","aio-app"));
        this.world=world;
        icon=new Identifier("cool-link","textures/gui/aio_app_icon.png");
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;

        WPlainPanel panel = (WPlainPanel)root;
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        panel.add(title,phoneWidth/2,5);
        if(clickedOnBlockEntity instanceof AIOBlockEntity aio) {
            logInScreen(aio);
        }else{
            panel.add(new WLabel(MutableText.of(new LiteralTextContent("AIO not detected"))), 100, 100);
        }
    }

    /**do not use this constructor to initialize the app
     * only use to get an instance of this app
     */
    private AIOSettingApp(){
        super(new Identifier("cool-link","aio-app"));
        icon=new Identifier("cool-link","textures/gui/aio_app_icon.png");
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
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));
    }

    public static AIOSettingApp getDummyInstance(){
        return new AIOSettingApp();
    }

    public boolean openOnBlockEntity(BlockEntity blockEntity){
        return blockEntity instanceof AIOBlockEntity;
    }

    private void logInScreen(AIOBlockEntity blockA)
    {
        ((WPlainPanel)root).add(logInPanel,0,0);
        adminPasswordField = new WPasswordField(MutableText.of(new LiteralTextContent("admins may be able to see text entered here")));
        adminPasswordField.setMaxLength(96);
        passwordVisibleButton = new WToggleButton();
        WButton checkPassB = new WButton(MutableText.of(new LiteralTextContent("Submit")).setStyle(Style.EMPTY.withColor(0xFFFFFF)));

        logInPanel.add(new WLabel(MutableText.of(new LiteralTextContent("set password for this AIO"))), 100, 50);
        logInPanel.add(adminPasswordField, 50, 85);
        adminPasswordField.setSize(300, 20);
        logInPanel.add(passwordVisibleButton, 355, 85);
        passwordVisibleButton.setOnToggle(on -> {
            adminPasswordField.setShown(on);
            Main.LOGGER.info("on="+on);
        });
        logInPanel.add(checkPassB, 180, 120,40,1);
        //Main.LOGGER.info(blockA.password);
        checkPassB.setOnClick(() -> {
//
            if(blockA.password.equals(adminPasswordField.getText()))
            {
                passAccepted=true;
                root.remove(logInPanel);
                aioSettingsScreen(blockA);
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

        if(blockA.ssid==null)
        {netName="Network Name";}
        else{netName=blockA.ssid;}
        if(blockA.netPass==null){
            netPassL="Password Unset";
        }else{
            if(passVisible)
            {
                netPassL=blockA.netPass;
            }
            else {
                Main.LOGGER.info("pass length: "+blockA.netPass.length());
                Main.LOGGER.info("netPass: "+blockA.netPass);
                String hiddenText = "";
                for (int i = 0; i < blockA.netPass.length(); i++) {
                    hiddenText += "*";
                }
                netPassL=hiddenText;
            }

        }

        ((WPlainPanel)root).add(aioSettingsPanel,0,0);
        WButton changeAdminPass=new WButton();
        WLabel connectedDevices=new WLabel(Text.of("Connected Devices"));
        WLabel uLStatus=new WLabel(Text.of("Uplink Status:"));
        WLabel sSID=new WLabel(Text.of("SSID:"));
        WLabel wifiPass=new WLabel(Text.of("WIFI Password:"));
        netPassField=new WPasswordField(MutableText.of(new LiteralTextContent(netPassL)));
        WButton setNetPass=new WButton(Text.of("set"));
        passwordVisibleButton = new WToggleButton();
        sSIDName=new WTextField(MutableText.of(new LiteralTextContent(netName)));
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

        passwordVisibleButton.setOnToggle(on -> {
            netPassField.setShown(on);
            passVisible=on;
        });


        setSSID.setOnClick(() -> {
            netName=sSIDName.getText();
            blockA.ssid=sSIDName.getText();
            //Main.LOGGER.info("setting SSID to: "+blockA.ssid);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockA.getPos());
                buf.writeString(sSIDName.getText());
                buf.writeRegistryKey(world.getRegistryKey());
                ClientPlayNetworking.send(new Identifier("cool-link", "aio-set-ssid"), buf);
        });
        setNetPass.setOnClick(() -> {
            netPassL=netPassField.getText();
            blockA.netPass=netPassField.getText();
            Main.LOGGER.info("setting netPass to:"+blockA.netPass);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(blockA.getPos());
            buf.writeString(netPassField.getText());
            buf.writeRegistryKey(world.getRegistryKey());
            ClientPlayNetworking.send(new Identifier("cool-link", "aio-set-net-password"), buf);
        });

    }

}
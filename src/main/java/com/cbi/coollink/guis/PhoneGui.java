package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import com.cbi.coollink.app.AbstractPhoneApp;
import com.cbi.coollink.app.SettingsPhoneApp;
import com.cbi.coollink.blocks.AIOBlockEntity;
import com.cbi.coollink.items.SmartPhone;
import com.cbi.coollink.mic.WPasswordField;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PhoneGui extends LightweightGuiDescription {

    WPlainPanel root,topPanel,appPanel;
    WLabel time;
    WPasswordField networkPasswordField;
    WToggleButton passwordVisibleButton;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
    AbstractPhoneApp currentApp;
    World world;
    BlockEntity clickedOnBLockEntity;
    int top=0,left=0;
    SmartPhone phone;

    WButton homeButton;


    public PhoneGui(SmartPhone phone, World world, BlockEntity clickedOnBLockEntity) {
        this.world=world;
        this.clickedOnBLockEntity=clickedOnBLockEntity;
        this.phone=phone;
        root = new WPlainPanel();//.setBackgroundPainter(new BackgroundPainter());
        setRootPanel(root);
        topPanel=new WPlainPanel();
        topPanel.setSize(400,200);
        root.setSize(400, 200);
        appPanel=new WPlainPanel();
        root.add(appPanel,0,0);
        appPanel.setSize(400,200);

        //currentApp= new SettingsPhoneApp(world,clickedOnBLockEntity);
        //root.add(currentApp.getPanel(),0,0);

        //networkPasswordField = new WPasswordField(MutableText.of(new LiteralTextContent("admins may be able to see text entered here")));
        //networkPasswordField.setMaxLength(96);
        //passwordVisibleButton = new WToggleButton();
        //WButton tmpb = new WButton();

        //if (onAIO) {
        //    root.add(new WLabel(MutableText.of(new LiteralTextContent("clicked on an AIO"))), 2, 2);
        //    root.add(networkPasswordField, 50, 85);
        //    networkPasswordField.setSize(300, 20);
        //    root.add(passwordVisibleButton, 355, 85);
        //    passwordVisibleButton.setOnToggle(on -> {
        //        networkPasswordField.setShown(on);
        //    });
        //    root.add(tmpb, 200, 120);
        //    AIOBlockEntity aio = (AIOBlockEntity)phone.usedBlockEntity;
        //    Main.LOGGER.info(aio.password);
        //    tmpb.setOnClick(() -> {
//
        //        //Main.LOGGER.info("setting password to: "+aio.password);
        //        PacketByteBuf buf = PacketByteBufs.create();
        //        buf.writeBlockPos(aio.getPos());
        //        buf.writeString(networkPasswordField.getText());
        //        buf.writeRegistryKey(world.getRegistryKey());
        //        ClientPlayNetworking.send(new Identifier("cool-link","aio-set-password"), buf);
//
        //    });
        //}
        homeButton=new WButton();
        topPanel.add(homeButton,190,180);

        homeButton.setOnClick(()->{
            if(currentApp!=null) {
                root.remove(currentApp.getPanel());
                root.add(appPanel,0,0);
            }
            currentApp=null;
        });


        time = new WLabel(MutableText.of(new LiteralTextContent(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        topPanel.add(time, (int) (400 * 0.89), (int) (250 * 0.02));
        root.add(topPanel,0,0,400,200);


    }

    /**
     * sets tha background of the GUI
     */
    @Override
    public void addPainters() {
        if (this.rootPanel != null && !fullscreen) {
            this.rootPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                this.top=top;
                this.left=left;
                int bezel =6;
                //sets the background to a textures                                                                                                                            UVs go form 0 to 1 indicating where on the image to pull from
                //ScreenDrawing.texturedRect(matrices, left - 4, top - 4, panel.getWidth() + 2, panel.getHeight() + 2, new Identifier("cool-link", "textures/gui/phone_background.png"), 0, 0, 1, 1, 0xFF_FFFFFF);
                ScreenDrawing.coloredRect(matrices,left-bezel,top-bezel,panel.getWidth()+2*bezel,panel.getHeight()+2*bezel,0xFF007BAB);
                ScreenDrawing.texturedRect(matrices,left,top,panel.getWidth(),panel.getHeight(),new Identifier("cool-link", "textures/gui/phone_background_1.png"),0,0,1,1,0xFF_FFFFFF);

                if(currentApp!=null){
                    currentApp.addPainters();
                }

            });
            appPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                for(int i=0;i<phone.apps.size();i++){
                    ScreenDrawing.texturedRect(matrices,left+20+25*i,top+20,20,20,phone.apps.get(i).icon,0,0,1,1,0xFF_FFFFFF);
                }
            });
            topPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                ScreenDrawing.texturedRect(matrices,left,top+panel.getHeight()/4,panel.getWidth()/24, panel.getHeight()/2,new Identifier("cool-link", "textures/gui/noch.png"),0,0,1,1,0xFF_FFFFFF);
            });
        }
    }

    public void tick() {
        //Main.LOGGER.info("ticked");
        time.setText(MutableText.of(new LiteralTextContent(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor((currentApp==null)?0xFFFFFF:currentApp.timeColor)));
    }

    /**
     *
     * @param app the app to open. note: this instance is only used to create a new instance of this app
     * @return
     */
    public PhoneGui openApp(AbstractPhoneApp app){
        if(currentApp!=null){
            root.remove(currentApp.getPanel());
            root.remove(topPanel);
        }else{
            root.remove(appPanel);
        }
        currentApp=app.init(world,clickedOnBLockEntity);
        root.add(currentApp.getPanel(),0,0,400,200);
        root.add(topPanel,0,0,400,200);
        return this;
    }

    public void mouseClicked(double mouseX,double mouseY){
        mouseX-=left;//adjust the mouse pos to be centered around the top corner of the root panel
        mouseY-=top;
        //left+20+25*i,top+20,20,20
        if(currentApp==null) {
            for (int i = 0; i < phone.apps.size(); i++) {
                if (mouseX >= 20 + 25 * i && mouseX <= 20 + 25 * i + 20 && mouseY >= 20 && mouseY <= 20 + 20) {
                    openApp(phone.apps.get(i));
                }
            }
        }

    }


}

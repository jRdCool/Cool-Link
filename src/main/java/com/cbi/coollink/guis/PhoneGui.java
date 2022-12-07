package com.cbi.coollink.guis;

import com.cbi.coollink.app.AbstractPhoneApp;
import com.cbi.coollink.items.SmartPhone;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PhoneGui extends LightweightGuiDescription {

    WPlainPanel root, notchAndTimePanel,appPanel,homeButtonPanel;
    WLabel time;

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
        notchAndTimePanel =new WPlainPanel();
        homeButtonPanel=new WPlainPanel();
        appPanel=new WPlainPanel();
        notchAndTimePanel.setSize(400,200);
        root.setSize(400, 200);

        root.add(appPanel,0,0);
        appPanel.setSize(400,200);

        homeButton=new WButton();
        homeButtonPanel.add(homeButton,0,0,20,20);

        homeButton.setOnClick(()->{
            if(currentApp!=null) {
                root.remove(currentApp.getPanel());
                root.add(appPanel,0,0);
                root.remove(homeButtonPanel);
            }
            currentApp=null;
        });


        time = new WLabel(MutableText.of(new LiteralTextContent(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        notchAndTimePanel.add(time, (int) (400 * 0.89), (int) (250 * 0.02));
        root.add(notchAndTimePanel,0,0,1,1);
        //root.add(homeButtonPanel,190,180,20,20);

        //open a specific app based on the block that was clicked on
        if(clickedOnBLockEntity!=null)
            for(int i=0;i<phone.apps.size();i++) {
                if(phone.apps.get(i).openOnBlockEntity(clickedOnBLockEntity)){
                    openApp(phone.apps.get(i).init(world,clickedOnBLockEntity));
                    break;
                }
            }


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

                ScreenDrawing.coloredRect(matrices,left-bezel,top-bezel,panel.getWidth()+2*bezel,panel.getHeight()+2*bezel,0xFF007BAB);
                ScreenDrawing.texturedRect(matrices,left,top,panel.getWidth(),panel.getHeight(),new Identifier("cool-link", "textures/gui/phone_background_1.png"),0,0,1,1,0xFF_FFFFFF);

                if(currentApp!=null){
                    currentApp.addPainters();
                }

            });
            //paint the app panel
            appPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                for(int i=0;i<phone.apps.size();i++){
                    ScreenDrawing.texturedRect(matrices,left+20+25*i,top+20,20,20,phone.apps.get(i).icon,0,0,1,1,0xFF_FFFFFF);
                }
            });

            //pain the notch to the screen
            notchAndTimePanel.setBackgroundPainter((matrices, left, top, panel) -> {
                ScreenDrawing.texturedRect(matrices,left,top+50,17, 100,new Identifier("cool-link", "textures/gui/noch.png"),0,0,1,1,0xFF_FFFFFF);
            });
        }
    }

    public void tick() {
        //Main.LOGGER.info("ticked");
        time.setText(MutableText.of(new LiteralTextContent(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor((currentApp==null)?0xFFFFFF:currentApp.timeColor)));
        if(currentApp!=null){
            currentApp.tick();
        }
    }

    /**
     *
     * @param app the app to open. note: this instance is only used to create a new instance of this app
     * @return
     */
    public PhoneGui openApp(AbstractPhoneApp app){
        if(currentApp!=null){
            root.remove(currentApp.getPanel());
            root.remove(notchAndTimePanel);
            root.remove(homeButtonPanel);
        }else{
            root.remove(appPanel);
        }
        currentApp=app.init(world,clickedOnBLockEntity);
        root.add(currentApp.getPanel(),0,0,400,200);
        root.add(notchAndTimePanel,0,0,0,0);
        root.add(homeButtonPanel,190,180,20,20);
        currentApp.getPanel().setHost(this);
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

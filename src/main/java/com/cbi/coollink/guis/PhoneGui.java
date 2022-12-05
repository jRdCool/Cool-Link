package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.AIOBlockEntity;
import com.cbi.coollink.items.SmartPhone;
import com.cbi.coollink.mic.WPasswordField;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PhoneGui extends LightweightGuiDescription {
    boolean onAIO;
    WPlainPanel root;
    WLabel time;
    WPasswordField networkPasswordField;
    WToggleButton passwordVisibleButton;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");


    public PhoneGui(SmartPhone phone, World world) {
        onAIO = phone.clickedOnAIO;
        MutableText netButtonLabel = MutableText.of(new LiteralTextContent("Network Router"));
        root = new WPlainPanel();//.setBackgroundPainter(new BackgroundPainter());
        setRootPanel(root);
        root.setSize(400, 200);
        WButton accessPointButton = new WButton().setIcon(new TextureIcon(new Identifier("cool-link", "textures/icon/cbi.png")));
        WLabel label = new WLabel(netButtonLabel);
        root.add(accessPointButton, 2, 1);
        root.add(label, 1, 3);

        networkPasswordField = new WPasswordField(MutableText.of(new LiteralTextContent("admins may be able to see text entered here")));
        networkPasswordField.setMaxLength(96);
        passwordVisibleButton = new WToggleButton();
        WButton tmpb = new WButton();

        if (onAIO) {
            root.add(new WLabel(MutableText.of(new LiteralTextContent("clicked on an AIO"))), 2, 2);
            root.add(networkPasswordField, 50, 85);
            networkPasswordField.setSize(300, 20);
            root.add(passwordVisibleButton, 355, 85);
            passwordVisibleButton.setOnToggle(on -> {
                networkPasswordField.setShown(on);
            });
            root.add(tmpb, 200, 120);

            tmpb.setOnClick(() -> {
                AIOBlockEntity aio = (AIOBlockEntity)phone.usedBlockEntity;
                //Main.LOGGER.info("setting password to: "+aio.password);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(aio.getPos());
                buf.writeString(networkPasswordField.getText());
                buf.writeRegistryKey(world.getRegistryKey());
                ClientPlayNetworking.send(new Identifier("cool-link","aio-set-password"), buf);

            });
        }

        time = new WLabel(MutableText.of(new LiteralTextContent(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        root.add(time, (int) (400 * 0.86), (int) (250 * 0.02));


    }

    /**
     * sets tha background of the GUI
     */
    @Override
    public void addPainters() {
        if (this.rootPanel != null && !fullscreen) {
            this.rootPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                //sets the background to a textures                                                                                                                            UVs go form 0 to 1 indicating where on the image to pull from
                ScreenDrawing.texturedRect(matrices, left - 4, top - 4, panel.getWidth() + 2, panel.getHeight() + 2, new Identifier("cool-link", "textures/gui/phone_background.png"), 0, 0, 1, 1, 0xFF_FFFFFF);
                //ScreenDrawing.coloredRect(matrices,left-4,top-4,panel.getWidth()+2,panel.getHeight()+2,0x007BAB);
            });
        }
    }

    public void tick() {
        //Main.LOGGER.info("ticked");
        time.setText(MutableText.of(new LiteralTextContent(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
    }




}

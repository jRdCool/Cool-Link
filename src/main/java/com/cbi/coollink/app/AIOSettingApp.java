package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.AIOBlockEntity;
import com.cbi.coollink.mic.WPasswordField;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AIOSettingApp extends AbstractPhoneApp{

    WPasswordField networkPasswordField;
    WToggleButton passwordVisibleButton;



    public AIOSettingApp(World world, BlockEntity clickedOnBlockEntity){
        icon=new Identifier("cool-link","textures/gui/aio_app_icon.png");
        root=new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;

        WPlainPanel panel = (WPlainPanel)root;
        if(clickedOnBlockEntity instanceof AIOBlockEntity aio) {
            networkPasswordField = new WPasswordField(MutableText.of(new LiteralTextContent("admins may be able to see text entered here")));
            networkPasswordField.setMaxLength(96);
            passwordVisibleButton = new WToggleButton();
            WButton tmpb = new WButton();

            panel.add(new WLabel(MutableText.of(new LiteralTextContent("set password for this AIO"))), 100, 50);
            panel.add(networkPasswordField, 50, 85);
            networkPasswordField.setSize(300, 20);
            panel.add(passwordVisibleButton, 355, 85);
            passwordVisibleButton.setOnToggle(on -> networkPasswordField.setShown(on));
            panel.add(tmpb, 200, 120);
            Main.LOGGER.info(aio.password);
            tmpb.setOnClick(() -> {
//
                //Main.LOGGER.info("setting password to: "+aio.password);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(aio.getPos());
                buf.writeString(networkPasswordField.getText());
                buf.writeRegistryKey(world.getRegistryKey());
                ClientPlayNetworking.send(new Identifier("cool-link", "aio-set-password"), buf);
//
            });
        }else{
            panel.add(new WLabel(MutableText.of(new LiteralTextContent("AIO not detected"))), 100, 100);
        }
    }

    private AIOSettingApp(){
        icon=new Identifier("cool-link","textures/gui/aio_app_icon.png");
    }
    @Override
    public void tick() {

    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity) {
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
}

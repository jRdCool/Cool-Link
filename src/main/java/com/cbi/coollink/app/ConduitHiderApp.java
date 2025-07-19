package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.net.UpdateConduitBlockCover;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ConduitHiderApp extends AbstractPhoneApp{

    public static final Identifier ID = Identifier.of(Main.namespace,"hide_those_conduits");

    public ConduitHiderApp(World world, BlockEntity blockEntity, NbtCompound appData, ProgramNetworkInterface networkInterface) {
        super(ID);
        root = new WPlainPanel();
        root.setSize(phoneWidth,phoneHeight);
        timeColor=TIME_COLOR_BLACK;

        WPlainPanel panel = (WPlainPanel)root;
        if(blockEntity != null){
            WTextField input = new WTextField();
            input.setMaxLength(50);
            panel.add(input, 50,50,200,20);
            WButton submit = new WButton(Text.of("Submit"));
            panel.add(submit,50,100,200,20);
            submit.setOnClick(() -> {
                try {
                    String inputText = input.getText();
                    BlockState outState;
                    if (!inputText.isEmpty()) {
                        Identifier blockId = Identifier.of(inputText);
                        outState = Registries.BLOCK.get(blockId).getDefaultState();
                    } else {
                        outState = null;
                    }

                    if(outState == null){
                        outState = Registries.BLOCK.get(Identifier.of("air")).getDefaultState();
                    }
                    ClientPlayNetworking.send(new UpdateConduitBlockCover(blockEntity.getPos(),outState,world.getRegistryKey()));
                }catch (Exception e){
                    Main.LOGGER.error("error trying to set block state ",e);
                }
            });
        }else {
            WLabel ohNo = new WLabel(Text.of("Oh no you did no click on a conduit!"));
            panel.add(ohNo,phoneWidth/4,phoneHeight/2);
        }
    }

    /**
     * this function is called by the phone every tick
     */
    @Override
    public void tick() {

    }

    /**
     * used to set what is painted by the app
     */
    @Override
    public void addPainters() {
            root.setBackgroundPainter((matrices, left, top, panel) -> {
                ScreenDrawing.coloredRect(matrices, left, top, phoneWidth, phoneHeight, 0xFF_FFFFFF);
            });
    }

    public static boolean openOnBlockEntity(BlockEntity blockEntity){
        return blockEntity instanceof ConduitBlockEntity;
    }
}

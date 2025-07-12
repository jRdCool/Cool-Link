package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.terminal.CommandContext;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TerminalPhoneApp extends AbstractPhoneApp{

    public static final Identifier ID = Identifier.of(Main.namespace,"terminal_phone");
    public static final Identifier ICON = Identifier.of(Main.namespace,"textures/gui/terminal.png");

    WTextField inputBox;
    WButton executeButton;
    CommandContext commandContext;
    WLabel title;
    public TerminalPhoneApp(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, CommandContext commandRunner) {
        super(ID);
        root=new WPlainPanel();//create the panel witch all widget will sit on
        timeColor=TIME_COLOR_WHITE;//set the color of the clock if necessary
        icon = ICON;
        inputBox = new WTextField();
        WPlainPanel panel = (WPlainPanel)root;
        panel.add(inputBox,40,155,350,20);
        inputBox.setMaxLength(1024);
        this.commandContext = commandRunner;
        executeButton = new WButton(Text.of("Execute"));
        panel.add(executeButton,250,180,80,20);
        executeButton.setOnClick(() ->{
           commandContext.executeCommand(inputBox.getText());
           inputBox.setText("");
           inputBox.requestFocus();
        });
        panel.add(commandContext.getTextOutput(),20,15,375,135);
        title = new WLabel(Text.of("Terminal"));
        title.setColor(0xFF_FFFFFF);
        title.setDarkmodeColor(0xFF_FFFFFF);
        panel.add(title,180,3);

    }

    /**
     * this function is called by the phone every tick
     */
    @Override
    public void tick() {
        commandContext.tick();
        executeButton.setEnabled(!commandContext.commandExecuting());
    }

    /**
     * used to set what is painted by the app
     */
    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> {
            ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_000000);
        });
    }
}

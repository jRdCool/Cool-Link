package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.terminal.CommandLineContext;
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
import org.lwjgl.glfw.GLFW;

public class TerminalPhoneApp extends AbstractPhoneApp{

    public static final Identifier ID = Identifier.of(Main.namespace,"terminal_phone");
    public static final Identifier ICON = Identifier.of(Main.namespace,"textures/gui/terminal.png");

    WTextField inputBox;
    WButton executeButton;
    CommandLineContext commandLineContext;
    WLabel title;
    public TerminalPhoneApp(World ignoredWorld, BlockEntity ignoredClickedOnBlockEntity, NbtCompound ignoredAppData, CommandLineContext commandRunner) {
        super(ID);
        root=new WPlainPanel();//create the panel witch all widget will sit on
        timeColor=TIME_COLOR_WHITE;//set the color of the clock if necessary
        icon = ICON;
        inputBox = new WTextField();
        WPlainPanel panel = (WPlainPanel)root;
        panel.add(inputBox,40,155,350,20);
        inputBox.setMaxLength(1024);
        this.commandLineContext = commandRunner;
        executeButton = new WButton(Text.of("Execute"));
        panel.add(executeButton,250,180,80,20);
        executeButton.setOnClick(this::executeCommand);
        panel.add(commandLineContext.getTextOutput(),20,15,375,135);
        title = new WLabel(Text.of("Terminal"));
        title.setColor(0xFF_FFFFFF);
        title.setDarkmodeColor(0xFF_FFFFFF);
        panel.add(title,180,3);
    }

    @Override
    public void keyPressed(int ch, int keyCode, int modifiers) {
        if(ch == GLFW.GLFW_KEY_ENTER){
            executeCommand();
        }
    }

    /**
     * this function is called by the phone every tick
     */
    @Override
    public void tick() {
        commandLineContext.tick();
        executeButton.setEnabled(!commandLineContext.commandExecuting());
    }

    /**
     * used to set what is painted by the app
     */
    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_000000));
    }

    void executeCommand(){
        String command = inputBox.getText().trim();
        if(!commandLineContext.commandExecuting() && !command.isEmpty()) {
            commandLineContext.executeCommand(command);
            inputBox.setText("");
        }
        inputBox.requestFocus();
    }
}

package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.PhoneCommandLineContext;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.sdl.SDLKeycode;

import java.util.ArrayList;

public class TerminalPhoneApp extends AbstractPhoneApp{

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"terminal_phone");
    public static final Identifier ICON = Identifier.fromNamespaceAndPath(Main.namespace,"textures/gui/terminal.png");

    WTextField inputBox;
    WButton executeButton, terminateButton;
    PhoneCommandLineContext commandLineContext;
    WLabel title;
    ProgramNetworkInterface networkInterface;
    ArrayList<String> commandHistory = new ArrayList<>();

    int histroyLocation=0;
    public TerminalPhoneApp(Level ignoredWorld, BlockEntity ignoredClickedOnBlockEntity, CompoundTag appData, ProgramNetworkInterface networkInterface) {
        super(ID);
        PhoneCommandLineContext commandRunner = new PhoneCommandLineContext(appData);
        root=new WPlainPanel();//create the panel witch all widget will sit on
        timeColor=TIME_COLOR_WHITE;//set the color of the clock if necessary
        icon = ICON;
        inputBox = new WTextField();
        WPlainPanel panel = (WPlainPanel)root;
        panel.add(inputBox,40,155,350,20);
        inputBox.setMaxLength(1024);
        this.commandLineContext = commandRunner;
        executeButton = new WButton(Component.literal("Execute"));
        panel.add(executeButton,250,180,80,20);
        executeButton.setOnClick(this::executeCommand);
        panel.add(commandLineContext.getTextOutput(),20,15,370,135);
        title = new WLabel(Component.literal("Terminal"));
        title.setColor(0xFF_FFFFFF);
        title.setDarkmodeColor(0xFF_FFFFFF);
        panel.add(title,180,3);
        terminateButton = new WButton(Component.literal("Terminate"));
        panel.add(terminateButton,70,180,80,20);
        terminateButton.setOnClick(() -> commandLineContext.terminateRunningProgram());
        this.networkInterface = networkInterface;

    }

    @Override
    public void keyPressed(int ch, int keyCode, int modifiers) {

        //the SDL constants work for the keycode not the ch
        if(ch == 40 || ch == 88){ // enter key => execute command
            executeCommand();
        } else if(ch == 82){ //up key => previous command
            if(histroyLocation > 0){
                histroyLocation --;
                inputBox.setText(commandHistory.get(histroyLocation));
            }
        } else if(ch == 81){ // down key => next command
            if(histroyLocation < commandHistory.size()){
                histroyLocation++;
                if(histroyLocation == commandHistory.size()){
                    inputBox.setText("");
                } else {
                    inputBox.setText(commandHistory.get(histroyLocation));
                }
            }
        } else {
            Main.LOGGER.info("KEy pressed: "+ch+" "+keyCode+" "+modifiers);
        }
    }

    /**
     * this function is called by the phone every tick
     */
    @Override
    public void tick() {
        commandLineContext.tick();
        executeButton.setEnabled(!commandLineContext.commandExecuting());
        terminateButton.setEnabled(commandLineContext.commandExecuting());
        if(commandLineContext.requestSave()){
            requestSave = true;
        }
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
            commandLineContext.executeCommand(command,networkInterface);
            if(commandHistory.isEmpty() || !command.equals(commandHistory.getLast())) {
                commandHistory.add(command);
            }
            inputBox.setText("");
            if(commandHistory.size() > 500){
                commandHistory.removeFirst();
            }
            histroyLocation = commandHistory.size();
        }
        inputBox.requestFocus();
    }

    @Override
    public CompoundTag saveData() {
        return commandLineContext.getSaveData();
    }
}

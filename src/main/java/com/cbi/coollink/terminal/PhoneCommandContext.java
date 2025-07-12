package com.cbi.coollink.terminal;

import com.cbi.coollink.Main;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.w3c.dom.html.HTMLParagraphElement;

public class PhoneCommandContext extends CommandContext{

    public PhoneCommandContext(){
        textOut = new CommandTextOutputArea(375,130,100,"CBi Phone OS 1.0 (C) CBi 2025, All rights reserved");
    }

    private CommandTextOutputArea textOut;
    int tmcCnt = 0;
    @Override
    public void executeCommand(String command) {
        Main.LOGGER.info("Phone attempted to execute command: "+command);
        tmcCnt = 20;
    }

    @Override
    public WWidget getTextOutput() {
        return textOut.getWidget();
    }

    @Override
    public void tick() {
        if(tmcCnt  > 0){
            tmcCnt --;
        }
    }

    @Override
    public boolean commandExecuting() {
        return tmcCnt > 0;
    }
}

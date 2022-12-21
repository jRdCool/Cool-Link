package com.cbi.coollink.app;


import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.text.Text;

public class AIODeviceList extends WPlainPanel {
    WButton device;


    public AIODeviceList() {
        device = new WButton(Text.literal("name"));
        this.add(device,5,5,160,20);

        this.setSize(130,180);



    }
}

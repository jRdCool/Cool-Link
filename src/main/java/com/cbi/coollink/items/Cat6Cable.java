package com.cbi.coollink.items;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.item.Item;

public class Cat6Cable extends ACableItem {
    public Cat6Cable(Settings settings) {
        super(settings);
    }

    public WireType TYPE = WireType.CAT6;
}

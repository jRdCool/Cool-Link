package com.cbi.coollink.guis;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

public class PhoneGui extends LightweightGuiDescription {
    public PhoneGui(){
        MutableText netButtonLabel = MutableText.of(new LiteralTextContent("Network Router"));
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(300,250);
        WButton accessPointButton = new WButton().setIcon(new TextureIcon(new Identifier("cool-link","cbi")));
        WLabel label = new WLabel(netButtonLabel);
        root.add(accessPointButton,2,1);
        root.add(label,1,3);

    }
}

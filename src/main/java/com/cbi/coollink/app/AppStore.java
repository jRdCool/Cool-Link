package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AppStore extends AbstractPhoneApp{
    WScrollBar scrollBar;
    public AppStore() {
        super(new Identifier("cool-link","app-store"));
        icon=new Identifier("cool-link","textures/gui/app_shop_icon.png");
    }

    public AppStore(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
        super(new Identifier("cool-link","app-store"));
        icon=new Identifier("cool-link","textures/gui/app_shop_icon.png");
        root=new WPlainPanel();
        WPlainPanel panel=(WPlainPanel)root;
        WLabel title=new WLabel(Text.of("App Shop"));
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        panel.add(title,phoneWidth/2,5);
        timeColor=TIME_COLOR_BLACK;
        scrollBar = new WScrollBar(Axis.VERTICAL);
        //set this value to 16 + number of apps that don't fit on screen
        scrollBar.setMaxValue(800);
        panel.add(scrollBar,380,15,20,170);
    }

    @Override
    public void tick() {
        //Main.LOGGER.info(scrollBar.getValue()+"");
    }

    @Override
    public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData) {
        return new AppStore(world,clickedOnBlockEntity,appData);
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.coloredRect(matrices,left,top,phoneWidth,phoneHeight,0xFF_FFFFFF));
    }
}

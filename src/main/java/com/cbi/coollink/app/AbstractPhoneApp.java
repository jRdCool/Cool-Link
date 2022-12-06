package com.cbi.coollink.app;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class AbstractPhoneApp {
    /**the panel all widgets of this are added too
     *
     */
    WPanel root;

    public final int phoneWidth=400,phoneHeight=200,TIME_COLOR_WHITE=0xFFFFFF,TIME_COLOR_BLACK=0x0;
    public int timeColor=TIME_COLOR_WHITE;

    public Identifier icon = new Identifier("minecraft","textures/misc/unknown_pack.png");

    /**get the main panel of this app
     *
     * @return the main panel of this app
     */
     public final WPanel getPanel(){
         return root;
     }

    /**this function is called by the phone every tick
     *
     */
     abstract public void tick();

    /**this function is used by the phone to initialize the app
     *
     * @param world the world the phone is in
     * @param clickedOnBlockEntity the block entity the phone clicked on (this may be nullZ)
     * @return a new instance of the app
     */
     abstract public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity);

    /**used to set what is painted by the app
     *
     */
    abstract public void addPainters();


}

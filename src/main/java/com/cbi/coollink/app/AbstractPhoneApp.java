package com.cbi.coollink.app;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class AbstractPhoneApp {
    /**the panel all widgets of this are added too
     *
     */
    WPanel root;

    public final int phoneWidth=400,phoneHeight=200,TIME_COLOR_WHITE=0xFFFFFF,TIME_COLOR_BLACK=0x0;
    public int timeColor=TIME_COLOR_WHITE;
    public boolean requestSave=false;
    public Identifier icon = Identifier.of("minecraft","textures/misc/unknown_pack.png");
    public final Identifier appId;

    public AbstractPhoneApp(Identifier appId) {
        this.appId = appId;
    }

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

//    /**
//     * this function is used by the phone to initialize the app
//     *
//     * @param world                the world the phone is in
//     * @param clickedOnBlockEntity the block entity the phone clicked on (this may be null)
//     * @param appData contains data previously saved to the phone by this app. if no data has been saved it will be an empty NbtCompound
//     * @return a new instance of the app
//     */
//     abstract public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData);

    /**used to set what is painted by the app
     *
     */
    abstract public void addPainters();

//    /**determines weather this app should open on the phone when the phone is activated while clicking on a specific block entity
//     *
//     * @param blockEntity the block entity that was clicked on
//     * @return weather or not this app should open
//     */
//    public boolean openOnBlockEntity(BlockEntity blockEntity){
//        return false;
//    }

    /**NEVER CALL THIS METHOD!!!!
     * this method is called by the phone every time it attempts to save while an app is open
     * @return the data to save
     */
    public NbtCompound saveData(){
        return null;
    }

    /**called by the phone when a key is pressed on the user's keyboard
     * useful for being able to control an app from the keyboard
     * @param ch the char ID of the key pressed (letter typed)
     * @param keyCode the ID of they key Pressed
     * @param modifiers modifier keys held down when the key was pressed(Shift Ctl ...)
     */
    public void keyPressed(int ch,int keyCode,int modifiers){}

    /**called by the phone when a key is released on the user's keyboard
     * useful for being able to control an app from the keyboard
     * @param ch the char ID of the key released (letter typed)
     * @param keyCode the ID of they key released
     * @param modifiers modifier keys held down when the key was released(Shift Ctl ...)
     */
    public void keyReleased(int ch,int keyCode,int modifiers){}

    public Identifier getIcon(){
        return icon;
    }

    public Identifier getId(){
        return appId;
    }



}

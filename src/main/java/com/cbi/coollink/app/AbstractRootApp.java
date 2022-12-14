package com.cbi.coollink.app;

import com.cbi.coollink.guis.PhoneGui;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class AbstractRootApp extends AbstractPhoneApp{
    public AbstractRootApp(Identifier appId) {
        super(appId);
    }

    abstract public AbstractPhoneApp init(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, PhoneGui phone);
}

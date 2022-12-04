package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.guis.PhoneGui;
import com.cbi.coollink.guis.PhoneScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SmartPhone extends Item {
    public SmartPhone(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){

        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            client.setScreen(new PhoneScreen(new PhoneGui(this)));

        });
        //MinecraftClient.getInstance().setScreen(new PhoneScreen(new PhoneGui()));
        return super.use(world, user, hand);
    }
}

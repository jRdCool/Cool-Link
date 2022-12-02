package com.cbi.coollink;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cool-link");
    @Override
    public void onInitialize() {
        LOGGER.info("loading cool link");
    }
}

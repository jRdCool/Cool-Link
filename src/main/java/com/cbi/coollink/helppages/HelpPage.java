package com.cbi.coollink.helppages;


import net.minecraft.network.chat.Component;

public class HelpPage {
    private final String pageTitle;

    private final HelpPageComponent[] content;

    public HelpPage(String title, HelpPageComponent ... content) {
        pageTitle = title;
        this.content = content;
    }

    public Component getTitle() {
        return Component.literal(pageTitle);
    }

    public int getAmountOfContent(){
        return content.length;
    }

    public HelpPageComponent getContent(int index){
        return content[index];
    }
}

package com.cbi.coollink.helppages;

import net.minecraft.text.Text;

public class HelpPage {
    private final String pageTitle;

    private final HelpPageComponent[] content;

    public HelpPage(String title, HelpPageComponent ... content) {
        pageTitle = title;
        this.content = content;
    }

    public Text getTitle() {
        return Text.of(pageTitle);
    }

    public int getAmountOfContent(){
        return content.length;
    }

    public HelpPageComponent getContent(int index){
        return content[index];
    }
}

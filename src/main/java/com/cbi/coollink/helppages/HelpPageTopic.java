package com.cbi.coollink.helppages;


import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

public class HelpPageTopic {

    private final List<HelpPage> pages;

    private final String topicName;
    private final Component topicDescription;

    public HelpPageTopic(String name, Component topicDescription,HelpPage ... pages){
        this.pages = Arrays.asList(pages);
        topicName = name;
        this.topicDescription = topicDescription;
    }

    public int getNumPages(){
        return pages.size();
    }

    public HelpPage getPage(int index){
        return pages.get(index);
    }

    public Component getName(){
        return Component.literal(topicName);
    }

    public Component getDescription(){
        return topicDescription;
    }
}

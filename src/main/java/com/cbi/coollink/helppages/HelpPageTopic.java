package com.cbi.coollink.helppages;

import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class HelpPageTopic {

    private final List<HelpPage> pages;

    private final String topicName;
    private final Text topicDescription;

    public HelpPageTopic(String name, Text topicDescription,HelpPage ... pages){
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

    public Text getName(){
        return Text.of(topicName);
    }

    public Text getDescription(){
        return topicDescription;
    }
}

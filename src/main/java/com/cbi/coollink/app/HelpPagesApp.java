package com.cbi.coollink.app;

import com.cbi.coollink.Main;
import com.cbi.coollink.helppages.*;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;

public class HelpPagesApp extends AbstractPhoneApp{
    public static final Identifier ID = Identifier.of(Main.namespace,"help-pages");

    private final WPlainPanel plainRoot;

    private HelpPageScreenType currentScreen = new HelpPageStartScreen();

    private final WButton backButton = new WButton(Text.of("Back"));

    private static ArrayList<HelpPageTopic> topics = new ArrayList<>();


    public HelpPagesApp(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, ProgramNetworkInterface networkInterface) {
        super(ID);
        root=new WPlainPanel();//create the panel witch all widget will sit on
        plainRoot = (WPlainPanel) root;
        timeColor=TIME_COLOR_BLACK;//set the color of the clock if necessary
        WLabel title = new WLabel(Text.of("Help Pages"));
        plainRoot.add(title,phoneWidth/2-20,10);
        plainRoot.add(currentScreen.getPanel(),30,40,phoneWidth-30,phoneHeight-60);
        plainRoot.add(backButton,5,5,50,20);
        backButton.setEnabled(false);
        backButton.setOnClick(() -> {
           changePage(currentScreen.getPreviousPage());
        });

    }

    /**
     * this function is called by the phone every tick
     *
     */
    @Override
    public void tick() {

    }

    /**
     * used to set what is painted by the app
     *
     */
    @Override
    public void addPainters() {
        root.setBackgroundPainter((matrices, left, top, panel) -> {
            ScreenDrawing.coloredRect(matrices, left, top, phoneWidth, phoneHeight, 0xFF_FFFFFF);//draw the background
        });
    }

    void changePage(HelpPageScreenType newPage){
        root.remove(currentScreen.getPanel());
        currentScreen = newPage;
        WPanel panel = newPage.getPanel();
        plainRoot.add(panel,30,40,phoneWidth-30,phoneHeight-60);
        panel.layout();
        backButton.setEnabled(!(currentScreen instanceof HelpPageStartScreen));
    }

    public static void addHelpPageTopic(HelpPageTopic topic){
        topics.add(topic);
    }

    static abstract class HelpPageScreenType{
        public abstract WPanel getPanel();

        public HelpPageScreenType getPreviousPage(){
            return null;
        }
    }

    class HelpPageStartScreen extends HelpPageScreenType{

        WPlainPanel innerPanel = new WPlainPanel();
        WScrollPanel panel;
        public HelpPageStartScreen(){
            panel = new WScrollPanel(innerPanel);
            panel.setScrollingHorizontally(TriState.FALSE);
            innerPanel.add(new WText(Text.of("Select Topic")),phoneWidth/2-60,0,100,20);
            for(int i=0;i< topics.size();i++){
                HelpPageTopic topic = topics.get(i);
                WButton button = new WButton(topic.getName());
                button.setOnClick(() -> {
                    changePage(new HelpPageTopicScreen(this,topic));
                });
                innerPanel.add(button,0,25*i+25,350,20);
            }
        }

        @Override
        public WPanel getPanel() {
            return panel;
        }
    }

    class HelpPageTopicScreen extends HelpPageScreenType{

        WPlainPanel panel = new WPlainPanel();
        HelpPageScreenType prevPage;
        public HelpPageTopicScreen(HelpPageScreenType prevScreen, HelpPageTopic topic){//take the topic grooup as an arg
            this.prevPage = prevScreen;
            WPlainPanel contentList = new WPlainPanel();
            WScrollPanel contentScroll = new WScrollPanel(contentList);
            contentScroll.setScrollingHorizontally(TriState.FALSE);
            //left side
            panel.add(contentScroll,0,0,(phoneWidth-30)/2,phoneHeight-60);
            for(int i=0;i<topic.getNumPages();i++){
                HelpPage page = topic.getPage(i);
                WButton button = new WButton(page.getTitle());
                contentList.add(button,0,25*i,(phoneWidth-30)/2-10,20);
                button.setOnClick(() -> {
                    changePage(new HelpPagePage(this,page));
                });
            }
            //right side
            WLabel topicTitle = new WLabel(topic.getName());
            topicTitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            panel.add(topicTitle,3*(phoneWidth-30)/4,0);
            WText topicDescription = new WText(topic.getDescription());
            panel.add(topicDescription,(phoneWidth-30)/2,25,(phoneWidth-30)/2,phoneHeight-85);
        }

        @Override
        public WPanel getPanel() {
            return panel;
        }

        @Override
        public HelpPageScreenType getPreviousPage() {
            return prevPage;
        }
    }

    class HelpPagePage extends HelpPageScreenType{
        WPlainPanel innerPanel = new WPlainPanel();
        WScrollPanel panel;
        HelpPageScreenType prevPage;
        public HelpPagePage(HelpPageScreenType prevPage, HelpPage page){//take the page content as an arg
            this.prevPage = prevPage;
            panel = new WScrollPanel(innerPanel);
            WLabel title = new WLabel(page.getTitle());
            title.setHorizontalAlignment(HorizontalAlignment.CENTER);
            innerPanel.add(title,(phoneWidth-30)/2,0);
            int runningHeight = 20;
            for(int i=0;i<page.getAmountOfContent();i++){
                HelpPageComponent component = page.getContent(i);
                innerPanel.add(component.getItem(),0,runningHeight,component.getWidth(phoneWidth-40),component.getHeight());
                runningHeight += component.getHeight();//maby add alittle more here for space between the components
            }
        }

        @Override
        public WPanel getPanel() {
            return panel;
        }

        @Override
        public HelpPageScreenType getPreviousPage() {
            return prevPage;
        }
    }

    static {
        addHelpPageTopic(
          new HelpPageTopic("Test topic 1",Text.of("The first testing topic of the help page system"),
                  new HelpPage("Page 1", new HelpPageText(Text.of("Hello first page!"),20)),
                  new HelpPage("Page 2", new HelpPageText(Text.of("Hello second page!"),20), new HelpPageText(Text.of("Another body of text!"),20)),
                  new HelpPage("Page 3", new HelpPageText(Text.of("Hello third page!"),20),new HelpPageText(Text.of("Lets have a multi line segment!!!\nWOoooooo how coooooooooooooool"),40))
                  )//end of topic
        );

        addHelpPageTopic(
            new HelpPageTopic("Test topic 2", Text.of("This is the second Testing topic of the hepl page system. this one will include some images!!"),
                new HelpPage("Page 4",
                    new HelpPageText(Text.of("This page includes an image!!"),20),
                    new HelpPageImage(Identifier.of(Main.namespace,"textures/gui/phone_background_0.png"),300,200)
                ),
                new HelpPage("page 5",
                    new HelpPageText(Text.of("This page includes 2 images!!!!"),20),
                    new HelpPageImage(Identifier.of(Main.namespace,"textures/gui/phone_background_1.png"),300,200),
                    new HelpPageImage(Identifier.of(Main.namespace,"textures/gui/phone_background_2.png"),300,200)
                ),
                new HelpPage("page 6",
                    new HelpPageText(Text.of("This is the 6th help page, it inclides some text and images!!!"),20),
                    new HelpPageImage(Identifier.of(Main.namespace,"textures/gui/phone_background_3.png"),300,200),
                    new HelpPageText(Text.of("This is an image caption!!!"),20),
                    new HelpPageImage(Identifier.of(Main.namespace,"textures/gui/terminal.png"),128,128)
                )
            )//end of topic
        );
    }
}

package com.cbi.coollink.app;

import com.cbi.coollink.Main;
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

public class HelpPagesApp extends AbstractPhoneApp{
    public static final Identifier ID = Identifier.of(Main.namespace,"help-pages");

    private final WLabel title;
    private final WPlainPanel plainRoot;

    private HelpPageScreenType currentScreen = new HelpPageStartScreen();

    private WButton backButton = new WButton(Text.of("Back"));


    public HelpPagesApp(World world, BlockEntity clickedOnBlockEntity, NbtCompound appData, ProgramNetworkInterface networkInterface) {
        super(ID);
        root=new WPlainPanel();//create the panel witch all widget will sit on
        plainRoot = (WPlainPanel) root;
        timeColor=TIME_COLOR_BLACK;//set the color of the clock if necessary
        title = new WLabel(Text.of("Help Pages"));
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
            for(int i=0;i<10;i++){
                WButton button = new WButton(Text.of("TESST"));
                button.setOnClick(() -> {
                    changePage(new HelpPageTopicScreen(this));
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
        public HelpPageTopicScreen(HelpPageScreenType prevScreen){//take the topic grooup as an arg
            this.prevPage = prevScreen;
            WPlainPanel contentList = new WPlainPanel();
            WScrollPanel contentScroll = new WScrollPanel(contentList);
            contentScroll.setScrollingHorizontally(TriState.FALSE);
            //left side
            panel.add(contentScroll,0,0,(phoneWidth-30)/2,phoneHeight-60);
            for(int i=0;i<10;i++){
                WButton button = new WButton(Text.of("CONTENT"));
                contentList.add(button,0,25*i,(phoneWidth-30)/2-10,20);
                button.setOnClick(() -> {
                    changePage(new HelpPagePage(this));
                });
            }
            //right side
            WLabel topicTitle = new WLabel(Text.of("TOPIC NAME HERE"));
            topicTitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            panel.add(topicTitle,3*(phoneWidth-30)/4,0);
            WText topicDescription = new WText(Text.of("This is a soample toics dscrioptoin thjat shoes that stuff will worka dn that lots of text can be present.\n\n\n new line breqaks may also be allowed and this is compolettly fine. spelling an optional add on that can be purched for $19.99 at the concession stand"));
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
        public HelpPagePage(HelpPageScreenType prevPage){//take the page content as an arg
            this.prevPage = prevPage;
            panel = new WScrollPanel(innerPanel);
            WLabel title = new WLabel(Text.of("PAGE NAME HERE"));
            title.setHorizontalAlignment(HorizontalAlignment.CENTER);
            innerPanel.add(title,(phoneWidth-30)/2,0);
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
}

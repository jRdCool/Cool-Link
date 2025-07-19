package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import com.cbi.coollink.app.*;
import com.cbi.coollink.net.SavePhoneDataPacket;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.PlainTextContent.Literal;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;


public class PhoneGui extends LightweightGuiDescription {

    WPlainPanel root, notchAndTimePanel,appPanel,homeButtonPanel;
    WLabel time;

    public DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
    AbstractPhoneApp currentApp;
    World world;
    BlockEntity clickedOnBLockEntity;
    int top=0,left=0, numberOfPreinstalledApps;

    public ItemStack phoneInstance;

    WButton homeButton;
    private final ArrayList<PhoneAppInfo> installedApps = new ArrayList<>();


    public NbtCompound appData;
    public int backgroundNumber=0;

    public  enum ClockTimeType{AMPM,HOUR24}
    public ClockTimeType clockTimeType = ClockTimeType.AMPM;
    public String phoneName;

    public Vec3d playerPosition;
    ProgramNetworkInterface networkInterface;//TODO

    public PhoneGui(World world, ItemStack phoneInstance, Vec3d playerPosition) {
        installedApps.add(new PhoneAppInfo(SettingsPhoneApp.ID, (world1, blockEntity, nbtCompound, networkInterface) -> new SettingsPhoneApp(world1,blockEntity,this), SettingsPhoneApp.ICON,true, (be) -> false));

        installedApps.add(new PhoneAppInfo(AIOSettingApp.ID,(world1, blockEntity, nbtCompound, networkInterface) -> new AIOSettingApp(world1, blockEntity), AIOSettingApp.ICON,true, AIOSettingApp::openOnBlockEntity));

        installedApps.add(new PhoneAppInfo(AppStore.ID,(world1, blockEntity, nbtCompound, networkInterface) -> new AppStore(this), AppStore.ICON,true, (be)-> false));


        numberOfPreinstalledApps = installedApps.size();
        this.world=world;
        this.phoneInstance=phoneInstance;
        this.playerPosition = playerPosition;

        //load data from the phone instance
        NbtCompound nbt;//= phoneInstance.getOrCreateNbt();
        var rawData = phoneInstance.get(DataComponentTypes.CUSTOM_DATA);
        if(rawData!=null){
            nbt = rawData.copyNbt();
        }else{
            nbt = new NbtCompound();
        }

        if(!nbt.isEmpty()){
            Main.LOGGER.info("NBT data found\n"+nbt);
            NbtList nbtApps = (NbtList) nbt.get("apps");
            if(nbtApps != null && !nbtApps.isEmpty()){
                for(int i=0; i < nbtApps.size(); i++){
                    Identifier tmpName = Identifier.of(nbtApps.getString(i,null));
                    Main.LOGGER.info("Found APP: "+tmpName);
                    //if(AppRegistry.get(tmpName)==null)
                    //    continue;
                    installedApps.add(new PhoneAppInfo(tmpName, AppRegistry.getLauncher(tmpName), AppRegistry.getIcon(tmpName),false, AppRegistry.getOpensOnBlockEntity(tmpName)));
                }
            }
            appData=nbt.getCompoundOrEmpty("appData");
            if(appData==null){
                appData=new NbtCompound();
            }
            backgroundNumber= nbt.getInt("background",0);
            if (nbt.getInt("clock type",0) == 1) {
                clockTimeType = ClockTimeType.HOUR24;
            } else {
                clockTimeType = ClockTimeType.AMPM;
            }
            phoneName=nbt.getString("Name",null);
            if(phoneName==null || phoneName.isEmpty()) {
                phoneName = "UnNamed phone";
            }

            Optional<int[]> optionalBlockEntityPos = nbt.getIntArray("BePos");
            if(optionalBlockEntityPos.isPresent()){//if block entity pos was found
                int[] posI = optionalBlockEntityPos.get();
                clickedOnBLockEntity = world.getBlockEntity(new BlockPos(posI[0],posI[1],posI[2]));
                nbt.remove("BePos");
                phoneInstance.set(DataComponentTypes.CUSTOM_DATA,NbtComponent.of(nbt));//remove the block info from the phone
            }else{
                this.clickedOnBLockEntity = null;
            }

        }else{
            Main.LOGGER.info("no NBT data");
            appData=new NbtCompound();
            phoneName="UnNamed phone";
            this.clickedOnBLockEntity = null;
        }

        root = new WPlainPanel();//.setBackgroundPainter(new BackgroundPainter());
        setRootPanel(root);
        notchAndTimePanel =new WPlainPanel();
        homeButtonPanel=new WPlainPanel();
        appPanel=new WPlainPanel();
        notchAndTimePanel.setSize(400,200);
        root.setSize(400, 200);

        root.add(appPanel,0,0);
        appPanel.setSize(400,200);

        homeButton=new WButton();
        homeButtonPanel.add(homeButton,0,0,20,20);

        homeButton.setOnClick(()->{
            if(currentApp!=null) {
                root.remove(currentApp.getPanel());
                root.add(appPanel,0,0);
                root.remove(homeButtonPanel);
                saveData();
            }
            currentApp=null;
        });
        homeButtonPanel.setHost(this);


        time = new WLabel(MutableText.of(new Literal(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        notchAndTimePanel.add(time, (int) (400 * 0.89), (int) (250 * 0.02));
        root.add(notchAndTimePanel,0,0,1,1);

        //open a specific app based on the block that was clicked on
        if(clickedOnBLockEntity!=null) {
            for (PhoneAppInfo appInfo : installedApps) {
                if (appInfo.openOnBlockEntityCheck().openOnBlockEntity(clickedOnBLockEntity)) {
                    openApp(appInfo);
                    break;
                }
            }
        }

        saveData();
        root.setHost(this);
    }

    /**
     * sets tha background of the GUI
     */
    @Override
    public void addPainters() {
        if (this.rootPanel != null && !fullscreen) {
            this.rootPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                this.top=top;
                this.left=left;
                int bezel =6;
                //sets the background to a textures                                                                                                                            UVs go form 0 to 1 indicating where on the image to pull from

                ScreenDrawing.coloredRect(matrices,left-bezel,top-bezel,panel.getWidth()+2*bezel,panel.getHeight()+2*bezel,0xFF007BAB);
                ScreenDrawing.texturedRect(matrices,left,top,panel.getWidth(),panel.getHeight(),Identifier.of("cool-link", "textures/gui/phone_background_"+backgroundNumber+".png"),0,0,1,1,0xFF_FFFFFF);

                if(currentApp!=null){
                    currentApp.addPainters();
                }

            });
            //paint the app panel
            appPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                for(int i = 0; i< installedApps.size(); i++){
                    ScreenDrawing.texturedRect(matrices,left+20+25*(i%15),top+20+25*(i/15),20,20, installedApps.get(i).icon,0,0,1,1,0xFF_FFFFFF);
                }
            });

            //pain the notch to the screen
            notchAndTimePanel.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.texturedRect(matrices,left,top+50,17, 100,Identifier.of("cool-link", "textures/gui/noch.png"),0,0,1,1,0xFF_FFFFFF));
        }
    }

    public void tick() {
        //Main.LOGGER.info("ticked");
        time.setText(MutableText.of(new Literal(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor((currentApp==null)?0xFFFFFF:currentApp.timeColor)));
        if(currentApp!=null){
            currentApp.tick();
            if(currentApp.requestSave) {
                saveData();
                currentApp.requestSave=false;
            }
            currentApp.getPanel().setHost(this);
        }
    }

    /**Launch an app and open its GUI for the user
     * @param appInfo the information about the app to open
     */
    protected void openApp(PhoneAppInfo appInfo){
        if(currentApp!=null){//if an app was open previously
            root.remove(currentApp.getPanel());//remove that apps panel
            root.remove(notchAndTimePanel);//tmp remove the notch
            root.remove(homeButtonPanel);//tmp remove the home button
        }else{//if no app had opened pervously
            root.remove(appPanel);//remove what was in the app slot
        }

        NbtCompound dataForApp = appData.getCompoundOrEmpty(appInfo.appId.toString());//get any stored data for the app

        if(dataForApp == null){//this should not happen but just encase
            dataForApp = new NbtCompound();
        }

        currentApp = appInfo.launcher.launch(world, clickedOnBLockEntity, dataForApp, networkInterface);//launch the app and get a reference to it

        root.add(currentApp.getPanel(),0,0,400,200);//add the app panel to the display stack
        root.add(notchAndTimePanel,0,0,0,0);//re add the notch
        root.add(homeButtonPanel,190,180,20,20);//re add the home button
        currentApp.getPanel().setHost(this);//set this as the host for the apps panel
    }

    public void mouseClicked(double mouseX,double mouseY){
        mouseX-=left;//adjust the mouse pos to be centered around the top corner of the root panel
        mouseY-=top;
        //Main.LOGGER.info("mouse clicked at: "+mouseX+" "+mouseY);
        if(currentApp==null) {
            for (int i = 0; i < installedApps.size(); i++) {//                                             The integer division here is intentional
                if (mouseX >= 20 + 25 * (i%15) && mouseX <= 20 + 25 * (i%15) + 20 && mouseY >= 20+25*(i/15) && mouseY <= 20 + 20+25*(i/15)) {
                    openApp(installedApps.get(i));
                }
            }
        }

    }

    public void keyPressed(int ch,int keyCode,int modifiers){
        if(currentApp!=null) {
            currentApp.keyPressed(ch,keyCode,modifiers);
        }
    }

    public void keyReleased(int ch,int keyCode,int modifiers){
        if(currentApp!=null) {
            currentApp.keyReleased(ch,keyCode,modifiers);
        }
    }

    /**saves the data of the phone to the world
     *
     */
    void saveData(){
        NbtCompound nbt = new NbtCompound();
        NbtList nbtApps = new NbtList();
        for(int i = numberOfPreinstalledApps; i < installedApps.size(); i++){
            if(installedApps.get(i).appId == null) {
                Main.LOGGER.info("Null app id for: "+installedApps.get(i).getClass().getName());
            }
            nbtApps.add(NbtString.of(installedApps.get(i).appId.toString()));
        }
        nbt.put("apps",nbtApps);
        if(currentApp != null) {//if an app is currently open
            NbtCompound dataFromApp = currentApp.saveData();//get the save data from that app
            if(dataFromApp !=null && !dataFromApp.isEmpty()){
                appData.put(currentApp.appId.toString(),dataFromApp);
            }
        }
        if(!appData.isEmpty()){
            nbt.put("appData",appData);
        }
        nbt.putInt("background", backgroundNumber);
        nbt.putInt("clock type",(clockTimeType==ClockTimeType.AMPM)? 0: 1);
        nbt.putString("Name",phoneName);

        //write the data
        phoneInstance.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        ClientPlayNetworking.send(new SavePhoneDataPacket(nbt,phoneInstance));

    }

    public boolean isAppInstalled(Identifier id){
        return installedApps.stream().map(PhoneAppInfo::appId).anyMatch(appId -> appId.equals(id));
    }

    public void installApp(Identifier appId){
        installedApps.add(new PhoneAppInfo(appId, AppRegistry.getLauncher(appId), AppRegistry.getIcon(appId),false, AppRegistry.getOpensOnBlockEntity(appId)));
    }

    public void uninstallApp(Identifier appId){
        for(int i=0;i<installedApps.size();i++){
            if(installedApps.get(i).appId.equals(appId)){
                installedApps.remove(i);
                break;
            }
        }
    }

    public int getNumInstalledApps(){
        return installedApps.size();
    }

    protected record PhoneAppInfo(Identifier appId, AppRegistry.AppLauncher launcher,Identifier icon,boolean isRoot, AppRegistry.OpenOnBlockEntityCheck openOnBlockEntityCheck){}



}

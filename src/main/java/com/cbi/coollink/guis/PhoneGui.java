package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import com.cbi.coollink.app.*;
import com.cbi.coollink.net.*;
import com.cbi.coollink.net.protocol.IpDataPacket;
import com.cbi.coollink.net.protocol.Mac;
import com.cbi.coollink.net.protocol.PhoneNetworkInterface;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;


public class PhoneGui extends LightweightGuiDescription {

    WPlainPanel root, notchAndTimePanel,appPanel,homeButtonPanel;
    WLabel time;

    public DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
    AbstractPhoneApp currentApp;
    Level world;
    BlockEntity clickedOnBLockEntity;
    int top=0,left=0, numberOfPreinstalledApps;

    public ItemStack phoneInstance;

    WButton homeButton;
    private final ArrayList<PhoneAppInfo> installedApps = new ArrayList<>();


    public CompoundTag appData;
    private final ArrayList<WifiNetworkInfo> savedNetworks = new ArrayList<>();
    public int backgroundNumber=0;

    public boolean isWifiConnectFinished() {
        return wifiConnectFinished;
    }

    public void setWifiConnectFinished(boolean wifiConnectFinished) {
        this.wifiConnectFinished = wifiConnectFinished;
    }

    public int getWifiConnectError() {
        return wifiConnectError;
    }

    public  enum ClockTimeType{AMPM,HOUR24}
    public ClockTimeType clockTimeType = ClockTimeType.AMPM;
    public String phoneName;

    public Vec3 playerPosition;
    ProgramNetworkInterface networkInterface;

    private double distToAp = 100e300;
    public BlockPos apBlockPos = new BlockPos(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);

    private final Mac mac;

    private boolean connectedToWifi = false;
    private String deviceIp = "";
    private String wifiSsid = "";

    private boolean deviceOnline = false;

    private boolean wifiConnectFinished = false;
    private int wifiConnectError = 0;

    private final WSprite wifiIcon, noInternetIcon;

    public PhoneGui(Level world, ItemStack phoneInstance, Vec3 playerPosition) {
        installedApps.add(new PhoneAppInfo(SettingsPhoneApp.ID, (world1, blockEntity, nbtCompound, networkInterface) -> new SettingsPhoneApp(world1,blockEntity,this), SettingsPhoneApp.ICON,true, (be) -> false));

        installedApps.add(new PhoneAppInfo(AIOSettingApp.ID,(world1, blockEntity, nbtCompound, networkInterface) -> new AIOSettingApp(world1, blockEntity), AIOSettingApp.ICON,true, AIOSettingApp::openOnBlockEntity));

        installedApps.add(new PhoneAppInfo(AppStore.ID,(world1, blockEntity, nbtCompound, networkInterface) -> new AppStore(this,networkInterface), AppStore.ICON,true, (be)-> false));


        numberOfPreinstalledApps = installedApps.size();
        this.world=world;
        this.phoneInstance=phoneInstance;
        this.playerPosition = playerPosition;

        //load data from the phone instance
        CompoundTag nbt;//= phoneInstance.getOrCreateNbt();
        var rawData = phoneInstance.get(DataComponents.CUSTOM_DATA);
        if(rawData!=null){
            nbt = rawData.copyTag();
        }else{
            nbt = new CompoundTag();
        }

        if(!nbt.isEmpty()){
            //Main.LOGGER.info("NBT data found\n"+nbt);
            ListTag nbtApps = (ListTag) nbt.get("apps");
            if(nbtApps != null && !nbtApps.isEmpty()){
                for(int i=0; i < nbtApps.size(); i++){
                    Identifier tmpName = Identifier.parse(nbtApps.getStringOr(i,null));
                    //Main.LOGGER.info("Found APP: "+tmpName);
                    //if(AppRegistry.get(tmpName)==null)
                    //    continue;
                    installedApps.add(new PhoneAppInfo(tmpName, AppRegistry.getLauncher(tmpName), AppRegistry.getIcon(tmpName),false, AppRegistry.getOpensOnBlockEntity(tmpName)));
                }
            }
            appData=nbt.getCompoundOrEmpty("appData");
            
            backgroundNumber= nbt.getIntOr("background",0);
            if (nbt.getIntOr("clock type",0) == 1) {
                clockTimeType = ClockTimeType.HOUR24;
            } else {
                clockTimeType = ClockTimeType.AMPM;
            }
            phoneName=nbt.getStringOr("Name","");
            if(phoneName.isEmpty()) {
                phoneName = "UnNamed phone";
            }

            Optional<int[]> optionalBlockEntityPos = nbt.getIntArray("BePos");
            if(optionalBlockEntityPos.isPresent()){//if block entity pos was found
                int[] posI = optionalBlockEntityPos.get();
                clickedOnBLockEntity = world.getBlockEntity(new BlockPos(posI[0],posI[1],posI[2]));
                nbt.remove("BePos");
                phoneInstance.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));//remove the block info from the phone
            }else{
                this.clickedOnBLockEntity = null;
            }

            //WIFI TIME!
            ListTag savedWifiNetworks = nbt.getListOrEmpty("networks");
            for(int i=0;i<savedWifiNetworks.size();i++){
                CompoundTag networkDescription = savedWifiNetworks.getCompoundOrEmpty(i);
                savedNetworks.add(new WifiNetworkInfo(networkDescription));
            }

            for (WifiNetworkInfo network : savedNetworks) {
                //if this AP is not in this dimension, then skip it
                if (!world.dimension().registry().equals(network.dimension())) {
                    continue;
                }
                BlockPos knownAp = new BlockPos(network.apX(), network.apY(), network.apZ());
                if (playerPosition.distanceTo(Vec3.atCenterOf(knownAp)) < 512) {//only check for aps that are less than 512 blocks away

                    ClientPlayNetworking.send(new RequestAccessPointPositionsPacket(world.dimension(), knownAp));//request all ap locations on that network
                }
            }

            Optional<int[]> raw_mac_opt = nbt.getIntArray("mac");
            mac = raw_mac_opt.map(Mac::new).orElseGet(() -> new Mac(0x31));

            //end of nbt is not empty
        }else{
            //Main.LOGGER.info("no NBT data");
            appData=new CompoundTag();
            phoneName="UnNamed phone";
            this.clickedOnBLockEntity = null;
            mac = new Mac(0x31);
            installApp(HelpPagesApp.ID);
        }

        networkInterface = new PhoneNetworkInterface(mac);

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
                if(networkInterface != null){
                    networkInterface.clearPacketReceivers();
                }
            }
            currentApp=null;
        });
        homeButtonPanel.setHost(this);

        wifiIcon = new WSprite(Identifier.fromNamespaceAndPath(Main.namespace,"textures/icon/wifi_0.png"));
        noInternetIcon = new WSprite(Identifier.fromNamespaceAndPath(Main.namespace,"textures/icon/wifi_no_network.png"));

        time = new WLabel(MutableComponent.create(new PlainTextContents.LiteralContents(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        notchAndTimePanel.add(time, (int) (400 * 0.89), (int) (250 * 0.02));
        notchAndTimePanel.add(wifiIcon,(int) (400 * 0.85),4,10,10);
        notchAndTimePanel.add(noInternetIcon,(int) (400 * 0.86),7,6,6);
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
                ScreenDrawing.texturedRect(matrices,left,top,panel.getWidth(),panel.getHeight(),Identifier.fromNamespaceAndPath(Main.namespace, "textures/gui/phone_background_"+backgroundNumber+".png"),0,0,1,1,0xFF_FFFFFF);

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

            //append the notch to the screen
            notchAndTimePanel.setBackgroundPainter((matrices, left, top, panel) -> ScreenDrawing.texturedRect(matrices,left,top+50,17, 100,Identifier.fromNamespaceAndPath(Main.namespace, "textures/gui/noch.png"),0,0,1,1,0xFF_FFFFFF));
        }
    }

    public void tick() {
        //Main.LOGGER.info("ticked");
        time.setText(MutableComponent.create(new PlainTextContents.LiteralContents(dtf.format(LocalDateTime.now()))).setStyle(Style.EMPTY.withColor((currentApp==null)?0xFFFFFF:currentApp.timeColor)));
        if(connectedToWifi){
            double distanceToAp = playerPosition.distanceTo(Vec3.atCenterOf(apBlockPos));
            int strength;
            if(distanceToAp > 22.5){
                strength = 1;
            }else if(distanceToAp > 15){
                strength = 2;
            }else if(distanceToAp > 7.5){
                strength = 3;
            }else{
                strength = 4;
            }
            String color = getIconColor();
            wifiIcon.setImage(Identifier.fromNamespaceAndPath(Main.namespace,"textures/icon/wifi_"+strength+"_"+color+".png"));
        }else{
            wifiIcon.setImage(Identifier.fromNamespaceAndPath(Main.namespace,"textures/icon/wifi_0.png"));
        }
        if(deviceOnline || !connectedToWifi){
            noInternetIcon.setImage(Identifier.fromNamespaceAndPath(Main.namespace,"textures/icon/blank.png"));
        }else{
            noInternetIcon.setImage(Identifier.fromNamespaceAndPath(Main.namespace,"textures/icon/wifi_no_network.png"));
        }
        if(currentApp!=null){
            currentApp.tick();
            if(currentApp.requestSave) {
                saveData();
                currentApp.requestSave=false;
            }
            currentApp.getPanel().setHost(this);
        }
    }

    @NotNull
    private String getIconColor() {
        String color = "white";
        if(currentApp !=null){
            int timeColor = currentApp.timeColor;
            timeColor &= 0xFFFFFF;//cut off any supplied alpha channel
            int red = timeColor >> 16 & 0xFF;
            int green = timeColor >> 8 & 0xFF;
            int blue = timeColor & 0xFF;
            int luminance = ((red * 299) + (green * 587) + (blue * 114)) / 1000;
            color = luminance < 127 ? "black": "white";
        }
        return color;
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

        CompoundTag dataForApp = appData.getCompoundOrEmpty(appInfo.appId.toString());//get any stored data for the app

        if(dataForApp == null){//this should not happen but just encase
            dataForApp = new CompoundTag();
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
        CompoundTag nbt = new CompoundTag();
        ListTag nbtApps = new ListTag();
        for(int i = numberOfPreinstalledApps; i < installedApps.size(); i++){
            if(installedApps.get(i).appId == null) {
                Main.LOGGER.info("Null app id for: "+installedApps.get(i).getClass().getName());
            }
            nbtApps.add(StringTag.valueOf(installedApps.get(i).appId.toString()));
        }
        nbt.put("apps",nbtApps);
        if(currentApp != null) {//if an app is currently open
            CompoundTag dataFromApp = currentApp.saveData();//get the save data from that app
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

        ListTag networks = new ListTag();
        for(WifiNetworkInfo wifi: savedNetworks){
            networks.add(wifi.toNbt());
        }
        nbt.put("networks",networks);
        nbt.putIntArray("mac",mac.getMac());

        //write the data
        phoneInstance.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
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

    public void accessPointLocationResponse(BlockPos[] aps, String ssid){
        //find the one closest to the player
        double closeDist = 100e40;
        BlockPos closePos = new BlockPos(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
        for(BlockPos p :aps){
            double dist = playerPosition.distanceTo(Vec3.atCenterOf(p));
            if(closeDist > dist){
                closePos = p;
                closeDist = dist;
            }
        }
        //check if this AP is even in range
        if(closeDist > 30){//30 block range
            return;
        }

        //check if this one is closer than any that is currently connected
        if(closeDist < distToAp) {
            distToAp = closeDist;
            //get the password for this network
            String pass = "";
            for(WifiNetworkInfo net: savedNetworks){
                if(net.ssid().equals(ssid)){
                    pass = net.password();
                    break;
                }
            }

            //try to connect to that one
            ClientPlayNetworking.send(new ConnectToWifiNetworkRequestPacket(world.dimension(),closePos,pass,mac,phoneName));

            //update the stored AP position
            apBlockPos = closePos;

            //if already connected to a network then disconnect
            if(connectedToWifi){
                connectedToWifi = false;
                CompoundTag goAway = new CompoundTag();
                goAway.putString("type","disconnect");
                networkInterface.sendRawData("169.0.0.1",goAway);
            }
        }
    }

    public void handleWifiConnectionResponse(ClientWifiConnectionResultPacket response){
        if(response.incorrectPassword() || response.networkFull()){
            if(response.incorrectPassword() && ! response.networkFull()){
                wifiConnectError = 1;
            }else if(response.networkFull() && ! response.incorrectPassword()){
                wifiConnectError = 2;
            }else{
                wifiConnectError = 3;
            }
        }else{
            for(WifiNetworkInfo net: savedNetworks){
                if(net.ssid.equals(response.ssid())){
                    if(apBlockPos.equals(new BlockPos(net.apX,net.apY,net.apZ))) {//if this is the network we want to be connected to
                        connectedToWifi = true;
                        deviceIp = response.deviceIp();
                        wifiSsid = response.ssid();
                        deviceOnline = response.connectedToTheInternet();
                        networkInterface = new PhoneNetworkInterface(mac, deviceIp, world, apBlockPos, deviceOnline);
                    }else{
                        //send disconnect packet
                        CompoundTag goAway = new CompoundTag();
                        goAway.putString("type","disconnect");
                        ClientPlayNetworking.send(new WIFIClientIpPacket(world.dimension(),new BlockPos(net.apX,net.apY,net.apZ),new IpDataPacket("169.0.0.1", response.deviceIp(), mac,goAway)));
                    }
                }
            }
            wifiConnectError = 0;
        }
        wifiConnectFinished = true;
    }

    public Mac getMac(){
        return mac;
    }

    public String getWifiSsid(){
        return wifiSsid;
    }

    public ArrayList<WifiNetworkInfo> getSavedNetworks(){
        return savedNetworks;
    }

    public void updateSavedNetwork(WifiNetworkInfo networkInfo){
        //check if there is a network with this name already in the list
        boolean delete = networkInfo.dimension().equals(Identifier.withDefaultNamespace("delete-this"));

        for(int i=0;i<savedNetworks.size();i++){
            if(savedNetworks.get(i).ssid().equals(networkInfo.ssid())){
                if(delete){
                    savedNetworks.remove(i);
                    if(wifiSsid.equals(networkInfo.ssid())){
                        //disconnect
                        connectedToWifi = false;
                        CompoundTag goAway = new CompoundTag();
                        goAway.putString("type","disconnect");
                        networkInterface.sendRawData("169.0.0.1",goAway);
                    }
                }else {
                    savedNetworks.set(i, networkInfo);
                }
                saveData();
                return;
            }
        }
        savedNetworks.add(networkInfo);
        saveData();
    }

    public void handleIncomingDataPacket(IpDataPacket data){
        if(data.getData().getStringOr("type","noIdea").equals("ping")){
            CompoundTag pongCompound = new CompoundTag();
            pongCompound.putString("type","pong");
            networkInterface.sendIpPacketOverNetwork(data.createResponsePacket(pongCompound));
            return;
        }
        //handle basic low level packets for the phone its self like ping
        Main.LOGGER.info("Phone Received packet: "+data);
        networkInterface.processReceivedDataPacket(data);
    }

    protected record PhoneAppInfo(Identifier appId, AppRegistry.AppLauncher launcher,Identifier icon,boolean isRoot, AppRegistry.OpenOnBlockEntityCheck openOnBlockEntityCheck){}
    public record WifiNetworkInfo(String ssid,String password,Identifier dimension, int apX,int apY,int apZ){
        public CompoundTag toNbt(){
            CompoundTag compound = new CompoundTag();
            compound.putString("ssid",ssid);
            compound.putString("password",password);
            compound.putString("dim",dimension.toString());
            compound.putInt("x",apX);
            compound.putInt("y",apY);
            compound.putInt("z",apZ);
            return compound;
        }

        public WifiNetworkInfo(CompoundTag compound){
            this(
                compound.getStringOr("ssid","Error network"),
                compound.getStringOr("password",""),
                Identifier.parse(compound.getStringOr("dim","air")),
                compound.getIntOr("x",Integer.MIN_VALUE),
                compound.getIntOr("y",Integer.MIN_VALUE),
                compound.getIntOr("z",Integer.MIN_VALUE)
            );
        }

        @Override
        public String toString() {
            return "WifiNetworkInfo{" +
                    "ssid='" + ssid + '\'' +
                    ", password='" + password + '\'' +
                    ", dimension=" + dimension +
                    ", apX=" + apX +
                    ", apY=" + apY +
                    ", apZ=" + apZ +
                    '}';
        }
    }
}

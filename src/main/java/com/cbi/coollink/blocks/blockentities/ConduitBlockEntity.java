package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.conduits.Conduit;
import com.cbi.coollink.net.protocol.WireDataPacket;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ConduitBlockEntity extends BlockEntity implements IWireNode {

    private final LocalNode[] localNodes;
    private static final int nodeCount = 0b11111111;
    private final boolean[] isNodeUsed = new boolean[nodeCount];

    public static ConduitBlockEntity of(BlockPos pos, BlockState state, int type){
        return switch (type){
            case 0 -> new ConduitBlockEntity(pos, state, Main.SMALL_CONDUIT_BLOCK_ENTITY);
            case 1 -> new ConduitBlockEntity(pos, state, Main.MEDIUM_CONDUIT_BLOCK_ENTITY);
            case 2 -> new ConduitBlockEntity(pos, state, Main.LARGE_CONDUIT_BLOCK_ENTITY);
            default -> null;
        };
    }
    public ConduitBlockEntity(BlockPos pos, BlockState state, BlockEntityType<?> type) {
        super(type, pos, state);
        this.localNodes = new LocalNode[getNodeCount()];
    }
    private BlockState coverBlock;

    @Override
    public void writeData(WriteView view){
        super.writeData(view);
        if(coverBlock != null){
            view.put("cover",BlockState.CODEC,coverBlock);
        }

        WriteView.ListView connections = view.getList("connections");
        for(int i=0;i<nodeCount;i++){
            if(localNodes[i]==null){
                connections.add();
            }else{
                WriteView connection = connections.add();
                localNodes[i].write(connection);
            }
        }
    }


    @Override
    public void readData(ReadView view) {
        super.readData(view);
        coverBlock = view.read("cover",BlockState.CODEC).orElse(null);

        ReadView.ListReadView lrv = view.getListReadView("connections");
        List<ReadView> connectionNodes = lrv.stream().toList();

        for (int i=0;i<nodeCount;i++){
            if(i < connectionNodes.size()){
                ReadView cn = connectionNodes.get(i);

                if(cn.getOptionalInt(LocalNode.ID).isEmpty()){
                    isNodeUsed[i] = false;
                }else{
                    localNodes[i]=new LocalNode(this , cn);
                    isNodeUsed[i] = true;
                }
            }else{
                isNodeUsed[i] = false;
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }


    public void updateStates(){
        assert world != null;
        world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
    }

    public boolean isCovered(){
        return coverBlock != null;
    }

    public BlockState getCoverBlock(){
        return coverBlock;
    }

    public void setCoverBlock(BlockState newCover){
        coverBlock = newCover;

        if(world != null) {//stop the regular model from rendering if it is being hidden
            world.setBlockState(getPos(), world.getBlockState(getPos()).with(Conduit.HIDDEN, coverBlock != null));
        }
        markDirty();
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);//CRITICAL FOR RENDER UPDATE, MAKE SURE FLAGS IS 0
    }




    //---------------------WireNode Functions---------------------//

    /*
    *Wire nodes for conduits are complex. They will be initially passes as binary and be indexed as such;
    * FFTTTTWW
    * F = Facing direction
    *   00 = N, 01 = S
    *   10 = W, 11 = E
    * T = Tube #
    *0b      0001  0011  0101  0111  1001  1011
    *0b   0000  0010  0100  0110  1000  1010  1100
    *        1 3 5 7 9 11
    *       0 2 4 6 8 10 12
    *
    * tube 13 (1101) is used for the wallports on each side
    *
    * W = Wire #
    *  00 10
    *  01 11
    *
    *
    * small conduits automatically use tube 6
    *
    * medium conduits use tubes 4-8 inclusive
     */


    @Override
    public Vec3d getNodeOffset(int node) {
        Direction direction = nodeDirection(node);
        int tube = tubeNumber(node);
        int wire = wireNum(node);
        int yOffset = ((tube % 2) << 1) + (wire % 2) ;
        double[] y = {0.05,0.10,0.15,0.20};
        double x = 0.0;
        double z = 0.0;
        if(tube == 13){
            boolean coax = (this.getPortType(node) == WireType.COAX);
            boolean left = wire < 2;
            double y13 = 0.5;
            switch (direction){
                case SOUTH -> {
                    x=1.0;
                    if(coax){z=0.5;}
                    else if(left){
                        z=0.5-.05;
                    }
                    else{
                        z=0.5+.05;
                    }
                }
                case EAST -> {
                    z=1.0;
                    if(coax){x=0.5;}
                    else if(left){
                        x=0.5-.05;
                    }
                    else{
                        x=0.5+.05;
                    }
                }
                case WEST -> {
                    z=0.0;
                    if(coax){x=0.5;}
                    else if(left){
                        x=0.5+.05;
                    }
                    else{
                        x=0.5-.05;
                    }
                }
                default -> {
                    x=0.0;
                    if(coax){z=0.5;}
                    else if(left){
                        z=0.5+.05;
                    }
                    else{
                     z=0.5-.05;
                    }
                }
            }
            if(!coax){
                if(wire % 2 ==0){y13+=0.05;}
                else{y13-=0.05;}
            }
            return new Vec3d(x,y13,z);
        }
        //todo: Create function to determine the x/z offset (side dependent)


        return new Vec3d(x,y[yOffset],z);
    }


    @Override
    public IWireNode getWireNode(int index) {return this;}

    @Override
    public int getOtherNodeIndex(int index) {
        return 0;
    }

    @Override
    public LocalNode getLocalNode(int index) {
        return null;
    }

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {

    }

    @Override
    public void removeNode(int index, boolean dropWire) {

    }

    @Override
    public WireType getPortType(int index) {
        return null;
    }

    @Override
    public boolean isNodeInUse(int index) {
        return isNodeUsed[index];
    }

    @Override
    public void setIsNodeUsed(int index, boolean set) {
        isNodeUsed[index]=set;
    }

    @Override
    public LocalNode getDestinationNode(int connectionIndex) {
        return null;
    }

    @Override
    public void transmitData(int connectionIndex, WireDataPacket data) {

    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }//returns the number of nodes the device has


    private Direction nodeDirection(int node){
        int direction = (node ^ 0b11000000) >>> 6;//take the direction bits and shift the bits to the right
        return switch(direction){
            case 0b00 -> Direction.NORTH;//North
            case 0b01 -> Direction.SOUTH;//South
            case 0b10 -> Direction.WEST;//West
            case 0b11 -> Direction.EAST;//East
            default -> null;
        };
    }

    private int intNodeDirection(int node){
        return (node ^ 0b11000000) >>> 6;//take the direction bits and shift the bits to the right
    }

    private int tubeNumber(int node){
        return (node ^ 0b00111100) >>> 2;//take the tube bits and shift them to the right
    }

    private int wireNum(int node){
        return node ^ 0b00000011;
    }
}

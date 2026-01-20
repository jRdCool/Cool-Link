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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.cbi.coollink.blocks.conduits.Conduit.cableShape;

public class ConduitBlockEntity extends BlockEntity implements IWireNode {

    private final LocalNode[] localNodes;
    private static final int nodeCount = 0b11111111;
    private final boolean[] isNodeUsed = new boolean[nodeCount];
    private final WireType[] nodeType = new WireType[nodeCount];

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
        Arrays.fill(nodeType, WireType.ANY);
        initialBlockState=state;
    }
    private BlockState coverBlock;

    private final BlockState initialBlockState;

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
                    nodeType[i]=localNodes[i].getType();
                    setNodeType(directionIndexTranslation(i),nodeType[i]);
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
        assert world != null;
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

    /**
     *
     * @param node
     *            The port on this wire node.
     *
     * @return
     *          The local to the block XYZ position the requested node is
     *          connected to.
     */
    @Override
    public Vec3d getNodeOffset(int node) {
        Direction direction = nodeDirection(node);
        int tube = tubeNumber(node);
        int wire = wireNum(node);
        int yOffset = ((tube % 2) << 1) + (wire % 2) ;
        double[] y = {0.085,0.035,0.195,0.145};
        double x ;
        double z ;
        if(direction==null){
            Main.LOGGER.error("!!Node Index Too Large!! Setting Offset to block origin.");
            return new Vec3d(0,0,0);
        }//Handles the case of a null direction
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
            //Main.LOGGER.info("X:"+ x+" Y:"+y13+" Z:"+z);
            return new Vec3d(x,y13,z);
        }//Handles the wall ports
        switch (direction){
            case SOUTH -> {
                x=nodeOffsetHelp(tube,wire,false);
                z=1.0;
            }
            case WEST -> {
                x=0.0;
                z=nodeOffsetHelp(tube,wire,true);
            }
            case EAST -> {
                x=1.0;
                z=nodeOffsetHelp(tube,wire,false);
            }
            default -> {
                x=nodeOffsetHelp(tube,wire,true);
                z=0.0;
            }
        }//handles the direct conduit ends.
        //Main.LOGGER.info("X:"+ x+" Y:"+y[yOffset]+" Z:"+z);
        return new Vec3d(x,y[yOffset],z);
    }


    /**
     *
     * @param index
     *          The port on this wire node.
     *
     * @return
     *          This class
     */
    @Override
    public IWireNode getWireNode(int index) {return this;}

    /**
     *
     * @param index
     *          The port on this wire node.
     * @return
     *          The index of the node on the connected block
     */
    @Override
    public int getOtherNodeIndex(int index) {
        return localNodes[index].getOtherIndex();
    }

    /**
     *
     * @param index
     *          The port on this wire node.
     *
     * @return
     *          The requested {@link LocalNode}
     */
    @Override
    public LocalNode getLocalNode(int index) {
        return localNodes[index];
    }

    /**
     *
     * @param index the port on this wire node
     * @param otherNode the port on the remote wire node
     * @param pos the position of the remote node block
     * @param type the type pof the wire
     */
    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
        this.localNodes[index] = new LocalNode(this, index, otherNode, type, pos);
        isNodeUsed[index]=true;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
        if(world.getBlockState(pos).getBlock() instanceof Conduit){
            int cableShape=world.getBlockState(pos).get(Conduit.cableShape);
            if(cableShape!=2){
                Direction inputDir = nodeDirection(index);
                Direction outputDir=Direction.NORTH;
                switch (inputDir){
                    case SOUTH -> {
                        outputDir= switch (cableShape){
                            case 5-> Direction.EAST;//SE
                            case 6-> Direction.WEST;//SW
                            default -> Direction.NORTH;//NS
                        };
                    }
                    case WEST -> {
                        outputDir= switch (cableShape){
                            case 4-> Direction.NORTH;//SE
                            case 5-> Direction.SOUTH;//SW
                            default -> Direction.EAST;//NS
                        };
                    }
                    case EAST -> {
                        outputDir= switch (cableShape){
                            case 4-> Direction.NORTH;//SE
                            case 7-> Direction.WEST;//SW
                            default -> Direction.WEST;//NS
                        };
                    }
                    case null -> {nullDirectionError();}
                    default -> {
                        outputDir= switch (cableShape){
                            case 4-> Direction.EAST;//SE
                            case 7-> Direction.WEST;//SW
                            default -> Direction.SOUTH;//NS
                        };
                    }//north

                }
                setNodeType(directionIndexTranslation(outputDir,index),type);
            }
        }
    }

    /**
     * Sets the {@link WireType} of the requested node
     * @param index the port on this wire node
     * @param type The {@link WireType} to set the node to
     */
    private void setNodeType(int index,WireType type){
        nodeType[index]=type;
    }

    /**
     *
     * @param index
     *          The port on this wire node.
     * @param dropWire
     *          Whether to drop wires or not.
     */
    @Override
    public void removeNode(int index, boolean dropWire) {
        this.localNodes[index] = null;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
        this.setIsNodeUsed(index,false);
    }

    /**
     *
     * @param index
     *          The port on this wire node.
     * @return
     *          The {@link WireType} of the requested node
     */
    @Override
    public WireType getPortType(int index) {
        return nodeType[index];
    }

    /**
     *
     * @param index
     *          The port on this wire node.
     * @return
     *          Weather the node is in use
     */
    @Override
    public boolean isNodeInUse(int index) {
        return isNodeUsed[index];
    }

    /**
     *
     * @param index The port on this wire node.
     * @param set   The value to set the node status to
     */
    @Override
    public void setIsNodeUsed(int index, boolean set) {
        isNodeUsed[index]=set;
    }

    /**
     *
     * @param connectionIndex The index of the start of the connection on this device
     * @return NULL
     */
    @Override
    public LocalNode getDestinationNode(int connectionIndex) {
        return null;
    }

    /**
     *
     * @param connectionIndex The index of the connection node on the destination device that is reviving the data
     * @param data The data to send to the other device
     */
    @Override
    public void transmitData(int connectionIndex, WireDataPacket data) {
        Main.LOGGER.error("Function Called by a conduit at: "+pos.getX()+","+pos.getY()+","+pos.getZ());
    }

    /**
     *
     * @return The number of nodes the block has
     */
    @Override
    public int getNodeCount() {
        return nodeCount;
    }//returns the number of nodes the device has

    /**
     *
     * @param node The port on this wire node.
     * @return A {@link Direction} from the provided node index
     */
    private static Direction nodeDirection(int node){
        int direction = (node & 0b11000000) >>> 6;//take the direction bits and shift the bits to the right
        return switch(direction){
            case 0b00 -> Direction.NORTH;//North
            case 0b01 -> Direction.SOUTH;//South
            case 0b10 -> Direction.WEST;//West
            case 0b11 -> Direction.EAST;//East
            default -> null;
        };
    }

    /**
     *
     * @param node The port on this wire node.
     * @return The integer representation of the nodes direction
     */
    private static int intNodeDirection(int node){
        return (node & 0b11000000) >>> 6;//take the direction bits and shift the bits to the right
    }

    /**
     *
     * @param node  The port on this wire node.
     * @return  The number of the tube of the provided node.
     */
    private static int tubeNumber(int node){
        return (node & 0b00111100) >>> 2;//take the tube bits and shift them to the right
    }

    /**
     *
     * @param node  The port on this wire node.
     * @return  The number of the wire in the tube
     */
    private static int wireNum(int node){
        return node & 0b00000011;
    }

    /**
     *
     * @param tube  The number of the tube
     * @param wire  The number of the wire
     * @param notOrient Weather to invert the offset from the origin
     * @return  The requested offset from 0
     */
    private static double nodeOffsetHelp(int tube,int wire, boolean notOrient){
        double wireOffset = 0.03;
        double nodeOffset = 0.0;
        if(wire<2){
            wireOffset = -1*wireOffset;
        }
        nodeOffset = (tube * 0.0625)+0.125+wireOffset;
        if(notOrient){
            nodeOffset = 1.0 - nodeOffset;
        }
        return nodeOffset;
    }

    /**
     *
     * @param direction The int direction of the node
     * @param tube  The number of the tube
     * @param wire  The number of the wire
     * @return  The assembled index of the node
     */
    public static int assembleIndex(int direction,int tube,int wire){
        return (direction<<6)+(tube<<2)+wire;
    }

    /**
     *
     * @param out   The output {@link Direction}
     * @param inputIndex    The inbound index of the node being worked on
     * @return  The corresponding node on the opposite end of the conduit
     */
    public static int directionIndexTranslation(Direction out,int inputIndex){
        int inputWire=wireNum(inputIndex);
        int inputTube=tubeNumber(inputIndex);

        int outputTube=Math.abs(12-inputTube);
        int outputWire=inputWire^0b10;


        int outDirNum = directionToDirectionNumber(out);

        //Main.LOGGER.info(Integer.toBinaryString(assembleIndex(outDirNum,outputTube,outputWire)));
        return assembleIndex(outDirNum,outputTube,outputWire);
    }

    private static int directionToDirectionNumber(Direction out) {
        int outDirNum=0b00;
        switch (out){
            case SOUTH -> {
                outDirNum =0b01;
            }
            case EAST -> {
                outDirNum =0b11;
            }
            case WEST -> {
                outDirNum =0b10;
            }
            default -> {}
        }
        return outDirNum;
    }

    /**
     * translates the inbound index to it's corresponding outbound index
     * @param inputIndex index of the entry point node
     * @return index of the output node
     */
    public int directionIndexTranslation(int inputIndex){
        BlockState state;
        if(world==null|| !(world.getBlockState(pos).getBlock() instanceof Conduit)) {
            state=initialBlockState;
        }else{
            state=world.getBlockState(pos);
        }
        int cableShape = state.get(Conduit.cableShape);
        if (cableShape != 2) {
            return directionIndexTranslation(outPutDirection(nodeDirection(inputIndex)), inputIndex);
        } else {
            //todo
            return directionIndexTranslation(Direction.NORTH, inputIndex);
        }//Junction box logic
    }

    /**
     *
     */
    private void nullDirectionError(){
        Main.LOGGER.error("Got Null Direction");
    }

    /**
     * Takes the direction the connection is coming from and translates it to the
     * direction it is heading to.
     * @param input Direction coming from
     * @return Direction heading to
     */
    public Direction outPutDirection(Direction input){
        BlockState state;
        if(world==null|| !(world.getBlockState(pos).getBlock() instanceof Conduit)) {
            state=initialBlockState;
        }else{
            state=world.getBlockState(pos);
        }
        int cableShape=state.get(Conduit.cableShape);
        Direction outputDir=Direction.NORTH;

        //  0 = NS
        //  1 = EW
        //  2 = Junction Box (3 or 4 directions)
        //  3 = Vertical Transition Box
        //  4 = NE
        //  5 = SE
        //  6 = SW
        //  7 = NW

        switch (input){
            case SOUTH -> {
                outputDir= switch (cableShape){
                    case 5-> Direction.EAST;//SE
                    case 6-> Direction.WEST;//SW
                    default -> Direction.NORTH;//NS
                };
            }
            case WEST -> {
                outputDir= switch (cableShape){
                    case 1-> Direction.EAST;//EW
                    case 6-> Direction.SOUTH;//SW
                    default -> Direction.NORTH;//NW
                };
            }
            case EAST -> {
                outputDir= switch (cableShape){
                    case 5-> Direction.SOUTH;//SE
                    case 1-> Direction.WEST;//EW
                    default -> Direction.NORTH;//NE
                };
            }
            case null -> {nullDirectionError();}
            default -> {
                outputDir= switch (cableShape){
                    case 4-> Direction.EAST;//NE
                    case 7-> Direction.WEST;//NW
                    default -> Direction.SOUTH;//NS
                };
            }//north
        }
        return outputDir;
    }

    /**
     *
     * @param startingBlock The starting conduit
     * @param input Direction starting from
     * @return the ConduitBlockEntity of the final block in the chain before an air block or a junction box
     */
    public static OtherEnd otherConduitEnd(ConduitBlockEntity startingBlock,Direction input){
        ConduitBlockEntity outputBlock = startingBlock;
        Direction outputDirection = input;//if facing north the input is south
        World world = startingBlock.getWorld();
        for(int i=0;i<300;i++){
            outputDirection=outputBlock.outPutDirection(input);
            //Main.LOGGER.info("Step "+i+" direction:"+outputDirection.asString());
            assert world != null;
            BlockPos neighbor = outputBlock.getPos().offset(outputDirection);
            //Main.LOGGER.info(neighbor.toString());
            BlockState neighborBS =world.getBlockState(neighbor);
            if(neighborBS.getBlock() instanceof Conduit){
                int shape =neighborBS.get(cableShape);
                if(shape!=2){
                    BlockEntity tempOutputBlock= world.getBlockEntity(neighbor);
                    input=outputDirection.getOpposite();

                    if(tempOutputBlock instanceof ConduitBlockEntity ob)
                        outputBlock=ob;
                    else{
                        //Main.LOGGER.info("1");
                        break;
                        }
                }else{
                    //Main.LOGGER.info("2");
                    break;
                }
            }else{
                //Main.LOGGER.info("3");
                break;
            }
        }
        return new OtherEnd(outputBlock,outputDirection);
    }

    public IWireNode.WireDescriptor getConnectedNode(int inputIndex){
        int outputIndex = directionIndexTranslation(inputIndex);
        LocalNode node = localNodes[outputIndex];//get the wire connection if one exsists
        if(node!= null){//check if a wire is directly connected
            return new WireDescriptor(node);//return the local node
        }
        outputIndex ^= 2;//directionIndexTranslation flips the 2 but witch is necessary for checking the local node but breaks things if we walk down the conduit so flip the bit back here
        Direction nextConduitDirection = nodeDirection(outputIndex);
        BlockPos nextConduitPos = pos.offset(nextConduitDirection);
        if(world == null){
//            Main.LOGGER.info("World null");
            return null;
        }
        //get the neighbor block
        BlockEntity nextBlockEntity = world.getBlockEntity(nextConduitPos);
        if(nextBlockEntity instanceof ConduitBlockEntity nextConduitBlockEntity){//check the neibor block is a conduit
            //get the index this wire connects to on the next block
            assert nextConduitDirection != null;//should be impossible to be null
            int otherConduitIndex = assembleIndex(directionToDirectionNumber(nextConduitDirection.getOpposite()),tubeNumber(outputIndex),wireNum(outputIndex));
//            Main.LOGGER.info("Next place: "+otherConduitIndex+" "+outputIndex+" "+inputIndex);
            //return the local node with that info
            return new WireDescriptor(nextConduitPos,otherConduitIndex);
        } else {
//            Main.LOGGER.info("outputIndex: "+outputIndex);
//            for(int i=0;i<localNodes.length;i++){
//                if(localNodes[i] != null){
//                    Main.LOGGER.info(i+") "+localNodes[i]);
//                }
//            }
//            Main.LOGGER.info("Next block was not conduit "+nextConduitPos+" : "+nextBlockEntity);
            return null;
        }

    }


    /**
     *record for returning other end data
     * @param blockEntity
     * @param direction
     */
    public record OtherEnd (ConduitBlockEntity blockEntity,Direction direction){}
}

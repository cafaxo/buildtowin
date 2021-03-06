package buildtowin.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeDirection;
import buildtowin.BuildToWin;
import buildtowin.ChunkLoadingManager;

public class TileEntityConnectionWire extends TileEntitySynchronized implements ITicketProvider {
    
    private ArrayList<TileEntity> surroundingTileEntities = new ArrayList<TileEntity>();
    
    private ArrayList<TileEntityConnectionWire> connectedWires;
    
    private boolean activated;
    
    private int lastSignal;
    
    private Ticket ticket;
    
    @Override
    public void initialize() {
        ChunkLoadingManager.forceChunk(this);
    }
    
    @Override
    public void invalidate() {
        ChunkLoadingManager.unforceChunk(this);
        super.invalidate();
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBoolean(this.activated);
        return true;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream inputStream) throws IOException {
        boolean newState = inputStream.readBoolean();
        
        if (newState != this.activated) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.activated = newState;
        }
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (this.lastSignal == 0) {
                this.activated = false;
            } else {
                this.activated = true;
                --this.lastSignal;
            }
        }
        
        super.updateEntity();
    }
    
    public void refresh() {
        this.surroundingTileEntities.clear();
        
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                    this.xCoord + direction.offsetX,
                    this.yCoord + direction.offsetY,
                    this.zCoord + direction.offsetZ);
            
            if (tileEntity instanceof TileEntity) {
                this.surroundingTileEntities.add(tileEntity);
            }
        }
    }
    
    public void refreshConnectedWires() {
        this.connectedWires = this.getConnectedWires(null);
    }
    
    public ArrayList<TileEntityConnectionWire> getConnectedWires(ArrayList<TileEntityConnectionWire> connectedWires) {
        if (connectedWires == null) {
            connectedWires = new ArrayList<TileEntityConnectionWire>();
        }
        
        if (!connectedWires.contains(this)) {
            connectedWires.add(this);
        }
        
        this.refresh();
        ArrayList<TileEntityConnectionWire> surroundingConnectionWires = this.getSurroundingTileEntities(TileEntityConnectionWire.class);
        
        for (TileEntityConnectionWire connectionWire : surroundingConnectionWires) {
            if (!connectedWires.contains(connectionWire)) {
                connectionWire.getConnectedWires(connectedWires);
            }
        }
        
        return connectedWires;
    }
    
    public ArrayList getSurroundingTileEntities() {
        return this.surroundingTileEntities;
    }
    
    public ArrayList<TileEntity> getConnectedTileEntities() {
        ArrayList<TileEntity> connectedTileEntities = new ArrayList<TileEntity>();
        
        for (TileEntityConnectionWire wire : this.connectedWires) {
            connectedTileEntities.addAll(wire.getSurroundingTileEntities());
        }
        
        return connectedTileEntities;
    }
    
    public ArrayList getSurroundingTileEntities(Class validTileEntity) {
        ArrayList desiredTileEntites = new ArrayList();
        
        for (TileEntity tileEntity : this.surroundingTileEntities) {
            if (tileEntity.getClass() == validTileEntity) {
                desiredTileEntites.add(tileEntity);
            }
        }
        
        return desiredTileEntites;
    }
    
    public void sendSignal() {
        for (TileEntityConnectionWire wire : this.connectedWires) {
            wire.activated = true;
            wire.lastSignal = 50;
        }
    }
    
    public boolean isActivated() {
        return this.activated;
    }
    
    public boolean isConnected(ForgeDirection direction) {
        int blockId = this.worldObj.getBlockId(this.xCoord + direction.offsetX, this.yCoord + direction.offsetY, this.zCoord + direction.offsetZ);
        
        return blockId == BuildToWin.connectionWire.blockID
                || blockId == BuildToWin.teamHub.blockID
                || blockId == BuildToWin.gameHub.blockID;
    }
    
    @Override
    public Ticket getTicket() {
        return this.ticket;
    }
    
    @Override
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}

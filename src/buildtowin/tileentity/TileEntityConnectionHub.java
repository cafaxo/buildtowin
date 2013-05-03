package buildtowin.tileentity;

import java.util.Arrays;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeDirection;
import buildtowin.ChunkLoadingManager;

public abstract class TileEntityConnectionHub extends TileEntitySynchronized implements ITicketProvider {
    
    private List<Class> validTileEntities;
    
    private Ticket ticket;
    
    public TileEntityConnectionHub(Class[] validTileEntities) {
        this.validTileEntities = Arrays.asList(validTileEntities);
    }
    
    @Override
    public void initialize() {
        ChunkLoadingManager.forceChunk(this);
    }
    
    @Override
    public void invalidate() {
        ChunkLoadingManager.unforceChunk(this);
        super.invalidate();
    }
    
    public abstract void onConnectionEstablished(TileEntity tileEntity);
    
    public void updateConnections() {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                    this.xCoord + direction.offsetX,
                    this.yCoord + direction.offsetY,
                    this.zCoord + direction.offsetZ);
            
            if (tileEntity instanceof TileEntityConnectionWire) {
                TileEntityConnectionWire wire = (TileEntityConnectionWire) tileEntity;
                wire.refreshConnectedWires();
                
                boolean isConnected = false;
                
                for (TileEntity connectedTileEntity : wire.getConnectedTileEntities()) {
                    if (this.validTileEntities.contains(connectedTileEntity.getClass())) {
                        this.onConnectionEstablished(connectedTileEntity);
                        isConnected = true;
                    }
                }
                
                if (isConnected) {
                    wire.sendSignal();
                }
            }
        }
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

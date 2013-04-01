package buildtowin.tileentity;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildtowin.BuildToWin;

public class TileEntityConnectionWire extends TileEntity {
    
    private ArrayList<TileEntityConnectionWire> surroundingConnectionWires;
    
    private ArrayList<TileEntityTeamHub> surroundingTeamHubs;
    
    public ArrayList<TileEntityTeamHub> getSurroundingTeamHubs() {
        return surroundingTeamHubs;
    }
    
    public TileEntityConnectionWire() {
        this.surroundingConnectionWires = new ArrayList<TileEntityConnectionWire>();
        this.surroundingTeamHubs = new ArrayList<TileEntityTeamHub>();
    }
    
    public void refresh() {
        this.surroundingConnectionWires.clear();
        this.surroundingTeamHubs.clear();
        
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                    this.xCoord + direction.offsetX,
                    this.yCoord + direction.offsetY,
                    this.zCoord + direction.offsetZ);
            
            if (tileEntity != null) {
                if (tileEntity instanceof TileEntityConnectionWire) {
                    this.surroundingConnectionWires.add((TileEntityConnectionWire) tileEntity);
                } else if (tileEntity instanceof TileEntityTeamHub) {
                    this.surroundingTeamHubs.add((TileEntityTeamHub) tileEntity);
                }
            }
        }
    }
    
    public ArrayList<TileEntityConnectionWire> getConnectedWires(ArrayList<TileEntityConnectionWire> connectedWires) {
        if (connectedWires == null) {
            connectedWires = new ArrayList<TileEntityConnectionWire>();
        }
        
        if (!connectedWires.contains(this)) {
            connectedWires.add(this);
        }
        
        for (TileEntityConnectionWire connectionWire : this.surroundingConnectionWires) {
            if (!connectedWires.contains(connectionWire)) {
                connectionWire.getConnectedWires(connectedWires);
            }
        }
        
        return connectedWires;
    }
    
    public static ArrayList<TileEntityTeamHub> getConnectedTeamHubs(ArrayList<TileEntityConnectionWire> connectedWires) {
        ArrayList<TileEntityTeamHub> connectedTeamHubs = new ArrayList<TileEntityTeamHub>();
        
        for (TileEntityConnectionWire connectionWire : connectedWires) {
            connectedTeamHubs.addAll(connectionWire.getSurroundingTeamHubs());
        }
        
        return connectedTeamHubs;
    }
    
    public static void activateWires(ArrayList<TileEntityConnectionWire> connectionWires) {
        for (TileEntityConnectionWire connectionWire : connectionWires) {
            connectionWire.worldObj.setBlock(connectionWire.xCoord, connectionWire.yCoord, connectionWire.zCoord, BuildToWin.connectionWire.blockID, 1, 3);
        }
    }
    
    public static void deactivateWires(ArrayList<TileEntityConnectionWire> connectionWires) {
        for (TileEntityConnectionWire connectionWire : connectionWires) {
            connectionWire.worldObj.setBlock(connectionWire.xCoord, connectionWire.yCoord, connectionWire.zCoord, BuildToWin.connectionWire.blockID, 0, 3);
        }
    }
    
    public boolean isConnected(ForgeDirection direction) {
        int blockId = this.worldObj.getBlockId(this.xCoord + direction.offsetX, this.yCoord + direction.offsetY, this.zCoord + direction.offsetZ);
        return blockId == BuildToWin.connectionWire.blockID || blockId == BuildToWin.teamHub.blockID || blockId == BuildToWin.gameHub.blockID;
    }
}

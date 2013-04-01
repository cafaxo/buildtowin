package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildtowin.blueprint.Blueprint;
import buildtowin.blueprint.IBlueprintProvider;
import buildtowin.network.PacketIds;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityGameHub extends TileEntity implements IBlueprintProvider {
    
    private ArrayList<TileEntityTeamHub> connectedTeamHubs = new ArrayList<TileEntityTeamHub>();
    
    private long plannedTimespan = 0;
    
    private long deadline = 0;
    
    private long sleptTime = 0;
    
    private int syncTimer = 0;
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setLong("plantmspn", this.plannedTimespan);
        
        par1NBTTagCompound.setLong("deadline", this.deadline);
        
        par1NBTTagCompound.setLong("slepttime", this.sleptTime);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.plannedTimespan = par1NBTTagCompound.getLong("plantmspn");
        
        this.deadline = par1NBTTagCompound.getLong("deadline");
        
        this.sleptTime = par1NBTTagCompound.getLong("slepttime");
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        NBTTagCompound tag = pkt.customParam1;
        this.readFromNBT(tag);
    }
    
    public Packet getUpdatePacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.GAMEHUB_UPDATE);
            
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            dataoutputstream.writeLong(this.plannedTimespan);
            dataoutputstream.writeLong(this.deadline);
            dataoutputstream.writeLong(this.sleptTime);
            
            return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void onUpdatePacket(DataInputStream inputStream) throws IOException {
        this.plannedTimespan = inputStream.readLong();
        this.deadline = inputStream.readLong();
        this.sleptTime = inputStream.readLong();
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            this.checkGameStatus();
            
            if (this.syncTimer == 30) {
                this.refreshConnection();
                PacketDispatcher.sendPacketToAllPlayers(this.getUpdatePacket());
                this.syncTimer = 0;
            }
            
            ++this.syncTimer;
        }
    }
    
    public void refreshTimespan(long newTimespan) {
        if (this.deadline != 0) {
            this.deadline = this.getRealWorldTime() + newTimespan;
        }
        
        this.plannedTimespan = newTimespan;
    }
    
    @Override
    public void loadBlueprint(Blueprint blueprint) {
        this.refreshConnection();
        
        for (TileEntityTeamHub teamHub : this.connectedTeamHubs) {
            teamHub.loadBlueprint(blueprint);
        }
    }
    
    public void startGame() {
        this.refreshConnection();
        
        this.sleptTime = 0;
        this.deadline = this.worldObj.getTotalWorldTime() + this.plannedTimespan;
        
        for (TileEntityTeamHub teamHub : this.connectedTeamHubs) {
            teamHub.getBlueprint().reset();
            teamHub.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has started."));
        }
    }
    
    public void stopGame(boolean notify) {
        this.refreshConnection();
        
        this.sleptTime = 0;
        this.deadline = 0;
        
        for (TileEntityTeamHub teamHub : this.connectedTeamHubs) {
            teamHub.getBlueprint().reset();
            
            if (notify) {
                teamHub.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has been stopped."));
            }
        }
    }
    
    private void checkGameStatus() {
        if (this.deadline != 0) {
            if (this.deadline >= this.getRealWorldTime()) {
                for (TileEntityTeamHub teamHub : this.connectedTeamHubs) {
                    if (teamHub.getFinishedBlockCount() == teamHub.getBlueprint().getBlocks().size()) {
                        teamHub.sendWinMessage();
                        
                        ArrayList<TileEntityTeamHub> ranking = this.getRanking();
                        
                        for (TileEntityTeamHub losingTeamHub : this.connectedTeamHubs) {
                            if (losingTeamHub != teamHub) {
                                losingTeamHub.sendLoseMessage(ranking.indexOf(losingTeamHub));
                            }
                        }
                        
                        this.stopGame(false);
                    }
                }
            } else {
                ArrayList<TileEntityTeamHub> ranking = this.getRanking();
                
                for (TileEntityTeamHub losingTeamHub : this.connectedTeamHubs) {
                    losingTeamHub.sendLoseMessage(ranking.indexOf(losingTeamHub));
                }
                
                this.stopGame(false);
            }
        }
    }
    
    public long getRealWorldTime() {
        return this.worldObj.getTotalWorldTime() + this.sleptTime;
    }
    
    public long getPlannedTimespan() {
        return plannedTimespan;
    }
    
    public void setPlannedTimespan(long plannedTimespan) {
        this.plannedTimespan = plannedTimespan;
    }
    
    public long getDeadline() {
        return deadline;
    }
    
    public long getSleptTime() {
        return sleptTime;
    }
    
    public void setSleptTime(long sleptTime) {
        this.sleptTime = sleptTime;
    }
    
    public ArrayList<TileEntityTeamHub> getRanking() {
        HashMap<Float, TileEntityTeamHub> unsortedTeamHubs = new HashMap<Float, TileEntityTeamHub>();
        ArrayList<Float> progressValues = new ArrayList<Float>();
        
        for (TileEntityTeamHub teamHub : this.connectedTeamHubs) {
            unsortedTeamHubs.put(teamHub.getProgress(), teamHub);
            progressValues.add(teamHub.getProgress());
        }
        
        Collections.sort(progressValues, Collections.reverseOrder());
        
        ArrayList<TileEntityTeamHub> sortedTeamHubs = new ArrayList<TileEntityTeamHub>();
        
        for (float progress : progressValues) {
            sortedTeamHubs.add(unsortedTeamHubs.get(progress));
        }
        
        return sortedTeamHubs;
    }
    
    public void refreshConnection() {
        this.connectedTeamHubs.clear();
        
        byte color = (byte) 0;
        
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                    this.xCoord + direction.offsetX,
                    this.yCoord + direction.offsetY,
                    this.zCoord + direction.offsetZ);
            
            if (tileEntity != null && tileEntity instanceof TileEntityConnectionWire) {
                TileEntityConnectionWire connectionWire = (TileEntityConnectionWire) tileEntity;
                
                ArrayList<TileEntityConnectionWire> connectionWires = connectionWire.getConnectedWires(null);
                ArrayList<TileEntityTeamHub> connectedTeamHubs = TileEntityConnectionWire.getConnectedTeamHubs(connectionWires);
                
                if (!connectedTeamHubs.isEmpty()) {
                    TileEntityConnectionWire.activateWires(connectionWires);
                    
                    for (TileEntityTeamHub teamHub : connectedTeamHubs) {
                        this.connectedTeamHubs.add(teamHub);
                        teamHub.setGameHub(this);
                        teamHub.getBlueprint().setColor(color);
                        
                        if (teamHub.getBlueprint().getBlocks().size() == 0) {
                            teamHub.getBlueprint().reset();
                        }
                        
                        ++color;
                    }
                    
                } else {
                    TileEntityConnectionWire.deactivateWires(connectionWires);
                }
            }
        }
    }
}

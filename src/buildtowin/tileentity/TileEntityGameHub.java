package buildtowin.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.Blueprint;
import buildtowin.blueprint.IBlueprintProvider;
import buildtowin.util.ItemStackList;

public class TileEntityGameHub extends TileEntityConnectionHub implements IBlueprintProvider {
    
    private ArrayList<TileEntityTeamHub> connectedTeamHubs;
    
    private long plannedTimespan = 0;
    
    private long deadline = 0;
    
    private long sleptTime = 0;
    
    private ItemStackList shop;
    
    public TileEntityGameHub() {
        super(new Class[] { TileEntityTeamHub.class });
        
        this.connectedTeamHubs = new ArrayList<TileEntityTeamHub>();
        this.shop = new ItemStackList();
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setLong("plantmspn", this.plannedTimespan);
        
        par1NBTTagCompound.setLong("deadline", this.deadline);
        
        par1NBTTagCompound.setLong("slepttime", this.sleptTime);
        
        par1NBTTagCompound.setTag("shopcontents", this.shop.getTagList());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.plannedTimespan = par1NBTTagCompound.getLong("plantmspn");
        
        this.deadline = par1NBTTagCompound.getLong("deadline");
        
        this.sleptTime = par1NBTTagCompound.getLong("slepttime");
        
        this.shop.readTagList(par1NBTTagCompound.getTagList("shopcontents"));
    }
    
    /*
     * @Override public Packet getDescriptionPacket() { NBTTagCompound tag = new
     * NBTTagCompound(); this.writeToNBT(tag);
     * 
     * return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord,
     * 1, tag); }
     */
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        NBTTagCompound tag = pkt.customParam1;
        this.readFromNBT(tag);
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeLong(this.plannedTimespan);
        dataOutputStream.writeLong(this.deadline);
        dataOutputStream.writeLong(this.sleptTime);
        
        return true;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream inputStream) throws IOException {
        this.plannedTimespan = inputStream.readLong();
        this.deadline = inputStream.readLong();
        this.sleptTime = inputStream.readLong();
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            this.checkGameStatus();
        }
        
        super.updateEntity();
    }
    
    @Override
    protected void onSynchronization() {
        this.updateConnectedTeamHubs();
        
        super.onSynchronization();
    }
    
    public void refreshTimespan(long newTimespan) {
        if (this.deadline != 0) {
            this.deadline = this.getRealWorldTime() + newTimespan;
        }
        
        this.plannedTimespan = newTimespan;
    }
    
    @Override
    public void loadBlueprint(Blueprint blueprint) {
        this.updateConnectedTeamHubs();
        
        for (TileEntityTeamHub teamHub : this.connectedTeamHubs) {
            teamHub.loadBlueprint(blueprint);
            teamHub.getBlueprint().reset();
        }
    }
    
    public void startGame() {
        this.updateConnectedTeamHubs();
        
        this.sleptTime = 0;
        this.deadline = this.worldObj.getTotalWorldTime() + this.plannedTimespan;
        
        for (TileEntityTeamHub teamHub : this.connectedTeamHubs) {
            teamHub.getBlueprint().reset();
            teamHub.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has started."));
        }
    }
    
    public void stopGame(boolean notify) {
        this.updateConnectedTeamHubs();
        
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
                                losingTeamHub.sendLoseMessage(ranking.indexOf(losingTeamHub) + 1);
                            }
                        }
                        
                        this.stopGame(false);
                        break;
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
    
    public void updateConnectedTeamHubs() {
        this.connectedTeamHubs.clear();
        this.updateConnections();
    }
    
    @Override
    public void onConnectionEstablished(TileEntity tileEntity) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) tileEntity;
        
        teamHub.setGameHub(this);
        teamHub.getBlueprint().setColor((byte) this.connectedTeamHubs.size());
        
        this.connectedTeamHubs.add(teamHub);
    }
    
    public ArrayList<TileEntityTeamHub> getConnectedTeamHubs() {
        return connectedTeamHubs;
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
    
    public ItemStackList getShop() {
        return shop;
    }
    
    public void setShop(ItemStackList shop) {
        this.shop = shop;
    }
}

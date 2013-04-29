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
import buildtowin.util.Color;
import buildtowin.util.ItemStackList;
import buildtowin.util.TileEntityList;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityGameHub extends TileEntityConnectionHub implements IBlueprintProvider {
    
    private TileEntityList connectedTeamHubs = new TileEntityList();
    
    private Blueprint blueprint = new Blueprint(this);
    
    private long plannedTimespan;
    
    private long deadline;
    
    private long sleptTime;
    
    private ItemStackList shop = new ItemStackList(27);
    
    public TileEntityGameHub() {
        super(new Class[] { TileEntityTeamHub.class });
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setIntArray("blueprint", this.blueprint.encode());
        
        par1NBTTagCompound.setLong("plantmspn", this.plannedTimespan);
        
        par1NBTTagCompound.setLong("deadline", this.deadline);
        
        par1NBTTagCompound.setLong("slepttime", this.sleptTime);
        
        par1NBTTagCompound.setTag("shopcontents", this.shop.getTagList());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.blueprint.decode(par1NBTTagCompound.getIntArray("blueprint"));
        
        this.plannedTimespan = par1NBTTagCompound.getLong("plantmspn");
        
        this.deadline = par1NBTTagCompound.getLong("deadline");
        
        this.sleptTime = par1NBTTagCompound.getLong("slepttime");
        
        this.shop.readTagList(par1NBTTagCompound.getTagList("shopcontents"));
    }
    
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
        
        this.connectedTeamHubs.writeDescriptionPacket(dataOutputStream);
        
        return true;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.plannedTimespan = dataInputStream.readLong();
        this.deadline = dataInputStream.readLong();
        this.sleptTime = dataInputStream.readLong();
        
        this.connectedTeamHubs.readDescriptionPacket(dataInputStream);
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
        this.getConnectedTeamHubs().clear();
        this.updateConnections();
        
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
        this.blueprint = blueprint;
        
        for (TileEntity tileEntity : this.getConnectedTeamHubs()) {
            TileEntityTeamHub teamHub = (TileEntityTeamHub) tileEntity;
            teamHub.loadBlueprint(blueprint);
        }
    }
    
    public void startGame() {
        this.sleptTime = 0;
        this.deadline = this.worldObj.getTotalWorldTime() + this.plannedTimespan;
        this.shop.clear();
        
        for (TileEntity tileEntity : this.getConnectedTeamHubs()) {
            TileEntityTeamHub teamHub = (TileEntityTeamHub) tileEntity;
            teamHub.getBlueprint().reset();
            
            teamHub.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has started."));
        }
    }
    
    public void stopGame(boolean notify) {
        this.sleptTime = 0;
        this.deadline = 0;
        
        for (TileEntity tileEntity : this.getConnectedTeamHubs()) {
            TileEntityTeamHub teamHub = (TileEntityTeamHub) tileEntity;
            teamHub.getBlueprint().reset();
            
            if (notify) {
                teamHub.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has been stopped."));
            }
        }
    }
    
    private void checkGameStatus() {
        if (this.deadline != 0) {
            if (this.deadline >= this.getRealWorldTime()) {
                for (TileEntity tileEntity : this.getConnectedTeamHubs()) {
                    TileEntityTeamHub teamHub = (TileEntityTeamHub) tileEntity;
                    
                    if (teamHub.getFinishedBlockCount() == teamHub.getBlueprint().getBlocks().size()) {
                        teamHub.sendWinMessage();
                        
                        ArrayList<TileEntityTeamHub> ranking = this.getRanking();
                        
                        for (TileEntity losingTeamHub : this.getConnectedTeamHubs()) {
                            if (losingTeamHub != teamHub) {
                                ((TileEntityTeamHub) losingTeamHub).sendLoseMessage(ranking.indexOf(losingTeamHub) + 1);
                            }
                        }
                        
                        this.stopGame(false);
                        break;
                    }
                }
            } else {
                ArrayList<TileEntityTeamHub> ranking = this.getRanking();
                
                for (TileEntity losingTeamHub : this.getConnectedTeamHubs()) {
                    ((TileEntityTeamHub) losingTeamHub).sendLoseMessage(ranking.indexOf(losingTeamHub));
                }
                
                this.stopGame(false);
            }
        }
    }
    
    public ArrayList<TileEntityTeamHub> getRanking() {
        HashMap<Float, TileEntityTeamHub> unsortedTeamHubs = new HashMap<Float, TileEntityTeamHub>();
        ArrayList<Float> progressValues = new ArrayList<Float>();
        
        for (TileEntity tileEntity : this.getConnectedTeamHubs()) {
            TileEntityTeamHub teamHub = (TileEntityTeamHub) tileEntity;
            
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
    
    @Override
    public void onConnectionEstablished(TileEntity tileEntity) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) tileEntity;
        teamHub.setGameHub(this);
        
        if (teamHub.getColor().id != this.getConnectedTeamHubs().size() + 1) {
            teamHub.setColor(Color.fromId(this.getConnectedTeamHubs().size() + 1));
            teamHub.getBlueprint().refresh(true);
            
            PacketDispatcher.sendPacketToAllPlayers(teamHub.getDescriptionPacket());
        }
        
        if (this.blueprint != null && teamHub.getBlueprint().getBlocks().size() != this.blueprint.getBlocks().size()) {
            teamHub.loadBlueprint(this.blueprint);
        }
        
        this.getConnectedTeamHubs().add(tileEntity);
    }
    
    public ArrayList<TileEntity> getConnectedTeamHubs() {
        return this.connectedTeamHubs.getTileEntityList(this.worldObj);
    }
    
    public long getRealWorldTime() {
        return this.worldObj.getTotalWorldTime() + this.sleptTime;
    }
    
    public long getPlannedTimespan() {
        return this.plannedTimespan;
    }
    
    public void setPlannedTimespan(long plannedTimespan) {
        this.plannedTimespan = plannedTimespan;
    }
    
    public long getDeadline() {
        return this.deadline;
    }
    
    public long getSleptTime() {
        return this.sleptTime;
    }
    
    public void setSleptTime(long sleptTime) {
        this.sleptTime = sleptTime;
    }
    
    public ItemStackList getShop() {
        return this.shop;
    }
    
    public void setShop(ItemStackList shop) {
        this.shop = shop;
    }
    
    @Override
    public Color getColor() {
        return null;
    }
}

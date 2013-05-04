package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildtowin.network.PacketIds;
import buildtowin.penalization.Penalization;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityPenalizer extends TileEntity implements ITeamHubExtension {
    
    private TileEntityTeamHub teamHub;
    
    private ArrayList<Penalization> penalizationQueue = new ArrayList<Penalization>();
    
    private ArrayList<TileEntityTeamHub> penalizingTeamHubQueue = new ArrayList<TileEntityTeamHub>();
    
    private int repetitionsLeft;
    
    private Random random = new Random();
    
    public void sendPenalizePacket(int type, int selectedTeam) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.PENALIZER_PENALIZE);
            
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            dataoutputstream.writeInt(type);
            dataoutputstream.writeInt(selectedTeam);
            
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void onPenalizePacket(DataInputStream dataInputStream) throws IOException {
        if (this.teamHub == null || this.teamHub.getGameHub() == null) {
            return;
        }
        
        int type = dataInputStream.readInt();
        int selectedTeam = dataInputStream.readInt();
        
        if (type >= 0 && type < Penalization.penalizationList.length
                && selectedTeam >= 0 && selectedTeam < this.teamHub.getGameHub().getConnectedTeamHubs().size()) {
            int price = Penalization.penalizationList[type].getPrice(this.teamHub);
            
            if (this.teamHub.getCoins() < price) {
                return;
            }
            
            this.teamHub.setCoins(this.teamHub.getCoins() - price);
            
            this.penalizationQueue.add(Penalization.penalizationList[type]);
            this.penalizingTeamHubQueue.add(this.teamHub);
            this.repetitionsLeft = this.penalizationQueue.get(0).getRepetitions(this.teamHub);
        }
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (!this.penalizationQueue.isEmpty()) {
                if (this.repetitionsLeft > 0) {
                    if (this.random.nextInt(this.penalizationQueue.get(0).getChance(this.penalizingTeamHubQueue.get(0))) == 0) {
                        this.penalizationQueue.get(0).penalize(this.penalizingTeamHubQueue.get(0));
                        --this.repetitionsLeft;
                    }
                } else {
                    this.penalizationQueue.remove(0);
                    this.penalizingTeamHubQueue.remove(0);
                    
                    if (!this.penalizationQueue.isEmpty()) {
                        this.repetitionsLeft = this.penalizationQueue.get(0).getRepetitions(this.teamHub);
                    }
                }
            }
        }
        
        super.updateEntity();
    }
    
    @Override
    public TileEntityTeamHub getTeamHub() {
        if (this.teamHub != null && this.teamHub.isInvalid()) {
            this.teamHub = null;
        }
        
        return this.teamHub;
    }
    
    @Override
    public void setTeamHub(TileEntityTeamHub teamHub) {
        this.teamHub = teamHub;
    }
}

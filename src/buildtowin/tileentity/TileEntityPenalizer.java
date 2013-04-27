package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildtowin.network.PacketIds;
import buildtowin.penalization.Penalization;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityPenalizer extends TileEntity implements ITeamHubExtension {
    
    private TileEntityTeamHub teamHub;
    
    private Penalization activePenalization;
    
    private int strength;
    
    private int repetitionsLeft;
    
    private Random random;
    
    public TileEntityPenalizer() {
        this.strength = 0;
        this.repetitionsLeft = 0;
        this.random = new Random();
    }
    
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
            this.penalizeTeam(Penalization.penalizationList[type], (TileEntityTeamHub) this.teamHub.getGameHub().getConnectedTeamHubs().get(selectedTeam));
        }
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (this.activePenalization != null && this.repetitionsLeft > 0) {
                if (this.random.nextInt(this.activePenalization.getChance(this.teamHub, this.strength)) == 0) {
                    this.activePenalization.penalize(this.teamHub, this.strength);
                    --this.repetitionsLeft;
                }
            }
        }
        
        super.updateEntity();
    }
    
    private void penalizeTeam(Penalization penalization, TileEntityTeamHub teamHub) {
        int price = penalization.getPrice(teamHub, 1);
        
        if (this.teamHub.getEnergy() < price) {
            return;
        }
        
        this.teamHub.setEnergy(this.teamHub.getEnergy() - price);
        
        for (TileEntity tileEntity : teamHub.getExtensionList()) {
            if (tileEntity instanceof TileEntityPenalizer) {
                ((TileEntityPenalizer) tileEntity).penalize(penalization, 1);
            }
        }
    }
    
    private void penalize(Penalization penalization, int strength) {
        this.activePenalization = penalization;
        this.strength = strength;
        this.repetitionsLeft = penalization.getRepetitions(this.teamHub, strength);
    }
    
    @Override
    public void setTeamHub(TileEntityTeamHub teamHub) {
        this.teamHub = teamHub;
    }
    
    public TileEntityTeamHub getTeamHub() {
        if (this.teamHub != null && this.teamHub.isInvalid()) {
            this.teamHub = null;
        }
        
        return this.teamHub;
    }
}

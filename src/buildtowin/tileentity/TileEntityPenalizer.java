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

public class TileEntityPenalizer extends TileEntitySynchronized implements ITeamHubExtension {
    
    private TileEntityTeamHub teamHub;
    
    private Penalization activePenalization;
    
    private int strength;
    
    private int repetitionsLeft;
    
    private int clientPrices[];
    
    private Random random;
    
    public TileEntityPenalizer() {
        this.strength = 0;
        this.repetitionsLeft = 0;
        this.clientPrices = new int[Penalization.penalizationList.length];
        this.random = new Random();
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        for (Penalization penalization : Penalization.penalizationList) {
            dataOutputStream.writeInt(this.getPrice(penalization, 1));
        }
        
        return true;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        for (int i = 0; i < Penalization.penalizationList.length; ++i) {
            this.clientPrices[i] = dataInputStream.readInt();
        }
    }
    
    public void sendPenalizePacket(int type, int strength) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.PENALIZER_PENALIZE);
            
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            dataoutputstream.writeInt(type);
            dataoutputstream.writeInt(strength);
            
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void onPenalizePacket(DataInputStream dataInputStream) throws IOException {
        if (this.teamHub == null) {
            return;
        }
        
        int type = dataInputStream.readInt();
        int strength = dataInputStream.readInt();
        
        this.penalizeOtherTeams(type, strength);
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
    
    private void penalizeOtherTeams(int type, int strength) {
        Penalization penalization = null;
        
        try {
            penalization = Penalization.penalizationList[type];
        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
            return;
        }
        
        int price = this.getPrice(penalization, strength);
        
        if (this.teamHub.getEnergy() < price) {
            return;
        }
        
        this.teamHub.setEnergy(this.teamHub.getEnergy() - price);
        
        ArrayList<TileEntity> teamHubs = this.teamHub.getGameHub().getConnectedTeamHubs();
        
        for (TileEntity teamHub : teamHubs) {
            if (teamHub != this.teamHub) {
                for (TileEntity tileEntity : ((TileEntityTeamHub) teamHub).getExtensionList()) {
                    if (tileEntity instanceof TileEntityPenalizer) {
                        ((TileEntityPenalizer) tileEntity).penalize(penalization, strength);
                    }
                }
            }
        }
    }
    
    private void penalize(Penalization penalization, int strength) {
        this.activePenalization = penalization;
        this.strength = strength;
        this.repetitionsLeft = penalization.getRepetitions(this.teamHub, strength);
    }
    
    public int getPrice(Penalization penalization, int strength) {
        int price = 0;
        
        if (this.teamHub == null || this.teamHub.getGameHub() == null) {
            return 0;
        }
        
        for (TileEntity teamHub : this.teamHub.getGameHub().getConnectedTeamHubs()) {
            if (teamHub != this.teamHub) {
                price += penalization.getPrice((TileEntityTeamHub) teamHub, strength);
            }
        }
        
        return price;
    }
    
    public int getPriceClient(Penalization penalization, int strength) {
        return this.clientPrices[penalization.penalizationId];
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

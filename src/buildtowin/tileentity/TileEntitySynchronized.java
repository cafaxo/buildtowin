package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildtowin.network.PacketIds;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class TileEntitySynchronized extends TileEntity {
    
    private short synchronizationTimer;
    
    public TileEntitySynchronized() {
        this.synchronizationTimer = 0;
    }
    
    public abstract void readDescriptionPacket(DataInputStream dataInputStream) throws IOException;
    
    public abstract boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException;
    
    public final Packet getDescriptionPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.TILEENTITY_UPDATE);
            
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            if (this.writeDescriptionPacket(dataoutputstream)) {
                return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    protected void onSynchronization() {
        Packet descriptionPacket = this.getDescriptionPacket();
        
        if (descriptionPacket != null) {
            PacketDispatcher.sendPacketToAllPlayers(descriptionPacket);
        }
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (this.synchronizationTimer == 30) {
                this.onSynchronization();
                this.synchronizationTimer = 0;
            }
            
            ++this.synchronizationTimer;
        }
    }
}

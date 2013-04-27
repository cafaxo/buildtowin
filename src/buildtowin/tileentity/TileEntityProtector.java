package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import buildtowin.network.PacketIds;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityProtector extends TileEntitySynchronized implements ITeamHubExtension {
    
    private TileEntityTeamHub teamHub;
    
    private int radius;
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("radius", this.radius);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.radius = par1NBTTagCompound.getInteger("radius");
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.radius);
        return true;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.radius = dataInputStream.readInt();
    }
    
    public boolean sendRadiusChangedPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.PROTECTOR_RADIUS_UPDATE);
            
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            dataoutputstream.writeInt(this.radius);
            
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return true;
    }
    
    public void onRadiusChanged(DataInputStream dataInputStream) throws IOException {
        int newRadius = dataInputStream.readInt();
        
        if (this.teamHub.getEnergy() >= this.getPrice(newRadius)) {
            this.teamHub.setEnergy(this.teamHub.getEnergy() - this.getPrice(newRadius));
            this.radius = newRadius;
        }
    }
    
    public boolean isBlockProtected(int x, int y, int z) {
        return x > this.xCoord - this.radius && x < this.xCoord + this.radius
                && y > this.yCoord - this.radius && y < this.yCoord + this.radius
                && z > this.zCoord - this.radius && z < this.zCoord + this.radius;
    }
    
    public int getPrice(int targetRadius) {
        return (targetRadius * targetRadius - this.radius * this.radius) * 2;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
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

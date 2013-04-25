package buildtowin.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTeamHubExtension extends TileEntitySynchronized {
    
    private TileEntityTeamHub teamHub;
    
    private int teamHubX, teamHubY, teamHubZ;
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("teamHubX", this.teamHubX);
        par1NBTTagCompound.setInteger("teamHubY", this.teamHubY);
        par1NBTTagCompound.setInteger("teamHubZ", this.teamHubZ);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.teamHubX = par1NBTTagCompound.getInteger("teamHubX");
        this.teamHubY = par1NBTTagCompound.getInteger("teamHubY");
        this.teamHubZ = par1NBTTagCompound.getInteger("teamHubZ");
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        if (this.getTeamHub() != null) {
            dataOutputStream.writeInt(this.teamHubX);
            dataOutputStream.writeInt(this.teamHubY);
            dataOutputStream.writeInt(this.teamHubZ);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                dataInputStream.readInt(),
                dataInputStream.readInt(),
                dataInputStream.readInt());
        
        if (tileEntity != null) {
            this.setTeamHub((TileEntityTeamHub) tileEntity);
        }
    }
    
    public TileEntityTeamHub getTeamHub() {
        if (this.teamHub == null) {
            if (this.teamHubX != 0 && this.teamHubY != 0 && this.teamHubZ != 0) {
                this.setTeamHub((TileEntityTeamHub) this.worldObj.getBlockTileEntity(this.teamHubX, this.teamHubY, this.teamHubZ));
            }
        }
        
        return this.teamHub;
    }
    
    public void setTeamHub(TileEntityTeamHub teamHub) {
        if (this.teamHub != null) {
            this.teamHub.getExtensionList().remove(this);
        }
        
        if (teamHub != null) {
            teamHub.getExtensionList().add(this);
            
            this.teamHubX = teamHub.xCoord;
            this.teamHubY = teamHub.yCoord;
            this.teamHubZ = teamHub.zCoord;
        } else {
            this.teamHubX = 0;
            this.teamHubY = 0;
            this.teamHubZ = 0;
        }
        
        this.teamHub = teamHub;
    }
}

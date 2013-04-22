package buildtowin.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityProtector extends TileEntitySynchronized {
    
    private int radius;
    
    public TileEntityProtector() {
        this.radius = 5;
    }
    
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
    public void readDescriptionPacket(DataInputStream inputStream) throws IOException {
        this.radius = inputStream.readInt();
    }
    
    public boolean isBlockProtected(int x, int y, int z) {
        return x > this.xCoord - radius && x < this.xCoord + radius
                && y > this.yCoord - radius && y < this.yCoord + radius
                && z > this.zCoord - radius && z < this.zCoord + radius;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
    }
}

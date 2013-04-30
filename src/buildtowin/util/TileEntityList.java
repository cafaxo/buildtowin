package buildtowin.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityList {
    
    private int tileEntitiesToLoad[];
    
    private ArrayList<TileEntity> tileEntityList = new ArrayList<TileEntity>();
    
    public int[] encode() {
        int[] encodedData = new int[this.tileEntityList.size() * 3];
        
        for (int i = 0; i < this.tileEntityList.size(); ++i) {
            TileEntity tileEntity = this.tileEntityList.get(i);
            
            if (tileEntity != null) {
                encodedData[i * 3] = tileEntity.xCoord;
                encodedData[i * 3 + 1] = tileEntity.yCoord;
                encodedData[i * 3 + 2] = tileEntity.zCoord;
            }
        }
        
        return encodedData;
    }
    
    public void decode(int encodedData[]) {
        this.tileEntitiesToLoad = encodedData;
    }
    
    public void writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.tileEntityList.size() * 3);
        
        for (TileEntity tileEntity : this.tileEntityList) {
            dataOutputStream.writeInt(tileEntity.xCoord);
            dataOutputStream.writeInt(tileEntity.yCoord);
            dataOutputStream.writeInt(tileEntity.zCoord);
        }
    }
    
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.tileEntitiesToLoad = new int[dataInputStream.readInt()];
        
        for (int i = 0; i < this.tileEntitiesToLoad.length; ++i) {
            this.tileEntitiesToLoad[i] = dataInputStream.readInt();
        }
    }
    
    public ArrayList<TileEntity> getTileEntityList(World worldObj) {
        if (this.tileEntitiesToLoad != null) {
            this.tileEntityList.clear();
            
            for (int i = 0; i < this.tileEntitiesToLoad.length / 3; ++i) {
                this.tileEntityList.add(worldObj.getBlockTileEntity(
                        this.tileEntitiesToLoad[i * 3],
                        this.tileEntitiesToLoad[i * 3 + 1],
                        this.tileEntitiesToLoad[i * 3 + 2]));
            }
            
            this.tileEntitiesToLoad = null;
        }
        
        return this.tileEntityList;
    }
}

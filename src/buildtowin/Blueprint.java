package buildtowin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class Blueprint {
    private String name;
    
    private String author;
    
    private ArrayList<BlockData> blockDataList;
    
    public Blueprint() {
        this.name = "undefined";
        this.author = "undefined";
    }
    
    public Blueprint(String name, String author, ArrayList<BlockData> blockDataList) {
        this.name = name;
        this.author = author;
        this.blockDataList = blockDataList;
    }
    
    public boolean read(File file) {
        FileInputStream fileInputStream;
        
        try {
            fileInputStream = new FileInputStream(file);
            
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            
            this.name = dataInputStream.readUTF();
            this.author = dataInputStream.readUTF();
            
            this.blockDataList = new ArrayList<BlockData>();
            int blockCount = dataInputStream.readInt();
            
            for (int i = 0; i < blockCount; ++i) {
                BlockData blockData = new BlockData(
                        dataInputStream.readInt(),
                        dataInputStream.readInt(),
                        dataInputStream.readInt(),
                        dataInputStream.readShort(),
                        dataInputStream.readByte());
                
                this.blockDataList.add(blockData);
            }
            
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean readSchematic(File file) {
        NBTTagCompound nbt;
        try {
            nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
            
            this.name = file.getName();
            this.author = "Unknown";
            
            int width = nbt.getShort("Width");
            int height = nbt.getShort("Height");
            int length = nbt.getShort("Length");
            
            byte[] blockIds = nbt.getByteArray("Blocks");
            byte[] blockMetadata = nbt.getByteArray("Data");
            
            this.blockDataList = new ArrayList<BlockData>();
            
            int index = 0;
            
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++) {
                        if (blockIds[index] != 0) {
                            BlockData blockData = new BlockData(
                                    x,
                                    y,
                                    z,
                                    blockIds[index],
                                    blockMetadata[index]);
                            
                            this.blockDataList.add(blockData);
                        }
                        
                        ++index;
                    }
                }
            }
            
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean write(File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            
            dataOutputStream.writeUTF(this.name);
            dataOutputStream.writeUTF(this.author);
            
            dataOutputStream.writeInt(this.blockDataList.size());
            
            for (BlockData blockData : this.blockDataList) {
                dataOutputStream.writeInt(blockData.x);
                dataOutputStream.writeInt(blockData.y);
                dataOutputStream.writeInt(blockData.z);
                dataOutputStream.writeShort(blockData.id);
                dataOutputStream.writeByte(blockData.metadata);
            }
            
            dataOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public ArrayList<BlockData> getBlockDataList() {
        return blockDataList;
    }
}

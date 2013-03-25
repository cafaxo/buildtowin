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

public class Blueprint {
    private String name;
    
    private String author;
    
    private ArrayList<BlockData> blockDataList;
    
    public Blueprint(File file) {
        this.read(file);
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
                        dataInputStream.readInt(),
                        dataInputStream.readInt());
                
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
                dataOutputStream.writeInt(blockData.id);
                dataOutputStream.writeInt(blockData.metadata);
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

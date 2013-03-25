package buildtowin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.network.packet.Packet250CustomPayload;

public class BlueprintList {
    
    private File blueprintDir;
    
    private ArrayList<Blueprint> blueprintList = new ArrayList<Blueprint>();
    
    public ArrayList<Blueprint> getBlueprintList() {
        return blueprintList;
    }
    
    public BlueprintList() {
    }
    
    public BlueprintList(String baseDir) {
        this.blueprintDir = new File(baseDir, "blueprints");
        System.out.println(baseDir);
    }
    
    public boolean read() {
        if (this.blueprintDir.isDirectory()) {
            List<File> blueprintFiles = Arrays.asList(this.blueprintDir.listFiles());
            
            for (File blueprintFile : blueprintFiles) {
                Blueprint blueprint = new Blueprint(blueprintFile);
                this.blueprintList.add(blueprint);
            }
            
            return true;
        } else {
            this.blueprintDir.delete();
            this.blueprintDir.mkdirs();
            
            return false;
        }
    }
    
    public Packet250CustomPayload getDescriptionPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(blueprintList.size());
            
            for (Blueprint blueprint : blueprintList) {
                dataoutputstream.writeUTF(blueprint.getName());
                dataoutputstream.writeUTF(blueprint.getAuthor());
            }
            
            return new Packet250CustomPayload("btwbpupdt", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void onDataPacket(Packet250CustomPayload packet) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        this.blueprintList.clear();
        
        try {
            int blueprintCount = inputStream.readInt();
            
            for (int i = 0; i < blueprintCount; ++i) {
                Blueprint blueprint = new Blueprint(inputStream.readUTF(), inputStream.readUTF(), null);
                this.blueprintList.add(blueprint);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void save(ArrayList<BlockData> blockDataList, String name, String author) {
        Blueprint blueprint = new Blueprint(name, author, blockDataList);
        blueprint.write(new File(this.blueprintDir.getAbsolutePath() + "/" + name + ".blueprint"));
        
        this.blueprintList.add(blueprint);
    }
}

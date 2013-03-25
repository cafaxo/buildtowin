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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

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
    }
    
    public boolean read() {
        if (this.blueprintDir.isDirectory()) {
            List<File> blueprintFiles = Arrays.asList(this.blueprintDir.listFiles());
            
            for (File blueprintFile : blueprintFiles) {
                if (blueprintFile.getAbsolutePath().endsWith(".blueprint")) {
                    
                    Blueprint blueprint = new Blueprint();
                    
                    if (blueprint.read(blueprintFile)) {
                        
                        this.blueprintList.add(blueprint);
                    }
                }
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
    
    public boolean save(ArrayList<BlockData> blockDataList, EntityPlayer player, String name) {
        if (name.isEmpty()) {
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> The name must not be empty."), (Player) player);
            return false;
        }
        
        for (Blueprint blueprint : this.blueprintList) {
            if (name.equals(blueprint.getName())) {
                PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> This name is already in use."), (Player) player);
                return false;
            }
        }
        
        Blueprint blueprint = new Blueprint(name, player.username, blockDataList);
        blueprint.write(new File(this.blueprintDir.getAbsolutePath() + "/" + name + ".blueprint"));
        
        this.blueprintList.add(blueprint);
        
        PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Saved the blueprint successfully."), (Player) player);
        
        return true;
    }
}

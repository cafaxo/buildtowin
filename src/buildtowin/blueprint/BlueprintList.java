package buildtowin.blueprint;

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
import buildtowin.BuildToWin;
import buildtowin.network.PacketIds;
import buildtowin.tileentity.TileEntityBuildingHub;
import cpw.mods.fml.common.network.PacketDispatcher;

public class BlueprintList {
    
    public static BlueprintList serverInstance = new BlueprintList();
    
    public static BlueprintList clientInstance = new BlueprintList();
    
    private File blueprintDir;
    
    private ArrayList<Blueprint> blueprintList = new ArrayList<Blueprint>();
    
    public boolean init(File blueprintDir) {
        this.blueprintDir = blueprintDir;
        
        if (blueprintDir.isDirectory()) {
            List<File> blueprintFiles = Arrays.asList(blueprintDir.listFiles());
            
            for (File blueprintFile : blueprintFiles) {
                if (blueprintFile.getAbsolutePath().endsWith("blueprint")) {
                    Blueprint blueprint = new Blueprint(null);
                    
                    if (blueprint.loadBlueprintFile(blueprintFile)) {
                        this.blueprintList.add(blueprint);
                    }
                } else if (blueprintFile.getAbsolutePath().endsWith("schematic")) {
                    Blueprint blueprint = new Blueprint(null);
                    
                    if (blueprint.loadSchematicFile(blueprintFile)) {
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
            dataoutputstream.writeInt(PacketIds.BLUEPRINTLIST_UPDATE);
            
            dataoutputstream.writeInt(this.blueprintList.size());
            
            for (Blueprint blueprint : this.blueprintList) {
                dataoutputstream.writeUTF(blueprint.getName());
                
                if (blueprint.getAuthors() != null) {
                    dataoutputstream.writeInt(blueprint.getAuthors().size());
                    
                    for (String author : blueprint.getAuthors()) {
                        dataoutputstream.writeUTF(author);
                    }
                } else {
                    dataoutputstream.writeInt(0);
                }
            }
            
            return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.blueprintList.clear();
        
        int blueprintCount = dataInputStream.readInt();
        
        for (int i = 0; i < blueprintCount; ++i) {
            Blueprint blueprint = new Blueprint(null);
            blueprint.setName(dataInputStream.readUTF());
            
            int authorCount = dataInputStream.readInt();
            
            for (int j = 0; j < authorCount; ++j) {
                blueprint.getAuthors().add(dataInputStream.readUTF());
            }
            
            this.blueprintList.add(blueprint);
        }
    }
    
    public boolean saveBlueprint(TileEntityBuildingHub buildingHub, EntityPlayer player, String name) {
        if (name.isEmpty()) {
            BuildToWin.sendChatMessage(player, "The name must not be empty.");
            return false;
        }
        
        for (Blueprint blueprint : this.blueprintList) {
            if (name.equals(blueprint.getName())) {
                BuildToWin.sendChatMessage(player, "This name is already in use.");
                return false;
            }
        }
        
        Blueprint blueprint = new Blueprint(null, buildingHub.getBlueprint());
        blueprint.setName(name);
        blueprint.getAuthors().addAll(buildingHub.getPlayerList().getConnectedPlayers());
        
        if (blueprint.writeBlueprintFile(new File(this.blueprintDir.getAbsolutePath() + "/" + name + ".blueprint"))) {
            this.blueprintList.add(blueprint);
            PacketDispatcher.sendPacketToAllPlayers(BlueprintList.serverInstance.getDescriptionPacket());
            
            BuildToWin.sendChatMessage(player, "Saved the blueprint successfully.");
            return true;
        }
        
        return false;
    }
    
    public ArrayList<Blueprint> getBlueprintList() {
        return this.blueprintList;
    }
}

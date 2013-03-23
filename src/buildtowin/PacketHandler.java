package buildtowin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerEntity) {
        if (packet.channel == "btwtimsupdt") {
            this.handleTimespanPacket(packet, playerEntity);
        } else if (packet.channel == "btwstart") {
            this.handleStartPacket(packet, playerEntity);
        } else if (packet.channel == "btwwin") {
            this.handleWinPacket(packet, playerEntity);
        } else if (packet.channel == "btwlose") {
            this.handleLosePacket(packet, playerEntity);
        }
    }
    
    private void handleStartPacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                buildingController.connectPlayer(player);
                buildingController.setDeadline(player.worldObj.getTotalWorldTime() + buildingController.getPlannedTimespan());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handleTimespanPacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            long plannedTimespan = inputStream.readLong();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                if (buildingController.getDeadline() != 0) {
                    buildingController.setDeadline(buildingController.worldObj.getTotalWorldTime() + plannedTimespan);
                }
                
                buildingController.setPlannedTimespan(plannedTimespan);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handleWinPacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                buildingController.setDeadline(0);
                
                Minecraft mc = FMLClientHandler.instance().getClient();
                
                mc.ingameGUI.getChatGUI().printChatMessage("<BuildToWin> You have won, " + ((EntityPlayer) playerEntity).username + "!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handleLosePacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                buildingController.setDeadline(0);
                
                Minecraft mc = FMLClientHandler.instance().getClient();
                
                mc.ingameGUI.getChatGUI().printChatMessage("<BuildToWin> You have lost, " + ((EntityPlayer) playerEntity).username + "!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

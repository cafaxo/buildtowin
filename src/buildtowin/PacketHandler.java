package buildtowin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
    
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerEntity) {
        if (packet.channel.equals("btwbcupdt")) {
            this.handleBuildingControllerPacket(packet, playerEntity);
        } else if (packet.channel.equals("btwtimsupdt")) {
            this.handleTimespanPacket(packet, playerEntity);
        } else if (packet.channel.equals("btwbpupdt")) {
            this.handleBlueprintListPacket(packet, playerEntity);
        } else if (packet.channel.equals("btwbpsav")) {
            this.handleBlueprintSavePacket(packet, playerEntity);
        } else if (packet.channel.equals("btwbpload")) {
            this.handleBlueprintLoadPacket(packet, playerEntity);
        } else if (packet.channel.equals("btwstart")) {
            this.handleStartPacket(packet, playerEntity);
        } else if (packet.channel.equals("btwstop")) {
            this.handleStopPacket(packet, playerEntity);
        }
    }
    
    private void handleBuildingControllerPacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                buildingController.onDataPacketOptimized(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleBlueprintListPacket(Packet250CustomPayload packet, Player playerEntity) {
        BuildToWin.blueprintListClient.onDataPacket(packet);
    }
    
    private void handleBlueprintSavePacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            
            String name = inputStream.readUTF();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                BuildToWin.blueprintListServer.save(buildingController.getBlockDataListRelative(), (EntityPlayer) playerEntity, name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleBlueprintLoadPacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            
            int blueprintIndex = inputStream.readInt();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                if (blueprintIndex < BuildToWin.blueprintListServer.getBlueprintList().size()) {
                    buildingController.loadBlueprintRelative(BuildToWin.blueprintListServer.getBlueprintList().get(blueprintIndex).getBlockDataList(), true);
                    PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Loaded the blueprint successfully."), playerEntity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                buildingController.startGame(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleStopPacket(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                buildingController.stopGame(true);
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
                buildingController.refreshTimespan(plannedTimespan, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

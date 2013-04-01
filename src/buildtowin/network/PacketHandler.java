package buildtowin.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.Blueprint;
import buildtowin.blueprint.BlueprintList;
import buildtowin.blueprint.IBlueprintProvider;
import buildtowin.tileentity.TileEntityBuildingHub;
import buildtowin.tileentity.TileEntityGameHub;
import buildtowin.tileentity.TileEntityTeamHub;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
    
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int packetId = dataInputStream.readInt();
            
            if (packetId == PacketIds.BLUEPRINTLIST_UPDATE) {
                BlueprintList.blueprintListClient.onUpdatePacket(dataInputStream);
            } else {
                int x = dataInputStream.readInt();
                int y = dataInputStream.readInt();
                int z = dataInputStream.readInt();
                
                TileEntity tileEntity = ((EntityPlayer) player).worldObj.getBlockTileEntity(x, y, z);
                
                if (tileEntity != null) {
                    switch (packetId) {
                    case PacketIds.PLAYERLIST_UPDATE:
                        ((IPlayerListProvider) tileEntity).getPlayerList().onUpdatePacket(dataInputStream);
                        break;
                    case PacketIds.BLUEPRINT_LOAD:
                        Blueprint blueprint = BlueprintList.blueprintListServer.getBlueprintList().get(dataInputStream.readInt());
                        ((IBlueprintProvider) tileEntity).loadBlueprint(blueprint);
                        
                        break;
                    case PacketIds.BLUEPRINT_SAVE:
                        BlueprintList.blueprintListServer.saveBlueprint(
                                (TileEntityBuildingHub) tileEntity,
                                (EntityPlayer) player,
                                dataInputStream.readUTF());
                        
                        break;
                    case PacketIds.GAMEHUB_TIMESPAN_UPDATE:
                        ((TileEntityGameHub) tileEntity).refreshTimespan(dataInputStream.readLong());
                        break;
                    case PacketIds.GAMEHUB_START:
                        ((TileEntityGameHub) tileEntity).startGame();
                        break;
                    case PacketIds.GAMEHUB_STOP:
                        ((TileEntityGameHub) tileEntity).stopGame(true);
                        break;
                    case PacketIds.GAMEHUB_UPDATE:
                        ((TileEntityGameHub) tileEntity).onUpdatePacket(dataInputStream);
                        break;
                    case PacketIds.TEAMHUB_UPDATE:
                        ((TileEntityTeamHub) tileEntity).onUpdatePacket(dataInputStream);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

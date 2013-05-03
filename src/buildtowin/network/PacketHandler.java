package buildtowin.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.Blueprint;
import buildtowin.blueprint.BlueprintList;
import buildtowin.client.gui.GuiScreenLose;
import buildtowin.client.gui.GuiScreenWin;
import buildtowin.tileentity.IBlueprintProvider;
import buildtowin.tileentity.TileEntityBuildingHub;
import buildtowin.tileentity.TileEntityGameHub;
import buildtowin.tileentity.TileEntityPenalizer;
import buildtowin.tileentity.TileEntityProtector;
import buildtowin.tileentity.TileEntitySynchronized;
import buildtowin.util.PriceList;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler implements IPacketHandler {
    
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int packetId = dataInputStream.readInt();
            
            switch (packetId) {
            case PacketIds.BLUEPRINTLIST_UPDATE:
                BlueprintList.clientInstance.readDescriptionPacket(dataInputStream);
                return;
            case PacketIds.TEAMHUB_WIN:
                this.displayWinMessage();
                return;
            case PacketIds.TEAMHUB_LOSE:
                this.displayLoseMessage();
                return;
            case PacketIds.PRICELIST_UPDATE:
                PriceList.clientInstance.readDescriptionPacket(dataInputStream);
                return;
            }
            
            int x = dataInputStream.readInt();
            int y = dataInputStream.readInt();
            int z = dataInputStream.readInt();
            
            TileEntity tileEntity = ((EntityPlayer) player).worldObj.getBlockTileEntity(x, y, z);
            
            if (tileEntity != null) {
                switch (packetId) {
                case PacketIds.TILEENTITY_UPDATE:
                    ((TileEntitySynchronized) tileEntity).readDescriptionPacket(dataInputStream);
                    return;
                case PacketIds.BLUEPRINT_LOAD:
                    Blueprint blueprint = BlueprintList.serverInstance.getBlueprintList().get(dataInputStream.readInt());
                    ((IBlueprintProvider) tileEntity).loadBlueprint(blueprint);
                    
                    return;
                case PacketIds.BLUEPRINT_SAVE:
                    BlueprintList.serverInstance.saveBlueprint(
                            (TileEntityBuildingHub) tileEntity,
                            (EntityPlayer) player,
                            dataInputStream.readUTF());
                    
                    return;
                case PacketIds.GAMEHUB_TIMESPAN_UPDATE:
                    ((TileEntityGameHub) tileEntity).refreshTimespan(dataInputStream.readLong());
                    return;
                case PacketIds.GAMEHUB_START:
                    ((TileEntityGameHub) tileEntity).startGame();
                    return;
                case PacketIds.GAMEHUB_STOP:
                    ((TileEntityGameHub) tileEntity).stopGame(true);
                    return;
                case PacketIds.PENALIZER_PENALIZE:
                    ((TileEntityPenalizer) tileEntity).onPenalizePacket(dataInputStream);
                    return;
                case PacketIds.PROTECTOR_RADIUS_UPDATE:
                    ((TileEntityProtector) tileEntity).onRadiusChanged(dataInputStream);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void displayWinMessage() {
        Minecraft mc = FMLClientHandler.instance().getClient();
        mc.displayGuiScreen(new GuiScreenWin());
    }
    
    @SideOnly(Side.CLIENT)
    public void displayLoseMessage() {
        Minecraft mc = FMLClientHandler.instance().getClient();
        mc.displayGuiScreen(new GuiScreenLose());
    }
}

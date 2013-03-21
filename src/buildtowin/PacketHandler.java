package buildtowin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerEntity) {
        if (packet.channel == "btwdeadlnupdt") {
            this.handleDeadlineUpdate(packet, playerEntity);
        }
    }
    
    public void handleDeadlineUpdate(Packet250CustomPayload packet, Player playerEntity) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        try {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int z = inputStream.readInt();
            int deadline = inputStream.readInt();
            
            EntityPlayer player = (EntityPlayer) playerEntity;
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController != null) {
                buildingController.setDeadline(deadline);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package buildtowin;

import net.minecraft.network.packet.Packet15Place;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityXPOrbPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import buildtowin.item.ItemPencil;
import buildtowin.tileentity.TileEntityBlueprint;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import cpw.mods.fml.common.network.PacketDispatcher;

public class EventHandler {
    
    @ForgeSubscribe
    public void onWorldUnload(WorldEvent.Unload event) {
        PlayerList.playerToTileEntityMapClient.clear();
        PlayerList.playerToTileEntityMapServer.clear();
        TileEntityBlueprint.instancesClient.clear();
    }
    
    @ForgeSubscribe
    public void onPlayerInteraction(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            TileEntityTeamHub teamHub = TileEntityTeamHub.getTeamHub(event.entityPlayer);
            
            if (teamHub != null && teamHub.getGameHub() != null) {
                for (TileEntityTeamHub connectedTeamHub : teamHub.getGameHub().getConnectedTeamHubs()) {
                    if (teamHub != connectedTeamHub && connectedTeamHub.getProtector() != null) {
                        if (connectedTeamHub.getProtector().isBlockProtected(event.x, event.y, event.z)) {
                            BuildToWin.sendChatMessage(event.entityPlayer, "This Block is protected.");
                            event.setCanceled(true);
                        }
                    }
                }
            }
        } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            int facedX = event.x;
            int facedY = event.y;
            int facedZ = event.z;
            
            if (event.face == 0) {
                --facedY;
            } else if (event.face == 1) {
                ++facedY;
            } else if (event.face == 2) {
                --facedZ;
            } else if (event.face == 3) {
                ++facedZ;
            } else if (event.face == 4) {
                --facedX;
            } else if (event.face == 5) {
                ++facedX;
            }
            
            int blockId = event.entityPlayer.worldObj.getBlockId(facedX, facedY, facedZ);
            
            if (event.entityPlayer.inventory.getCurrentItem() != null && event.entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemPencil) {
                if (event.entityPlayer.worldObj.isRemote) {
                    PacketDispatcher.sendPacketToServer(new Packet15Place(event.x, event.y, event.z, event.face, null, 0, 0, 0));
                }
                
                if (event.entityPlayer.inventory.getCurrentItem().getItem().itemID == BuildToWin.pencil.itemID) {
                    BuildToWin.pencil.onBlockRightClicked(event.entityPlayer.inventory.getCurrentItem(), event.x, event.y, event.z, facedX, facedY, facedZ, event.entityPlayer);
                } else {
                    BuildToWin.rubber.onBlockRightClicked(event.entityPlayer.inventory.getCurrentItem(), event.x, event.y, event.z, facedX, facedY, facedZ, event.entityPlayer);
                }
            } else if (blockId == BuildToWin.blueprint.blockID && event.entityPlayer.inventory.getCurrentItem() != null) {
                if (event.entityPlayer.worldObj.isRemote) {
                    PacketDispatcher.sendPacketToServer(new Packet15Place(event.x, event.y, event.z, event.face, null, 0, 0, 0));
                } else {
                    BuildToWin.blueprint.onBlockRightClicked(facedX, facedY, facedZ, event.entityPlayer);
                }
                
                event.setCanceled(true);
            }
        }
    }
    
    @ForgeSubscribe
    public void onItemPickup(EntityXPOrbPickupEvent event) {
        TileEntityTeamHub teamHub = TileEntityTeamHub.getTeamHub(event.entityPlayer);
        teamHub.setEnergy(teamHub.getEnergy() + event.xpValue);
    }
}

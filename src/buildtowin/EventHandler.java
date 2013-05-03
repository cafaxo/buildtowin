package buildtowin;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import buildtowin.blueprint.BlueprintList;
import buildtowin.tileentity.TileEntityProtector;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import buildtowin.util.PriceList;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class EventHandler implements IConnectionHandler {
    
    @ForgeSubscribe
    public void onWorldUnload(WorldEvent.Unload event) {
        PlayerList.playerToPlayerListMapClient.clear();
        PlayerList.playerToPlayerListMapServer.clear();
    }
    
    @ForgeSubscribe
    public void onPlayerInteraction(PlayerInteractEvent event) {
        if (!event.entityPlayer.capabilities.isCreativeMode) {
            TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider(event.entityPlayer, TileEntityTeamHub.class);
            
            if (teamHub != null && teamHub.getGameHub() != null) {
                for (TileEntity connectedTeamHub : teamHub.getGameHub().getConnectedTeamHubs()) {
                    if (teamHub != connectedTeamHub) {
                        for (TileEntity teamHubExtension : ((TileEntityTeamHub) connectedTeamHub).getExtensionList()) {
                            if (teamHubExtension instanceof TileEntityProtector) {
                                if (((TileEntityProtector) teamHubExtension).isBlockProtected(event.x, event.y, event.z)) {
                                    if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                                        BuildToWin.printChatMessage(event.entityPlayer, "This Block is protected.");
                                    } else {
                                        BuildToWin.sendChatMessage(event.entityPlayer, "This Block is protected.");
                                    }
                                    
                                    event.setCanceled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
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
            
            if (blockId == BuildToWin.blueprint.blockID && event.entityPlayer.inventory.getCurrentItem() != null) {
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
    public void onItemPickup(PlaySoundAtEntityEvent event) {
        if (event.entity instanceof EntityXPOrb && !event.entity.worldObj.isRemote) {
            EntityPlayer entityPlayer = event.entity.worldObj.getClosestVulnerablePlayerToEntity(event.entity, 6.0D);
            
            if (entityPlayer != null) {
                TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityTeamHub.class);
                
                if (teamHub != null) {
                    teamHub.setCoins(teamHub.getCoins() + 10);
                }
            }
        }
    }
    
    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
        PacketDispatcher.sendPacketToPlayer(PriceList.serverInstance.getDescriptionPacket(), player);
        PacketDispatcher.sendPacketToPlayer(BlueprintList.serverInstance.getDescriptionPacket(), player);
    }
    
    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
        return null;
    }
    
    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
    }
    
    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
    }
    
    @Override
    public void connectionClosed(INetworkManager manager) {
    }
    
    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
    }
}

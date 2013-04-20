package buildtowin;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import buildtowin.blueprint.BlueprintList;
import buildtowin.tileentity.TileEntityGameHub;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ServerTickHandler implements ITickHandler {
    
    private short timer = 0;
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.PLAYER))) {
            EntityPlayer entityPlayer = (EntityPlayer) tickData[0];
            
            if (entityPlayer.isPlayerSleeping()) {
                TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityTeamHub.class);
                
                if (teamHub != null) {
                    TileEntityGameHub gameHub = teamHub.getGameHub();
                    
                    if (gameHub != null) {
                        gameHub.setSleptTime(gameHub.getSleptTime() + 100);
                    }
                }
            }
        } else {
            if (this.timer == 30) {
                PacketDispatcher.sendPacketToAllPlayers(BlueprintList.serverInstance.getDescriptionPacket());
                this.timer = 0;
            }
            
            ++this.timer;
        }
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER, TickType.PLAYER);
    }
    
    @Override
    public String getLabel() {
        return "servertickhandler";
    }
}

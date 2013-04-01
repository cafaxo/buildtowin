package buildtowin;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import buildtowin.blueprint.BlueprintList;
import buildtowin.tileentity.TileEntityGameHub;
import buildtowin.tileentity.TileEntityTeamHub;
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
                TileEntityGameHub gameHub = TileEntityTeamHub.getTeamHub(entityPlayer).getGameHub();
                
                if (gameHub != null) {
                    gameHub.setSleptTime(gameHub.getSleptTime() + 100);
                }
            }
        } else {
            if (this.timer == 20) {
                PacketDispatcher.sendPacketToAllPlayers(BlueprintList.blueprintListServer.getUpdatePacket());
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

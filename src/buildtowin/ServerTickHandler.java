package buildtowin;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import buildtowin.tileentity.TileEntityGameHub;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
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
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER);
    }
    
    @Override
    public String getLabel() {
        return "servertickhandler";
    }
}

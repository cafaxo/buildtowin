package buildtowin;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ServerTickHandler implements ITickHandler {
    
    private short timer = 0;
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.PLAYER))) {
            EntityPlayer player = (EntityPlayer) tickData[0];
            
            if (player.isPlayerSleeping()) {
                TileEntityBuildingController buildingController = BuildToWin.buildingControllerListServer.getBuildingController(player);
                
                if (buildingController != null) {
                    buildingController.setSleptTime(buildingController.getSleptTime() + 1000);
                }
            }
        } else {
            if (this.timer == 20) {
                BuildToWin.buildingControllerListServer.updateServer();
                PacketDispatcher.sendPacketToAllPlayers(BuildToWin.blueprintListServer.getDescriptionPacket());
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

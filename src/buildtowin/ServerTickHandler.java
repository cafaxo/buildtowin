package buildtowin;

import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ServerTickHandler implements ITickHandler {
    private short timer = 0;
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (timer == 20) {
            BuildToWin.buildingControllerListServer.updateServer();
            PacketDispatcher.sendPacketToAllPlayers(BuildToWin.serverBlueprintList.getDescriptionPacket());
            timer = 0;
        }
        
        ++timer;
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }
    
    @Override
    public String getLabel() {
        return "servertickhandler";
    }
}

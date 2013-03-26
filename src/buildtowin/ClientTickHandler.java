package buildtowin;

import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
    private short timer = 0;
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (timer == 20) {
            BuildToWin.buildingControllerListClient.updateClient();
            timer = 0;
        }
        
        ++timer;
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }
    
    @Override
    public String getLabel() {
        return "clienttickhandler";
    }
}

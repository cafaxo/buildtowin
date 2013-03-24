package buildtowin;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
    
    private GuiBuildingInfo buildingInfo;
    
    public ClientTickHandler() {
        this.buildingInfo = new GuiBuildingInfo(Minecraft.getMinecraft());
    }
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && !Minecraft.getMinecraft().isGamePaused) {
            this.buildingInfo.tick();
        }
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.RENDER);
    }
    
    @Override
    public String getLabel() {
        return "tickhandler";
    }
}

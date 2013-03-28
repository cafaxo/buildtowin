package buildtowin;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
    
    private short timer = 0;
    
    private GuiBuildingInfo buildingInfo;
    
    public ClientTickHandler() {
        this.buildingInfo = new GuiBuildingInfo(Minecraft.getMinecraft());
    }
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.PLAYER))) {
            EntityPlayer player = (EntityPlayer) tickData[0];
            
            if (player.getSleepTimer() > 99) {
                TileEntityBuildingController buildingController = BuildToWin.buildingControllerListClient.getBuildingController(player);
                
                if (buildingController != null) {
                    buildingController.setSleptTime(buildingController.getSleptTime() + 1000);
                }
            }
        } else if (type.equals(EnumSet.of(TickType.CLIENT))) {
            if (this.timer == 20) {
                BuildToWin.buildingControllerListClient.updateClient();
                this.timer = 0;
            }
            
            ++this.timer;
        }
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.RENDER))) {
            if (!Minecraft.getMinecraft().isGamePaused) {
                this.buildingInfo.tick();
            }
        }
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT, TickType.RENDER, TickType.PLAYER);
    }
    
    @Override
    public String getLabel() {
        return "clienttickhandler";
    }
}

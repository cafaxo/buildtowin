package buildtowin;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && !Minecraft.getMinecraft().isGamePaused) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            
            if (player != null) {
                TileEntityBuildingController buildingController = BuildToWin.getBuildingController().getTileEntity(player);
                if (buildingController != null) {
                    if (buildingController.getDeadline() != 0) {
                        int percent = 100;
                        
                        if (buildingController.getBlockDataList().size() != 0) {
                            percent = 100 * buildingController.getFinishedBlocks() / buildingController.getBlockDataList().size();
                        }
                        
                        int daysleft = (int) ((buildingController.getDeadline() - player.worldObj.getTotalWorldTime()) / 24000);
                        
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Days left: " + daysleft, 10, 10, 0xffffff);
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Progress: " + percent, 10, 25, 0xffffff);
                    }
                }
            }
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

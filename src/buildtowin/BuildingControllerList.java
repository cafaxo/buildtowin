package buildtowin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BuildingControllerList {
    private ArrayList<TileEntityBuildingController> buildingControllerList = new ArrayList<TileEntityBuildingController>();
    
    private Map<Integer, TileEntityBuildingController> playerToBuildingController = new HashMap<Integer, TileEntityBuildingController>();
    
    public TileEntityBuildingController getBuildingController(EntityPlayer player) {
        return playerToBuildingController.get(player.entityId);
    }
    
    @SideOnly(Side.CLIENT)
    public void updateClient() {
        this.buildingControllerList.clear();
        this.playerToBuildingController.clear();
        
        if (Minecraft.getMinecraft().theWorld != null) {
            for (Object tileEntity : Minecraft.getMinecraft().theWorld.loadedTileEntityList) {
                if (tileEntity instanceof TileEntityBuildingController) {
                    TileEntityBuildingController buildingController = (TileEntityBuildingController) tileEntity;
                    
                    this.buildingControllerList.add(buildingController);
                    
                    for (EntityPlayer player : buildingController.getConnectedAndOnlinePlayers()) {
                        playerToBuildingController.put(player.entityId, buildingController);
                    }
                }
            }
        }
    }
    
    public void updateServer() {
        this.buildingControllerList.clear();
        this.playerToBuildingController.clear();
        
        for (Object tileEntity : MinecraftServer.getServer().worldServers[0].loadedTileEntityList) {
            if (tileEntity instanceof TileEntityBuildingController) {
                TileEntityBuildingController buildingController = (TileEntityBuildingController) tileEntity;
                
                this.buildingControllerList.add(buildingController);
                buildingController.refreshConnectedAndOnlinePlayers();
                
                for (EntityPlayer player : buildingController.getConnectedAndOnlinePlayers()) {
                    playerToBuildingController.put(player.entityId, buildingController);
                }
            }
        }
    }
}

package buildtowin;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import buildtowin.client.gui.GuiBuildingInfo;
import buildtowin.tileentity.TileEntityGameHub;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickHandler implements ITickHandler {
    
    private GuiBuildingInfo buildingInfo;
    
    public ClientTickHandler() {
        this.buildingInfo = new GuiBuildingInfo(Minecraft.getMinecraft());
    }
    
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
        }
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.RENDER)) && !Minecraft.getMinecraft().isGamePaused) {
            this.buildingInfo.tick();
        }
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.RENDER, TickType.PLAYER);
    }
    
    @Override
    public String getLabel() {
        return "clienttickhandler";
    }
}

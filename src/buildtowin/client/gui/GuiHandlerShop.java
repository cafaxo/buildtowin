package buildtowin.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.ContainerShop;
import buildtowin.tileentity.TileEntityShop;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandlerShop implements IGuiHandler {
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        
        if (tileEntity instanceof TileEntityShop) {
            TileEntityShop shop = (TileEntityShop) tileEntity;
            shop.setTeamHub((TileEntityTeamHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityTeamHub.class));
            
            return new ContainerShop(entityPlayer.inventory, shop);
        }
        
        return null;
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        
        if (tileEntity instanceof TileEntityShop) {
            TileEntityShop shop = (TileEntityShop) tileEntity;
            shop.setTeamHub((TileEntityTeamHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityTeamHub.class));
            
            return new GuiContainerShop(entityPlayer.inventory, shop);
        }
        
        return null;
    }
}

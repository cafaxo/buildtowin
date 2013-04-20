package buildtowin.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.ContainerShop;
import buildtowin.tileentity.TileEntityShop;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandlerShop implements IGuiHandler {
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        
        if (tileEntity instanceof TileEntityShop) {
            return new ContainerShop(player.inventory, (TileEntityShop) tileEntity);
        }
        
        return null;
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        
        if (tileEntity instanceof TileEntityShop) {
            return new GuiContainerShop(player.inventory, (TileEntityShop) tileEntity);
        }
        
        return null;
    }
}

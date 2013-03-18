package buildtowin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemBlueprinter extends Item {
    
    public ItemBlueprinter(int id) {
        super(id);
        
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("Blueprinter");
    }
    
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        int blockId = par3World.getBlockId(par4, par5, par6);
        
        if (blockId != 243) {
            par3World.setBlockAndMetadataWithNotify(par4, par5, par6, 243, 0, 3);
            
            TileEntityBlockData te = (TileEntityBlockData) par3World.getBlockTileEntity(par4, par5, par6);
            
            if (te != null) {
                te.setBlockId(blockId);
            }
            
            return true;
        }
        
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public void func_94581_a(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.func_94245_a("pickaxeIron");
    }
}

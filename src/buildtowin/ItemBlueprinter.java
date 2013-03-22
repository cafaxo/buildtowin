package buildtowin;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemBlueprinter extends Item {
    public ItemBlueprinter(int id) {
        super(id);
        
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("Blueprinter");
    }
    
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        int blockId = par3World.getBlockId(par4, par5, par6);
        
        if (blockId == BuildToWin.getBlueprint().blockID) {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingController().getTileEntity(par3World, par2EntityPlayer);
            BlockData blockData = buildingController.getBlockData(par4, par5, par6);
            par3World.setBlock(par4, par5, par6, blockData.id);
            buildingController.removeBlock(blockData);
        } else if (blockId != BuildToWin.getBuildingController().blockID) {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingController().getTileEntity(par3World, par2EntityPlayer);
            
            if (buildingController != null) {
                buildingController.addBlock(new BlockData(par4, par5, par6, blockId));
                par3World.setBlock(par4, par5, par6, BuildToWin.getBlueprint().blockID);
                TileEntityBlueprint te = (TileEntityBlueprint) par3World.getBlockTileEntity(par4, par5, par6);
                
                if (te != null) {
                    te.setBlockId(blockId);
                } else {
                    throw new RuntimeException();
                }
                return true;
            } else {
                if (par3World.isRemote) {
                    Minecraft mc = FMLClientHandler.instance().getClient();
                    mc.ingameGUI.getChatGUI().printChatMessage(
                            "<BuildToWin> Please connect with a Building Controller, " + par2EntityPlayer.username);
                }
            }
        }
        
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.registerIcon("pickaxeIron");
    }
}

package buildtowin;

import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBuildingController extends BlockContainer {
    protected BlockBuildingController(int id) {
        super(id, Material.rock);
        setCreativeTab(CreativeTabs.tabMisc);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setUnlocalizedName("Building Controller");
    }
    
    @Override
    public void onBlockAdded(World par1World, int x, int y, int z) {
        super.onBlockAdded(par1World, x, y, z);
        
        par1World.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(par1World));
    }
    
    @Override
    public void updateTick(World par1World, int x, int y, int z, Random par5Random) {
        TileEntityBuildingController buildingController = (TileEntityBuildingController) par1World.getBlockTileEntity(x, y, z);
        
        buildingController.updateBlocks(par1World);
        PacketDispatcher.sendPacketToAllPlayers(buildingController.getDescriptionPacket());
        par1World.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(par1World));
    }
    
    @Override
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
        if (par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityBuildingController) {
            par5EntityPlayer.getEntityData().setIntArray("buildingcontroller", new int[] { par2, par3, par4 });
            
            Minecraft mc = FMLClientHandler.instance().getClient();
            
            if (par1World.isRemote) {
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Connected " + par5EntityPlayer.getEntityName() + " to the Building Controller.");
            }
        }
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par1World.isRemote) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            TileEntityBuildingController te = (TileEntityBuildingController) par1World.getBlockTileEntity(par2, par3, par4);
            
            if (par5EntityPlayer.capabilities.isCreativeMode) {
                mc.displayGuiScreen(new GuiBuildingSettings(te));
            } else {
                int percent = 100;
                
                if (te.getBlockDataList().size() != 0) {
                    percent = 100 * te.getFinishedBlocks() / te.getBlockDataList().size();
                }
                
                int daysleft = (int) (te.getDeadline() - (par1World.getTotalWorldTime() / 200));
                mc.displayGuiScreen(new GuiBuildingInfo(daysleft, percent));
            }
        }
        
        return false;
    }
    
    public TileEntityBuildingController getTileEntity(World world, NBTTagCompound stackTagCompound) {
        if (stackTagCompound == null) {
            return null;
        }
        
        int associatedBuildingControllerCoords[] = stackTagCompound.getIntArray("buildingcontroller");
        
        if (associatedBuildingControllerCoords != null && associatedBuildingControllerCoords.length == 3) {
            TileEntityBuildingController buildingController = (TileEntityBuildingController) world.getBlockTileEntity(
                    associatedBuildingControllerCoords[0],
                    associatedBuildingControllerCoords[1],
                    associatedBuildingControllerCoords[2]);
            
            return buildingController;
        }
        
        return null;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBuildingController();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("obsidian");
    }
}

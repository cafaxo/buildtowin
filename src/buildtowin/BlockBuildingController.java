package buildtowin;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBuildingController extends BlockContainer {
    private Icon iconDisconnected;
    
    protected BlockBuildingController(int id) {
        super(id, Material.rock);
        
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("Building Controller");
    }
    
    @Override
    public void onBlockAdded(World par1World, int x, int y, int z) {
        super.onBlockAdded(par1World, x, y, z);
        
        par1World.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(par1World));
    }
    
    @Override
    public void updateTick(World par1World, int x, int y, int z, Random par5Random) {
        TileEntityBuildingController buildingController = (TileEntityBuildingController) par1World.getBlockTileEntity(x, y, z);
        
        buildingController.updateBlocks();
        buildingController.refreshConnectedAndOnlinePlayers();
        
        if (buildingController.getDeadline() != 0) {
            if (buildingController.getFinishedBlocks() == buildingController.getBlockDataList().size()) {
                buildingController.setDeadline(0);
                
                if (buildingController.getConnectedAndOnlinePlayers().size() > 1) {
                    buildingController.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team has won."));
                } else {
                    buildingController.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have won."));
                }
            } else if (buildingController.getDeadline() <= par1World.getTotalWorldTime()) {
                buildingController.setDeadline(0);
                
                if (buildingController.getConnectedAndOnlinePlayers().size() > 1) {
                    buildingController.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team has lost."));
                } else {
                    buildingController.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have lost."));
                }
            }
        }
        
        PacketDispatcher.sendPacketToAllPlayers(buildingController.getDescriptionPacketOptimized());
        
        par1World.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(par1World));
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par1World.isRemote) {
            if (par5EntityPlayer.getCurrentEquippedItem() != null) {
                if (par5EntityPlayer.getCurrentEquippedItem().itemID == BuildToWin.getBlueprinter().itemID) {
                    return false;
                }
            }
            
            Minecraft mc = FMLClientHandler.instance().getClient();
            TileEntityBuildingController buildingController = (TileEntityBuildingController) par1World.getBlockTileEntity(par2, par3, par4);
            
            mc.displayGuiScreen(new GuiScreenBuildingSettings(buildingController, par5EntityPlayer.capabilities.isCreativeMode));
        }
        
        return false;
    }
    
    @Override
    public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
        TileEntityBuildingController te = (TileEntityBuildingController) par1World.getBlockTileEntity(par2, par3, par4);
        te.removeAllBlocks();
    }
    
    public TileEntityBuildingController getTileEntity(EntityPlayer entityPlayer) {
        for (int i = 0; i < entityPlayer.worldObj.loadedTileEntityList.size(); ++i) {
            TileEntity te = (TileEntity) entityPlayer.worldObj.loadedTileEntityList.get(i);
            
            if (te instanceof TileEntityBuildingController) {
                TileEntityBuildingController buildingController = (TileEntityBuildingController) te;
                
                if (buildingController.isPlayerConnectedAndOnline(entityPlayer)) {
                    return buildingController;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        TileEntityBuildingController buildingController = (TileEntityBuildingController) par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
        
        if (buildingController.getConnectedAndOnlinePlayers().contains(Minecraft.getMinecraft().thePlayer)) {
            return this.blockIcon;
        } else {
            return this.iconDisconnected;
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBuildingController();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:buildingcontroller_connected");
        this.iconDisconnected = par1IconRegister.registerIcon("buildtowin:buildingcontroller_disconnected");
    }
}

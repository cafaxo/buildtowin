package buildtowin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBuildingController extends BlockContainer {
    protected BlockBuildingController(int id) {
        super(id, Material.rock);
        
        this.setCreativeTab(CreativeTabs.tabMisc);
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
        
        buildingController.updateBlocks(par1World);
        PacketDispatcher.sendPacketToAllPlayers(buildingController.getDescriptionPacket());
        
        if (buildingController.getDeadline() != 0) {
            if (buildingController.getFinishedBlocks() == buildingController.getBlockDataList().size()) {
                buildingController.setDeadline(0);
                
                if (buildingController.getConnectedPlayers().tagCount() != 0) {
                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                    DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
                    
                    try {
                        dataoutputstream.writeInt(buildingController.xCoord);
                        dataoutputstream.writeInt(buildingController.yCoord);
                        dataoutputstream.writeInt(buildingController.zCoord);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    
                    Packet250CustomPayload winPacket = new Packet250CustomPayload("btwwin", bytearrayoutputstream.toByteArray());
                    
                    for (int i = 0; i < buildingController.getConnectedPlayers().tagCount(); ++i) {
                        NBTTagString playerName = (NBTTagString) buildingController.getConnectedPlayers().tagAt(i);
                        EntityPlayer player = par1World.getPlayerEntityByName(playerName.data);
                        
                        if (player != null && !player.capabilities.isCreativeMode) {
                            PacketDispatcher.sendPacketToPlayer(winPacket, (Player) player);
                        }
                    }
                }
            } else if (buildingController.getDeadline() <= par1World.getTotalWorldTime()) {
                buildingController.setDeadline(0);
                
                if (buildingController.getConnectedPlayers().tagCount() != 0) {
                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                    DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
                    
                    try {
                        dataoutputstream.writeInt(buildingController.xCoord);
                        dataoutputstream.writeInt(buildingController.yCoord);
                        dataoutputstream.writeInt(buildingController.zCoord);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    
                    Packet250CustomPayload losePacket = new Packet250CustomPayload("btwlose", bytearrayoutputstream.toByteArray());
                    
                    for (int i = 0; i < buildingController.getConnectedPlayers().tagCount(); ++i) {
                        NBTTagString playerName = (NBTTagString) buildingController.getConnectedPlayers().tagAt(i);
                        EntityPlayer player = par1World.getPlayerEntityByName(playerName.data);
                        
                        if (player != null && !player.capabilities.isCreativeMode) {
                            PacketDispatcher.sendPacketToPlayer(losePacket, (Player) player);
                        }
                    }
                }
            }
        }
        
        par1World.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(par1World));
    }
    
    @Override
    public void onBlockClicked(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer) {
        TileEntityBuildingController buildingController = (TileEntityBuildingController) par1World.getBlockTileEntity(x, y, z);

        buildingController.connectPlayer(par5EntityPlayer);
        
        Minecraft mc = FMLClientHandler.instance().getClient();
        
        if (par1World.isRemote) {
            mc.ingameGUI.getChatGUI().printChatMessage(
                    "<BuildToWin> Connected " + par5EntityPlayer.getEntityName() + " to the Building Controller.");
        }
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par1World.isRemote) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            TileEntityBuildingController buildingController = (TileEntityBuildingController) par1World.getBlockTileEntity(par2, par3, par4);
            
            if (par5EntityPlayer.capabilities.isCreativeMode) {
                mc.displayGuiScreen(new GuiBuildingSettings(buildingController));
            } else if (buildingController.getDeadline() == 0) {
                mc.displayGuiScreen(new GuiBuildingStart(buildingController));
            } else {
                int percent = 100;
                
                if (buildingController.getBlockDataList().size() != 0) {
                    percent = 100 * buildingController.getFinishedBlocks() / buildingController.getBlockDataList().size();
                }
                
                int daysleft = (int) ((buildingController.getDeadline() - par1World.getTotalWorldTime()) / 24000);
                mc.displayGuiScreen(new GuiBuildingInfo(daysleft, percent));
            }
        }
        
        return false;
    }
    
    public TileEntityBuildingController getTileEntity(World world, EntityPlayer entityPlayer) {     
        // check if the player has cached the connection coordinates
        int coords[] = entityPlayer.getEntityData().getIntArray("buildingcontroller");
        
        if (coords != null && coords.length == 3) {
            TileEntityBuildingController buildingController = (TileEntityBuildingController) world.getBlockTileEntity(
                    coords[0], coords[1], coords[2]);
            
            if (buildingController != null) {
                return buildingController;
            }
        }
        
        // player has not cached his connection coordinates; bruteforce and cache them
        for (int i = 0; i < world.loadedTileEntityList.size(); ++i) {
            TileEntity te = (TileEntity) world.loadedTileEntityList.get(i);
            
            if (te instanceof TileEntityBuildingController) {
                TileEntityBuildingController buildingController = (TileEntityBuildingController) te;
            
                if (buildingController.isPlayerConnected(entityPlayer)) {
                
                    entityPlayer.getEntityData().setIntArray("buildingcontroller", new int[] {
                            buildingController.xCoord, buildingController.yCoord, buildingController.zCoord});
                    
                    return buildingController;
                }
            }
        }
        
        // player is not connected, or chunk with his building controller is not loaded
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

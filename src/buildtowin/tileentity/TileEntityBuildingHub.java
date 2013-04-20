package buildtowin.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import buildtowin.BuildToWin;
import buildtowin.blueprint.Blueprint;
import buildtowin.blueprint.IBlueprintProvider;
import buildtowin.util.IPlayerListProvider;
import buildtowin.util.PlayerList;

public class TileEntityBuildingHub extends TileEntitySynchronized implements IPlayerListProvider, IBlueprintProvider {
    
    private PlayerList playerList;
    
    private Blueprint blueprint;
    
    public TileEntityBuildingHub() {
        this.playerList = new PlayerList(this);
        this.blueprint = new Blueprint();
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setTag("players", this.playerList.getTagList());
        
        par1NBTTagCompound.setIntArray("blueprint", this.blueprint.encode());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        NBTTagList connectedPlayersNbt = (NBTTagList) par1NBTTagCompound.getTag("players");
        this.playerList.readTagList(connectedPlayersNbt);
        
        this.blueprint.decode(par1NBTTagCompound.getIntArray("blueprint"));
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        this.playerList.writeDescriptionPacket(dataOutputStream);
        return true;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.playerList.readDescriptionPacket(dataInputStream);
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            this.blueprint.refresh();
        }
        
        super.updateEntity();
    }
    
    public void connectPlayer(EntityPlayer entityPlayer) {
        this.playerList.connectPlayer(entityPlayer);
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Connected to Building Hub.");
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        this.playerList.disconnectPlayer(entityPlayer);
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Disconnected from Building Hub.");
    }
    
    public static TileEntityBuildingHub getBuildingHub(EntityPlayer entityPlayer) {
        TileEntity tileEntity = PlayerList.getTileEntity(entityPlayer);
        
        if (tileEntity instanceof TileEntityBuildingHub) {
            return (TileEntityBuildingHub) tileEntity;
        }
        
        return null;
    }
    
    @Override
    public PlayerList getPlayerList() {
        return this.playerList;
    }
    
    public Blueprint getBlueprint() {
        if (!this.worldObj.isRemote) {
            return this.blueprint;
        }
        
        return null;
    }
    
    @Override
    public void loadBlueprint(Blueprint blueprint) {
        this.getBlueprint().loadBlueprint(blueprint.getBlocks());
    }
    
    public void validate() {
        super.validate();
        
        this.blueprint.setWorldObj(this.worldObj);
        this.blueprint.setOffset(this.xCoord, this.yCoord, this.zCoord);
    }
}

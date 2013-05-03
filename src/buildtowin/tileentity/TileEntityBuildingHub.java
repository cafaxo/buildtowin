package buildtowin.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import buildtowin.BuildToWin;
import buildtowin.ChunkLoadingManager;
import buildtowin.blueprint.Blueprint;
import buildtowin.util.Color;
import buildtowin.util.IPlayerListProvider;
import buildtowin.util.PlayerList;

public class TileEntityBuildingHub extends TileEntitySynchronized implements IPlayerListProvider, IBlueprintProvider, ITicketProvider {
    
    private Color color = Color.fromId(1);
    
    private PlayerList playerList = new PlayerList(this);
    
    private Blueprint blueprint = new Blueprint(this);
    
    private Ticket ticket;
    
    @Override
    public void initialize() {
        ChunkLoadingManager.forceChunk(this);
    }
    
    @Override
    public void invalidate() {
        ChunkLoadingManager.unforceChunk(this);
        super.invalidate();
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
            this.blueprint.refresh(false);
        }
        
        super.updateEntity();
    }
    
    @Override
    public boolean isValid() {
        return !this.isInvalid();
    }
    
    @Override
    public void onPlayerConnected(EntityPlayer entityPlayer) {
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Connected to Building Hub.");
    }
    
    @Override
    public void onPlayerDisconnect(EntityPlayer entityPlayer) {
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Disconnected from Building Hub.");
    }
    
    @Override
    public PlayerList getPlayerList() {
        return this.playerList;
    }
    
    public Blueprint getBlueprint() {
        return this.blueprint;
    }
    
    @Override
    public void loadBlueprint(Blueprint blueprint) {
        this.blueprint.clear();
        
        this.blueprint = new Blueprint(this, blueprint);
        this.blueprint.reset();
    }
    
    @Override
    public Color getColor() {
        return this.color;
    }
    
    @Override
    public Ticket getTicket() {
        return this.ticket;
    }
    
    @Override
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}

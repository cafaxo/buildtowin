package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import buildtowin.BuildToWin;
import buildtowin.blueprint.Blueprint;
import buildtowin.blueprint.IBlueprintProvider;
import buildtowin.network.PacketIds;
import buildtowin.util.Color;
import buildtowin.util.Coordinates;
import buildtowin.util.IPlayerListProvider;
import buildtowin.util.ItemStackList;
import buildtowin.util.PlayerList;
import buildtowin.util.TileEntityList;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityTeamHub extends TileEntityConnectionHub implements IPlayerListProvider, IBlueprintProvider {
    
    private TileEntityGameHub gameHub;
    
    private Color color = new Color();
    
    private TileEntityList extensionList = new TileEntityList();
    
    private ItemStackList teamChestContents = new ItemStackList(27);
    
    private PlayerList playerList = new PlayerList(this);
    
    private Blueprint blueprint = new Blueprint(this);
    
    private Coordinates nextUnfinishedBlueprint = new Coordinates(0, 0, 0);
    
    private int coins;
    
    private int finishedBlockCount;
    
    private int totalBlockCount;
    
    public TileEntityTeamHub() {
        super(new Class[] { TileEntityGameHub.class });
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setIntArray("blueprint", this.blueprint.encode());
        
        par1NBTTagCompound.setTag("players", this.playerList.getTagList());
        
        par1NBTTagCompound.setInteger("coins", this.coins);
        
        par1NBTTagCompound.setTag("teamChestContents", this.teamChestContents.getTagList());
        
        par1NBTTagCompound.setIntArray("extensions", this.extensionList.encode());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.blueprint.decode(par1NBTTagCompound.getIntArray("blueprint"));
        
        NBTTagList connectedPlayersNbt = (NBTTagList) par1NBTTagCompound.getTag("players");
        this.playerList.readTagList(connectedPlayersNbt);
        
        this.coins = par1NBTTagCompound.getInteger("coins");
        
        this.teamChestContents.readTagList(par1NBTTagCompound.getTagList("teamChestContents"));
        
        this.extensionList.decode(par1NBTTagCompound.getIntArray("extensions"));
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.finishedBlockCount);
        dataOutputStream.writeInt(this.totalBlockCount);
        dataOutputStream.writeInt(this.coins);
        
        if (this.gameHub != null) {
            dataOutputStream.writeInt(this.color.id);
            dataOutputStream.writeInt(this.gameHub.xCoord);
            dataOutputStream.writeInt(this.gameHub.yCoord);
            dataOutputStream.writeInt(this.gameHub.zCoord);
        } else {
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
        }
        
        Coordinates nextUnfinishedBlueprint = this.blueprint.getNextUnfinishedBlueprint();
        
        if (nextUnfinishedBlueprint != null) {
            dataOutputStream.writeInt(nextUnfinishedBlueprint.x);
            dataOutputStream.writeInt(nextUnfinishedBlueprint.y);
            dataOutputStream.writeInt(nextUnfinishedBlueprint.z);
        } else {
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
        }
        
        this.playerList.writeDescriptionPacket(dataOutputStream);
        this.extensionList.writeDescriptionPacket(dataOutputStream);
        
        return true;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.finishedBlockCount = dataInputStream.readInt();
        this.totalBlockCount = dataInputStream.readInt();
        this.coins = dataInputStream.readInt();
        
        Color newColor = Color.fromId(dataInputStream.readInt());
        
        if (newColor.id != this.color.id) {
            this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
            
            for (TileEntity teamHubExtension : this.getExtensionList()) {
                this.worldObj.markBlockForRenderUpdate(teamHubExtension.xCoord, teamHubExtension.yCoord, teamHubExtension.zCoord);
            }
            
            this.color = newColor;
        }
        
        TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                dataInputStream.readInt(),
                dataInputStream.readInt(),
                dataInputStream.readInt());
        
        if (tileEntity instanceof TileEntityGameHub) {
            this.gameHub = (TileEntityGameHub) tileEntity;
        } else {
            this.gameHub = null;
        }
        
        this.nextUnfinishedBlueprint = new Coordinates(dataInputStream.readInt(), dataInputStream.readInt(), dataInputStream.readInt());
        
        this.playerList.readDescriptionPacket(dataInputStream);
        this.extensionList.readDescriptionPacket(dataInputStream);
    }
    
    @Override
    public boolean isValid() {
        return !this.isInvalid();
    }
    
    @Override
    public void onPlayerConnected(EntityPlayer entityPlayer) {
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Connected to Team Hub.");
    }
    
    @Override
    public void onPlayerDisconnect(EntityPlayer entityPlayer) {
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Disconnected from Team Hub.");
    }
    
    @Override
    protected void onSynchronization() {
        this.gameHub = null;
        this.updateConnections();
        
        if (this.gameHub == null) {
            this.blueprint.clear();
        }
        
        Iterator<TileEntity> iter = this.getExtensionList().iterator();
        
        while (iter.hasNext()) {
            TileEntity tileEntity = iter.next();
            
            if (tileEntity == null || tileEntity.isInvalid()) {
                iter.remove();
            } else {
                ((ITeamHubExtension) tileEntity).setTeamHub(this);
            }
        }
        
        this.finishedBlockCount = this.blueprint.refresh();
        this.totalBlockCount = this.blueprint.getBlocks().size();
        
        super.onSynchronization();
    }
    
    @Override
    public void onConnectionEstablished(TileEntity tileEntity) {
        this.gameHub = (TileEntityGameHub) tileEntity;
    }
    
    @Override
    public void loadBlueprint(Blueprint blueprint) {
        this.blueprint.clear();
        this.blueprint.loadBlockData(blueprint.getBlocks());
        this.blueprint.reset();
    }
    
    public float getProgress() {
        if (this.totalBlockCount != 0) {
            return (float) this.finishedBlockCount / (float) this.totalBlockCount;
        }
        
        return 0.00F;
    }
    
    public void sendWinMessage() {
        if (this.playerList.getConnectedPlayers().size() > 1) {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your Team Won!"));
        } else {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You Won!"));
        }
        
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.TEAMHUB_WIN);
            
            this.sendPacketToConnectedPlayers(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void sendLoseMessage(int ranking) {
        if (this.gameHub.getConnectedTeamHubs().size() > 2) {
            if (this.playerList.getConnectedPlayers().size() > 1) {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team reached the " + ranking + ". place."));
            } else {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You reached the " + ranking + ". place."));
            }
        } else {
            if (this.playerList.getConnectedPlayers().size() > 1) {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team lost!"));
            } else {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You lost!"));
            }
        }
        
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.TEAMHUB_LOSE);
            
            this.sendPacketToConnectedPlayers(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void sendPacketToConnectedPlayers(Packet packet) {
        for (String player : this.playerList.getConnectedPlayers()) {
            EntityPlayer entityPlayer = null;
            
            if ((entityPlayer = this.worldObj.getPlayerEntityByName(player)) != null) {
                PacketDispatcher.sendPacketToPlayer(packet, (Player) entityPlayer);
            }
        }
    }
    
    public int getFinishedBlockCount() {
        return this.finishedBlockCount;
    }
    
    public TileEntityGameHub getGameHub() {
        return this.gameHub;
    }
    
    public void setGameHub(TileEntityGameHub gameHub) {
        this.gameHub = gameHub;
    }
    
    public ArrayList<TileEntity> getExtensionList() {
        return this.extensionList.getTileEntityList(this.worldObj);
    }
    
    @Override
    public PlayerList getPlayerList() {
        return this.playerList;
    }
    
    public Blueprint getBlueprint() {
        return this.blueprint;
    }
    
    public int getCoins() {
        return this.coins;
    }
    
    public void setCoins(int coins) {
        this.coins = coins;
    }
    
    public ItemStackList getTeamChestContents() {
        return this.teamChestContents;
    }
    
    @Override
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Coordinates getNextUnfinishedBlueprint() {
        return this.nextUnfinishedBlueprint;
    }
}

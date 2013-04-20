package buildtowin.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;

public class PlayerList {
    
    public static HashMap<String, TileEntity> playerToTileEntityMapServer = new HashMap<String, TileEntity>();
    
    public static HashMap<String, TileEntity> playerToTileEntityMapClient = new HashMap<String, TileEntity>();
    
    private TileEntity tileEntity;
    
    private ArrayList<String> connectedPlayers;
    
    public PlayerList(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.connectedPlayers = new ArrayList<String>();
    }
    
    public boolean isPlayerConnected(EntityPlayer entityPlayer) {
        return this.getPlayerToTileEntityMap(entityPlayer).get(entityPlayer.username) == this.tileEntity;
    }
    
    public void connectPlayer(EntityPlayer entityPlayer) {
        if (!this.connectedPlayers.contains(entityPlayer.username)) {
            this.connectedPlayers.add(entityPlayer.username);
            this.getPlayerToTileEntityMap(entityPlayer).put(entityPlayer.username, this.tileEntity);
        }
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        if (this.connectedPlayers.contains(entityPlayer.username)) {
            this.connectedPlayers.remove(entityPlayer.username);
            this.getPlayerToTileEntityMap(entityPlayer).remove(entityPlayer.username);
        }
    }
    
    public NBTTagList getTagList() {
        NBTTagList connectedPlayersNbt = new NBTTagList();
        
        for (String player : this.connectedPlayers) {
            connectedPlayersNbt.appendTag(new NBTTagString("", player));
        }
        
        return connectedPlayersNbt;
    }
    
    public void readTagList(NBTTagList tagList) {
        for (int i = 0; i < tagList.tagCount(); ++i) {
            this.connectedPlayers.add(((NBTTagString) tagList.tagAt(i)).data);
            this.playerToTileEntityMapServer.put(((NBTTagString) tagList.tagAt(i)).data, this.tileEntity);
        }
    }
    
    public void writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.connectedPlayers.size());
        
        for (String player : this.connectedPlayers) {
            dataOutputStream.writeUTF(player);
        }
    }
    
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        int size = dataInputStream.readInt();
        
        for (int i = 0; i < size; ++i) {
            String player = dataInputStream.readUTF();
            this.playerToTileEntityMapClient.put(player, this.tileEntity);
            this.connectedPlayers.add(player);
        }
    }
    
    public static TileEntity getTileEntity(EntityPlayer entityPlayer) {
        TileEntity tileEntity = PlayerList.getPlayerToTileEntityMap(entityPlayer).get(entityPlayer.username);
        
        if (tileEntity != null) {
            if (tileEntity == entityPlayer.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)) {
                return tileEntity;
            } else {
                PlayerList.getPlayerToTileEntityMap(entityPlayer).remove(entityPlayer);
            }
        }
        
        return null;
    }
    
    public ArrayList<String> getConnectedPlayers() {
        return connectedPlayers;
    }
    
    public static HashMap<String, TileEntity> getPlayerToTileEntityMap(EntityPlayer entityPlayer) {
        if (entityPlayer.worldObj.isRemote) {
            return PlayerList.playerToTileEntityMapClient;
        } else {
            return PlayerList.playerToTileEntityMapServer;
        }
    }
}

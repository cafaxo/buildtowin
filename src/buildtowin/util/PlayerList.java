package buildtowin.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class PlayerList {
    
    public static HashMap<String, PlayerList> playerToPlayerListMapServer = new HashMap<String, PlayerList>();
    
    public static HashMap<String, PlayerList> playerToPlayerListMapClient = new HashMap<String, PlayerList>();
    
    private IPlayerListProvider playerListProvider;
    
    private ArrayList<String> connectedPlayers;
    
    public PlayerList(IPlayerListProvider playerListProvider) {
        this.playerListProvider = playerListProvider;
        this.connectedPlayers = new ArrayList<String>();
    }
    
    public boolean isPlayerConnected(EntityPlayer entityPlayer) {
        return this.connectedPlayers.contains(entityPlayer.username);
    }
    
    public void connectPlayer(EntityPlayer entityPlayer) {
        if (!this.connectedPlayers.contains(entityPlayer.username)) {
            PlayerList playerList = PlayerList.getPlayerToPlayerListMap(entityPlayer).get(entityPlayer.username);
            
            if (playerList != null) {
                playerList.disconnectPlayer(entityPlayer);
            }
            
            this.connectedPlayers.add(entityPlayer.username);
            PlayerList.getPlayerToPlayerListMap(entityPlayer).put(entityPlayer.username, this);
            this.playerListProvider.onPlayerConnected(entityPlayer);
        }
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        if (this.connectedPlayers.contains(entityPlayer.username)) {
            this.connectedPlayers.remove(entityPlayer.username);
            PlayerList.getPlayerToPlayerListMap(entityPlayer).remove(entityPlayer.username);
            this.playerListProvider.onPlayerDisconnect(entityPlayer);
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
            PlayerList.playerToPlayerListMapServer.put(((NBTTagString) tagList.tagAt(i)).data, this);
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
        
        for (String player : this.connectedPlayers) {
            PlayerList.playerToPlayerListMapClient.remove(player);
        }
        
        this.connectedPlayers.clear();
        
        for (int i = 0; i < size; ++i) {
            String player = dataInputStream.readUTF();
            PlayerList.playerToPlayerListMapClient.put(player, this);
            this.connectedPlayers.add(player);
        }
    }
    
    public static IPlayerListProvider getPlayerListProvider(EntityPlayer entityPlayer) {
        PlayerList playerList = PlayerList.getPlayerToPlayerListMap(entityPlayer).get(entityPlayer.username);
        
        if (playerList != null) {
            if (playerList.playerListProvider.isValid()) {
                return playerList.playerListProvider;
            } else {
                PlayerList.getPlayerToPlayerListMap(entityPlayer).remove(entityPlayer.username);
            }
        }
        
        return null;
    }
    
    public static IPlayerListProvider getPlayerListProvider(EntityPlayer entityPlayer, Class clazz) {
        PlayerList playerList = PlayerList.getPlayerToPlayerListMap(entityPlayer).get(entityPlayer.username);
        
        if (playerList != null) {
            if (!playerList.playerListProvider.getClass().isAssignableFrom(clazz)) {
                return null;
            }
            
            if (playerList.playerListProvider.isValid()) {
                return playerList.playerListProvider;
            } else {
                PlayerList.getPlayerToPlayerListMap(entityPlayer).remove(entityPlayer.username);
            }
        }
        
        return null;
    }
    
    public ArrayList<String> getConnectedPlayers() {
        return this.connectedPlayers;
    }
    
    public static HashMap<String, PlayerList> getPlayerToPlayerListMap(EntityPlayer entityPlayer) {
        if (entityPlayer.worldObj.isRemote) {
            return PlayerList.playerToPlayerListMapClient;
        } else {
            return PlayerList.playerToPlayerListMapServer;
        }
    }
}

package buildtowin.client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildtowin.network.PacketIds;
import buildtowin.tileentity.TileEntityTeamHub;

public class GameStats {
    
    public static GameStats instance = new GameStats();
    
    private ArrayList<TeamStats> teamStatsList = new ArrayList<TeamStats>();
    
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        int teamCount = dataInputStream.readInt();
        this.teamStatsList.clear();
        
        if (teamCount > 0) {
            for (int i = 0; i < teamCount; ++i) {
                TeamStats teamStats = new TeamStats();
                
                teamStats.progress = dataInputStream.readFloat();
                teamStats.daysLeft = dataInputStream.readFloat();
                teamStats.coins = dataInputStream.readInt();
                teamStats.colorId = dataInputStream.readInt();
                
                this.teamStatsList.add(teamStats);
            }
        }
    }
    
    public static Packet getDescriptionPacket(TileEntityTeamHub teamHub) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.GAMESTATS_UPDATE);
            
            if (teamHub == null) {
                dataoutputstream.writeInt(0);
            } else {
                dataoutputstream.writeInt(teamHub.getGameHub().getConnectedTeamHubs().size());
                
                dataoutputstream.writeFloat(teamHub.getProgress());
                dataoutputstream.writeFloat((teamHub.getGameHub().getDeadline() - teamHub.getGameHub().getRealWorldTime()) / 24000.F);
                dataoutputstream.writeInt(teamHub.getCoins());
                dataoutputstream.writeInt(teamHub.getColor().id);
                
                for (TileEntity tileEntity : teamHub.getGameHub().getConnectedTeamHubs()) {
                    if (tileEntity != teamHub) {
                        TileEntityTeamHub otherTeamHub = (TileEntityTeamHub) tileEntity;
                        
                        dataoutputstream.writeFloat(otherTeamHub.getProgress());
                        dataoutputstream.writeFloat((otherTeamHub.getGameHub().getDeadline() - otherTeamHub.getGameHub().getRealWorldTime()) / 24000.F);
                        dataoutputstream.writeInt(otherTeamHub.getCoins());
                        dataoutputstream.writeInt(otherTeamHub.getColor().id);
                    }
                }
            }
            
            return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public ArrayList<TeamStats> getTeamStatsList() {
        return this.teamStatsList;
    }
}

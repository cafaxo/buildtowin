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
import buildtowin.util.Coordinates;

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
                teamStats.nextUnfinishedBlueprint = new Coordinates(dataInputStream.readInt(), dataInputStream.readInt(), dataInputStream.readInt());
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
                
                GameStats.writeTeamHubDescription(dataoutputstream, teamHub);
                
                for (TileEntity tileEntity : teamHub.getGameHub().getConnectedTeamHubs()) {
                    if (tileEntity != teamHub) {
                        GameStats.writeTeamHubDescription(dataoutputstream, (TileEntityTeamHub) tileEntity);
                    }
                }
            }
            
            return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    private static void writeTeamHubDescription(DataOutputStream dataOutputStream, TileEntityTeamHub teamHub) throws IOException {
        dataOutputStream.writeFloat(teamHub.getProgress());
        dataOutputStream.writeFloat((teamHub.getGameHub().getDeadline() - teamHub.getGameHub().getRealWorldTime()) / 24000.F);
        dataOutputStream.writeInt(teamHub.getCoins());
        
        Coordinates nextUnfinishedBlueprint = teamHub.getBlueprint().getNextUnfinishedBlueprint();
        
        if (nextUnfinishedBlueprint != null) {
            dataOutputStream.writeInt(nextUnfinishedBlueprint.x);
            dataOutputStream.writeInt(nextUnfinishedBlueprint.y);
            dataOutputStream.writeInt(nextUnfinishedBlueprint.z);
        } else {
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
        }
        
        dataOutputStream.writeInt(teamHub.getColor().id);
    }
    
    public ArrayList<TeamStats> getTeamStatsList() {
        return this.teamStatsList;
    }
}

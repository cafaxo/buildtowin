package buildtowin;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import buildtowin.network.PlayerList;
import buildtowin.tileentity.TileEntityBlueprint;

public class EventHandler {
    
    @ForgeSubscribe
    public void onWorldUnload(WorldEvent.Unload event) {
        PlayerList.playerToTileEntityMapClient.clear();
        PlayerList.playerToTileEntityMapServer.clear();
        TileEntityBlueprint.instancesClient.clear();
    }
}

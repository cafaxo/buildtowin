package buildtowin;

import net.minecraft.client.Minecraft;
import buildtowin.blueprint.BlueprintList;
import buildtowin.client.renderer.BlueprintRenderer;
import buildtowin.client.renderer.ConnectionWireRenderer;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void init() {
        if (BlueprintList.blueprintListServer == null) {
            BlueprintList.blueprintListServer = new BlueprintList(Minecraft.getMinecraftDir().getAbsolutePath());
            BlueprintList.blueprintListServer.read();
        }
        
        BuildToWin.connectionWireRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new ConnectionWireRenderer());
        
        BuildToWin.blueprintRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlueprintRenderer());
    }
}

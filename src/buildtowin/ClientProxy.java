package buildtowin;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void init() {
        BuildToWin.blueprintListServer = new BlueprintList(Minecraft.getMinecraftDir().getAbsolutePath());
        BuildToWin.blueprintListServer.read();
        
        BuildToWin.blueprintListClient = new BlueprintList();
        
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        
        BuildToWin.blueprintRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlueprintRenderer());
    }
}

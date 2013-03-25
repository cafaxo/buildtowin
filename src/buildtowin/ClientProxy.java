package buildtowin;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        BuildToWin.blueprintRenderingId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlueprintRenderer());
    }
}

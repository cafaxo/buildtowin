package buildtowin;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        BuildToWin.serverBlueprintList = new BlueprintList(Minecraft.getMinecraftDir().getAbsolutePath());
        BuildToWin.serverBlueprintList.read();
        
        BuildToWin.clientBlueprintList = new BlueprintList();
        
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        
        BuildToWin.blueprintRenderingId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlueprintRenderer());
    }
}

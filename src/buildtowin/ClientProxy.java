package buildtowin;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.client.renderer.Tessellator;
import buildtowin.blueprint.BlueprintList;
import buildtowin.client.renderer.BlueprintRenderer;
import buildtowin.client.renderer.ColoredBlockRenderer;
import buildtowin.client.renderer.ConnectionWireRenderer;
import buildtowin.client.renderer.TeamChestItemRenderHelper;
import buildtowin.client.renderer.TessellatorColorReplacer;
import buildtowin.client.renderer.TileEntityTeamChestRenderer;
import buildtowin.tileentity.TileEntityTeamChest;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void init() {
        Tessellator.instance = new TessellatorColorReplacer();
        
        BlueprintList.serverInstance.init(new File(Minecraft.getMinecraftDir().getAbsolutePath(), "blueprints"));
        
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        
        BuildToWin.connectionWireRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new ConnectionWireRenderer());
        
        BuildToWin.blueprintRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlueprintRenderer());
        
        BuildToWin.coloredBlockRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(ColoredBlockRenderer.instance);
        
        ChestItemRenderHelper.instance = new TeamChestItemRenderHelper();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTeamChest.class, new TileEntityTeamChestRenderer());
    }
}

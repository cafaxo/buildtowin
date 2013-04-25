package buildtowin.client.renderer;

import net.minecraft.world.IBlockAccess;
import buildtowin.util.Color;

public interface IColoredBlock {
    
    public void switchIconToOverlay();
    
    public void switchIconToStandard();
    
    public Color getColor(IBlockAccess world, int x, int y, int z);
}

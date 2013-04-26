package buildtowin.client.renderer;

import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TessellatorColorReplacer extends Tessellator {
    
    boolean replaceColor;
    
    float red, green, blue, alpha;
    
    @Override
    public void setColorRGBA(int par1, int par2, int par3, int par4) {
        if (this.replaceColor) {
            super.setColorRGBA((int) (this.red * 255.0F), (int) (this.green * 255.0F), (int) (this.blue * 255.0F), (int) (this.alpha * 255.0F));
        } else {
            super.setColorRGBA(par1, par2, par3, par4);
        }
    }
}

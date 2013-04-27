package buildtowin.client.gui;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenAdvanced extends GuiScreen {
    
    protected void renderTooltip(List list, int x, int y) {
        if (!list.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = list.iterator();
            
            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                int l = this.fontRenderer.getStringWidth(s);
                
                if (l > k) {
                    k = l;
                }
            }
            
            int i1 = x + 12;
            int j1 = y - 12;
            int k1 = 8;
            
            if (list.size() > 1) {
                k1 += 2 + (list.size() - 1) * 10;
            }
            
            if (i1 + k > this.width) {
                i1 -= 28 + k;
            }
            
            if (j1 + k1 + 6 > this.height) {
                j1 = this.height - k1 - 6;
            }
            
            this.zLevel = 300.0F;
            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);
            
            for (int k2 = 0; k2 < list.size(); ++k2) {
                String s1 = (String) list.get(k2);
                this.fontRenderer.drawStringWithShadow(s1, i1, j1, -1);
                
                if (k2 == 0) {
                    j1 += 2;
                }
                
                j1 += 10;
            }
            
            this.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
}

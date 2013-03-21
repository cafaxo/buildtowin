package buildtowin;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.util.StatCollector;

public class GuiBuildingInfo extends GuiScreen {
    private int daysleft = 0;
    
    private int progress = 0;
    
    public GuiBuildingInfo(int daysleft, int progress) {
        super();
        
        this.daysleft = daysleft;
        this.progress = progress;
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/buildinfo.png");
        
        int x = (width - 138) / 2;
        int y = (height - 64) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 138, 100);
        
        this.fontRenderer.drawString("Building Info", (this.width - this.fontRenderer.getStringWidth("Building Info")) / 2, height / 2 - 45, 4210752);
        
        String strTimeleft = "Time left: " + this.daysleft + " days";
        this.fontRenderer.drawString(strTimeleft, (this.width - this.fontRenderer.getStringWidth(strTimeleft)) / 2, height / 2 - 25, 4210752);
        
        String strProgress = "Progress: " + this.progress + "%";
        this.fontRenderer.drawString(strProgress, (this.width - this.fontRenderer.getStringWidth(strProgress)) / 2, height / 2 - 10, 4210752);
        
        super.drawScreen(par1, par2, par3);
    }
}

package buildtowin.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

public class GuiScreenWin extends GuiScreen {
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        GuiButton returnButton = new GuiButton(1, this.width / 2 - 70, this.height / 2 + 85, 140, 20, "Return");
        this.buttonList.add(returnButton);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.mc.displayGuiScreen((GuiScreen) null);
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.mc.renderEngine.bindTexture("/mods/buildtowin/textures/gui/win.png");
        
        int x = (this.width - 170) / 2;
        int y = (this.height - 190) / 2 - 20;
        this.drawTexturedModalRect(x, y, 0, 0, 170, 190);
        
        GL11.glDisable(GL11.GL_BLEND);
        
        super.drawScreen(par1, par2, par3);
    }
}

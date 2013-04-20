package buildtowin.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import buildtowin.tileentity.TileEntityPenalizer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenPenalizer extends GuiScreen {
    
    private TileEntityPenalizer penalizer;
    
    public GuiScreenPenalizer(TileEntityPenalizer penalizer) {
        this.penalizer = penalizer;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        GuiButton lightning = new GuiButton(1, this.width / 2 - 45, this.height / 2 - 30, 90, 20, "Lightning");
        this.buttonList.add(lightning);
        
        GuiButton monsters = new GuiButton(2, this.width / 2 - 45, this.height / 2, 90, 20, "Monsters");
        this.buttonList.add(monsters);
        
        GuiButton poison = new GuiButton(3, this.width / 2 - 45, this.height / 2 + 30, 90, 20, "Poison");
        this.buttonList.add(poison);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.penalizer.sendPenalizePacket(0, 1);
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 2) {
            this.penalizer.sendPenalizePacket(1, 1);
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 3) {
            this.penalizer.sendPenalizePacket(2, 1);
            this.mc.displayGuiScreen((GuiScreen) null);
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/buildtowin/textures/gui/gamehub.png");
        
        int x = (this.width - 170) / 2;
        int y = (this.height - 150) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 170, 200);
        
        this.fontRenderer.drawString("Penalizer", this.width / 2 - 10, this.height / 2 - 62, 4210752);
        
        super.drawScreen(par1, par2, par3);
    }
}
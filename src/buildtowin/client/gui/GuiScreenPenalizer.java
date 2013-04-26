package buildtowin.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import buildtowin.penalization.Penalization;
import buildtowin.tileentity.TileEntityPenalizer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenPenalizer extends GuiScreenAdvanced {
    
    private TileEntityPenalizer penalizer;
    
    private GuiButton lightning;
    
    private GuiButton monsters;
    
    private GuiButton poison;
    
    public GuiScreenPenalizer(TileEntityPenalizer penalizer) {
        this.penalizer = penalizer;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        this.lightning = new GuiButton(1, this.width / 2 - 45, this.height / 2 - 30 - 5, 90, 20, "Lightning");
        this.lightning.enabled = this.penalizer.getTeamHub() != null && this.penalizer.getTeamHub().getEnergy() >= this.penalizer.getPriceClient(Penalization.lightning, 1);
        this.buttonList.add(this.lightning);
        
        this.monsters = new GuiButton(2, this.width / 2 - 45, this.height / 2 - 5, 90, 20, "Monsters");
        this.monsters.enabled = this.penalizer.getTeamHub() != null && this.penalizer.getTeamHub().getEnergy() >= this.penalizer.getPriceClient(Penalization.monsters, 1);
        this.buttonList.add(this.monsters);
        
        this.poison = new GuiButton(3, this.width / 2 - 45, this.height / 2 + 30 - 5, 90, 20, "Poison");
        this.poison.enabled = this.penalizer.getTeamHub() != null && this.penalizer.getTeamHub().getEnergy() >= this.penalizer.getPriceClient(Penalization.poison, 1);
        this.buttonList.add(this.poison);
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
        this.mc.renderEngine.bindTexture("/mods/buildtowin/textures/gui/penalizer.png");
        
        int x = (this.width - 170) / 2;
        int y = (this.height - 150) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 170, 200);
        
        this.fontRenderer.drawString("Penalizer", this.width / 2 - 10, this.height / 2 - 62, 4210752);
        
        super.drawScreen(par1, par2, par3);
        
        if (this.lightning.func_82252_a()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.RED + ((Integer) this.penalizer.getPriceClient(Penalization.lightning, 1)).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
        
        if (this.monsters.func_82252_a()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.RED + ((Integer) this.penalizer.getPriceClient(Penalization.monsters, 1)).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
        
        if (this.poison.func_82252_a()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.RED + ((Integer) this.penalizer.getPriceClient(Penalization.poison, 1)).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
    }
}

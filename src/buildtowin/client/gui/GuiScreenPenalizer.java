package buildtowin.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import buildtowin.penalization.Penalization;
import buildtowin.tileentity.TileEntityPenalizer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenPenalizer extends GuiScreen {
    
    private TileEntityPenalizer penalizer;
    
    private GuiButton lightning;
    
    private GuiButton monsters;
    
    private GuiButton poison;
    
    public GuiScreenPenalizer(TileEntityPenalizer penalizer) {
        this.penalizer = penalizer;
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
            list.add(EnumChatFormatting.AQUA + ((Integer) this.penalizer.getPriceClient(Penalization.lightning, 1)).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
        
        if (this.monsters.func_82252_a()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.AQUA + ((Integer) this.penalizer.getPriceClient(Penalization.monsters, 1)).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
        
        if (this.poison.func_82252_a()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.AQUA + ((Integer) this.penalizer.getPriceClient(Penalization.poison, 1)).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
    }
    
    private void renderTooltip(List list, int x, int y) {
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

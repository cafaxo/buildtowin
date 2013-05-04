package buildtowin.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import buildtowin.BuildToWin;
import buildtowin.client.renderer.ColoredBlockRenderer;
import buildtowin.penalization.Penalization;
import buildtowin.tileentity.TileEntityPenalizer;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.Color;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenPenalizer extends GuiScreenAdvanced {
    
    private TileEntityPenalizer penalizer;
    
    private GuiButton lightning;
    
    private GuiButton monsters;
    
    private GuiButton poison;
    
    private int selectedTeamHub;
    
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
        
        GuiButton selectLeft = new GuiButton(1, this.width / 2 - 36, this.height / 2 - 27, 20, 20, "<");
        selectLeft.enabled = this.getSelectedTeamHub() != null
                && this.selectedTeamHub - 1 >= 0;
        this.buttonList.add(selectLeft);
        
        GuiButton selectRight = new GuiButton(2, this.width / 2 + 15, this.height / 2 - 27, 20, 20, ">");
        selectRight.enabled = this.getSelectedTeamHub() != null
                && this.selectedTeamHub + 1 < this.penalizer.getTeamHub().getGameHub().getConnectedTeamHubs().size();
        this.buttonList.add(selectRight);
        
        this.lightning = new GuiButton(3, this.width / 2 - 45, this.height / 2 + 5, 90, 20, "Lightning");
        this.lightning.enabled = this.getSelectedTeamHub() != null
                && Penalization.lightning.getPrice(this.getSelectedTeamHub()) > 0
                && this.penalizer.getTeamHub().getCoins() >= Penalization.lightning.getPrice(this.getSelectedTeamHub());
        this.buttonList.add(this.lightning);
        
        this.monsters = new GuiButton(4, this.width / 2 - 45, this.height / 2 + 30, 90, 20, "Monsters");
        this.monsters.enabled = this.getSelectedTeamHub() != null
                && Penalization.monsters.getPrice(this.getSelectedTeamHub()) > 0
                && this.penalizer.getTeamHub().getCoins() >= Penalization.monsters.getPrice(this.getSelectedTeamHub());
        this.buttonList.add(this.monsters);
        
        this.poison = new GuiButton(5, this.width / 2 - 45, this.height / 2 + 55, 90, 20, "Poison");
        this.poison.enabled = this.getSelectedTeamHub() != null
                && Penalization.poison.getPrice(this.getSelectedTeamHub()) > 0
                && this.penalizer.getTeamHub().getCoins() >= Penalization.poison.getPrice(this.getSelectedTeamHub());
        this.buttonList.add(this.poison);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.selectedTeamHub -= 1;
            this.initGui();
        } else if (par1GuiButton.id == 2) {
            this.selectedTeamHub += 1;
            this.initGui();
        } else if (par1GuiButton.id == 3) {
            this.penalizer.getTeamHub().setCoins(this.penalizer.getTeamHub().getCoins() - Penalization.lightning.getPrice(this.penalizer.getTeamHub()));
            this.penalizer.sendPenalizePacket(0, this.selectedTeamHub);
            this.initGui();
        } else if (par1GuiButton.id == 4) {
            this.penalizer.getTeamHub().setCoins(this.penalizer.getTeamHub().getCoins() - Penalization.monsters.getPrice(this.penalizer.getTeamHub()));
            this.penalizer.sendPenalizePacket(1, this.selectedTeamHub);
            this.initGui();
        } else if (par1GuiButton.id == 5) {
            this.penalizer.getTeamHub().setCoins(this.penalizer.getTeamHub().getCoins() - Penalization.poison.getPrice(this.penalizer.getTeamHub()));
            this.penalizer.sendPenalizePacket(2, this.selectedTeamHub);
            this.initGui();
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
        
        this.fontRenderer.drawString("Team to penalize:", (this.width - this.fontRenderer.getStringWidth("Team to penalize:")) / 2, this.height / 2 - 40, 4210752);
        
        Color teamColor = new Color(1.0F, 1.0F, 1.0F);
        
        if (this.penalizer.getTeamHub() != null && this.penalizer.getTeamHub().getGameHub() != null) {
            teamColor = Color.fromId(((TileEntityTeamHub) this.penalizer.getTeamHub().getGameHub().getConnectedTeamHubs().get(this.selectedTeamHub)).getColor().id);
        }
        
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        
        ColoredBlockRenderer.instance.renderBlockAsItem(BuildToWin.teamHub, this.mc.renderEngine, new RenderBlocks(), this.width / 2 - 8, this.height / 2 - 25, this.zLevel, teamColor);
        
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        
        super.drawScreen(par1, par2, par3);
        
        if (this.getSelectedTeamHub() != null) {
            if (this.lightning.func_82252_a() && Penalization.lightning.getPrice(this.getSelectedTeamHub()) > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(EnumChatFormatting.RED + ((Integer) Penalization.lightning.getPrice(this.getSelectedTeamHub())).toString() + " coins");
                this.renderTooltip(list, par1, par2);
            }
            
            if (this.monsters.func_82252_a() && Penalization.monsters.getPrice(this.getSelectedTeamHub()) > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(EnumChatFormatting.RED + ((Integer) Penalization.monsters.getPrice(this.getSelectedTeamHub())).toString() + " coins");
                this.renderTooltip(list, par1, par2);
            }
            
            if (this.poison.func_82252_a() && Penalization.poison.getPrice(this.getSelectedTeamHub()) > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(EnumChatFormatting.RED + ((Integer) Penalization.poison.getPrice(this.getSelectedTeamHub())).toString() + " coins");
                this.renderTooltip(list, par1, par2);
            }
        }
    }
    
    private TileEntityTeamHub getSelectedTeamHub() {
        if (this.selectedTeamHub >= 0
                && this.penalizer.getTeamHub() != null
                && this.penalizer.getTeamHub().getGameHub() != null
                && this.selectedTeamHub < this.penalizer.getTeamHub().getGameHub().getConnectedTeamHubs().size()) {
            return (TileEntityTeamHub) this.penalizer.getTeamHub().getGameHub().getConnectedTeamHubs().get(this.selectedTeamHub);
        }
        
        return null;
    }
}

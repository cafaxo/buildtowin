package buildtowin.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import buildtowin.tileentity.TileEntityGameHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenGameHub extends GuiScreen {
    
    private TileEntityGameHub gameHub;
    
    private boolean isPlayerCreative = false;
    
    public GuiScreenGameHub(TileEntityGameHub gameHub, boolean isPlayerCreative) {
        this.gameHub = gameHub;
        
        this.isPlayerCreative = isPlayerCreative;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        GuiButton decreaseTimespan = new GuiButton(1, this.width / 2 - 36, this.height / 2 - 27, 20, 20, "-");
        decreaseTimespan.enabled = this.isPlayerCreative && this.gameHub.getPlannedTimespan() >= 24000;
        this.buttonList.add(decreaseTimespan);
        
        GuiButton increaseTimespan = new GuiButton(2, this.width / 2 + 15, this.height / 2 - 27, 20, 20, "+");
        increaseTimespan.enabled = this.isPlayerCreative;
        this.buttonList.add(increaseTimespan);
        
        if (this.gameHub.getDeadline() == 0) {
            GuiButton start = new GuiButton(3, this.width / 2 - 45, this.height / 2 + 30, 90, 20, "Start");
            start.enabled = this.gameHub.getPlannedTimespan() >= 24000;
            this.buttonList.add(start);
        } else {
            GuiButton stop = new GuiButton(4, this.width / 2 - 45, this.height / 2 + 30, 90, 20, "Stop");
            this.buttonList.add(stop);
        }
        
        GuiButton load = new GuiButton(5, this.width / 2 - 45, this.height / 2 + 5, 90, 20, "Load");
        load.enabled = this.isPlayerCreative && this.gameHub.getDeadline() == 0;
        this.buttonList.add(load);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.gameHub.setPlannedTimespan(this.gameHub.getPlannedTimespan() - 24000);
            this.gameHub.sendTimespanUpdatePacket();
            this.initGui();
        } else if (par1GuiButton.id == 2) {
            this.gameHub.setPlannedTimespan(this.gameHub.getPlannedTimespan() + 24000);
            this.gameHub.sendTimespanUpdatePacket();
            this.initGui();
        } else if (par1GuiButton.id == 3) {
            this.gameHub.setDeadline(1);
            this.gameHub.sendStartPacket();
            this.initGui();
        } else if (par1GuiButton.id == 4) {
            this.gameHub.setDeadline(0);
            this.gameHub.sendStopPacket();
            this.initGui();
        } else if (par1GuiButton.id == 5) {
            this.mc.displayGuiScreen(new GuiScreenBlueprintLoad(this.gameHub, this));
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
        
        this.fontRenderer.drawString("Game Hub", this.width / 2 - 10, this.height / 2 - 62, 4210752);
        
        this.fontRenderer.drawString("Days left:", (this.width - this.fontRenderer.getStringWidth("Days left:")) / 2, this.height / 2 - 40, 4210752);
        
        Integer daysLeft = (int) (this.gameHub.getPlannedTimespan() / 24000D);
        this.fontRenderer.drawString(daysLeft.toString(), (this.width - this.fontRenderer.getStringWidth(daysLeft.toString())) / 2, this.height / 2 - 21, 4210752);
        
        super.drawScreen(par1, par2, par3);
    }
}

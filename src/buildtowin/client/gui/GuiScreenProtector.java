package buildtowin.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import buildtowin.tileentity.TileEntityProtector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenProtector extends GuiScreenAdvanced {
    
    private TileEntityProtector protector;
    
    private boolean isPlayerCreative;
    
    private GuiButton decreaseRadius;
    
    private GuiButton increaseRadius;
    
    public GuiScreenProtector(TileEntityProtector protector, boolean isPlayerCreative) {
        this.protector = protector;
        this.isPlayerCreative = isPlayerCreative;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        this.decreaseRadius = new GuiButton(1, this.width / 2 - 36, this.height / 2 - 27 + 30, 20, 20, "-");
        this.decreaseRadius.enabled = this.protector.getTeamHub() != null && this.protector.getRadius() > 0;
        this.buttonList.add(this.decreaseRadius);
        
        this.increaseRadius = new GuiButton(2, this.width / 2 + 15, this.height / 2 - 27 + 30, 20, 20, "+");
        this.increaseRadius.enabled = this.protector.getTeamHub() != null
                && this.protector.getTeamHub().getEnergy() >= this.protector.getPrice(this.protector.getRadius() + 1);
        
        this.buttonList.add(this.increaseRadius);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.protector.getTeamHub().setEnergy(this.protector.getTeamHub().getEnergy() - this.protector.getPrice(this.protector.getRadius() - 1));
            this.protector.setRadius(this.protector.getRadius() - 1);
            this.protector.sendRadiusChangedPacket();
            this.initGui();
        } else if (par1GuiButton.id == 2) {
            this.protector.getTeamHub().setEnergy(this.protector.getTeamHub().getEnergy() - this.protector.getPrice(this.protector.getRadius() + 1));
            this.protector.setRadius(this.protector.getRadius() + 1);
            this.protector.sendRadiusChangedPacket();
            this.initGui();
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/buildtowin/textures/gui/protector.png");
        
        int x = (this.width - 170) / 2;
        int y = (this.height - 90) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 170, 200);
        
        this.fontRenderer.drawString("Protector", this.width / 2 - 10, this.height / 2 - 62 + 30, 4210752);
        
        this.fontRenderer.drawString("Protection radius:", (this.width - this.fontRenderer.getStringWidth("Protection radius:")) / 2, this.height / 2 - 40 + 30, 4210752);
        
        String strRadius = ((Integer) this.protector.getRadius()).toString();
        this.fontRenderer.drawString(strRadius, (this.width - this.fontRenderer.getStringWidth(strRadius)) / 2, this.height / 2 - 21 + 30, 4210752);
        
        super.drawScreen(par1, par2, par3);
        
        if (this.protector.getRadius() > 0 && this.decreaseRadius.func_82252_a()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.GREEN + ((Integer) Math.abs(this.protector.getPrice(this.protector.getRadius() - 1))).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
        
        if (this.increaseRadius.func_82252_a()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(EnumChatFormatting.RED + ((Integer) this.protector.getPrice(this.protector.getRadius() + 1)).toString() + " coins");
            this.renderTooltip(list, par1, par2);
        }
    }
}
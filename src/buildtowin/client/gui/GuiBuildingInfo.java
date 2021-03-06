package buildtowin.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import org.lwjgl.opengl.GL11;

import buildtowin.client.GameStats;
import buildtowin.client.TeamStats;
import buildtowin.util.Color;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBuildingInfo extends Gui {
    
    private Minecraft theGame;
    
    private float speed;
    
    private float position;
    
    public GuiBuildingInfo(Minecraft par1Minecraft) {
        this.theGame = par1Minecraft;
        this.position = -32;
        this.speed = 0;
    }
    
    public void tick() {
        if (!GameStats.instance.getTeamStatsList().isEmpty()
                && GameStats.instance.getTeamStatsList().get(0).daysLeft > 0.00F) {
            if (this.position < 0) {
                this.position += this.speed;
                this.speed += 0.1F;
            } else {
                this.position = 0;
                this.speed = 0;
            }
        } else {
            if (this.position > -32) {
                this.position -= this.speed;
                this.speed += 0.1F;
            } else {
                this.position = -32;
                this.speed = 0;
            }
        }
        
        if (!GameStats.instance.getTeamStatsList().isEmpty() && this.position != -32) {
            int y = 0;
            
            this.drawHud(GameStats.instance.getTeamStatsList().get(0), Math.round(this.position), y, true);
            y += 80;
            
            for (int i = 1; i < GameStats.instance.getTeamStatsList().size(); ++i) {
                this.drawHud(GameStats.instance.getTeamStatsList().get(i), Math.round(this.position), y, false);
                y += 20;
            }
        }
    }
    
    private void drawHud(TeamStats teamStats, int x, int y, boolean full) {
        this.theGame.renderEngine.bindTexture("/mods/buildtowin/textures/gui/icons.png");
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 1.0F);
        
        String progress = String.format("%.2f", teamStats.progress * 100.F) + "%";
        
        if (full) {
            this.drawTexturedModalRect((10 + x) * 2, (10 + y) * 2, 0, 0, 16, 16);
            this.drawTexturedModalRect((10 + x) * 2, (30 + y) * 2, 17, 0, 16, 16);
            this.drawTexturedModalRect((10 + x) * 2, (50 + y) * 2, 33, 0, 16, 16);
            
            GL11.glPopMatrix();
            
            Integer energy = teamStats.coins;
            
            String daysLeft = "0,00";
            
            if (teamStats.daysLeft > 0.F) {
                daysLeft = String.format("%.2f", teamStats.daysLeft);
            }
            
            this.theGame.fontRenderer.drawStringWithShadow(daysLeft, 22 + x, 10 + y, 0xffffff);
            this.theGame.fontRenderer.drawStringWithShadow(daysLeft, 22 + x, 10 + y, 0xffffff);
            this.theGame.fontRenderer.drawStringWithShadow(progress, 22 + x, 30 + y, 0xffffff);
            this.theGame.fontRenderer.drawStringWithShadow(energy.toString(), 22 + x, 50 + y, 0xffffff);
        } else {
            this.drawTexturedModalRect((10 + x) * 2, y * 2, 17, 0, 16, 16);
            
            GL11.glPopMatrix();
            
            Color white = new Color(1.0F, 1.0F, 1.0F);
            
            this.drawStringWithShadow(progress, 22 + x, y, white, Color.fromId(teamStats.colorId));
        }
    }
    
    public void drawStringWithShadow(String string, int x, int y, Color stringColor, Color shadowColor) {
        this.theGame.fontRenderer.drawString(string, x + 1, y + 1, shadowColor.toDecimal());
        this.theGame.fontRenderer.drawString(string, x, y, stringColor.toDecimal());
    }
}

package buildtowin.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import buildtowin.tileentity.TileEntityTeamHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBuildingInfo extends Gui {
    
    private Minecraft theGame;
    
    private float ySpeed;
    
    private float yPosition;
    
    public GuiBuildingInfo(Minecraft par1Minecraft) {
        this.theGame = par1Minecraft;
        this.yPosition = -32;
        this.ySpeed = 0;
    }
    
    public void tick() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        
        if (player != null) {
            TileEntityTeamHub teamHub = TileEntityTeamHub.getTeamHub(player);
            
            if (teamHub != null && teamHub.getGameHub() != null && teamHub.getGameHub().getDeadline() != 0) {
                if (this.yPosition < 0) {
                    this.yPosition += this.ySpeed;
                    this.ySpeed += 0.1F;
                } else {
                    this.yPosition = 0;
                    this.ySpeed = 0;
                }
            } else {
                if (this.yPosition > -32) {
                    this.yPosition -= this.ySpeed;
                    this.ySpeed += 0.1F;
                } else {
                    this.yPosition = -32;
                    this.ySpeed = 0;
                }
            }
            
            if (this.yPosition != -32) {
                int progress = 100;
                String daysLeft = "0,00";
                
                if (teamHub != null && teamHub.getGameHub() != null) {
                    progress = (int) (teamHub.getProgress() * 100.F);
                    float daysLeftFloat = (teamHub.getGameHub().getDeadline() - teamHub.getGameHub().getRealWorldTime()) / 24000.F;
                    
                    if (daysLeftFloat > 0.F) {
                        daysLeft = String.format("%.2f", daysLeftFloat);
                    }
                }
                
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                this.theGame.renderEngine.bindTexture("/achievement/bg.png");
                
                this.drawTexturedModalRect(0, Math.round(this.yPosition), 96, 202, 160, 32);
                this.drawTexturedModalRect(45, Math.round(this.yPosition), 96 + 30, 202, 160 - 30, 32);
                this.theGame.fontRenderer.drawStringWithShadow("Days left: " + daysLeft, 10, Math.round(this.yPosition) + 12, 0xffffff);
                this.theGame.fontRenderer.drawStringWithShadow("Progress: " + progress + "%", 90, Math.round(this.yPosition) + 12, 0xffffff);
            }
        }
    }
}

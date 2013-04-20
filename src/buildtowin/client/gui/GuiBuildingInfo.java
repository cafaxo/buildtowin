package buildtowin.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
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
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        
        if (entityPlayer != null) {
            TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityTeamHub.class);
            
            if (teamHub != null && teamHub.getGameHub() != null && teamHub.getGameHub().getDeadline() != 0) {
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
            
            if (this.position != -32) {
                int progress = 100;
                Integer energy = 0;
                String daysLeft = "0,00";
                
                if (teamHub != null && teamHub.getGameHub() != null) {
                    progress = (int) (teamHub.getProgress() * 100.F);
                    energy = teamHub.getEnergy();
                    float daysLeftFloat = (teamHub.getGameHub().getDeadline() - teamHub.getGameHub().getRealWorldTime()) / 24000.F;
                    
                    if (daysLeftFloat > 0.F) {
                        daysLeft = String.format("%.2f", daysLeftFloat);
                    }
                }
                
                this.theGame.renderEngine.bindTexture("/mods/buildtowin/textures/gui/icons.png");
                
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glPushMatrix();
                GL11.glScalef(0.5F, 0.5F, 1.0F);
                
                this.drawTexturedModalRect((10 + Math.round(this.position)) * 2, 10 * 2, 0, 0, 16, 16);
                this.drawTexturedModalRect((10 + Math.round(this.position)) * 2, 30 * 2, 17, 0, 16, 16);
                this.drawTexturedModalRect((10 + Math.round(this.position)) * 2, 50 * 2, 33, 0, 16, 16);
                
                GL11.glPopMatrix();
                
                this.theGame.fontRenderer.drawStringWithShadow(daysLeft, 22 + Math.round(this.position), 10, 0xffffff);
                this.theGame.fontRenderer.drawStringWithShadow(progress + "%", 22 + Math.round(this.position), 30, 0xffffff);
                this.theGame.fontRenderer.drawStringWithShadow(energy.toString(), 22 + Math.round(this.position), 50, 0xffffff);
            }
        }
    }
}

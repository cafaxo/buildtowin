package buildtowin.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.world.World;
import buildtowin.client.GameStats;
import buildtowin.util.Coordinates;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureBlueprintDetector extends TextureStitched {
    
    public double currentAngle;
    
    public double angleDelta;
    
    public TextureBlueprintDetector() {
        super("buildtowin:detector");
    }
    
    @Override
    public void updateAnimation() {
        Minecraft minecraft = Minecraft.getMinecraft();
        
        if (minecraft.theWorld != null && minecraft.thePlayer != null) {
            if (!GameStats.instance.getTeamStatsList().isEmpty()) {
                Coordinates coords = GameStats.instance.getTeamStatsList().get(0).nextUnfinishedBlueprint;
                
                if (coords.x != 0 && coords.y != 0 && coords.z != 0) {
                    this.updatePointer(minecraft.theWorld, minecraft.thePlayer.posX, minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, false, coords);
                }
            }
        } else {
            this.updatePointer(null, 0.0D, 0.0D, 0.0D, true, null);
        }
    }
    
    public void updatePointer(World world, double playerPosX, double playerPosZ, double playerRotation, boolean shouldRefresh, Coordinates coords) {
        double d3 = 0.0D;
        
        if (world != null && !shouldRefresh) {
            world.getSpawnPoint();
            double d4 = coords.x - playerPosX;
            double d5 = coords.z - playerPosZ;
            playerRotation %= 360.0D;
            d3 = -((playerRotation - 90.0D) * Math.PI / 180.0D - Math.atan2(d5, d4));
            
            if (!world.provider.isSurfaceWorld()) {
                d3 = Math.random() * Math.PI * 2.0D;
            }
        }
        
        double d6;
        
        for (d6 = d3 - this.currentAngle; d6 < -Math.PI; d6 += Math.PI * 2D) {
        }
        
        while (d6 >= Math.PI) {
            d6 -= Math.PI * 2D;
        }
        
        if (d6 < -1.0D) {
            d6 = -1.0D;
        }
        
        if (d6 > 1.0D) {
            d6 = 1.0D;
        }
        
        this.angleDelta += d6 * 0.1D;
        this.angleDelta *= 0.8D;
        this.currentAngle += this.angleDelta;
        
        int frame;
        
        for (frame = (int) ((this.currentAngle / (Math.PI * 2D) + 1.0D) * this.textureList.size()) % this.textureList.size(); frame < 0; frame = (frame + this.textureList.size()) % this.textureList.size()) {
        }
        
        if (frame != this.frameCounter) {
            this.frameCounter = frame;
            this.textureSheet.copyFrom(this.originX, this.originY, (Texture) this.textureList.get(this.frameCounter), false);
        }
    }
}

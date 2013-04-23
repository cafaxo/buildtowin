package buildtowin.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.lwjgl.opengl.GL11;

import buildtowin.network.PacketIds;
import buildtowin.tileentity.TileEntityProtector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenProtector extends GuiScreen {
    
    private TileEntityProtector protector;
    
    private boolean isPlayerCreative;
    
    public GuiScreenProtector(TileEntityProtector protector, boolean isPlayerCreative) {
        this.protector = protector;
        this.isPlayerCreative = isPlayerCreative;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        GuiButton decreaseRadius = new GuiButton(1, this.width / 2 - 36, this.height / 2 - 27 + 30, 20, 20, "-");
        decreaseRadius.enabled = this.isPlayerCreative && this.protector.getRadius() > 0;
        this.buttonList.add(decreaseRadius);
        
        GuiButton increaseTimespan = new GuiButton(2, this.width / 2 + 15, this.height / 2 - 27 + 30, 20, 20, "+");
        increaseTimespan.enabled = this.isPlayerCreative;
        this.buttonList.add(increaseTimespan);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.protector.setRadius(this.protector.getRadius() - 1);
            this.initGui();
            this.sendRadiusPacket();
        } else if (par1GuiButton.id == 2) {
            this.protector.setRadius(this.protector.getRadius() + 1);
            this.initGui();
            this.sendRadiusPacket();
        }
    }
    
    private void sendRadiusPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.PROTECTOR_RADIUS_UPDATE);
            
            dataoutputstream.writeInt(this.protector.xCoord);
            dataoutputstream.writeInt(this.protector.yCoord);
            dataoutputstream.writeInt(this.protector.zCoord);
            
            dataoutputstream.writeInt(this.protector.getRadius());
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
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
    }
}
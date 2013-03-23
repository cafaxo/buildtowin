package buildtowin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.packet.Packet250CustomPayload;

@SideOnly(Side.CLIENT)
public class GuiBuildingStart extends GuiScreen {
    private TileEntityBuildingController buildingController;
    
    public GuiBuildingStart(TileEntityBuildingController buildingController) {
        this.buildingController = buildingController;
    }
    
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 45, this.height / 2 - 30, 90, 20, "Start!"));
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
            
            try {
                dataoutputstream.writeInt(this.buildingController.xCoord);
                dataoutputstream.writeInt(this.buildingController.yCoord);
                dataoutputstream.writeInt(this.buildingController.zCoord);
                
                this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btwstart", bytearrayoutputstream.toByteArray()));
                PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("btwstart", bytearrayoutputstream.toByteArray()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            
            this.mc.displayGuiScreen((GuiScreen) null);
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/buildinfo.png");
        
        int x = (width - 138) / 2;
        int y = (height - 64) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 138, 100);
        
        super.drawScreen(par1, par2, par3);
    }
}

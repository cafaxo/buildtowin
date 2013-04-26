package buildtowin.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import buildtowin.network.PacketIds;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenBuildingHub extends GuiScreen {
    
    private TileEntity sender;
    
    private GuiTextField saveFileName;
    
    public GuiScreenBuildingHub(TileEntity sender) {
        this.sender = sender;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        this.saveFileName = new GuiTextField(this.fontRenderer, this.width / 2 - 45, this.height / 2 - 30, 90, 15);
        
        GuiButton save = new GuiButton(1, this.width / 2 - 45, this.height / 2 - 5, 90, 20, "Save");
        this.buttonList.add(save);
        
        GuiButton load = new GuiButton(2, this.width / 2 - 45, this.height / 2 + 20, 90, 20, "Load");
        this.buttonList.add(load);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.sendBlueprintSavePacket();
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 2) {
            this.mc.displayGuiScreen(new GuiScreenBlueprintLoad(this.sender, this));
        }
    }
    
    @Override
    public void updateScreen() {
        this.saveFileName.updateCursorCounter();
    }
    
    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        this.saveFileName.textboxKeyTyped(par1, par2);
    }
    
    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        this.saveFileName.mouseClicked(par1, par2, par3);
    }
    
    private void sendBlueprintSavePacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.BLUEPRINT_SAVE);
            
            dataoutputstream.writeInt(this.sender.xCoord);
            dataoutputstream.writeInt(this.sender.yCoord);
            dataoutputstream.writeInt(this.sender.zCoord);
            
            dataoutputstream.writeUTF(this.saveFileName.getText());
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/buildtowin/textures/gui/buildinghub.png");
        
        int x = (this.width - 170) / 2;
        int y = (this.height - 150) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 170, 200);
        
        this.fontRenderer.drawString("Building Hub", this.width / 2 - 10, this.height / 2 - 62, 4210752);
        
        this.saveFileName.drawTextBox();
        
        super.drawScreen(par1, par2, par3);
    }
}
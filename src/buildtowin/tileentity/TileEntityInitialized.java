package buildtowin.tileentity;

import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityInitialized extends TileEntity {
    
    private boolean wasInititalized;
    
    public abstract void initialize();
    
    @Override
    public void updateEntity() {
        if (!this.wasInititalized && !this.isInvalid()) {
            this.initialize();
            this.wasInititalized = true;
        }
    }
    
    @Override
    public void invalidate() {
        this.wasInititalized = false;
        super.invalidate();
    }
}

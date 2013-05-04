package buildtowin;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import buildtowin.tileentity.ITicketProvider;

public class ChunkLoadingManager implements LoadingCallback {
    
    public static void forceChunk(ITicketProvider ticketProvider) {
        if (ticketProvider.getTicket() == null) {
            TileEntity tileEntity = (TileEntity) ticketProvider;
            
            if (!tileEntity.worldObj.isRemote) {
                Ticket ticket = ForgeChunkManager.requestTicket(BuildToWin.instance, tileEntity.worldObj, Type.NORMAL);
                ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(tileEntity.xCoord >> 4, tileEntity.zCoord >> 4));
                
                ticket.getModData().setIntArray("coords", new int[] { tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord });
                ticketProvider.setTicket(ticket);
            }
        }
    }
    
    public static void unforceChunk(ITicketProvider ticketProvider) {
        TileEntity tileEntity = (TileEntity) ticketProvider;
        
        if (!tileEntity.worldObj.isRemote) {
            ForgeChunkManager.unforceChunk(ticketProvider.getTicket(), new ChunkCoordIntPair(tileEntity.xCoord >> 4, tileEntity.zCoord >> 4));
            ticketProvider.setTicket(null);
        }
    }
    
    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        for (Ticket ticket : tickets) {
            int tileEntityCoords[] = ticket.getModData().getIntArray("coords");
            TileEntity tileEntity = world.getBlockTileEntity(tileEntityCoords[0], tileEntityCoords[1], tileEntityCoords[2]);
            
            if (tileEntity instanceof ITicketProvider) {
                ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(tileEntity.xCoord >> 4, tileEntity.zCoord >> 4));
                ((ITicketProvider) tileEntity).setTicket(ticket);
            }
        }
    }
}

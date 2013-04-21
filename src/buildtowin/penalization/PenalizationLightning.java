package buildtowin.penalization;

import net.minecraft.entity.effect.EntityLightningBolt;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.Coordinates;

public class PenalizationLightning extends Penalization {
    
    public PenalizationLightning(int penalizationId) {
        super(penalizationId);
    }
    
    @Override
    public void penalize(TileEntityTeamHub teamHub, int strength) {
        Coordinates randCoords = teamHub.getBlueprint().getRandomCoordinatesOutside();
        
        teamHub.worldObj.addWeatherEffect(new EntityLightningBolt(
                teamHub.worldObj,
                (double) randCoords.x,
                (double) randCoords.y,
                (double) randCoords.z));
        
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = 0; z < 4; ++z) {
                    if (teamHub.getBlueprint().getBlockData(randCoords.x + x - 2, randCoords.y + y - 2, randCoords.z + z - 2) != null) {
                        teamHub.worldObj.destroyBlock(randCoords.x + x - 2, randCoords.y + y - 2, randCoords.z + z - 2, false);
                    }
                }
            }
        }
    }
    
    @Override
    public int getRepetitions(TileEntityTeamHub teamHub, int strength) {
        return 5;
    }
    
    @Override
    public int getPrice(TileEntityTeamHub teamHub, int strength) {
        if (teamHub.getBlueprint().getBlocks().size() == 0) {
            return 5;
        }
        
        return 100 * teamHub.getFinishedBlockCount() / teamHub.getBlueprint().getBlocks().size();
    }
}

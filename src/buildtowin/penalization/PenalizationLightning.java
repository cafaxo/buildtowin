package buildtowin.penalization;

import net.minecraft.entity.effect.EntityLightningBolt;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.Coordinates;

public class PenalizationLightning extends Penalization {
    
    public PenalizationLightning(int penalizationId) {
        super(penalizationId);
    }
    
    @Override
    public void penalize(TileEntityTeamHub teamHub) {
        Coordinates randCoords = teamHub.getBlueprint().getRandomCoordinatesOutside();
        
        teamHub.worldObj.addWeatherEffect(new EntityLightningBolt(
                teamHub.worldObj,
                randCoords.x,
                randCoords.y,
                randCoords.z));
        
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
    public int getRepetitions(TileEntityTeamHub teamHub) {
        return 5;
    }
    
    @Override
    public int getPrice(TileEntityTeamHub teamHub) {
        return (int) (10000.F * teamHub.getProgress());
    }
}

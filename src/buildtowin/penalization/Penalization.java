package buildtowin.penalization;

import buildtowin.tileentity.TileEntityTeamHub;

public class Penalization {
    
    public static Penalization penalizationList[] = new Penalization[3];
    
    public static Penalization lightning = new PenalizationLightning(0);
    
    public static Penalization monsters = new PenalizationMonsters(1);
    
    public static Penalization poison = new PenalizationPoison(2);
    
    public int penalizationId;
    
    public Penalization(int penalizationId) {
        this.penalizationId = penalizationId;
        Penalization.penalizationList[penalizationId] = this;
    }
    
    public void penalize(TileEntityTeamHub teamHub, int strength) {
    }
    
    public int getRepetitions(TileEntityTeamHub teamHub, int strength) {
        return 1;
    }
    
    public int getChance(TileEntityTeamHub teamHub, int strength) {
        return 100;
    }
    
    public int getPrice(TileEntityTeamHub teamHub, int strength) {
        return 0;
    }
}

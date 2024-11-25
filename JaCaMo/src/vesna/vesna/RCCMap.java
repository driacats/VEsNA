package vesna;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import jason.asSyntax.Literal;
import jason.asSyntax.ASSyntax;

public class RCCMap {

    private Map<Literal, List<Integer>> triangleMap;
    private Map<Integer, List<Integer>> adjsMap;
    private int current;
    private int target;

    public RCCMap () {
        this.triangleMap = new HashMap<>();
        this.adjsMap = new HashMap<>();
        this.target = -1;
    }

    public void updateCurrent(int current) {
        this.current = current;
    }

    public int getCurrent() {
        return this.current;
    }

    public void setTarget(int t) {
        this.target = t;
    }

    public void setTarget(long t) {
        this.target = (int) t;
    }

    public int getTarget(){
        return this.target;
    }

    public void addTriangle(Literal currentRegion, int triangle){
        this.triangleMap.putIfAbsent( currentRegion, new ArrayList<Integer>() );
        if ( ! triangleMap.get( currentRegion ).contains(triangle) )
            this.triangleMap.get( currentRegion ).add(triangle);
        try{
            Literal unreached = ASSyntax.parseLiteral("unreached");
            if (triangleMap.keySet().contains(unreached) && triangleMap.get( unreached ).contains( triangle ))
                triangleMap.get( unreached ).remove(Integer.valueOf(triangle));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNotReachedTriangle( int triangle ) {
        try {
            Literal unreached = ASSyntax.parseLiteral("unreached");
            this.triangleMap.putIfAbsent( unreached, new ArrayList<Integer>() );
            if ( ! triangleMap.get(unreached).contains(triangle) )
                this.triangleMap.get(unreached).add(triangle);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void addAdjsToTriangle( int triangle, List<Integer> adjs ) {
        adjsMap.putIfAbsent( triangle , adjs );
    }

    public List<Integer> getAdjs ( int triangle ) {
        return adjsMap.get(triangle);
    }

    public void printMap() {
        for (Literal r : triangleMap.keySet() ){
            System.out.println( r + " : " + triangleMap.get(r) );
        }
    }

    // Deprecated    
    public boolean isTriangleExplored(Literal currentRegion, int t) {
        if (! triangleMap.keySet().contains(currentRegion))
            return false;
        return triangleMap.get(currentRegion).contains(t);
    }

    public Literal getRegionFromTriangle(int t) {
        for (Literal key : triangleMap.keySet() ){
            if (triangleMap.get(key).contains(t))
                return key;
        }
        return null;
    }
}
package vesna;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import jason.asSyntax.Literal;

public class RCCMap {

    private Map<Literal, List<Integer>> triangleMap;
    private Map<Integer, List<Integer>> adjsMap;
    private int current;

    public RCCMap () {
        this.triangleMap = new HashMap<>();
        this.adjsMap = new HashMap<>();
    }

    public void updateCurrent(int current) {
        this.current = current;
    }

    public int getCurrent() {
        return this.current;
    }

    public void addTriangle(Literal currentRegion, int triangle){
        this.triangleMap.putIfAbsent( currentRegion, new ArrayList<Integer>() );
        if ( ! triangleMap.get( currentRegion ).contains(triangle) )
            this.triangleMap.get( currentRegion ).add(triangle);
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

    public boolean isTriangleExplored(Literal currentRegion, int t) {
        if (! triangleMap.keySet().contains(currentRegion))
            return false;
        return triangleMap.get(currentRegion).contains(t);
    }
}
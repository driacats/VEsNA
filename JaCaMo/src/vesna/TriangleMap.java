package vesna;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import jason.asSyntax.Literal;

public class TriangleMap {

	public class Triangle {
		// index is the index of the triangle in Godot
		// flag can be:
		// 0: not visited
		// 1: visited
		// -1: not reachable
		public int index;
		public Literal region;
		public int flag;

		public Triangle(int index){
			this.index = index;
			this.flag = 0;
		}

		@Override
		public boolean equals(Object o) {
			if ( this == o )
				return true;
			if ( o == null || getClass() != o.getClass() )
				return false;
			Triangle t = (Triangle) o;
			return t.index == this.index;
		}

		@Override
		public int hashCode(){
			return Objects.hash(this.index);
		}

		@Override
		public String toString(){
			return "T(" + this.index + ")[" + this.region + "] " + this.flag;
		}

	}

	private Queue<Triangle> q = new LinkedList<>();
	
	private Triangle current;
	private Triangle target;

	private Graph<Triangle, DefaultEdge> triangleMap = new SimpleGraph<>(DefaultEdge.class);

	public void addTriangle( int idx ){
		Triangle t = new Triangle( idx );
		triangleMap.addVertex(t);
	}

	public int getCurrent(){
		return this.current.index;
	}

	public int getTarget(){
		return this.target.index;
	}

	public void setCurrent( int current ){
		if ( this.current.index == current )
			return;
		Triangle newCurrent = new Triangle( current );
		newCurrent.flag = 1;
		this.current = newCurrent;
	}

	public void addEdges( int t, ArrayList<Integer> adjs){
		Triangle current = new Triangle(t);
		for ( Integer adj : adjs ){
			Triangle adjT = new Triangle(adj);
			triangleMap.addEdge(current, adjT);
		}
	}

	public ArrayList<Integer> getEdges( int v ){
		ArrayList<Integer> adjs = new ArrayList<>();
		for (Edge t : triangleMap.edgesOf(new Triangle(v)) ){
			adjs.add(t.index);
		}
	}

	public void setCurrentRegion ( Literal region ){
		this.current.region = region;
	}

	public void setVNotReachable( int v ){
		for ( Triangle t : triangleMap.vertexSet() ){
			if ( t.index == v )
				t.flag = -1;
		}
	}
}

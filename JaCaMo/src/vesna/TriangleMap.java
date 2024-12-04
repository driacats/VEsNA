package vesna;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.Stack;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

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

	private Stack<Triangle> q = new Stack<>();
	private Map<String, Stack<Triangle>> regionQueues = new HashMap<>();
	
	private Triangle current;
	private Triangle target;

	private Graph<Triangle, DefaultEdge> triangleMap = new SimpleGraph<>(DefaultEdge.class);

	public void addTriangle( int idx ){
		if ( triangleMap.containsVertex(new Triangle(idx)))
			return;
		Triangle t = new Triangle( idx );
		triangleMap.addVertex(t);
	}

	public int getCurrent(){
		return this.current.index;
	}

	public int getTarget(){
		return this.target.index;
	}

	public boolean setTarget( int target ){
		System.out.println("SET TARGET");
		if ( this.target != null && target == this.target.index )
			return false;
		if ( ! triangleMap.containsVertex(new Triangle(target)) ){
			throw new Error("The triangle is not in the graph!");
		}
		for (Triangle t : triangleMap.vertexSet()){
			if (t.index == target){
				this.target = t;
				return true;
			}
		}
		return false;
	}

	public int nextTriangle(){
		if (q.empty())
			return -1;
		Triangle next = q.pop();
		if (next == null)
			return -1;
		return next.index;
	}

	public void setCurrent( int current ){
		if ( this.current != null && this.current.index == current )
			return;
		Triangle newCurrent = new Triangle( current );
		if (!triangleMap.containsVertex(newCurrent))
			triangleMap.addVertex(newCurrent);
		for (Triangle t : triangleMap.vertexSet() ){
			if ( t.index == current ){
				t.flag = 1;
				this.current = t;
				q.remove(t);
				break;
			}
		}
	}

	public void addEdges( int t, ArrayList<Integer> adjs){
		Triangle current = new Triangle(t);
		for ( Integer adj : adjs ){
			Triangle adjT = new Triangle(adj);
			if (!triangleMap.containsVertex(adjT)) {
				triangleMap.addVertex(adjT);
			}
			for (Triangle tr : triangleMap.vertexSet()){
				if (tr.index == adj ){
					if (tr.flag == 0){
						q.push(tr);
					}
				}
			}
			triangleMap.addEdge(current, adjT);
		}
	}

	public ArrayList<Integer> getEdges( int v ){
		ArrayList<Integer> adjs = new ArrayList<>();
		for (DefaultEdge t : triangleMap.edgesOf(new Triangle(v)) ){
			Triangle source = triangleMap.getEdgeSource(t);
			Triangle target = triangleMap.getEdgeTarget(t);
			if ( source.index != v )
				adjs.add(source.index);
			else if ( target.index != v )
				adjs.add(target.index);
		}
		return adjs;
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

	public Literal getRegion( int v ){
		for ( Triangle t : triangleMap.vertexSet() ){
			if ( t.index == v )
				return t.region;
		}
		return null;
	}

}

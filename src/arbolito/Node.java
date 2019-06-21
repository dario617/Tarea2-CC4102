package arbolito;

import java.util.HashMap;

public class Node {
	/*
	 * params:
	 * 	value: indice del string donde comienza el sufijo. Si value = -1 entonces no es una hoja.
	 * 	depth: profundidad del nodo en el arbol
	 * 	parent: Nodo padre
	 * */
	private int[][] links;
	private Node[] children;
	private int nbchildren;
	private int value;
	private int depth;
	private Node parent;
	
	public Node(Node parent, int depth, int value, int size) {
		this.parent = parent;
		this.depth = depth;
		this.value = value;
		this.links = new int[size][2];
		this.children = new Node[size];
		this.nbchildren = -1;
		initArrays();
	}
	
	private void initArrays() {
		for (int i = 0; i < this.links.length; i++) {
			this.links[i][0] = -1;
			this.links[i][1] = -1;
		}
	}
	
	//crea un nodo raiz
	public Node(int size) {
		this(null, 0, -1, size);
	}
	
	public int getValue() {return this.value;}
	
	public void setValue(int newValue) {this.value = newValue;}
	
	public void setInner() {this.setValue(-1);}
	
	public int getDepth() {return this.depth;}
	public void setDepth(int newDepth) {this.depth = newDepth;}
	
	public void setParent(Node par) {this.parent = par;}
	
	public void addLink(int pos, int index1, int index2, Node n) {
		links[pos][0] = index1;
		links[pos][1] = index2;
		children[pos] = n;
	}
	
	public void removeLink(int pos) {
		links[pos][0] = -1;
		links[pos][1] = -1;
		children[pos] = null;
	}
	
	public int[][] getLinks(){
		return links;
	}
	
	public Node[] getChildren() {
		return children;
	}
	
	public void setNBChildren(int n) {this.nbchildren = n;}
	
	public int getNBChildren() {return this.nbchildren;}
	
	public Node getParent() {return this.parent;}
	public int size() {return links.length;}
	
}

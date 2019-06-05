package arbolito;

import java.util.HashMap;

public class Node {
	/*
	 * params:
	 * 	value: indice del string donde comienza el sufijo. Si value = -1 entonces no es una hoja.
	 * 	depth: profundidad del nodo en el arbol
	 * 	parent: Nodo padre
	 * */
	private HashMap<String, Node> links;
	private int value;
	private int depth;
	private Node parent;
	
	public Node(Node parent, int depth, int value, HashMap<String, Node> links) {
		this.parent = parent;
		this.depth = depth;
		this.value = value;
		this.links = links;
	}
	public Node(Node parent, int depth, int value) {
		this.parent = parent;
		this.depth = depth;
		this.value = value;
		this.links = new HashMap<String, Node>();
	}
	
	//crea un nodo raiz
	public Node() {
		this(null, 0, -1);
	}
	
	public int getValue() {return this.value;}
	
	public void setValue(int newValue) {this.value = newValue;}
	
	public void setInner() {this.setValue(-1);}
	
	public int getDepth() {return this.depth;}
	public void setDepth(int newDepth) {this.depth = newDepth;}
	public HashMap<String, Node> getLinks() {return this.links;}

	public void setLinks(HashMap<String, Node> links) {this.links = links;}
	
	public void addLink(String s, Node n) {links.put(s, n);}
	public void removeLink(String s) {links.remove(s);}
	public Node getParent() {return this.parent;}
	public int size() {return links.size();}
}

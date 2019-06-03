package arbolito;

import java.util.HashMap;

public class Node {
	private HashMap<String, Node> links; 

	public Node() {
		links = new HashMap<String, Node>();
	}

	public HashMap<String, Node> getLinks() {
		return links;
	}

	public void setLinks(HashMap<String, Node> links) {
		this.links = links;
	}
	
	public void addLink(String s, Node n) {
		links.put(s, n);
	}
	
	public int size() {
		return links.size();
	}
}

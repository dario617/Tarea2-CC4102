package arbolito;

import java.util.ArrayList;

public class SuffixTree {
	
	private Node root;
	
	public SuffixTree() {
		root = new Node();
		initialize("ababaa$");
	}
		
	public void initialize(String text) {
		int textLength = text.length();
		ArrayList<Integer> suffixArray = new ArrayList<Integer>();
		ArrayList<Integer> longestCommonPrefixArray = new ArrayList<Integer>();
		
		//TODO: Crear Suffix Array a partir del texto
		suffixArray.add(new Integer(6));
		suffixArray.add(new Integer(5));
		suffixArray.add(new Integer(4));
		suffixArray.add(new Integer(2));
		suffixArray.add(new Integer(0));
		suffixArray.add(new Integer(3));
		suffixArray.add(new Integer(1));
		
		//TODO: Crear LCP a partir del SA
		longestCommonPrefixArray.add(new Integer(0));
		longestCommonPrefixArray.add(new Integer(1));
		longestCommonPrefixArray.add(new Integer(1));
		longestCommonPrefixArray.add(new Integer(3));
		longestCommonPrefixArray.add(new Integer(0));
		longestCommonPrefixArray.add(new Integer(2));
		
		//TODO: Init SuffixTree
		//Se agrega el primer elemento a la raiz del arbol.
		//S.P.G, siempre pasa que el primer nodo solo contiene '$'????????
		Node firstNode = new Node(this.root, this.root.getDepth()+1, suffixArray.get(0));
		root.addLink(text.substring(suffixArray.get(0)),firstNode);
		Node currentNode = firstNode;
		for(int i=1;i<textLength; i++) {			
			//La profundidad a la que debo insertar mi nuevo nodo
			int lcpDepth = (int) longestCommonPrefixArray.get(i-1);
			//La pos donde comienza mi nuevo sufijo
			int stringIndex = (int) suffixArray.get(i);
			//El nuevo sufijo (para hashear)
			String palabraToInsert = text.substring(suffixArray.get(i));
			String newKey = palabraToInsert.substring(lcpDepth);
			//Init nuevo nodo
			Node newNode;			
			while(lcpDepth<currentNode.getParent().getDepth()) {
				currentNode = currentNode.getParent();
			}
			if(lcpDepth == currentNode.getParent().getDepth()) {
				//el padre del cursor es un nodo con la profundidad adecuada
				// <==> El nodo a insertar es hermano del nodo actual.
				currentNode = currentNode.getParent();
				newNode = new Node(currentNode,(textLength-stringIndex),
						stringIndex); 
				currentNode.addLink(newKey, newNode);
				currentNode = newNode;
			} else{ //lcpDepth > parent.depth	
				//generar 2 substrings		
				String llaveOriginal = text.substring(currentNode.getValue()+currentNode.getParent().getDepth());
				String subIzq = llaveOriginal.substring(0,lcpDepth-currentNode.getParent().getDepth());
				String subDer = llaveOriginal.substring(lcpDepth-currentNode.getParent().getDepth());
				//generar una copia del cursor (currentNode), linkear a cursor con substr derecho como llave
				Node currentCopy = new Node(currentNode, currentNode.getDepth(), currentNode.getValue());
				currentNode.addLink(subDer, currentCopy);				
				//rehash cursor con substr izquierdo como llave (crear hash nuevo, remover antiguo)
				currentNode.getParent().addLink(subIzq, currentNode);
				currentNode.getParent().removeLink(llaveOriginal);
				//setear cursor como nodo interno y nueva depth = LCP
				currentNode.setInner();
				currentNode.setDepth(lcpDepth);
				//anexar nodo nuevo a agregar, setear cursor en nuevo nodo.
				newNode = new Node(currentNode,(textLength-stringIndex),
						stringIndex); 				
				currentNode.addLink(newKey, newNode);
				currentNode = newNode;
			}
		}
		System.out.println("init Terminated");
	}
	
	public int count(String query) {
		return 0;
	}
	
	public ArrayList<Integer> locate(String query) {
		return new ArrayList<Integer>();
	}
	
	public ArrayList<String> topKQ(int k, int q){
		return new ArrayList<String>();
	}
}

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
		//int[] suffixArray = new int[textLength];
		//int[] longestCommonPrefixArray = new int[textLength];
		
		//TODO: Crear Suffix Array a partir del texto
		int[]suffixArray = {6,5,4,2,0,3,1}; //hardcoded para "ababaa$"
		//Crear LCP a partir del SA
		int[] longestCommonPrefixArray = this.generateLCP(suffixArray, text, textLength);		//TODO: Init SuffixTree
		//Se agrega el primer elemento a la raiz del arbol.		
		Node firstNode = new Node(this.root, this.root.getDepth()+1, suffixArray[0]);
		root.addLink(text.substring(suffixArray[0]),firstNode);
		Node currentNode = firstNode;
		for(int i=1;i<textLength; i++) {			
			//La profundidad a la que debo insertar mi nuevo nodo
			int lcpDepth = (int) longestCommonPrefixArray[i-1];
			//La pos donde comienza mi nuevo sufijo
			int stringIndex = (int) suffixArray[i];
			//El nuevo sufijo (para hashear)
			String palabraToInsert = text.substring(suffixArray[i]);
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
	
	
	//UTILS
	private int[] generateLCP(int[] aSuffixArray, String aText, int textLength) {				    		    
		    int[] R = new int[textLength];		    
		    int[] L = new int[textLength-1];		   
		    for(int i = 0; i<textLength; i++) {
		    	R[aSuffixArray[i]] = i;
		    	if(i<textLength-1) {
		    		L[i] = -1;
		    	}
		    }		       
		    int h = 0;
		    for(int i = 0; i<textLength; i++) {
		    	if(R[i]>0) {
		    		int k = aSuffixArray[R[i]-1];
		    		while((i+h < textLength) && (k+h < textLength) && (aText.charAt(i+h) == aText.charAt(k+h))) {
		    			h++;
		    		}
		    		L[R[i]-1] = h;
		    		h = Math.max(h-1, 0);
		    	}
		    }
		    return L;		
	}
}

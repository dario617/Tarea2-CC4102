package arbolito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import utils.SuffixArrayNLogN;
import utils.Tools;

import java.util.Objects;
import java.util.Random;
import java.util.Stack;

public class SuffixTree {
	
	private class MyPair implements Comparable<MyPair>{
		private int first;
		private int second;
		
		public MyPair(int first, int second){
			this.first = first;
			this.second = second;
		}

		@Override
		// Compare in reverse
		public int compareTo(MyPair o) {
			int cmp;
			if (first < o.first)
			   cmp = +1;
			else if (first > o.first)
			   cmp = -1;
			else
			   cmp = 0;
			return cmp;
		}
		
	}
	
	private Node root;
	private String text;
	private HashMap<Character, Integer> universe;
	private int qkPos;

	public SuffixTree(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append(text);
		sb.append("\u0006");
		this.text = sb.toString(); 
		universe = Tools.getStringUniverse(this.text);
		root = new Node(universe.size());
		initialize(this.text);
	}

	public void initialize(String text) {
		System.out.println("begin initialize...");
		int textLength = text.length();
		
		// Se genera el Suffix Array
		int[] suffixArray = SuffixArrayNLogN.suffixArray(text);
		System.out.println("did suffix array");
		
		// Se genera el LCP a partir del Suffix Array
		int[] longestCommonPrefixArray = this.generateLCP(suffixArray, text, textLength); // TODO: Init SuffixTree
		System.out.println("did lcp array");
		
		// Se agrega el primer elemento a la raiz del arbol.
		Node firstNode = new Node(this.root, this.root.getDepth() + 1, suffixArray[0], universe.size());
		root.addLink(universe.get(text.charAt(suffixArray[0])), suffixArray[0], textLength - 1, firstNode);
		
		Node currentNode = firstNode;
		for (int i = 1; i < textLength; i++) {
			// La profundidad a la que debo insertar mi nuevo nodo
			int lcpDepth = longestCommonPrefixArray[i - 1];
			// La pos donde comienza mi nuevo sufijo
			int stringIndex = suffixArray[i]; // Indice inicio palabra to insert
			// El nuevo sufijo (para hashear)
			int newKey1 = suffixArray[i] + lcpDepth; // Indice inicio nueva llave to insert
			
			// Init nuevo nodo
			Node newNode;
			while (lcpDepth < currentNode.getParent().getDepth()) {
				currentNode = currentNode.getParent();
			}
			if (lcpDepth == currentNode.getParent().getDepth()) {
				// el padre del cursor es un nodo con la profundidad adecuada
				// <==> El nodo a insertar es hermano del nodo actual.
				// No hay que "cortar" la llave
				currentNode = currentNode.getParent();
				newNode = new Node(currentNode, (textLength - stringIndex), stringIndex, universe.size());
				currentNode.addLink(universe.get(text.charAt(newKey1)), newKey1, textLength - 1, newNode);
				currentNode = newNode;
			} else {
				// lcpDepth > parent.depth
				// generar 2 substrings
				if (currentNode.getValue() == -1) { 
					// Soy nohoja, busco a alguien para saber cual es mi hoja
					
					// Elegimos un nodo no vacio
					Node actNode = currentNode;
					Node[] currentLinks;
					while (actNode.getValue() == -1) {
						currentLinks = actNode.getChildren();
						for (int j = 0; j < currentLinks.length; j++) {
							if(currentLinks[j] != null) {
								actNode = currentLinks[j];
								break;
							}
						}
					}

					// IN CASE OF FIRE: HERE!!! git reset hard
					
					int initVal = actNode.getValue() + currentNode.getParent().getDepth();
					int endVal = actNode.getValue() + currentNode.getDepth() - 1;
					// int subIzq1 = initVal;
					int subIzq2 = initVal + lcpDepth - currentNode.getParent().getDepth() - 1;
					int subDer1 = initVal + lcpDepth - currentNode.getParent().getDepth();
					//int subDer2 = endVal;
					
					
					// Generamos un nuevo nodo interno
					int newKeyLength1 = subIzq2 - initVal + 1;
					Node babyInnerNode = new Node(currentNode.getParent(), 
							 newKeyLength1 + currentNode.getParent().getDepth(), currentNode.getValue(), universe.size());
					babyInnerNode.setInner();
					
					// Este nuevo nodo, almacena la nueva llave a insertar, por lo que la generamos
					newNode = new Node(babyInnerNode, (textLength - stringIndex), stringIndex, universe.size());
					
					//babyInnerNode.addLink(universe.get(text.charAt(initVal)), initVal, subIzq2, newNode);
					babyInnerNode.addLink(universe.get(text.charAt(stringIndex + lcpDepth)), stringIndex + lcpDepth, textLength - 1, newNode);
					
					// Ahora, nuestro nuevo nodo es intermedio entre current y el padre de current
					babyInnerNode.addLink(universe.get(text.charAt(subDer1)), subDer1, endVal, currentNode);
					
					currentNode.getParent().removeLink(universe.get(text.charAt(initVal)));
					currentNode.getParent().addLink(universe.get(text.charAt(initVal)), initVal, subIzq2, babyInnerNode);
					currentNode.setParent(babyInnerNode);
					currentNode = newNode;
				} else {
					// This is a HOJA
					int initVal = currentNode.getValue() + currentNode.getParent().getDepth(); // subIzq
					int endVal = textLength - 1; // subDer2
					int subIzq2 = currentNode.getValue() + lcpDepth - 1;
					int subDer1 = subIzq2 + 1; // subDer
					
					// generar una copia del cursor (currentNode), linkear a cursor con substr
					// derecho como llave
					Node currentCopy = new Node(currentNode, currentNode.getDepth(), currentNode.getValue(), universe.size());
					currentNode.addLink(universe.get(text.charAt(subDer1)), subDer1, endVal, currentCopy);
					// rehash cursor con substr izquierdo como llave (crear hash nuevo, remover
					// antiguo)
					currentNode.getParent().removeLink(universe.get(text.charAt(initVal)));
					currentNode.getParent().addLink(universe.get(text.charAt(initVal)), initVal, subIzq2, currentNode);
					
					// setear cursor como nodo interno y nueva depth = LCP
					currentNode.setInner();
					currentNode.setDepth(lcpDepth);
					// anexar nodo nuevo a agregar, setear cursor en nuevo nodo.
					newNode = new Node(currentNode, (textLength - stringIndex), stringIndex, universe.size());
					currentNode.addLink( universe.get(text.charAt(newKey1)), newKey1, textLength  - 1,newNode);
					currentNode = newNode;
				}
			}
		}
		initCount();
		System.out.println("init Terminated");
	}
	
	private void initCount() {
		initCountRec(this.root);
	}
	
	private int initCountRec(Node node) {
		int n = 0;
		// Si soy hoja
		if(node.getValue() >= 0) {
			node.setNBChildren(1);
			return 1;
		}
		// Si no soy hoja
		for (int i = 0; i < node.getChildren().length; i++) {
			if(!Objects.isNull(node.getChildren()[i]))
				n += initCountRec(node.getChildren()[i]);
		}
		node.setNBChildren(n);
		return n;
	}

	public int count(String query) {
		Node currentNode = this.root;
		int queryLen = query.length();
		int offset = 0;
		while (queryLen > offset) {
			// Chequeo de la primera letra de lo restante de la consulta
			int[][] currentLinks = currentNode.getLinks();
			// Si la letra buscada no esta en el universo, no existen coincidencias
			if(!universe.containsKey(query.charAt(offset))) {
				return 0;
			}
			int pos = universe.get(query.charAt(offset));
			if(currentLinks[pos][0] == -1) {
				// No hay llave coincidente
				return 0;
			}
			else {
				int init = currentLinks[pos][0];
				int end = currentLinks[pos][1] + 1;
				int keyLen = end - init;
				String key = text.substring(init, end);
				String subquery = query.substring(offset);
				if (keyLen < queryLen - offset) {
					if (subquery.startsWith(key)) {
						currentNode = currentNode.getChildren()[pos];
						offset += keyLen;
						continue;
					} else {
						return 0;
					}
				} // keyLen >= queryLen
				else {
					if (key.startsWith(subquery)) {
						offset += keyLen;
						currentNode = currentNode.getChildren()[pos];
					} else {
						return 0;
					}
				}
			}
		}
		return currentNode.getNBChildren();
	}

	public ArrayList<Integer> locate(String query) {
		
		Node currentNode = this.root;
		int queryLen = query.length();
		int offset = 0;
		ArrayList<Integer> ans = new ArrayList<Integer>();
		while (queryLen > offset) {
			// Chequeo de la primera letra de lo restante de la consulta
			int[][] currentLinks = currentNode.getLinks();
			// Si la letra buscada no esta en el universo, no existen coincidencias
			if(!universe.containsKey(query.charAt(offset))) {
				return ans;
			}
			int pos = universe.get(query.charAt(offset));
			if(currentLinks[pos][0] == -1) {
				// No hay llave coincidente
				return ans;
			}
			else {
				int init = currentLinks[pos][0];
				int end = currentLinks[pos][1] + 1;
				int keyLen = end - init;
				String key = text.substring(init, end);
				String subquery = query.substring(offset);
				if (keyLen < queryLen - offset) {
					if (subquery.startsWith(key)) {
						currentNode = currentNode.getChildren()[pos];
						offset += keyLen;
						continue;
					} else {
						return ans;
					}
				} // keyLen >= queryLen
				else {
					if (key.startsWith(subquery)) {
						offset += keyLen;
						currentNode = currentNode.getChildren()[pos];
					} else {
						return ans;
					}
				}
			}
		}
		// El nodo actual era una hoja
		if (currentNode.getValue() >= 0) {
			ans.add(currentNode.getValue());
			return ans;
		}
		Stack<Node> stack = new Stack<Node>();
		// Metemos todos lo nodos de currentNode al stack
		for (int i = 0; i < currentNode.getChildren().length; i++) {
			if(!Objects.isNull(currentNode.getChildren()[i])) {
				stack.push(currentNode.getChildren()[i]);
			}
		}
		while (!stack.isEmpty()) {
			currentNode = stack.pop();
			if (currentNode.getValue() >= 0) {
				ans.add(currentNode.getValue());
			} 
			else {
				for (int i = 0; i < currentNode.getChildren().length; i++) {
					if(!Objects.isNull(currentNode.getChildren()[i])) {
						stack.push(currentNode.getChildren()[i]);
					}
				}
			}
		}
		return ans;
	}

	public ArrayList<String> topKQ(int k, int q) {
		
		int[][] indices = new int[text.length()][2];
		int[] count = new int[text.length()];
		qkPos = 0;
		
		// FAILSAFE
		if(q > text.length()) {
			return null;
		}
		
		topKQHelper(root, q, indices, count);
		
		// Get top K
		MyPair[] myPairs = new MyPair[qkPos];
		for (int i = 0; i < myPairs.length; i++) {
			myPairs[i] = new MyPair(count[i], i);
		}
		Arrays.sort(myPairs);
		
		int init;
		int end;
		ArrayList<String> ans = new ArrayList<String>();
		// Give me K, while there are enough values
		for (int i = 0; i < k && i < qkPos; i++) {
			init = indices[myPairs[i].second][0];
			end = indices[myPairs[i].second][1];
			ans.add(text.substring(init,end));
		}
		
		return ans;
	}
	
	private void topKQHelper(Node n, int q, int[][] ans, int[] count) {
		// If current branch is smaller than the requested Q
		if(n.getDepth() < q) {
			// Do a recursive step on children
			Node[] children = n.getChildren();
			for (int i = 0; i < children.length; i++) {
				if(children[i] != null) {
					topKQHelper(children[i], q, ans, count);					
				}
			}
		// Else we can add up the results
		}else {
			// Find the index of this string
			
			// Get a child with a valid index
			Node current = n;
			Node[] currentChildren;
			while(current.getValue() == -1) {
				currentChildren = current.getChildren();
				for (int i = 0; i < currentChildren.length; i++) {
					if(currentChildren[i] != null) {
						current = currentChildren[i];
						break;
					}
				}
			}
			
			int stringInit = current.getValue();
			int stringEnd = stringInit + q; // Inclusive value
			
			ans[qkPos][0] = stringInit;
			ans[qkPos][1] = stringEnd;
			
			count[qkPos] = n.getNBChildren();
			
			qkPos++;
		}
		return;
	}
	
	public boolean checkSuffixTree() {				
		Node startingNode = this.root;
		int[] originalSA = SuffixArrayNLogN.suffixArray(this.text);
		Stack<Node> valueStack = new Stack<Node>();
		ArrayList<Integer> SA = new ArrayList<Integer>();
		valueStack.push(startingNode);
		while(!valueStack.isEmpty()) {
			Node currentNode = valueStack.pop();
			if(currentNode.getValue() == -1) {
				//recorrer hijos en orden Lex y pushearlos
				for(Node n : currentNode.getChildren()) {
					if(n!=null) valueStack.push(n);
				}
			} else {
				SA.add(new Integer(currentNode.getValue()));
			}
		}
		System.out.println("Verificando SA reconstruido:");
		int saLen = originalSA.length;
		for(int i = 0; i<saLen; i++) {			
			if((int) SA.get(i) != originalSA[saLen - 1 - i]) {
				System.out.println("failed");
				return false;
			}
		}
		System.out.println("OK!");
		return true;
	}
	
	public boolean checkSuffixTreeOnLinks() {
		int[] originalSA = SuffixArrayNLogN.suffixArray(this.text);
		StringBuilder sb = new StringBuilder();
		Node current;
		int[][] currentLinks;
		Node[] childs;
		int index, position;
		for (int i = 0; i < originalSA.length; i++) {
			String currentQuery = this.text.substring(originalSA[i]);
			// reset buffer
			sb.setLength(0);
			
			// Look into the root
			current = this.root;
			index = 0;
			while(current.getValue() == -1) {
				currentLinks = current.getLinks();
				childs = current.getChildren();
				position = universe.get(currentQuery.charAt(index));
				// The element should be here
				if(currentLinks[position][0] != -1) {
					
					// Append the elements of the link
					sb.append(this.text.substring(currentLinks[position][0], currentLinks[position][1] + 1));
					
					// Get the corresponding node
					current = childs[position];
					// Update the index on the character to look for
					index = current.getDepth();
				}else {
					System.out.println("FAIL on lookup :c");
					System.out.println("Asking for char: "+currentQuery.charAt(index)+", on road: "+sb.toString());
					return false;
				}
			}
			
			// Compare the elements
			String recovered = sb.toString();
			if(recovered.compareTo(currentQuery) != 0) {
				System.out.println("FAIL on comparison");
				System.out.println("Recovered: "+recovered+", query: "+currentQuery);
				return false;
			}
		}
		
		System.out.println("OK! :)");
		return true;
	}
	
	// UTILS
	private int[] generateLCP(int[] aSuffixArray, String aText, int textLength) {
		int[] R = new int[textLength];
		int[] L = new int[textLength - 1];
		for (int i = 0; i < textLength; i++) {
			R[aSuffixArray[i]] = i;
			if (i < textLength - 1) {
				L[i] = -1;
			}
		}
		int h = 0;
		for (int i = 0; i < textLength; i++) {
			if (R[i] > 0) {
				int k = aSuffixArray[R[i] - 1];
				while ((i + h < textLength) && (k + h < textLength) && (aText.charAt(i + h) == aText.charAt(k + h))) {
					h++;
				}
				L[R[i] - 1] = h;
				h = Math.max(h - 1, 0);
			}
		}
		return L;
	}
}

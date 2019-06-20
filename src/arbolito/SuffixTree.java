package arbolito;

import java.util.ArrayList;
import java.util.HashMap;

import utils.SuffixArrayNLogN;
import utils.Tools;

import java.util.Objects;
import java.util.Random;
import java.util.Stack;

public class SuffixTree {

	private Node root;
	private String text;
	private HashMap<Character, Integer> universe;

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
						currentLinks = actNode.getLinks();
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
					int newKeyLength1 = subIzq2 - initVal;
					Node babyInnerNode = new Node(currentNode.getParent(), 
							 newKeyLength1 + currentNode.getParent().getDepth(), currentNode.getValue(), universe.size());
					babyInnerNode.setInner();
					
					// Este nuevo nodo, almacena la nueva llave a insertar, por lo que la generamos
					newNode = new Node(babyInnerNode, (textLength - stringIndex), stringIndex, universe.size());
					
					babyInnerNode.addLink(universe.get(text.charAt(initVal)), initVal, subIzq2, newNode);
					
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
		System.out.println("init Terminated");
	}
	
	public int count(String query) {
//		Node currentNode = this.root;
//		int queryLen = query.length();
//		int offset = 0;
//		while (queryLen > offset) {
//			String currentKey = null;
//			// Chequeo de la primera letra de la llave
//			for (String key : currentNode.getLinks().keySet()) {
//				if (key.charAt(0) == query.charAt(offset)) {
//					currentKey = key;
//					break;
//				}
//			}
//			// No hay matching key
//			if (Objects.isNull(currentKey)) {
//				return 0;
//			} else {
//				int keyLen = currentKey.length();
//				if (keyLen < queryLen) {
//					if (query.substring(offset).startsWith(currentKey)) {
//						currentNode = currentNode.getLinks().get(currentKey);
//						offset += keyLen;
//						continue;
//					} else {
//						return 0;
//					}
//				} // keyLen > queryLen
//				else {
//					if (currentKey.startsWith(query.substring(offset))) {
//						offset += keyLen;
//						currentNode = currentNode.getLinks().get(currentKey);
//					} else {
//						return 0;
//					}
//				}
//			}
//		}
//		int counter = 0;
//		// El nodo actual era una hoja
//		if (currentNode.getValue() >= 0) {
//			counter += 1;
//			return counter;
//		}
//		Stack<Node> stack = new Stack<Node>();
//		for (Node nodo : currentNode.getLinks().values()) {
//			stack.push(nodo);
//		}
//		while (!stack.isEmpty()) {
//			currentNode = stack.pop();
//			if (currentNode.getValue() >= 0) {
//				counter += 1;
//			} else {
//				for (Node nodo : currentNode.getLinks().values()) {
//					stack.push(nodo);
//				}
//			}
//		}
		//return counter;
		return 0;
	}

	public ArrayList<Integer> locate(String query) {
		return new ArrayList<Integer>();
	}

	public ArrayList<String> topKQ(int k, int q) {
		return new ArrayList<String>();
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

package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Tools {
	
	public ArrayList<String> suffixArray(String text){
		
		// Get the universe of strings
		char[] charArray = text.toCharArray();
		Set<Character> setOfChars = new HashSet<Character>();
		for (int i = 0; i < charArray.length; i++) {
			setOfChars.add(charArray[i]);
		}
		charArray = null; // GB come here!
		Iterator<Character> it = setOfChars.iterator();
		ArrayList<Character> al = new ArrayList<Character>();
		while(it.hasNext()) {
			al.add(it.next());
		}
		Collections.sort(al);
		HashMap<Character, Integer> charSet = new HashMap<Character, Integer>();
		for (int i = 0; i < al.size(); i++) {
			charSet.put(al.get(i), i);
		}
		
		// Create group of suffixes
		int size = text.length()/3 + 1;
		int[] S0 = new int[size];
		int[] S1 = new int[size];
		int[] S2 = new int[size];
		for (int i = 0; i < text.length(); i = i + 3) {
			S0[i] = i;
			S1[i] = i+1;
			S2[i] = i+2;
		}
		
		// Construct S12: 3-grams of S1 and S2
		@SuppressWarnings("unchecked")
		int[] S12 = new int[size*2];
		for (int i = 0; i < S1.length; i++) {
			S12[i] = S1[i];
		}
		for (int i = 0; i < S2.length; i++) {
			S12[i + S1.length] = S2[i];
		}
		// Do radix sort and recover the rank (sorted positions)
		ArrayList<Integer> ranks = radixSort(S12, charSet, text);
		
		// 
		
		return null;
	}
	
	public static ArrayList<Integer> radixSort(int[] S12, HashMap<Character, Integer> charSet, String text){
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		// For each caracter 
		// Initialize bucket
		int[] bucketIter = countingSort();

		// Para comparar pasar los $ (la posicion del caracter final)
		
		return results;
	}
	
}

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
		ArrayList<Integer> S0 = new ArrayList<>();
		ArrayList<Integer> S1 = new ArrayList<>();
		ArrayList<Integer> S2 = new ArrayList<>();
		for (int i = 0; i < text.length(); i = i + 3) {
			S0.add(i);
			S1.add(i+1);
			S2.add(i+2);
		}
		
		// Construct S12: 3-grams of S1 and S2
		@SuppressWarnings("unchecked")
		ArrayList<Integer> S12 = (ArrayList<Integer>) S1.clone();
		S12.addAll(S2);
		// Do radix sort and recover the rank (sorted positions)
		ArrayList<Integer> ranks = radixSort(S12, charSet, text);
		
		// 
		
		return null;
	}
	
	public static ArrayList<Integer> radixSort(ArrayList<Integer> strings, HashMap<Character, Integer> charSet, String text){
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		// For each caracter 
		for(int i=0; i < charSet.size(); i++) {
			// Initialize bucket
			
		}
		
		return results;
	}
	
}

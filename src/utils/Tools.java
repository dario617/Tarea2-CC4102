package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	
	public static int[] countingSort(int[] input, HashMap<Character, Integer> universe, String text) {
		int n = input.length;
		int[] b = new int[n];
		Integer[] integerInput = new Integer[n];
		HashMap<Character, Integer> c = new HashMap<Character, Integer>();
		Object[] alphabet = universe.keySet().toArray();
		for (Character k : universe.keySet() ) {
			// we initialize all values in c to 0
			c.put(k, 0);
		}
		for(int j = 0; j < n; j++) {
			c.put(text.charAt(input[j]), c.get(text.charAt(input[j])) + 1);
			integerInput[j] = new Integer(input[j]);
		}
		List<Integer> inputAsList = Arrays.asList(integerInput);
		for(int i = 1; i < alphabet.length; i++) {
			c.put((Character) alphabet[i], c.get(alphabet[i]) + c.get(alphabet[i-1]));
		}
		for(int j = n-1; j >= 0; j--) {
			b[c.get(text.charAt(input[j])) - 1] = inputAsList.indexOf(input[j]);
			c.put(text.charAt(input[j]), c.get(text.charAt(input[j])) - 1);
		}
		return b;
	}
	public static void main(String[] args) {
		System.out.println("Veamos si funca");
		String text = "banbalbbaala";
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
		System.out.println(charSet);
		int [] input1 = {0,3,6,9};
		int [] input2 = {1,4,7,10};
		int [] input3 = {2,5,8,11};
		int[] res1 = countingSort(input1, charSet, text);
		int[] res2 = countingSort(input2, charSet, text);
		int[] res3 = countingSort(input3, charSet, text);
		System.out.println(Arrays.toString(res1));//3012
		System.out.println(Arrays.toString(res2));//0123
		System.out.println(Arrays.toString(res3));//2310
	}
}

package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class Tools {
	
	private static HashMap<Character,Integer> getStringUniverse(String text){
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
		return charSet;
	}
	
	/**
	 * Create suffix array in linear time
	 * Referece: https://gist.github.com/petar-dambovaliev/651b44b42aba8e9be2b26252a5ef27e2
	 * @param text
	 * @return sorted suffix array
	 */
	public static int[] suffixArray(String text){
		
		// Get the universe of strings
		HashMap<Character, Integer> charSet = getStringUniverse(text);
		
		// Create group of suffixes
		// This way avoids clutter coding ...
		int textSize = text.length();
		int size = 0, index;
		// S0 S1 S2
		ArrayList<int[]> arrays  = new ArrayList<int[]>(3); 
		for (int i = 0; i < 3; i++) {
			size = (textSize - i)/3;
			// Avoid using $ as suffix
			if((textSize - i)%3 != 0) {
				size = size + 1;
			}
			int[] arr = new int[size];
			for (int j = 0; j < arr.length; j++) {
				index = j*3 + i;
				arr[j] = index;
				if(index >= textSize) {
					arr[j] = textSize - 1;	
				}
			}
			arrays.add(i, arr);
		}
		
		// Construct S12: 3-grams of S1 and S2
		int[] S1 = arrays.get(1), S2 = arrays.get(2);
		int[] S12 = new int[S1.length + S2.length];
		for (int i = 0; i < S1.length; i++) {
			S12[i] = S1[i];
		}
		for (int i = 0; i < S2.length; i++) {
			S12[i + S1.length] = S2[i];
		}
		
		// Do radix sort and recover the rank (sorted positions)
		int[] S12_ranks = rankingRadixSort(S12, charSet, text);
		
		// Sort S0 in linear time
		// Using a mini radix sort on S1 ranks
		
		// S1 ranks
		int[] S1_ranks = rankingRadixSort(S1, charSet, text);
		// Shuffle as in a radix iteration
		int[] S0 = arrays.get(0), tmp = new int[S0.length], tmp2, S0_ranks = new int[S0.length];
		int addOne = 0;
		// In case of |S0| > |S1| because of $
		if(S0.length > S1.length) {
			addOne = 1;
			tmp[0] = text.length() - 1;
		}
		for (int i = 0; i < S0.length - addOne; i++) {
			tmp[i + addOne] = S0[S1_ranks[i]];
		}
		// Get final positions
		tmp2 = countingSort(tmp, charSet, text);
		if(addOne > 0) {
			S0_ranks[S0.length - 1] = 0;
		}
		for (int i = 0 + addOne; i < tmp2.length; i++) {
			S0_ranks[S1_ranks[tmp2[i] - addOne]] = i;
		}
		tmp = null; tmp2 = null; // GC please clean up!!
		
		// Create lists with sorted indices
		int[] S0_sorted_indexes = new int[S0.length];
		int[] S12_sorted_indexes = new int[S12.length];
		for (int i = 0; i < S0_sorted_indexes.length; i++) {
			S0_sorted_indexes[S0_ranks[i]] = S0[i];
		}
		for (int i = 0; i < S12_sorted_indexes.length; i++) {
			S12_sorted_indexes[S12_ranks[i]] = S12[i];
		}
		
		int[] ans = new int[S1.length + S2.length + S0.length];
		
		// Merge sort pairs elements
		int i_S12 = 0, i_S0 = 0, k= 0;
		while(i_S0 < S0.length && i_S12 <= S12.length) {
			if(text.charAt(S0_sorted_indexes[i_S0]) <= text.charAt(S12_sorted_indexes[i_S12])){
				ans[k] = S0_sorted_indexes[i_S0];
				i_S0++;
			}else {
				ans[k] = S12_sorted_indexes[i_S12];
				i_S12++;
			}
			k++;
		}		  
		while( i_S0 < S0.length) {
			ans[k] = S0_sorted_indexes[i_S0];
			i_S0++;
			k++;
		}
		while( i_S12 < S12.length) {
			ans[k] = S12_sorted_indexes[i_S12];
			i_S12++;
			k++;
		}
		
		return ans;
	}
	
	public static int[] rankingRadixSort(int[] S12, HashMap<Character, Integer> charSet, String text){
		int[] input = new int[S12.length];
		int textLength = text.length();
		// Radix on first set of characters from
		// right to left
		for (int i = 0; i < input.length; i++) {
			input[i] = S12[i] + 2;
			// Manage overflows
			if(input[i] >= textLength) {
				input[i] = textLength - 1;
			}
		}
		// Get first sorting transformation
		int[] iterOne = countingSort(input, charSet, text);
		
		// Radix on second set of characters from
		// right to left after using the transformation for position
		for (int i = 0; i < input.length; i++) {
			input[i] = S12[iterOne[i]] + 1;
			// Manage overflows
			if(input[i] >= textLength) {
				input[i] = textLength - 1;
			}
		}
		// Get second sorting transformation
		int[] iterTwo = countingSort(input, charSet, text);
		
		// Radix on third (leftmost) set of characters from
		// right to left after using the transformations for position
		for (int i = 0; i < input.length; i++) {
			input[i] = S12[iterOne[iterTwo[i]]];
			// NB: No overflow to manage
		}
		// Get final sorting transformation and ranks
		int[] finalIter = countingSort(input, charSet, text);
		
		// Construct ranks (sorted order for original positions)
		// Recycle array
		for (int i = 0; i < input.length; i++) {
			input[iterOne[iterTwo[finalIter[i]]]] = i;
		}
		
		// The output is the final position for the original elements
		return input;
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
			alphabet[universe.get(k)] = k;
		}
		
		for(int j = 0; j < n; j++) {
			c.put(text.charAt(input[j]), c.get(text.charAt(input[j])) + 1);
			integerInput[j] = new Integer(input[j]);
		}
		List<Integer> inputAsList = Arrays.asList(integerInput);
		for(int i = 1; i < alphabet.length; i++) {
			c.put((Character) alphabet[i], c.get(alphabet[i]) + c.get(alphabet[i-1]));
		}
		int index;
		for(int j = n-1; j >= 0; j--) {
			index = inputAsList.indexOf(input[j]);
			b[c.get(text.charAt(input[j])) - 1] = index;
			inputAsList.set(index, -1);
			c.put(text.charAt(input[j]), c.get(text.charAt(input[j])) - 1);
			
		}
		return b;
	}
	
	public static void main(String[] args) {
		System.out.println("Veamos si funca");
		String text = "eating$";
		// Get the universe of strings
		HashMap<Character, Integer> charSet = getStringUniverse(text);
		
		System.out.println(charSet);

		int[] res = suffixArray(text);
		System.out.println(text);
		System.out.println(Arrays.toString(res));
		for (int i = 0; i < res.length; i++) {
			System.out.println(text.substring(res[i]));
		}
	}
}

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
			System.out.printf("Size: %d \n", size);
			int[] arr = new int[size];
			for (int j = 0; j < arr.length; j++) {
				index = j*3 + i;
				arr[j] = index;
				if(index >= textSize) {
					arr[j] = textSize - 1;	
				}				
			}
			System.out.println(Arrays.toString(arr));
			arrays.add(i, arr);
		}
		
		// Construct S12: 3-grams of S1 and S2
		int[] S0 = arrays.get(0), S1 = arrays.get(1), S2 = arrays.get(2);
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
		// Using a mini radix sort on S1 ranks.

		// Shuffle as in a radix iteration
		int[] tmp = new int[S0.length], tmp2, S0_ranks = new int[S0.length];
		int addOne = 0;
		
		// S1 ranks; if |S0| > |S1| add the last dummy character $$$
		int[] S1_ranks;
		if(S0.length > S1.length) {
			addOne = 1;
			S1_ranks = new int[S0.length];
			for (int i = 0; i < S1.length; i++) {
				tmp[i] = S1[i];
			}
			tmp[S1.length] = textSize - 1;
		}else {
			S1_ranks = new int[S1.length];
			for (int i = 0; i < S1.length; i++) {
				tmp[i] = S1[i];
			}
		}
		S1_ranks = rankingRadixSort(tmp, charSet, text);
		
		for (int i = 0; i < S0.length; i++) {
			tmp[i] = S0[S1_ranks[i]];
		}
		// Get final positions
		tmp2 = countingSort(tmp, charSet, text);
		for (int i = 0; i < tmp2.length; i++) {
			S0_ranks[S1_ranks[tmp2[i]]] = i;
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
		
		// Preparing for merge sort:
		// Create pairs for S0 and S1 using the ranking of 
		// the next character
		// NOTE: Ranks can only be read from S12 to make 
		// consistent comparisons
		HashMap<Integer, Integer> pairs = new HashMap<Integer, Integer>();
		// Put pairs from S0
		for (int i = 0; i < S0.length - addOne; i++) {
			// When asking for pos that belongs to S0, note: S0[i] = S12[i] -1
			pairs.put(S12[i] - 1, S12_ranks[i]);
		}
		// Form pairs for S1
		for (int i = 0; i < S1.length; i++) {
			// When asking for pos, give the next char ranking
			// hence i + S1.length
			// NB: if we go OutOfBounds assign the rank of $
			index = i+S1.length >= S12.length ? S1.length : i+S1.length;  
			pairs.put(S12[i], S12_ranks[index]);
		}
		// Create triples as Strings
		HashMap<Integer, String> triples = new HashMap<Integer, String>();
		// Put triples by adding the ranking of the next next value
		// Do it for S0
		for (int i = 0; i < S0.length - addOne; i++) {
			index = i+S1.length >= S12.length ? S1.length : i+S1.length;
			triples.put(S12[i] - 1, text.substring(S12[i],S12[i]+1)+S12_ranks[index]);
		}
		// Do it for S2
		for (int i = S1.length; i < S12.length; i++) {
			index = S12[i]+1 >= textSize ? textSize - 1 : S12[i]+1;
			triples.put(S12[i], text.substring(index,index+1) + S12_ranks[i-S1.length+1]);
		}
		
		// Put dummy for OutOfBounds value
		
		int[] ans = new int[S1.length + S2.length + S0.length];
		
		// Merge sort pairs elements
		int i_S12 = 0, i_S0 = 0, k= 0; 
		int leftPair_info, rightPair_info;
		while(i_S0 < S0.length && i_S12 < S12.length) {			
			if( (text.charAt(S0_sorted_indexes[i_S0])) < text.charAt(S12_sorted_indexes[i_S12])){
				ans[k] = S0_sorted_indexes[i_S0];
				i_S0++;
			}else if((text.charAt(S0_sorted_indexes[i_S0])) > text.charAt(S12_sorted_indexes[i_S12])){
				ans[k] = S12_sorted_indexes[i_S12];
				i_S12++;
			}else {
				// Start by checking if we should compare pairs or triples
				leftPair_info = pairs.getOrDefault(S0_sorted_indexes[i_S0], -1);
				rightPair_info = pairs.getOrDefault(S12_sorted_indexes[i_S12], -1); 
				if(leftPair_info == -1 || rightPair_info == -1) {
					// Do triples comparison
					if(triples.get(S0_sorted_indexes[i_S0]).compareTo(triples.get(S12_sorted_indexes[i_S12])) <= 0) {
						ans[k] = S0_sorted_indexes[i_S0];
						i_S0++;
					}else {
						ans[k] = S12_sorted_indexes[i_S12];
						i_S12++;
					}
				}else {
					if(leftPair_info <= rightPair_info) {
						ans[k] = S0_sorted_indexes[i_S0];
						i_S0++;
					}else {
						ans[k] = S12_sorted_indexes[i_S12];
						i_S12++;		
					}
				}
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
		System.out.println(Arrays.toString(S0_sorted_indexes));
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
//		String text = "GTCTCTAAAAT$";
		String text = "aaaaa$";
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

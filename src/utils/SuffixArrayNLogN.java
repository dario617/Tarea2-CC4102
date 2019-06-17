package utils;

// source https://leetcode.com/problems/longest-palindromic-substring/discuss/3209/onlogn-suffix-array-solution-clear-explanation
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

public class SuffixArrayNLogN {
	
	// sort suffixes of S in O(n*log(n))
	public static int[] suffixArray(CharSequence S) {
		int n = S.length();
		Integer[] order = new Integer[n];
		for (int i = 0; i < n; i++)
			order[i] = n - 1 - i;

		// stable sort of characters
		Arrays.sort(order, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Character.compare(S.charAt(o1), S.charAt(o2));
			}
		});

		int[] sa = new int[n];
		int[] classes = new int[n];
		for (int i = 0; i < n; i++) {
			sa[i] = order[i];
			classes[i] = S.charAt(i);
		}
		// sa[i] - suffix on i'th position after sorting by first len characters
		// classes[i] - equivalence class of the i'th suffix after sorting by
		// first len characters

		for (int len = 1; len < n; len <<= 1) {
			int[] c = classes.clone();
			for (int i = 0; i < n; i++) {
				// condition sa[i - 1] + len < n simulates 0-symbol at the end
				// of the string
				// a separate class is created for each suffix followed by
				// simulated 0-symbol
				classes[sa[i]] = i > 0 && c[sa[i - 1]] == c[sa[i]] && sa[i - 1] + len < n
						&& c[sa[i - 1] + (len >> 1)] == c[sa[i] + (len >> 1)] ? classes[sa[i - 1]] : i;
			}
			// Suffixes are already sorted by first len characters
			// Now sort suffixes by first len * 2 characters
			int[] cnt = new int[n];
			for (int i = 0; i < n; i++)
				cnt[i] = i;
			int[] s = sa.clone();
			for (int i = 0; i < n; i++) {
				// s[i] - order of suffixes sorted by first len characters
				// (s[i] - len) - order of suffixes sorted only by second len
				// characters
				int s1 = s[i] - len;
				// sort only suffixes of length > len, others are already sorted
				if (s1 >= 0)
					sa[cnt[classes[s1]]++] = s1;
			}
		}
		return sa;
	}
	
	public static void main(String[] args) throws IOException {
		String sir = "";
		try {
			sir = new String(Files.readAllBytes(Paths.get("/home/scifuent/Documents/Ramos/Logaritmos/Tarea2-CC4102/utils/dna.50MB_clean_2^23")));
			sir = sir + "\u0006"; 
		} catch (IOException e) {
			e.printStackTrace();
		}
		long ini = System.nanoTime();
		int[] res = suffixArray(sir);
		long fin = System.nanoTime();
		System.out.printf("Terminé en: %d", fin-ini);
		

	}

}

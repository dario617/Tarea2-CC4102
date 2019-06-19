package experiments;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.NextAction;


import arbolito.SuffixTree;

public class Experiments {
	
	private static final String filesFolder = "./";
	private static Logger log;
	private static final char bottom = '$';
	private static ArrayList<String> randomStrings;
	private static final int randomCharLength = 5;
	
	public static String getRandomPattern(int length) {
		return getRandomPattern(length, false);
	}
	
	public static String getRandomPattern(int length, boolean dna) {
		String universe= dna ? "ACTG" : "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * universe.length());
            sb.append(universe.charAt(index));
        }
        return sb.toString();
	}

	public static void doEnglish(File f) {
		try {
			// Recover text
			FileReader fr = new FileReader(f);
			StringBuilder sb = new StringBuilder();
			int index;
			while((index = fr.read()) != -1) {
				sb.append((char)index);	
			}
			// Add bottom element
			sb.append(bottom);
			
			String text = sb.toString();
						
			// Create suffix tree
			log.startTest("Initialize suffix tree");
			SuffixTree suffixtree = new SuffixTree(text);
			// TODO: get size of suffix tree
			log.logInfo("Initialize suffix tree", "Total size: "); //+st.getSize());
			log.stopTest("Initialize suffix tree");
			
			// Generate patterns
			int n = text.length()/10;
			String[] patterns = text.split("\\s", n);
			if(randomStrings.size() < n) {
				for (int i = randomStrings.size(); i < n; i++) {
					randomStrings.add(getRandomPattern(randomCharLength));
				}
			}
			
			// Do queries for count
			log.startTest("Positive Query COUNT");
			for (int i = 0; i < patterns.length; i++) {
				suffixtree.count(patterns[i]);
			}
			log.stopTest("Positive Query COUNT");
			
			// Do "miss search" queries
			log.startTest("Negative Query COUNT");
			for (int i = 0; i < randomStrings.size(); i++) {
				suffixtree.count(randomStrings.get(i));
			}
			log.stopTest("Negative Query COUNT");
			
			// Do queries for locate
			log.startTest("Positive Query LOCATE");
			for (int i = 0; i < patterns.length; i++) {
				suffixtree.locate(patterns[i]);
			}
			log.stopTest("Positive Query LOCATE");
			
			// Do "miss search" queries
			log.startTest("Negative Query LOCATE");
			for (int i = 0; i < randomStrings.size(); i++) {
				suffixtree.locate(randomStrings.get(i));
			}
			log.stopTest("Negative Query LOCATE");
			
			int[] k = {3,5,10};
			int[] q = {4,5,6,7};
			
			log.startTest("Top-k-q queries");
			for (int i = 0; i < q.length; i++) {
				for (int j = 0; j < k.length; j++) {
					suffixtree.topKQ(k[j], q[i]);
				}
			}
			log.stopTest("Top-k-q queries");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void doDNA(File f) {
		try {
			// Recover text
			FileReader fr = new FileReader(f);
			StringBuilder sb = new StringBuilder();
			int index;
			while((index = fr.read()) != -1) {
				sb.append((char)index);	
			}
			// Add bottom element
			sb.append(bottom);
			
			String text = sb.toString();
						
			// Create suffix tree
			log.startTest("Initialize suffix tree");
			SuffixTree suffixtree = new SuffixTree(text);
			// TODO: get size of suffix tree
			log.logInfo("Initialize suffix tree", "Total size: "); //+st.getSize());
			log.stopTest("Initialize suffix tree");
			
			// Generate patterns
			int n = text.length()/10;
			int count = 0, left, right;
			String[] patterns = new String[n];
			Random rnd = new Random();
			// n patterns, of variable size
			while(count < n) {
				right = (int)Math.pow(2, rnd.nextInt(5)+1);
				left = rnd.nextInt(text.length() - 32) + 1;
				patterns[count] = text.substring(left,left + right);
				count++;
			}
			
			if(randomStrings.size() < n) {
				for (int i = randomStrings.size(); i < n; i++) {
					randomStrings.add(getRandomPattern(randomCharLength,true));
				}
			}
			
			// Do queries for count
			log.startTest("Positive Query COUNT");
			for (int i = 0; i < patterns.length; i++) {
				suffixtree.count(patterns[i]);
			}
			log.stopTest("Positive Query COUNT");
			
			// Do "miss search" queries
			log.startTest("Negative Query COUNT");
			for (int i = 0; i < randomStrings.size(); i++) {
				suffixtree.count(randomStrings.get(i));
			}
			log.stopTest("Negative Query COUNT");
			
			// Do queries for locate
			log.startTest("Positive Query LOCATE");
			for (int i = 0; i < patterns.length; i++) {
				suffixtree.locate(patterns[i]);
			}
			log.stopTest("Positive Query LOCATE");
			
			// Do "miss search" queries
			log.startTest("Negative Query LOCATE");
			for (int i = 0; i < randomStrings.size(); i++) {
				suffixtree.locate(randomStrings.get(i));
			}
			log.stopTest("Negative Query LOCATE");
			
			int[] k = {3,5,10};
			int[] q = {4,8,16,32};
			
			log.startTest("Top-k-q queries");
			for (int i = 0; i < q.length; i++) {
				for (int j = 0; j < k.length; j++) {
					suffixtree.topKQ(k[j], q[i]);
				}
			}
			log.stopTest("Top-k-q queries");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void doExperiments(){
		// Open files
		File dir = new File(filesFolder);
		// english.50
		File[] englishFiles = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith("english") && name.endsWith("50MB");
		    }
		});
		// dna.50
		File[] dnaFiles = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith("dna") && name.endsWith("50MB");
		    }
		});
		
		randomStrings = new ArrayList<String>();
		
		log.startTest("English files", true);
		for (int i = 0; i < englishFiles.length; i++) {
			log.startTest("File "+englishFiles[i].getName(), true);
			doEnglish(englishFiles[i]);
			log.stopTest("File "+englishFiles[i].getName());
		}
		log.stopTest("English files");
		
		randomStrings = new ArrayList<String>();
		
		log.startTest("DNA files", true);
		for (int i = 0; i < dnaFiles.length; i++) {
			log.startTest("File "+englishFiles[i].getName(), true);
			doDNA(dnaFiles[i]);
			log.stopTest("File "+englishFiles[i].getName());
		}
		log.stopTest("DNA files");
	}

	public static void main(String[] args) throws IOException {
		
		// Initialize
		String logName = System.currentTimeMillis() + ".log";
		log = new Logger(filesFolder + logName, "SuffixTree", logName);
		
		// Run!
		//doExperiments();
		
		SuffixTree st = new SuffixTree("abacbadcbaedcbafedcbagfedcbahijk");
		System.out.println(st.count("ab"));
		//System.out.println(st.count("zorro"));
		//System.out.println(st.count("porro"));
		//System.out.println(st.count("cifuentes"));


	}

}

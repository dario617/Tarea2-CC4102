package experiments;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import arbolito.SuffixTree;
import experiments.Logger;
public class Experiments {
	
	private static final String filesFolder = "./";
	private static Logger log;
	private static final char bottom = '\u0006';
	private static ArrayList<String> randomStrings;
	private static final int randomCharLength = 5;
	private static final boolean DO_TEST = false;
	private static Random rnd;
	
	public static String getRandomPattern(int length) {
		return getRandomPattern(length, false);
	}
	
	public static String getRandomPattern(int length, boolean dna) {
		String universe= dna ? "actg" : "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
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
			//sb.append(bottom);
			
			String text = sb.toString();
						
			// Create suffix tree
			log.startTest("Initialize suffix tree");
			SuffixTree suffixtree = new SuffixTree(text);
			log.stopTest("Initialize suffix tree");
			
			if(DO_TEST) {
				System.out.println("DOING TEST!!!");
				suffixtree.checkSuffixTree();
				suffixtree.checkSuffixTreeOnLinks();
				System.out.println("DID TEST!!!");				
			}
			
			// Generate patterns
			int n = text.length()/10;
			String[] patterns = text.split("\\s", n); // Get all words
			
			if(randomStrings.size() < n) {
				for (int i = randomStrings.size(); i < n; i++) {
					randomStrings.add(getRandomPattern(randomCharLength));
				}
			}
			
			// Do random queries for count
			log.startTest("Positive Query COUNT");
			for (int i = 0; i < n; i++) {
				index = rnd.nextInt(n);
				suffixtree.count(patterns[index]);
			}
			log.stopTest("Positive Query COUNT");
			
			// Do "miss search" queries
			log.startTest("Negative Query COUNT");
			for (int i = 0; i < randomStrings.size(); i++) {
				index = rnd.nextInt(n);
				suffixtree.count(randomStrings.get(index));
			}
			log.stopTest("Negative Query COUNT");
			
			// Do queries for locate
			log.startTest("Positive Query LOCATE");
			for (int i = 0; i < n; i++) {
				index = rnd.nextInt(n);
				suffixtree.locate(patterns[index]);
			}
			log.stopTest("Positive Query LOCATE");
			
			// Do "miss search" queries
			log.startTest("Negative Query LOCATE");
			for (int i = 0; i < randomStrings.size(); i++) {
				index = rnd.nextInt(n);
				suffixtree.locate(randomStrings.get(index));
			}
			log.stopTest("Negative Query LOCATE");
			
			int[] k = {3,5,10};
			int[] q = {4,5,6,7};
			
			log.startTest("Top-k-q queries");
			for (int i = 0; i < q.length; i++) {
				for (int j = 0; j < k.length; j++) {
					log.startTest("Top-k-q q="+q[i]+",k="+k[j]);
					suffixtree.topKQ(k[j], q[i]);
					log.stopTest("Top-k-q q="+q[i]+",k="+k[j]);
				}
			}
			log.stopTest("Top-k-q queries");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void doDNA(File f, boolean doCountTests) {
		try {
			// Recover text
			FileReader fr = new FileReader(f);
			StringBuilder sb = new StringBuilder();
			int index;
			while((index = fr.read()) != -1) {
				sb.append((char)index);	
			}
			// Add bottom element
			//sb.append(bottom);
			
			String text = sb.toString();
						
			// Create suffix tree
			log.startTest("Initialize suffix tree");
			SuffixTree suffixtree = new SuffixTree(text);
			log.stopTest("Initialize suffix tree");
			
			if(DO_TEST) {
				System.out.println("DOING TEST!!!");
				suffixtree.checkSuffixTree();
				suffixtree.checkSuffixTreeOnLinks();
				System.out.println("DID TEST!!!");				
			}
			
			if(doCountTests) {
				
				// Generate patterns
				int n = text.length()/10;
				int count, left, right;
				String[] patterns = new String[n];
				Random rnd = new Random();
				// n patterns, of variable size
				int[] sizes = {8,16,32,64};
				
				for (int i = 0; i < sizes.length; i++) {
					// Create the substrings of current size
					count = 0;
					while(count < n) {
						right = sizes[i];
						left = rnd.nextInt(text.length() - sizes[i]) + 1;
						patterns[count] = text.substring(left,left + right);
						count++;
					}
					System.out.println("Created valid patterns");
					// and random strings of size
					randomStrings = new ArrayList<String>(n);
					for (int j = randomStrings.size(); j < n; j++) {
						randomStrings.add(getRandomPattern(sizes[i],true));
					}
					System.out.println("Created random patterns");
					
					// Do queries for count
					log.startTest("Positive Query COUNT for patterns size="+sizes[i]);
					for (int j = 0; j < patterns.length; j++) {
						index = rnd.nextInt(n);
						suffixtree.count(patterns[index]);
					}
					log.stopTest("Positive Query COUNT for patterns size="+sizes[i]);
					
					// Do "miss search" queries
					log.startTest("Negative Query COUNT for patterns size="+sizes[i]);
					for (int j = 0; j < randomStrings.size(); j++) {
						index = rnd.nextInt(n);
						suffixtree.count(randomStrings.get(index));
					}
					log.stopTest("Negative Query COUNT for patterns size="+sizes[i]);
					
					// Do queries for locate
					log.startTest("Positive Query LOCATE for patterns size="+sizes[i]);
					for (int j = 0; j < patterns.length; j++) {
						index = rnd.nextInt(n);
						suffixtree.locate(patterns[index]);
					}
					log.stopTest("Positive Query LOCATE for patterns size="+sizes[i]);
					
					// Do "miss search" queries
					log.startTest("Negative Query LOCATE for patterns size="+sizes[i]);
					for (int j = 0; j < randomStrings.size(); j++) {
						index = rnd.nextInt(n);
						suffixtree.locate(randomStrings.get(index));
					}
					log.stopTest("Negative Query LOCATE for patterns size="+sizes[i]);				
				}
			}
			
			int[] k = {3,5,10};
			int[] q = {4,8,16,32};
			
			log.startTest("Top-k-q queries");
			for (int i = 0; i < q.length; i++) {
				for (int j = 0; j < k.length; j++) {
					log.startTest("Top-k-q q="+q[i]+",k="+k[j]);
					suffixtree.topKQ(k[j], q[i]);
					log.stopTest("Top-k-q q="+q[i]+",k="+k[j]);
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
		        return name.startsWith("english");
		    }
		});
		// dna.50
		File[] dnaFiles = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith("dna");
		    }
		});
		
		// Set random
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		randomStrings = new ArrayList<String>();
		
		log.startTest("English files", true);
		for (int i = 0; i < englishFiles.length; i++) {
			log.startTest("File "+englishFiles[i].getName(), true);
			doEnglish(englishFiles[i]);
			log.stopTest("File "+englishFiles[i].getName());
			System.gc();
		}
		log.stopTest("English files");
		
		randomStrings = new ArrayList<String>();
		
		log.startTest("DNA files", true);
		for (int i = 12; i < dnaFiles.length - 1; i++) {
			log.startTest("File "+dnaFiles[i].getName(), true);
			// On final iter do tests (2^22)
			// (Como dijo el auxiliar)
			if(i + 2 == dnaFiles.length){ 
				doDNA(dnaFiles[i], true);				
			}
			doDNA(dnaFiles[i], false);
			log.stopTest("File "+dnaFiles[i].getName());
			System.gc();
		}
		log.stopTest("DNA files");
	}

	public static void main(String[] args) throws IOException {
		
		// Initialize
		String logName = System.currentTimeMillis() + ".log";
		log = new Logger(filesFolder + logName, "SuffixTree", logName);
		
		// Run!
		doExperiments();
		log.close();
	}

}

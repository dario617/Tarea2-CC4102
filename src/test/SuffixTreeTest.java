package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import arbolito.SuffixTree;

class SuffixTreeTest {
	
	private String s1 = "abacabadacaba";
	private String s2 = "el zorro rorro es muy porro pero le gusta subir el morro desde cachorro con su chaqueta de chiporro";

	@Test
	public void creationTest() {
		// If it dies we are doomed!
		SuffixTree st1 = new SuffixTree(s1);
		SuffixTree st2 = new SuffixTree(s2);
	}

	@Test
	public void verifyCorrectenessTest() {
		SuffixTree st1 = new SuffixTree(s1);
		assertTrue(st1.checkSuffixTree());
		assertTrue(st1.checkSuffixTreeOnLinks());
		SuffixTree st2 = new SuffixTree(s2);
		assertTrue(st2.checkSuffixTree());
		assertTrue(st2.checkSuffixTreeOnLinks());
	}
	
	@Test
	public void verifyCounterTest() {
		SuffixTree st1 = new SuffixTree(s1);
		assertEquals(7,st1.count("a"));
		assertEquals(3,st1.count("b"));
		assertEquals(2,st1.count("c"));
		assertEquals(1,st1.count("d"));
		SuffixTree st2 = new SuffixTree(s2);
		assertEquals(6,st2.count("orro"));
	}
	
	@Test
	public void verifyLocateTest1() {
		
		ArrayList<Integer> actual;
		ArrayList<Integer> ans;
		
		SuffixTree st1 = new SuffixTree(s1);
		String[] queries = {"a", "b", "c", "d"};
		for (int i = 0; i < queries.length; i++) {
			ans = st1.locate(queries[i]);
			actual = getActualLocate(s1, queries[i]);
			Collections.sort(ans);
			
			for (int j = 0; j < ans.size(); j++) {
				assertEquals(actual.get(j), ans.get(j));
			}
		}
	}
	
	@Test
	public void verifyLocateTest2() {
		
		ArrayList<Integer> actual;
		ArrayList<Integer> ans;
		
		SuffixTree st1 = new SuffixTree(s1);
		String[] queries = {"orro","en","el","u"};
		for (int i = 0; i < queries.length; i++) {
			ans = st1.locate(queries[i]);
			actual = getActualLocate(s2, queries[i]);
			Collections.sort(ans);
			
			for (int j = 0; j < ans.size(); j++) {
				assertEquals(actual.get(j), ans.get(j));
			}
		}
	}
	
	private ArrayList<Integer> getActualLocate(String text, String query) {
		ArrayList<Integer> actual = new ArrayList<Integer>();
		// Find the index
		int index = 0, last = 0, current;
		while(index < text.length()) {
			current = text.indexOf(query,last);
			if(current == -1) {
				break;
			}
			actual.add(current);
			last = current + 1;
			index = last;
		}
		return actual;
	}
}

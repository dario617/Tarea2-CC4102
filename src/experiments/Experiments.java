package experiments;
import arbolito.SuffixTree;
public class Experiments {

	public static void main(String[] args) {
		SuffixTree st = new SuffixTree("abacbadcbaedcbafedcbagfedcbahijk");
		System.out.println(st.count("orro"));
		//System.out.println(st.count("zorro"));
		//System.out.println(st.count("porro"));
		//System.out.println(st.count("cifuentes"));

	}

}

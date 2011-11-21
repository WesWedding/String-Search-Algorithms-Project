/**
 * 
 */
package stringsearch;
import java.util.Hashtable;

/**
 * @author Weston Wedding
 *
 */
public class StringSearch {
	
	public String text = "";
	
	public enum SearchType {
		BRUTE, BOYER, HOR
	};
	
	public StringSearch() {
	}
	
	public StringSearch(String inString) {
		text = inString;		
	}
	
	public void setString(String inString) {
		text = inString;
	}
	
	public int bruteSearch(String needle) {
		return -1;
	}
	
	public int getIndex(String needle, SearchType sType) {
		switch (sType) {
		case BRUTE:
			return bruteSearch(text.toCharArray(), needle.toCharArray(), text.length(), needle.length());
		case HOR:
			char[] alpha = genAlphabet(text);
			return horSearch(text.toCharArray(), needle.toCharArray(), text.length(), needle.length(), alpha);
		case BOYER:
		default:
			return -1;
		}
	}
	
	
	/*public int boyerSearch(String needle) {
		return -1;
	} */	
	private int bruteSearch(char[] text, char[] pattern, int n, int m) {
		for(int i = 0; i <= (n - m);i++) {
			int j = 0;
			while(j < m && pattern[j] == text[i+j]) {
				j++;
			}
			if (j == m)
				return i;
		}
		return -1;
	}
	
	public char[] genAlphabet(String text) {
		String alphabet = new String();
		for(int i = 0; i < text.length(); i++ ) {
			char c = text.charAt(i);
			if(alphabet.indexOf(c) == -1) {
				alphabet += c;				
			}
		}
		return alphabet.toCharArray();
	}
	
	private int horSearch(char[] text, char[] pattern, int n, int m, char[] alpha) {
		Hashtable<Character, Integer> table = shiftTable(pattern, m, alpha);
		int k;
		int i = m - 1;
		while(i <= (n - 1)) {
			k = 0;
			while(k <= (m-1) && pattern[m - 1 - k] == text[i - k]) {
				k++;
			}
			if (k == m) {
				return i - m + 1;
			} else {
				i = i + table.get(text[i]);
			}
		}
		return -1;
	}
	
	private Hashtable<Character, Integer> shiftTable(char[] pattern, int m, char[] alphabet) {
		Hashtable<Character, Integer> table = new Hashtable<Character, Integer>();
		//initialize all elements of the table
		for(int i = 0; i < alphabet.length; i++) {
			table.put(alphabet[i], m);
		}
		for(int j = 0; j <= (m - 2); j++) {
			table.put(pattern[j], m - 1 - j);	
		}
		return table;
	}
	
	public static void main(String [ ] args)
	{
		StringSearch search = new StringSearch("I like ham.  I also like gravy.");
		search.getIndex("I like ham.", SearchType.HOR);
	}
}

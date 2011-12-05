/**
 * 
 */
package stringsearch;
import java.util.Hashtable;
import java.lang.Math;

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
	
	public int getIndex(String needle, SearchType sType, char[] alpha) {
		//char[] alpha;
		switch (sType) {
		case BRUTE:
			return bruteSearch(text.toCharArray(), needle.toCharArray(), text.length(), needle.length());
		case HOR:
			alpha = genAlphabet(text);
			return horSearch(text.toCharArray(), needle.toCharArray(), text.length(), needle.length(), alpha);
		case BOYER:
			return boyerSearch(text.toCharArray(), needle.toCharArray(), text.length(), needle.length(), alpha);
		default:
			return -500;
		}
	}
	
	/**
	 * Brute force search algorithm.  Not a lot to explain: step through each individual character
	 * and try to match the pattern.  A mismatch means shifting to the next character and trying again from
	 * the beginning of the string.
	 * @param text
	 * @param pattern
	 * @param n
	 * @param m
	 * @return
	 */
	private int bruteSearch(char[] text, char[] pattern, int n, int m) {
		int compares = 0;
		for(int i = 0; i <= (n - m);i++) {
			int j = 0;
			while(j < m && pattern[j] == text[i+j]) {
				compares++;
				j++;
			}
			if (j == m)
				return i;
		}
		return -1;
	}
	
	private int bruteSearchReverse(char[] text, char[] pattern, int n, int m) {
		for(int i = n - m; i >= 0;i--) {
			int j = 0;
			while(j < m && i >= j && pattern[j] == text[i-j]) {
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
	
	private int boyerSearch(char[] text, char[] pattern, int n, int m, char[] alpha) {
	  	Hashtable<Character, Integer> badshift = shiftTable(pattern, m, alpha);
	  	Hashtable<String, Integer> goodSuffix = goodSuffixTable(pattern, m);
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
				int dist1, dist2;
				dist1 = Math.max(badshift.get(text[i]) -1 , 1);
				if (k == 0) {
					i += dist1;
				}
				else if (k > 0) {
					int patternLength = pattern.length;
					char[] searchkey = new char[k];
					for(int j = 0; j < k; j++) {
						searchkey[j] = pattern[patternLength - k + j];						
					}
					String searchkey2 = new String(searchkey);
					i += Math.max(dist1, goodSuffix.get(searchkey2));
				}			
			}
		}
		return -1;
	}
	
	// shiftTable used by boyer and horspool
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
	
	// goodSuffixTable for boyer
	private Hashtable<String, Integer> goodSuffixTable(char[] pattern, int m) {
		String suffix = new String(), prefix = new String();
		Hashtable<String, Integer> theTable = new Hashtable<String, Integer>();
		for(int k = 1; k <= m; k++) {
			int shift = 0, suffixLength = 0, patternLength = 0, suffixPos = 0;
			int repeatPos = pattern.length;
			suffix = pattern[m-k] + suffix;
			suffixLength = suffix.length();
			patternLength = pattern.length;
			suffixPos = patternLength - suffixLength;
			char[] subPattern = new char[pattern.length - 1];
			for(int i = 0; i < subPattern.length;i++) {
				subPattern[i] = pattern[i];			
			}
			repeatPos = bruteSearchReverse(subPattern, suffix.toCharArray(), subPattern.length, suffixLength);
			if(repeatPos != suffixPos && repeatPos != -1) { //We've found another, rightmost, suffix
				//Make sure the preceeding character is not the same
				if((repeatPos - 1) > 0 && (patternLength - suffixLength - 1) > 0 //bounds
						 && pattern[repeatPos - 1] != pattern[patternLength - suffixLength - 1]) {
					shift = suffixPos - repeatPos;
				}
			} else { //We need to find the longest prefix l<k that matches the suffix of size l and use this
				     //distance as shift
				//Compare ever increasing suffix chunks until matches are no longer found.
				int prefixPos = 0;
				prefix = "";
				for(int j = 0, pos = -1; j < suffix.length() - 1 || pos != -1; j++) {
					prefixPos = pos;
					prefix += pattern[j];
					pos = bruteSearchReverse(suffix.toCharArray(), prefix.toCharArray(), suffix.length(), prefix.length());
				}
				//Shift is now the distance between suffix and prefix
				shift = suffixPos - prefixPos;
			}
			theTable.put(suffix, shift);
			
		}
		return theTable;		
	}
}

package test;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import stringsearch.*;

public class TestingStringSearchClass {
	
	private String originSpecies = null;
	private String kingJames = null;
	private String generatedText = "";
	private String partialDarwin = null;
	private String partialBible = null;
	private String partialGenerated = null;
	private String darwinPattern, biblePattern, generatedPattern;
	private int nSize = 0; 
	private long testLoops = 1000;
	private StringSearch originSearch, bibleSearch, generatedSearch;
	char[] alphabetChoices = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p'};
	
	// Initially used to determine where a pattern might fall in the text
	// but hijacked to generate specific worst-case pattern types
	public enum PatternLocation {
		HALFWAY, END, NONE, BRUTEWORST, HORWORST, BOYERWORST
	};
	
	public enum TextChoice {
		ORIGINS, BIBLE, GENERATED
	};
	
	
	/**
	 * 
	 */
	public TestingStringSearchClass() {
		try {
			 originSpecies = readTextFile("fulldarwin.txt");
			 kingJames = readTextFile("fullbible.txt");
		} catch (Exception e) {
			throw new Error("Failed to open files");
		}
	}
	
	private void TestInit(int textLength, int patternSize, PatternLocation loc) {
		int startIndex, endIndex;
		nSize = textLength;
		if (textLength <= originSpecies.length()) {
			partialDarwin = new String(originSpecies.substring(0, textLength));
		} else {
			partialDarwin = originSpecies;
		}
			
		if (textLength <= kingJames.length()) {
			partialBible = new String(kingJames.substring(0, textLength));
		} else {
			partialBible = kingJames;
		}
		
		if (textLength <= generatedText.length()) {
			partialGenerated = new String(generatedText.substring(0, textLength));
		} else {
			partialGenerated = generatedText;
		}
		originSearch = new StringSearch(partialDarwin);
		bibleSearch = new StringSearch(partialBible);
		generatedSearch = new StringSearch(partialGenerated);
		
				
		generatePattern(loc, -1, patternSize);
	}
		
	
	private void generateText(int length, int alphabetSize) {
		Random rand = new Random(System.currentTimeMillis());
		generatedText = new String();
		for(int i = 0; i< length ;i++) {
			generatedText += alphabetChoices[rand.nextInt(alphabetSize)];
		}
	}	
	
	private void generatePattern(PatternLocation type, int patternStart, int patternSize) {
		int startIndex, endIndex;
		darwinPattern = new String();
		biblePattern = new String();
		char nadda = '\u000F';
		switch(type) {
		case HALFWAY:
			startIndex = partialDarwin.length()/2;
			endIndex = partialDarwin.length()/2 + patternSize;
			if(endIndex >= partialDarwin.length()) {
				endIndex = partialDarwin.length() - 1;
			}
			darwinPattern = partialDarwin.substring(startIndex, endIndex);
			startIndex = partialBible.length()/2;
			endIndex = partialBible.length()/2 + patternSize;
			if(endIndex >= partialBible.length()) {
				endIndex = partialBible.length() - 1;
			}
			biblePattern = partialBible.substring(startIndex, endIndex);
			break;
		case END:
			startIndex = partialDarwin.length() - patternSize;
			endIndex = partialDarwin.length();
			darwinPattern = partialDarwin.substring(startIndex, endIndex);
			biblePattern = partialBible.substring(startIndex, endIndex);
			break;
		case NONE:
			char unusual = '\u000F';
			for(int i = 1; i <= patternSize; i++) {
				darwinPattern += unusual;
				biblePattern += unusual;
			}
			break;
		case BRUTEWORST:
			//generatedPattern
			startIndex = patternStart;
			endIndex = startIndex + patternSize;
			
			if(endIndex >= partialGenerated.length()) {
				endIndex = partialGenerated.length() - 1;
			}
			generatedPattern = partialGenerated.substring(startIndex, endIndex);
			generatedPattern = generatedPattern.substring(0, generatedPattern.length() - 1) + nadda;
			
			//Failed attempt to generate worst cases in a useful fashion for "real" text
			/*if(endIndex >= partialDarwin.length()) {
				endIndex = partialDarwin.length() - 1;
			}
			darwinPattern = partialDarwin.substring(startIndex, endIndex);
			//Replace the last character
			darwinPattern = darwinPattern.substring(0, darwinPattern.length() - 1) + nadda;
			
			startIndex = patternStart;
			endIndex = startIndex + patternSize;
			if(endIndex >= partialBible.length()) {
				endIndex = partialBible.length() - 1;
			}
			biblePattern = partialBible.substring(startIndex, endIndex);	
			biblePattern = biblePattern.substring(0, biblePattern.length() - 1) + nadda; */
			
			
			break;
		case HORWORST: //Depends on the string being a single character
			startIndex = patternStart;
			endIndex = startIndex + patternSize;
			if(endIndex >= partialGenerated.length()) {
				endIndex = partialGenerated.length() - 1;
			}
			generatedPattern = partialGenerated.substring(startIndex, endIndex);
			generatedPattern = nadda + generatedPattern.substring(1, generatedPattern.length() - 1);
			break;
		case BOYERWORST:
			//TODO: find a way to beat the heuristics
			break;
		}
	}

	private void TestCleanup() {
		partialDarwin = new String();
		partialBible = new String();
		originSearch = new StringSearch();
		bibleSearch = new StringSearch();	
	}
	
	
	/**
	 * Credit goes to Gervase Gallant (http://www.javazoid.com/foj_file.html#ReadTextfile)
	 * Allows you to easily load an entire file with a single command line.
	 *  
	 * @param fullPathFilename
	 * @return
	 * @throws IOException
	 */
	public static String readTextFile(String fullPathFilename) throws IOException {
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new FileReader(fullPathFilename));
				
		char[] chars = new char[1024];
		int numRead = 0;
		while( (numRead = reader.read(chars)) > -1){
			sb.append(String.valueOf(chars));	
		}

		reader.close();

		return sb.toString();
	}	
	
	
	/*
	 * Basic Class Testing
	 */
	
	@Test
	public void testStringLoadEmpty() {
		StringSearch search = new StringSearch();
		assertEquals(null, search.text, "");
	}
	
	@Test
	public void testStringLoadCorrectLong() {
		StringSearch search = new StringSearch(originSpecies);
		assertEquals("Constructor Passing Failure",search.text, originSpecies);
		search.setString(originSpecies);
		assertEquals("Method Setting Failure",search.text, originSpecies);
	}
	
	/* 
	 * Sequential Tests
	 */
	@Test
	public void testStringSearchSequentialExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		assertEquals(0, search.getIndex("strings", StringSearch.SearchType.BRUTE, null));
		assertEquals(8, search.getIndex("are", StringSearch.SearchType.BRUTE, null));
		assertEquals(10, search.getIndex("e here", StringSearch.SearchType.BRUTE, null));
	}
	@Test
	public void testStringSearchSequentialNotExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		assertEquals(-1, search.getIndex("apples", StringSearch.SearchType.BRUTE, null));
	}
	
	@Test
	public void testStringSearchSequentialExistsLong() {
		//There should only be 9 exact matches of the string
		// "species" according to Eclipse IDE's find/replace
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(2331, search.getIndex("species", StringSearch.SearchType.BRUTE, null));
	}
	@Test
	public void testStringSearchSequentialNotExistsLong() {
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(-1 ,search.getIndex("Nintendo", StringSearch.SearchType.BRUTE, null), 0);
	}
	
	/*
	 * Boyer-Moore Algo tests
	 */
	
	@Test
	public void testStringSearchBoyerExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(0, search.getIndex("strings", StringSearch.SearchType.BOYER, alpha));
		assertEquals(8, search.getIndex("are", StringSearch.SearchType.BOYER, alpha));
		assertEquals(10, search.getIndex("e here", StringSearch.SearchType.BOYER, alpha));
	}
	@Test
	public void testStringSearchBoyerNotExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(-1, search.getIndex("ABCBAB", StringSearch.SearchType.BOYER, alpha));
	}
	
	@Test
	public void testStringSearchBoyerExistsLong() {
		//There should only be 9 exact matches of the string
		// "species" according to Eclipse IDE's find/replace
		StringSearch search = new StringSearch(originSpecies);
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(2331, search.getIndex("species", StringSearch.SearchType.BOYER, alpha));
	}
	@Test
	public void testStringSearchBoyerNotExistsLong() {
		StringSearch search = new StringSearch(originSpecies);
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(-1, search.getIndex("Nintendo", StringSearch.SearchType.BOYER, alpha));
	}
	
	/*
	 * Hoorspool Algo tests
	 */
			
	@Test
	public void testStringSearchHoorspoolExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(0, search.getIndex("strings", StringSearch.SearchType.HOR, alpha));
		assertEquals("Couldn't find are in simple string.",8, search.getIndex("are", StringSearch.SearchType.HOR, alpha));
		assertEquals("Couldn't find \"e here\" in simple string.", 10, search.getIndex("e here", StringSearch.SearchType.HOR, alpha));
		
	}
	@Test
	public void testStringSearchHoorspoolNotExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(-1, search.getIndex("apples", StringSearch.SearchType.HOR, alpha));
	}
	
	@Test
	public void testStringSearchHoorspoolExistsLong() {
		//There should only be 9 exact matches of the string
		// "species" according to Eclipse IDE's find/replace
		StringSearch search = new StringSearch(originSpecies);
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(2331, search.getIndex("species", StringSearch.SearchType.HOR, alpha));
	}
	@Test
	public void testStringSearchHoorspoolNotExistsLong() {
		StringSearch search = new StringSearch(originSpecies);
		char[] alpha = search.genAlphabet(search.text);
		assertEquals(-1, search.getIndex("Nintendo", StringSearch.SearchType.HOR, alpha));
	}
	
/*	@Test
	public void testBruteWorstCaseGeneration() {
		int textSize = 100, patternSize = 10, limit = 10000;
		TestInit(textSize, patternSize, PatternLocation.BRUTEWORST);
		String originsCompareStr = "is for th" + '\u000F';
		String bibleCompareStr = "yone anyw" + '\u000F';
		assertEquals("Brute Worst generation failed", bibleCompareStr, biblePattern);
		assertEquals("Brute Worst generation failed", originsCompareStr, darwinPattern);
	}
	*/
	/*** Begin data generating tests ***/
	
	// This test keeps the pattern size minimal but increases the text size incrementally
	// Should be linear across the board
	@Test
	public void testAll3VariableTextSizeNotFound() throws Exception{
		int textSize = 100, patternSize = 1, limit = 100000;
		double avgTime;
		TextChoice text;
		PrintWriter out;
			out = new PrintWriter(new FileWriter("all3VariableTextSizeNotFound.csv"));
			out.print("search"+'\t'+"text"+'\t'+"n"+'\t'+"m"+'\t'+"avgTime"+'\n');
		while(textSize <= limit) {
			TestInit(textSize, patternSize, PatternLocation.NONE);
			
			text = TextChoice.ORIGINS;
			avgTime = testText(text, out, textSize, patternSize, -1);
			
			text = TextChoice.BIBLE;
			avgTime = testText(text, out, textSize, patternSize, -1);
			
			//Baselines
			out.print("n*m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize*patternSize)+'\n');
			out.print("n+m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize+patternSize)+'\n');
			out.print("n"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+textSize+'\n');
			out.print("n/m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize/patternSize)+'\n');
			
			TestCleanup();
			textSize = textSize + 1000;
		}
		out.close();
	}
	
	// This test keeps the text size large and increases pattern sizes
	// This should also be linear in all cases
	@Test
	public void testAll3StableTextSizeNotFound() throws Exception{
		int textSize = 10000, patternSize = 1, limit = 100;
		double avgTime;
		TextChoice text;
		PrintWriter out;
			out = new PrintWriter(new FileWriter("all3StableTextSizeNotFound.csv"));
			out.print("search"+'\t'+"text"+'\t'+"n"+'\t'+"m"+'\t'+"avgTime"+'\n');
		while(patternSize <= limit && patternSize <= textSize ) {
			TestInit(textSize, patternSize, PatternLocation.NONE);
			
			text = TextChoice.ORIGINS;
			avgTime = testText(text, out, textSize, patternSize, -1);
			
			text = TextChoice.BIBLE;
			avgTime = testText(text, out, textSize, patternSize, -1);
			//Baselines
			out.print("n*m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize*patternSize)+'\n');
			out.print("n+m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize+patternSize)+'\n');
			out.print("n"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+textSize+'\n');
			out.print("n/m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(float)(textSize/patternSize)+'\n');
			
			TestCleanup();
			patternSize = patternSize + 1;
		}
		out.close();
	}
	
	// Evaluates an ever-increasing pattern size along with text size increases
	@Test
	public void testAll3VariableTextAndPatternNotFound() throws Exception{
		int textSize = 10000, patternSize = 1, textLimit = 100000, patternLimit = 150;
		
		double avgTime;
		TextChoice text;
		PrintWriter out;
			out = new PrintWriter(new FileWriter("all3VariableTextAndPatternNotFound.csv"));
			out.print("search"+'\t'+"text"+'\t'+"n"+'\t'+"m"+'\t'+"avgTime"+'\n');
		while(textSize <= textLimit) {
			while(patternSize <= patternLimit) {
				TestInit(textSize, patternSize, PatternLocation.NONE);
			
				text = TextChoice.ORIGINS;
				avgTime = testText(text, out, textSize, patternSize, -1);
			
				text = TextChoice.BIBLE;
				avgTime = testText(text, out, textSize, patternSize, -1);
				//Baselines
				out.print("n*m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize*patternSize)+'\n');
				out.print("n+m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize+patternSize)+'\n');
				out.print("n"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+textSize+'\n');
				out.print("n/m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(float)(textSize/patternSize)+'\n');
			
				TestCleanup();
				patternSize = patternSize + 1;
			}
			out.print("*****************************************");
			textSize = textSize + 1000;
		}
		out.close();
	}
	
	@Test
	public void testAll3StableTextMedPatternNotFound() throws Exception{
		int textSize = 10000, patternSize = 100, limit = 300;
		double avgTime;
		TextChoice text;
		PrintWriter out;
			out = new PrintWriter(new FileWriter("all3StableTextMedPatternNotFound.csv"));
			out.print("search"+'\t'+"text"+'\t'+"n"+'\t'+"m"+'\t'+"avgTime"+'\n');
		while(patternSize <= limit && patternSize <= textSize ) {
			TestInit(textSize, patternSize, PatternLocation.NONE);
			testLoops = 100;
			
			text = TextChoice.ORIGINS;
			avgTime = testText(text, out, textSize, patternSize, -1);
			
			text = TextChoice.BIBLE;
			avgTime = testText(text, out, textSize, patternSize, -1);
			//Baselines
			out.print("n*m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize*patternSize)+'\n');
			out.print("n+m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize+patternSize)+'\n');
			out.print("n"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+textSize+'\n');
			out.print("n/m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(float)(textSize/patternSize)+'\n');
			
			TestCleanup();
			patternSize = patternSize + 1;
		}
		out.close();
	}
	
	@Test
	// Brute worst case: searching all of M every Nth element
	public void testBruteWorstCase() throws Exception{
		int textSize = 500, maxTextSize = 100000, patternSize = 10, limit = 10000, patternStart = 0;
		generateText(maxTextSize,1);
		StringSearch generatedSearch = new StringSearch(generatedText);
		double avgTime;
		long oldLoops = testLoops;
		testLoops = 1000;
		TextChoice text;
		PrintWriter out;
			out = new PrintWriter(new FileWriter("testBruteWorstCase.csv"));
			out.print("search"+'\t'+"text"+'\t'+"n"+'\t'+"m"+'\t'+"avgTime"+'\n');
		//Grow our inputs
		while(textSize < limit && patternSize < limit) {
			TestInit(textSize, patternSize, PatternLocation.NONE);
			
			text = TextChoice.GENERATED;
			generatePattern(PatternLocation.BRUTEWORST, patternStart, patternSize);	
			avgTime = runTestLoops(text, StringSearch.SearchType.BRUTE, out);
			out.print("brute"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+avgTime+'\n');

			//Baselines
			out.print("n*m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize*patternSize)+'\n');
			out.print("n"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+textSize+'\n');
			
			out.flush();
			TestCleanup();
			textSize += 50;
			patternSize += 5;
		}
		out.close();
		testLoops = oldLoops;
	}
	
	@Test
	public void testHorWorstCase() throws Exception{
		int textSize = 500, maxTextSize = 100000, patternSize = 10, limit = 10000, patternStart = 0;
		generateText(maxTextSize,1);
		StringSearch generatedSearch = new StringSearch(generatedText);
		double avgTime;
		long oldLoops = testLoops;
		testLoops = 1000;
		TextChoice text;
		PrintWriter out;
			out = new PrintWriter(new FileWriter("testHorWorstCase.csv"));
			out.print("search"+'\t'+"text"+'\t'+"n"+'\t'+"m"+'\t'+"avgTime"+'\n');
		//Grow our inputs
		while(textSize < limit && patternSize < limit) {
			TestInit(textSize, patternSize, PatternLocation.NONE);
			
			text = TextChoice.GENERATED;
			generatePattern(PatternLocation.HORWORST, patternStart, patternSize);	
			avgTime = runTestLoops(text, StringSearch.SearchType.HOR, out);
			out.print("horspool"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+avgTime+'\n');

			//Baselines
			out.print("n*m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(textSize*patternSize)+'\n');
			out.print("n"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+textSize+'\n');
			
			out.flush();
			TestCleanup();
			textSize += 50;
			patternSize += 5;
		}
		out.close();
		testLoops = oldLoops;
	}
	
	@Test
	public void testBoyerWorstCase() throws Exception{
		int textSize = 500, maxTextSize = 100000, patternSize = 10, limit = 10000, patternStart = 0;
		generateText(maxTextSize,1);
		StringSearch generatedSearch = new StringSearch(generatedText);
		double avgTime;
		TextChoice text;
		PrintWriter out;
			out = new PrintWriter(new FileWriter("testBoyerWorstCase"));
			out.print("search"+'\t'+"text"+'\t'+"n"+'\t'+"m"+'\t'+"avgTime"+'\n');
		//Grow our inputs
		while(textSize < limit && patternSize < limit) {
			TestInit(textSize, patternSize, PatternLocation.NONE);
			
			text = TextChoice.GENERATED;
			generatePattern(PatternLocation.BOYERWORST, patternStart, patternSize);	
			avgTime = runTestLoops(text, StringSearch.SearchType.HOR, out);
			out.print("boyer"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+avgTime+'\n');

			//Baselines
			out.print("n/m"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+(float)(textSize/patternSize)+'\n');
			out.print("n"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+textSize+'\n');
			
			out.flush();
			TestCleanup();
			textSize += 50;
			patternSize += 5;
		}
		out.close();
	}
	
	
	private double testText(TextChoice text, PrintWriter out, int textSize, int patternSize, int patternType) {
		double avgTime;
		avgTime = runTestLoops(text, StringSearch.SearchType.BRUTE, out);
		out.print("brute"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+avgTime+'\n');
		out.flush();
		avgTime = runTestLoops(text, StringSearch.SearchType.HOR, out);
		out.print("horspool"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+avgTime+'\n');
		out.flush();	
		avgTime = runTestLoops(text, StringSearch.SearchType.BOYER, out);
		out.print("boyer"+'\t'+text+'\t'+textSize+'\t'+patternSize+'\t'+avgTime+'\n');
		out.flush();
		return avgTime;
	}
	
	private double runTestLoops(TextChoice text, StringSearch.SearchType type, PrintWriter out) {
		long startTime, endTime, totalTime;
		double avgTime;
		StringSearch search = null;
		String pattern = null;
		char[] alphabet = null;
		switch(text) {
		case ORIGINS: //Origins
			search = originSearch;
			pattern = darwinPattern;
			break;
		case BIBLE: //Bible
			search = bibleSearch;
			pattern = biblePattern;
			break;	
		case GENERATED:
			search = generatedSearch;
			pattern = generatedPattern;
			break;
		}
		switch(type) {
		case BOYER:
		case HOR:
			alphabet = search.genAlphabet(search.text);
			break;
		}
		startTime = System.currentTimeMillis();
		for(int i = 0; i < testLoops; i++) {
			search.getIndex(pattern, type, alphabet);
		}
		endTime = System.currentTimeMillis();
		totalTime = (endTime - startTime);
		avgTime = totalTime/(double)testLoops;
		return avgTime;
	}
	
	public static void main(String [ ] args) throws Exception {
		TestingStringSearchClass test = new TestingStringSearchClass();
		test.testBruteWorstCase();
	}
}

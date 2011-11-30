package test;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import stringsearch.*;

public class TestingStringSearchClass {
	
	private String originSpecies = null;
	private String kingJames = null;
	private String partialDarwin = null;
	private String partialBible = null;
	private int nSize = 0, testLoops = 500; 
	private StringSearch originSearch, bibleSearch;
	
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
	
	private void TestInit(int textLength) {
		nSize = textLength;
		partialDarwin = new String(originSpecies.substring(0, textLength));
		partialBible = new String(kingJames.substring(0, textLength));
		originSearch = new StringSearch(partialDarwin);
		bibleSearch = new StringSearch(partialBible);	
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
		assertEquals(0, search.getIndex("strings", StringSearch.SearchType.BRUTE));
		assertEquals(8, search.getIndex("are", StringSearch.SearchType.BRUTE));
		assertEquals(10, search.getIndex("e here", StringSearch.SearchType.BRUTE));
	}
	@Test
	public void testStringSearchSequentialNotExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		assertEquals(-1, search.getIndex("apples", StringSearch.SearchType.BRUTE));
	}
	
	@Test
	public void testStringSearchSequentialExistsLong() {
		//There should only be 9 exact matches of the string
		// "species" according to Eclipse IDE's find/replace
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(2331, search.getIndex("species", StringSearch.SearchType.BRUTE));
	}
	@Test
	public void testStringSearchSequentialNotExistsLong() {
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(-1 ,search.getIndex("Nintendo", StringSearch.SearchType.BRUTE), 0);
	}
	
	/*
	 * Boyer-Moore Algo tests
	 */
	
	@Test
	public void testStringSearchBoyerExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		assertEquals(0, search.getIndex("strings", StringSearch.SearchType.BOYER));
		assertEquals(8, search.getIndex("are", StringSearch.SearchType.BOYER));
		assertEquals(10, search.getIndex("e here", StringSearch.SearchType.BOYER));
	}
	@Test
	public void testStringSearchBoyerNotExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		assertEquals(-1, search.getIndex("apples", StringSearch.SearchType.BOYER));
	}
	
	@Test
	public void testStringSearchBoyerExistsLong() {
		//There should only be 9 exact matches of the string
		// "species" according to Eclipse IDE's find/replace
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(1661, search.getIndex("species", StringSearch.SearchType.BOYER));
	}
	@Test
	public void testStringSearchBoyerNotExistsLong() {
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(-1, search.getIndex("Nintendo", StringSearch.SearchType.BOYER));
	}
	
	/*
	 * Hoorspool Algo tests
	 */
			
	@Test
	public void testStringSearchHoorspoolExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		assertEquals(0, search.getIndex("strings", StringSearch.SearchType.HOR));
		assertEquals("Couldn't find are in simple string.",8, search.getIndex("are", StringSearch.SearchType.HOR));
		assertEquals("Couldn't find \"e here\" in simple string.", 10, search.getIndex("e here", StringSearch.SearchType.HOR));
		
	}
	@Test
	public void testStringSearchHoorspoolNotExistsSimple() {
		StringSearch search = new StringSearch("strings are here");
		assertEquals(-1, search.getIndex("apples", StringSearch.SearchType.HOR));
	}
	
	@Test
	public void testStringSearchHoorspoolExistsLong() {
		//There should only be 9 exact matches of the string
		// "species" according to Eclipse IDE's find/replace
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(1661, search.getIndex("species", StringSearch.SearchType.HOR));
	}
	@Test
	public void testStringSearchHoorspoolNotExistsLong() {
		StringSearch search = new StringSearch(originSpecies);
		assertEquals(-1, search.getIndex("Nintendo", StringSearch.SearchType.HOR));
	}
	
	/*@Test
	public void testGenerateAlphabet() {
		StringSearch search = new StringSearch();
		alphastrn
	} */
	
	@Test
	public void testAll3WithSize100NotFound() {
		TestInit(100);
		assertEquals("Origins string too short.",nSize, partialDarwin.length());
		assertEquals("Origins string too short.",nSize, partialBible.length());
		for(int i = 1; i <= testLoops; i++) {
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
		}		
		TestCleanup();
	}
	@Test
	public void testAll3WithSize1000NotFound() {
		TestInit(1000);
		assertEquals("Origins string too short.",nSize, partialDarwin.length());
		assertEquals("Origins string too short.",nSize, partialBible.length());
		for(int i = 1; i <= testLoops; i++) {
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
		}	
		TestCleanup();		
	}
	@Test
	public void testAll3WithSize10000NotFound() {
		TestInit(10000);
		assertEquals("Origins string too short.",nSize, partialDarwin.length());
		assertEquals("Origins string too short.",nSize, partialBible.length());
		for(int i = 1; i <= testLoops; i++) {
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
		}	
		TestCleanup();		
	}
	@Test
	public void testAll3WithSize100000NotFound() {
		TestInit(100000);
		assertEquals("Origins string too short.",nSize, partialDarwin.length());
		assertEquals("Origins string too short.",nSize, partialBible.length());
		for(int i = 1; i <= testLoops; i++) {
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
		}	
		TestCleanup();
	}

	@Test
	public void testAll3WithSize1000000NotFound() {
		TestInit(1000000);
		assertEquals("Origins string too short.",nSize, partialDarwin.length());
		assertEquals("Origins string too short.",nSize, partialBible.length());
		for(int i = 1; i <= testLoops; i++) {
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, originSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.HOR));
			assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, bibleSearch.getIndex("Nintendo", StringSearch.SearchType.BOYER));
		}	
		TestCleanup();
	}
	
	@Test
	public void testAll3WithSize100Found() {
		TestInit(100);
		assertEquals("Origins string too short.",nSize, partialDarwin.length());
		assertEquals("Origins string too short.",nSize, partialBible.length());
		for(int i = 1; i <= testLoops; i++) {
			assertEquals(-1, originSearch.getIndex("species", StringSearch.SearchType.HOR));
			assertEquals(-1, originSearch.getIndex("species", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, originSearch.getIndex("species", StringSearch.SearchType.BOYER));
			assertEquals(-1, bibleSearch.getIndex("Jesus", StringSearch.SearchType.HOR));
			assertEquals(-1, bibleSearch.getIndex("Jesus", StringSearch.SearchType.BRUTE));
			//assertEquals(-1, bibleSearch.getIndex("Jesus", StringSearch.SearchType.BOYER));
		}		
		TestCleanup();
	}
	
	
	/*@Test
	public void testStringSearchHoorspoolExistsLong100000() {
		StringSearch search = new StringSearch(originSpecies);
		for(int i = 1; i <= 10000; i++) {
			assertEquals(2331, search.getIndex("species", StringSearch.SearchType.HOR));			
		}
	}
	
	@Test
	public void testStringSearchHoorspoolNotExistsLong100000() {
		StringSearch search = new StringSearch(originSpecies);
		for(int i = 1; i <= 10000; i++) {
			assertEquals(2331, search.getIndex("Nintendo", StringSearch.SearchType.HOR));			
		}
	}*/
		
	/* String originTenChapts = new String("ON THE ORIGIN OF SPECIES.\n" + 
			"\n" + 
			"OR THE PRESERVATION OF FAVOURED RACES IN THE STRUGGLE FOR LIFE.\n" + 
			"\n" + 
			"\n" + 
			"By Charles Darwin, M.A.,\n" + 
			"\n" + 
			"Fellow Of The Royal, Geological, Linnaean, Etc., Societies;\n" + 
			"\n" + 
			"Author Of 'Journal Of Researches During H.M.S. Beagle's Voyage Round The\n" + 
			"World.'\n" + 
			"\n" + 
			"\n" + 
			"LONDON:\n" + 
			"\n" + 
			"JOHN MURRAY, ALBEMARLE STREET.\n" + 
			"\n" + 
			"1859.\n" + 
			"\n" + 
			"\n" + 
			"Down, Bromley, Kent,\n" + 
			"\n" + 
			"October 1st, 1859.\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"\"But with regard to the material world, we can at least go so far as\n" + 
			"this--we can perceive that events are brought about not by insulated\n" + 
			"interpositions of Divine power, exerted in each particular case, but by\n" + 
			"the establishment of general laws.\"\n" + 
			"\n" + 
			"W. Whewell: Bridgewater Treatise.\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"\"To conclude, therefore, let no man out of a weak conceit of sobriety,\n" + 
			"or an ill-applied moderation, think or maintain, that a man can search\n" + 
			"too far or be too well studied in the book of God's word, or in the book\n" + 
			"of God's works; divinity or philosophy; but rather let men endeavour an\n" + 
			"endless progress or proficience in both.\"\n" + 
			"\n" + 
			"Bacon: Advancement of Learning.\n" + 
			"\n" + 
			"\n" + 
			"CONTENTS.\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"  INTRODUCTION.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 1. VARIATION UNDER DOMESTICATION.\n" + 
			"\n" + 
			"  Causes of Variability.\n" + 
			"  Effects of Habit.\n" + 
			"  Correlation of Growth.\n" + 
			"  Inheritance.\n" + 
			"  Character of Domestic Varieties.\n" + 
			"  Difficulty of distinguishing between Varieties and Species.\n" + 
			"  Origin of Domestic Varieties from one or more Species.\n" + 
			"  Domestic Pigeons, their Differences and Origin.\n" + 
			"  Principle of Selection anciently followed, its Effects.\n" + 
			"  Methodical and Unconscious Selection.\n" + 
			"  Unknown Origin of our Domestic Productions.\n" + 
			"  Circumstances favourable to Man's power of Selection.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 2. VARIATION UNDER NATURE.\n" + 
			"\n" + 
			"  Variability.\n" + 
			"  Individual Differences.\n" + 
			"  Doubtful species.\n" + 
			"  Wide ranging, much diffused, and common species vary most.\n" + 
			"  Species of the larger genera in any country vary more than the species\n" + 
			"  of the smaller genera.\n" + 
			"  Many of the species of the larger genera resemble varieties in being\n" + 
			"  very closely, but unequally, related to each other, and in having\n" + 
			"  restricted ranges.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 3. STRUGGLE FOR EXISTENCE.\n" + 
			"\n" + 
			"  Bears on natural selection.\n" + 
			"  The term used in a wide sense.\n" + 
			"  Geometrical powers of increase.\n" + 
			"  Rapid increase of naturalised animals and plants.\n" + 
			"  Nature of the checks to increase.\n" + 
			"  Competition universal.\n" + 
			"  Effects of climate.\n" + 
			"  Protection from the number of individuals.\n" + 
			"  Complex relations of all animals and plants throughout nature.\n" + 
			"  Struggle for life most severe between individuals and varieties of the\n" + 
			"  same species; often severe between species of the same genus.\n" + 
			"  The relation of organism to organism the most important of all\n" + 
			"  relations.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 4. NATURAL SELECTION.\n" + 
			"\n" + 
			"  Natural Selection: its power compared with man's selection, its power\n" + 
			"  on characters of trifling importance, its power at all ages and on\n" + 
			"  both sexes.\n" + 
			"  Sexual Selection.\n" + 
			"  On the generality of intercrosses between individuals of the same\n" + 
			"  species.\n" + 
			"  Circumstances favourable and unfavourable to Natural Selection,\n" + 
			"  namely, intercrossing, isolation, number of individuals.\n" + 
			"  Slow action.\n" + 
			"  Extinction caused by Natural Selection.\n" + 
			"  Divergence of Character, related to the diversity of inhabitants of\n" + 
			"  any small area, and to naturalisation.\n" + 
			"  Action of Natural Selection, through Divergence of Character and\n" + 
			"  Extinction, on the descendants from a common parent.\n" + 
			"  Explains the Grouping of all organic beings.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 5. LAWS OF VARIATION.\n" + 
			"\n" + 
			"  Effects of external conditions.\n" + 
			"  Use and disuse, combined with natural selection; organs of flight and\n" + 
			"  of vision.\n" + 
			"  Acclimatisation.\n" + 
			"  Correlation of growth.\n" + 
			"  Compensation and economy of growth.\n" + 
			"  False correlations.\n" + 
			"  Multiple, rudimentary, and lowly organised structures variable.\n" + 
			"  Parts developed in an unusual manner are highly variable: specific\n" + 
			"  characters more variable than generic: secondary sexual characters\n" + 
			"  variable.\n" + 
			"  Species of the same genus vary in an analogous manner.\n" + 
			"  Reversions to long-lost characters.\n" + 
			"  Summary.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 6. DIFFICULTIES ON THEORY.\n" + 
			"\n" + 
			"  Difficulties on the theory of descent with modification.\n" + 
			"  Transitions.\n" + 
			"  Absence or rarity of transitional varieties.\n" + 
			"  Transitions in habits of life.\n" + 
			"  Diversified habits in the same species.\n" + 
			"  Species with habits widely different from those of their allies.\n" + 
			"  Organs of extreme perfection.\n" + 
			"  Means of transition.\n" + 
			"  Cases of difficulty.\n" + 
			"  Natura non facit saltum.\n" + 
			"  Organs of small importance.\n" + 
			"  Organs not in all cases absolutely perfect.\n" + 
			"  The law of Unity of Type and of the Conditions of Existence embraced\n" + 
			"  by the theory of Natural Selection.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 7. INSTINCT.\n" + 
			"\n" + 
			"  Instincts comparable with habits, but different in their origin.\n" + 
			"  Instincts graduated.\n" + 
			"  Aphides and ants.\n" + 
			"  Instincts variable.\n" + 
			"  Domestic instincts, their origin.\n" + 
			"  Natural instincts of the cuckoo, ostrich, and parasitic bees.\n" + 
			"  Slave-making ants.\n" + 
			"  Hive-bee, its cell-making instinct.\n" + 
			"  Difficulties on the theory of the Natural Selection of instincts.\n" + 
			"  Neuter or sterile insects.\n" + 
			"  Summary.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 8. HYBRIDISM.\n" + 
			"\n" + 
			"  Distinction between the sterility of first crosses and of hybrids.\n" + 
			"  Sterility various in degree, not universal, affected by close\n" + 
			"  interbreeding, removed by domestication.\n" + 
			"  Laws governing the sterility of hybrids.\n" + 
			"  Sterility not a special endowment, but incidental on other\n" + 
			"  differences.\n" + 
			"  Causes of the sterility of first crosses and of hybrids.\n" + 
			"  Parallelism between the effects of changed conditions of life and\n" + 
			"  crossing.\n" + 
			"  Fertility of varieties when crossed and of their mongrel offspring not\n" + 
			"  universal.\n" + 
			"  Hybrids and mongrels compared independently of their fertility.\n" + 
			"  Summary.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 9. ON THE IMPERFECTION OF THE GEOLOGICAL RECORD.\n" + 
			"\n" + 
			"  On the absence of intermediate varieties at the present day.\n" + 
			"  On the nature of extinct intermediate varieties; on their number.\n" + 
			"  On the vast lapse of time, as inferred from the rate of deposition and\n" + 
			"  of denudation.\n" + 
			"  On the poorness of our palaeontological collections.\n" + 
			"  On the intermittence of geological formations.\n" + 
			"  On the absence of intermediate varieties in any one formation.\n" + 
			"  On the sudden appearance of groups of species.\n" + 
			"  On their sudden appearance in the lowest known fossiliferous strata.\n" + 
			"\n" + 
			"\n" + 
			"  CHAPTER 10. ON THE GEOLOGICAL SUCCESSION OF ORGANIC BEINGS.\n" + 
			"\n" + 
			"  On the slow and successive appearance of new species.\n" + 
			"  On their different rates of change.\n" + 
			"  Species once lost do not reappear.\n" + 
			"  Groups of species follow the same general rules in their appearance\n" + 
			"  and disappearance as do single species.\n" + 
			"  On Extinction.\n" + 
			"  On simultaneous changes in the forms of life throughout the world.\n" + 
			"  On the affinities of extinct species to each other and to living\n" + 
			"  species.\n" + 
			"  On the state of development of ancient forms.\n" + 
			"  On the succession of the same types within the same areas.\n" + 
			"  Summary of preceding and present chapters.");
	*/
}

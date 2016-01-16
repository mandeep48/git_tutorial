import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class WordFrequency {
	public static void main(String[] unused) throws IOException {
        int n = 0;
        Map<String, Integer> words = new HashMap<String, Integer>();

        //... Read words from file and count them.
        Scanner wordScanner = new Scanner(new File("test.txt"));
        wordScanner.useDelimiter("[^A-Za-z]+");
        while (wordScanner.hasNext()) {
            String word = wordScanner.next().toLowerCase();
            Integer count = words.get(word);
            words.put(word, (count == null) ? 1 : count + 1);
        }
       
        System.out.println("------"+words);
        System.out.println("*******"+words.size());
        //... Sort by frequency, or alphabetically if equal frequnecy.
        ArrayList<Map.Entry<String, Integer>> entries =
                new ArrayList<Map.Entry<String, Integer>>(words.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> ent1, Map.Entry<String, Integer> ent2) {
                return (ent1.getValue() == ent2.getValue())
                        ? ent1.getKey().compareTo(ent2.getKey())
                        : ent2.getValue() - ent1.getValue();
            }
        });
        
        //System.out.println("Word Freq. Rank  f.r");
        //... Display the output.
        for (Map.Entry<String, Integer> ent : entries) {
            //if (++n > 50) break;
        	if (((++n)%1000) > 50) break;
            System.out.printf("%20s  %d  %d  %d\n", ent.getKey(), ent.getValue(),n,ent.getValue()*n);
        }
        
       
        
        Iterator<Map.Entry<String, Integer>> mEntries = words.entrySet().iterator();

		Map<Integer, Integer> newwords = new TreeMap<Integer, Integer>();
		while (mEntries.hasNext()) {
		    Map.Entry<String, Integer> entry = mEntries.next();
		    
		    Integer word = entry.getValue();
            Integer count = newwords.get(word);
            newwords.put(word, (count == null) ? 1 : count + 1);
		  
		}
		System.out.println("-----"+newwords);
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WordFrequencyCalculator {
	
	
	public static void main(String[] args)  {
		
		// Regular Expression used for tokenisation
		String regex = "[^A-Za-z]+";
		// Flag used for printing the output
        int counter = 0;
        // Data Structure used to store the words 
        Map<String, Integer> wordMap = new HashMap<String, Integer>();

		Scanner inputScanner=null;
		try {
			inputScanner = new Scanner(new File("taggers/test.txt"));
			inputScanner.useDelimiter("\\Z");

			System.out.println("------"+inputScanner.next().toLowerCase());
		} catch (FileNotFoundException e) {
			System.out.println("File is not present in the root directory");
			e.printStackTrace();
		}
		inputScanner.useDelimiter(regex);
		while (inputScanner.hasNext()) {
			String token = inputScanner.next().toLowerCase();
			Integer count = wordMap.get(token);
			wordMap.put(token, (count == null) ? 1 : count + 1);
		}
       
        // Sort the wordMap
		ArrayList<Map.Entry<String, Integer>> wordList = new ArrayList<Map.Entry<String, Integer>>(wordMap.entrySet());
		Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
				return (e1.getValue() == e2.getValue()) ? e1.getKey().compareTo(e2.getKey())
						: e2.getValue() - e1.getValue();
			}
		});
        
        
       
        System.out.println("----------Printing Frequency of Top 50 Words------------");
        System.out.printf("%15s  %12s  %12s  %12s\n", "Word", "Frequency","Rank","F.R");
		for (Map.Entry<String, Integer> entry : wordList) {
			if (++counter > 50)
				break;
			System.out.printf("%15s  %12d  %12d  %12d\n", entry.getKey(), entry.getValue(), counter, entry.getValue() * counter);
			
		}
        
        counter=0;
        System.out.println("----------Printing Frequency of every 50 words in Steps of 1000--------");
        System.out.printf("%15s  %12s  %12s  %12s\n", "Word", "Frequency","Rank","F.R");
		for (Map.Entry<String, Integer> entry : wordList) {
			if (((++counter) % 1000) <= 50)
				System.out.printf("%15s  %12d  %12d  %12d\n", entry.getKey(), entry.getValue(), counter, entry.getValue() * counter);
		}
        
       System.out.println("----------Printing Frequency of Frequency for Count 1 to 50------------");
       System.out.printf("%20s  %20s\n", "Word Frequency", "Frequency of Frequency");
		for (int i = 1; i <= 50; i++) {
			System.out.printf("%15d %15d\n", i, Collections.frequency(wordMap.values(), i));

		}
        
        
    }
}

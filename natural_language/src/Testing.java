import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Testing {

	
	public static void main(String[] args) {
		List<String> l = new ArrayList<String>();
		System.out.println(String.format("%10s  %10s  %25s  %25s\n", "Unigram","Frequency","UnSmoothedProbability","SmoothedProbability"));
		String tweet = "My name is MANdeep . 1ab";
		
		int len =tweet.length();
		//Map<Character, Integer> map = new HashMap<Character, Integer>();

		
		//StringTokenizer st = new StringTokenizer(tweet);
		
		for(int i=0;i<len;i++){
			
			char key = tweet.charAt(i);
			key= Character.toLowerCase(key);
			if(Pattern.matches("[a-z|\\.|[0-9]]", key+""))
				System.out.println(key);
			
		}
		
		for (int i = 0; i < len; i++)
		{	
			
			String key =null;
			
			if(!(i+1==len)){
				key=tweet.substring(i, i+2).toLowerCase();
				//key = new StringBuilder().append(Character.toLowerCase(tweet.charAt(i))).append(Character.toLowerCase(tweet.charAt(i+1))).toString();
			}else{
				key = tweet.substring(i).toLowerCase();
				//key = new StringBuilder().append(tweet.charAt(i)).toString();
			}
			//System.out.println(key);
			if(Pattern.matches("[A-Za-z]+", key))
			System.out.println(key);
		}
		/*for (int i=0;i<mandeep.length();i++){
			
			char c = mandeep.charAt(i);
			
			if(c != ' ')
			System.out.println(c);
		}*/
		
		Scanner inputScanner=null;
		try {
			inputScanner = new Scanner(new File("test.txt"));
			while(inputScanner.hasNext()){
				
				String str = inputScanner.nextLine();
				String n1= str.split("\\s+",4)[3];
				System.out.println(n1);
			}
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File is not present in the root directory");
			e.printStackTrace();
		}
		
		String regex="[^A-Za-z|\\-|\\']+";
		//String regex="\\s+";
		String input = "peter's";
		String a[] =input.split(regex);
		for (String temp :a){
			System.out.println(temp);
		}
		System.out.println("------"+Pattern.matches(regex, input));
		
		Map<String, Integer> words = new HashMap<String, Integer>();
		
		words.put("a",10);
		words.put("b", 1);
		words.put("c", 1);
		words.put("d", 1);
		words.put("e", 1);
		words.put("f", 1);
		words.put("g", 5);
		words.put("h", 4);
		words.put("i", 3);
		words.put("j", 2);
		words.put("k", 2);
		
		System.out.println("------"+words.entrySet());
		
		Iterator<Map.Entry<String, Integer>> mEntries = words.entrySet().iterator();

		Map<Integer, Integer> newwords = new TreeMap<Integer, Integer>();
		while (mEntries.hasNext()) {
		    Map.Entry<String, Integer> entry = mEntries.next();
		    
		    Integer word = entry.getValue();
            Integer count = newwords.get(word);
            newwords.put(word, (count == null) ? 1 : count + 1);
		  
		}
		System.out.println("-----"+newwords);
		
		/*for(int i=1;i<=50;i++){
			System.out.println("value of i : "+i+"-----"+Collections.frequency(words.values(), i));
		}*/
		
		/*if((1049)%1000 <=50){
			System.out.println("yeahhhhhhhhhh");
		}*/
		
	}

	
}

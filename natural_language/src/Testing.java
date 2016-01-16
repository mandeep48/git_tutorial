import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Testing {

	
	public static void main(String[] args) {
		
		Map<String, Integer> words = new HashMap<String, Integer>();
		
		words.put("a",10);
		words.put("b", 1);
		words.put("c", 1);
		words.put("d", 1);
		words.put("e", 1);
		
		Iterator<Map.Entry<String, Integer>> mEntries = words.entrySet().iterator();

		Map<Integer, Integer> newwords = new TreeMap<Integer, Integer>();
		while (mEntries.hasNext()) {
		    Map.Entry<String, Integer> entry = mEntries.next();
		    
		    Integer word = entry.getValue();
            Integer count = newwords.get(word);
            newwords.put(word, (count == null) ? 1 : count + 1);
		  
		}
		System.out.println("-----"+newwords);
		
		if((1049)%1000 <=50){
			System.out.println("yeahhhhhhhhhh");
		}
		
	}
}

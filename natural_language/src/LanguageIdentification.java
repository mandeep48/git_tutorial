
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * 
 * @author Mandeep Singh
 * Concordia ID: 27849559
 *
 */
public class LanguageIdentification {
	
	public static String trainingData = "simple-training-tweets.txt";
	public static String testingData = "simple-testing-tweets.txt";

	
	public static void writeOutputToFile(String content, String filename){
		
		try {
			 
			String givenFilename = filename;
			System.out.println("=== output is stored at === " + givenFilename);
			File file = new File(givenFilename);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {

		Map<String, UnigramModel> unigramModelMap = new HashMap<String,UnigramModel>();
		Map<String, BigramModel> bigramModelMap = new HashMap<String,BigramModel>();
		Scanner inputScanner=null;
		try {
			inputScanner = new Scanner(new File(trainingData));
			
			while(inputScanner.hasNext()){
				
				String inputStr = inputScanner.nextLine();
				String[] inputsplits = inputStr.split("\\s+",4);
		
				String language = inputsplits[2];
				String tweet = reduceDiacritics(inputsplits[3]);
				if(!unigramModelMap.containsKey(language)) {
					Map<Character, Integer> unigramCountMap = trainingUnigramLM(tweet, new HashMap<Character, Integer>());
					unigramModelMap.put(language, new UnigramModel(language, unigramCountMap));
				} else {
					UnigramModel unigramModel = unigramModelMap.get(language);
					Map<Character, Integer> unigramCountMap = trainingUnigramLM(tweet, unigramModel.getUniGramMap());
					unigramModel.setUniGramMap(unigramCountMap);
					unigramModelMap.put(language, unigramModel);
				
					
				}
				
				if(!bigramModelMap.containsKey(language)) {
					Map<String, Integer> bigramCountMap = trainingBigramLM(tweet, new HashMap<String, Integer>());
					bigramModelMap.put(language, new BigramModel(language, bigramCountMap));
				} else {
					BigramModel bigramModel = bigramModelMap.get(language);
					Map<String, Integer> bigramCountMap = trainingBigramLM(tweet, bigramModel.getBigramMap());
					bigramModel.setBigramMap(bigramCountMap);
					bigramModelMap.put(language, bigramModel);
				
					
				}
			}
			setUnigramModelParameters(unigramModelMap);
			displayUnigramModel(unigramModelMap);
			testUnigramLM(unigramModelMap);
			
			setBigramModelParameters(bigramModelMap);
			displayBigramModel(bigramModelMap);
			testBigramLM(bigramModelMap);
			
		
			
		}catch (FileNotFoundException e) {
			System.out.println("File is not present in the root directory");
			e.printStackTrace();
		}
	}
	
	
	public static Map<Character, Integer> trainingUnigramLM(String tweet, Map<Character, Integer> unigramCountMap) {
		for (char c : tweet.toCharArray()) {
			char key = Character.toLowerCase(c);
			if(Pattern.matches("[a-z]|[0-9]", key+"")) {
				if(unigramCountMap.containsKey(key)) {
					unigramCountMap.put(key, unigramCountMap.get(key) + 1);
				}
				else {
					unigramCountMap.put(key, 1);
				}
			}
		}
		return unigramCountMap;
	}
	
	public static Map<String, Integer> trainingBigramLM(String tweet, Map<String, Integer> bigramCountMap) {
		
		int len =tweet.length();
		

		for (int i = 0; i < len; i++)
		{	
			
			String key =null;
			
			if(!(i+1==len)){
				key=tweet.substring(i, i+2).toLowerCase();
				
			}else{
				key = tweet.substring(i).toLowerCase();
				
			}
			if(Pattern.matches("[a-z]+", key)){
			if(bigramCountMap.containsKey(key)) {
				bigramCountMap.put(key, bigramCountMap.get(key) + 1);
			}
			else {
				bigramCountMap.put(key, 1);
			}
			}
	}

		return bigramCountMap;
	}
	
	public static void setUnigramModelParameters(Map<String, UnigramModel> uniGramMap) {
		for (UnigramModel unigramModel : uniGramMap.values()) {
			unigramModel.setTokenCount();
			unigramModel.setSmoothedProbabilityMap();
			unigramModel.setUnsmoothedProbabilityMap();
		}
	}
	
	public static void setBigramModelParameters(Map<String, BigramModel> bigramMap) {
		for (BigramModel bigramModel : bigramMap.values()) {
			bigramModel.setTokenCount();
			bigramModel.setSmoothedProbabilityMap();
			bigramModel.setUnsmoothedProbabilityMap();
		}
	}
	
	
	
	public static void testUnigramLM(Map<String, UnigramModel> uniGramMap) {
		 List<String> correctAnswer = new ArrayList<String>();
		 List<String> systemAnswer = new ArrayList<String>();
		
		StringBuilder stringBuilder = new StringBuilder();
		Scanner inputScanner=null;
		try {
			inputScanner = new Scanner(new File(testingData));
			
			while(inputScanner.hasNext()){
				
				String inputStr = inputScanner.nextLine();
				String[] inputsplits = inputStr.split("\\s+",4);
				String language = inputsplits[2];
				String tweet = reduceDiacritics(inputsplits[3]);
				Map<String, Double> probabilityMap = new HashMap<String, Double>();
				for(Entry<String,UnigramModel> unigramModel : uniGramMap.entrySet()) {
					probabilityMap.put(unigramModel.getKey(), calculateProbabilityUnigram(tweet, unigramModel.getValue()));
				}
				
				List<Map.Entry<String, Double>> listProbabilityMap = new ArrayList<Map.Entry<String, Double>>(probabilityMap.entrySet());
				Collections.sort(listProbabilityMap, new Comparator<Map.Entry<String, Double>>()
				{
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				stringBuilder.append(inputsplits[0]+ "\t"+listProbabilityMap.get(0).getKey() + "\t"  + tweet + "\n");
				correctAnswer.add(language);
				systemAnswer.add(listProbabilityMap.get(0).getKey());
				
			}
 			writeOutputToFile(stringBuilder.toString(), "results-unigram.txt");
 			inputScanner.close();
 			analyzeResult(correctAnswer, systemAnswer, "analysis-unigram.txt");
		}catch (Exception e) {
		
			e.printStackTrace();
		}
	}
	
	public static void testBigramLM(Map<String, BigramModel> uniMap) {
		 List<String> correctAnswer = new ArrayList<String>();
		 List<String> systemAnswer = new ArrayList<String>();
		
		StringBuilder stringBuilder = new StringBuilder();
		Scanner inputScanner=null;
		try {
			inputScanner = new Scanner(new File(testingData));
			
			while(inputScanner.hasNext()){
				
				String inputStr = inputScanner.nextLine();
				String[] inputsplits = inputStr.split("\\s+",4);
				String language = inputsplits[2];
				String tweet = reduceDiacritics(inputsplits[3]);
				Map<String, Double> probabilityMap = new HashMap<String, Double>();
				for(Entry<String,BigramModel> uniModel : uniMap.entrySet()) {
					probabilityMap.put(uniModel.getKey(), calculateProbabilityBigram(tweet, uniModel.getValue()));
				}
				
				List<Map.Entry<String, Double>> listProbabilityMap = new ArrayList<Map.Entry<String, Double>>(probabilityMap.entrySet());
				Collections.sort(listProbabilityMap, new Comparator<Map.Entry<String, Double>>()
				{
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				stringBuilder.append(inputsplits[0]+ "\t"+listProbabilityMap.get(0).getKey() + "\t"  + tweet + "\n");
				correctAnswer.add(language);
				systemAnswer.add(listProbabilityMap.get(0).getKey());
			}
 			writeOutputToFile(stringBuilder.toString(), "results-bigram.txt");
 			inputScanner.close();
			analyzeResult(correctAnswer, systemAnswer, "analysis-bigram.txt");
		}catch (Exception e) {
		
			e.printStackTrace();
		}
	}
	
	public static void analyzeResult(List<String> correctAnswer,List<String> systemAnswer,String filename) {
		Map<String, Integer> correctResponse = new HashMap<String, Integer>();
		Map<String, Integer> totTweetLang = new HashMap<String, Integer>();
		String[] languages = {"eu", "ca", "gl", "es", "en", "pt"};
		List<String> languageList = new ArrayList<String>(Arrays.asList(languages));
		int[][] confusionMatrix = new int[6][6];
		int totalCorrect = 0;
		
		for (int i = 0; i < correctAnswer.size(); i++) {
			String m = correctAnswer.get(i);
			String n = systemAnswer.get(i);
				confusionMatrix[languageList.indexOf(m)][languageList.indexOf(n)] += 1;
				if (m.equals(n)) {
					if(!(correctResponse.containsKey(m))) {
						correctResponse.put(m, 1);
						
					} else {
						correctResponse.put(m, correctResponse.get(m)+1);
						totalCorrect++;
					}
				}
				if(totTweetLang.containsKey(m))
					totTweetLang.put(m, totTweetLang.get(m)+1);
				else
					totTweetLang.put(m, 1);
		}
		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Overall accuracy of the Language Model :: " + ((double)totalCorrect*100/correctAnswer.size()) + "\n");
			stringBuilder.append("------------Accuracy for each language---------------------\n");
		
			for (String language : languageList) {
				double accuracy = (correctResponse.get(language).doubleValue()*100)/totTweetLang.get(language);
				stringBuilder.append(String.format("%5s %25s\n", language,accuracy));
				}
			stringBuilder.append("---------------------Confusion Matrix----------------------\n");
			stringBuilder.append(String.format("%5s", "") + "\t");
			for (String string : languageList) {
				stringBuilder.append(String.format("%5s", string) + "\t");
			}
			stringBuilder.append("\n");
			for (int i = 0; i < confusionMatrix.length; i++) {
				stringBuilder.append(String.format("%5s", languages[i]) + "\t");
				
				for (int j = 0; j < confusionMatrix[i].length; j++) {
					stringBuilder.append(String.format("%5s", confusionMatrix[i][j]) + "\t");
					
				}
				stringBuilder.append("\n");
				
			}
			writeOutputToFile(stringBuilder.toString(), filename);
		
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static double calculateProbabilityUnigram(String tweet, UnigramModel uniModel) {
		double probability = 0;
		int count = 0;
		for (char c : tweet.toCharArray()) {
			c=Character.toLowerCase(c);
			if(uniModel.getSmoothedProbabilityMap().containsKey(c)) {
				probability += Math.log(uniModel.getSmoothedProbabilityMap().get(c));
				count++;
			}
		}
		return probability/count;
	}
	
	public static double calculateProbabilityBigram(String tweet, BigramModel bigramModel) {
		double probability = 0;
		int count = 0;
		
		for(int i=0;i<tweet.length();i++){
			String key = null;
			if(!(i+1==tweet.length())){
				key=tweet.substring(i, i+2).toLowerCase();
				
			}else{
				key = tweet.substring(i).toLowerCase();
				
			}
			if(bigramModel.getSmoothedProbabilityMap().containsKey(key)) {
				probability += Math.log(bigramModel.getSmoothedProbabilityMap().get(key));
				count++;
			}
		}
		
		return probability/count;
	}
	
	public static void displayUnigramModel(Map<String, UnigramModel> unigramModelMap) {
		try {
			
			StringBuilder stringBuilder = new StringBuilder();
			for (UnigramModel unigramModel : unigramModelMap.values()) {
				
				stringBuilder.append("------------------------Top 50 Unigrams for Language: " + unigramModel.getLanguage() + "------------------------------\n");
				stringBuilder.append(String.format("%10s  %10s  %10s %25s  %25s\n", "Unigram","Frequency","Rank","UnSmoothedProbability","SmoothedProbability"));
				Map<Character, Integer> unigramCountMap = unigramModel.getUniGramMap();
				List<Map.Entry<Character, Integer>> listUnigramCountMap = new ArrayList<Map.Entry<Character, Integer>>(unigramCountMap.entrySet());
				Collections.sort(listUnigramCountMap, new Comparator<Map.Entry<Character, Integer>>()
				{
					public int compare(Entry<Character, Integer> o1, Entry<Character, Integer> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				int i=1;
				for (Entry<Character, Integer> entry : listUnigramCountMap) {
					char ch = entry.getKey();
					stringBuilder.append(String.format("%10s  %10s %10s %25s  %25s\n", ch,unigramCountMap.get(ch),i++,unigramModel.getUnsmoothedProbabilityMap().get(ch),unigramModel.getSmoothedProbabilityMap().get(ch)));
				}
			
				
			}
			writeOutputToFile(stringBuilder.toString(), "unigramLM.txt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void displayBigramModel(Map<String, BigramModel> bigramModelMap) {
		try {
			
			StringBuilder stringBuilder = new StringBuilder();
			for (BigramModel bigramModel : bigramModelMap.values()) {
				
				stringBuilder.append("------------------------Top 50 Bigrams for Language: " + bigramModel.getLanguage() + "------------------------------\n");
				stringBuilder.append(String.format("%10s  %10s  %10s %25s  %25s\n", "Bigram","Frequency","Rank","UnSmoothedProbability","SmoothedProbability"));;
				Map<String, Integer> bigramCountMap = bigramModel.getBigramMap();
				Map<String, Double> smoothedProbabilityMap = bigramModel.getSmoothedProbabilityMap();
				List<Map.Entry<String, Double>> listProbMap = new ArrayList<Map.Entry<String, Double>>(smoothedProbabilityMap.entrySet());
				
				Collections.sort(listProbMap, new Comparator<Map.Entry<String, Double>>()
				{
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				int i=0;
				for (Entry<String, Double> entry : listProbMap) {
					String ch = entry.getKey();
					if (++i>50)
						break;
					stringBuilder.append(String.format("%10s  %10s %10s %25s  %25s\n", ch,bigramCountMap.get(ch),i,bigramModel.getUnsmoothedProbabilityMap().get(ch),smoothedProbabilityMap.get(ch)));
					}
				
				
			}
			writeOutputToFile(stringBuilder.toString(), "bigramLM.txt");
			
		} catch (Exception e) {
			System.out.print("error----");
			e.printStackTrace();
		}
	}
	public static String reduceDiacritics(String diacritics) {
		
		String temp = Normalizer.normalize(diacritics, Normalizer.Form.NFD);
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(temp).replaceAll("");
	}
}

class UnigramModel {
	public static final double DELTA = 0.1;
	private int tokenCount;
	private String language;
	private Map<Character, Integer> unigramCountMap;
	private Map<Character, Double> smoothedProbabilityMap;
	private Map<Character, Double> unsmoothedProbabilityMap;
	
	public UnigramModel(String language, Map<Character, Integer> uniGramMap) {
		super();
		this.language = language;
		this.unigramCountMap = uniGramMap;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Map<Character, Integer> getUniGramMap() {
		return unigramCountMap;
	}
	public void setUniGramMap(Map<Character, Integer> countMap) {
		this.unigramCountMap = countMap;
	}
	public int getTokenCount() {
		return tokenCount;
	}
	public void setTokenCount() {
		tokenCount = 0;
		for (Character ch : unigramCountMap.keySet()) {
			tokenCount += unigramCountMap.get(ch);
		}
	}
	public Map<Character, Double> getSmoothedProbabilityMap() {
		return smoothedProbabilityMap;
	}
	public void setSmoothedProbabilityMap() {
		smoothedProbabilityMap = new HashMap<Character, Double>();
		for (Character ch : unigramCountMap.keySet()) {
			
			double prob = (unigramCountMap.get(ch).doubleValue() + DELTA) / (tokenCount + (unigramCountMap.size()*DELTA));
			smoothedProbabilityMap.put(ch, prob);
		}
	}

	public Map<Character, Double> getUnsmoothedProbabilityMap() {
		return unsmoothedProbabilityMap;
	}

	public void setUnsmoothedProbabilityMap() {
		unsmoothedProbabilityMap = new HashMap<Character, Double>();
		for (Character ch : unigramCountMap.keySet()) {
			
			double probability = (unigramCountMap.get(ch).doubleValue() ) / (tokenCount );
			unsmoothedProbabilityMap.put(ch, probability);
		}
	}
}

class BigramModel {
	public static final double DELTA = 0.1;
	private String language;
	private int tokenCount;
	private Map<String, Integer> bigramCountMap;
	private Map<String, Double> smoothedProbabilityMap;
	private Map<String, Double> unsmoothedProbabilityMap;
	
	public BigramModel(String language, Map<String, Integer> bigramMap) {
		super();
		this.language = language;
		this.bigramCountMap = bigramMap;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Map<String, Integer> getBigramMap() {
		return bigramCountMap;
	}
	public void setBigramMap(Map<String, Integer> bigramMap) {
		this.bigramCountMap = bigramMap;
	}
	public int getTokenCount() {
		return tokenCount;
	}
	public void setTokenCount() {
		tokenCount = 0;
		for (String ch : bigramCountMap.keySet()) {
			tokenCount += bigramCountMap.get(ch);
		}
	}
	public Map<String, Double> getSmoothedProbabilityMap() {
		return smoothedProbabilityMap;
	}
	public void setSmoothedProbabilityMap() {
		smoothedProbabilityMap = new HashMap<String, Double>();
		for (String ch : bigramCountMap.keySet()) {
			
			double probability = (bigramCountMap.get(ch).doubleValue() + DELTA) / (tokenCount + (Math.pow(bigramCountMap.size(),2)*DELTA));
			smoothedProbabilityMap.put(ch, probability);
		}
	}

	public Map<String, Double> getUnsmoothedProbabilityMap() {
		return unsmoothedProbabilityMap;
	}

	public void setUnsmoothedProbabilityMap() {
		unsmoothedProbabilityMap = new HashMap<String, Double>();
		for (String ch : bigramCountMap.keySet()) {
			
			double probability = (bigramCountMap.get(ch).doubleValue() ) / (tokenCount );
			unsmoothedProbabilityMap.put(ch, probability);
		}
	}
}
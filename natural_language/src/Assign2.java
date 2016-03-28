/*

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;


public class Assign2 {
	static String inputPathTrainingCorpus = "training.txt";
	static String inputPathTestingCorpus = "testing.txt";
	static String outputPath = "";
	static List<String> gold = new ArrayList<String>();
	static List<String> res = new ArrayList<String>();
	
	public static void main(String[] args) {

		Map<String, UnigramModel> uniMap = new HashMap<String,UnigramModel>();
		try {
			FileReader fr = new FileReader(inputPathTrainingCorpus);
			BufferedReader br = new BufferedReader(fr);
			String inputStr;
			while ((inputStr = br.readLine()) != null) {
				String[] inputsplits = inputStr.split("\t");
				String lang = inputsplits[2];
				String tweet = normalize(inputsplits[3]);
				if(uniMap.containsKey(lang)) {
					UnigramModel uniModel = uniMap.get(lang);
					Map<Character, Integer> countMap = trainUnigramModel(tweet, uniModel.getCountMap());
					uniModel.setCountMap(countMap);
					uniMap.put(lang, uniModel);
				} else {
					Map<Character, Integer> countMap = trainUnigramModel(tweet, new HashMap<Character, Integer>());
					uniMap.put(lang, new UnigramModel(lang, countMap));
				}
			}
			countTotalTokensAndProbs(uniMap);
			printUniList(uniMap);
			testTweetsFindLang(uniMap, inputPathTestingCorpus);
			analyzeResult();
			fr.close();
			br.close();
		}catch (Exception e) {
			System.out.print("error----");
			e.printStackTrace();
		}
	}
	
	public static Map<Character, Integer> trainUnigramModel(String tweet, Map<Character, Integer> countMap) {
		for (char c : tweet.toCharArray()) {
			char key = Character.toLowerCase(c);
			if(key >= 97 && key<=122) {
				if(countMap.containsKey(key)) {
					countMap.put(key, countMap.get(key) + 1);
				}
				else {
					countMap.put(key, 1);
				}
			}
		}
		return countMap;
	}
	
	public static void countTotalTokensAndProbs(Map<String, UnigramModel> uniMap) {
		for (UnigramModel uniModel : uniMap.values()) {
			uniModel.setTotalTokens();
			uniModel.setProbMap();
		}
	}
	
	public static void calcProbs(Map<String, UnigramModel> uniMap) {
		for (UnigramModel uniModel : uniMap.values()) {
			uniModel.setTotalTokens();
		}
	}
	
	public static void testTweetsFindLang(Map<String, UnigramModel> uniMap, String inputPath) {
		try {
			FileReader fr = new FileReader(inputPath);
			BufferedReader br = new BufferedReader(fr);
			String inputStr;
			File file = new File(outputPath+"results-unigram.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
 			while ((inputStr = br.readLine()) != null) {
				String[] inputsplits = inputStr.split("\t");
				String lang = inputsplits[2];
				String tweet = normalize(inputsplits[3]);
				Map<String, Double> probLMsMap = new HashMap<String, Double>();
				for(Entry<String,UnigramModel> uniModel : uniMap.entrySet()) {
					probLMsMap.put(uniModel.getKey(), calcProbwithLM(tweet, uniModel.getValue()));
				}
				
				List<Map.Entry<String, Double>> listProbLMsMap = new ArrayList<Map.Entry<String, Double>>(probLMsMap.entrySet());
				Collections.sort(listProbLMsMap, new Comparator<Map.Entry<String, Double>>()
				{
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				bw.write(listProbLMsMap.get(0).getKey() + "\t" + "\"" + tweet + "\"\n");
				gold.add(lang);
				res.add(listProbLMsMap.get(0).getKey());
				//System.out.println(lang + "\t" + listProbLMsMap.get(0).getKey());
			}
			fr.close();
			br.close();
			bw.flush();
			fw.close();
			bw.close();
		}catch (Exception e) {
			System.out.print("error----");
			e.printStackTrace();
		}
	}
	
	public static void analyzeResult() {
		Map<String, Integer> correctRes = new HashMap<String, Integer>();
		Map<String, Integer> totTweetLang = new HashMap<String, Integer>();
		String[] langs = {"eu", "ca", "gl", "es", "en", "pt"};
		List<String> langList = new ArrayList<String>(Arrays.asList(langs));
		int[][] confMat = new int[langList.size()][langList.size()];
		int totalCorrect = 0;
		
		for (int i = 0; i < gold.size(); i++) {
			String g = gold.get(i);
			String r = res.get(i);
				confMat[langList.indexOf(g)][langList.indexOf(r)] += 1;
				if (g.equals(r)) {
					if(correctRes.containsKey(g)) {
						correctRes.put(g, correctRes.get(g)+1);
						totalCorrect++;
					} else 
						correctRes.put(g, 1);
				}
				if(totTweetLang.containsKey(g))
					totTweetLang.put(g, totTweetLang.get(g)+1);
				else
					totTweetLang.put(g, 1);
		}
		try {
			File file = new File(outputPath+"analysis-unigram.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Overall accuracy of the LMs : " + ((double)totalCorrect*100/gold.size()) + "\n");
			bw.write("Accuracy for each language : \n");
			for (String lang : langList) {
				double accuracy = correctRes.get(lang).doubleValue()*100/totTweetLang.get(lang);
				bw.write(lang + "\t" + accuracy + "\n");
			}
			
			bw.write(String.format("%5s", "") + "\t");
			for (String string : langList) {
				bw.write(String.format("%5s", string) + "\t");
			}
			bw.newLine();
			for (int i = 0; i < confMat.length; i++) {
				bw.write(String.format("%5s", langs[i]) + "\t");
				for (int j = 0; j < confMat[i].length; j++) {
					bw.write(String.format("%5s", confMat[i][j]) + "\t");
				}
				bw.newLine();
			}
			bw.flush();
			bw.close();
			fw.close();
			
		} catch(Exception e) {
			System.out.print("error----");
			e.printStackTrace();
		}
		
	}
	
	public static double calcProbwithLM(String tweet, UnigramModel uniModel) {
		double prob = 0;
		int count = 0;
		for (char c : tweet.toCharArray()) {
			if(uniModel.getProbMap().containsKey(c)) {
				prob += Math.log(uniModel.getProbMap().get(c));
				count++;
			}
		}
		return prob/count;
	}
	
	public static void printUniList(Map<String, UnigramModel> uniMap) {
		try {
			File file = new File(outputPath+"unigramLM.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (UnigramModel uniModel : uniMap.values()) {
				bw.write("#############Language: " + uniModel.getLang() + "######################\n");
				Map<Character, Integer> countMap = uniModel.getCountMap();
				Map<Character, Double> probMap = uniModel.getProbMap();
				List<Map.Entry<Character, Double>> listProbMap = new ArrayList<Map.Entry<Character, Double>>(probMap.entrySet());
				
				Collections.sort(listProbMap, new Comparator<Map.Entry<Character, Double>>()
				{
					public int compare(Entry<Character, Double> o1, Entry<Character, Double> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				for (Entry<Character, Double> entry : listProbMap) {
					char ch = entry.getKey();
					bw.write(ch + "\t" + countMap.get(ch) + "\t" + probMap.get(ch) + "\n");
				}
				bw.write("Total Tokens for language (" + uniModel.getLang() +") : " + uniModel.getTotalTokens() + "\n");
			}
			bw.flush();
			fw.close();
			bw.close();
		} catch (Exception e) {
			System.out.print("error----");
			e.printStackTrace();
		}
	}
	
	public static String normalize(String str) {
		String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(temp).replaceAll("");
	}
}

class UnigramModel {
	public static final double DELTA = 0.1;
	private String lang;
	private Map<Character, Integer> countMap;
	private int totalTokens;
	private Map<Character, Double> probMap;
	
	public UnigramModel(String lang, Map<Character, Integer> countMap) {
		super();
		this.lang = lang;
		this.countMap = countMap;
	}
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public Map<Character, Integer> getCountMap() {
		return countMap;
	}
	public void setCountMap(Map<Character, Integer> countMap) {
		this.countMap = countMap;
	}
	public int getTotalTokens() {
		return totalTokens;
	}
	public void setTotalTokens() {
		totalTokens = 0;
		for (Character ch : countMap.keySet()) {
			totalTokens += countMap.get(ch);
		}
	}
	public Map<Character, Double> getProbMap() {
		return probMap;
	}
	public void setProbMap() {
		probMap = new HashMap<Character, Double>();
		for (Character ch : countMap.keySet()) {
			
			double prob = (countMap.get(ch).doubleValue() + DELTA) / (totalTokens + (countMap.size()*DELTA));
			probMap.put(ch, prob);
		}
	}
}
*/
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class SentimentalAnalysis {
	static List<String> gold = new ArrayList<String>();
	static List<String> res = new ArrayList<String>();
	
	public static void main(String[] args) {
		String inputFolderPos = "D:\\workspace\\SentimentalAnalysis\\review_polarity\\txt_sentoken\\pos";
		String inputFolderNeg = "D:\\workspace\\SentimentalAnalysis\\review_polarity\\txt_sentoken\\neg";
		String inputTestFolder = "p2-sample-output.txt";
		
		Map<String, LanguageModel> modelMap = new HashMap<String, LanguageModel>();
		LanguageModel posModel = new LanguageModel("pos", new HashMap<String, Integer>(), inputFolderPos);
		LanguageModel negModel = new LanguageModel("neg", new HashMap<String, Integer>(), inputFolderNeg);
		modelMap.put("pos", posModel);
		modelMap.put("neg", negModel);
		try {
			
			buildModel(modelMap);

			testTweetsFindLang(modelMap, inputTestFolder);
			
			analyzeResult();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void buildModel( Map<String, LanguageModel> modelMap){
		for (Map.Entry<String, LanguageModel> entry : modelMap.entrySet()){	
			File folder = new File(entry.getValue().getInputPath());
			for (final File fileEntry : folder.listFiles()) {
				String regex = "[^A-Za-z]+";
				
		        // Data Structure used to store the words 
		        
		
				Scanner inputScanner=null;
				try {
					inputScanner = new Scanner(fileEntry);
				} catch (FileNotFoundException e) {
					System.out.println("File is not present in the root directory");
					e.printStackTrace();
				}
				inputScanner.useDelimiter(regex);
				while (inputScanner.hasNext()) {
					String token = inputScanner.next().toLowerCase();
					Integer count = entry.getValue().getCountMap().get(token);
					entry.getValue().getCountMap().put(token, (count == null) ? 1 : count + 1);
				}
		       
		        
			}
			// Sort the wordMap
			ArrayList<Map.Entry<String, Integer>> wordList = new ArrayList<Map.Entry<String, Integer>>(entry.getValue().getCountMap().entrySet());
			Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
					return (e1.getValue() == e2.getValue()) ? e1.getKey().compareTo(e2.getKey())
							: e2.getValue() - e1.getValue();
				}
			});
			for(Entry<String, Integer> entry1 : wordList) {
				if(entry1.getValue() > 500 || entry1.getValue() < 5) {
					System.out.println(entry1.getKey() + " + " + entry1.getValue());
					entry.getValue().getCountMap().remove(entry1.getKey());
					
				}
				
			}
			entry.getValue().setTotalTokens();
			entry.getValue().setProbMap();
			//System.out.println(model.getTotalTokens());
			
			/*for(Map.Entry<String, Double> entry : model.getProbMap().entrySet()) {
				System.out.println(entry.getKey() + " + " + entry.getValue());
			}*/
		}
	}
	
	public static ArrayList<Map.Entry<String, Integer>>  sortMap(Map<String, Integer> wordMap){
		ArrayList<Map.Entry<String, Integer>> wordList = new ArrayList<Map.Entry<String, Integer>>(wordMap.entrySet());
		Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
				return (e1.getValue() == e2.getValue()) ? e1.getKey().compareTo(e2.getKey())
						: e2.getValue() - e1.getValue();
			}
		});
		
		return wordList;
	}
	
	public static void testTweetsFindLang(Map<String, LanguageModel> modelMap, String inputPath) {
		File folderPos = new File("D:\\workspace\\SentimentalAnalysis\\review_polarity\\txt_sentoken\\test\\pos");
		File folderNeg = new File("D:\\workspace\\SentimentalAnalysis\\review_polarity\\txt_sentoken\\test\\neg");
		try {
			 for (File fileEntry : folderPos.listFiles()) {
				Scanner inputScanner=null;
				try {
					inputScanner = new Scanner(fileEntry);
				} catch (FileNotFoundException e) {
					System.out.println("File is not present in the root directory");
					e.printStackTrace();
				}
				String review = new String();
				inputScanner.useDelimiter("\\Z");
				review = inputScanner.next().toLowerCase();
				
				Map<String, Double> probLMsMap = new HashMap<String, Double>();
				for(Entry<String,LanguageModel> model : modelMap.entrySet()) {
					probLMsMap.put(model.getKey(), calcProbwithLM(review, model.getValue()));
				}
				
				List<Map.Entry<String, Double>> listProbLMsMap = new ArrayList<Map.Entry<String, Double>>(probLMsMap.entrySet());
				Collections.sort(listProbLMsMap, new Comparator<Map.Entry<String, Double>>()
				{
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				
				gold.add("pos");
				res.add(listProbLMsMap.get(0).getKey());
			 }
			
			 for (File fileEntry : folderNeg.listFiles()) { 
				Scanner inputScanner=null;
				try {
					inputScanner = new Scanner(fileEntry);
				} catch (FileNotFoundException e) {
					System.out.println("File is not present in the root directory");
					e.printStackTrace();
				}
				String review = new String();
				inputScanner.useDelimiter("\\Z");
				review = inputScanner.next().toLowerCase();
				
				Map<String, Double> probLMsMap = new HashMap<String, Double>();
				for(Entry<String,LanguageModel> model : modelMap.entrySet()) {
					probLMsMap.put(model.getKey(), calcProbwithLM(review, model.getValue()));
				}
				
				List<Map.Entry<String, Double>> listProbLMsMap = new ArrayList<Map.Entry<String, Double>>(probLMsMap.entrySet());
				Collections.sort(listProbLMsMap, new Comparator<Map.Entry<String, Double>>()
				{
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						return (o2.getValue().compareTo(o1.getValue()));
					}				
				});
				
				gold.add("neg");
				res.add(listProbLMsMap.get(0).getKey());
			 }
		}catch (Exception e) {
			System.out.print("error----");
			e.printStackTrace();
		}
	}
	
	public static double calcProbwithLM(String review, LanguageModel model) {
		double prob = 0;
		String[] reviewWords = review.split("\\s");
		for (int i = 0; i < reviewWords.length - 1; i++) {
			if(model.getProbMap().containsKey(reviewWords[i])) {
				prob += Math.log(model.getProbMap().get(reviewWords[i]));
			}
		}
		return prob;
	}
	
	public static void analyzeResult() {
		Map<String, Integer> correctRes = new HashMap<String, Integer>();
		Map<String, Integer> totTweetLang = new HashMap<String, Integer>();
		String[] polarities = {"pos", "neg"};
		List<String> polarityList = new ArrayList<String>(Arrays.asList(polarities));
		int[][] confMat = new int[polarityList.size()][polarityList.size()];
		int totalCorrect = 0;
		
		for (int i = 0; i < gold.size(); i++) {
			String g = gold.get(i);
			String r = res.get(i);
				confMat[polarityList.indexOf(g)][polarityList.indexOf(r)] += 1;
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
		for(String lang : polarityList) {
			if(!correctRes.containsKey(lang)) {
				correctRes.put(lang, 0);
			}
		}
		try {
			File file = new File("analysis-bigram.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Overall accuracy of the LMs: " + ((double)totalCorrect*100/gold.size()) + "\n");
			
			bw.newLine();
			bw.write("Accuracy for each Language: \n");
			for (String lang : polarityList) {
				double accuracy = correctRes.get(lang).doubleValue()*100/totTweetLang.get(lang);
				bw.write(lang + "\t" + accuracy + "\n");
			}
			bw.newLine();
			
			bw.write("Confusion Matrix");
			bw.newLine();
			bw.write(String.format("%5s", "") + "\t");
			for (String string : polarityList) {
				bw.write(String.format("%5s", string) + "\t");
			}
			bw.newLine();
			for (int i = 0; i < confMat.length; i++) {
				bw.write(String.format("%5s", polarities[i]) + "\t");
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
}


class LanguageModel {
	public static final double DELTA = 0.1;
	private String polarity;
	private List<Character> vocabList;
	private Map<String, Integer> countMap;
	private int totalTokens;
	private Map<String, Double> probMap;
	private Map<String, Double> probUnsmoothedMap;
	private String inputPath;
	
	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public LanguageModel(String lang, Map<String, Integer> countMap, String inputPath) {
		super();
		this.polarity = lang;
		this.countMap = countMap;
		this.inputPath = inputPath;
	}
	
	public String getLang() {
		return polarity;
	}
	public void setLang(String lang) {
		this.polarity = lang;
	}
	public Map<String, Integer> getCountMap() {
		return countMap;
	}
	public void setCountMap(Map<String, Integer> countMap) {
		this.countMap = countMap;
	}
	public int getTotalTokens() {
		return totalTokens;
	}
	public List<Character> getVocabList() {
		return vocabList;
	}

	public void setVocabList(List<Character> vocabList) {
		this.vocabList = vocabList;
	}

	public void setTotalTokens() {
		totalTokens = 0;
		for (String ch : countMap.keySet()) {
			totalTokens += countMap.get(ch);
		}
	}
	public Map<String, Double> getProbMap() {
		return probMap;
	}
	public void setProbMap() {
		probMap = new HashMap<String, Double>();
		for (String ch : countMap.keySet()) {	
			double prob = (countMap.get(ch).doubleValue() + DELTA) / (totalTokens + (countMap.size()*countMap.size()*DELTA));
			probMap.put(ch, prob);
		}
	}
	public Map<String, Double> getUnsmoothedMap() {
		return probUnsmoothedMap;
	}
	public void setUnsmoothedMap() {
		probUnsmoothedMap = new HashMap<String, Double>();
		for (String ch : countMap.keySet()) {
			double prob = (countMap.get(ch).doubleValue()) / (totalTokens);
			probUnsmoothedMap.put(ch, prob);
		}
	}
}
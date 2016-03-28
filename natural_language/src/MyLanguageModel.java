
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;




public class MyLanguageModel {
	
	public static int tokenSizeInFile=0; // to count total number of words in the file
	public static int totalVocab=0;
	public static String contentForUniFile = "\n"; 
	public static HashMap<String,Double> tempUniHash = new HashMap<String,Double>();
	public static HashMap UniHashForProbSum=new HashMap();
	public static HashMap BiHashForProbSum=new HashMap();
	public static HashMap HashForAlpha = new HashMap();
	public static String outputPath;
	/*public static double calculateProbability(double val,double totalSize){
		
		double x= Math.log(val)-Math.log(totalSize);
		
		return Math.exp(x);
	}*/
	
	
	public static void writeOutputToFile(String content, String filename){
		
		try {
			 
			String givenFilename = outputPath+filename;
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
	
	public static void testingData(String testfilename,HashMap uniHash, HashMap bigramHash ){
		
		String line,S1,S2,S3;
		String testContent = "\n";
		Double num1=0.0,num2=0.0,logv=0.6,num3=0.0,num4=0.0;
		int countToken=0,unseencount=0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testfilename));
		
				
				while ((line = reader.readLine()) != null) {
						
					String[] input = line.split("\\s+");
										
					//Performing Bi-gram count
					for(int i=0;i<input.length-1;i++){
						//Increment word count as a word is seen
						//s3=s3+input;
						S1=input[i];
						S2=input[i+1];
						S3=S1+" "+S2;
						countToken++;
						
						if(bigramHash.containsKey(S3)){
							num1 = num1 + (Double)((Math.log((Double)bigramHash.get(S3)))/logv);
							
						}
						else{
							unseencount++;
							if((Double)HashForAlpha.get(S1)>0){
								num2 = (Double)HashForAlpha.get(S1);
								} else num2 = 0.7047;
							if((Double)uniHash.get(S2) > 0){
								num3 = (Double)uniHash.get(S2);
							} else num3 = 0.3;
							
							num3 =  num3*num2;
							num3 =  (Double)((Math.log(num3)));
							
							num4 = num4+num3;
							
						}
							
						} 
				}
						
		}
		catch(IOException e){
			System.out.println(e);
		}
		
		Double tempVal = num1+num4;
		tempVal = (tempVal)/countToken;
		tempVal = -1 * tempVal;
		
		Double perplexityValue = Math.pow(2,tempVal);
		System.out.println("Perplexity Value is : " + perplexityValue);
		System.out.println("=============  All Done   ==============");
		
	}
	
	public static Double unsmoothedProb(double value){
		
		Double prob = value/tokenSizeInFile;
		return prob;
		
	}
	
	public static Double smoothed(double value){
		
		Double prob = (value+0.1)/(tokenSizeInFile+totalVocab*0.1);
		
		return prob;
	}
	public static HashMap countUnigram(String inFilename){
		
		
		String line;
		String content="\n";
		
		 //to count vocabulary size in the file
		int instancecount=0;
		int lineCount=0;
		HashMap<String,Double> uniHash = new HashMap<String,Double>();
		
			
			Scanner inputScanner=null;
			try {
				inputScanner = new Scanner(new File(inFilename));
			} catch (FileNotFoundException e) {
				System.out.println("File is not present in the root directory");
				e.printStackTrace();
			}
			inputScanner.useDelimiter("[^A-Za-z]+");
			while (inputScanner.hasNext()) {
				String token = inputScanner.next().toLowerCase();
				tokenSizeInFile++;
				if(uniHash.containsKey(token)){
					Double total = ((Double)uniHash.get(token));
					total++;
					uniHash.put(token, total);
				}
				else {
					 // Increment totalVocab value when a new word is seen (unique words)
					totalVocab++; 
					uniHash.put(token,new Double(1.0));
				}
				//Double count = uniHash.get(token);
				//uniHash.put(token, (count == null) ? 1 : count + 1);
			}
			//BufferedReader reader = new BufferedReader(new FileReader(inFilename));
		
				
					

				 // End of while
				
				
				
				System.out.println("=== Calculating Probability for unigrams ===");
				
				//Enumeration enumeration = wordLM.keys();
				/*double sum=0;
				Collection<?> keys = uniHash.keySet();
				for ( Object key : keys) {
					
				    Double value = ((Double)uniHash.get(key));
				
				    Double probValue=(Double)(value/tokenSizeInFile);
				    content=content+ "\t " +probValue+ "\t " + key+"\n";
				    tempUniHash.put(key.toString(),probValue);
				    sum+=probValue;
				  
				}*/
				ArrayList<Map.Entry<String,Double>> wordList = new ArrayList<Map.Entry<String,Double>>(uniHash.entrySet());
				Collections.sort(wordList, new Comparator<Map.Entry<String,Double>>() {
					public int compare(Map.Entry<String,Double> e1, Map.Entry<String,Double> e2) {
						return (e1.getValue() == e2.getValue()) ? e1.getKey().compareTo(e2.getKey())
								: Double.compare(e2.getValue(), e1.getValue()) ;
					}
				});
				int counter=0;
				for (Map.Entry<String, Double> entry : wordList) {
					if (++counter > 50)
						break;
					System.out.println(entry.getKey()+" "+ entry.getValue()+ " "+unsmoothedProb(entry.getValue()) + "----"+smoothed(entry.getValue()));
					//System.out.println("Count is"+ counter+" key is "+entry.getKey() +" value is "+entry.getValue());
					//System.out.printf("%15s  %12d  %12d  %12d\n", entry.getKey(), entry.getValue(), counter);
					
				}
				//Write to the file
				//writeOutputToFile(content,"Unigram-Output.txt");

				
				
				
				
		
		//finFilename.close();
		System.out.println("Value of Vocabulary" + totalVocab);
		System.out.println("Token Size" + tokenSizeInFile);
		System.out.println("Size of Uni Hash" + uniHash.size());
		contentForUniFile += "Number of Unique strings is :\t " + totalVocab + "\n";
		contentForUniFile += "Total number of words or token size : \t" + tokenSizeInFile + "\n";
		return uniHash;
		
		
		
	} // End of function countUnigram
	
	
	
	
	
	
	public static HashMap countBigram(String inFilename, HashMap uniHash){
		
		int lineCount=0;
		int instancecount=0;
		String line;
		int tokenSizeInFile=0;
		int totalVocab=0;
		int countForSplit=0;
		String S3=null;
		String S2=null;
		String S1=null;
		int bigramOnce=1;
		int bigramTwice=1;
		String content="\n";
		
		HashMap<String,Double> twoGramWordLM = new HashMap<String,Double>();
		//HashMap tempBiHash=new HashMap();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inFilename));
		
				instancecount++;
				while ((line = reader.readLine()) != null) {
						
					String[] input = line.split("[^A-Za-z]+");
										
					//Performing Bi-gram count
					for(int i=0;i<input.length-1;i++){
						//Increment word count as a word is seen
						//s3=s3+input;
						S1=input[i];
						S2=input[i+1];
						S3=S1+" "+S2;
						
						//tokenSizeInFile++;
						
						//Insert BI-Gram into Hash
						if(twoGramWordLM.containsKey(S3)){
							double total = ((Double)twoGramWordLM.get(S3));
							total++;
							twoGramWordLM.put(S3, total);
							
							
						}
						else {
							 // Increment totalVocab value when a new word is seen
							totalVocab++; 
							twoGramWordLM.put(S3,new Double(1.00));
							
						}
					} //End of input for loop
					lineCount++;
					//}

				} // End of while
			
				System.out.println("Size of unique Bigram words : " + totalVocab );
				/*for (Map.Entry<String, Double> entry : twoGramWordLM.entrySet()) {
					System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				}*/
				
				
				
				
				
		} // End of try
		catch(IOException e){
			System.out.println(e);
		}
		
		//calculateAlphaValue(inFilename,uniHash, twoGramWordLM);
		return twoGramWordLM;
		
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String trainFile, testFile;
		
		
		//Scanner a = new Scanner(System.in);
		int instancecount = 0;
		
		/*if(args.length!=3){
			System.out.println("=====Wrong format for input =====\n Please run as");
			System.out.println("java class-file-training_file-with-path test-file-with-path output-file-path ");
			System.out.println("Example:-  java lm C:\\Drive-N\\test.txt C:\\Drive-N\\train.txt C:\\Drive-N\\");
			
		}*/
		String inFilename ="corpus_sports.txt";
		//String testfilename = args[1];
		//outputPath = args[2];
		
		HashMap uniHash = countUnigram(inFilename);
		HashMap bigramHash = countBigram(inFilename,uniHash);
		//testingData(testfilename,uniHash,bigramHash);
		
	} // End of main
} // End of class


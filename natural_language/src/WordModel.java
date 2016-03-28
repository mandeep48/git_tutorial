
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;




public class WordModel {
	
	public static int tokenSizeInFile=0; // to count total number of words in the file
	public static String contentForUniFile = "\n"; 
	public static HashMap tempUniHash = new HashMap();
	public static HashMap UniHashForProbSum=new HashMap();
	public static HashMap BiHashForProbSum=new HashMap();
	public static HashMap HashForAlpha = new HashMap();
	public static String outputPath;
	public static double calculateProbability(double val,double totalSize){
		
		double x= Math.log(val)-Math.log(totalSize);
		
		return Math.exp(x);
	}
	
	
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
	public static HashMap countUnigram(String inFilename){
		
		
		String line;
		String content="\n";
		
		int totalVocab=0; //to count vocabulary size in the file
		int instancecount=0;
		int lineCount=0;
		HashMap uniHash = new HashMap();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inFilename));
		
				instancecount++;
				while ((line = reader.readLine()) != null) {
					String[] input = line.split("\\s+");
					
					//Performing Unigram count
					
					for(int i=0;i<input.length;i++){
						//Increment word count as a word is seen
						tokenSizeInFile++;
						if(uniHash.containsKey(input[i])){
							Double total = ((Double)uniHash.get(input[i]));
							total++;
							uniHash.put(input[i], total);
						}
						else {
							 // Increment totalVocab value when a new word is seen (unique words)
							totalVocab++; 
							uniHash.put(input[i],new Double(1.0));
						}
					}
					lineCount++;
					

				} // End of while
				
				
				
				System.out.println("=== Calculating Probability for unigrams ===");
				
				//Enumeration enumeration = wordLM.keys();
				double sum=0;
				Collection<?> keys = uniHash.keySet();
				for ( Object key : keys) {
					
				    Double value = ((Double)uniHash.get(key));
				
				    Double probValue=(Double)(value/tokenSizeInFile);
				    content=content+ "\t " +probValue+ "\t " + key+"\n";
				    tempUniHash.put(key,probValue);
				    sum+=probValue;
				  
				}
				
				
				//Write to the file
				writeOutputToFile(content,"Unigram-Output.txt");

				
				
				
				
		} // End of try
		catch(IOException e){
			System.out.println(e);
		}
		//finFilename.close();
		System.out.println("Value of Vocabulary" + totalVocab);
		System.out.println("Token Size" + tokenSizeInFile);
		System.out.println("Size of Uni Hash" + uniHash.size());
		contentForUniFile += "Number of Unique strings is :\t " + totalVocab + "\n";
		contentForUniFile += "Total number of words or token size : \t" + tokenSizeInFile + "\n";
		return uniHash;
		
		
		
	} // End of function countUnigram
	
	
	public static void calculateAlphaValue(String inFilename,HashMap uniHash, HashMap twoGramWordLM){
		
		String content="\n";
		int tempCount=0;
		
		/*try {
			BufferedReader reader = new BufferedReader(new FileReader(inFilename));*/
		
				int instancecount=0;
				instancecount++;
				String line,S1;
				Double alphaValue =0.0;
				String S2,S3,leftString="",rightString="";
				
				/*while ((line = reader.readLine()) != null) {
					tempCount++; 	
					String[] input = line.split("\\s+");*/
										
					//Performing Bi-gram count
					Collection<?> keys = uniHash.keySet();
					for ( Object key : keys) {
					    S1 = (String) key;
						//S1 = (String)uniHash.get(key);
						
						//S3=S1+" "+S2;
						
						//tokenSizeInFile++;
						
						/* Check if alphaValue has been already calculated?
						 * If the string exists in Hash - HashForAlpha then no need to perform 
						 * below operations
						 */
					  if(!(HashForAlpha.containsKey(S1))){
						 
						//Insert BI-Gram into Hash
						double probSumForNum=0.0;
						double probSumForDen=0.0;
						
						//Try to get the keys in an array and the split and jump directly ???
						 
						probSumForNum = (Double)BiHashForProbSum.get(S1);
						probSumForDen = (Double)UniHashForProbSum.get(S1);
						
						//alphaValue = calculateProbability((1-probSumForNum),(1-probSumForDen));
						alphaValue = (Double)((1-probSumForNum)/(1-probSumForDen));	
						contentForUniFile += "\n" + ((Double)tempUniHash.get(S1)) +S1 + " :\t" +  " :\t" + alphaValue + "\n"; 
					        
							/* Replace uniword count with alphaValue - Since we do not need uni count anymore, we can use
							 * same hash for storing alpha value and use for calculating perplexity  
							 */
							
							HashForAlpha.put(S1, alphaValue);
							
							
					 } // End of "if" to check if alpha value has been calculated or not
					       
					}  	// End of outer for loop for reading key string
				
				
				//}  // End of while - to raed many line from the file 	
					
			
		
		
				System.out.println("Size of Alpha Hash" + HashForAlpha.size());
		//} // End of try block
		
		
		writeOutputToFile(contentForUniFile,"OutputUniProbAlpha.txt");
		
		
		
	} // End of method
	
	
	
	
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
		
		HashMap twoGramWordLM = new HashMap();
		//HashMap tempBiHash=new HashMap();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inFilename));
		
				instancecount++;
				while ((line = reader.readLine()) != null) {
						
					String[] input = line.split("\\s+");
										
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
				
				System.out.println("=== Calculating probability for Bigrams ===");
				
				/* Run through hash and read all the values which are one or two
				 * and if value is one - increase bigramOnce=N1 and if value is two
				 * increase bigramTwice=N2
				 */
				int k=0;
				Collection<?> keys = twoGramWordLM.keySet();
				for ( Object key : keys) {
					
					
					if((Double)twoGramWordLM.get(key)==1){
						bigramOnce++;
					}
					if((Double)twoGramWordLM.get(key)==2){
						bigramTwice++;
					}
					
				} // End of loop - value from hash
				
				try{
					Thread.sleep(5000);
				}
				catch(InterruptedException ie){
				}
				
				double sum=0;
				int scount=0;
				double probValue;
				//int i=0;
				
				/* To perform BIGRAM LM, we can scan through the given file and take combination of two words and 
				 * check inside HASH. If count is zero, calculate alpha value and perform back-off if required.
				 * If count is 1, apply Good Turing method and if count is >1, then apply MLE
				 */
				
				
				
				
				
				
				for (Object key:keys) {
					
					Double valueOfTwo=0.0;
					String str = (String)key;
				    
					String [] tempString = str.split("\\s+");
				    String singleString=tempString[0];
				    String nextString = tempString[1];
				    
				    valueOfTwo = ((Double)twoGramWordLM.get(str));
				    Double valueOfOne = ((Double)uniHash.get(singleString));
				    
				    
				    
				    // We can place sum of probability of all second string appearing with first string
				    if(valueOfTwo==1.0){
				    	/* This means that bigram has occurred only once hence do not calculate ML probability for it
				    	 * instead use good Turing estimate - PGT(w|h)=2*N2/(N1*C(h))
				    	 */
				    	int tempNumerator = 2*bigramTwice;
				    	double tempDenominator= bigramOnce*valueOfOne;
				    	//probValue=calculateProbability(tempNumerator,tempDenominator);
				    	probValue=(Double)(tempNumerator/tempDenominator);
				    	content=content+probValue+"\t\t:\t"+str+"\n";
				    	twoGramWordLM.put(str,(Double)probValue);
				    	
				    	if(BiHashForProbSum.containsKey(singleString)){
				    		double total = ((Double)BiHashForProbSum.get(singleString));
							total = total+ probValue;
							BiHashForProbSum.put(singleString,total);
				    		
				    	}
				    	else{
				    		BiHashForProbSum.put(singleString,probValue);
				    	}
				    	if(UniHashForProbSum.containsKey(singleString)){
				    		double total = ((Double) BiHashForProbSum.get(singleString));
				    		total = total + ((Double)tempUniHash.get(nextString));
				    		UniHashForProbSum.put(singleString,total);
				    	}
				    	else{
				    		UniHashForProbSum.put(singleString,((Double)tempUniHash.get(nextString)));
				    	}
				    }
				    else if(valueOfTwo>1.0){
				    	
				    //double probValue=(double)(value/tokenSizeInFile);
				    //probValue=calculateProbability(valueOfTwo,valueOfOne);
				    probValue=(Double)(valueOfTwo/valueOfOne);
				    //Multiplying by gamma value i.e 0.85
				    probValue = 0.99*probValue;
				    content=content+str+"\t\t:\t"+probValue+"\n";
				    //sum+=probValue;
				    
				    if(BiHashForProbSum.containsKey(singleString)){
			    		double total = ((Double)BiHashForProbSum.get(singleString));
						total = total+ probValue;
						BiHashForProbSum.put(singleString,total);
			    		
			    	}
			    	else{
			    		BiHashForProbSum.put(singleString,probValue);
			    	}
				    
				    if(UniHashForProbSum.containsKey(singleString)){
			    		double total = ((Double) tempUniHash.get(nextString));
			    		total = total + ((Double)tempUniHash.get(nextString));
			    		UniHashForProbSum.put(singleString,total);
			    	}
			    	else{
			    		UniHashForProbSum.put(singleString,((Double)tempUniHash.get(nextString)));
			    	}
				    twoGramWordLM.put(str,(Double)probValue);
				    
				    
				    }
				    
				}
				
				
				
				
				
				Double total = 0.05;
				BiHashForProbSum.put("</s>",total);
				total = 0.075;
				UniHashForProbSum.put("</s>",total);
				writeOutputToFile(content,"Bigram-Output.txt");
				System.out.println("===Size of UniHashForProbSum " + UniHashForProbSum.size());
				System.out.println("===Size of BiHashForProbSum " + BiHashForProbSum.size());
				
				
		} // End of try
		catch(IOException e){
			System.out.println(e);
		}
		
		calculateAlphaValue(inFilename,uniHash, twoGramWordLM);
		return twoGramWordLM;
		
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String trainFile, testFile;
		
		
		//Scanner a = new Scanner(System.in);
		int instancecount = 0;
		
		if(args.length!=3){
			System.out.println("=====Wrong format for input =====\n Please run as");
			System.out.println("java class-file-training_file-with-path test-file-with-path output-file-path ");
			System.out.println("Example:-  java lm C:\\Drive-N\\test.txt C:\\Drive-N\\train.txt C:\\Drive-N\\");
			
		}
		String inFilename = args[0];
		String testfilename = args[1];
		outputPath = args[2];
		
		HashMap uniHash = countUnigram(inFilename);
		HashMap bigramHash = countBigram(inFilename,uniHash);
		testingData(testfilename,uniHash,bigramHash);
		
	} // End of main
} // End of class


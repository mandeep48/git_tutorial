import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TestPosTagger {

	
	public static void main(String[] args) {
		String a = "I like watching movies.";
		MaxentTagger tagger =  new MaxentTagger("taggers/english-left3words-distsim.tagger");
		

		//tagger.get
		//TTags a1 = tagger.getTags();
		//System.out.println(a1);
		String tagged = tagger.tagString(a);
		
		String n = tagger.tagTokenizedString(a);
		System.out.println(n+"------");
		System.out.println(tagged);
	}
}

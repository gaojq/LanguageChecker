package GrammarCheck;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 // the trie structure which saves the word and word formation

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import SpellSearch.Dictionary;

/*@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "action")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Counter.class, name = "NgramCount")
})*/

public class BuildRule {
	@JsonIgnore
	public Set<String> trainSample;// train sentence
	public Set<String> RuleSet; // How Many rule(word formation)
    public int n; // Number of N-gram
    
    @JsonSerialize(as = Counter.class)
    public Counter NgramCount; 
    public double trainNum; // how many ngram model
    int testing = 0;
    @JsonIgnore 
    public Dictionary root;
    public final String[] formation = {"PRP","LS","NN"};
    
    public static class Counter
    {
        public int depth;
        public HashMap<String, Counter> map;
        public double count;
        public double gtcount;
        public Counter(int depth)
        {
            this.depth = depth;
            if (depth == 0) {
                this.map = null;
                this.count = 0.0;
            } else {
                this.map = new HashMap<String, Counter>();
            }
        }
        
        public void insert(String[] ngram)
        {
        	
            if (depth == 1) {
                count++;
            }
            if (depth == 0) {
                count++;
                return;
            }
            
            Counter next;
            if (map.containsKey(ngram[ngram.length-depth])) {
                next = map.get(ngram[ngram.length-depth]); // get next level
            } else {
                next = new Counter(depth-1);
                map.put(ngram[ngram.length-depth], next);
            }
            next.insert(ngram);
            return;
        }
        
        public double wordCount(String[] ngram)
        {
        	if (depth == 1) {
                return count;
            }
            
            return map.containsKey(ngram[ngram.length-depth] ) ?map.get(ngram[ngram.length-depth]).wordCount(ngram) : 0.0;
        }
        
        public double count(String[] ngram)
        {
            if (depth == 0) {
                return count;
            }
            return map.containsKey(ngram[ngram.length-depth])?map.get(ngram[ngram.length-depth]).count(ngram) : 0.0;
        }
        
        
        

    }
    
    public BuildRule(HashSet<String> samples, int n, Dictionary root)
    {
        this.trainSample = samples;
        this.n = n;
        this.NgramCount = new Counter(n);
        this.RuleSet = new HashSet<String>();
        this.root = root;
        this.trainNum = 0;
    }
    
    
    public void train()
    {
        String regexp = "('?\\w+|\\p{Punct}\\.\\,)";
        Pattern pattern = Pattern.compile(regexp);
        for (String sample : trainSample) {
            ArrayList<String> Rules = new ArrayList<String>(); // local variable to record rules of n words
            Matcher matcher = pattern.matcher(sample.toLowerCase());
            
            while (matcher.find()) {
                String match = matcher.group();
                if(root.search(match)){
                	Rules.add(root.tagger); // root.tagger is the word formation
                	RuleSet.add(root.tagger);
                }
                else{
                	Rules.add(formation[match.length()%3]); // assume it is name of unknown
                	RuleSet.add(formation[match.length()%3]);
                }
            }
            String[] nGram = new String[n];
            for (int i = 0; i < n; i++) {
                nGram[i] = "<S>"; // start symbol
            }
            for (String word : Rules) {
            	
                for (int i = 0; i < n-1; i++) {
                    nGram[i] = nGram[i+1];
                }
                nGram[n-1] = word;
                trainNum += 1;
                NgramCount.insert(nGram);
               
            }
            if(nGram[0] == "<S>" && nGram[1] == "<S>" && nGram[2] == "PRP"){
            	testing++;
            }
        }
        
    }
    
    public double addOne(String[] ngrams){ // add one smoothing 
    	return (NgramCount.count(ngrams) + 1.0) / (NgramCount.wordCount(ngrams) + RuleSet.size());
    }
    
    public ArrayList<Double> getCount(String test)
    {
        ArrayList<Double> Appearance = new ArrayList<Double>();
        ArrayList<Double> Probabilities = new ArrayList<Double>();
        String[] samples = test.split("[,]");
        String regexp = "('?\\w+|\\p{Punct}\\.)";
        Pattern pattern = Pattern.compile(regexp);
        String[] nGram = new String[n];
        
        for (String sample : samples) {
            Matcher matcher = pattern.matcher(sample.toLowerCase());
            
            for (int i = 0; i < n; i++) {
                nGram[i] = "<S>"; // start symbol
            }
            
            while (matcher.find()) {
                String match = matcher.group();
                //System.out.println(match);
                String property = null;
                if(root.search(match)){
                	property = root.tagger;
                }
                for (int i = 0; i < n-1; i++) {
                    nGram[i] = nGram[i+1];
                }
                
                nGram[n-1] = property;
                //System.out.println(nGram[0] + "  " +nGram[1] +"  " +  nGram[2] +"  "+ NgramCount.count(nGram));
                Appearance.add(NgramCount.count(nGram));
                Probabilities.add(addOne(nGram));
                //double a = (ngc.count(words) + 1.0) / (ngc.deptch1Count(words) + RuleSet.size());
            }
        }
        //System.out.println(Probabilities);
        return Appearance;
    }
}

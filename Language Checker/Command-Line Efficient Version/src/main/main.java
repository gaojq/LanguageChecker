package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

import GrammarCheck.Bigram;
import SpellSearch.Dictionary;
import SpellSearch.checker;
import GrammarCheck.BuildRule;
import JsonConvertor.ConvertToJson;
import Parse.parse;
import javafx.util.Pair;

public class main {
	public static void main(String args[]) throws IOException
    {
	 
    	/********************Read file of big word list*********************/
		Dictionary root = new Dictionary();
		parse Parse = new parse();
        long begin = System.currentTimeMillis();
        //String fileName = args[0];
        //String examine = args[1];
        Parse.parseFILE("word_tagged.txt", root);
        long end = System.currentTimeMillis();
        //System.out.println("insertion time elapse: " + (double)(end-begin)/1000);
       
        
        /***********************Read data from phrase check dataset**************************/
        
		//Bigram n = new Bigram(Parse.parseFILE("mergedData.txt"));
	    //n.train();
	    
	    
	    /***********************Read data from grammar rule dataset**************************/
	    //BuildRule rule = new BuildRule(Parse.parseFILE("mergedData.txt"), 3, root);
	    //rule.train();
	    
	    //ConvertToJson con = new ConvertToJson(rule);
	    JsonFactory jsonFactory = new JsonFactory();
		JsonParser jp = jsonFactory.createParser(new File("GrammarRule.json"));
		jp.setCodec(new ObjectMapper());
		JsonNode Grammar = jp.readValueAsTree();
		
		jsonFactory = new JsonFactory();
		jp = jsonFactory.createParser(new File("phrase.json"));
		jp.setCodec(new ObjectMapper());
		JsonNode phrase = jp.readValueAsTree();
        /***************************Begin to check*************************************/
        
		//System.out.println(Grammar.get("NgramCount"));
		//System.out.println(phrase.get("BigramCount"));
	    begin = System.currentTimeMillis();
	    
	    // modify test  !!!!!
	    StringBuilder test = Parse.parseString("examine.txt");  //read test string
	    
	    
        checker check = new checker(test, root, phrase);
        check.SpellCheck();
        
        //Spell check result:
        if(!check.WrongSpell){
        	System.out.println("Spell is correct!");
        }
        else{
        	System.out.println(check.Correction); // HashMap (index : correction)
        }
        
     // Grammar check
        
        
        if(check.GrammarCheck){
        	
		    ArrayList<Double> GrammarCount = getCount(check.original, Grammar.get("NgramCount"), root);
		    ArrayList<ArrayList<Integer>> GrammarSuspect = new ArrayList<>();
		    Double preCount = GrammarCount.get(0);
		    ArrayList<Integer> inner = new ArrayList<>();
	        boolean connection = false;
	        if(preCount == 0.0)
	        	inner.add(0);
		    for(int i = 1; i< GrammarCount.size();i++){
	        	if(GrammarCount.get(i) < 10.0){
	        		if(preCount < 10.0){
	        			if(inner.size()==3 && !connection )
	        				inner.remove(0);
	        			inner.add(i);
	        			connection = true;
	        		}
	        		else
	        		{
	        			connection = false;
	        			inner = new ArrayList<>();
	        			if(i-2 >= 0) inner.add(i-2);
	        			if(i-1 >= 0) inner.add(i-1);
	        			inner.add(i);
	        			//GrammarSuspect.add(new ArrayList<Integer>(inner));
	        			
	        		}
	        	}
	        	else{
	        		if(!inner.isEmpty())
	        			GrammarSuspect.add(new ArrayList<Integer>(inner));
		        	inner = new ArrayList<>();
		        	connection = false;
		        	
		        	
	        	}
	        	preCount = GrammarCount.get(i);
	        	
	        }
		    if(!inner.isEmpty())
				GrammarSuspect.add(new ArrayList<Integer>(inner));
		    if(GrammarSuspect.isEmpty())
		    	System.out.println("Grammar usage seems good");
		    else{
		    	System.out.println("Following Grammar usage is wrong : ");
		    	//System.out.println(GrammarSuspect); // indexs of grammar outliers!
		    	for(ArrayList<Integer> inner1 : GrammarSuspect){
		    		String output = "";
		    		for(Integer Int: inner1){
		    			output += check.IndexString.get(Int) + " ";
		    		}
		    		System.out.println(output);
		    	}
		    }
        }
        
        
        //Grammar and Phrase check:
        ArrayList<ArrayList<Integer>> Suspect = new ArrayList<>();
        if(check.GrammarCheck){
			ArrayList<Pair<Integer,Integer>> result = perplexity(check.original, phrase.get("BigramCount")); // Grammar check : perplexity of probabilities
			//System.out.println("preplexity is : " + result);
			if(!result.isEmpty()){
				System.out.println("following phrase is not in our data :");
                for(Pair<Integer,Integer> x : result){
                    System.out.println(check.IndexString.get(x.getKey()) + " " + check.IndexString.get(x.getValue()));
                	}
		    	}
        }
        
		end = System.currentTimeMillis();
        //System.out.println("checking time elapsed: " + (double)(end-begin)/1000);
    }
	
public static ArrayList<Double> getCount(String test, JsonNode jsonNode, Dictionary root){
	ArrayList<Double> Appearance = new ArrayList<Double>();
    ArrayList<Double> Probabilities = new ArrayList<Double>();
    String[] samples = test.split("[,]");
    String regexp = "('?\\w+|\\p{Punct}\\.)";
    Pattern pattern = Pattern.compile(regexp);
    int n = 3;
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
            //System.out.println(nGram[0] + "  " +nGram[1] +"  " +  nGram[2]);
            Appearance.add(count(nGram,  jsonNode));
            //Probabilities.add(addOne(nGram));
            //double a = (ngc.count(words) + 1.0) / (ngc.deptch1Count(words) + RuleSet.size());
        }
    }
	    System.out.println(Appearance);
	    return Appearance;
	
	} 
	public static double count(String[] ngram, JsonNode js)
	{
	    if (js.get("depth").intValue() == 0) {
	    	ObjectNode object = (ObjectNode)js;
	    	object.put("count", js.get("count").longValue() + 1.0);
	        return js.get("count").longValue() - 1.0;
	    }
	    
	    //System.out.println(ngram[ngram.length - js.get("depth").intValue()]);
	    return js.get("map").has(ngram[ngram.length - js.get("depth").intValue()])?count(ngram,js.get("map").get(ngram[ngram.length - js.get("depth").intValue()])) : 0.0;
	}
	
	
	public static ArrayList<Pair<Integer,Integer>> perplexity(String test, JsonNode js) {
        float product = 1;
        int wordCountId = 0;
        //System.out.println(test);
        ArrayList<Pair<Integer,Integer>> wrongIndex = new ArrayList<>();
        ArrayList<Double> products = new ArrayList<Double>();
        String[] samples = test.split("[,]");
        String regexp = "('?\\w+|\\p{Punct}\\.)";
        Pattern pattern = Pattern.compile(regexp);
        //GoodTuring();
        for(String sample : samples){
	        Matcher matcher = pattern.matcher(sample.toLowerCase());
	        String pre = "<S>";
	        while (matcher.find()) {
	        	
	        	String match = matcher.group();
	        	double count = phraseCount(pre,match, js);
	        	if(count == 0){
	        		
	        		wrongIndex.add(new Pair<Integer,Integer>(wordCountId-1, wordCountId));
	        	}
	        	
	        }
        }
        
        return wrongIndex;
    }
	public static double phraseCount(String word1, String word2, JsonNode js)
    {
		
        if (js.has(word1) && js.get(word1).has(word2)) {
            return js.get(word1).get(word2).longValue();
        }
        return 0.0;
    }
}

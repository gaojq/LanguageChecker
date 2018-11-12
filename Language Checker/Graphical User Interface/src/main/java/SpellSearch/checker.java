package SpellSearch;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;

import SpellSearch.Dictionary;

public class checker {
	
	public String original = "";
	//public Bigram n;
	JsonNode phrase;
	public Dictionary root;package SpellSearch;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;

import SpellSearch.Dictionary;

public class checker {
	
	public String original = "";
	//public Bigram n;
	JsonNode phrase;
	public Dictionary root;
	public StringBuilder sb;
	public boolean WrongSpell = false;
	public boolean GrammarCheck = false;
	public ArrayList<String> IndexString;
	public HashMap<Integer,String> Correction;
	public checker(StringBuilder sb, Dictionary root, JsonNode phrase)
    {
	 
		//this.n = n;
		this.phrase = phrase;
		this.root = root;
		this.sb = sb;
		this.Correction = new HashMap<>();
		this.IndexString = new ArrayList<>();
        
    }
	
	public void SpellCheck(){
		String[] test = sb.toString().split("[\\s;,.:-?!\"]+");
		//String[] test = sb.toString().split("('?\\w+|\\p{Punct}\\.\\,\\:)");
		original = sb.toString();
        Set<String> suggested = new HashSet<String>();
        Set<String> correction = new HashSet<String>();
        
        String pre = "<S>";
        int index = 0;
        LevDistance lev = new LevDistance();
        if(test.length == 1){
        	
        	String finalCor = "";
        	String str = test[0];
        	IndexString.add(str);
        	if(root.search(str.toLowerCase()) == false){
        		WrongSpell = true;
        		suggested = lev.getEdits(str);
        		correction = lev.findCorrection(suggested, str, 2);
        		double max = Integer.MIN_VALUE;
        		String inner = "";
        		for(String cor : correction){
        			if(root.search(cor)){
        				if(phrase.get("wordCount").has(cor)){
		        			if(phrase.get("wordCount").get(cor).longValue() > max){
		        				max = phrase.get("wordCount").get(cor).longValue();
		        				inner = cor;
		        			}
        				}
        			}
        			
        			//System.out.println("For " + str +", you may mean : " + cor);
        		}
        		Correction.put(0, inner);
        	}
        	//else System.out.println(str + "  " + root.tagger);
        	
        }
        else {
        	GrammarCheck = true;
        	JsonNode BigramCount = phrase.get("BigramCount");
        	for(String str : test){

        		IndexString.add(str);
        		String finalCor = "";
        		/*if(str.length()== 0 )
        			continue;*/
	        	if(root.search(str.toLowerCase()) == false){
	        		WrongSpell = true;
	        		suggested = lev.getEdits(str);
	        		correction = lev.findCorrection(suggested, str, 2);
	        		double max = Integer.MIN_VALUE;
	        		for(String cor : correction){
	        			if(cor.length() <= 1)
	        				continue;
	        			if(root.search(cor)){
	        				if(BigramCount.has(pre) && BigramCount.get(pre).has(cor) ){
	        					double val =  BigramCount.get(pre).get(cor).longValue();
	        				//System.out.println(pre + "   " + cor +  "   " +  val);
	        					if(val > max){
		        					max = val;
		        					finalCor = cor;
	        					}
	        				}
	        			}
	        		}
	        		//System.out.println(finalCor);
                    
	        		original = original.replaceAll(str, finalCor);
                Correction.put(index, finalCor);
	        		pre = finalCor;
	        		
	        		
	        	}
	        	else{
		        	pre = str;
	        	}
	        	index++;
        	}
        }
        //System.out.println(original);
	}
	
	
	
}

	public StringBuilder sb;
	public boolean WrongSpell = false;
	public boolean GrammarCheck = false;
	public ArrayList<String> IndexString;
	public HashMap<Integer,String> Correction;
	public checker(StringBuilder sb, Dictionary root, JsonNode phrase)
    {
	 
		//this.n = n;
		this.phrase = phrase;
		this.root = root;
		this.sb = sb;
		this.Correction = new HashMap<>();
		this.IndexString = new ArrayList<>();
        
    }
	
	public void SpellCheck(){
		String[] test = sb.toString().split("[\\s;,.:-?!\"]+");
		//String[] test = sb.toString().split("('?\\w+|\\p{Punct}\\.\\,\\:)");
		original = sb.toString();
        Set<String> suggested = new HashSet<String>();
        Set<String> correction = new HashSet<String>();
        
        String pre = "<S>";
        int index = 0;
        LevDistance lev = new LevDistance();
        if(test.length == 1){
        	
        	String finalCor = "";
        	String str = test[0];
        	IndexString.add(str);
        	if(root.search(str.toLowerCase()) == false){
        		WrongSpell = true;
        		suggested = lev.getEdits(str);
        		correction = lev.findCorrection(suggested, str, 2);
        		double max = Integer.MIN_VALUE;
        		String inner = "";
        		for(String cor : correction){
        			if(root.search(cor)){
        				if(phrase.get("wordCount").has(cor)){
		        			if(phrase.get("wordCount").get(cor).longValue() > max){
		        				max = phrase.get("wordCount").get(cor).longValue();
		        				inner = cor;
		        			}
        				}
        			}
        			
        			//System.out.println("For " + str +", you may mean : " + cor);
        		}
        		Correction.put(0, inner);
        	}
        	//else System.out.println(str + "  " + root.tagger);
        	
        }
        else {
        	GrammarCheck = true;
        	JsonNode BigramCount = phrase.get("BigramCount");
        	for(String str : test){

        		IndexString.add(str);
        		String finalCor = "";
        		/*if(str.length()== 0 )
        			continue;*/
	        	if(root.search(str.toLowerCase()) == false){
	        		WrongSpell = true;
	        		suggested = lev.getEdits(str);
	        		correction = lev.findCorrection(suggested, str, 2);
	        		double max = Integer.MIN_VALUE;
	        		for(String cor : correction){
	        			if(cor.length() <= 1)
	        				continue;
	        			if(root.search(cor)){
	        				if(BigramCount.has(pre) && BigramCount.get(pre).has(cor) ){
	        					double val =  BigramCount.get(pre).get(cor).longValue();
	        				//System.out.println(pre + "   " + cor +  "   " +  val);
	        					if(val > max){
		        					max = val;
		        					finalCor = cor;
	        					}
	        				}
	        			}
	        		}
	        		//System.out.println(finalCor);
                    
	        		original = original.replaceAll(str, finalCor);
                    if(!Correction.containsKey(index))
                        Correction.put(index, finalCor);
	        		pre = finalCor;
	        		
	        		
	        	}
	        	else{
		        	pre = str;
	        	}
	        	index++;
        	}
        }
        //System.out.println(original);
	}
	
	
	
}

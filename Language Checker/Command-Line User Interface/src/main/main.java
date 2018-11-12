package main;

import java.util.ArrayList;
import GrammarCheck.Bigram;
import SpellSearch.Dictionary;
import SpellSearch.checker;
import GrammarCheck.BuildRule;
import Parse.parse;
import javafx.util.Pair;

public class main {
	public static void main(String args[])
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
        
		Bigram n = new Bigram(Parse.parseFILE("mergedData.txt"));
	    n.train();
	    
	    
	    /***********************Read data from grammar rule dataset**************************/
	    BuildRule rule = new BuildRule(Parse.parseFILE("mergedData.txt"), 3, root);
	    rule.train();
	    
	    
	    
        /***************************Begin to check*************************************/
        
	    begin = System.currentTimeMillis();
	    
	    // modify test  !!!!!
	    StringBuilder test = Parse.parseString("examine.txt");  //read test string
	    
	    
        checker check = new checker(test, root, n);
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
		    ArrayList<Double> GrammarCount = rule.getCount(check.original);
		    ArrayList<ArrayList<Integer>> GrammarSuspect = new ArrayList<>();
		    Double preCount = GrammarCount.get(0);
		    ArrayList<Integer> inner = new ArrayList<>();
	        boolean connection = false;
	        if(preCount == 0.0)
	        	inner.add(0);
		    for(int i = 1; i< GrammarCount.size();i++){
	        	if(GrammarCount.get(i) < 3.0){
	        		if(preCount < 3.0){
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
        /*ArrayList<ArrayList<Integer>> Suspect = new ArrayList<>();
        if(check.GrammarCheck){
			double result = n.perplexity(check.original); // Grammar check : perplexity of probabilities
			System.out.println("preplexity is : " + result);
			
			// evaluate result
			if((!n.WrongUsage && !n.Suspicious) || result < 179){
		    	System.out.println("Grammar and phrase usage is correct!");
		    }
		    else
		    {
		    	if(n.WrongUsage){
		    		Pair<Integer,Integer> pre = n.wrongIndex.get(0);
		    		ArrayList<Integer> innerSuspect = new ArrayList<>();
		    		innerSuspect.add(pre.getKey());
		    		innerSuspect.add(pre.getValue());
		    		for(Pair<Integer,Integer> x : n.wrongIndex){
		    			if(x == pre){
		    				continue;
		    			}
		    			if(x.getKey() == pre.getValue()){
		    				//System.out.println(innerSuspect);
		    				innerSuspect.add(x.getValue());
		    			}
		    			else{
		    				Suspect.add(new ArrayList<>(innerSuspect));
		    				innerSuspect = new ArrayList<>();
		    				innerSuspect.add(x.getKey());
				    		innerSuspect.add(x.getValue());
		    			}
		    			pre = x;
		    		}
		    		Suspect.add(new ArrayList<>(innerSuspect));
		    	}
		    	else if(n.Suspicious){
		    		Suspect = new ArrayList<>();
		    		Pair<Integer,Integer> pre = n.suspicious.get(0);
		    		ArrayList<Integer> innerSuspect = new ArrayList<>();
		    		innerSuspect.add(pre.getKey());
		    		innerSuspect.add(pre.getValue());
		    		for(Pair<Integer,Integer> x : n.suspicious){
		    			if(x == pre){
		    				continue;
		    			}
		    			if(x.getKey() == pre.getValue()){
		    				//System.out.println(innerSuspect);
		    				innerSuspect.add(x.getValue());
		    			}
		    			else{
		    				Suspect.add(new ArrayList<>(innerSuspect));
		    				innerSuspect = new ArrayList<>();
		    				innerSuspect.add(x.getKey());
				    		innerSuspect.add(x.getValue());
		    			}
		    			pre = x;
		    		}
		    		Suspect.add(new ArrayList<>(innerSuspect)); 
		    	}
		    	System.out.println(Suspect);  // Suspect is the result of index with wrong grammar usage
		    }
        }*/
		end = System.currentTimeMillis();
        //System.out.println("checking time elapsed: " + (double)(end-begin)/1000);
    }
}

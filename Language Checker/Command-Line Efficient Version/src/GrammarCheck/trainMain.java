package GrammarCheck;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaFileObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import JsonConvertor.ConvertToJson;
import Parse.parse;
import SpellSearch.Dictionary;

public class trainMain {
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException{
		/********************Read file of big word list*********************/
		Dictionary root = new Dictionary();
		parse Parse = new parse();
        long begin = System.currentTimeMillis();
        //String fileName = args[0];
        //String examine = args[1];
        Parse.parseFILE("word_tagged.txt", root);
        long end = System.currentTimeMillis();
        System.out.println("insertion time elapse: " + (double)(end-begin)/1000);
       
        
        /***********************Read data from phrase check dataset**************************/
        
		Bigram n = new Bigram(Parse.parseFILE("Grammar.txt"));
	    n.train();
	    ObjectMapper phrase = new ObjectMapper();
	    String PhraseJson = phrase.writeValueAsString(n);
	    phrase.writeValue(new File("Phrase.json"), n);
	    
	    /***********************Read data from grammar rule dataset**************************/
	    BuildRule rule = new BuildRule(Parse.parseFILE("Grammar.txt"), 3, root);
	    rule.train();
	    
	    System.out.println(rule.testing);
	    ConvertToJson con = new ConvertToJson(rule);
	    ObjectMapper mapper = new ObjectMapper();
	    String ruleJson = mapper.writeValueAsString(rule);
		mapper.writeValue(new File("GrammarRule.json"), rule);
        /***************************Begin to check*************************************/
	}
}

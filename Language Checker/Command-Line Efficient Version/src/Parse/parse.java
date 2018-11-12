package Parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import SpellSearch.Dictionary;

public class parse {
	public void parseFILE(String filename, Dictionary root){
		File file = new File(filename);
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
            	String[] str = line.split("\\_");
            	//System.out.println(str[0] + "  " + str[1]);
                root.insert(str[0],str[1]);
                count++;
            }
        }
        catch (IOException e) {
        	
        }
	}
	public HashSet<String> parseFILE(String filename){
		HashSet<String> set = new HashSet<>();
	    File file = new File(filename);
	    int count = 0;
	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	//System.out.println(line);
	        	String[] str = line.split(",");
	        	for(String s : str){
	        		set.add(s);
	        	}
	            count++;
	            
	            //if(count == 2000000)
	            	//break;
	        }
	    }
	    catch (IOException e) {
	    	System.out.println("can't open target file");
	    }
	    return set;
	}
	
	public StringBuilder parseString(String filename){
		File file1 = new File(filename);
        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(file1))){
        	String line;
        	while ((line = br.readLine()) != null){
        		sb.append(line);
        		sb.append(" ");
        	}

        }
        catch (IOException e) {
        	System.out.println("can't open target file");
        }
        return sb;
	}
}

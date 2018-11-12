package SpellSearch;
import java.util.*;
import SpellSearch.Node;

public class Dictionary {
	private HashMap<Character,Node> roots = new HashMap<Character,Node>();
	int flag; 
	public String tagger = null; // word formation
	
	public void insert(String string, String tagger) {
		string = string.toLowerCase();
		if(string.length() == 0)
			return;
		if (!roots.containsKey(string.charAt(0))) {
			roots.put(string.charAt(0), new Node());
		}
		insertWord(string.substring(1), roots.get(string.charAt(0)), tagger);
	}
	
	private void insertWord(String string, Node node, String tagger) {
		final Node nextChar;
		if(string.length() == 0 ){
			node.endOfWord = true;
			node.wordProperty = tagger;
			return;// ending condition : string.len == 0 or string.len == 1
		}
		if (node.children.containsKey(string.charAt(0))) {
			nextChar = node.children.get(string.charAt(0));
		} else {
			nextChar = new Node();
			node.children.put(string.charAt(0), nextChar);
		}
		insertWord(string.substring(1),nextChar, tagger);
	}
	
	
	
	public boolean search(String string) {
		flag = 0;
		if (roots.containsKey(string.charAt(0))) {
			if (string.length()== 1 && roots.get(string.charAt(0)).endOfWord) {
				tagger = roots.get(string.charAt(0)).wordProperty;
				return true;
			}
			return searchFor(string.substring(1),roots.get(string.charAt(0)));
		} 
		else {
			return false;
		}	
	}
	
		private boolean searchFor(String string, Node node) {
			flag++;
			if (string.length()==0) {
				if (node.endOfWord) {
					tagger = node.wordProperty;
					return true;
				} else return false;
				
			}
			return node.children.containsKey(string.charAt(0))?searchFor(string.substring(1),node.children.get(string.charAt(0))): false;
			
		}
	
	
	
	private String getTag(String string, Node node){
		return node.wordProperty;
	}
	
	void show(){
		System.out.println(roots);
	}
	
	

	
	
	 
}

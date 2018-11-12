package SpellSearch;

import java.util.HashMap;

public class Node { // Trie 
	public Node parent;
	public Boolean endOfWord = false; // it is an entire word
	
	public String wordProperty = null;// word formation
	public HashMap<Character,Node> children = new HashMap<Character,Node>();
}

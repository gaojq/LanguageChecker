package MainGUI;

import javax.swing.*;
import javax.swing.Timer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Import Speech File
import microphone.*;
import recognizer.*;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import SpellSearch.*;
import SpellSearch.Dictionary;
import GrammarChecker.*;


public class MainFile extends JFrame {
    
    public static Dictionary root = new Dictionary();
    public Set<String> edits = new HashSet<>();
    public JLabel output;
    public static JLabel label;
    public JTextArea input;
    public JLabel note;
    public static Bigram n;
    public static BuildRule rule;
    public Timer timer;
    public checker check;
    public StringBuilder readoutSpell, readoutGrammar, readoutPhrase;
    public StringBuilder readinstr = new StringBuilder("JIANG");
    public String FileName;
    public static JsonNode phrase, Grammar;

    public MainFile() {
        initUI();
    }

    private void initUI() {
            
            JPanel main = new JPanel(new BorderLayout());
            
            JPanel northPanel = new JPanel();
            northPanel.setLayout(new BoxLayout(northPanel,BoxLayout.Y_AXIS));
            
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel recordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel readPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
            
            JPanel southPanel = new JPanel();
            JPanel crPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            input = new JTextArea("Type something here ...");  // Default Text
            input.setPreferredSize( new Dimension( 310, 135 ) ); // Customized size of TextArea
            input.setBackground(new Color(255,255,255));
            input.setLineWrap(true);
            input.setWrapStyleWord(true);
            
            output = new JLabel("CHECK RESULTS GOES HERE"); // Default Text
            //output.setPreferredSize( new Dimension( 200, 150 ) ); // Customized size of TextArea

            label = new JLabel("<html><font color='rgb(238,238,238)'> Invisible </font></html>"); // Default Text
            note = new JLabel("<html><span style='font-size:8px'><font color='red'>" + "SPELL ISSUE" + "</font>" + "  |  " + "<font color='blue'>" + "GRAMMAR ISSUE" + "</font>" + " | " + "<font color='green'>" + "PHRASE ISSUE" + "</font>" + "</span></html>");
            
            
            JButton spell = new JButton("SPELL");
            JButton grammar = new JButton("GRAMMAR");
            JButton phrasebtn = new JButton("PHRASE");
            JButton clear = new JButton("CLEAR");
            JButton record = new JButton("VOICE RECORDING");
            JButton readin = new JButton("Import File");
            JButton readout = new JButton("Export Results");
            record.setPreferredSize(new Dimension(310,18));
            readin.setPreferredSize(new Dimension(153,18));
            readout.setPreferredSize(new Dimension(153,18));
            spell.setPreferredSize(new Dimension(75,18));
            grammar.setPreferredSize(new Dimension(75,18));
            phrasebtn.setPreferredSize(new Dimension(75,18));
            clear.setPreferredSize(new Dimension(75,18));

            //record.setBackground(new Color(0,0,0));

            JLabel title = new JLabel("LANGUAGE CHECKER");
            title.setFont(new Font("Serif", Font.PLAIN, 30));
            JLabel copyright = new JLabel("Author: M.Hang, Y.Han, J.Wang, J.Gao, Y.Huang");
            copyright.setFont(new Font("Serif", Font.ITALIC, 14));
            
            titlePanel.add(title);
            inputPanel.add(input);
            labelPanel.add(label);
            outputPanel.add(output);
            notePanel.add(note);
            btnPanel.add(spell);
            btnPanel.add(grammar);
            btnPanel.add(phrasebtn);
            btnPanel.add(clear);
            recordPanel.add(record);
            readPanel.add(readin);
            readPanel.add(readout);
            
            
            northPanel.add(titlePanel);
            //northPanel.setOpaque(false);

            centerPanel.add(inputPanel);
            centerPanel.add(recordPanel);
            centerPanel.add(readPanel);
            centerPanel.add(btnPanel);
            centerPanel.add(labelPanel);
            centerPanel.add(outputPanel);
            centerPanel.add(notePanel);
            //centerPanel.setOpaque(false);


            crPanel.add(copyright);
            southPanel.add(crPanel);
            //southPanel.setOpaque(false);
            
            main.add(northPanel, BorderLayout.NORTH);
            main.add(centerPanel, BorderLayout.CENTER);
            main.add(southPanel, BorderLayout.SOUTH);
            main.setOpaque(true);
            //main.setBackground(new Color(237,237,230));

            add(main, BorderLayout.CENTER);
            
            // Add Action to Each Button
            spell.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String instr = input.getText();
                if(instr.length() > 0) {
                    // Spell Check for user input
                    check = new checker(new StringBuilder(instr), root, phrase);
                    check.SpellCheck();
                    
                    HashMap<Integer,String> map = check.Correction;
                    StringBuilder sb  = new StringBuilder();
                    int cnt = 0;

                    String[] strs = instr.split("\\s+");
                    for (String str : strs) {
                        if(map.containsKey(cnt++)) // Word along with current index has Spell Issues
                        {
                            if(map.get(cnt - 1) != "")
                            {
                                sb.append("<font color='red'>" + map.get(cnt - 1) + "</font>" + " ");
                            }else{
                                sb.append("<font color='red'>" + "NULL" + "</font>" + " ");
                            }
                        }else {
                            sb.append(str + " ");
                        }
                    }
                    if(map.size() > 0)
                    {
                        label.setText("Spell Issue Found..."); // Means there is Spell Issues
                    }else{
                        label.setText("Looks like all good...!"); // Means there is NOT Spell Issues
                    }
                        output.setText("<html>"+ sb.toString() + "</html>");
                }else{
                    label.setText("Did you really type anything...?");
                    output.setText("<html><font color='rgb(238,238,238)'> Invisible </font></html>");
                }
            }
            });
            
            
            grammar.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    String instr = input.getText();
                    if(instr.length() > 0)
                    {
                        check = new checker(new StringBuilder(instr), root, phrase);
                        check.SpellCheck();

                        if(check.GrammarCheck)
                        {
                                ArrayList<Double> GrammarCount = getCount(instr, Grammar.get("NgramCount"), root);
                            ArrayList<ArrayList<Integer>> GrammarSuspect = new ArrayList<>();
                            Double preCount = GrammarCount.get(0);
                            ArrayList<Integer> inner = new ArrayList<>();
                            boolean connection = false;
                            if(preCount == 0.0)
                                inner.add(0);
                            for(int i = 1; i< GrammarCount.size();i++){
                                if(GrammarCount.get(i) < 10.0){
                                    if(preCount < 10.0){
                                        /*if(inner.size()==3 && !connection )
                                            inner.remove(0);*/
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
                            if(GrammarSuspect.isEmpty()) {
                                label.setText("Grammar seems good...");
                                output.setText(input.getText());
                            }else{
                                HashSet<Integer> indexset = new HashSet<>();
                                for(ArrayList<Integer> arls : GrammarSuspect)
                                {
                                    for(Integer index : arls)
                                    {
                                        indexset.add(index);
                                    }
                                }
                                // Highlight Grammar Issues
                                StringBuilder sb = new StringBuilder();
                                int cnt = 0;
                                String[] strs = instr.split("\\s+");
                                for (String str : strs) {
                                    if(indexset.contains(cnt++)) // Word along with current index has Spell Issues
                                    {
                                        sb.append("<font color='blue'>" + str + "</font>" + " ");
                                    }else {
                                        sb.append(str + " ");
                                    }
                                }
                                label.setText("Grammar Issue Found...");
                                    output.setText("<html>"+ sb.toString() + "</html>");
                            }
                        }
                    }else{
                            label.setText("Did you really type anything...?");
                        output.setText("<html><font color='rgb(238,238,238)'> Invisible </font></html>");
                    }
                }
            });
            
            phrasebtn.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    String instr = input.getText();
                    if(instr.length() > 0)
                    {
                        check = new checker(new StringBuilder(instr), root, phrase);
                        check.SpellCheck();

                        if(check.GrammarCheck)
                        {
                                ArrayList<ArrayList<Integer>> result = perplexity(instr, phrase.get("BigramCount"),check.Correction);//phrase check result 
                            if(result.isEmpty()) {
                                label.setText("Phrase seems good...");
                                output.setText(input.getText());
                            }else{
                                HashSet<Integer> indexset = new HashSet<>();
                                for(ArrayList<Integer> arls : result)
                                {
                                    for(Integer index : arls)
                                    {
                                        indexset.add(index);
                                    }
                                }
                                // Highlight Grammar Issues
                                StringBuilder sb = new StringBuilder();
                                int cnt = 0;
                                String[] strs = instr.split("\\s+");
                                for (String str : strs) {
                                    if(indexset.contains(cnt++)) // Word along with current index has Spell Issues
                                    {
                                        sb.append("<font color='green'>" + str + "</font>" + " ");
                                    }else {
                                        sb.append(str + " ");
                                    }
                                }
                                label.setText("Phrase Issue Found...");
                                    output.setText("<html>"+ sb.toString() + "</html>");
                            }
                        }
                    }else{
                            label.setText("Did you really type anything...?");
                        output.setText("<html><font color='rgb(238,238,238)'> Invisible </font></html>");
                    }
                }
            });
            
            clear.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(input.getText().length() > 0)
                 {
                        input.setText("");
                        label.setText("<html><font color='rgb(238,238,238)'> Invisible </font></html>");
                        output.setText("<html><font color='rgb(238,238,238)'> Invisible </font></html>");
                 }else {
                    label.setText("It is empty already...");
                    output.setText("<html><font color='rgb(238,238,238)'> Invisible </font></html>");
                 }        
                }
            });
            
            record.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingWorker<Void,Integer> worker = new SwingWorker<Void,Integer>(){

                        @Override
                        protected Void doInBackground() throws Exception {
                            Microphone mic = new Microphone(FLACFileWriter.FLAC);
                            File file = new File ("Recording.flac");    //Name your file whatever you want
                            try {
                                    mic.captureAudioToFile (file);
                            } catch (Exception ex) {
                                    //Microphone not available or some other error.
                                    System.out.println ("ERROR: Microphone is not availible.");
                                    ex.printStackTrace ();
                            }
                            
                            try {
                                    System.out.println ("Recording...");
                                    for(int i = 5; i > 0; i--)
                                    {
                                        Thread.sleep(1000);
                                        publish(i);
                                    }
                                    mic.close();
                            } catch (InterruptedException ex) {
                                    ex.printStackTrace ();
                            }
                            
                            mic.close ();       //Ends recording and frees the resources
                            System.out.println ("Recording stopped.");

                            Recognizer recognizer = new Recognizer (Recognizer.Languages.ENGLISH_US, "AIzaSyD8ryM0Nh350OdYABSOXRTJE44mGbyJ9Jk");
                            //Although auto-detect is available, it is recommended you select your region for added accuracy.
                            try {
                                    int maxNumOfResponses = 4;
                                    System.out.println("Sample rate is: " + (int) mic.getAudioFormat().getSampleRate());
                                    GoogleResponse response = recognizer.getRecognizedDataForFlac (file, maxNumOfResponses, (int) mic.getAudioFormat().getSampleRate ());
                                    input.setText(response.getResponse());
                                    System.out.println ("Google Response: " + response.getResponse ());
                                    System.out.println ("Google is " + Double.parseDouble (response.getConfidence ()) * 100 + "% confident in" + " the reply");
                                    System.out.println ("Other Possible responses are: ");
                                    for (String s:response.getOtherPossibleResponses ()) {
                                        System.out.println ("\t" + s);
                                }
                            } catch (Exception ex) {
                                    // TODO Handle how to respond if Google cannot be contacted
                                    System.out.println ("ERROR: Google cannot be contacted");
                                    ex.printStackTrace ();
                            }
                                file.deleteOnExit ();   //Deletes the file as it is no longer necessary.
                            return null;
                        }
                        
                        @Override
                        protected void process(java.util.List<Integer> chunks) {
                            // TODO Auto-generated method stub
                            super.process(chunks);
                            Integer countdown = chunks.get(chunks.size() - 1);
                            label.setText("Recording Finished in " + countdown + "s");
                        }

                        @Override
                        protected void done() {
                            // TODO Auto-generated method stub
                            super.done();
                            if(input.getText().length() > 0) // If recording success
                            {
                                label.setText("Recording Finished!");
                            }else {
                                label.setText("Please say something or check your mic...");
                            }
                        }
                        
                    };
                    worker.execute();
                }
            });
            
            readin.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        
                            pickFile(); // Import File into checker for language check
                        
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            
            readout.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        
                            exportFile(); // Import File into checker for language check
                        
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        
        setTitle("Language Checker");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public void pickFile() throws Exception{
            JFileChooser filechoose = new JFileChooser();
        readinstr = new StringBuilder();
        
        if(filechoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            java.io.File file = filechoose.getSelectedFile();
            
            FileName = file.getName();
            label.setText("Read in " + FileName + " Successfully!");
            
            Scanner inputFile = new Scanner(file);
            
            while(inputFile.hasNext())
            {
                readinstr.append(inputFile.nextLine());
                readinstr.append("\n");
            }
            
            if(readinstr.toString().length() > 100)
            {
                label.setText(FileName + " is too large to display, Please click 'Export Results' to check...");
            }else {
                input.setText(readinstr.toString());
            }
            
            inputFile.close();
        }else {
            label.setText("No File Selected!");
            readinstr = new StringBuilder("JIANG");
        }
    }
    
    public void exportFile() throws Exception{
            if(!readinstr.toString().equals("JIANG")) // A is the default readinstr value, i.e. DID NOT READ IN ANY FILE YET
            {
            // Spell Check for readin File
                check = new checker(readinstr, root, phrase);
                check.SpellCheck();
                
                HashMap<Integer,String> map = check.Correction;
                int cnt = 0;

                String[] strs = readinstr.toString().split("\\s+");
                StringBuilder sbSpell = new StringBuilder();
                for (String str : strs) {
                    if(map.containsKey(cnt++)) // Word along with current index has Spell Issues
                    {
                        if(map.get(cnt - 1) != "")
                        {
                            sbSpell.append("<font color='red'>" + map.get(cnt - 1) + "</font>" + " ");
                        }else{
                            sbSpell.append("<font color='red'>" + "NULL" + "</font>" + " ");
                        }
                    }else {
                            sbSpell.append(str + " ");
                    }
                }
                readoutSpell = sbSpell;
                
                if(check.GrammarCheck)
                {
                    //Grammar Check for readin File
                        ArrayList<Double> GrammarCount = getCount(readinstr.toString(), Grammar.get("NgramCount"), root);
                        ArrayList<ArrayList<Integer>> GrammarSuspect = new ArrayList<>();
                        Double preCount = GrammarCount.get(0);
                        ArrayList<Integer> inner = new ArrayList<>();
                        boolean connection = false;
                        if(preCount == 0.0)
                            inner.add(0);
                            for(int i = 1; i< GrammarCount.size();i++){
                            if(GrammarCount.get(i) < 10.0){
                                if(preCount < 10.0){
                                    /*if(inner.size()==3 && !connection )
                                        inner.remove(0);*/
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
                   
                        HashSet<Integer> indexset = new HashSet<>();
                        for(ArrayList<Integer> arls : GrammarSuspect)
                        {
                            for(Integer index : arls)
                            {
                                indexset.add(index);
                            }
                        }
                        // Highlight Grammar Issues
                        int cntt = 0;
                        StringBuilder sbGrammar = new StringBuilder();
                        for (String str : strs) {
                            if(indexset.contains(cntt++)) // Word along with current index has Spell Issues
                            {
                                sbGrammar.append("<font color='blue'>" + str + "</font>" + " ");
                            }else {
                                sbGrammar.append(str + " ");
                            }
                        }
                        readoutGrammar = sbGrammar;
                    
                    
                            // Phrase Check for readin File
                        ArrayList<ArrayList<Integer>> result = perplexity(readinstr.toString(), phrase.get("BigramCount"),check.Correction);//phrase check result 
                        HashSet<Integer> indexsettwo = new HashSet<>();
                    for(ArrayList<Integer> arls : result)
                    {
                        for(Integer index : arls)
                        {
                            indexsettwo.add(index);
                        }
                    }
                    // Highlight Phrase Issues
                    StringBuilder sbPhrase = new StringBuilder();
                    int cntb = 0;
                    for (String str : strs) {
                        if(indexsettwo.contains(cntb++)) // Word along with current index has Spell Issues
                        {
                            sbPhrase.append("<font color='green'>" + str + "</font>" + " ");
                        }else {
                            sbPhrase.append(str + " ");
                        }
                    }
                    readoutPhrase = sbPhrase;
                }
                
                // Write into separate HTML file
                String home = System.getProperty("user.home");
                File resultSpell = new File(home + File.separator + "Desktop" + File.separator + "SpellCheckResults-" + FileName + ".html");
                File resultGrammar = new File(home + File.separator + "Desktop" + File.separator + "GrammarCheckResults-" + FileName + ".html");
                File resultPhrase = new File(home + File.separator + "Desktop" + File.separator + "PhraseCheckResults-" + FileName + ".html");

                BufferedWriter bwSpell = new BufferedWriter(new FileWriter(resultSpell));
                bwSpell.write(readoutSpell.toString());
                bwSpell.close();
                
                BufferedWriter bwGrammar = new BufferedWriter(new FileWriter(resultGrammar));
                bwGrammar.write(readoutGrammar.toString());
                bwGrammar.close();
                
                BufferedWriter bwPhrase = new BufferedWriter(new FileWriter(resultPhrase));
                bwPhrase.write(readoutPhrase.toString());
                bwPhrase.close();
                
                label.setText("Results Export to Desktop Successfully!");
            }else {
                label.setText("You Haven't Select Any File");
            }
    }
    
    // METHODS FOR JSON
    public static ArrayList<Double> getCount(String test, JsonNode jsonNode, Dictionary root){
        ArrayList<Double> Appearance = new ArrayList<Double>();
        ArrayList<Double> Probabilities = new ArrayList<Double>();
        String[] samples = test.split("[,.]+");
        String regexp = "('?\\w+|\\p{Punct}\\.\\,\\:)";
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
                else{
                    property = "NN";
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
        //System.out.println(Appearance);
        return Appearance;
    }
    public static double count(String[] ngram, JsonNode js)
    {
        if (js.get("depth").intValue() == 0) {
            //System.out.println(js.get("count").longValue());
            ObjectNode object = (ObjectNode)js;
            object.put("count", js.get("count").longValue() + 1.0);
            return js.get("count").longValue() - 1.0;
        }
        //System.out.println(ngram[ngram.length - js.get("depth").intValue()]);
        return js.get("map").has(ngram[ngram.length - js.get("depth").intValue()])?count(ngram,js.get("map").get(ngram[ngram.length - js.get("depth").intValue()])) : 0.0;
    }
    
    
    public static ArrayList<ArrayList<Integer>> perplexity(String test, JsonNode js, HashMap<Integer,String> Correction) {
        float product = 1;
        int wordCountId = 0;
        ArrayList<ArrayList<Integer>> wrongIndex = new ArrayList<>();
        ArrayList<Double> products = new ArrayList<Double>();
        String[] samples = test.split("[,:;!?]+");
        
        String regexp = "('?\\w+|\\p{Punct}\\.\\,\\:)";
        Pattern pattern = Pattern.compile(regexp);
        for(String sample : samples){
            Matcher matcher = pattern.matcher(sample);
            String pre = "<S>";
            while (matcher.find()) {
                
                String match = matcher.group();
                double count = phraseCount(pre.toLowerCase(),match.toLowerCase(), js);
                if(count == 0.0 && pre != "<S>" && !Correction.containsKey(wordCountId-1)&&!Correction.containsKey(wordCountId) ){ //&& !Correction.containsKey(pre) && !Correction.containsKey(match)){
                        ArrayList<Integer> aaa = new ArrayList<>();
                        aaa.add(wordCountId-1);
                        aaa.add(wordCountId);
                        wrongIndex.add(new ArrayList<Integer>(aaa));
                }
                wordCountId++;
                pre = match;
            }
            pre = "<S>";
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
    public static void main(String[] args) throws JsonParseException, IOException {
        
        long begin = System.currentTimeMillis();

        JsonFactory jsonFactory = new JsonFactory();
        InputStream inGrammar = MainFile.class.getClassLoader().getResourceAsStream("GrammarRule.json");
        JsonParser jp = jsonFactory.createParser(inGrammar);
        jp.setCodec(new ObjectMapper());
        Grammar = jp.readValueAsTree();
        
        jsonFactory = new JsonFactory();
        InputStream inPhrase = MainFile.class.getClassLoader().getResourceAsStream("Phrase.json");
        jp = jsonFactory.createParser(inPhrase);
        jp.setCodec(new ObjectMapper());
        phrase = jp.readValueAsTree();
                    
        InputStream in = MainFile.class.getClassLoader().getResourceAsStream("word_tagged.txt");
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                    String[] str = line.split("\\_");
                    root.insert(str[0],str[1]);
            }
        }
        catch (IOException e) {
        }
        
        long end = System.currentTimeMillis();
        System.out.println("Data Structure Building and Data Training Time:" + (double)(end-begin)/1000);
       
        EventQueue.invokeLater(() -> {
            MainFile ui = new MainFile();
            ui.setVisible(true);
        });
    }
}

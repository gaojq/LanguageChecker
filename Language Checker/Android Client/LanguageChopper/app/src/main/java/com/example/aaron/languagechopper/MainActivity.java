package com.example.aaron.languagechopper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;

import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

// Import File From Other Packages
import com.example.aaron.languagechopper.GrammarCheck.*;
import com.example.aaron.languagechopper.SpellSearch.*;

public class MainActivity extends Activity {

    // Build new root
    public static com.example.aaron.languagechopper.SpellSearch.Dictionary root = new com.example.aaron.languagechopper.SpellSearch.Dictionary();
    public static HashSet<String> set = new HashSet<>();
    public com.example.aaron.languagechopper.GrammarCheck.Bigram n;
    public BuildRule rule;

    private EditText speechText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Read big text into Dictionary
        try {
            BufferedReader readerone = new BufferedReader(new InputStreamReader(getAssets().open("word_tagged.txt")));
            String mLine = readerone.readLine();
            while (mLine != null) {
                String[] str = mLine.split("\\_");
                root.insert(str[0],str[1]);
                mLine = readerone.readLine();
            }
            readerone.close();
        }
        catch (IOException e) {
        }
        // Read from dataset for training
        try {
            BufferedReader readertwo = new BufferedReader(new InputStreamReader(getAssets().open("Grammar.txt")));
            String Line = readertwo.readLine();
            int count = 0;
            while (Line != null) {
                String[] str = Line.split(",");
                for(String s : str)
                    set.add(s);
                count++;
                Line = readertwo.readLine();
                if(count == 20000)
                    break;
            }
            readertwo.close();
        }
        catch (IOException e) {
        }

        n = new Bigram(set);
        n.train();
        System.out.println(n.BigramCount.size() + "dfabskdbflkasdbfkjabsdddddddd");

        rule = new BuildRule(set,3,root);
        rule.train();

        // Set notation label
        TextView note = (TextView) findViewById((R.id.Note));
        note.setText(Html.fromHtml("<font color='red'>" + "SPELL ISSUE" + "</font>" + "  |  " + "<font color='blue'>" + "GRAMMAR ISSUE" + "</font>"));

        speechText = (EditText) findViewById(R.id.input);
    }

    public void btn_check(View v){

        // Dismiss keyboard on Button Click
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Get input and processed, then print to output
        EditText input = (EditText) findViewById(R.id.input);
        TextView output = (TextView) findViewById((R.id.output));
        TextView label = (TextView) findViewById(R.id.tip);

        String instr = input.getText().toString();

        if(instr.length() > 0) {
            // Spell Check for user input
            checker check = new checker(new StringBuilder(instr), root, n);
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
                label.setText("Did you mean...?"); // Means there is Spell Issues
            }else{
                label.setText("Looks like all good...!"); // Means there is NOT Spell Issues
            }
            output.setText(Html.fromHtml(sb.toString()));
        }else{
            label.setText("");
            output.setText("Did you really type anything...?");
        }
    }
    public void btn_correct(View v){
        // Dismiss keyboard on Button Click
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Get input and processed, then print to output
        EditText input = (EditText) findViewById(R.id.input);
        TextView output = (TextView) findViewById((R.id.output));
        TextView label = (TextView) findViewById(R.id.tip);

        label.setText("");

        String instr = input.getText().toString();

        if(instr.length() > 0)
        {
            checker check = new checker(new StringBuilder(instr), root, n);
            check.SpellCheck();

            if(check.GrammarCheck)
            {
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
                    output.setText("Grammar seems good...");
                else{
                    HashSet<Integer> indexset = new HashSet<>();
                    for(ArrayList<Integer> arls : GrammarSuspect)
                    {
                        for(Integer index : arls)
                        {
                            indexset.add(index);
                        }
                    }
                    System.out.println(indexset + "aaaaaa");
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
                    output.setText("Grammar seems NOT good...");
                    output.setText(Html.fromHtml(sb.toString()));
                }
            }
        }else{
            output.setText("Did you really type anything...?");
        }
    }
    public void btn_clear(View v){

        // Dismiss keyboard on Button Click
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Get input and processed, then print to output
        EditText input = (EditText) findViewById(R.id.input);
        TextView output = (TextView) findViewById((R.id.output));
        TextView label = (TextView) findViewById(R.id.tip);

        label.setText("");

        if(input.getText().toString().length() > 0)
        {
            ((EditText) findViewById(R.id.input)).getText().clear();
            output.setText("Did you mean...?");
        }else {
            output.setText("It is empty already...");
        }
    }
    public void btn_record(View v){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH);

        try{
            startActivityForResult(intent,10);
        }catch(ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(),"DEVICE PROBLEM !!!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 10)
        {
            if(resultCode == RESULT_OK && data != null)
            {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                speechText.setText(res.get(0));
            }
        }
    }

    public void btn_about(View v){
        Intent next = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(next);
    }
    public void btn_contact(View v){
        Intent next = new Intent(MainActivity.this, ContactActivity.class);
        startActivity(next);
    }
}

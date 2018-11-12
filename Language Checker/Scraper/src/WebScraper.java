import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class WebScraper {

    String pathname = "sampleURL.txt";
    String theURL;

    private void getText() throws IOException {
        String line = null;
        List<String> list = new ArrayList<String>();
        final String textFile = "sampledata.txt";
        File theFile = new File(textFile);
        if (!theFile.exists()) {
            theFile.createNewFile();
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathname));
            try {
                while((line = reader.readLine()) != null) {
                    list.add(line);
                }
            } finally {
                reader.close();
            }
            int cnt = 0;
            for (String theURL: list) {
                try {
                    Document doc = Jsoup.connect(theURL).get();
                    cnt += 1;
                    Elements title = doc.select("H1");
                    Elements tag = doc.select("p");
                    System.out.println(cnt + ". " + title.text());
                    FileWriter fw = new FileWriter(theFile, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw);
                    for(Element e: tag) {
                        String text =e.text();
                        text = text.replaceAll("\\[.*\\]", "");
                        //System.out.println(text);
                        out.print(text);
                    }
                    out.close();
                } catch (Exception e) {
                    System.out.println(">> Connection Error: " + theURL);
                }
            }
        } catch (IOException ex) {
            System.out.println(">> Reader Error");
        }
    }

    public static void main(String[] args) throws IOException {
        WebScraper scraper = new WebScraper();
        scraper.getText();
    }
}


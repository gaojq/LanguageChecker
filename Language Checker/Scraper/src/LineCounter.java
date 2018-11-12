import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LineCounter {
    public static void main(String[] args) throws IOException {
        int lineCount = 0;

        // Open the file
        FileInputStream fstream = new FileInputStream("wikiScrapedURLs.txt");

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

//Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
            // Print the content on the console
            lineCount += 1;
        }

//Close the input stream
        br.close();
        System.out.print(lineCount);
    }
}


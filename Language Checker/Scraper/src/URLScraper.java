import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class URLScraper {
    private static ArrayList<String> urllist = new ArrayList();
    private static ArrayList<String> nextPageList =  new ArrayList<>();
    private static String wikiPagesURL = "https://en.wikipedia.org/w/index.php?title=Special:AllPages&from=%287135%29+1993+VO";

    public static void main(String[] args)
    {
        int findCount = 0;

        nextPageList.add("https://en.wikipedia.org/wiki/Special:AllPages#mw-head");
        nextPageList.add("https://en.wikipedia.org/wiki/Special:AllPages#p-search");
        nextPageList.add("https://en.wikipedia.org/wiki/Special:AllPages");
        nextPageList.add("https://en.wikipedia.org/w/index.php?title=Special:CreateAccount&returnto=Special%3AAllPages");
        nextPageList.add("https://en.wikipedia.org/w/index.php?title=Special:UserLogin&returnto=Special%3AAllPages");
        nextPageList.add("https://en.wikipedia.org/w/index.php?title=Special:AllPages&printable=yes");
        //nextPageList.add("")

        Path file = Paths.get("wikiScrapedURLs.txt");

        try {
            Document doc = Jsoup.connect(wikiPagesURL).get();
            Elements elements = doc.select("a");
            for(Element element : elements){
                String url = element.absUrl("href");

                if (url.contains("AllPages")
                        && (!nextPageList.contains(url))
                        && (!url.contains("toggle_view_mobile"))
                        && (!url.contains("printable"))
                        && (url != wikiPagesURL)
                        && (!url.contains("CreateAccount")
                        && (!url.contains("UserLogin"))
                        && (!url.contains("#mw-head"))
                        && (!url.contains("#p-search"))
                ))
                {
                    if (findCount == 1)
                    {
                        wikiPagesURL = url;
                        System.out.print(wikiPagesURL + "\n");
                    }
                    nextPageList.add(url);
                    findCount += 1;
                }
                else if ((!nextPageList.contains(url))
                        && (!url.contains("toggle_view_mobile"))
                        && (!url.contains("printable"))
                        && (!url.contains("CreateAccount"))
                        && (!url.contains("UserLogin"))
                        && (!url.contains("#mw-head"))
                        && (!url.contains("#p-search")))
                {
                    if (url.startsWith("https://en."))
                    {
                        urllist.add(url);
                    }
                }
            }

            urllist.remove("https://en.wikipedia.org/wiki/Help:Contents");
            urllist.remove("https://en.wikipedia.org/wiki/Wikipedia:About");
            urllist.remove("https://en.wikipedia.org/wiki/Main_Page");
            urllist.remove("https://en.wikipedia.org/wiki/Wikipedia:Community_portal");
            urllist.remove("https://en.wikipedia.org/wiki/Special:RecentChanges");
            urllist.remove("https://en.wikipedia.org/wiki/Wikipedia:File_Upload_Wizard");
            urllist.remove("https://en.wikipedia.org/wiki/Wikipedia:Contact_us");
            urllist.remove("https://en.wikipedia.org/wiki/Special:SpecialPages");
            urllist.remove("https://en.wikipedia.org/wiki/Wikipedia:About");
            urllist.remove("https://en.wikipedia.org/wiki/Wikipedia:General_disclaimer");
            urllist.remove("https://en.wikipedia.org/wiki/Special:MyTalk");
            urllist.remove("https://en.wikipedia.org/wiki/Special:MyContributions");
            urllist.remove("https://en.wikipedia.org/wiki/Portal:Contents");
            urllist.remove("https://en.wikipedia.org/wiki/Portal:Featured_content");
            urllist.remove("https://en.wikipedia.org/wiki/Portal:Current_events");
            urllist.remove("https://en.wikipedia.org/wiki/Special:Random");
            urllist.remove("https://en.wikipedia.org/wiki/Wikipedia:Contact_us");
            urllist.remove("https://en.wikipedia.org/wiki/Main_Page");

            Files.write(file, urllist, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error happened");
        }
    }
}


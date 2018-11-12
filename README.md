# Language Checker

* **GUI_DEMO/Check Detail in Graphical User Intreafce Folder**

<img src="https://user-images.githubusercontent.com/22137277/39412717-f6ae379a-4bee-11e8-8c95-cece84ef8be7.gif" width="280"/> <img src="https://user-images.githubusercontent.com/22137277/39412809-df15be90-4bef-11e8-8813-ebc7c8a5c35c.gif" width="280"/> <img src="https://user-images.githubusercontent.com/22137277/39412834-1dbe5ada-4bf0-11e8-8cab-fde0f8fd6306.gif" width="280"/>

* **Android Client_DEMO/Check Detail in Android Client Folder**

<img src="https://user-images.githubusercontent.com/22137277/39412849-536bff34-4bf0-11e8-821d-6ebbff5a3309.gif" width="280"/> <img src="https://user-images.githubusercontent.com/22137277/39412848-535ba512-4bf0-11e8-9df5-bdf0963c0ed5.gif" width="280"/> <img src="https://user-images.githubusercontent.com/22137277/39412847-534bd4ac-4bf0-11e8-828b-5526c0a9ce5d.gif" width="280"/>

## It is a powerful spelling and grammar checker in English.

* **What's the Problem** -- Natural Language Processing (NPL) is a complicate and popular problem in recent years. It used to be difficult to solve or even approach in the past. Also, people living in different regions have different language style even though they speak the same language. So it can not be simplified into several constant grammar rules. Is there any model that could fit all the language’s features? We were not sure. But nowadays with the help of computer programming,  it is possible to use programming algorithms and data structures to help solve or at least approach this problem. One of the popular topics in the NPL is about checking spelling and grammar in the text. Given a sentence, could the checker smart enough to find all possible spelling error and grammar error in the sentence? Some great techniques and methodologies have been invented to solve this problem, include N-Grams, Part-Of-Speech Tagging, etc. We proposed and implemented a English language checker that is able to check the possible spelling and grammar error and support voice input.

* **Why Important** -- To write a text without any error is difficult. The language checker can help students, bloggers, even professionals to correct the text. Especially users that write in a second language (like our group) will benefit from a language checker.



## Contact Info

If you have any questions about GUI or Android Client, please contact:

* **Jiangyu Wang** - *jiangyu@bu.edu*
* **Jianqing Gao** - *gaojq@bu.edu*


For Command Line interface and N-Gram Model, RNN, Algorithm related object

* **Mengyu Hang** - *myhang@bu.edu*

For Web Scrapper, Database related object

* **You Han** - *yhan94@bu.edu*

* **Yaqin Huang** - *yaqinh@bu.edu*


## Implementation


### high-level description of the implementation


* **Spell Check** -- Based on the word list we have(containing word and its word formation), we build an efficient class Dictionary with inner class Node(TrieNode) which ensures the efficiency. When we find the outlier which is not in the word list, we will compute the Levenshtein distance to find accepted word within 2 maxError. Besides, the relationship with context is also considered in our program by getting the final correction with highest probabiliy in phrase use.

* **Grammar Check** -- To utilize N-gram model, we retrieve word formation of each word in Dictionary class object and build rule-based model. 

* **Phrase Check** -- Different from Grammar Check, we utilize Bigram model and focus on the phrase of words instead of word formation.

* **Jackson use** -- We use Jackson API to convert object to JSON file. And update the JSON file based on the target user input.



* **Start with Data** -- For data, we crawled through the reliable websites on Internet and collected around 11GB text in English. We use these text to build our language model which can detect the suspicious expressions from the user.

* **Find Spelling Errors** -- Once we've received your text, our checker will perform the spelling checking and find any word you have misspelled.

* **Find Grammar Errors** -- The most important part of the project is how we implement the grammar checking. We use **predefined rules** to detect common grammatical errors. As well, we **create rules** based on the text corpus. That is, we define the grammars in statistical approaches and detect the expressions that are statistically impossible to be seen.

* **Easy To Use** -- We developed Command-line User Interface and Graphical User Interface for the checker.

  * For the Commoand-line User Interface, the first text file is the word list with their formation and the second text file is the sentence dataset scraping from wikipedia. The sentence with "" can be changed to your own sentence which will be examined.

  * In GUI, we designed a simple interface where the user can type the text into the blank or use microphone to record. Then the spell or grammar checking will performed according to which button the user click on. After checking, the user will clear the blank and start to input.

```
// Try in terminal for Command-line Efficient Version, navigate to src folder and execute using following command

    java -cp *:. Checker -file examine.txt

```
* **Free Your Hands** -- Do not want to type letter by letter? Try to use the microphone to speak to the GUI. We implement the **speech input** with Google Speech API which recognize the microphone input with the extremely high accuracy and speed. In this case, what we are trying to do is to check the grammars only.

* **Try the Android Client** -- We developed the Android Client for the checker. Although the simulator does not support microphone input, we implement other functions and make it performs well.


### Data Structures

* **Trie** -- is a kind of search tree—an ordered tree data structure that is used to store a dynamic set or associative array where the keys are usually strings. It is an efficient information retrieval data structure, and Hash table lookups are O(1) in the average case. Considering the high-efficiency especially in lookup of these two data structures, We implemented HashMap with TrieNode (HashMap <Character, TrieNode >()) as a dictionary to accomplish prefix search function. In our data structure, a trie node has three instances, it contains the character, its children(also a HashMap <Character, TrieNode >()) and the flag that marks if it is a leaf node(isEndOfWord). After inserting words of big text we scraped into dictionary, it can efficiently complete the check function (it takes 1.275 second to insert 460000 words and 0.007 second to check one sentence whether there exists spelling mistake and give suggestions based on checking result).


### Algorithms

* **Spelling Checking** -- Once we received the use input, we started to look for words that did not exist in Dictionary. We have built a Dictionary using the crawled words(word_tagged.txt) by Trie Data Structure, (HashMap<Character, TrieNode>) and time efficiency shows great performance in Inserting and Searching a specific word in Dictionary, even if the overall size is significant. We can also retrieve the word formation of specific word of this data structure.

* **Spelling Correction** -- If we found some words that did not exist in Dictionary, we manipulating the words in four methods: insert a char, delete a char, swap two chars and substitute a char. And search for the after-edits words in Dictionary, return the edits word as suggestion word if exist and Levenshiten Distance is less than MaxError(We set as 2). ALSO, we calculate the count that this possible correction with context combination appears in our dataset to determine which word will be left for the final correction.We manipulating the words in four methods: insert a char, delete a char, swap two chars and substitute a char. And search for the after-edits words in Dictionary, return the edits word as suggestion word if exist.

* **Grammar Checking** -- We built a N-gram(3-gram) model to generate every 3-word formation combination such as(NN + VB + NN), after training the Grammar.txt. We write this Object into Json file ("GrammarRule.json") so that we can directly read the json file when we running our checker program. ALSO, we will update the json file based on target examined sentence to improve our checker.

* **Phrase Checking** -- We implemented the concept of Bigram Model to approximates the probability of a word given a previous word. We write this Object into Json file("Phrase.json"). We only care about the outlier circumstance of phrase check(can be improved in the future work).

* **N-Gram** -- An n-gram model is a type of probabilistic language model for predicting the next item in such a sequence in the form of a (n − 1)–order Markov model.

* **Bigram** -- A bigram or digram is a sequence of two adjacent elements from a string of tokens, which are typically letters, syllables, or words. A bigram is an n-gram for n=2. The frequency distribution of every bigram in a string is commonly used for simple statistical analysis of text.

* **Smoothing** -- To keep a language model from assigning zero probability to these unseen events, we’ll have to shave off a bit of probability mass from some more frequent events and give it to the events we’ve never seen. This modification is called smoothing

  * **add-1 smoothing** (Laplace smoothing): The simplest way to do smoothing is add one to all the bigram counts, before we normalize them into probabilities.

  * **Good-Turing**: Good–Turing estimation is a statistical technique for estimating the probability of encountering an object of a hitherto unseen species, given a set of past observations of objects from different species. The basic idea for Good-Turing Smoothing is to reallocate the probability mass of n-grams that were seen once to the n-grams that were never seen.



### Features

* **Crawler that stores information about 11GB of ASCII text on the Internet.**

  * The scraper use the [jsoup](https://jsoup.org/download) to scrape and parse HTML from a URL. In our project, we use the Wikipedia and Gutenberg Dataset as our main data source.

  * On Wikipedia, there is a [list](https://en.wikipedia.org/wiki/Special:AllPages) that contains most of the Wiki pages’ URL. Every list page contains hundreds of Wiki URLs. So the task is to first, given a list web page, scrape all useful Wiki pages. Second, scrape the next page button’s URL so we could easily scrape next available list web page and finally, put all the scraped Wiki pages’ URL in one txt file.

  * Because of the large volume of the Wiki pages (about 13 million URLs), the volume of list pages are also large. For safety and efficiency reason, we scrape 40 thousand category pages per run. And then run the URLScraper.java several times to get 13 million URLs used by next step. We store these URLs in a text file.

  * In the WebScraper.java, we use the jsoup to scrape all the text contents given a URL and then use regular expression to remove contents that are useless for us, such as such as brackets and quotation marks. All text are stored in text file mergeData.txt per sentence per line.

  * The Gutenberg Dataset is downloaded from [UMICH Professor Lahiri’s website](http://web.eecs.umich.edu/~lahiri/gutenberg_dataset.html ), and then we use the textFormatTransfer.java to reformat the Gutenberg dataset into the same format as the Wiki dataset.

  * The total size of our dataset is around 10.9 GB. [Here](https://drive.google.com/drive/folders/12x9x1WM-4nzg7ABVWg_Qqg04ZYdU3bKQ?usp=sharing) is the google drive link for the data. The folder ScrapedURLs contains all URLs we used. The folder  SentenseData contains all information we collected.

* **Checker to check each word separately.**

  * See Spelling Checking and Spelling Correction under Algorithms.

* **Checker that is able to identify unusual outliers.**

  

* **Evidence of the reasonable correctness of the checker.**

  * The GrammarRule.json file will be updated based on user input sentence, the count of target N-gram will increase by 1.
  * The data is from formal article and 3-Gram for Grammar check and Bigram for Phrase check, accuracy should be accepted.

```
Example:

User typed: you must should be intersted in this jackkt

Our checker: Spelling Correct: intersted -> interested; jackkt -> jacket   Highlighted Grammar Issues: must shoud be

A third-party tool: It correct the misspelling words, yet without highlight the Grammar Issues
```


* **Command-line User Interface that allows users to check a given file, and output the most suspicious phrases.**

  * Spell Check and Correct, Grammar Issues Hightlight

* **Graphical User Interface that highlights suspicious and non-suspicious textual elements.**

  * Spell Check and Correct, Grammar Issues Hightlight, Speech-to-Text Input

* **Android client for the checker.**

  * Spell Check and Correct, Grammar Issues Hightlight, Speech-to-Text Input(ONLY on real device)

* **Speech input for Graphical User Interface.**

  The checker could accept microphone input and check it. For speech input, we use part of the word from the [JARVIS](https://github.com/lkuza2/java-speech-api). The live speech recognition process is :

  * Capture the audio from microphone and save it as a wave file. (we do not implement it in Android client since it doesn't support microphone interface).

  * We use [JavaFlacEncoder](http://sourceforge.net/projects/javaflacencoder/) API to converts WAVE files from microphone input to FLAC. We use  FLAC (Free Lossless Audio Codec) is an audio coding format. It's highly recommended to use FLAC encoding because it uses lossless compression; therefore recognition accuracy is not compromised by a lossy codec.

  * Get response from Google, including confidence score and text. I use my API key from Google Speech API v2. The source code is [here](https://github.com/gillesdemey/google-speech-v2/).


* **Checking speed**

  * In Termianl, the checking speed is around 5 seconds to return the results(Most consuming part is load Json File)
  * In GUI, it return the results immediately after user click the check button(Without Implement the huge dataset stored by Json, yet the results has trivial difference)
  * In Android, loading application takes several seconds, yet checking is fast and checking result is reliable. Without implement the huge dataset, as limited by the Max File size in Android Emulator.

* **Checking speed.**

  * Command-line User Interface Efficient: 5-7 seconds


* **Part-Of-Speech Tagging**

  * Part-of-speech tagging (POS tagging or PoS tagging or POST), also called grammatical tagging or word-category disambiguation, is the process of marking up a word in a text (corpus) as corresponding to a particular part of speech, based on both its definition and its context—i.e., its relationship with adjacent and related words in a phrase, sentence, or paragraph.

  * We use the open source — [Stanford POS Tagger](https://nlp.stanford.edu/software/tagger.shtml) (v3.8.0) to tag the text files we have collected.

  * To see examples: POSTagger/word_tagged.txt, sample-output.txt

```
Example:

Input text: A passenger plane has crashed shortly after take-off from Kyrgyzstan's
capital, Bishkek, killing a large number of those on board.

Tagged text: A_DT passenger_NN plane_NN has_VBZ crashed_VBN shortly_RB after_IN take-off_NN from_IN Kyrgyzstan_NNP 's_POS capital_NN ,_, Bishkek_NNP ,_, killing_VBG a_DT large_JJ number_NN of_IN those_DT on_IN board_NN ._.

Explanation examples:  NN >> Noun, singular or mass, VBZ >> Verb, 3rd person singular present, JJ >> Adjective, ...
```


## References

* [Rule-Based Grammar Checker](http://www.danielnaber.de/languagetool/download/style_and_grammar_checker.pdf)

* [Language Independent Spellchecking and Autocorrection](http://static.googleusercontent.com/media/research.google.com/en/us/pubs/archive/36180.pdf)

* JARVIS(https://github.com/lkuza2/java-speech-api)

* [Stanford POS Tagger](https://nlp.stanford.edu/software/tagger.shtml)

* [Google Speech API v2](https://github.com/gillesdemey/google-speech-v2/)

* [JavaFlacEncoder](http://sourceforge.net/projects/javaflacencoder/)



## Course

EC504 Advanced Data Structure Course Project



## Acknowledgments

* [JARVIS](https://github.com/lkuza2/java-speech-api)

* [JavaFlacEncoder](http://sourceforge.net/projects/javaflacencoder/)

* [Google Speech API v2](https://github.com/gillesdemey/google-speech-v2/)

* [Stanford POS Tagger](https://nlp.stanford.edu/software/tagger.shtml)

* We would like to thank the above so much for your work, this checker could not have been created without it.

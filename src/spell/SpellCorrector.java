package spell;

import java.util.*;
import java.io.*;

/** SpellCorrector
 *
 * a class that compares a potentially misspelled word with a dictionary and offers a possible correct spelling
 * if the word is already spelled correctly, the same word is suggested
 */
public class SpellCorrector implements ISpellCorrector {

    public static final int MAX_DISTANCE = 2;

    private Trie dictionary;
    private TreeSet<String> found;
    private TreeSet<String> checkSet;
    private TreeSet<String> extra;
    private int distance;

    public SpellCorrector() {}

    /** useDictionary
     *
     * reads all the words in a text file into a Trie to be used as a dictionary
     *
     * @param dictionaryFileName File containing the words to be used
     * @throws IOException: if the filename is invalid
     */
    public void useDictionary(String dictionaryFileName) throws IOException {
        dictionary = new Trie();

        Scanner inFile = new Scanner(new BufferedInputStream(new FileInputStream(dictionaryFileName)));
        // inFile.useDelimiter("\n");

        while (inFile.hasNext()) { dictionary.add(inFile.next()); }

        inFile.close();

        // System.out.println("Words: " + dictionary.getWordCount());
        // System.out.println("Nodes: " + dictionary.getNodeCount());
        // System.out.println(dictionary.toString());
    }

    /** suggestSimilarWord
     *
     * takes an input word and checks it for accuracy, suggesting another word if it is spelled incorrectly
     *
     * @param inputWord: the word to check
     * @return a string with the smallest edit distance (up to MAX_DISTANCE) and highest usage value in the Trie
     * @throws NoSimilarWordFoundException: if no suitable word is found within MAX_DISTANCE edits from the input word
     */
    public String suggestSimilarWord(String inputWord) throws NoSimilarWordFoundException {

        String word = inputWord.toLowerCase();
        String similar = null;
        this.distance = 0;
        this.found = new TreeSet<>();
        this.extra = new TreeSet<>();

        if (dictionary.find(word) != null) { similar = word; }
        else {
            while (distance < MAX_DISTANCE && similar == null) {
                similar = similarWord(word);
            }

            if (similar == null) { throw new NoSimilarWordFoundException(); }
        }

        return similar;
    }

    /** similarWord
     *
     * +1 edit distance iteration (adds all words with edit distance 1 from the current set of words to the next set)
     *
     * @param word: the word to check
     * @return a similar word if found, or null if not found
     */
    public String similarWord(String word) {
        checkSet = extra;
        extra = new TreeSet<>();
        String similar = null;

        for (String i : checkSet) {
            deletion(i);
            transposition(i);
            alteration(i);
            insertion(i);
        }

        if (distance == 0) {
            deletion(word);
            transposition(word);
            alteration(word);
            insertion(word);
        }

        int greatestVal = 0;
        for (String i : found) {
            int val = dictionary.find(i).getValue();
            if (val > greatestVal) { similar = i; greatestVal = val; }
        }

        this.distance++;
        return similar;
    }

    /** deletion
     *
     * adds all words with deletion distance 1 from @word to the proper set (found if in dictionary, extra if not)
     *
     * @param word: the word to check
     */
    public void deletion(String word) {
        for (int i = 0; i < word.length(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(word.substring(0, i));
            if ((i + 1) < word.length()) { sb.append(word.substring(i + 1)); }

            String check = sb.toString();
            ITrie.INode wordNode = dictionary.find(check);
            if (wordNode != null && wordNode.getValue() > 0) { found.add(check); }
            else { extra.add(check); }
        }
    }

    /** transposition
     *
     * adds all words with transposition distance 1 from @word to the proper set (found if in dictionary, extra if not)
     *
     * @param word: the word to check
     */
    public void transposition(String word) {
        for (int i = 0; i < word.length() - 1; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(word.substring(0, i));
            sb.append(word.substring(i + 1, i + 2));
            sb.append(word.substring(i, i + 1));
            if ((i + 2) < word.length()) { sb.append(word.substring(i + 2)); }

            String check = sb.toString();

            ITrie.INode wordNode = dictionary.find(check);
            if (wordNode != null && wordNode.getValue() > 0) { found.add(check); }
            else { extra.add(check); }
        }
    }

    /** alteration
     *
     * adds all words with alteration distance 1 from @word to the proper set (found if in dictionary, extra if not)
     *
     * @param word: the word to check
     */
    public void alteration(String word) {
        for (int i = 0; i < word.length(); i++) {
            for (int j = 0; j < Trie.NUM_LETTERS; j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(word.substring(0, i));
                sb.append((char)(j + 'a'));
                if ((i + 1) < word.length()) { sb.append(word.substring(i + 1)); }

                String check = sb.toString();
                ITrie.INode wordNode = dictionary.find(check);
                if (wordNode != null && wordNode.getValue() > 0) { found.add(check); }
                else { extra.add(check); }
            }
        }
    }

    /** insertion
     *
     * adds all words with insertion distance 1 from @word to the proper set (found if in dictionary, extra if not)
     *
     * @param word: the word to check
     */
    public void insertion(String word) {
        for (int i = 0; i < word.length() + 1; i++) {
            for (int j = 0; j < Trie.NUM_LETTERS; j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(word.substring(0, i));
                sb.append((char)(j + 'a'));
                if (i < word.length()) { sb.append(word.substring(i)); }

                String check = sb.toString();
                ITrie.INode wordNode = dictionary.find(check);
                if (wordNode != null && wordNode.getValue() > 0) { found.add(check); }
                else { extra.add(check); }
            }
        }
    }
}
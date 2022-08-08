package wordle;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DictionaryHandler {

    private List<String> dictionary;
    private final Map<String, Integer> wordsWeight;

    public DictionaryHandler(String fileName) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            this.dictionary =
                    lines
                            .map(String::toLowerCase)
                            .distinct()
                            .filter(word -> word.matches("[a-zа-я]{5}"))
                            .collect(Collectors.toList());

        }
        wordsWeight = getWordsWeight(this.dictionary);
        dictionary.sort((a, b) -> wordsWeight.get(b) - wordsWeight.get(a));

        Path path = Paths.get("words.txt");
        Files.write(path, dictionary);

    }

    public List<String> getDictionary() {
        return dictionary;
    }

// --Commented out by Inspection START (08.02.2022 12:41):
//    public List<String> removeWrongWordsOld(String mask, String word) {
//
//        List<Character> disallowedChars = new ArrayList<>();
//        List<Character> knownChars = new ArrayList<>();
//        Map<Integer, Character> charsPositions = new HashMap<>();
//        Map<Character, List<Integer>> wrongPositions = new HashMap<>();
//
//        for (int k = 0; k < 5; k++) {
//            char currentCharInWord = word.charAt(k);
//            switch (mask.charAt(k)) {
//                case ('G'):
//                    charsPositions.putIfAbsent(k, currentCharInWord);
//                    break;
//                case ('Y'):
//                    knownChars.add(currentCharInWord);
//                    if (wrongPositions.get(currentCharInWord) == null) {
//                        List<Integer> lst = new ArrayList<>();
//                        lst.add(k);
//                        wrongPositions.put(currentCharInWord, lst);
//                    } else {
//                        List<Integer> lst = wrongPositions.get(currentCharInWord);
//                        lst.add(k);
//                        wrongPositions.put(currentCharInWord, lst);
//                    }
//                    break;
//                case ('g'):
//                    disallowedChars.add(currentCharInWord);
//                    break;
//                default:
//                    System.out.println("Wrong char at mask position " + k + 1);
//                    break;
//            }
//        }
//        return dictionary
//                .stream()
//                .filter(
//                        wordFromDictionary -> {
//                            for (char c : disallowedChars) {
//                                if (wordFromDictionary.indexOf(c) >= 0) return false;
//                            }
//                            for (char c : knownChars) {
//                                if (wordFromDictionary.indexOf(c) < 0) return false;
//                            }
//                            for (int i : charsPositions.keySet()) {
//                                if (wordFromDictionary.charAt(i) != charsPositions.get(i)) {
//                                    return false;
//                                }
//                            }
//                            for (Character c : wrongPositions.keySet()) {
//                                for (int i : wrongPositions.get(c)) {
//                                    if (wordFromDictionary.charAt(i) == c) return false;
//                                }
//                            }
//                            return true;
//                        }
//                )
//                .collect(Collectors.toList());
//    }
// --Commented out by Inspection STOP (08.02.2022 12:41)

    public List<String> removeWrongWords(String mask, String word) {
        List<String> tempDictionary = new ArrayList<>();
        for (String wordFromDictionary : dictionary) {
            if (isValidWord(wordFromDictionary, mask, word)) {
                tempDictionary.add(wordFromDictionary);
            }

        }
        return tempDictionary;
    }

    private boolean isValidWord(String wordFromDictionary, String mask, String word) {
        for (int i = 0; i < 5; i++) {
            char charAtMask = mask.charAt(i);
            char charAtWord = word.charAt(i);
            char charAtDictionaryWord = wordFromDictionary.charAt(i);
            switch (charAtMask) {
                case ('g'):
                    if (wordFromDictionary.indexOf(charAtWord) >= 0) return false;
                    break;
                case ('G'):
                    if (charAtDictionaryWord != charAtWord) return false;
                    break;
                case ('Y'):
                    if (charAtDictionaryWord == charAtWord || wordFromDictionary.indexOf(charAtWord) < 0) return false;
                    break;
                default:
                    System.out.println("Something bad happens...");
            }
        }
        return true;
    }

    public long getWordWeight(String word) {
        return wordsWeight.get(word);
    }

    private Map<String, Integer> getWordsWeight(List<String> dictionary) {

        Map<Character, Integer> charOccurrenceMap = getCharacterOccurrenceMap(dictionary);

        Map<String, Integer> wordsWeightMap = new HashMap<>();
        for (String s : dictionary) {
            Integer wordWeight = 0;
            String tempWord = s;

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (charOccurrenceMap.get(c) != null & tempWord.substring(0, i).indexOf(c) < 0) {
                    tempWord = s;
                    wordWeight += charOccurrenceMap.get(c);
                }
                wordsWeightMap.put(s, wordWeight);
            }
        }
        return wordsWeightMap;
    }

    private Map<Character, Integer> getCharacterOccurrenceMap(List<String> dictionary) {
        String allWordsAtString = String.join("", dictionary);
        Map<Character, Integer> charOccurrenceMap = new HashMap<>();
        for (int i = 0; i < allWordsAtString.length(); i++) {
            char c = allWordsAtString.charAt(i);
            Integer count = charOccurrenceMap.get(c);
            if (count == null) charOccurrenceMap.put(c, 1);
            else {
                count++;
                charOccurrenceMap.put(c, count);
            }
        }
        return charOccurrenceMap;
    }

    public void setDictionary(List<String> dictionary) {
        this.dictionary = dictionary;
    }
}

package wordle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DictionaryHandler {
    private List<String> dictionary;
    private Map<String, Long> wordsWeight = new HashMap<>();

    public DictionaryHandler(String fileName) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {

            List<String> words = new ArrayList<>();
            this.dictionary = lines
                    .map(String::toLowerCase)
                    .filter(word -> word.matches("[a-z]{5}"))
                    .collect(Collectors.toList());

        }
        wordsWeight = getWordsWeight(this.dictionary);
        dictionary = dictionary
                .stream()
                .sorted(
                        (a, b) -> {
                            return (int) (wordsWeight.get(b) - wordsWeight.get(a));
                        })
                .collect(Collectors.toList());
    }

    public List<String> getDictionary() {
        return this.dictionary;
    }

    public List<String> getWordsByMask(List<Character> disallowedChars,
                                       List<Character> knownChars,
                                       Map<Integer, Character> positionedChars,
                                       Map<Character, List<Integer>> wrongPositions) {
        return
                dictionary.
                        stream()
                        .filter(word -> {
                                    for (char c : disallowedChars) {
                                        if (word.indexOf(c) >= 0) return false;
                                    }
                                    for (char c : knownChars) {
                                        if (word.indexOf(c) < 0) return false;
                                    }
                                    for (int i : positionedChars.keySet()) {
                                        if (word.charAt(i) != positionedChars.get(i)) {
                                            return false;
                                        }
                                    }
                                    for (Character c : wrongPositions.keySet()) {
                                        for (int i : wrongPositions.get(c)) {
                                            if (word.charAt(i) == c) return false;
                                        }
                                    }
                                    return true;
                                }
                        )
                        .collect(Collectors.toList());
    }

    public long getWordWeight(String word){
        return wordsWeight.get(word);
    }

    private Map<String, Long> getWordsWeight(List<String> dictionary) {

        char[] abc = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        String allWordsAtString = String.join("", dictionary);
        Map<Character, Long> charOccurrenceMap = new HashMap<>();
        for (char c : abc) {
            Long occurrencesCount = allWordsAtString.chars().filter(ch -> ch == c).count();
            charOccurrenceMap.put(c, occurrencesCount);
        }

        Map<String, Long> wordsWeightMap = new HashMap<>();
        for (String s : dictionary) {
            Long wordWeight = Long.valueOf(0);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (charOccurrenceMap.get(c) != null) {
                    wordWeight += charOccurrenceMap.get(c);
                }
                wordsWeightMap.put(s, wordWeight);
            }

        }

        return wordsWeightMap;


    }
}

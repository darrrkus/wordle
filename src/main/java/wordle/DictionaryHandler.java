package wordle;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            this.dictionary = lines
                    .map(String::toLowerCase)
                    .distinct()
                    .filter(word -> word.matches("[a-zа-я]{5}"))
                    .collect(Collectors.toList());

        }
        wordsWeight = getWordsWeight(this.dictionary);
        dictionary = dictionary
                .stream()
                .sorted(
                        (a, b) -> (wordsWeight.get(b) - wordsWeight.get(a)))
                .collect(Collectors.toList());
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

    private Map<String, Integer> getWordsWeight(List<String> dictionary) {

        String allWordsAtString = String.join("", dictionary);
        Map<Character, Integer> charOccurrenceMap = new HashMap<>();
        for (int i = 0; i < allWordsAtString.length(); i++) {
            char c = allWordsAtString.charAt(i);
            Integer count = charOccurrenceMap.get(c);
            if (count  == null) charOccurrenceMap.put(c, 1);
            else {
                count++;
                charOccurrenceMap.put(c, count);
            }
        }

        Map<String, Integer> wordsWeightMap = new HashMap<>();
        for (String s : dictionary) {
            Integer wordWeight = 0;
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

package wordle;

import java.io.IOException;
import java.util.*;

public class WordleHelper {


    public static void main(String[] args) {


        String fileName = args.length > 0 ? args[0] : "american.txt";

        DictionaryHandler dictionaryHandler;
        try {
            dictionaryHandler = new DictionaryHandler(fileName);
        } catch (IOException e) {
            System.out.printf("Dictionary file %s is missing or corrupted\n", fileName);
            return;
        }

        for (int i = 1; i <= 6; i++) {

            List<Character> disallowedChars = new ArrayList<>();
            Map<Integer, Character> positionedChars = new HashMap<>();
            List<Character> knownChars = new ArrayList<>();
            Map<Character, List<Integer>> wrongPositions = new HashMap<>();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Iteration # " + i);
            String word;
            do {
                System.out.print("Input word >> ");
                word = scanner.next();
            } while (!word.matches("[a-zа-я]{5}"));
            String mask;
            do {
                System.out.print("Input answer (G-green, Y-yellow, g-gray letter)  >> ");
                mask = scanner.next();
            } while (!mask.matches("[GgY]{5}"));

            if ("GGGGG".equals(mask)) {
                System.out.println("You win. Game is over!");
                break;
            }
            for (int k = 0; k < 5; k++) {
                char currentCharInWord = word.charAt(k);
                switch (mask.charAt(k)) {
                    case ('G'):
                        positionedChars.putIfAbsent(k, currentCharInWord);
                        break;
                    case ('Y'):
                        knownChars.add(currentCharInWord);
                        if (wrongPositions.get(currentCharInWord) == null) {
                            List<Integer> lst = new ArrayList<>();
                            lst.add(k);
                            wrongPositions.put(currentCharInWord, lst);
                        } else {
                            List<Integer> lst = wrongPositions.get(currentCharInWord);
                            lst.add(k);
                            wrongPositions.put(currentCharInWord, lst);
                        }
                        break;
                    case ('g'):
                        disallowedChars.add(currentCharInWord);
                        break;
                    default:
                        System.out.println("Wrong char at mask position " + k + 1);
                        break;
                }
            }
            dictionaryHandler.removeWrongWords(disallowedChars, knownChars, positionedChars, wrongPositions);
            System.out.println("\n=============================");
            System.out.printf("Alternatives (%d words):\n",dictionaryHandler.getDictionary().size());
            int counter = 0;
            for (String s : dictionaryHandler.getDictionary()) {
                System.out.printf("%s - %d\t", s, dictionaryHandler.getWordWeight(s));

                if (counter++ == 5) {
                    System.out.println();
                    counter = 0;
                }
            }
            System.out.println();

        }


    }


}

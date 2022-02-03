package wordle;

import java.io.IOException;
import java.util.*;

public class WordleHelper {


    public static void main(String[] args) throws IOException {

        List<Character> disallowedChars = new ArrayList<>();
        Map<Integer, Character> positionedChars = new HashMap<>();
        List<Character> knownChars = new ArrayList<>();
        Map<Character, List<Integer>> wrongPositions = new HashMap<>();
        String fileName = args.length > 0 ? args[0] : "american.txt";

        DictionaryHandler dictionary = new DictionaryHandler(fileName);

        for (int i = 1; i <= 6; i++) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Iteration # " + i);
            System.out.print("Input word >> ");
            String word = scanner.next();
            System.out.print("Input answer (G-green, Y-yellow, g-gray letter)  >> ");
            String mask = scanner.next();
            if ("GGGGG".equals(mask)) {
                System.out.println("You win. Game is over!");
                break;
            }
            for (int k = 0; k < 5; k++) {
                switch (mask.charAt(k)) {
                    case ('G'):
                        positionedChars.putIfAbsent(k, word.charAt(k));
                        break;
                    case ('Y'):
                        knownChars.add(word.charAt(k));
                        if (wrongPositions.get(word.charAt(k)) == null) {
                            List<Integer> lst = new ArrayList<>();
                            lst.add(k);
                            wrongPositions.put(word.charAt(k), lst);
                        } else {
                            List<Integer> lst = wrongPositions.get(word.charAt(k));
                            lst.add(k);
                            wrongPositions.put(word.charAt(k), lst);
                        }
                        break;
                    case ('g'):
                        disallowedChars.add(word.charAt(k));
                        break;
                    default:
                        System.out.println("Wrong char at mask position " + k + 1);
                        break;
                }
            }
            List<String> validWords = dictionary.getWordsByMask(disallowedChars, knownChars, positionedChars, wrongPositions);
            System.out.println("Alternatives:");
            int counter = 0;
            for (String s : validWords) {
//                System.out.printf("%s\t", s);
                System.out.printf("%s - %d\t",s,dictionary.getWordWeight(s));

                if (counter++ == 6) {
                    System.out.println();
                    counter = 0;
                }
            }
            System.out.println();
//            System.out.println(String.join("\n", validWords));

        }


    }


}

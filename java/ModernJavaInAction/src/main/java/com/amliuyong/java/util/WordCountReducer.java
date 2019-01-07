package com.amliuyong.java.util;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WordCountReducer {

    static class WordCounter {
        int counter;
        boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                if (lastSpace) {
                    return this;
                } else {
                    return new WordCounter(counter, true);
                }
            } else {
                if (lastSpace) {
                    return new WordCounter(counter + 1, false);
                } else {
                    return this;

                }

            }
        }

        public WordCounter combine(WordCounter that) {
            return new WordCounter(this.counter + that.counter, that.lastSpace);
        }

        public int getCounter() {
            return this.counter;
        }
    }


    public static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(
                new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine);

        return wordCounter.getCounter();
    }

    public static void main(String... args) {
        String content = "aaa    bbbb  d   dd  eee fffff\n" +
                "aaa  \t    aa ccc";

        Stream<Character> stream =
                IntStream.range(0, content.length())
                        .mapToObj(content::charAt);

        int charCount = countWords(stream);
        System.out.println(charCount);

        //System.out.println(Character.isWhitespace('\n'));
    }


}

package com.amliuyong.java.collections;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class JavaCollections {

    Map<String, byte[]> dataToHash = new HashMap<>();
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

    public JavaCollections() throws NoSuchAlgorithmException {
    }


    public static void main(String... args) {
        //sortMap();
        map_merge();
    }

    public static void sortMap() {
        Map<String, String> favouriteMovies = Map.ofEntries(
                entry("Raphael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James Bond")
        );
        favouriteMovies
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(System.out::println);

        System.out.println(favouriteMovies);
    }

    public void map_computeIfAbsent() {
        Map<String, byte[]> dataToHash = new HashMap();
        List<String> lines = List.of("Aaaaaaa", "bbbbbb", "cccccc");
        lines.forEach(line -> dataToHash.computeIfAbsent(line, this::calculateDigest));

    }

    private byte[] calculateDigest(String key) {
        return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
    }

    public void map_computeIfAbsent2() {
        Map<String, List<String>> favouriteMovies = new HashMap<>();
        favouriteMovies
                .computeIfAbsent("Raphael", name -> new ArrayList<>())
                .add("Star Wars");
    }

    public void map_replaceAll() {
        Map<String, String> favouriteMovies = new HashMap<>();
        favouriteMovies.put("Raphael", "Star Wars");
        favouriteMovies.put("Olivia", "james bond");
        favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println(favouriteMovies);
    }

    public static void map_merge() {
        Map<String, String> family = Map.ofEntries(
                entry("Teo", "Star Wars"),
                entry("Yong", "I hate it"),
                entry("Cristina", "James Bond1")
        );

        Map<String, String> friends = Map.ofEntries(
                entry("Raphael", "Star Wars"),
                entry("Cristina", "James Bond2")
        );

        Map<String, String> everyone = new HashMap<>(family);
        everyone.putAll(friends);

        System.out.println(everyone);


        everyone.merge("Yong", "I love it", (v1, v2) -> {
            System.out.println("old: " + v1 + ", new: " + v2);
            return v2;
        });

        System.out.println(everyone);


    }


}

package com.amliuyong.java.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SubSet {

    public static void main(String... args) {
        List<List<Integer>> subsets = getSubSet(List.of(1, 4, 9));
        System.out.println(subsets);

    }

    public static List<List<Integer>> getSubSet(List<Integer> list) {
        if (list.isEmpty()) {
            List<List<Integer>> emptySet = new ArrayList<>();
            emptySet.add(Collections.emptyList());
            return emptySet;
        }

        Integer first = list.get(0);
        List<Integer> rest = list.subList(1, list.size());

        List<List<Integer>> subset1 = getSubSet(rest);

        List<List<Integer>> subset2 = insertAll(first, subset1);

        return contact(subset1, subset2);

    }

    private static List<List<Integer>> contact(List<List<Integer>> subset1, List<List<Integer>> subset2) {
        List<List<Integer>> list = new ArrayList<>();
        list.addAll(subset1);
        list.addAll(subset2);
        return list;
    }

    private static List<List<Integer>> insertAll(Integer first, List<List<Integer>> subset1) {
        List<List<Integer>> list = new ArrayList<>();
        list.addAll(
                subset1.stream().map(l -> {
                    List<Integer> ll = new ArrayList<>();
                    ll.add(first);
                    ll.addAll(l);
                    return ll;
                }).collect(toList())
        );

        return list;
    }
}

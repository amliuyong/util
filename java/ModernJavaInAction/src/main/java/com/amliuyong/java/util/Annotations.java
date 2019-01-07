package com.amliuyong.java.util;

import io.reactivex.annotations.NonNull;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Annotations {
    Book book = new Book();

    @NonNull String name = book.getName();

    // List<@NonNull Car> cars = new ArrayList<>();

    public static void main(String[] args) {
        Author2[] authors = Book2.class.getAnnotationsByType(Author2.class);
        Arrays.asList(authors).forEach(a -> {
            System.out.println(a.name());
        });
    }
}


@interface Author {
    String name();
}

@interface Authors {
    Author[] value();
}


@Authors(
        {@Author(name = "Raoul"), @Author(name = "Mario"), @Author(name = "Alan")}
)
class Book {
    public String getName() {
        return "OKName";
    }
}

@Repeatable(Authors2.class)
@Retention(RetentionPolicy.RUNTIME)
@interface Author2 {
    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@interface Authors2 {
    Author2[] value();
}


@Author2(name = "Raoul")
@Author2(name = "Mario")
@Author2(name = "Alan")
class Book2 {
}


class Car {

}


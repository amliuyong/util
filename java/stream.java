

import static java.util.stream.Collectors.*;

/*

Collectors:
- toList/toSet/toCollection
- counting
- summingInt/averagingInt/summarizingInt
- joining  -> String
- maxBy    -> Optional<T>
- minBy    -> Optional<T>
- reducing
- collectingAndThen
- groupingBy
- partitioningBy

*/

//Slicing
List<Dish> slicedMenu1 = specialMenu.stream().takeWhile(dish -> dish.getCalories() < 320).collect(toList());
List<Dish> slicedMenu2 = specialMenu.stream().dropWhile(dish -> dish.getCalories() < 320).collect(toList());



List<String> uniqueCharacters = words.stream().map(word -> word.split("")).flatMap(Arrays::stream).distinct().collect(toList());


List<Integer> numbers1 = Arrays.asList(1, 2, 3);
List<Integer> numbers2 = Arrays.asList(3, 4);
List<int[]> pairs = numbers1.stream()
    .flatMap(i -> numbers2.stream().map(j -> new int[]{i, j}))
    .collect(toList());


int sum = numbers.stream().reduce(0, Integer::sum);
Optional<Integer> sum = numbers.stream().reduce((a, b) -> (a + b));


OptionalInt maxCalories = menu.stream().mapToInt(Dish::getCalories).max();
Optional<Integer> highestValue = transactions.stream().map(Transaction::getValue).reduce(Integer::max);
Optional<Integer> max = numbers.stream().reduce(Integer::max);


int calories = menu.stream().map(Dish::getCalories).reduce(0, Integer::sum);
int calories = menu.stream().map(Dish::getCalories).sum();

String traderStr = transactions.stream().map(transaction -> transaction.getTrader().getName()).distinct().sorted().reduce("", (n1, n2) -> n1 + n2);
String traderStr = transactions.stream().map(transaction -> transaction.getTrader().getName()).distinct().sorted().collect(joining());

int count = menu.stream().map(d -> 1).reduce(0, (a, b) -> a + b);
long count = menu.stream().count();


Stream<int[]> pythagoreanTriples =
IntStream.rangeClosed(1, 100).boxed().flatMap(
    a ->IntStream.rangeClosed(a, 100).filter(b -> Math.sqrt(a*a + b*b) % 1 == 0).mapToObj(b ->new int[]{a, b, (int)Math.sqrt(a * a + b * b)})
);

Stream<double[]> pythagoreanTriples =
IntStream.rangeClosed(1, 100).boxed()
    .flatMap(
        a ->IntStream.rangeClosed(a, 100).mapToObj(b -> new double[]{a, b, Math.sqrt(a*a + b*b)}).filter(t -> t[2] % 1 == 0)
    );


Stream<String> stream = Stream.of("Modern ", "Java ", "In ", "Action");
Stream<String> emptyStream = Stream.empty();

//Stream from nullable
Stream<String> values = Stream.of("config", "home", "user").flatMap(key -> Stream.ofNullable(System.getProperty(key)));

//Streams from arrays
int[] numbers = {2, 3, 5, 7, 11, 13};
int sum = Arrays.stream(numbers).sum();


//Streams from files
long uniqueWords = 0;
try(Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())){
    uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" "))).distinct().count();
}
catch(IOException e){
}


IntStream.iterate(0, n -> n < 100, n -> n + 4).forEach(System.out::println);

Stream.generate(Math::random).limit(5).forEach(System.out::println);


//# Fibonacci
Stream.iterate(new int[]{0, 1},
              t -> new int[]{t[1], t[0]+t[1]}
              )
.limit(20)
.forEach(t -> System.out.println("(" + t[0] + "," + t[1] +")"));


Stream.iterate(new int[]{0, 1},
              t -> new int[]{t[1],t[0] + t[1]})
.limit(10)
.map(t -> t[0])
.forEach(System.out::println);


//a lambda is that the anonymous class can define state via fields
IntSupplier fib = new IntSupplier(){
    private int previous = 0;
    private int current = 1;
    public int getAsInt(){
        int oldPrevious = this.previous;
        int nextValue = this.previous + this.current;
        this.previous = this.current;
        this.current = nextValue;
        return oldPrevious;
    }
};
IntStream.generate(fib).limit(10).forEach(System.out::println);




long howManyDishes = menu.stream().collect(Collectors.counting());
long howManyDishes = menu.stream().count();


String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));

//--------------------------------------------//
// collect - reducing

//misusing the reduce method
//The *reduce* method is meant to combine two values and produce a new one; it’s an immutable reduction.
//In contrast, the *collect* method is designed to mutate a container to accumulate the result it’s supposed to produce.
Stream<Integer> stream = Arrays.asList(1, 2, 3, 4, 5, 6).stream();
List<Integer> numbers = stream.reduce(
                new ArrayList<Integer>(),
                (List<Integer> l, Integer e) -> {
                    l.add(e);
                    l;
                },
                (List<Integer> l1, List<Integer> l2) -> {
                    l1.addAll(l2);
                    return l1;
                }
            );


Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));

Optional<Dish> mostCalorieDish = menu.stream().collect(reducing( (d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2) );

int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum));
int totalCalories = menu.stream().map(Dish::getCalories).reduce(Integer::sum).get();

String shortMenu = menu.stream().map(Dish::getName).collect(joining());
String shortMenu = menu.stream().map(Dish::getName).collect( reducing( (s1, s2) -> s1 + s2 ) ).get();
String shortMenu = menu.stream().collect( reducing( "", Dish::getName, (s1, s2) -> s1 + s2 ) );
//This doesn’t compile because the one argument that reducing accepts is aBinaryOperator<T> that’s a BiFunction<T,T,T>.
String shortMenu = menu.stream().collect( reducing( (d1, d2) -> d1.getName() + d2.getName() )).get();


//groupingBy

Map<Dish.Type, List<Dish>> dishesByType =
menu.stream().collect(groupingBy(Dish::getType));

Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
menu.stream().collect(
    groupingBy(dish -> {
        if (dish.getCalories() <= 400) return CaloricLevel.DIET;
        else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
        }
    )
);


Map<Dish.Type, List<Dish>> caloricDishesByType =
menu.stream().filter(dish -> dish.getCalories() > 500).collect(groupingBy(Dish::getType));

Map<Dish.Type, List<Dish>> caloricDishesByType =
menu.stream().collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));



Map<Dish.Type, List<String>> dishNamesByType =
menu.stream().collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));


Map<Dish.Type, Set<String>> dishNamesByType =
menu.stream().collect(
    groupingBy(Dish::getType,
               flatMapping(dish -> dishTags.get( dish.getName() ).stream(), toSet())
    )
);


Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
menu.stream().collect(
    groupingBy(Dish::getType,
                groupingBy(dish -> {
                        if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                        else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                        else return CaloricLevel.FAT;
                    }
                )
    )
);

Map<Dish.Type, Long> typesCount =
menu.stream().collect(groupingBy(Dish::getType, counting()));


Map<Dish.Type, Optional<Dish>> mostCaloricByType =
menu.stream().collect(
    groupingBy(Dish::getType,
              maxBy(comparingInt(Dish::getCalories)))
    );

Map<Dish.Type, Dish> mostCaloricByType =
menu.stream().collect(groupingBy(Dish::getType,
    collectingAndThen(
        maxBy(comparingInt(Dish::getCalories)),
        Optional::get)
    )
);


Map<Dish.Type, Integer> totalCaloriesByType =
menu.stream().collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));


Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
menu.stream().collect(
    groupingBy(Dish::getType, mapping(dish -> {
        if (dish.getCalories() <= 400) return CaloricLevel.DIET;
        else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
        },
        toSet()  //mapping
       ) // groupingBy
    )
);


Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
menu.stream().collect(
    groupingBy(Dish::getType, mapping(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel. FAT;
            },
            toCollection(HashSet::new)
        ) //mapping
    ) //groupingBy
);


//partitioningBy

Map<Boolean, Dish> mostCaloricPartitionedByVegetarian =

menu.stream().collect(partitioningBy(Dish::isVegetarian,
    collectingAndThen(
        maxBy(comparingInt(Dish::getCalories)), Optional::get
        )
    )
);

Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType =

menu.stream().collect(
    partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType))
    );


Map<Boolean, Map<Boolean, List<Dish>>> var1 =
menu.stream().collect(partitioningBy(Dish::isVegetarian, partitioningBy(d -> d.getCalories() > 500)));

Map<Boolean, Integer> var2 =
menu.stream().collect(partitioningBy(Dish::isVegetarian, counting()));



// -------------------------------------//
// Collector interface


public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    Function<A, R> finisher();
    BinaryOperator<A> combiner();
    Set<Characteristics> characteristics();
}


List<Dish> dishes = menuStream.collect(
     ArrayList::new,  //supplier
     List::add,       //accumulator
     List::addAll     //combiner
     );


// partitionPrimes

public boolean isPrime(int candidate) {
    int candidateRoot = (int) Math.sqrt((double) candidate);
    return IntStream.rangeClosed(2, candidateRoot).noneMatch(i -> candidate % i == 0);
}

public Map<Boolean, List<Integer>> partitionPrimes(int n) {
    return IntStream.rangeClosed(2, n).boxed()
           .collect(
                    partitioningBy(candidate -> isPrime(candidate))
                  );
}


// PrimeNumbersCollector

// java9
public static boolean isPrime(List<Integer> primes, int candidate){
    int candidateRoot = (int) Math.sqrt((double) candidate);
    return primes.stream()
         .takeWhile(i -> i <= candidateRoot)
         .noneMatch(i -> candidate % i == 0);
}


//java8 - takeWhile

public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
    int i = 0;
    for (A item : list) {
        if (!p.test(item)) {
            return list.subList(0, i);
        }
        i++;
    }
    return list;
}

public static boolean isPrime(List<Integer> primes, int candidate){
    int candidateRoot = (int) Math.sqrt((double) candidate);
    return takeWhile(primes, i -> i <= candidateRoot)
           .stream().noneMatch(p -> candidate % p == 0);
}


//PrimeNumbersCollector

public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<Boolean, List<Integer>>() {{
            put(true, new ArrayList<Integer>());
            put(false, new ArrayList<Integer>());
        }};
    }


    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
                   acc.get( isPrime(acc.get(true), candidate) )
                   .add(candidate);
               };
    }


    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
                map1.get(true).addAll(map2.get(true));
                map1.get(false).addAll(map2.get(false));
                return map1;
               };
    }


    public Function<Map<Boolean, List<Integer>>,Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }


    public Set<Characteristics> characteristics() {
         return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
    }

}


public Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
     return IntStream.rangeClosed(2, n).boxed().collect(new PrimeNumbersCollector());
}








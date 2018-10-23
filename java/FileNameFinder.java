package am.liuyong;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileNameFinder {
    private String keyWord = ".md";
    private String rootDir = "/Users/yongliu/Documents/github";

    private Queue<Path> queue = new ConcurrentLinkedQueue<>();
    private Queue<Thread> threads = new ConcurrentLinkedQueue<>();

    private Queue<Path> poolFileList = new ConcurrentLinkedQueue<>();
    private Queue<Future<?>> poolFutures = new ConcurrentLinkedQueue<>();


    private Queue<Path> completableFuList = new ConcurrentLinkedQueue<>();
    private Queue<CompletableFuture<List<Path>>> completableFutures = new ConcurrentLinkedQueue<>();


    private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

    public FileNameFinder(String keyWord, String rootDir) {
        this.keyWord = keyWord;
        this.rootDir = rootDir;
    }

    public FileNameFinder() {

    }

    public List<Path> findDirect() {
        return this.findDirect(Paths.get(this.rootDir));
    }

    private List<Path> findDirect(Path path) {

        File f = path.toFile();

        File[] files = f.listFiles();

        List<Path> resultFiles = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (isTheFile(files[i].toPath())) {
                resultFiles.add(files[i].toPath());
            } else if (files[i].isDirectory()) {
                resultFiles.addAll(findDirect(files[i].toPath()));
            }
        }

        return resultFiles;
    }


    public List<Path> findByMutiThreads() {
        this.findByMutiThreads(Paths.get(this.rootDir));

        this.threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        List<Path> result = new ArrayList<>(this.queue);
        return result;

    }

    private void findByMutiThreads(Path path) {
        File f = path.toFile();
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (isTheFile(files[i].toPath())) {
                this.queue.offer(files[i].toPath());

            } else if (files[i].isDirectory()) {
                int j = i;
                Thread t = new Thread(() -> {
                    this.findByMutiThreads(files[j].toPath());

                });
                threads.add(t);
                t.start();
            }
        }
    }

    private void findByThreadPool(Path path) {
        File f = path.toFile();
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (isTheFile(files[i].toPath())) {
                this.poolFileList.add(files[i].toPath());
            } else if (files[i].isDirectory()) {
                int j = i;
                Future<?> future = this.pool.submit(() -> {
                    this.findByThreadPool(files[j].toPath());
                });
                this.poolFutures.add(future);
            }
        }

    }

    public List<Path> findByThreadPool() {
        this.findByThreadPool(Paths.get(this.rootDir));
        this.poolFutures.forEach(fu -> {
            try {
                fu.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        this.pool.shutdown();
        return new ArrayList<>(this.poolFileList);
    }

    public List<Path> findByFind() {
        Path p = Paths.get(this.rootDir);
        List<Path> list = null;
        try {
            list = Files.find(p, 999, (path, attr) -> {
                return isTheFile(path);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Path> findByWalk() {
        Path p = Paths.get(this.rootDir);
        List<Path> list = null;

        try {
            list = Files.walk(p).filter(this::isTheFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        return list;
    }


    public List<Path> findByWalkTree() {
        Path p = Paths.get(this.rootDir);
        List<Path> list = new ArrayList<>();
        try {
            Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (isTheFile(file)) {
                        list.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    private void findByCompletableFuture(Path path) {
        File f = path.toFile();
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (isTheFile(files[i].toPath())) {
                completableFuList.add(files[i].toPath());
            } else if (files[i].isDirectory()) {
                final Path dir = files[i].toPath();
                CompletableFuture fu = CompletableFuture.runAsync(() -> {
                    findByCompletableFuture(dir);
                });
                completableFutures.add(fu);
            }
        }

    }

    public List<Path> findByCompletableFuture() {
        completableFutures = new ConcurrentLinkedQueue<>();
        completableFuList = new ConcurrentLinkedQueue<>();

        findByCompletableFuture(Paths.get(this.rootDir));

        completableFutures.forEach(f -> f.join());
        return new ArrayList<>(completableFuList);
    }


    private List<Path> findByCompletableFutureNotWork(Path path) {
        File f = path.toFile();
        File[] files = f.listFiles();

        List<Path> list = new ArrayList<>();
        List<CompletableFuture> fuList = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (isTheFile(files[i].toPath())) {
                list.add(files[i].toPath());
            } else if (files[i].isDirectory()) {
                final Path dir = files[i].toPath();
                CompletableFuture fu = CompletableFuture.supplyAsync(() -> {
                    return findByCompletableFutureNotWork(dir);
                }).thenAccept(lis -> list.addAll(lis));

                fuList.add(fu);
            }
        }

        fuList.forEach(fu -> fu.join());
        return list;
    }

    public List<Path> findByCompletableFutureNotWork() {
        return findByCompletableFutureNotWork(Paths.get(this.rootDir));
    }


    private List<CompletableFuture<Path>> findAsynByCompletableFuture(Path path) {
        File dir = path.toFile();

        List<CompletableFuture<Path>> result = new ArrayList();

        Arrays.asList(dir.listFiles()).forEach(f -> {
            if (f.isDirectory()) {
                result.addAll(findAsynByCompletableFuture(f.toPath()));

            } else if (this.isTheFile(f.toPath())) {
                result.add(CompletableFuture.supplyAsync(() -> f.toPath()));
            }
        });

        return result;
    }


    public List<Path> findAsynByCompletableFuture() {
        return findAsynByCompletableFuture(Paths.get(this.rootDir)).stream().map(fu -> fu.join()).collect(Collectors.toList());
    }


    private Stream<CompletableFuture<Path>> findAsynStreamByCompletableFuture(Path path) {
        File dir = path.toFile();
        List<CompletableFuture<Path>> result = new ArrayList();
        return Stream.of(dir.listFiles()).flatMap(f -> {
            if (isTheFile(f.toPath())) {
                return Stream.of(CompletableFuture.supplyAsync(() -> f.toPath()));
            } else if (f.isDirectory()) {
                return findAsynStreamByCompletableFuture(f.toPath());
            } else {
                return Stream.empty();
            }
        });
    }

    public List<Path> findAsynStreamByCompletableFuture() {
        return findAsynStreamByCompletableFuture(Paths.get(this.rootDir)).map(cfu -> cfu.join()).collect(Collectors.toList());
    }


    private CompletableFuture<List<Path>> findAsyn2ByCompletableFuture(Path path) {
        File dir = path.toFile();

        List<Path> result = new ArrayList();

        List<CompletableFuture<List<Path>>> subResult = new ArrayList<>();

        Arrays.asList(dir.listFiles()).forEach(f -> {
            if (f.isDirectory()) {
                subResult.add(findAsyn2ByCompletableFuture(f.toPath()));

            } else if (this.isTheFile(f.toPath())) {
                result.add(f.toPath());
            }
        });


        return CompletableFuture.supplyAsync(
                () -> {
                    subResult.forEach(cfu -> result.addAll(cfu.join()));
                    return result;
                }
        );

    }

    public List<Path> findAsyn2ByCompletableFuture() {
        return findAsyn2ByCompletableFuture(Paths.get(this.rootDir)).join();
    }

    private boolean isTheFile(Path path) {
        if (Files.isRegularFile(path)) {
            return path.toFile().getName().endsWith(this.keyWord);
        } else {
            return false;
        }
    }


    /**
     *
     * findDirect Total:7831, time:2604 millis
     * findByMutiThreads Total:7831, time:1441 millis
     * findByThreadPool Total:7830, time:616 millis
     * findByCompletableFuture Total:7831, time:605 millis
     * findByFind Total:7831, time:1929 millis
     * findByWalk Total:7831, time:2025 millis
     * findByWalkTree Total:7831, time:1911 millis
     * findAsynByCompletableFuture Total:7831, time:2355 millis
     * findAsynStreamByCompletableFuture Total:7831, time:2569 millis
     * findAsyn2ByCompletableFuture Total:7831, time:2542 millis
     * ForkJoinPool Total:7831, time:655 millis
     *
     * @param args
     */
    public static void main(String... args) {
        FileNameFinder finder = new FileNameFinder();

        //finder.findByCompletableFutureNotWork().forEach(System.out::println);

        long start = System.currentTimeMillis();
        List<Path> result = finder.findDirect();
        long end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findDirect Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findByMutiThreads();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findByMutiThreads Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findByThreadPool();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findByThreadPool Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findByCompletableFuture();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findByCompletableFuture Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findByFind();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findByFind Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findByWalk();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findByWalk Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findByWalkTree();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findByWalkTree Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findAsynByCompletableFuture();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findAsynByCompletableFuture Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findAsynStreamByCompletableFuture();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findAsynStreamByCompletableFuture Total:" + result.size() + ", time:" + (end - start) + " millis");


        start = System.currentTimeMillis();
        result = finder.findAsyn2ByCompletableFuture();
        end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("findAsyn2ByCompletableFuture Total:" + result.size() + ", time:" + (end - start) + " millis");


        FileFindTask.main();

    }

}


class FileFindTask extends RecursiveTask<List<Path>> {

    private Path dir;
    public static final String keyWord = ".md";

    public FileFindTask(Path dir) {
        this.dir = dir;
    }

    @Override
    protected List<Path> compute() {
        File[] files = dir.toFile().listFiles();
        List<Path> list = new ArrayList<>();
        List<FileFindTask> tasks = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (this.isFound(files[i].toPath())) {
                list.add(files[i].toPath());
            } else if (files[i].isDirectory()) {
                FileFindTask task = new FileFindTask(files[i].toPath());
                task.fork();
                tasks.add(task);
            }
        }

        tasks.forEach(t -> list.addAll(t.join()));
        return list;
    }


    private boolean isFound(Path path) {
        if (Files.isRegularFile(path)) {
            return path.toFile().getName().endsWith(keyWord);
        } else {
            return false;
        }
    }

    public static void main(String... args) {
        String rootDir = "/Users/yongliu/Documents/github";
        ForkJoinPool pool = new ForkJoinPool();
        FileFindTask task = new FileFindTask(Paths.get(rootDir));
        long start = System.currentTimeMillis();
        List<Path> result = pool.invoke(task);
        long end = System.currentTimeMillis();
        //result.forEach(System.out::println);
        System.out.println("ForkJoinPool Total:" + result.size() + ", time:" + (end - start) + " millis");
    }


}
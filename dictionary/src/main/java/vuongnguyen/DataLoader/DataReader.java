package vuongnguyen.DataLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataReader {

    //Dùng ThreadPool để xử lý file và add vào Dictionary
    public static void read(String dir) throws IOException, InterruptedException {
        int poolSize = 20;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        Set<String> listFile = listFilesUsingFileWalk(dir, 1);
        for(String fileName : listFile){
            TaskReadFile task = new TaskReadFile(dir+"/"+fileName);
            executor.execute(task);
        }
        executor.awaitTermination(1, TimeUnit.SECONDS);
        executor.shutdown();
    }

    //Liệt kê ta các file trong folder
    public static Set<String> listFilesUsingFileWalk(String dir, int depth) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

}


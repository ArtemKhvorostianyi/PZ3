import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ArrayProcessor {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        Random random = new Random();

        // Генерація трьох масивів
        int[] array1 = random.ints(15 + random.nextInt(11), 0, 1001).toArray();
        int[] array2 = random.ints(15 + random.nextInt(11), 0, 1001).toArray();
        int[] array3 = random.ints(15 + random.nextInt(11), 0, 1001).toArray();

        // Запис масивів у файли
        writeArrayToFile(array1, "array1.txt");
        writeArrayToFile(array2, "array2.txt");
        writeArrayToFile(array3, "array3.txt");

        // Паралельне виконання обробки масивів
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<List<Integer>> processedArray1 = executor.submit(() -> {
            int[] arr = readArrayFromFile("array1.txt");
            return Arrays.stream(arr)
                    .map(x -> x * 3)
                    .sorted()
                    .boxed()
                    .collect(Collectors.toList());
        });

        Future<List<Integer>> processedArray2 = executor.submit(() -> {
            int[] arr = readArrayFromFile("array2.txt");
            return Arrays.stream(arr)
                    .filter(x -> x % 2 == 0)
                    .sorted()
                    .boxed()
                    .collect(Collectors.toList());
        });

        Future<List<Integer>> processedArray3 = executor.submit(() -> {
            int[] arr = readArrayFromFile("array3.txt");
            return Arrays.stream(arr)
                    .filter(x -> x >= 10 && x <= 175)
                    .sorted()
                    .boxed()
                    .collect(Collectors.toList());
        });

        // Отримання результатів
        List<Integer> result1 = processedArray1.get();
        List<Integer> result2 = processedArray2.get();
        List<Integer> result3 = processedArray3.get();

        // Злиття масивів
        List<Integer> merged = result3.stream()
                .filter(x -> !result1.contains(x) && !result2.contains(x))
                .sorted()
                .collect(Collectors.toList());

        // Запис результатів
        writeListToFile(result1, "processed_array1.txt");
        writeListToFile(result2, "processed_array2.txt");
        writeListToFile(result3, "processed_array3.txt");
        writeListToFile(merged, "merged_array.txt");

        System.out.println("Обробка завершена. Результати збережено у файлах.");
        executor.shutdown();
    }

    private static void writeArrayToFile(int[] array, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int num : array) {
                writer.write(num + " ");
            }
        }
    }

    private static int[] readArrayFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return Arrays.stream(reader.readLine().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
    }

    private static void writeListToFile(List<Integer> list, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int num : list) {
                writer.write(num + " ");
            }
        }
    }
}

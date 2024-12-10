import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookUtils {

    // Метод для чтения книг из текстового файла
    public static ArrayList<Book> readBooksFromFile(String fileName) throws IOException {
        ArrayList<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int index = Integer.parseInt(line.trim());
                    String title = reader.readLine().trim();
                    String authors = reader.readLine().trim();
                    double averageRating = Double.parseDouble(reader.readLine().trim());
                    String id = reader.readLine().trim();
                    String isbn = reader.readLine().trim();
                    String languageCode = reader.readLine().trim();
                    int pageCount = Integer.parseInt(reader.readLine().trim());
                    int totalRatings = Integer.parseInt(reader.readLine().trim());
                    int totalReviews = Integer.parseInt(reader.readLine().trim());

                    books.add(new Book(index, title, authors, averageRating, id, isbn, languageCode, pageCount, totalRatings, totalReviews));
                } catch (Exception e) {
                    System.err.println("Ошибка чтения данных книги: " + e.getMessage());
                }
            }
        }
        return books;
    }

    // Метод для фильтрации книг по коду языка
    public static ArrayList<Book> filterBooksByLanguage(ArrayList<Book> books, String languageCode) {
        return books.stream()
                .filter(book -> languageCode.equals(book.getLanguageCode()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Метод для записи списка книг в файл
    public static void writeBooksToFile(ArrayList<Book> books, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Book book : books) {
                writer.write(book.toString());
                writer.newLine();
            }
        }
    }

    // Метод для поиска книги по названию
    public static void findBookByTitle(ArrayList<Book> books, String title) {
        boolean found = false;
        // Нормализуем ввод пользователя
        String normalizedInput = normalizeString(title);

        for (Book book : books) {
            // Нормализуем название книги
            String normalizedTitle = normalizeString(book.getTitle());

            if (normalizedTitle.equals(normalizedInput)) {
                System.out.println("Книга найдена: " + book);

                // Поиск других книг автора
                String[] authors = book.getAuthors().split("/");
                String mainAuthor = authors[0].trim();

                System.out.println("\nДругие книги автора \"" + mainAuthor + "\":");
                books.stream()
                        .filter(b -> b.getAuthors().contains(mainAuthor) && !b.getTitle().equals(book.getTitle()))
                        .forEach(System.out::println);

                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Книга не найдена.");
        }
    }

    // Метод для нормализации строки: убираем лишние пробелы и приводим к нижнему регистру
    private static String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    // Метод для обработки книг с несколькими соавторами
    public static void processBooksByCoauthors(ArrayList<Book> books) throws IOException {
        Map<Integer, List<Book>> coauthorGroups = groupBooksByCoauthorCount(books);

        for (Map.Entry<Integer, List<Book>> entry : coauthorGroups.entrySet()) {
            int coauthorCount = entry.getKey();
            List<Book> groupBooks = entry.getValue();

            String fileName = "coauthors_" + coauthorCount + ".txt";
            writeCoauthorGroupToFile(groupBooks, books, fileName);
        }
    }

    // Метод для группировки книг по количеству соавторов
    private static Map<Integer, List<Book>> groupBooksByCoauthorCount(ArrayList<Book> books) {
        return books.stream()
                .filter(book -> book.getAuthors().contains("/"))
                .collect(Collectors.groupingBy(book -> book.getAuthors().split("/").length));
    }

    // Метод для записи группы книг с соавторами в файл
    private static void writeCoauthorGroupToFile(List<Book> groupBooks, ArrayList<Book> books, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Book book : groupBooks) {
                String topAuthor = findTopAuthorByRatings(book.getAuthors(), books);
                writer.write("Книга: " + book.getTitle() + "\n");
                if (topAuthor != null) {
                    writer.write("Автор с наибольшим рейтингом: " + topAuthor + "\n");
                } else {
                    writer.write("Других книг соавторов не найдено.\n");
                }
                writer.write("\n");
            }
        }
    }

    // Метод для поиска автора с наибольшим рейтингом
    private static String findTopAuthorByRatings(String authors, ArrayList<Book> books) {
        String[] authorList = authors.split("/");
        String topAuthor = null;
        double maxAverage = -1;

        for (String author : authorList) {
            double average = books.stream()
                    .filter(b -> b.getAuthors().contains(author.trim()))
                    .mapToInt(Book::getTotalRatings)
                    .average()
                    .orElse(-1);

            if (average > maxAverage) {
                maxAverage = average;
                topAuthor = author.trim();
            }
        }

        return topAuthor != null && maxAverage > -1 ? topAuthor : null;
    }
}
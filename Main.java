import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// Что переделал:
// Фильтрация книг по коду языка реализована через filterBooksByLanguage.
// Реализация поиска книги вынесена в метод findBookByTitle в классе BookUtils.
// Метод processBooksByCoauthors разделён на:
// 1) groupBooksByCoauthorCount — группировка книг по количеству соавторов.
// 2) writeCoauthorGroupToFile — запись группы книг в файл.
// 3) findTopAuthorByRatings — поиск автора с наивысшим средним рейтингом.

public class Main {
    public static void main(String[] args) {
        try {
            // Загрузка данных из файла
            ArrayList<Book> books = BookUtils.readBooksFromFile("C:\\Фигня всякая\\Java ID2\\src\\data_book.txt");

            // Фильтрация книг с кодом языка en-US и вывод в файл
            ArrayList<Book> enUSBooks = BookUtils.filterBooksByLanguage(books, "en-US");
            BookUtils.writeBooksToFile(enUSBooks, "en_us_books.txt");

            // Вывод книг с кодом языка en-US
            System.out.println("Книги с кодом языка en-US:");
            enUSBooks.forEach(System.out::println);

            Scanner scanner = new Scanner(System.in);

            // Задача 1: Найти книгу по названию
            System.out.println("\nВведите название книги:");
            String userInput = scanner.nextLine().trim();
            BookUtils.findBookByTitle(books, userInput);

            // Задача 2: Вывести 10 самых тонких и 10 самых толстых книг
            System.out.println("\n10 самых тонких книг:");
            enUSBooks.stream()
                    .sorted((b1, b2) -> Integer.compare(b1.getPageCount(), b2.getPageCount()))
                    .limit(10)
                    .forEach(System.out::println);

            System.out.println("\n10 самых толстых книг:");
            enUSBooks.stream()
                    .sorted((b1, b2) -> Integer.compare(b2.getPageCount(), b1.getPageCount()))
                    .limit(10)
                    .forEach(System.out::println);

            // Задача 3: Списки книг с несколькими соавторами и обработка
            BookUtils.processBooksByCoauthors(books);

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}

package library;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryTest {

    private static final String ISBN = "978-0-00-000001-1";
    private static final LocalDate DAY =
            LocalDate.of(2026, 10, 1);

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();

        library.addBook(
                new Book(ISBN, "Software Engineering", 2016));

        library.addCopy(ISBN, "C-001");

        library.registerMember(
                new Member("M-01", "First member"));
    }

    @Test
    void lendingMakesTheCopyUnavailable() {

        Loan loan = library.lend("M-01", "C-001", DAY);

        assertFalse(library.findCopy("C-001").isAvailable());

        assertEquals(DAY.plusDays(14), loan.getDueDate());
    }

    @Test
    void cannotLendAnUnavailableCopy() {

        library.lend("M-01", "C-001", DAY);

        assertThrows(IllegalStateException.class,
                () -> library.lend("M-01", "C-001", DAY));
    }

    @Test
    void memberCannotHaveMoreThanFiveActiveLoans() {

        for (int i = 1; i <= 6; i++) {
            library.addCopy(ISBN, "C-00" + i);
        }

        for (int i = 1; i <= 5; i++) {
            library.lend("M-01", "C-00" + i, DAY);
        }

        assertThrows(IllegalStateException.class,
                () -> library.lend("M-01", "C-006", DAY));
    }

    @Test
    void unknownMemberIsRejected() {

        assertThrows(IllegalArgumentException.class,
                () -> library.lend("M-99", "C-001", DAY));
    }

    @Test
    void unknownCopyIsRejected() {

        assertThrows(IllegalArgumentException.class,
                () -> library.lend("M-01", "C-999", DAY));
    }

    @Test
    void returningCopyMakesItAvailableAgain() {

        library.lend("M-01", "C-001", DAY);

        library.returnCopy("C-001", DAY.plusDays(5));

        assertTrue(library.findCopy("C-001").isAvailable());
    }

    @Test
    void loanCanBeOpenAndLate() {

        Loan loan = library.lend("M-01", "C-001", DAY);

        assertTrue(loan.isOpen());

        assertTrue(loan.isLate(DAY.plusDays(15)));

        assertFalse(loan.isLate(DAY.plusDays(10)));
    }
}


package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Library {

    private List<Book> books;
    private List<Copy> copies;
    private List<Member> members;
    private List<Loan> loans;

    public Library() {
        books = new ArrayList<>();
        copies = new ArrayList<>();
        members = new ArrayList<>();
        loans = new ArrayList<>();
    }

    // Add a book
    public void addBook(Book book) {
        books.add(book);
    }

    // Add a copy of a book
    public void addCopy(String isbn, String inventoryNumber) {
        Book book = null;

        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                book = b;
                break;
            }
        }

        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        copies.add(new Copy(inventoryNumber, book));
    }

    // Register a member
    public void registerMember(Member member) {
        members.add(member);
    }

    // Find a copy by inventory number
    public Copy findCopy(String inventoryNumber) {
        for (Copy copy : copies) {
            if (copy.getInventoryNumber().equals(inventoryNumber)) {
                return copy;
            }
        }

        throw new IllegalArgumentException("Copy not found");
    }

    // Lend a copy to a member
    public Loan lend(String memberNumber, String inventoryNumber,
                     LocalDate today) {

        Member member = null;

        for (Member m : members) {
            if (m.getNumber().equals(memberNumber)) {
                member = m;
                break;
            }
        }

        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }

        Copy copy = findCopy(inventoryNumber);

        if (!copy.isAvailable()) {
            throw new IllegalStateException("Copy is not available");
        }

        int activeLoans = 0;

        for (Loan loan : loans) {
            if (loan.getMember().getNumber().equals(memberNumber)
                    && loan.isOpen()) {
                activeLoans++;
            }
        }

        if (activeLoans >= 5) {
            throw new IllegalStateException(
                    "Member cannot have more than 5 active loans");
        }

        Loan loan = new Loan(member, copy, today);
        loans.add(loan);
        copy.setAvailable(false);

        return loan;
    }

    // Return a copy
    public void returnCopy(String inventoryNumber, LocalDate today) {
        Copy copy = findCopy(inventoryNumber);

        if (copy.isAvailable()) {
            throw new IllegalStateException("Copy is already available");
        }

        for (Loan loan : loans) {
            if (loan.getCopy() == copy && loan.isOpen()) {
                loan.setReturnDate(today);
                copy.setAvailable(true);
                return;
            }
        }

        throw new IllegalStateException("No active loan found");
    }

    // Search books by title
    public List<Book> searchByTitle(String keyword) {
        List<Book> results = new ArrayList<>();

        for (Book book : books) {
            if (book.getTitle().toLowerCase()
                    .contains(keyword.toLowerCase())) {
                results.add(book);
            }
        }

        return results;
    }
}

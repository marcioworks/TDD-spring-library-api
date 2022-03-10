package br.com.marcioss.libraryapi.repositories;

import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when (count(l.id) > 0) then true else false end " +
            " from Loan l where l.book = :book and ( l.returned is null or l.returned is false)")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    @Query(value = " select l from Loan as l join l.book as b where b.isbn =:isbn or l.customer =:customer")
    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String anyString, @Param("customer") String anyString1, Pageable any);

    Page<Loan> getByBook(Book book, Pageable pageable);

    @Query(value = "select l from Loan l where l.loanDate <= :daysInArrear and ( l.returned is null or l.returned is false) ")
    List<Loan> findByLoanDateLessThanAndNotReturned( @Param("daysInArrear") LocalDate daysInArrear);
}

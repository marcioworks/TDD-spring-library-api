package br.com.marcioss.libraryapi.model.repository;

import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.repositories.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static br.com.marcioss.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    LoanRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Should return false if the borrowed book not returned.")
    public void existsByBookAndNotReturnedTest(){
        //scenary
        Book book = createNewBook("123");
        entityManager.persist(book);
        Loan loan =  Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);
        //execution
        boolean exists = repository.existsByBookAndNotReturned(book);

        //verifications
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should get a loan by book isbn or loan customer")
    public void findLoanByBookIsbnOrCustomer(){
        //scenary
        Book book = createNewBook("123");
        entityManager.persist(book);
        Loan loan =  Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);
        //execution
        Page<Loan> result = repository
                .findByBookIsbnOrCustomer(book.getIsbn(), loan.getCustomer(), PageRequest.of(0, 10));

        //validations
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);

    }
}

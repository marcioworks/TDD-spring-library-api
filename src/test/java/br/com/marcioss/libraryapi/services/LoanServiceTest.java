package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.exceptions.BusinessException;
import br.com.marcioss.libraryapi.repositories.LoanRepository;
import br.com.marcioss.libraryapi.services.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    private LoanService service;

    @MockBean
    private LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);

    }

    @Test
    @DisplayName("should save a loan")
    public void saveLoanTest() {
        //scenary
        Book book = Book.builder().id(1L).build();
        String customers = "Fulano";
        Loan toSaveLoan = Loan.builder()
                .book(book)
                .customer(customers)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .book(book)
                .customer(customers)
                .loanDate(LocalDate.now())
                .build();

        //executions
        when(repository.save(toSaveLoan)).thenReturn(savedLoan);
        Loan loan = service.save(toSaveLoan);

        //validations
        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("should throw a business exception when save a loan already borrowed")
    public void BorrowedSaveLoanTest() {
        //scenary
        Book book = Book.builder().id(1L).build();
        String customers = "Fulano";
        Loan toSaveLoan = Loan.builder()
                .book(book)
                .customer(customers)
                .loanDate(LocalDate.now())
                .build();


        //executions
        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
        Throwable exception = catchThrowable(() -> service.save(toSaveLoan));

        //validations
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("book already loaned");

        verify(repository, never()).save(toSaveLoan);


    }

}

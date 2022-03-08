package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.dto.input.LoanFilterDto;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.exceptions.BusinessException;
import br.com.marcioss.libraryapi.repositories.LoanRepository;
import br.com.marcioss.libraryapi.services.impl.LoanServiceImpl;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("should get loan details")
    public void getLoanDetailTest(){
        //scenary
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //executions
        Optional<Loan> result = service.getById(id);

        //validations
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("should update a loan")
    public void updateLoanTest(){
        //scenary
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);
        loan.setReturned(true);
        when(repository.save(loan)).thenReturn(loan);
        //executions
        Loan updatedLoan = service.update(loan);

        //validation
        assertThat(updatedLoan.isReturned()).isTrue();
        verify(repository).save(loan);

    }

    @Test
    @DisplayName("should filter loan by params")
    public void findLoanTest(){
        //scenary
        LoanFilterDto loanFilter = LoanFilterDto.builder().customers("Fulano").isbn("123").build();
        Loan loan = createLoan();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> list = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<Loan>(list, pageRequest, 1);
        when(repository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //executions
        Page<Loan> result = service.find(loanFilter, pageRequest);

        //validations
        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(result.getContent()).isEqualTo(list);
        AssertionsForClassTypes.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        AssertionsForClassTypes.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan(){
        Book book = Book.builder().id(1L).build();
        String customers = "Fulano";
       return  Loan.builder()
                .book(book)
                .customer(customers)
                .loanDate(LocalDate.now())
                .build();

    }

}

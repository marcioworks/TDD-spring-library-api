package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.dto.input.LoanFilterDto;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan any);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDto dto, Pageable pageRequest);

    Page<Loan> getLoanByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}

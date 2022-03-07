package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan any);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}

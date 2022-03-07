package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.entity.Loan;

public interface LoanService {
    Loan save(Loan any);

    Loan getById(Long id);

    void update(Loan loan);
}

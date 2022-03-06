package br.com.marcioss.libraryapi.services.impl;

import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.repositories.LoanRepository;
import br.com.marcioss.libraryapi.services.LoanService;

public class LoanServiceImpl implements LoanService {
    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}

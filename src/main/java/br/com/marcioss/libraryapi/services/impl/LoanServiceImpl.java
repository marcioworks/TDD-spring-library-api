package br.com.marcioss.libraryapi.services.impl;

import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.exceptions.BusinessException;
import br.com.marcioss.libraryapi.repositories.LoanRepository;
import br.com.marcioss.libraryapi.services.LoanService;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {
    private final LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("book already loaned");
        }
        return repository.save(loan);
    }
}

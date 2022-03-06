package br.com.marcioss.libraryapi.repositories;

import br.com.marcioss.libraryapi.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan,Long> {
}

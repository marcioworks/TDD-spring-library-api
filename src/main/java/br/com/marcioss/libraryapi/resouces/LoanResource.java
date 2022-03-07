package br.com.marcioss.libraryapi.resouces;

import br.com.marcioss.libraryapi.dto.input.ReturnedDTO;
import br.com.marcioss.libraryapi.dto.output.LoanDTO;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.services.BookService;
import br.com.marcioss.libraryapi.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanResource {

    private final BookService bookService;
    private final LoanService service;

    @PostMapping
    public Long createLoan(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Book not found"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();
        entity = service.save(entity);
        return entity.getId();

    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedDTO dto){
            Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            loan.setReturned(dto.getReturned());
            service.update(loan);
    }
}

package br.com.marcioss.libraryapi.resouces;

import br.com.marcioss.libraryapi.dto.input.LoanFilterDto;
import br.com.marcioss.libraryapi.dto.input.ReturnedDTO;
import br.com.marcioss.libraryapi.dto.output.BookDTO;
import br.com.marcioss.libraryapi.dto.output.LoanDTO;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.services.BookService;
import br.com.marcioss.libraryapi.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanResource {

    private final BookService bookService;
    private final LoanService service;
    private final ModelMapper modelMapper;

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

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDto dto, Pageable pageRequest){
        Page<Loan> result = service.find(dto, pageRequest);
        List<LoanDTO> loans = result.
                getContent()
                .stream()
                .map( entity ->{
                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                        return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }
}

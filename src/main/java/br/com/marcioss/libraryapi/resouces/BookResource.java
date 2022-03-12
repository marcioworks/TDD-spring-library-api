package br.com.marcioss.libraryapi.resouces;

import br.com.marcioss.libraryapi.dto.output.BookDTO;
import br.com.marcioss.libraryapi.dto.output.LoanDTO;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.services.BookService;
import br.com.marcioss.libraryapi.services.LoanService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "BOOK API")
@Slf4j
public class BookResource  {

    private final BookService service;

    private final ModelMapper modelMapper;

    private final LoanService loanService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a Book")
    public BookDTO createBook(@RequestBody @Valid BookDTO dto){
        log.info("Create a book for isbn {}", dto.getIsbn());
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);
        return modelMapper.map(entity,BookDTO.class);
    }

    @GetMapping("{id}")
    public BookDTO getBookById(@PathVariable Long id){
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @PutMapping("{id}")
    public BookDTO updateBook(@PathVariable Long id, BookDTO dto){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book = service.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pagerequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pagerequest);
        List<BookDTO> list = result.stream()
                .map(entity -> modelMapper.map(entity,BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pagerequest,result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> getLoansByBook(@PathVariable Long id, Pageable pageable){
        Book book = service.getById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoanByBook(book, pageable);
        List<LoanDTO> list = result
                .getContent()
                .stream()
                .map(loan ->{
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());

        return  new PageImpl<>(list, pageable, result.getTotalElements());
    }

}

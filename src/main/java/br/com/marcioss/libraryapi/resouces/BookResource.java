package br.com.marcioss.libraryapi.resouces;

import br.com.marcioss.libraryapi.dto.output.BookDTO;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookResource  {

    @Autowired
    BookService service;
    @Autowired
    ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody @Valid BookDTO dto){
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

}

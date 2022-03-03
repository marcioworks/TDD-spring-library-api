package br.com.marcioss.libraryapi.services.impl;

import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.exceptions.BusinessException;
import br.com.marcioss.libraryapi.repositories.BookRepository;
import br.com.marcioss.libraryapi.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public class BookServiceImpl implements BookService {


    BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }


    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("isbn already registered");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("book id cant be null");
        }
        this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("book id cant be null");
        }
       return this.repository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        return null;
    }


}

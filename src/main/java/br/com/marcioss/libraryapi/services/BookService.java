package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.entity.Book;

import java.util.Optional;

public interface BookService {
     Book save(Book any);

     Optional<Book> getById(Long id);


}

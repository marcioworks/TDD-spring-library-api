package br.com.marcioss.libraryapi.repositories;

import br.com.marcioss.libraryapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {
//    boolean existByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);
}

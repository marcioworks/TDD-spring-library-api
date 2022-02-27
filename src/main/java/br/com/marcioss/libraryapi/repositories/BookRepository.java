package br.com.marcioss.libraryapi.repositories;

import br.com.marcioss.libraryapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
//    boolean existByIsbn(String isbn);

    boolean existsByIsbn(String isbn);
}

package br.com.marcioss.libraryapi.model.repository;


import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.repositories.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("should return true when exists a book with isbn registered")
    public void returnTrueWhenIsbnExistsTest(){
        //scenary
        String isbn= "123";

        Book book = createNewBook(isbn);
        entityManager.persist(book);

        //execution
        boolean exists = repository.existsByIsbn(isbn);

        //validations
        assertThat(exists).isTrue();
    }

    private Book createNewBook(String isbn) {
        return Book.builder().title("As aventuras").author("Marcio").isbn(isbn).build();
    }

    @Test
    @DisplayName("should return false when not exists a book with isbn registered")
    public void returnFalseWhenIsbnNotExistsTest(){
        //scenary
        String isbn= "123";

        //execution
        boolean exists = repository.existsByIsbn(isbn);

        //validations
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should get a book by id")
    public void findBookByIdTest(){
        //scenary
        Book book = createNewBook("123");
        entityManager.persist(book);

        //execution
        Optional<Book> foundBook = repository.findById(book.getId());

        //validation
        assertThat(foundBook.isPresent()).isTrue();

    }

}

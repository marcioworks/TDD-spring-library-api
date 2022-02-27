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
    public void returnTrueWhenIsbnExists(){
        //scenary
        String isbn= "123";

        Book book = Book.builder().title("As aventuras").author("Marcio").isbn(isbn).build();
        entityManager.persist(book);

        //execution
        boolean exists = repository.existsByIsbn(isbn);

        //validations
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return false when not exists a book with isbn registered")
    public void returnFalseWhenIsbnNotExists(){
        //scenary
        String isbn= "123";

        //execution
        boolean exists = repository.existsByIsbn(isbn);

        //validations
        assertThat(exists).isFalse();
    }

}

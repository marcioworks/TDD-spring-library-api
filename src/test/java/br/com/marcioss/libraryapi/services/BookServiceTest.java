package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.exceptions.BusinessException;
import br.com.marcioss.libraryapi.repositories.BookRepository;
import br.com.marcioss.libraryapi.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {


    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }
    @Test
    @DisplayName("should save a Book")
    public void saveBookTest(){
        //scenary
        Book book = createAvalidBook();
        Mockito.when(repository.save(book)).thenReturn(
                Book.builder()
                        .id(11L)
                        .author("fulano")
                        .title("as Aventuras")
                        .isbn("123")
                        .build());

        //execution
        Book savedbook = service.save(book);

        //validation
        assertThat(savedbook.getId()).isNotNull();
        assertThat(savedbook.getAuthor()).isEqualTo("fulano");
        assertThat(savedbook.getTitle()).isEqualTo("as Aventuras");
        assertThat(savedbook.getIsbn()).isEqualTo("123");
    }

    @Test
    @DisplayName("should throw a Business Exception with duplicate isbn error")
    public void shouldNotCreatABookWithDuplicatedIsbnTest(){
        //scenary
        Book book = createAvalidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        //execution
        Throwable exception =  Assertions.catchThrowable(() -> service.save(book));

        //validations
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("isbn already registered");

        Mockito.verify(repository, Mockito.never()).save(book);
    }


    private Book createAvalidBook() {
        return Book.builder().author("fulano").title("as Aventuras").isbn("123").build();
    }
}

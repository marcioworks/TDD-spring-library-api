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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

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
        when(repository.save(book)).thenReturn(
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
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        //execution
        Throwable exception =  Assertions.catchThrowable(() -> service.save(book));

        //validations
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("isbn already registered");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("should get a book by id")
    public void getBookByIdTest(){
        //scenary
        Long id = 1L;

        Book book = createAvalidBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(book));

        //executions
        Optional<Book> foundBook = service.getById(id);

        //validations
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("should get empty where try to get a nonexistent book by id")
    public void getNonexistentBookByIdTest(){
        //scenary
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        //executions
        Optional<Book> foundBook = service.getById(id);

        //validations
        assertThat(foundBook.isPresent()).isFalse();

    }

    @Test
    @DisplayName("should delete a book")
    public void deleteBookTest(){
        //scenary
        Book book = Book.builder().id(1L).build();
        //executions
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(()->service.delete(book));
        //validations
        Mockito.verify(repository,Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("should throw a exception when try to delete a book without id")
    public void deleteInvalidBookTest(){
        //scenary
        Book book = new Book();
        //executions
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class ,()->service.delete(book));
        //validations
        Mockito.verify(repository,Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("should throw a exception when try to update a book without id")
    public void updateInvalidBookTest(){
        //scenary
        Book book = new Book();
        //executions
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class ,()->service.update(book));
        //validations
        Mockito.verify(repository,Mockito.never()).save(book);
    }

    @Test
    @DisplayName("should  update a book")
    public void updateBookTest(){
        //scenary
        Long id = 1L;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createAvalidBook();
        updatedBook.setId(id);
        //executions
       when(repository.save(updatingBook)).thenReturn(updatedBook);
       Book book = service.update(updatingBook);
        //validations
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("should filter bokk by params")
    public void findBookTest(){
        //scenary
        Book book = createAvalidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);
        when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //executions
        Page<Book> result = service.find(book, pageRequest);

        //validations
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should get a book by isbn")
    public void getBookByIsbnTest(){
        //scenary
        String isbn = "123";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1l).isbn(isbn).build()));
        //execution
        Optional<Book> book = service.getBookByIsbn(isbn);

        //validations
        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1l);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);

    }

    private Book createAvalidBook() {
        return Book.builder().author("fulano").title("as Aventuras").isbn("123").build();
    }
}

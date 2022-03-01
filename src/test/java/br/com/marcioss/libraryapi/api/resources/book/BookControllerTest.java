package br.com.marcioss.libraryapi.api.resources.book;

import br.com.marcioss.libraryapi.dto.output.BookDTO;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.exceptions.BusinessException;
import br.com.marcioss.libraryapi.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    static String BOOK_API ="/api/books";

    @Test
    @DisplayName("Should be able to create a book")
    public void createBookTest() throws Exception{
        BookDTO dto = createBook();

        Book saveBook = Book.builder().id(101L).author("Marcio").title("as Aventuras").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(101))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }



    @Test
    @DisplayName("Should not be able to create a book when there are missing parameters")
    public void createInvalidBookTest() throws Exception {
        //scenary
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        //executions
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //validations
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("should throw a business exception with duplicate error message")
    public void createBookWithDuplicateIsbnTest() throws Exception {
        BookDTO dto = createBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String message= "isbn already registered";
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(message));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors",hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("isbn already registered"));
    }

    @Test
    @DisplayName("should get book details")
    public void getBookDetailsTest() throws Exception {
        //scenary
        Long id = 1L;
        Book book = Book.builder().id(id)
                .title(createBook().getTitle())
                .author(createBook().getAuthor())
                .isbn(createBook().getIsbn())
                .build();

        //execution
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        //validations
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBook().getTitle()))
                .andExpect(jsonPath("author").value(createBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createBook().getIsbn()));
    }


    @Test
    @DisplayName("should throw a not found exception if the book not found")
    public void bookNotFoundTest() throws Exception {
        //scenary
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        //executions
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+ 1))
                .accept(MediaType.APPLICATION_JSON);

        //validations
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return no content status when delete a book")
    public void deleteBookTest() throws Exception{
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));
        //executions
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+ 1));

        //validations
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return resource not found when delete a nonexistent book")
    public void deleteNonexistentBookTest() throws Exception{
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());
        //executions
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+ 1));

        //validations
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should update a Book")
    public void updateBookTest() throws Exception {
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBook());
        Book updatingBook = Book.builder().id(id).title("some title").author("some Author").isbn("321").build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));
        Book updatedBook = Book.builder().id(id).author("Marcio").title("as Aventuras").isbn("321").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBook().getTitle()))
                .andExpect(jsonPath("author").value(createBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));

    }

    @Test
    @DisplayName("should return a not found where try to update a nonexistent Book")
    public void updateNonexistentBookTest() throws Exception {
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBook());
        BDDMockito.given(service.getById(id)).willReturn(Optional.empty());


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound());


    }
    private BookDTO createBook() {
        BookDTO dto = BookDTO.builder().author("Marcio").title("as Aventuras").isbn("001").build();
        return dto;
    }
}

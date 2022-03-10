package br.com.marcioss.libraryapi.api.resources.book;


import br.com.marcioss.libraryapi.dto.input.LoanFilterDto;
import br.com.marcioss.libraryapi.dto.input.ReturnedDTO;
import br.com.marcioss.libraryapi.dto.output.LoanDTO;
import br.com.marcioss.libraryapi.entity.Book;
import br.com.marcioss.libraryapi.entity.Loan;
import br.com.marcioss.libraryapi.exceptions.BusinessException;
import br.com.marcioss.libraryapi.resouces.LoanResource;
import br.com.marcioss.libraryapi.services.BookService;
import br.com.marcioss.libraryapi.services.LoanService;
import br.com.marcioss.libraryapi.services.LoanServiceTest;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanResource.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;


    @Test
    @DisplayName("should create a Loan")
    public void createLoanTest() throws Exception {
        //scenary
        LoanDTO dto = LoanDTO.builder().isbn("123").customerEmail("customer@email.com").customer("fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Book book = Book.builder().id(1L).isbn("123").build();

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).customer("fulano").book(book).loanDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

    }

    @Test
    @DisplayName("should throw a exception of bad request")
    public void LoanWithInvalidIsbnTest() throws Exception{
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors",hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found"));


    }

    @Test
    @DisplayName("should throw a exception of Business")
    public void BookAlreadyLoanedTest() throws Exception{
        //scenary
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book Already Loaned"));

        //executions
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //validations
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors",hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book Already Loaned"));
    }

    @Test
    @DisplayName("should give back a Loan")
    public void returnALoan() throws  Exception{
        ReturnedDTO dto= ReturnedDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Loan loan = Loan.builder().id(1L).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));
        //executions
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isOk());

    }
    @Test
    @DisplayName("should throw a not found exception when give back a nonexistent Loan")
    public void returnANonexistentLoan() throws  Exception{
        ReturnedDTO dto= ReturnedDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());
        //executions
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("should filter a loan")
    public void filterLoansPageTest() throws Exception{
        Long id = 1L;
        Loan loan = LoanServiceTest.createLoan();
        Book book = Book.builder().id(1L).isbn("123").build();
        loan.setBook(book);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDto.class),Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0,10),1));

        String params = String .format("?isbn=%s&customers=%s&page=0&size=10",book.getIsbn(),loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(params))
                .accept(MediaType.APPLICATION_JSON);
        mvc .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content",hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}

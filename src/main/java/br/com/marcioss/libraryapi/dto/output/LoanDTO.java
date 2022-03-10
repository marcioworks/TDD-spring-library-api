package br.com.marcioss.libraryapi.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {

    private Long id;
    @NotEmpty
    private String isbn;
    @NotEmpty
    private String customer;
    @NotEmpty
    @Email
    private String customerEmail;
    private BookDTO book;
}

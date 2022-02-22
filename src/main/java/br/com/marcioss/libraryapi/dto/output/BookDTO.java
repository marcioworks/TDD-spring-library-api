package br.com.marcioss.libraryapi.dto.output;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;
    private String author;
    private String title;
    private String isbn;
}

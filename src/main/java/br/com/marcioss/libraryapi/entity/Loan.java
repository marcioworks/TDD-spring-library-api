package br.com.marcioss.libraryapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String customer;

    @JoinColumn(name = "book_id")
    @ManyToOne
    private Book book;

    private LocalDate loanDate;

    private boolean returned;
}

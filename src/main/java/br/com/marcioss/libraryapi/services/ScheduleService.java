package br.com.marcioss.libraryapi.services;

import br.com.marcioss.libraryapi.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    @Value("${mail.lateloan.message}")
    private String message;

    private static final String LOAN_CRON = "0 0 0 1/1 * ?";
    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = LOAN_CRON)
    public void sendMailToLateLoans(){
        List<Loan> lateLoans = loanService.getAllLateLoans();
        List <String> mailList = lateLoans.stream().map(Loan::getCustomerEmail).collect(Collectors.toList());

        emailService.sendMails(message, mailList);
    }
}

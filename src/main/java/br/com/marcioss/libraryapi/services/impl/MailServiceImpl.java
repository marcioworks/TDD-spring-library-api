package br.com.marcioss.libraryapi.services.impl;

import br.com.marcioss.libraryapi.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailServiceImpl implements EmailService {


    private String remittent ="mail@library.com";

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMails(String message, List<String> mailList) {
        String[] mailTo = mailList.toArray(new String[mailList.size()]);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remittent);
        mailMessage.setSubject("emprestimo de Livro em atraso");
        mailMessage.setText(message);

        mailMessage.setTo(mailTo);

        javaMailSender.send(mailMessage);

    }
}

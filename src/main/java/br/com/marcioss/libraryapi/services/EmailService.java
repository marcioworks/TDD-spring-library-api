package br.com.marcioss.libraryapi.services;

import java.util.List;

public interface EmailService {
    void sendMails(String message, List<String> mailList);
}

package ru.danilarassokhin.notification.service.impl;

import java.io.InputStream;
import java.util.Arrays;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
public class MockJavaMailSender implements JavaMailSender {

  {
    log.info("Using MockJavaMailSender");
  }

  @Override
  public MimeMessage createMimeMessage() {
    return null;
  }

  @Override
  public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
    return null;
  }

  @Override
  public void send(MimeMessage... mimeMessages) throws MailException {

  }

  @Override
  public void send(SimpleMailMessage... simpleMessages) throws MailException {
    try {
      Thread.sleep(10000);
      log.info("Sent message to email: {}", Arrays.toString(simpleMessages));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

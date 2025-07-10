package ru.danilarassokhin.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import ru.danilarassokhin.notification.service.impl.MockJavaMailSender;

@Configuration
public class JavaMailConfig {

  @Bean
  @Primary
  public JavaMailSender mockSender() {
    return new MockJavaMailSender();
  }

}

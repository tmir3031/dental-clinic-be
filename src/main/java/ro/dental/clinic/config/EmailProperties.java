package ro.dental.clinic.config;

import org.apache.commons.mail.EmailException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
public class EmailProperties {

    @Bean
    public JavaMailSender javaMailSender() throws EmailException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); // sau 465 pentru SSL
        mailSender.setUsername("raluca.timoneaa@gmail.com");
        mailSender.setPassword("aqgleshvdtofwjfx");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", "*");
        return mailSender;


    }


}
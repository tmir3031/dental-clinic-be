package ro.dental.clinic.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SenderEmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String text, String subject){
        SimpleMailMessage mailMessage
                = new SimpleMailMessage();
        mailMessage.setFrom("loredana.roxy11@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setText(text);
        mailMessage.setSubject(subject);

        javaMailSender.send(mailMessage);
    }
}
package cotas.lamana.api.service.email;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

@Service
@Slf4j
public class EnviarEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendHtmlMail(String to, String from, String subject, String html) {
        // Envia email
        sendMail(to, from, subject, html, true);
    }

    public void sendMail(String to, String from, String subject, String text, boolean isHtml) {
        // Trata erro
        try {
            // instacia email sender
            MimeMessage mail = javaMailSender.createMimeMessage();
            // instancia helper
            MimeMessageHelper helper = new MimeMessageHelper(mail);
            // seta to
            helper.setTo(to);
            // seta from
            helper.setFrom(from);
            // seta assunto
            helper.setSubject(subject);
            // seta texto
            helper.setText(text, isHtml);
            // Envia Email
            javaMailSender.send(mail);
        } catch (Exception e) {
            // Registra erro
            System.out.println(e.getMessage());
        }
    }
}

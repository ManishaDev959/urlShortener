package in.co.codeplanet.urlshortner.service;

import in.co.codeplanet.urlshortner.bean.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(EmailDetails emailDetails)
    {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom("manishadev95@gmail.com");
        simpleMailMessage.setTo(emailDetails.getRecipient());
        simpleMailMessage.setSubject(emailDetails.getSubject());
        simpleMailMessage.setText(emailDetails.getBody());
        javaMailSender.send(simpleMailMessage);
    }

}

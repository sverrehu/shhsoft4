package no.shhsoft.mail;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface MailSender {

    void sendMail(String from, String to, String subject, String body, String contentType);

    void sendMail(String from, String to, String subject, String body);

    void sendHtmlMail(String from, String to, String subject, String body);

}

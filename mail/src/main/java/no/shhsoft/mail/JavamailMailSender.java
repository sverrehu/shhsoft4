package no.shhsoft.mail;

import no.shhsoft.utils.StringUtils;
import no.shhsoft.utils.UnknownEnumConstantException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JavamailMailSender
implements MailSender {

    private String mailHost;
    private int mailPort = 25;
    private Encryption encryption = Encryption.NONE;
    private String userName;
    private String password;

    public enum Encryption {
        NONE, STARTTLS, SSL
    }

    @Override
    public void sendMail(final String from, final String to, final String subject, final String body, final String contentType) {
        final Properties props = new Properties();
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", String.valueOf(mailPort));
        switch (encryption) {
            case SSL:
                props.setProperty("mail.smtp.ssl.enable", "true");
                break;
            case STARTTLS:
                props.setProperty("mail.smtp.starttls.enable", "true");
                break;
            case NONE:
                break;
            default:
                throw new UnknownEnumConstantException(encryption);
        }
        try {
            final Session session = Session.getInstance(props);
            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setHeader("Content-Type", contentType);
            msg.setHeader("Content-Transfer-Encoding", "quoted-printable");
            if (!StringUtils.isBlank(userName) && !StringUtils.isBlank(password)) {
                Transport.send(msg, userName, password);
            } else {
                Transport.send(msg);
            }
        } catch (final MessagingException e) {
            throw new RuntimeException("There was an error sending E-mail to `" + to + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void sendMail(final String from, final String to, final String subject, final String body) {
        sendMail(from, to, subject, body, "text/plain; charset=\"UTF-8\"");
    }

    @Override
    public void sendHtmlMail(final String from, final String to, final String subject, final String body) {
        sendMail(from, to, subject, body, "text/html; charset=\"UTF-8\"");
    }

    public void setMailHost(final String mailHost) {
        this.mailHost = mailHost;
    }

    public void setMailPort(final int mailPort) {
        this.mailPort = mailPort;
    }

    public void setEncryption(final Encryption encryption) {
        this.encryption = encryption;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

}

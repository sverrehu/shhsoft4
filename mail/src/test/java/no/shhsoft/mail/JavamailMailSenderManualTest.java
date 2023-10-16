package no.shhsoft.mail;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JavamailMailSenderManualTest {

    private static final String MAIL_SERVER = "mail";
    private static final int MAIL_PORT = 25;

    private JavamailMailSenderManualTest() {
    }

    public static void main(final String[] args) {
        final JavamailMailSender mailSender = new JavamailMailSender();
        mailSender.setMailHost(MAIL_SERVER);
        mailSender.setMailPort(MAIL_PORT);
        mailSender.sendMail("test@example.com", "shh@thathost.com",
                            "Subject Norwegian chars: \u00e6\u00f8\u00e5 \u00c6\u00d8\u00c5",
                            "Body Norwegian chars: \u00e6\u00f8\u00e5 \u00c6\u00d8\u00c5");
        System.out.println("Mail sent.");
    }

}

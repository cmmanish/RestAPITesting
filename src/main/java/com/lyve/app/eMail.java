package com.lyve.app;

/**
 * Created by mmadhusoodan on 1/8/15.
 */

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Mail
 */
public class eMail {
    /**
     * Session
     */
    protected Session session;

    /**
     * Mail constructor.
     *
     * @param host Host
     */
    public eMail(String host) {
        Properties properties = new Properties();
        properties.put("smtp.gmail.com", host);
        session = Session.getDefaultInstance(properties);
    }

    /**
     * Send email message.
     *
     * @param from    From
     * @param tos     Recipients
     * @param ccs     CC Recipients
     * @param subject Subject
     * @param text    Text
     * @throws MessagingException
     */
    public void send(String from, String tos[], String ccs[], String subject,
                     String text)
            throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        for (String to : tos)
            message.addRecipient(RecipientType.TO, new InternetAddress(to));
        for (String cc : ccs)
            message.addRecipient(RecipientType.TO, new InternetAddress(cc));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }

    public static void main(String args[]) throws  MessagingException{

        eMail email = new eMail("localhost");

        String tos[] = {"cmmanish@gmail.com"};
        String ccs[] = {"cmmanish@gmail.com"};
        email.send("cmmanish@gmail.com",tos,ccs,"Sub","Text");

    }
}
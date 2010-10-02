/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import javax.mail.internet.InternetAddress;
import models.Event;
import models.Newsletter;
import play.mvc.Mailer;

/**
 *
 * @author ali
 */
public class Notifier extends Mailer{

    public static boolean sendWelcome(Newsletter n) throws Exception{
        setFrom(new InternetAddress("admin@al-imane.org", "Administrateur"));
        setReplyTo(new InternetAddress("admin@al-imane.org", ""));
        setSubject("Calendrier inscription");
        addRecipient(n.email);
        return sendAndWait(n);
    }

    public static boolean sendEvent(String email,Event e) throws Exception{
        setFrom(new InternetAddress("admin@al-imane.org", "Administrateur"));
        setReplyTo(new InternetAddress("admin@al-imane.org", ""));
        setSubject("Calendrier événements");
        addRecipient(email);
        return sendAndWait(e);
    }

}

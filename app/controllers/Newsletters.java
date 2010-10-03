/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import models.Maraja;
import models.Newsletter;
import play.data.validation.Email;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.Http;

/**
 *
 * @author ali
 */
public class Newsletters extends Controller {

    public static void subscription(@Required @Email String email, int beforeDay) {
        Http.Cookie maraja = request.cookies.get("maraja");
        if (maraja == null) {
            return;
        }

        if (validation.hasError("email")) {
            flash.error("Cet email n'est pas valide");
            params.flash();
            Application.index(0, 0);
        }

        Newsletter news = Newsletter.find("email = ? and enabled = true", email).first();

        if (news != null) {
            flash.error("Cet email est déjà inscrit");
            Application.index(0, 0);
        }

        Maraja m = Maraja.findById(Long.parseLong(maraja.value));

        Newsletter n = new Newsletter();
        n.beforeDay = beforeDay;
        n.email = email;
        n.maraja = m;
        n.confirm = Codec.UUID();
        n.save();
        try {
            Notifier.sendWelcome(n);
            flash.success("Un email vous a été envoyé pour valider votre adresse email.");
        } catch (Exception ex) {
            Logger.getLogger(Newsletters.class.getName()).log(Level.SEVERE, null, ex);
            flash.error("Une erreur s'est produite. Veuillez contacter l'administrateur.");
        }
        
        Application.index(0, 0);
    }

    public static void delete(long id) {
        Newsletter n = Newsletter.findById(id);
        n.delete();

        flash.success("Element supprimé avec succès");
        Administration.newsletters();
    }

    public static void save(Newsletter n) {
        n.save();

        flash.success("Element sauvegardé avec succès");
        Administration.newsletters();
    }

    public static void validate(String confirm){
        Newsletter n = Newsletter.find("byConfirm", confirm).first();

        if(n == null){
            flash.error("Nous n'avons pas réussi à valider votre adresse email. Veuillez contacter l'administrateur.");
        }else{
            n.confirm = Codec.UUID();
            n.enabled = true;
            n.save();
            flash.success("Votre adresse email a été validée");
        }

        Application.index(0, 0);
    }

    public static void stop(String confirm){
        Newsletter n = Newsletter.find("confirm = ?", confirm).first();
        if(n == null){
            flash.error("Nous n'avons pas réussi à retouver vos paramêtres pour "
                    + "arrêter l'envoi des événements. Veuillez contacter l'administrateur.");
        }else{
            n.delete();
            flash.success("L'envoi des événements a été arrêté pour votre adresse email.");
            
        }
        Application.index(0, 0);
    }

}

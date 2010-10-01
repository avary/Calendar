/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.GregorianCalendar;
import java.util.List;
import models.Event;
import models.Maraja;
import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;
import play.mvc.Controller;

/**
 *
 * @author ali
 */
public class Administration extends Controller {

    public static void index() {
        List<Maraja> marajas = Maraja.all().fetch();
        DateTime d = new DateTime(new GregorianCalendar().getTimeInMillis());
        DateTime today = d.withChronology(IslamicChronology.getInstance());

        for (Maraja maraja : marajas) {
            DateTime dtISO = new DateTime(new GregorianCalendar().getTimeInMillis() + (maraja.days * 86400000));
            DateTime dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());
            maraja.today = dtIslamic;
        }

        render(marajas,today);
    }

    public static void event(){
        List<Event> events = Event.find("order by  month, day").fetch();
        String islamMonth [] = {"Muharram","Safar","Rabi'El Awwal","Rabi'At Thani","Joumada El Awwal","Joumada At Thani","Rajab",
    "Sha'ban","Ramadan","Shawwal","Dhoul Qa'da","Dhoul Hijja"};
        render(events,islamMonth);
    }
}

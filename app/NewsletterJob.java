
import controllers.Notifier;
import ext.MyExtension;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Event;
import models.Maraja;
import models.Newsletter;
import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.templates.JavaExtensions;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ali
 */
@OnApplicationStart
public class NewsletterJob extends Job<Void> {

    @Override
    public void doJob() throws Exception {
        List<Maraja> majaras = Maraja.find("order by days").fetch();
        int min = majaras.get(0).days;
        int max = majaras.get(majaras.size() - 1).days;

        GregorianCalendar d = new GregorianCalendar();
        DateTime dtISO = new DateTime(d.getTimeInMillis() + (min * 86400000));
        DateTime dateMin = dtISO.withChronology(IslamicChronology.getInstance());

        dtISO = new DateTime(d.getTimeInMillis() + ((max + 7) * 86400000));
        DateTime dateMax = dtISO.withChronology(IslamicChronology.getInstance());

        Logger.info("Start newsletter");
        Logger.info("Min islamic date : " + MyExtension.islamFormat(dateMin));
        Logger.info("Max islamic date : " + MyExtension.islamFormat(dateMax));

        List<Event> events = Event.find("(day >= ? and month >= ?) or (day <= ? and month <= ?) "
                + "order by month, day", dateMin.getDayOfMonth(), dateMin.getMonthOfYear(),
                dateMax.getDayOfMonth(), dateMax.getMonthOfYear()).fetch();

        Map<String, Event> eventMap = new HashMap<String, Event>();
        Logger.info("Events founded :");
        for (Event event : events) {
            Logger.info(event.day + " - " + MyExtension.islamMonth[event.month - 1] + " - "
                    + event.name);
            eventMap.put(event.day + ":" + event.month, event);
        }

        List<Newsletter> news = Newsletter.find("enabled = true").fetch();

        for (Newsletter n : news) {
            DateTime d1 = new DateTime(d.getTimeInMillis() + ((n.maraja.days + n.beforeDay) * 86400000));
            DateTime d2 = d1.withChronology(IslamicChronology.getInstance());
            Event e = eventMap.get(d2.getDayOfMonth() + ":" + d2.getMonthOfYear());

            if (e != null) {
                Notifier.sendEvent(n.email, e);
            }
        }
    }
}

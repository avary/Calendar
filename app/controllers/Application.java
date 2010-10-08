package controllers;

import com.mchange.v2.lang.Coerce;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.CalendarItem;
import models.Event;
import models.Maraja;
import models.Newsletter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.IslamicChronology;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.*;
import utils.PrayTime;

public class Application extends Controller {

    public static String islamMonth[] = {"Muharram", "Safar", "Rabi'El Awwal", "Rabi'At Thani", "Joumada El Awwal", "Joumada At Thani", "Rajab",
        "Sha'ban", "Ramadan", "Shawwal", "Dhoul Qa'da", "Dhoul Hijja"};

    public static void index(int monthNumber, int page) {

        Http.Cookie timeZone = request.cookies.get("timeZoneName");
        Http.Cookie latitude = request.cookies.get("latitude");
        Http.Cookie longitude = request.cookies.get("longitude");
        Http.Cookie address = request.cookies.get("address");
        Http.Cookie maraja = request.cookies.get("maraja");

        List<CalendarItem> calItems = new ArrayList<CalendarItem>();
        GregorianCalendar d = new GregorianCalendar();

        int today = d.get(Calendar.DAY_OF_MONTH);
        int currentMonth = d.get(Calendar.MONTH);

        if (page != 0) {
            d.set(Calendar.MONTH, monthNumber + page);
        }

        monthNumber = d.get(Calendar.MONTH);
        int year = d.get(Calendar.YEAR);
        String month = d.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRENCH);
        String adr = null;
        String marajaName = null;
        GregorianCalendar todayDate = new GregorianCalendar();
        DateTime todayIslamic = null;
        List<Maraja> marajas = null;

        if (timeZone != null && latitude != null && longitude != null && address != null
                && maraja != null) {

            Map<String, Event> ev = (Map<String, Event>) Cache.get("event");
            double lon = Double.parseDouble(longitude.value);
            double lat = Double.parseDouble(latitude.value);
            try {
                adr = URLDecoder.decode(address.value, "utf-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }

            int lastDay = d.getActualMaximum(Calendar.DAY_OF_MONTH);
            int day = 1;
            d.set(Calendar.DAY_OF_MONTH, day);

            Maraja m = Maraja.findById(Long.parseLong(maraja.value));
            marajaName = m.name;

            DateTime dtISO = new DateTime(d.getTimeInMillis() + (m.days * 86400000));
            
            DateTime dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());

            todayIslamic = new DateTime(todayDate.getTimeInMillis() + (m.days * 86400000)).
                    withChronology(IslamicChronology.getInstance());


            TimeZone tz = TimeZone.getTimeZone(timeZone.value);
            double t = tz.getRawOffset()/3600000;
            if(tz.inDaylightTime(new Date())){
                t++;
            }
            
            PrayTime p = new PrayTime();

            while (day <= lastDay) {

                String times[] = p.getPrayerTimes(d, lat, lon, t);
                CalendarItem c = new CalendarItem();
                c.weekDay = d.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRENCH);
                c.fajr = times[0];
                c.sunrise = times[1];
                c.duhr = times[2];
                c.maghrib = times[5];
                c.midnight = times[7];
                c.hijriDay = dtIslamic.getDayOfMonth();
                c.hijriMonth = Application.islamMonth[dtIslamic.getMonthOfYear() - 1];
                c.event = ev.get(dtIslamic.getDayOfMonth() + ":" + dtIslamic.getMonthOfYear());
                calItems.add(c);

                day++;
                d.set(Calendar.DAY_OF_MONTH, day);
                dtISO = new DateTime(d.getTimeInMillis() + (m.days * 86400000));
                dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());
            }
        }else{
            marajas = Maraja.all().fetch();
        }

        render(calItems, month, monthNumber, year, adr, today, currentMonth,
                marajaName,todayDate,todayIslamic,marajas);
    }

    public static void changeLocation() {
        List<Maraja> marajas = Maraja.all().fetch();

        for (Maraja maraja : marajas) {
            DateTime dtISO = new DateTime(new GregorianCalendar().getTimeInMillis() + (maraja.days * 86400000));
            DateTime dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());
            maraja.today = dtIslamic;
        }

        render(marajas);
    }

    public static void editNewsletter(String confirm) {
        Newsletter n = Newsletter.find("confirm = ?", confirm).first();
        if (n == null) {
            flash.error("Nous n'avons pas réussi à retouver vos paramêtres pour "
                    + "faire les modifications . Veuillez contacter l'administrateur.");
            Application.index(0, 0);
        } else {
            render(n,confirm);

        }
        
    }

    public static void saveNewsletter(Newsletter newsletter,String confirm){
        Newsletter n = Newsletter.find("confirm = ?", confirm).first();
        if (n == null) {
            flash.error("Nous n'avons pas réussi à retouver vos paramêtres pour "
                    + "faire les modifications . Veuillez contacter l'administrateur.");

        } else {
            n.beforeDay = newsletter.beforeDay;
            n.confirm = Codec.UUID();
            n.save();

            flash.success("Les modfications d'envoi en été sauvegardées");
        }

        Application.index(0, 0);
    }
}

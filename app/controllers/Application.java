package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.CalendarItem;
import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;
import play.mvc.*;
import utils.PrayTime;

public class Application extends Controller {

    public static String islamMonth [] = {"Muharram","Safar","Rabi'El Awwal","Rabi'At Thani","Joumada El Awwal","Joumada At Thani","Rajab",
    "Sha'ban","Ramadan","Shawwal","Dhoul Qa'da","Dhoul Hijja"};

    public static void index(int monthNumber,int page) {
        
        Http.Cookie timeZone = request.cookies.get("timeZone");
        Http.Cookie latitude = request.cookies.get("latitude");
        Http.Cookie longitude = request.cookies.get("longitude");
        Http.Cookie address = request.cookies.get("address");

        List<CalendarItem> calItems = new ArrayList<CalendarItem>();
        GregorianCalendar d = new GregorianCalendar();

        int today = d.get(Calendar.DAY_OF_MONTH);
        int currentMonth = d.get(Calendar.MONTH);

        if(page != 0){
            d.set(Calendar.MONTH, monthNumber+page);
        }
        
        monthNumber = d.get(Calendar.MONTH);
        int year = d.get(Calendar.YEAR);
        String month = d.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRENCH);
        String adr = null;
        
        if (timeZone != null && latitude != null && longitude != null && address != null) {
            double lon = Double.parseDouble(longitude.value);
            double lat = Double.parseDouble(latitude.value);
            double t = Double.parseDouble(timeZone.value) / 60.0;
            try {
                adr = URLDecoder.decode(address.value, "utf-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }

            int lastDay = d.getActualMaximum(Calendar.DAY_OF_MONTH);
            int day = 1;
            d.set(Calendar.DAY_OF_MONTH, day);

            DateTime dtISO = new DateTime(d.getTimeInMillis());
            DateTime dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());

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
                c.hijriMonth = Application.islamMonth[dtIslamic.getMonthOfYear()-1];
                calItems.add(c);

                day++;
                d.set(Calendar.DAY_OF_MONTH, day);
                dtISO = new DateTime(d.getTimeInMillis());
                dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());
            }
        }
        render(calItems,month,monthNumber,year,adr,today,currentMonth);
    }

    public static void changeLocation() {
        render();
    }
}

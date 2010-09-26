/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 *
 * @author ali
 */
public class PrayTime {

    public String[] timeNames;
    public int Jafari;
    private int calcMethod;
    private int numIterations;
    private double[][] methodParams;
    private double lat;
    private double lng;
    private double JDate;
    private double timeZone;

    public PrayTime() {

        //------------------------ Constants --------------------------


        // Calculation Methods
        this.Jafari = 0;    // Ithna Ashari

        // Time Names
        this.timeNames = new String[]{
                    "Fajr", "Sunrise", "Dhuhr", "Asr", "Sunset", "Maghrib", "Isha","Midnight"};


        //---------------------- Global doubleiables --------------------

        this.calcMethod = 0;		// caculation method

        //--------------------- Technical Settings --------------------

        this.numIterations = 1;		// number of iterations needed to compute times

        //------------------- Calc Method Parameters --------------------

        this.methodParams = new double[1][5];


        /*  this.methodParams[methodNum] = new Array(fa, ms, mv, is, iv);

        fa : fajr angle
        ms : maghrib selector (0 = angle; 1 = minutes after sunset)
        mv : maghrib parameter value (in angle or minutes)
        is : isha selector (0 = angle; 1 = minutes after maghrib)
        iv : isha parameter value (in angle or minutes)
         */

        this.methodParams[this.Jafari] = new double[]{16, 0, 4, 0, 14};

    }
//-------------------- Interface Functions --------------------
// return prayer times for a given date

    public String[] getDatePrayerTimes(int year, int month, int day, double latitude, double longitude, double timeZone) {
        this.lat = latitude;
        this.lng = longitude;
        this.timeZone = timeZone;
        this.JDate = this.julianDate(year, month, day) - longitude / (15 * 24);
        return this.computeDayTimes();
    }
// return prayer times for a given date

    public String[] getPrayerTimes(Calendar date, double latitude, double longitude, double timeZone) {
        return this.getDatePrayerTimes(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH), latitude, longitude, timeZone);
    }

// convert float hours to 24h format
    public String floatToTime24(double time) {
        time = this.fixhour(time + 0.5 / 60);  // add 0.5 minutes to round
        double hours = Math.floor(time);
        double minutes = Math.floor((time - hours) * 60);
        return this.twoDigitsFormat((int) hours) + ":" + this.twoDigitsFormat((int) minutes);
    }

//---------------------- Calculation Functions -----------------------
// References:
// http://www.ummah.net/astronomy/saltime
// http://aa.usno.navy.mil/faq/docs/SunApprox.html
// compute declination angle of sun and equation of time
    public Double[] sunPosition(double jd) {
        double D = jd - 2451545.0;
        double g = this.fixangle(357.529 + 0.98560028 * D);
        double q = this.fixangle(280.459 + 0.98564736 * D);
        double L = this.fixangle(q + 1.915 * this.dsin(g) + 0.020 * this.dsin(2.0 * g));

        double R = 1.00014 - 0.01671 * this.dcos(g) - 0.00014 * this.dcos(2.0 * g);
        double e = 23.439 - 0.00000036 * D;

        double d = this.darcsin(this.dsin(e) * this.dsin(L));
        double RA = this.darctan2(this.dcos(e) * this.dsin(L), this.dcos(L)) / 15.0;
        RA = this.fixhour(RA);
        double EqT = q / 15.0 - RA;

        return new Double[]{d, EqT};
    }
// compute equation of time

    public Double equationOfTime(double jd) {
        return this.sunPosition(jd)[1];
    }
// compute declination angle of sun

    public Double sunDeclination(double jd) {
        return this.sunPosition(jd)[0];
    }
// compute mid-day (Dhuhr, Zawal) time

    public Double computeMidDay(double t) {
        double T = this.equationOfTime(this.JDate + t);
        double Z = this.fixhour(12.0 - T);
        return Z;
    }
// compute time for a given angle G

    public Double computeTime(double G, double t) {
        double D = this.sunDeclination(this.JDate + t);
        double Z = this.computeMidDay(t);
        double V = 1.0 / 15.0 * this.darccos((-this.dsin(G) - this.dsin(D) * this.dsin(this.lat))
                / (this.dcos(D) * this.dcos(this.lat)));
        return Z + (G > 90 ? -V : V);
    }
// compute the time of Asr

    public Double computeAsr(int step, double t) // Shafii: step=1, Hanafi: step=2
    {
        double D = this.sunDeclination(this.JDate + t);
        double G = -this.darccot(step + this.dtan(Math.abs(this.lat - D)));
        return this.computeTime(G, t);
    }
//---------------------- Compute Prayer Times -----------------------
// compute prayer times at given julian date

    public double[] computeTimes(double times[]) {

        double[] t = this.dayPortion(times);

        double Fajr = this.computeTime(180.0 - this.methodParams[this.calcMethod][0], t[0]);
        double Sunrise = this.computeTime(180 - 0.833, t[1]);
        double Dhuhr = this.computeMidDay(t[2]);
        double Asr = this.computeAsr(1 , t[3]);
        double Sunset = this.computeTime(0.833, t[4]);

        double Maghrib = this.computeTime(this.methodParams[this.calcMethod][2], t[5]);
        double Isha = this.computeTime(this.methodParams[this.calcMethod][4], t[6]);

        return new double[]{Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha};
    }
// compute prayer times at given julian date

    public String[] computeDayTimes() {
        double times[] = new double[]{5.0, 6.0, 12.0, 13.0, 18.0, 18.0, 18.0}; //default times

        for (double i = 1; i <= this.numIterations; i++) {
            times = this.computeTimes(times);
        }


        times = this.adjustTimes(times);

        return this.adjustTimesFormat(times);
    }
// adjust times in a prayer time array

    public double[] adjustTimes(double times[]) {
        for (int i = 0; i < 7; i++) {
            times[i] += this.timeZone - this.lng / 15.0;
        }

        if (this.methodParams[this.calcMethod][1] == 1) // Maghrib
        {
            times[5] = times[4] + this.methodParams[this.calcMethod][2] / 60.0;
        }
        if (this.methodParams[this.calcMethod][3] == 1) // Isha
        {
            times[6] = times[5] + this.methodParams[this.calcMethod][4] / 60.0;
        }

        return times;
    }
// convert times array to given time format

    public String[] adjustTimesFormat(double times[]) {
        String t[] = new String[8];

        for (int i = 0; i < 7; i++) {

            t[i] = this.floatToTime24(times[i]);

        }
        t[7] = this.calcMidnight(t[2]);
        return t;
    }

    public String calcMidnight(String dhur){
        StringTokenizer st = new StringTokenizer(dhur, ":");

        int zHour = Integer.parseInt(st.nextToken());
        int zMin = Integer.parseInt(st.nextToken());

        int hDiff = zHour + 11;
        int mDiff = zMin + 15;
        if((zMin+15) > 59){
            hDiff++;
            mDiff = (zMin+15) - 60;
        }

        return this.twoDigitsFormat((hDiff)%24)+":"+this.twoDigitsFormat(mDiff);
    }

// convert hours to day portions
    public double[] dayPortion(double times[]) {
        for (int i = 0; i < 7; i++) {
            times[i] /= 24.0;
        }
        return times;
    }
//---------------------- Misc Functions -----------------------

// add a leading 0 if necessary

    public String twoDigitsFormat(int num) {
        return (String) ((num < 10) ? "0" + num : "" + num);
    }
//---------------------- Julian Date Functions -----------------------
// calculate julian date from a calendar date

    public double julianDate(int year, int month, int day) {
        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        double A = Math.floor(year / 100);
        double B = 2 - A + Math.floor(A / 4);

        double JD = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;
        return JD;
    }

//---------------------- Trigonometric Functions -----------------------
// degree sin
    public double dsin(double d) {
        return Math.sin(this.dtr(d));
    }
// degree cos

    public double dcos(double d) {
        return Math.cos(this.dtr(d));
    }
// degree tan

    public double dtan(double d) {
        return Math.tan(this.dtr(d));
    }
// degree arcsin

    public double darcsin(double x) {
        return this.rtd(Math.asin(x));
    }
// degree arccos

    public double darccos(double x) {
        return this.rtd(Math.acos(x));
    }
// degree arctan

    public double darctan(double x) {
        return this.rtd(Math.atan(x));
    }
// degree arctan2

    public double darctan2(double y, double x) {
        return this.rtd(Math.atan2(y, x));
    }
// degree arccot

    public double darccot(double x) {
        return this.rtd(Math.atan(1 / x));
    }
// degree to radian

    public double dtr(double d) {
        return (d * Math.PI) / 180.0;
    }
// radian to degree

    public double rtd(double r) {
        return (r * 180.0) / Math.PI;
    }
// range reduce angle in degrees.

    public double fixangle(double a) {
        a = a - 360.0 * (Math.floor(a / 360.0));
        a = a < 0 ? a + 360.0 : a;
        return a;
    }
// range reduce hours to 0..23

    public double fixhour(double a) {
        a = a - 24.0 * (Math.floor(a / 24.0));
        a = a < 0 ? a + 24.0 : a;
        return a;
    }

}

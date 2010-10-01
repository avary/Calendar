/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ext;

import org.joda.time.DateTime;
import play.templates.JavaExtensions;

/**
 *
 * @author ali
 */
public class MyExtension extends JavaExtensions{
    
    public static String islamMonth [] = {"Muharram","Safar","Rabi'El Awwal","Rabi'At Thani","Joumada El Awwal","Joumada At Thani","Rajab",
    "Sha'ban","Ramadan","Shawwal","Dhoul Qa'da","Dhoul Hijja"};

    public static String islamFormat(DateTime date){
        String d = date.getDayOfMonth()+" "+islamMonth[date.getMonthOfYear()-1]+" "+date.getYear();
        return d;
    }

}

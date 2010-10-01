/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import models.Maraja;
import play.mvc.Controller;

/**
 *
 * @author ali
 */
public class Marajas extends Controller{

    public static void saveDays(int days[],long id[]){
        for (int i = 0; i < id.length; i++) {
            Maraja m = Maraja.findById(id[i]);
            m.days = days[i];
            m.save();
        }

        Administration.index();
    }

}

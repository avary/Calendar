/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.util.Map;
import models.Event;
import play.cache.Cache;
import play.mvc.Controller;

/**
 *
 * @author ali
 */
public class Events extends Controller{

    public static void save(Event event){
        event.save();

        Map<String,Event> events = (Map<String, Event>) Cache.get("event");

        events.put(event.day+":"+event.month, event);

        Administration.event();
    }

    public static void delete(long id){
        Event e = Event.findById(id);
        e.delete();

        Map<String,Event> events = (Map<String, Event>) Cache.get("event");
        events.remove(e.day+":"+e.month);

        Administration.event();
    }

}

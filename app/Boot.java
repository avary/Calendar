
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Event;
import models.Maraja;
import play.cache.Cache;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ali
 */
@OnApplicationStart
public class Boot extends Job<Void>{

    @Override
    public void doJob() throws Exception {
        if(Maraja.count() == 0){
            Fixtures.load("data.yml");
        }

        List<Event> events = Event.all().fetch();
        Map<String,Event> e = new HashMap<String, Event>();

        for (Event event : events) {
            e.put(event.day+":"+event.month, event);
        }

        Cache.add("event", e, "26h");
    }



}

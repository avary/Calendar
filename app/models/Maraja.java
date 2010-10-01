/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import javax.persistence.Entity;
import javax.persistence.Transient;
import org.joda.time.DateTime;
import play.db.jpa.Model;

/**
 *
 * @author ali
 */
@Entity
public class Maraja extends Model{

    public String name;
    public int days;
    @Transient public DateTime today;

}

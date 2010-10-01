/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import play.db.jpa.Model;

/**
 *
 * @author ali
 */
@Entity
public class Event extends Model{

    public int day;
    public int month;
    public String name;
    @Lob public String link;

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.db.jpa.Model;

/**
 *
 * @author ali
 */
@Entity
public class Newsletter extends Model{

    public String email;
    @ManyToOne public Maraja maraja;
    public int beforeDay;
    public boolean enabled;
    public String confirm;

}

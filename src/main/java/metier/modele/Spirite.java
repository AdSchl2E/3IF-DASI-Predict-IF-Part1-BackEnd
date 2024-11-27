/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier.modele;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author aschlee
 */
@Entity
public class Spirite extends Medium implements Serializable {

    private static final long serialVersionUID = 1L;

    private String support;

    public Spirite() {
    }

    public Spirite(String denomination, String genre, String presentation, String support) {
        super(denomination, genre, presentation);
        this.support = support;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }
    
}

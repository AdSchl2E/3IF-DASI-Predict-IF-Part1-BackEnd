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
public class Cartomancier extends Medium implements Serializable {

    private static final long serialVersionUID = 1L;

    public Cartomancier() {
    }

    public Cartomancier(String denomination, String genre, String presentation) {
        super(denomination, genre, presentation);
    }

}

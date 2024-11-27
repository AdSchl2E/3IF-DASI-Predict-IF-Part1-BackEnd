/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import javax.persistence.TypedQuery;
import metier.modele.Client;
import java.util.List;
import metier.modele.Consultation;


/**
 *
 * @author aschlee
 */
public class ClientDao {
    
    public void create(Client client) {
        JpaUtil.obtenirContextePersistance().persist(client);
    }
    
    public Client findByMail(String mail) {
        String jpql = "SELECT c FROM Client c WHERE c.mail = :mail";
        TypedQuery query = JpaUtil.obtenirContextePersistance().createQuery(jpql, Client.class);
        query.setParameter("mail", mail);
        Client clientIdentifie;
        try {
            clientIdentifie = (Client)query.getSingleResult();
        } catch (Exception e) {
            clientIdentifie = null;
        }
        
        return clientIdentifie;
    }
    
    public Client findById(Long id) {
        return JpaUtil.obtenirContextePersistance().find(Client.class, id);
    }
    
    public List<Client> findAllSortedByName() {
        TypedQuery<Client> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT c FROM Client c ORDER BY c.nom ASC, c.prenom ASC", Client.class);
        return query.getResultList();
    }

    public List<Consultation> findAllConsultation(Long clientId) {
        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT c FROM Consultation c WHERE c.client.id = :clientId ORDER BY c.dateConsultation DESC", Consultation.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }
}

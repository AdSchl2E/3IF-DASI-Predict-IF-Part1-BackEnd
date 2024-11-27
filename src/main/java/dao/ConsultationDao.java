/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import metier.modele.Consultation;


/**
 *
 * @author aschlee
 */
public class ConsultationDao {
    
    public void create(Consultation consultation) {
        JpaUtil.obtenirContextePersistance().persist(consultation);
    }
    public void updateConsultation(Consultation consultation) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            em.merge(consultation); // Ajouter la consultation dans la base de données
            et.commit();
        } catch (RuntimeException e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            throw e; 
        } 
    }

    public Consultation findById(Long id) {
        return JpaUtil.obtenirContextePersistance().find(Consultation.class, id);
    }
}
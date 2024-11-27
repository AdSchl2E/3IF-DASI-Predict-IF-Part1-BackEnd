/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.TypedQuery;
import metier.modele.Client;
import metier.modele.Consultation;
import metier.modele.Employe;

/**
 *
 * @author aschlee
 */
public class EmployeDao {

    public void create(Employe employe) {
        JpaUtil.obtenirContextePersistance().persist(employe);
    }

    public List<Employe> findAllSortedByNbConsultations() {
        TypedQuery<Employe> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT e FROM Employe e ORDER BY e.nbConsultation DESC", Employe.class);
        return query.getResultList();
    }

    public List<Employe> findEmployesEligibles(String genre) {
        TypedQuery<Employe> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT e FROM Employe e WHERE e.genre = :genre AND e.disponible = 1 ORDER BY e.nbConsultation ASC", Employe.class);
        query.setParameter("genre", genre);
        return query.getResultList();
    }

    public void updateEmploye(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            em.merge(employe); // Mettre à jour l'employé dans la base de données
            et.commit();
        } catch (OptimisticLockException e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            // Gérer l'exception (par exemple, afficher un message d'erreur)
            throw e;
        }
    }

    public Employe findById(Long id) {

        return JpaUtil.obtenirContextePersistance().find(Employe.class, id);
    }
    
    public Employe findByMail(String mail) {
        String jpql = "SELECT e FROM Employe e WHERE e.mail = :mail";
        TypedQuery query = JpaUtil.obtenirContextePersistance().createQuery(jpql, Client.class);
        query.setParameter("mail", mail);
        Employe employeIdentifie;
        try {
            employeIdentifie = (Employe)query.getSingleResult();
        } catch (Exception e) {
            employeIdentifie = null;
        }
        
        return employeIdentifie;
    }
    
    public List<Map<Long, Integer>> findAllSortedByNbConsultation() {

        TypedQuery<Object[]> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT e.id, e.nbConsultation FROM Employe e ORDER BY e.nbConsultation DESC", Object[].class);
        List<Object[]> results = query.getResultList();

        List<Map<Long, Integer>> employes = new ArrayList<>();

        results.stream().map((result) -> {
            Long mediumId = (Long) result[0];
            Integer consultationCount = ((Number) result[1]).intValue();
            Map<Long, Integer> employeInfo = new HashMap<>();
            employeInfo.put(mediumId, consultationCount);
            return employeInfo;
        }).forEachOrdered((employeInfo) -> {
            employes.add(employeInfo);
        });

        return employes;
    }

    public List<Consultation> findAllConsultations(Long id) {

        TypedQuery<Consultation> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT c FROM Consultation c WHERE c.employe.id = :employeId ORDER BY c.dateConsultation DESC", Consultation.class);
        query.setParameter("employeId", id);
        return query.getResultList();
    }
}

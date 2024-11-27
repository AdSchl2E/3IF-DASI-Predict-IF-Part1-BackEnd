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
import javax.persistence.TypedQuery;
import metier.modele.Medium;

/**
 *
 * @author aschlee
 */
public class MediumDao {

    public void create(Medium medium) {
        JpaUtil.obtenirContextePersistance().persist(medium);
    }

    public List<Medium> findAllSortedByName() {
        TypedQuery<Medium> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT m FROM Medium m ORDER BY m.denomination ASC", Medium.class);
        return query.getResultList();
    }

    public Medium findById(Long id) {
        return JpaUtil.obtenirContextePersistance().find(Medium.class, id);
    }

    public List<Map<Long, Integer>> findAllSortedByNbConsultation() {
        TypedQuery<Object[]> query = JpaUtil.obtenirContextePersistance().createQuery("SELECT m.id, COUNT(c.id) FROM Medium m, Consultation c WHERE m.id = c.medium.id GROUP BY m.id ORDER BY COUNT(c.id) DESC",  Object[].class);
        List<Object[]> results = query.getResultList();
        
        List<Map<Long, Integer>> mediums = new ArrayList<>();

        results.stream().map((result) -> {
            Long mediumId = (Long) result[0];
            Integer consultationCount = ((Number) result[1]).intValue();
            Map<Long, Integer> mediumInfo = new HashMap<>();
            mediumInfo.put(mediumId, consultationCount);
            return mediumInfo;
        }).forEachOrdered((mediumInfo) -> {
            mediums.add(mediumInfo);
        });

        return mediums;

    }
}

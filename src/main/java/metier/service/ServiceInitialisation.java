/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier.service;

import dao.EmployeDao;
import dao.JpaUtil;
import dao.MediumDao;
import java.io.IOException;
import metier.modele.Astrologue;
import metier.modele.Cartomancier;
import metier.modele.Employe;
import metier.modele.Medium;
import metier.modele.Spirite;

/**
 *
 * @author aschlee
 */
public class ServiceInitialisation {

    public Boolean initMedium() throws IOException {

        MediumDao mediumDao = new MediumDao();
        Boolean initialisationReussie = true;

        Medium m1 = new Spirite("Patrick", "M",
                "Voici Patrick, un véritable chef d'orchestre de la vie, toujours en harmonie avec les défis qui se présentent. Avec sa touche personnelle, il transforme chaque obstacle en une mélodie de succès. Patrick : la note parfaite dans le concert de l'existence.",
                "Boule de polystirène");
        Medium m2 = new Cartomancier("Irma", "F",
                "Rencontrez Irma, notre tempête créative qui apporte des rafales d'innovation et des averses d'idées brillantes partout où elle passe. Avec Irma, attendez-vous à un climat de changement, car elle sait comment faire souffler un vent de fraîcheur sur les routines les plus enracinées. Irma : un ouragan de talents prêt à déferler sur le monde !");

        Medium m3 = new Astrologue("Nathalie", "F",
                "Voici Nathalie, la lumière au bout du tunnel de chaque projet. Elle navigue à travers les défis avec la grâce d'une étoile filante, illuminant le chemin pour tous ceux qui ont la chance de graviter dans son orbite. Nathalie, c'est cette étincelle d'espoir dans la nuit des deadlines, transformant l'ordinaire en extraordinaire avec juste un sourire. Une véritable alchimiste du quotidien, elle transforme le plomb des lundis matin en or des vendredis soir !",
                "INSA Lyon IF", "1990");

        Medium m4 = new Spirite("Ulrich", "M",
                "Rencontrez Ulrich, le maître du temps et de l'espace de notre équipe. Avec la précision d'un horloger suisse et la vision d'un astronome, il aligne les étoiles de nos projets pour qu'elles brillent de mille feux. Ulrich, c'est le navigateur émérite de notre navire, trouvant toujours le cap vers de nouveaux horizons. Dans l'univers souvent tumultueux des deadlines et des défis, il reste notre phare, guidant notre quête d'excellence avec sérénité et détermination. Ulrich, un nom qui rime avec réussite, une présence qui signifie l'avenir.",
                "Chat");

        Medium m5 = new Cartomancier("Luna", "F",
                "Luna est une voyageuse de l'esprit, naviguant à travers les océans de la conscience pour découvrir les trésors cachés de l'âme humaine. Avec son jeu de cartes comme boussole, elle guide ceux qui cherchent des réponses à travers les méandres du destin. Luna : une éclaireuse des mystères, prête à dévoiler les secrets les plus profonds avec une simple carte retournée.");

        Medium m6 = new Astrologue("Alexandre", "M",
                "Rencontrez Alexandre, le gardien des étoiles et des constellations, déchiffrant les signes du cosmos pour éclairer le chemin des voyageurs temporels. Sa sagesse ancestrale et son intuition cosmique en font un guide inestimable dans l'odyssée de la vie. Alexandre, c'est cette voix dans la nuit étoilée, murmurant les secrets de l'univers à ceux qui écoutent avec leur cœur ouvert.",
                "Université de Paris", "1985");

        Medium m7 = new Spirite("Elena", "F",
                "Voici Elena, une danseuse d'âmes sur le fil ténu entre les mondes visible et invisible. Avec sa grâce céleste et son charisme magnétique, elle relie les esprits égarés à leur essence véritable, apportant réconfort et guérison à ceux qui cherchent la lumière. Elena : une présence douce mais puissante, tissant des liens qui transcendent les frontières de l'existence.",
                "Cuillère");

        Medium m8 = new Cartomancier("Gabriel", "M",
                "Gabriel est un messager des royaumes cachés, jonglant avec les cartes comme autant de portails vers l'inconnu. Son esprit vif et sa perspicacité légendaire révèlent les voies secrètes du destin, offrant à ceux qui le consultent un aperçu des possibilités insoupçonnées. Gabriel : un éclaireur des voies futures, prêt à guider ceux qui osent entreprendre le voyage vers l'inconnu.");
        
        JpaUtil.creerContextePersistance();

        try {
            JpaUtil.ouvrirTransaction();
            mediumDao.create(m1);
            mediumDao.create(m2);
            mediumDao.create(m3);
            mediumDao.create(m4);
            mediumDao.create(m5);
            mediumDao.create(m6);
            mediumDao.create(m7);
            mediumDao.create(m8);
            JpaUtil.validerTransaction();

        } catch (Exception ex) { // ça n'a pas marché

            ex.printStackTrace();
            JpaUtil.annulerTransaction(); // ne pas oublier d'annuler la transaction !
            initialisationReussie = false; // on pourrait aussi lancer une exception

        } finally { // dans tous les cas, on ferme l'entity manager
            JpaUtil.fermerContextePersistance();
        }
        return initialisationReussie;
    }

    public Boolean initEmploye() throws IOException {

        EmployeDao employeDao = new EmployeDao();
        Boolean initialisationReussie = true;

        Employe emp1 = new Employe("Surville", "Camille", "F", true, 0, "camille.lbds@gmail.com", "LucXArthur", "06 45 78 95 85");
        Employe emp2 = new Employe("Mazarin", "Jean", "M", true, 0, "jean.lrdm@gmail.com", "JeanneDarcFan", "06 45 85 74 84");
        Employe emp3 = new Employe("Montmorency", "Sophie", "F", true, 0, "sophie.lvdmm@gmail.com", "SophieLaGirafe", "07 84 51 21 16");
        Employe emp4 = new Employe("Valombre", "Pierre", "M", true, 0, "pierre.lndv@gmail.com", "PierrePapierCiseaux", "07 89 45 12 56");

        JpaUtil.creerContextePersistance();

        try {

            JpaUtil.ouvrirTransaction();

            employeDao.create(emp1);
            employeDao.create(emp2);
            employeDao.create(emp3);
            employeDao.create(emp4);

            JpaUtil.validerTransaction();

        } catch (Exception ex) { // ça n'a pas marché

            ex.printStackTrace();
            JpaUtil.annulerTransaction(); // ne pas oublier d'annuler la transaction !
            initialisationReussie = false; // on pourrait aussi lancer une exception

        } finally { // dans tous les cas, on ferme l'entity manager
            JpaUtil.fermerContextePersistance();
        }
        return initialisationReussie;
    }
}

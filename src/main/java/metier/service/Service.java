/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier.service;

import com.google.maps.model.LatLng;
import metier.modele.Client;
import dao.ClientDao;
import java.util.Date;
import metier.modele.ProfilAstral;
import util.GeoNetApi;
import util.Message;
import dao.ConsultationDao;
import dao.EmployeDao;
import metier.modele.Consultation;
import metier.modele.Employe;
import dao.JpaUtil;
import dao.MediumDao;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import metier.modele.Medium;
import util.AstroNetApi;

/**
 *
 * @author aschlee
 */
public class Service {

    public Boolean inscrireClient(Client client) throws IOException {

        ClientDao clientDao = new ClientDao();
        AstroNetApi astroApi = new AstroNetApi();
        Boolean inscriptionReussie = true;

        String prenom = client.getPrenom();
        Date dateNaissance = client.getDateNaissance();

        List<String> profil = astroApi.getProfil(prenom, dateNaissance);

        String signeZodiaque = profil.get(0);
        String signeChinois = profil.get(1);
        String couleur = profil.get(2);
        String animal = profil.get(3);

        ProfilAstral profilAstral = new ProfilAstral(signeZodiaque, signeChinois, couleur, animal);

        JpaUtil.creerContextePersistance();

        try {

            LatLng coordsClient = GeoNetApi.getLatLng(client.getAdressePostale());
            client.setLatitude(coordsClient.lat);
            client.setLongitude(coordsClient.lng);
            client.setProfilAstral(profilAstral);

            JpaUtil.ouvrirTransaction();

            clientDao.create(client);

            JpaUtil.validerTransaction();

            Message.envoyerMail("contact@predictif.fr", client.getMail(), "Bienvenue chez PREDICT'IF", "Bonjour " + client.getPrenom() + ", nous vous confirmons votre inscription au service PREDICT'IF.\nRendez-vous vite sur notre site pour consulter votre profil astrologique et profiter des dons incroyables de no mediums.");

        } catch (Exception ex) { // ça n'a pas marché

            //ex.printStackTrace();
            JpaUtil.annulerTransaction(); // ne pas oublier d'annuler la transaction !
            Message.envoyerMail("contact@predictif.fr", client.getMail(), "Echec de l'inscription chez PREDICT'IF", "Bonjour " + client.getPrenom() + ", votre inscription au service PREDICT'IF a malencontreusement échoué...\nMerci de recommencer ultérieurement.");
            inscriptionReussie = false; // on pourrait aussi lancer une exception

        } finally { // dans tous les cas, on ferme l'entity manager

            JpaUtil.fermerContextePersistance();
        }

        return inscriptionReussie;
    }

    public Client authentifierClient(String mail, String motDePasse) {

        ClientDao clientDao = new ClientDao();
        JpaUtil.creerContextePersistance();
        Client clientIdentifie = clientDao.findByMail(mail);
        
        if (clientIdentifie != null && !clientIdentifie.getMotDePasse().equals(motDePasse)) {
            clientIdentifie = null;
        }
        JpaUtil.fermerContextePersistance();

        return clientIdentifie;
    }

    public Employe authentifierEmploye(String mail, String motDePasse) {

        EmployeDao employeDao = new EmployeDao();
        JpaUtil.creerContextePersistance();
        Employe employeIdentifie = employeDao.findByMail(mail);
        
        if (employeIdentifie != null && !employeIdentifie.getMotDePasse().equals(motDePasse)) {
            employeIdentifie = null;
        }
        
        JpaUtil.fermerContextePersistance();

        return employeIdentifie;
    }

    public Client rechercherClientParID(Long id) {

        ClientDao clientDao = new ClientDao();
        JpaUtil.creerContextePersistance();
        Client clientIdentifie = clientDao.findById(id);

        JpaUtil.fermerContextePersistance();

        return clientIdentifie;
    }

    public List<Client> obtenirListeClients() {

        ClientDao clientDao = new ClientDao();

        JpaUtil.creerContextePersistance();

        List<Client> listeClients = clientDao.findAllSortedByName();

        JpaUtil.fermerContextePersistance();

        return listeClients;
    }

    public List<Consultation> obtenirHistoriqueConsultationsClient(Long clientId) {

        ClientDao clientDao = new ClientDao();

        JpaUtil.creerContextePersistance();

        List<Consultation> listeConsultation = clientDao.findAllConsultation(clientId);

        JpaUtil.fermerContextePersistance();

        return listeConsultation;
    }

    public Consultation demanderConsultation(Client client, Medium medium) {

        EmployeDao employeDao = new EmployeDao();
        ConsultationDao consultationDao = new ConsultationDao();
        JpaUtil.creerContextePersistance();
        Consultation consultationTrouvee;
        String genre = medium.getGenre();
        List<Employe> employesEligibles = employeDao.findEmployesEligibles(genre);
        
        if (employesEligibles.isEmpty()) {

            JpaUtil.fermerContextePersistance();
            consultationTrouvee = null;

        } else {

            Employe employeChoisi = employesEligibles.get(0);

            String genreClient = client.getGenre();
            String genreMedium = medium.getGenre();
            String titreClient = "";
            String titreMedium = "";

            switch (genreClient) {
                case "F":
                    titreClient = "Mme";
                    break;
                case "M":
                    titreClient = "M.";
                    break;
            }

            switch (genreMedium) {
                case "F":
                    titreMedium = "Mme";
                    break;
                case "M":
                    titreMedium = "M.";
                    break;
            }

            String message = "Bonjour " + employeChoisi.getPrenom() + ".\nConsultation requise pour " + titreClient + " " + client.getPrenom() + " " + client.getNom() + ".\nMédium à incarner : " + titreMedium + " " + medium.getDenomination();

            Message.envoyerNotification(employeChoisi.getTelephone(), message);
            employeChoisi.setDisponible(false); // L'employe est désormais indisponible
            employeChoisi.setNbConsultation(employeChoisi.getNbConsultation() + 1);   // On ajoute 1 a son nombre de consultation (peut etre faire plus tard...)
            employeDao.updateEmploye(employeChoisi);

            Date dateConsultation = new Date();
            String commentaire = "Consultation en cours...";

            consultationTrouvee = new Consultation(dateConsultation, commentaire, client, employeChoisi, medium);

            try {
                JpaUtil.ouvrirTransaction();
                consultationDao.create(consultationTrouvee);
                JpaUtil.validerTransaction();
            } catch (Exception ex) {
                //ex.printStackTrace();
                JpaUtil.annulerTransaction();
                consultationTrouvee = null;

            } finally {
                JpaUtil.fermerContextePersistance();
            }

        }
        return consultationTrouvee;

    }

    public List<Employe> obtenirListeEmployes() {

        EmployeDao employeDao = new EmployeDao();

        JpaUtil.creerContextePersistance();

        List<Employe> listeEmployes = employeDao.findAllSortedByNbConsultations();

        JpaUtil.fermerContextePersistance();

        return listeEmployes;
    }

    public Employe rechercherEmployeParID(Long id) {

        EmployeDao employeDao = new EmployeDao();
        JpaUtil.creerContextePersistance();
        Employe employeIdentifie = employeDao.findById(id);

        JpaUtil.fermerContextePersistance();

        return employeIdentifie;
    }

    public void ecrireCommentaire(Consultation consultation, String commentaire) {
        
        if (commentaire.equals("Consultation en cours...")) {
            commentaire = "";
        }
        
        ConsultationDao consultationeDao = new ConsultationDao();
        JpaUtil.creerContextePersistance();

        consultation.setCommentaire(commentaire);
        consultationeDao.updateConsultation(consultation);

        JpaUtil.fermerContextePersistance();
    }

    public void finirConsultation(Consultation consultation) {

        EmployeDao employeDao = new EmployeDao();
        JpaUtil.creerContextePersistance();
        Employe employe = consultation.getEmploye();
        employe.setDisponible(Boolean.TRUE);
        employeDao.updateEmploye(employe);

        JpaUtil.fermerContextePersistance();
    }

    public List<Medium> obtenirListeMediums() {

        MediumDao mediumDao = new MediumDao();

        JpaUtil.creerContextePersistance();

        List<Medium> listeMediums = mediumDao.findAllSortedByName();

        JpaUtil.fermerContextePersistance();

        return listeMediums;
    }

    public List<String> genererPredictions(String couleur, String animal, int niveauAmour, int niveauSante, int niveauTravail) throws IOException {

        AstroNetApi astroApi = new AstroNetApi();
        List<String> predictions = astroApi.getPredictions(couleur, animal, niveauAmour, niveauSante, niveauTravail);
        return predictions;
    }

    public Medium rechercherMediumParID(Long id) {

        MediumDao mediumDao = new MediumDao();
        JpaUtil.creerContextePersistance();
        Medium mediumIdentifie = mediumDao.findById(id);

        JpaUtil.fermerContextePersistance();

        return mediumIdentifie;
    }
    
    public Consultation obtenirConsultationEnCours(List<Consultation> consultations)
    {
        for(Consultation consultation:consultations)
        {
            if(consultation.getCommentaire().equals("Consultation en cours..."))
            {
                return consultation;
            }
        }
        return null;
    }
    
    public Consultation rechercherConsultationParID(Long id) {

        ConsultationDao consultationDao = new ConsultationDao();
        JpaUtil.creerContextePersistance();
        Consultation consultationIdentifie = consultationDao.findById(id);

        JpaUtil.fermerContextePersistance();

        return consultationIdentifie;
    }
    
    public List<Medium> obtenirTop5Medium() {

        MediumDao mediumDao = new MediumDao();

        JpaUtil.creerContextePersistance();

        List<Map<Long, Integer>> mediumConsultationCounts = mediumDao.findAllSortedByNbConsultation();

        // Récupérer les 5 premiers médiums
        List<Medium> top5Mediums = mediumConsultationCounts.stream()
                .map(entry -> mediumDao.findById(entry.keySet().iterator().next()))
                .limit(5)
                .collect(Collectors.toList());

        JpaUtil.fermerContextePersistance();

        return top5Mediums;
    }

    public List<Map<Long, Integer>> obtenirRepartitionConsultationsMedium() {

        MediumDao mediumDao = new MediumDao();

        JpaUtil.creerContextePersistance();

        List<Map<Long, Integer>> mediumConsultationCounts = mediumDao.findAllSortedByNbConsultation();

        JpaUtil.fermerContextePersistance();

        return mediumConsultationCounts;
    }

    public List<Map<Long, Integer>> obtenirRepartitionConsultationsEmploye() {

        EmployeDao employeDao = new EmployeDao();

        JpaUtil.creerContextePersistance();

        List<Map<Long, Integer>> employeConsultationCounts = employeDao.findAllSortedByNbConsultation();

        JpaUtil.fermerContextePersistance();

        return employeConsultationCounts;
    }

    public List<Consultation> obtenirHistoriqueConsultationsEmploye(Long employeId) {

        EmployeDao employeDao = new EmployeDao();

        JpaUtil.creerContextePersistance();

        List<Consultation> listeConsultationsEmployes = employeDao.findAllConsultations(employeId);

        JpaUtil.fermerContextePersistance();

        return listeConsultationsEmployes;
    }

    public void indiquerPretClient(Consultation consultation) {

        String genreMedium = consultation.getMedium().getGenre(); // Supposons que vous ayez un champ "genre" dans votre entité Medium
        String titre = "";

        // Déterminez le titre en fonction du genre du medium
        switch (genreMedium) {
            case "F":
                titre = "Mme";
                break;
            case "M":
                titre = "M.";
                break;
        }

        // Utilisez le titre dans le message de notification
        String message = "Bonjour " + consultation.getClient().getPrenom() + ".\nJ'ai bien reçu votre demande de consultation du " + consultation.getDateConsultation() + ".\nVous pouvez dès à présent me contacter au " + consultation.getEmploye().getTelephone() + ".\nA tout de suite !\nMédiumiquement vôtre,\n" + titre + " " + consultation.getMedium().getDenomination();

        Message.envoyerNotification(consultation.getClient().getTelephone(), message);
    }
}

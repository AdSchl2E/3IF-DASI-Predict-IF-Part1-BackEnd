/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import dao.ConsultationDao;
import dao.EmployeDao;
import metier.modele.Client;
import metier.service.Service;
import dao.JpaUtil;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import metier.modele.Consultation;
import metier.modele.Employe;
import metier.modele.Medium;
import metier.modele.ProfilAstral;
import metier.service.ServiceInitialisation;
import util.Saisie;

/**
 *
 * @author aschlee
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {

        JpaUtil.desactiverLog();

        JpaUtil.creerFabriquePersistance();

        String fullTest = Saisie.lireChaine("Réaliser tous les tests de cas limites ? (Assez long) [Oui / Non]");

        if (fullTest.equalsIgnoreCase("o") || fullTest.equalsIgnoreCase("y") || fullTest.equalsIgnoreCase("yes") || fullTest.equalsIgnoreCase("oui")) {
            realiserTestsCasLimites();
        }

        String testCondReel = Saisie.lireChaine("Réaliser un test en condition réél ? [Oui / Non]");

        if (testCondReel.equalsIgnoreCase("o") || testCondReel.equalsIgnoreCase("y") || testCondReel.equalsIgnoreCase("yes") || testCondReel.equalsIgnoreCase("oui")) {

            if (!fullTest.equalsIgnoreCase("o") && !fullTest.equalsIgnoreCase("y") && !fullTest.equalsIgnoreCase("yes") && !fullTest.equalsIgnoreCase("oui")) {
                initialisation();
            }

            realiserTestsConditionReel();
        } else {
            initialisation();
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Tests inscriptions clients [Press enter to run the tests]");
        creerInscriptions();

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Tests création de nouvelles consultations [Press enter to run the tests]");
        creerConsultations();
        updateNbConsultationsEmployes();

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Test scénario d'une consultation [Press enter to run the test]");
        scenarioConsultation();

        System.out.println("-------------------------------------------------------------------------------------------------");
        String cmd = Saisie.lireChaine("Tests scénarios de consultation (Remplissage : demande des consultations jusqu'a qu'il n y ai plus d'employés disponibles.) [Oui / Non]");
        if (cmd.equalsIgnoreCase("o") || cmd.equalsIgnoreCase("y") || cmd.equalsIgnoreCase("yes") || cmd.equalsIgnoreCase("oui")) {
            scenarioConsultationRemplissage();
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Affichage des des 5 médiums les plus demandés (dashboard employé) [Press enter to run the test]");
        simulerTop5Medium();

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Affichage des consultations effectuées par chacun des clients (historique client) [Press enter to run the test]");
        afficherHistoriqueConsultationsClient();

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Affichage de la proportion des consultations effectuées par chacun des médiums (dashboard employé) [Press enter to run the test]");
        simulerRepartitionConsultationsMedium();

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Affichage de la proportion des consultations effectuées par chaque employé (dashboard employé) [Press enter to run the test]");
        simulerRepartitionConsultationsEmploye();

        System.out.println("-------------------------------------------------------------------------------------------------");
        Saisie.lireChaine("Affichage de l'historique des consultations effectuées par les employés [Press enter to run the tests]");
        afficherHistoriqueConsultationEmploye();

        JpaUtil.fermerFabriquePersistance();

    }

    public static void creerInscriptions() throws IOException, ParseException {

        inscription("Noukam", "Junior", "M", "20 avenue Albert Einstein", "69100", "Villeurbanne", "07 53 28 16 43", "06/04/2004", "noukam.junior@insa-lyon.fr", "jrnkm2004");
        inscription("Bonkoungou", "Mathis", "M", "173 rue anatole france", "69100", "Villeurbanne", "07 63 58 11 06", "06/01/2004", "mathis.bonkoungou@insa-lyon.fr", "toto");
        inscription("Kusno", "Louis", "M", "20 avenue Albert Einstein", "69100", "Villeurbanne", "07 89 51 24 65", "06/04/2004", "noukam.junior@insa-lyon.fr", "louisIlTriche");
        inscription("Kusno", "Louis", "M", "azerty", "00140", "bloupblouP", "07 89 51 24 65", "06/04/2004", "louis.kusno@insa-lyon.fr", "louisIlTriche");
    }

    public static void inscription(String nom, String prenom, String genre, String adresse, String codePostal, String ville, String telephone, String dateNaissanceString, String mail, String motDePasse) throws IOException, ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date dateNaissance = simpleDateFormat.parse(dateNaissanceString);

        String adressePostale = adresse + ville + codePostal;

        Client client = new Client(nom, prenom, genre, mail, adressePostale, telephone, dateNaissance, motDePasse);

        Service service = new Service();

        Boolean inscriptionOk = service.inscrireClient(client);

        if (inscriptionOk) {
            System.out.println("Inscription réussie pour " + client.getNom() + " " + client.getPrenom() + " <" + client.getMail() + ">");
        } else {
            System.out.println("Inscription échouée pour " + client.getNom() + " " + client.getPrenom() + " <" + client.getMail() + ">");
        }
    }

    public static void inscriptionSaisie() throws IOException, ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Test Inscription");
        String nom = Saisie.lireChaine("Nom : ");
        String prenom = Saisie.lireChaine("Prenom : ");
        String genre = Saisie.lireChaine("Genre (M/F) : ");
        String mail = Saisie.lireChaine("Mail : ");
        String adresse = Saisie.lireChaine("Adresse : ");
        String telephone = Saisie.lireChaine("Telephone : ");
        String dateNaissanceString = Saisie.lireChaine("Date de naissance (DD/MM/YYYY) : ");
        String motDePasse = Saisie.lireChaine("Mot de passe : ");

        Date dateNaissance = simpleDateFormat.parse(dateNaissanceString);

        Client client = new Client(nom, prenom, genre, mail, adresse, telephone, dateNaissance, motDePasse);

        Service service = new Service();

        Boolean inscriptionOk = service.inscrireClient(client);

        if (inscriptionOk) {
            System.out.println("Inscription réussie pour " + client.getNom() + " " + client.getPrenom() + " <" + client.getMail() + ">");
        } else {
            System.out.println("Inscription échouée pour " + client.getNom() + " " + client.getPrenom() + " <" + client.getMail() + ">");
        }
    }

    public static Client authentification() {

        Service service = new Service();
        String mail = Saisie.lireChaine("Mail : ");
        String motDePasse = Saisie.lireChaine("Mot de passe : ");
        Client clientAuthentifie = service.authentifierClient(mail, motDePasse);

        if (clientAuthentifie != null) {
            System.out.println(clientAuthentifie.getMail() + " s'est connecté avec succès !");
        } else {
            System.out.println("Échec de l'authentification.");
        }

        return clientAuthentifie;
    }

    public static Employe authentificationEmploye() {

        Service service = new Service();
        String mail = Saisie.lireChaine("Mail : ");
        String motDePasse = Saisie.lireChaine("Mot de passe : ");
        Employe employeAuthentifie = service.authentifierEmploye(mail, motDePasse);

        if (employeAuthentifie != null) {
            System.out.println(employeAuthentifie.getMail() + " s'est connecté avec succès !");
        } else {
            System.out.println("Échec de l'authentification.");
        }

        return employeAuthentifie;
    }

    public static void rechercheClient() {
        System.out.println("Test Recherche Client Par ID");
        Service service = new Service();
        Client clientIdentifie = service.rechercherClientParID(Saisie.lireLong("Id : "));

        if (clientIdentifie != null) {
            System.out.println("Résulat de la recherche pour le client numéro " + clientIdentifie.getId() + " : " + clientIdentifie.getMail());
        } else {
            System.out.println("Aucun client correspondant !");
        }
    }

    public static void initialisation() throws IOException {
        ServiceInitialisation serviceInitialisation = new ServiceInitialisation();
        serviceInitialisation.initEmploye();
        serviceInitialisation.initMedium();
    }

    public static void afficherHistoriqueConsultationsClient() {
        Service service = new Service();
        System.out.printf("----------- Historique Consultations%n");
        List<Client> clients = service.obtenirListeClients();
        clients.forEach((client) -> {
            System.out.printf("------- Client #%d: %s %s %n",
                    client.getId(),
                    client.getNom(),
                    client.getPrenom());
            List<Consultation> historiqueConsultations = service.obtenirHistoriqueConsultationsClient(client.getId());
            historiqueConsultations.forEach((consultation) -> {
                System.out.printf("- Consultation #%d: %s with %s%n",
                        consultation.getId(),
                        consultation.getDateConsultation(),
                        consultation.getMedium().getDenomination());
            });
        });
        System.out.printf("------------------------------------%n");
    }

    public static void obtenirListeClients() {
        Service serviceClient = new Service();

        System.out.printf("----------- Liste Clients%n");

        List<Client> clients = serviceClient.obtenirListeClients();
        clients.forEach((client) -> {
            System.out.printf("- Client #%d: %s %s [%f, %f]%n",
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getLatitude(),
                    client.getLongitude());
        });
        System.out.printf("-------------------------%n");
    }

    public static void obtenirListeMediums() {
        Service service = new Service();

        System.out.printf("----------- Liste Mediums%n");

        List<Medium> mediums = service.obtenirListeMediums();
        mediums.forEach((medium) -> {
            System.out.printf("- Medium #%d: %s %s %n",
                    medium.getId(),
                    medium.getDenomination(),
                    medium.getGenre());
        });
        System.out.printf("-------------------------%n");
    }

    public static void obtenirListeEmployes() {
        Service serviceEmploye = new Service();

        System.out.printf("----------- Liste Employes%n");

        List<Employe> employes = serviceEmploye.obtenirListeEmployes();
        employes.forEach((employe) -> {
            System.out.printf("- Employe #%d: %s %s %s %s [%d] %n",
                    employe.getId(),
                    employe.getNom(),
                    employe.getPrenom(),
                    employe.getGenre(),
                    employe.getTelephone(),
                    employe.getNbConsultation());
        });
        System.out.printf("---------------------------%n");
    }

    public static void scenarioConsultationRemplissage() throws IOException, ParseException {

        Service service = new Service();

        Client clientDemandeur = service.authentifierClient(Saisie.lireChaine("Mail : "), Saisie.lireChaine("Mot de passe : "));

        if (clientDemandeur != null) {
            System.out.println(clientDemandeur.getMail() + " s'est connecté avec succès !");
            obtenirListeMediums();
            List<Medium> mediums = service.obtenirListeMediums();
            Medium mediumDemandee = service.rechercherMediumParID(mediums.get(0).getId());

            Consultation consultationDemandee = service.demanderConsultation(clientDemandeur, mediumDemandee);

            if (consultationDemandee != null) {
                ProfilAstral profilAstralClient1 = clientDemandeur.getProfilAstral();
                String couleur = profilAstralClient1.getCouleurPorteBonheur();
                String animal = profilAstralClient1.getAnimalTotem();

                System.out.println("Oh, il y a une autre consultation en même temps !");

                scenarioConsultationRemplissage();

                service.indiquerPretClient(consultationDemandee);

                List<String> predictions = service.genererPredictions(couleur, animal, 4, 2, 3);

                String predictionAmour = predictions.get(0);
                String predictionSante = predictions.get(1);
                String predictionTravail = predictions.get(2);

                System.out.println("~<[ Prédictions ]>~");
                System.out.println("[ Amour ] " + predictionAmour);
                System.out.println("[ Santé ] " + predictionSante);
                System.out.println("[Travail] " + predictionTravail);
                System.out.println("~~~~~~~~~~~~~~~~~~~");

                service.ecrireCommentaire(consultationDemandee, "C'était génial !");

                service.finirConsultation(consultationDemandee);
            } else {
                System.out.println("Le medium choisi est indisponible pour le moment...");
            }
        } else {
            System.out.println("Échec de l'authentification.");
        }

    }

    public static void scenarioConsultation() throws IOException, ParseException {

        Service service = new Service();

        Client clientDemandeur = service.authentifierClient(Saisie.lireChaine("Mail : "), Saisie.lireChaine("Mot de passe : "));

        if (clientDemandeur != null) {
            System.out.println(clientDemandeur.getMail() + " s'est connecté avec succès !");
            obtenirListeMediums();
            List<Medium> mediums = service.obtenirListeMediums();
            Medium mediumDemandee = service.rechercherMediumParID(mediums.get(0).getId());

            Consultation consultationDemandee = service.demanderConsultation(clientDemandeur, mediumDemandee);

            if (consultationDemandee != null) {
                ProfilAstral profilAstralClient1 = clientDemandeur.getProfilAstral();
                String couleur = profilAstralClient1.getCouleurPorteBonheur();
                String animal = profilAstralClient1.getAnimalTotem();

                service.indiquerPretClient(consultationDemandee);

                List<String> predictions = service.genererPredictions(couleur, animal, 4, 2, 3);

                String predictionAmour = predictions.get(0);
                String predictionSante = predictions.get(1);
                String predictionTravail = predictions.get(2);

                System.out.println("~<[ Prédictions ]>~");
                System.out.println("[ Amour ] " + predictionAmour);
                System.out.println("[ Santé ] " + predictionSante);
                System.out.println("[Travail] " + predictionTravail);
                System.out.println("~~~~~~~~~~~~~~~~~~~");

                service.ecrireCommentaire(consultationDemandee, "C'était génial !");

                service.finirConsultation(consultationDemandee);
            } else {
                System.out.println("Le medium choisi est indisponible pour le moment...");
            }
        } else {
            System.out.println("Échec de l'authentification.");
        }

    }

    public static void simulerTop5Medium() throws ParseException {

        Service service = new Service();

        List<Medium> top5Medium = service.obtenirTop5Medium();

        System.out.printf("----------- Top 5 Medium -----------%n");

        for (int i = 0; i < top5Medium.size(); i++) {
            System.out.printf("-=%d=- %-12s %s %n",
                    i + 1,
                    top5Medium.get(i).getDenomination(),
                    top5Medium.get(i).getGenre());
        }

        System.out.printf("------------------------------------%n");
    }

    public static void simulerRepartitionConsultationsMedium() {

        Service service = new Service();

        List<Map<Long, Integer>> repartitionConsultationsMediums = service.obtenirRepartitionConsultationsMedium();

        // Calcul du nombre total de consultations
        int totalConsultations = repartitionConsultationsMediums.stream()
                .mapToInt(map -> map.values().iterator().next())
                .sum();

        System.out.printf("----------- Répartition des consultations par Medium -----------%n");

        System.out.println("Nombre total de consultations : " + totalConsultations);

        // Affichage du graphique
        System.out.println("Répartition des consultations par medium :");

        for (Map<Long, Integer> entry : repartitionConsultationsMediums) {

            Long mediumId = entry.keySet().iterator().next();
            int consultationCount = entry.values().iterator().next();
            Medium medium = service.rechercherMediumParID(mediumId);

            // Calcul du pourcentage de consultations pour ce médium
            double percentage = (double) consultationCount / totalConsultations * 100;

            // Affichage du médium, du nombre de consultations et du pourcentage
            System.out.printf("%-12s : %-3d consultations   (%-5.2f%%)   ", medium.getDenomination(), consultationCount, percentage);
            // Affichage de l'histogramme
            for (int i = 0; i < (int) percentage; i++) {
                System.out.print("#");
            }
            System.out.println();
        }

        System.out.printf("----------------------------------------------------------------%n");

    }

    public static void simulerRepartitionConsultationsEmploye() {

        Service service = new Service();

        List<Map<Long, Integer>> repartitionConsultationsEmployes = service.obtenirRepartitionConsultationsEmploye();

        // Calcul du nombre total de consultations
        int totalConsultations = repartitionConsultationsEmployes.stream()
                .mapToInt(map -> map.values().iterator().next())
                .sum();

        System.out.printf("----------- Répartition des consultations par Employe -----------%n");

        System.out.println("Nombre total de consultations : " + totalConsultations);

        // Affichage du graphique
        System.out.println("Répartition des consultations par employe :");

        for (Map<Long, Integer> entry : repartitionConsultationsEmployes) {

            Long employeId = entry.keySet().iterator().next();
            int consultationCount = entry.values().iterator().next();
            Employe employe = service.rechercherEmployeParID(employeId);

            // Calcul du pourcentage de consultations pour ce médium
            double percentage = (double) consultationCount / totalConsultations * 100;

            // Affichage du médium, du nombre de consultations et du pourcentage
            System.out.printf("%-20s %-12s : %-3d consultations   (%-5.2f%%)   ", employe.getNom(), employe.getPrenom(), consultationCount, percentage);
            // Affichage de l'histogramme
            for (int i = 0; i < (int) percentage; i++) {
                System.out.print("#");
            }
            System.out.println();
        }

        System.out.printf("----------------------------------------------------------------%n");

    }

    public static void afficherHistoriqueConsultationEmploye() {

        Service service = new Service();

        System.out.printf("----------- Historique Consultations Employe%n");
        List<Employe> employes = service.obtenirListeEmployes();
        employes.forEach((employe) -> {
            System.out.printf("------- Employe #%d: %s %s %n",
                    employe.getId(),
                    employe.getNom(),
                    employe.getPrenom());
            List<Consultation> historiqueConsultationsEmployes = service.obtenirHistoriqueConsultationsEmploye(employe.getId());
            historiqueConsultationsEmployes.forEach((consultation) -> {
                System.out.printf("- Consultation #%d: %s %s as %s%n",
                        consultation.getId(),
                        consultation.getDateConsultation(),
                        consultation.getCommentaire(),
                        consultation.getMedium().getDenomination());
            });
        });
        System.out.printf("------------------------------------%n");
    }

    public static void creerConsultations() {
        Service service = new Service();

        List<Client> clients = service.obtenirListeClients();
        List<Medium> mediums = service.obtenirListeMediums();
        List<Employe> employes = service.obtenirListeEmployes();

        for (int i = 0; i < 50; i++) {
            // Sélectionner aléatoirement un client, un employé et filtrer les mediums du même sexe que l'employé
            Client client = clients.get((int) (Math.random() * clients.size()));
            Employe employe = employes.get((int) (Math.random() * employes.size()));
            List<Medium> mediumsSameSex = mediums.stream()
                    .filter(medium -> medium.getGenre().equals(employe.getGenre())) // Filtrer les mediums du même sexe que l'employé
                    .collect(Collectors.toList());

            // Vérifier si des mediums du même sexe sont disponibles
            if (!mediumsSameSex.isEmpty()) {

                Medium medium = mediumsSameSex.get((int) (Math.random() * mediumsSameSex.size()));

                // Générer une date aléatoire dans les 30 derniers jours
                Date dateConsultation = new Date(System.currentTimeMillis() - (long) (Math.random() * 30 * 24 * 60 * 60 * 1000));

                Consultation consultation = new Consultation(dateConsultation, "Commentaire...", client, employe, medium);

                ConsultationDao consultationDao = new ConsultationDao();    //On utilise le dao ici uniquement a des fins de test...
                JpaUtil.creerContextePersistance();

                try {
                    JpaUtil.ouvrirTransaction();
                    consultationDao.create(consultation);
                    JpaUtil.validerTransaction();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JpaUtil.annulerTransaction();
                    System.out.println("Echec de l'ajout de la consultation.");

                } finally {
                    JpaUtil.fermerContextePersistance();
                }

            } else {
                System.out.println("Aucun medium éligible et disponible trouvé.");
            }
        }
    }

    public static void updateNbConsultationsEmployes() {
        Service service = new Service();
        EmployeDao employeDao = new EmployeDao();

        List<Employe> employes = service.obtenirListeEmployes();

        JpaUtil.creerContextePersistance();
        try {
            JpaUtil.ouvrirTransaction();

            for (Employe employe : employes) {

                List<Consultation> consultations = service.obtenirHistoriqueConsultationsEmploye(employe.getId());
                int nbConsultations = (int) consultations.stream()
                        .filter(consultation -> consultation.getEmploye().getId().equals(employe.getId()))
                        .count();

                employe.setNbConsultation(employe.getNbConsultation() + nbConsultations);

                JpaUtil.creerContextePersistance();
                employeDao.updateEmploye(employe);
            }
            System.out.println("Tous les employés ont été mis à jour avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Échec de la mise à jour des employés.");
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        //obtenirListeEmployes();
    }

    public static void realiserTestsCasLimites() throws ParseException {

        String ladate1 = "26/03/2026";
        String ladate2 = "15/05/1990";
        String ladate3 = "22/08/1985";
        String ladate4 = "11/10/1982";
        String ladate5 = "11/10/1982";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date dateDeNaissance1 = simpleDateFormat.parse(ladate1);
        Date dateDeNaissance2 = simpleDateFormat.parse(ladate2);
        Date dateDeNaissance3 = simpleDateFormat.parse(ladate3);
        Date dateDeNaissance4 = simpleDateFormat.parse(ladate4);
        Date dateDeNaissance5 = simpleDateFormat.parse(ladate5);

        Client client1 = new Client("Garcia", "Lucie", "F", "lucie.garcia@example.com", "Nice", "06 12 34 56 78", dateDeNaissance1, "password");
        Client client2 = new Client("Martin", "Paul", "M", "paul.martin@example.com", "Paris", "01 23 45 67 89", dateDeNaissance2, "secret123");
        Client client3 = new Client("Dubois", "Sophie", "F", "sophie.dubois@example.com", "Marseille", "04 56 78 90 12", dateDeNaissance3, "mdp1234");
        Client client4 = new Client("Leclerc", "Pierre", "M", "pierre.leclerc@example.com", "Bordeaux", "05 67 89 01 23", dateDeNaissance4, "mdp4567");
        Client client5 = new Client("Petit", "Marie", "F", "marie.petit@example.com", "Souuuuuufffffffellllweyyerrrrsheeeiiimmmm", "03 23 45 67 89", dateDeNaissance5, "abc123");

        Service service = new Service();
        ServiceInitialisation serviceInitialisation = new ServiceInitialisation();

        JpaUtil.creerFabriquePersistance();

        try {
            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES CLIENTS ALORS QU'IL N'Y A PAS DE CLIENTS INSCRITS");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Client> clients = service.obtenirListeClients();
            if (clients.isEmpty()) {
                System.out.println("Liste vide.");
            } else {
                clients.forEach((client) -> {
                    System.out.println(client.toString());
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("INSCRIRE DES CLIENTS");
            System.out.println("##################################################################################################################");

            boolean inscriptionReussie1;
            inscriptionReussie1 = service.inscrireClient(client1);
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            if (inscriptionReussie1) {
                System.out.println("Client 1 inscrit avec succès.");
            } else {
                System.out.println("Impossible d'inscrire le client 1.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            boolean inscriptionReussie2;
            inscriptionReussie2 = service.inscrireClient(client2);
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            if (inscriptionReussie2) {
                System.out.println("Client 2 inscrit avec succès.");
            } else {
                System.out.println("Impossible d'inscrire le client 2.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            boolean inscriptionReussie3;
            inscriptionReussie3 = service.inscrireClient(client3);
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            if (inscriptionReussie3) {
                System.out.println("Client 3 inscrit avec succès.");
            } else {
                System.out.println("Impossible d'inscrire le client 3.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            boolean inscriptionReussie4;
            inscriptionReussie4 = service.inscrireClient(client4);
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            if (inscriptionReussie4) {
                System.out.println("Client 4 inscrit avec succès.");
            } else {
                System.out.println("Impossible d'inscrire le client 4.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            boolean inscriptionReussie5;
            inscriptionReussie5 = service.inscrireClient(client5);
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            if (inscriptionReussie5) {
                System.out.println("Client 5 inscrit avec succès.");
            } else {
                System.out.println("Impossible d'inscrire le client 5.");
                System.out.println("L'adresse du client 5 est incorrect");
            }

            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES CLIENTS APRES INSCRIPTION");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Client> clients1 = service.obtenirListeClients();
            if (clients1.isEmpty()) {
                System.out.println("Liste vide.");
            } else {
                clients1.forEach((client) -> {
                    System.out.println(client.toString());
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AUTHENTIFIER DES CLIENTS");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez d'authentifier un client non inscrit");
            Client clientAuthentifie = service.authentifierClient(Saisie.lireChaine("Mail : "), Saisie.lireChaine("Mot de passe : "));
            if (clientAuthentifie != null) {
                System.out.println(clientAuthentifie.getMail() + " s'est connecté avec succès !");
            } else {
                System.out.println("Échec de l'authentification.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez d'authentifier un client inscrit");
            Client clientAuthentifie2 = service.authentifierClient(Saisie.lireChaine("Mail : "), Saisie.lireChaine("Mot de passe : "));
            if (clientAuthentifie2 != null) {
                System.out.println(clientAuthentifie2.getMail() + " s'est connecté avec succès !");
            } else {
                System.out.println("Échec de l'authentification.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("RECHERCHER DES CLIENTS");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez de chercher un client non inscrit par son ID");
            obtenirListeClients();
            Client unclient = service.rechercherClientParID(Saisie.lireLong("Id : "));
            if (unclient != null) {
                System.out.println("Résulat de la recherche pour le client numéro " + unclient.getId() + " : ");
                System.out.println(unclient.toString());
            } else {
                System.out.println("Aucun client correspondant !");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            Client unclient2 = service.rechercherClientParID(Saisie.lireLong("Id : "));
            if (unclient2 != null) {
                System.out.println("Résulat de la recherche pour le client numéro " + unclient2.getId() + " : ");
                System.out.println(unclient2.toString());
            } else {
                System.out.println("Aucun client correspondant !");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES EMPLOYES ALORS QU'IL N'Y A PAS D'EMPLOYES");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Employe> employes = service.obtenirListeEmployes();

            if (employes.isEmpty()) {
                System.out.println("Liste vide.");
            } else {
                employes.forEach((employe) -> {
                    System.out.println(employe.toString());
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("INITIALISATION DES EMPLOYES");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            Boolean initEmploye = serviceInitialisation.initEmploye();
            System.out.println("INITIALISATION DES EMPLOYES = " + initEmploye);
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES EMPLOYES");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            obtenirListeEmployes();
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AUTHENTIFIER DES EMPLOYÉS");
            System.out.println("##################################################################################################################");

            employes = service.obtenirListeEmployes();
            if (employes.isEmpty()) {
                System.out.println("Liste vide.");
            } else {
                employes.forEach((employe) -> {
                    System.out.printf("- Employe #%d: %s %s %s %s [%d] %s : %s%n",
                            employe.getId(),
                            employe.getNom(),
                            employe.getPrenom(),
                            employe.getGenre(),
                            employe.getTelephone(),
                            employe.getNbConsultation(),
                            employe.getMail(),
                            employe.getMotDePasse());
                });
            }

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez d'authentifier un employé non inscrit");
            Employe employeAuthentifie = service.authentifierEmploye(Saisie.lireChaine("Mail : "), Saisie.lireChaine("Mot de passe : "));
            if (employeAuthentifie != null) {
                System.out.println(employeAuthentifie.getMail() + " s'est connecté avec succès !");
            } else {
                System.out.println("Échec de l'authentification.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez d'authentifier un employé inscrit");
            Employe employeAuthentifie2 = service.authentifierEmploye(Saisie.lireChaine("Mail : "), Saisie.lireChaine("Mot de passe : "));
            if (employeAuthentifie2 != null) {
                System.out.println(employeAuthentifie2.getMail() + " s'est connecté avec succès !");
            } else {
                System.out.println("Échec de l'authentification.");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("RECHERCHER DES EMPLOYES");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez de chercher un employé non inscrit par son ID");
            obtenirListeEmployes();
            Employe unEmploye = service.rechercherEmployeParID(Saisie.lireLong("Id : "));
            if (unEmploye != null) {
                System.out.println("Résultat de la recherche pour l'employé numéro " + unEmploye.getId() + " : ");
                System.out.println(unEmploye.toString());
            } else {
                System.out.println("Aucun employé correspondant !");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            Employe unEmploye2 = service.rechercherEmployeParID(Saisie.lireLong("Id : "));
            if (unEmploye2 != null) {
                System.out.println("Résultat de la recherche pour l'employé numéro " + unEmploye2.getId() + " : ");
                System.out.println(unEmploye2.toString());
            } else {
                System.out.println("Aucun employé correspondant !");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES MEDIUMS ALORS QU'IL N'Y A PAS DE MEDIUMS");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Medium> mediums = service.obtenirListeMediums();
            if (mediums.isEmpty()) {
                System.out.println("Liste vide.");
            } else {
                mediums.forEach((medium) -> {
                    System.out.printf("- Medium #%d: %s %s %n",
                            medium.getId(),
                            medium.getDenomination(),
                            medium.getGenre());
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("INITIALISATION DES MEDIUMS");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            Boolean initMedium = serviceInitialisation.initMedium();
            System.out.println("INITIALISATION DES MEDIUMS = " + initMedium);
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES MEDIUMS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Medium> mediums2 = service.obtenirListeMediums();
            if (mediums2.isEmpty()) {
                System.out.println("Liste vide.");
            } else {
                mediums2.forEach((medium) -> {
                    System.out.printf("- Medium #%d: %s %s %n",
                            medium.getId(),
                            medium.getDenomination(),
                            medium.getGenre());
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("RECHERCHER DES MEDIUMS");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez de chercher un médium non existant par son ID");
            obtenirListeMediums();
            Medium unMedium = service.rechercherMediumParID(Saisie.lireLong("Id : "));
            if (unMedium != null) {
                System.out.println("Résultat de la recherche pour le médium numéro " + unMedium.getId() + " : ");
                System.out.println(unMedium.toString());
            } else {
                System.out.println("Aucun médium correspondant !");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Essayez de chercher un médium existant par son ID");
            obtenirListeMediums();
            Medium unMedium2 = service.rechercherMediumParID(Saisie.lireLong("Id : "));
            if (unMedium2 != null) {
                System.out.println("Résultat de la recherche pour le médium numéro " + unMedium2.getId() + " : ");
                System.out.println(unMedium2.toString());
            } else {
                System.out.println("Aucun médium correspondant !");
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES CONSULTATIONS D'UN CLIENT ALORS QU'IL N'A PAS DE CONSULTATIONS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Consultation> consultations = service.obtenirHistoriqueConsultationsClient(service.obtenirListeClients().get(0).getId());
            if (consultations.isEmpty()) {
                System.out.println("Liste vide.");
            } else {
                consultations.forEach((consultation) -> {
                    System.out.printf("- Consultation #%d: %s %s as %s%n",
                            consultation.getId(),
                            consultation.getDateConsultation(),
                            consultation.getCommentaire(),
                            consultation.getMedium().getDenomination());
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES CONSULTATIONS DE CHAQUE CLIENT ALORS QU'IL N'Y A PAS DE CONSULTATIONS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Afficher Historique Consultations Clients");
            List<Client> clients3 = service.obtenirListeClients();
            if (clients3.isEmpty()) {
                System.out.println("Pas de clients");
            } else {
                clients3.forEach((client) -> {
                    System.out.printf("------- Client #%d: %s %s %n",
                            client.getId(),
                            client.getNom(),
                            client.getPrenom());

                    List<Consultation> consultationsDuClient = service.obtenirHistoriqueConsultationsClient(client.getId());

                    if (consultationsDuClient.isEmpty()) {
                        System.out.println(client.getPrenom() + " n'a jamais pris de consultation !");
                    } else {
                        consultationsDuClient.forEach((consultation) -> {
                            System.out.printf("- Consultation #%d: %s %s with %s%n",
                                    consultation.getId(),
                                    consultation.getDateConsultation(),
                                    consultation.getCommentaire(),
                                    consultation.getMedium().getDenomination());
                        });
                    }

                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES CONSULTATIONS DE CHAQUE EMPLOYÉ ALORS QU'IL N'Y A PAS DE CONSULTATIONS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Afficher Historique Consultations Employés");
            List<Employe> employes3 = service.obtenirListeEmployes();
            if (employes3.isEmpty()) {
                System.out.println("Pas d'employés");
            } else {
                employes3.forEach((employe) -> {
                    System.out.printf("------- Employé #%d: %s %s %n",
                            employe.getId(),
                            employe.getNom(),
                            employe.getPrenom());

                    List<Consultation> consultationsDeLEmploye = service.obtenirHistoriqueConsultationsEmploye(employe.getId());

                    if (consultationsDeLEmploye.isEmpty()) {
                        System.out.println(employe.getPrenom() + " n'a jamais pris de consultation !");
                    } else {
                        consultationsDeLEmploye.forEach((consultation) -> {
                            System.out.printf("- Consultation #%d: %s %s with %s%n",
                                    consultation.getId(),
                                    consultation.getDateConsultation(),
                                    consultation.getCommentaire(),
                                    consultation.getMedium().getDenomination());
                        });
                    }
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DE LA REPARTITION DES CONSULTATIONS PAR MEDIUM ALORS QU'IL N'Y A PAS DE CONSULTATIONS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Map<Long, Integer>> repartitionConsultationsMediums = service.obtenirRepartitionConsultationsMedium();

            // Calcul du nombre total de consultations
            int totalConsultationsMediums = repartitionConsultationsMediums.stream()
                    .mapToInt(map -> map.values().iterator().next())
                    .sum();
            System.out.println("Nombre total de consultations : " + totalConsultationsMediums);
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            // Affichage du graphique
            System.out.println("Répartition des consultations par Medium :");

            for (Map<Long, Integer> entry : repartitionConsultationsMediums) {

                Long meidumId = entry.keySet().iterator().next();
                int consultationParMed = entry.values().iterator().next();
                Medium medium = service.rechercherMediumParID(meidumId);

                // Calcul du pourcentage de consultations pour ce médium
                double percentages = (double) consultationParMed / totalConsultationsMediums * 100;

                // Affichage du médium, du nombre de consultations et du pourcentage
                System.out.printf("%-30s : %-3d consultations   (%-5.2f%%)   ", medium.getDenomination(), consultationParMed, percentages);

                System.out.println();
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DE LA REPARTITION DES CONSULTATIONS PAR EMPLOYE ALORS QU'IL N'Y A PAS DE CONSULTATIONS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Map<Long, Integer>> repartitionConsultationsEmployes = service.obtenirRepartitionConsultationsEmploye();

            // Calcul du nombre total de consultations
            int totalConsultationsEmployes = repartitionConsultationsEmployes.stream()
                    .mapToInt(map -> map.values().iterator().next())
                    .sum();
            System.out.println("Nombre total de consultations : " + totalConsultationsEmployes);
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            // Affichage du graphique
            System.out.println("Répartition des consultations par Employe :");

            for (Map<Long, Integer> entry : repartitionConsultationsEmployes) {

                Long employeId = entry.keySet().iterator().next();
                int consultationsParEmploye = entry.values().iterator().next();
                Employe employe = service.rechercherEmployeParID(employeId);

                // Calcul du pourcentage de consultations pour cet employé
                double pourcentage = (double) consultationsParEmploye / totalConsultationsEmployes * 100;

                // Affichage de l'employé, du nombre de consultations et du pourcentage
                System.out.printf("%-30s %-30s : %-3d consultations   (%-5.2f%%)   ", employe.getNom(), employe.getPrenom(), consultationsParEmploye, pourcentage);

                System.out.println();
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER TOP 5 MEDIUMS ALORS QU'IL N'Y A PAS DE CONSULTATIONS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Medium> top5Medium = service.obtenirTop5Medium();

            System.out.printf("----------- Top 5 Medium -----------%n");

            for (int i = 0; i < top5Medium.size(); i++) {
                System.out.printf("numero %d :  %-30s %-30s %n",
                        i + 1,
                        top5Medium.get(i).getDenomination(),
                        top5Medium.get(i).getGenre());
            }

            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("INITIALISATION DES CONSULTATIONS");
            System.out.println("##################################################################################################################");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            creerConsultations();
            updateNbConsultationsEmployes();
            System.out.println("INITIALISATION DES CONSULTATIONS = " + true);
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES CONSULTATIONS DE CHAQUE CLIENT");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Afficher Historique Consultations Clients");
            List<Client> clients4 = service.obtenirListeClients();
            if (clients4.isEmpty()) {
                System.out.println("Pas de clients");
            } else {
                clients4.forEach((client) -> {
                    System.out.printf("------- Client #%d: %s %s %n",
                            client.getId(),
                            client.getNom(),
                            client.getPrenom());

                    List<Consultation> consultationsDuClient = service.obtenirHistoriqueConsultationsClient(client.getId());

                    if (consultationsDuClient.isEmpty()) {
                        System.out.println(client.getPrenom() + " n'a jamais pris de consultation !");
                    } else {
                        consultationsDuClient.forEach((consultation) -> {
                            System.out.printf("- Consultation #%d: %s %s with %s%n",
                                    consultation.getId(),
                                    consultation.getDateConsultation(),
                                    consultation.getCommentaire(),
                                    consultation.getMedium().getDenomination());
                        });
                    }

                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DES CONSULTATIONS DE CHAQUE EMPLOYÉ");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("Afficher Historique Consultations Employés");
            List<Employe> employes4 = service.obtenirListeEmployes();
            if (employes4.isEmpty()) {
                System.out.println("Pas d'employés");
            } else {
                employes4.forEach((employe) -> {
                    System.out.printf("------- Employé #%d: %s %s %n",
                            employe.getId(),
                            employe.getNom(),
                            employe.getPrenom());

                    List<Consultation> consultationsDeLEmploye = service.obtenirHistoriqueConsultationsEmploye(employe.getId());

                    if (consultationsDeLEmploye.isEmpty()) {
                        System.out.println(employe.getPrenom() + " n'a jamais pris de consultation !");
                    } else {
                        consultationsDeLEmploye.forEach((consultation) -> {
                            System.out.printf("- Consultation #%d: %s %s with %s%n",
                                    consultation.getId(),
                                    consultation.getDateConsultation(),
                                    consultation.getCommentaire(),
                                    consultation.getMedium().getDenomination());
                        });
                    }
                });
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DE LA REPARTITION DES CONSULTATIONS PAR MEDIUM");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Map<Long, Integer>> repartitionConsultationsMediums2 = service.obtenirRepartitionConsultationsMedium();

            // Calcul du nombre total de consultations
            int totalConsultationsMediums2 = repartitionConsultationsMediums2.stream()
                    .mapToInt(map -> map.values().iterator().next())
                    .sum();
            System.out.println("Nombre total de consultations : " + totalConsultationsMediums2);
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            // Affichage du graphique
            System.out.println("Répartition des consultations par Medium :");

            for (Map<Long, Integer> entry : repartitionConsultationsMediums2) {

                Long meidumId = entry.keySet().iterator().next();
                int consultationParMed = entry.values().iterator().next();
                Medium medium = service.rechercherMediumParID(meidumId);

                // Calcul du pourcentage de consultations pour ce médium
                double percentages = (double) consultationParMed / totalConsultationsMediums2 * 100;

                // Affichage du médium, du nombre de consultations et du pourcentage
                System.out.printf("%-30s : %-3d consultations   (%-5.2f%%)   ", medium.getDenomination(), consultationParMed, percentages);
                for (int i = 0; i < (int) percentages; i++) {
                    System.out.print("#");
                }
                System.out.println();
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER LISTE DE LA REPARTITION DES CONSULTATIONS PAR EMPLOYE");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Map<Long, Integer>> repartitionConsultationsEmployes2 = service.obtenirRepartitionConsultationsEmploye();

            // Calcul du nombre total de consultations
            int totalConsultationsEmployes2 = repartitionConsultationsEmployes2.stream()
                    .mapToInt(map -> map.values().iterator().next())
                    .sum();
            System.out.println("Nombre total de consultations : " + totalConsultationsEmployes2);
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("----------------------------------------------------------------------------------------------------------------");
            // Affichage du graphique
            System.out.println("Répartition des consultations par Employe :");

            for (Map<Long, Integer> entry : repartitionConsultationsEmployes2) {

                Long employeId = entry.keySet().iterator().next();
                int consultationsParEmploye = entry.values().iterator().next();
                Employe employe = service.rechercherEmployeParID(employeId);

                // Calcul du pourcentage de consultations pour cet employé
                double pourcentage = (double) consultationsParEmploye / totalConsultationsEmployes2 * 100;

                // Affichage de l'employé, du nombre de consultations et du pourcentage
                System.out.printf("%-30s %-30s : %-3d consultations   (%-5.2f%%)   ", employe.getNom(), employe.getPrenom(), consultationsParEmploye, pourcentage);
                // Affichage de l'histogramme
                for (int i = 0; i < (int) pourcentage; i++) {
                    System.out.print("#");
                }
                System.out.println();
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            System.out.println("##################################################################################################################");
            System.out.println("AFFICHER TOP 5 MEDIUMS");
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            List<Medium> top5Medium2 = service.obtenirTop5Medium();

            System.out.printf("----------- Top 5 Medium -----------%n");

            for (int i = 0; i < top5Medium2.size(); i++) {
                System.out.printf("numero %d :  %-30s %-30s %n",
                        i + 1,
                        top5Medium2.get(i).getDenomination(),
                        top5Medium2.get(i).getGenre());
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Une erreur s'est produite : " + e.getMessage());
        } finally {
            System.out.println("##################################################################################################################");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("--------------------------------------------- FIN SERIES DE TESTS ----------------------------------------------");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            System.out.println("##################################################################################################################");
            System.out.println("");
        }
    }

    public static void realiserTestsConditionReel() throws IOException, ParseException {

        Service service = new Service();
        System.out.println("##################################################################################################################");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------- TESTS EN CONDITIONS REELLES ------------------------------------------");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("##################################################################################################################");

        System.out.println("Vous êtes Pierre Rochard, un nouveau client voulant essayer Predict'IF");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");

        inscription("Rochard", "Pierre", "M", "20 avenue Albert Einstein", "69100", "Villeurbanne", "07 17 28 39 46", "06/04/2004", "prochard@gmail.com", "B3300");

        System.out.println("Vous êtes bien inscris ! Vous avez également du recevoir un mail.");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");

        System.out.println("Vous allez désormais vous connecter");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");

        System.out.println("Votre adresse mail : prochard@gmail.com");
        System.out.println("Votre mot de passe : B3300");

        Client clientAuthentifie = authentification();

        while (clientAuthentifie == null) {

            System.out.println("L'authentification a échoué, veuillez réessayer.\n");
            System.out.println("Votre adresse mail : prochard@gmail.com");
            System.out.println("Votre mot de passe : B3300");

            clientAuthentifie = authentification();
        }

        System.out.println("\nAffichage de votre profil astral :");

        ProfilAstral profilAstral = clientAuthentifie.getProfilAstral();

        String signeZodiaque = profilAstral.getSigneChinois();
        String signeChinois = profilAstral.getSigneChinois();
        String couleur = profilAstral.getCouleurPorteBonheur();
        String animal = profilAstral.getAnimalTotem();

        System.out.println("Votre signe du zodiaque     : " + signeZodiaque);
        System.out.println("Votre signe chinois         : " + signeChinois);
        System.out.println("Votre couleur porte-bonheur : " + couleur);
        System.out.println("Votre animal totem          : " + animal);

        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("Vous souhaitez maintenant demander une consultation.");

        System.out.println("La liste des médiums s'affiche :");
        obtenirListeMediums();
        Long idMediumChoisi = Saisie.lireLong("Veuillez entrer l'id du médium choisi : ");

        Medium mediumChoisi = service.rechercherMediumParID(idMediumChoisi);

        System.out.println("Vous avez choisi " + mediumChoisi.getDenomination() + ".");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("Vous avez demandé une consultation avec le médium.");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("L'employé élu par l'application pour donner la consultation est prévenu par mail.");

        Consultation consultationDemandee = service.demanderConsultation(clientAuthentifie, mediumChoisi);
        Employe employeDonnantConsultation = consultationDemandee.getEmploye();
        
        System.out.println("L'employé (" + employeDonnantConsultation.getPrenom() + ") se connecte sur l'application.");
        System.out.println("Désormais on va incarner l'employé qui donne la consultation.");

        System.out.println("Email de l'employée        : " + employeDonnantConsultation.getMail());
        System.out.println("Mot de passe de l'employée : " + employeDonnantConsultation.getMotDePasse());

        Employe employeAuthentifie = authentificationEmploye();

        while (employeAuthentifie == null) {

            System.out.println("L'authentification a échoué, veuillez réessayer.\n");
            System.out.println("Email de l'employée        : " + employeDonnantConsultation.getMail());
            System.out.println("Mot de passe de l'employée : " + employeDonnantConsultation.getMotDePasse());
            
            employeAuthentifie = authentificationEmploye();
        }
        
        List<Consultation> historiqueConsultationsEmployes2 = service.obtenirHistoriqueConsultationsEmploye(employeDonnantConsultation.getId());
        
        Consultation consultationEnCours = service.obtenirConsultationEnCours(historiqueConsultationsEmployes2);
        Client clientDemandeur = consultationEnCours.getClient();
        ProfilAstral profilAstralClientDemandeur = clientDemandeur.getProfilAstral();
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("On va observer les infos disponible qu'on a sur le client.");

        System.out.println("Nom et prénom du client         : " + clientDemandeur.getNom() + " " + clientDemandeur.getPrenom());
        System.out.println("Age du client                   : " + clientDemandeur.getAge());
        System.out.println("Signe du zodiaque du client     : " + profilAstralClientDemandeur.getSigneZodiaque());
        System.out.println("Signe chinois  du client        : " + profilAstralClientDemandeur.getSigneChinois());
        System.out.println("Couleur porte-bonheur du client : " + profilAstralClientDemandeur.getCouleurPorteBonheur());
        System.out.println("Animal totem du client          : " + profilAstralClientDemandeur.getAnimalTotem());

        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("On va observer l'historique des consultations du client.");
        List<Consultation> historiqueConsultations = service.obtenirHistoriqueConsultationsClient(clientDemandeur.getId());
        historiqueConsultations.forEach((consultation) -> {
            System.out.printf("- Consultation #%d: %s [%s] as %s%n",
                    consultation.getId(),
                    consultation.getDateConsultation(),
                    consultation.getCommentaire(),
                    consultation.getMedium().getDenomination());
        });
        System.out.println("Il n'y a que notre consultation actuelle, on en conclut que c'est un nouveau client.");

        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("On va indiquer au client qu'on est prêt.");

        service.indiquerPretClient(consultationDemandee);

        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("Le client appelle l'employé.");

        System.out.println("Au cours de la consultation on aimerait générer des prédictions pour notre client.");

        System.out.println("On saisit 3 notes de 1 à 4 en amour, santé et travail");

        System.out.println("Amour   : 4");
        System.out.println("Santé   : 2");
        System.out.println("Travail : 3");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        List<String> predictions = service.genererPredictions(couleur, animal, 4, 2, 3);

        String predictionAmour = predictions.get(0);
        String predictionSante = predictions.get(1);
        String predictionTravail = predictions.get(2);

        System.out.println("On génére les prédictions.");

        System.out.println("~<[ Prédictions ]>~");
        System.out.println("[ Amour ] " + predictionAmour);
        System.out.println("[ Santé ] " + predictionSante);
        System.out.println("[Travail] " + predictionTravail);
        System.out.println("~~~~~~~~~~~~~~~~~~~");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("La consultation s'est bien passé elle est désormais terminé.");
        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("On écrit maintenant un commentaire à l'intention de nos collèges.");

        service.ecrireCommentaire(consultationDemandee, Saisie.lireChaine("Commentaire : "));

        System.out.println("On envoie le commentaire et la consultation se finit.");

        service.finirConsultation(consultationDemandee);

        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("On affiche désormais notre historique des consultations.");
        List<Consultation> historiqueConsultationsEmployes = service.obtenirHistoriqueConsultationsEmploye(employeDonnantConsultation.getId());
        historiqueConsultationsEmployes.forEach((consultation) -> {
            System.out.printf("- Consultation #%d: %s [%s] as %s%n",
                    consultation.getId(),
                    consultation.getDateConsultation(),
                    consultation.getCommentaire(),
                    consultation.getMedium().getDenomination());
        });

        Saisie.lireChaine("[Appuyez sur entrée pour continuer...]");
        System.out.println("Le client fait de même.");

        List<Consultation> historiqueConsultationsClient = service.obtenirHistoriqueConsultationsClient(clientAuthentifie.getId());
        historiqueConsultationsClient.forEach((consultation) -> {
            System.out.printf("- Consultation #%d: %s with %s%n",
                    consultation.getId(),
                    consultation.getDateConsultation(),
                    consultation.getMedium().getDenomination());
        });

        System.out.println("##################################################################################################################");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("------------------------------------- TESTS EN CONDITIONS REELLES TERMINEE --------------------------------------");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("##################################################################################################################");

    }
}

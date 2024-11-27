package metier.modele;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aschlee
 */
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

@Entity
public class Client implements Serializable {
    
    // Attributs 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String prenom;
    private String genre;
    private int age;
    @Column(nullable=false, unique=true)
    private String mail;
    private String motDePasse;
    private String adressePostale;
    private String telephone;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateNaissance;
    private Double latitude;
    private Double longitude;
    @OneToOne(cascade = CascadeType.PERSIST)
    private ProfilAstral profilAstral;

    public Client() {
    }
    
    // Constructeur
    
    public Client(String nom, String prenom, String genre, String mail, String adressePostale, String telephone, Date dateNaissance, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = genre;
        this.mail = mail;
        this.adressePostale = adressePostale;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.motDePasse = motDePasse;
        this.age = calculerAge(dateNaissance);
    }
    
    // Methode
    
    private int calculerAge(Date dateNaissance) {

        Date dateActuelle = new Date();
        int anneeNaissance = dateNaissance.getYear();
        int anneeActuelle = dateActuelle.getYear();
        int ageClient = anneeActuelle - anneeNaissance;
        if (dateActuelle.getMonth() < dateNaissance.getMonth() || (dateActuelle.getMonth() == dateNaissance.getMonth() && dateActuelle.getDate() < dateNaissance.getDate())) {
            ageClient--;
        }

        return ageClient;
    }
    // Redefinition de la methode toString()
    @Override
    public String toString() {
        return "Client: " +
                "id=" + id + ";" +
                "nom=" + nom + ";" +
                "prenom=" + prenom + ";" +
                "mail=" + mail + ";" +
                "motDePasse=" + motDePasse + ";" +
                "adressePostale=" + adressePostale + ";" +
                "latitude=" + latitude + ";" +
                "longitude=" + longitude + ";";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getAdressePostale() {
        return adressePostale;
    }

    public void setAdressePostale(String adressePostale) {
        this.adressePostale = adressePostale;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ProfilAstral getProfilAstral() {
        return profilAstral;
    }

    public void setProfilAstral(ProfilAstral profilAstral) {
        this.profilAstral = profilAstral;
    }
    
}
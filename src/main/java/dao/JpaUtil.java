package dao;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

/**
 * Cette classe fournit des méthodes statiques utiles pour accéder aux
 * fonctionnalités de JPA (Entity Manager, Entity Transaction). Le nom de
 * l'unité de persistance (PERSISTENCE_UNIT_NAME) doit être conforme à la
 * configuration indiquée dans le fichier persistence.xml du projet.
 *
 * @author DASI Team
 */
public class JpaUtil {

    // *************************************************************************************
    // * TODO: IMPORTANT -- Adapter le nom de l'Unité de Persistance (cf. persistence.xml) *
    // *************************************************************************************
    /**
     * Nom de l'unité de persistance utilisée par la Factory de Entity Manager.
     * <br><strong>Vérifier le nom de l'unité de persistance
     * (cf.&nbsp;persistence.xml)</strong>
     */
    public static final String PERSISTENCE_UNIT_NAME = "fr.insalyon.dasi.test_PredictIF_jar_1.0-SNAPSHOTPU";
    /**
     * Factory de Entity Manager liée à l'unité de persistance.
     * <br/><strong>Vérifier le nom de l'unité de persistance indiquée dans
     * l'attribut statique PERSISTENCE_UNIT_NAME
     * (cf.&nbsp;persistence.xml)</strong>
     */
    private static EntityManagerFactory entityManagerFactory = null;
    /**
     * Gère les instances courantes de Entity Manager liées aux Threads.
     * L'utilisation de ThreadLocal garantie une unique instance courante par
     * Thread.
     */
    private static final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<EntityManager>() {

        @Override
        protected EntityManager initialValue() {
            return null;
        }
    };
    /**
     * Indicateur d'affichage du Log de JpaUtil.
     * Par défaut, le Log de JpaUtil s'affiche dans la console.
     * Utiliser la méthode <code>desactiverLog()</code> pour désactiver cet affichage.
     */
    private static boolean JPAUTIL_LOG_ACTIVE = true;

    /**
     * Méthode privée d'affichage du Log sur la console.
     * @param message Message à afficher dans le Log
     */
    private static void log(String message) {
        if (JPAUTIL_LOG_ACTIVE) {
            System.out.println("[JpaUtil:Log] " + message);
        }
    }
    
    /**
     * Méthode pour désactiver l'affichage du Log de JpaUtil.
     * À utiliser avant la méthode <code>creerFabriquePersistance()</code> pour
     * également désactiver le Log de la librairie EclipseLink.
     */    
    public static void desactiverLog() {
        JPAUTIL_LOG_ACTIVE = false;
    }

    /**
     * Initialise la Fabrique (Factory) de Contexte de Persistance (Entity Manager).
     * <br><strong>À utiliser uniquement au début de la méthode main() [projet
     * Java Application] ou dans la méthode init() de la Servlet Contrôleur
     * (ActionServlet) [projet Web Application].</strong>
     */
    public static synchronized void creerFabriquePersistance() {
        log("Création de la fabrique de contexte de persistance");
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
        Map<String, String> propertyMap = new HashMap<>();
        if (!JPAUTIL_LOG_ACTIVE) {
            propertyMap.put("eclipselink.logging.level", "OFF");
        }
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME,propertyMap);
    }

    /**
     * Libère la Fabrique (Factory) de Contexte de Persistance (Entity Manager).
     * <br><strong>À utiliser uniquement à la fin de la méthode main() [projet
     * Java Application] ou dans la méthode destroy() de la Servlet Contrôleur
     * (ActionServlet) [projet Web Application].</strong>
     */
    public static synchronized void fermerFabriquePersistance() {
        log("Fermeture de la fabrique de contexte de persistance");
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    /**
     * Créée l'instance courante du Contexte de Persistance (Entity Manager), liée à ce Thread.
     * <br><strong>À utiliser uniquement au niveau Service.</strong>
     */
    public static void creerContextePersistance() {
        log("Création du contexte de persistance");
        threadLocalEntityManager.set(entityManagerFactory.createEntityManager());
    }

    /**
     * Ferme l'instance courante du Contexte de Persistance (Entity Manager), liée à ce Thread.
     * <br><strong>À utiliser uniquement au niveau Service.</strong>
     */
    public static void fermerContextePersistance() {
        log("Fermeture du contexte de persistance");
        EntityManager em = threadLocalEntityManager.get();
        em.close();
        threadLocalEntityManager.set(null);
    }

    /**
     * Démarre une transaction sur l'instance courante du Contexte de Persistance (Entity Manager).
     * <br><strong>À utiliser uniquement au niveau Service.</strong>
     */
    public static void ouvrirTransaction() throws Exception {
        log("Ouverture de la transaction (begin)");
        try {
            EntityManager em = threadLocalEntityManager.get();
            em.getTransaction().begin();
        } catch (Exception ex) {
            log("Erreur lors de l'ouverture de la transaction");
            throw ex;
        }
    }

    /**
     * Valide la transaction courante sur l'instance courante du Contexte de Persistance (Entity Manager).
     * <br><strong>À utiliser uniquement au niveau Service.</strong>
     *
     * @exception RollbackException lorsque le <em>commit</em> n'a pas réussi.
     */
    public static void validerTransaction() throws RollbackException, Exception {
        log("Validation de la transaction (commit)");
        try {
            EntityManager em = threadLocalEntityManager.get();
            em.getTransaction().commit();
        } catch (Exception ex) {
            log("Erreur lors de la validation (commit) de la transaction");
            throw ex;
        }
    }

    /**
     * Annule la transaction courante sur l'instance courante du Contexte de Persistance (Entity Manager).
     * Si la transaction courante n'est pas démarrée, cette méthode n'effectue
     * aucune opération.
     * <br><strong>À utiliser uniquement au niveau Service.</strong>
     */
    public static void annulerTransaction() {
        try {
            log("Annulation de la transaction (rollback)");

            EntityManager em = threadLocalEntityManager.get();
            if (em.getTransaction().isActive()) {
                log("Annulation effective de la transaction (rollback d'une transaction active)");
                em.getTransaction().rollback();
            }

        } catch (Exception ex) {
            log("Erreur lors de l'annulation (rollback) de la transaction");
        }
    }

    /**
     * Retourne l'instance courante de Entity Manager.
     * <br><strong>À utiliser uniquement au niveau DAO.</strong>
     *
     * @return instance de Entity Manager
     */
    protected static EntityManager obtenirContextePersistance() {
        log("Obtention du contexte de persistance");
        return threadLocalEntityManager.get();
    }
}

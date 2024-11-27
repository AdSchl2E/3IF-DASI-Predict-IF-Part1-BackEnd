package util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author DASI Team
 */
public class Message {
    
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd~HH:mm:ss");
    public static final SimpleDateFormat HORODATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
    
    private static final PrintStream OUT = System.out;
    
    private static void debut() {
        Date maintenant = new Date();
        OUT.println();
        OUT.println();
        OUT.println(FG_BLUE+"---<([ MESSAGE @ " + TIMESTAMP_FORMAT.format(maintenant) + " ])>---"+RESET);
        OUT.println();
    }
    
    private static void fin() {
        OUT.println();
        OUT.println(FG_BLUE+"---<([ FIN DU MESSAGE ])>---"+RESET);
        OUT.println();
        OUT.println();
    }
    
    public static void envoyerMail(String mailExpediteur, String mailDestinataire, String objet, String corps) {
        
        String MAIL_COLOR = FG_CYAN;
        
        Date maintenant = new Date();
        Message.debut();
        OUT.println(MAIL_COLOR+"~~~ E-mail envoyé le " + HORODATE_FORMAT.format(maintenant) + " ~~~");
        OUT.println(MAIL_COLOR+"De : " + mailExpediteur);
        OUT.println(MAIL_COLOR+"À  : " + mailDestinataire);
        OUT.println(MAIL_COLOR+"Obj: " + objet);
        OUT.println();
        OUT.println(MAIL_COLOR+corps.replace("\n", "\n"+MAIL_COLOR));
        OUT.print(RESET);
        Message.fin();
    }

    public static void envoyerNotification(String telephoneDestinataire, String message) {
        
        String NOTIFICATION_COLOR = FG_MAGENTA;
        
        Date maintenant = new Date();
        Message.debut();
        OUT.println(NOTIFICATION_COLOR+"~~~ Notification envoyée le " + HORODATE_FORMAT.format(maintenant) + " ~~~");
        OUT.println(NOTIFICATION_COLOR+"À  : " + telephoneDestinataire);
        OUT.println();
        OUT.println(NOTIFICATION_COLOR+message.replace("\n", "\n"+NOTIFICATION_COLOR));
        OUT.print(RESET);
        Message.fin();
    }


    // ANSI escape codes for console colors
    
    public static final String FG_BLACK = "\u001b[30m";
    public static final String FG_BLUE = "\u001b[34m";
    public static final String FG_CYAN = "\u001b[36m";
    public static final String FG_GREEN = "\u001b[32m";
    public static final String FG_MAGENTA = "\u001b[35m";
    public static final String FG_RED = "\u001b[31m";
    public static final String FG_WHITE = "\u001b[37m";
    public static final String FG_YELLOW = "\u001b[33m";

    public static final String BG_BLACK = "\u001b[40m"; // Problem ?
    public static final String BG_BLUE = "\u001b[44m";
    public static final String BG_CYAN = "\u001b[46m";
    public static final String BG_GREEN = "\u001b[42m";
    public static final String BG_MAGENTA = "\u001b[45m";
    public static final String BG_RED = "\u001b[41m";
    public static final String BG_WHITE = "\u001b[47m";
    public static final String BG_YELLOW = "\u001b[43m";

    public static final String RESET = "\u001B[0m";
    
}
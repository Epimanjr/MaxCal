package maxcal;

import biweekly.component.VEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static maxcal.Manipulation.convertListToMap;

/**
 *
 * @author maxime
 */
public class Shell {

    /**
     * Launch shell.
     */
    public static void launchShell() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("MaxCal$ ");
            String line = sc.nextLine();
            switch (line) {
                case "exit":
                    System.exit(0);
                    break;
                case "help":
                    help();
                    break;
                case "open":
                    open(sc);
                    break;
                case "addlink":
                    addLink(sc);
                    break;
                case "listlinks":
                    listLinks(sc);
                    break;
                case "list":
                    listEvents();
                    break;
                case "reset":
                    reset();
                    break;
            }
        }
    }

    /**
     * List all events for week.
     */
    public static void listEvents() {
        ArrayList<String> listDates = Manipulation.getDates(Data.listEvents);
        HashMap<String, ArrayList<VEvent>> map = convertListToMap(Data.listEvents);
        for (String date : listDates) {
            System.out.println("Le " + date);
            ArrayList<VEvent> listeTmp = map.get(date);
            for (VEvent event : listeTmp) {
                String groupe = Data.config.get("groupe");
                String str = (groupe.equals("G1")) ? "G2" : "G1";
                if (event.getSummary().getValue().startsWith("CM") || !event.getSummary().getValue().contains(str)) {
                    Manipulation.printEvent(event);
                }
            }
        }
    }

    /**
     * Detect the next course, search all links and open links
     *
     * @param sc Scanner
     */
    public static void open(Scanner sc) {
        if (!Data.listEvents.isEmpty()) {
            VEvent event = Data.listEventsForGroup.get(0);
            
            Manipulation.printEvent(event);
            String name = event.getSummary().getValue();
            if (Data.listLinks.containsKey(name)) {
                ArrayList<String> listURL = Data.listLinks.get(name);

                if (listURL.isEmpty()) {
                    System.err.println("Error: no url for " + name);
                } else {
                    for (String str : listURL) {
                        System.out.println(str);
                    }
                    System.out.println("Open links for this event ? (Y/n)");
                    char c = sc.nextLine().charAt(0);
                    if (c == 'Y' || c == 'y') {
                        // Launch
                        System.out.println("Lancement");
                        if (Desktop.isDesktopSupported()) {
                            try {
                                for (String str : listURL) {
                                    Desktop.getDesktop().browse(new URI(str));
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                            } catch (IOException | URISyntaxException ex) {
                                Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            } else {
                System.err.println("Error: no url for " + name);
            }

        }
    }

    /**
     * Ask for course name and print all links
     *
     * @param sc Scanner
     */
    public static void listLinks(Scanner sc) {
        System.out.print("Which name? ");
        String name = sc.nextLine();
        listLinks(name);
    }

    /**
     * List Links relative to a course name.
     *
     * @param name String
     */
    public static void listLinks(String name) {
        if (name.equals("all")) {
            System.out.println(Data.listLinks.size() + " entries");
            Set cles = Data.listLinks.keySet();
            Iterator it = cles.iterator();
            while (it.hasNext()) {
                String cle = (String) it.next(); // tu peux typer plus finement ici
                ArrayList<String> listTmp = Data.listLinks.get(cle);
                for (String str : listTmp) {
                    System.out.println(cle + "=>" + str);
                }

            }
        } else if (Data.listLinks.containsKey(name)) {
            ArrayList<String> listTmp = Data.listLinks.get(name);
            for (String str : listTmp) {
                System.out.println(str);
            }
        } else {
            System.out.println("No links for " + name);
        }
    }

    /**
     * Ask for name and link, and add in HashMap.
     *
     * @param sc Scanner
     */
    public static void addLink(Scanner sc) {
        System.out.print("Name of the course : ");
        String name = sc.nextLine();
        System.out.print("Link : ");
        String link = sc.nextLine();
        addLink(name, link);
    }

    /**
     * Add a new link in HashMap.
     *
     * @param name String
     * @param link String
     */
    public static void addLink(String name, String link) {
        if (Data.listLinks.containsKey(name)) {
            ArrayList<String> listTmp = Data.listLinks.get(name);
            listTmp.add(link);
            Data.listLinks.replace(name, listTmp);
        } else {
            ArrayList<String> listTmp = new ArrayList<>();
            listTmp.add(link);
            Data.listLinks.put(name, listTmp);
        }
        // Write object
        Data.writeData();
    }

    /**
     * Reset list of links, create a new HashMap.
     */
    public static void reset() {
        Data.listLinks = new HashMap<>();
        Data.writeData();
    }

    /**
     * Print help.
     */
    public static void help() {
        System.out.println("list => Print all events of the weeks");
        System.out.println("listlinks => Print links associated with a course. It will ask a course name, enter 'all' to have all links");
        System.out.println("addlink => Add a new link for a course. It will be interactif with the terminal.");
        System.out.println("reset => Reset the links, create a new empty hashmap.");
        System.out.println("open => Open links for the next course ;)");
        System.out.println("exit => Exit the program.");
    }
}

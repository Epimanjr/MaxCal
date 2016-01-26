package maxcal;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxime
 */
public class Manipulation {

    /**
     * Main method.
     *
     * @param args Generally a path
     */
    public static void main(String[] args) {
        // Get Path
        String path = getPathFromArguments(args);
        // Init 
        Data.initConfig();
        Data.initData();
        Data.initListEvents(path);

        // Launch 
        Shell.launchShell();
    }

    /**
     * Get path, depends on arguments.
     *
     * @param args Arguments
     * @return Path
     */
    public static String getPathFromArguments(String[] args) {
        boolean pathOK = false;
        String path = "";
        // Test argument
        if (args.length == 1) {
            File f = new File(args[0]);
            if (f.exists()) {
                path = args[0];
                pathOK = true;
            } else {
                System.err.println("Error: file doesn't exist.");
            }
        }
        // Loop to list files in ics folder
        if (!pathOK) {
            File f = new File("data/ics");
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println(i + " -> " + files[i].getAbsolutePath());
            }
            Scanner sc = new Scanner(System.in);
            System.out.print("Quel fichier? ");
            int choix = sc.nextInt();
            if (choix < 0 || choix > files.length) {
                System.err.println("Bad choice!");
                System.exit(0);
            }
            path = files[choix].getAbsolutePath();
        }
        return path;
    }

    public static void printEvent(VEvent event) {
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        String dateStart = ft.format(event.getDateStart().getValue());
        String dateEnd = ft.format(event.getDateEnd().getValue());
        System.out.println("\t" + dateStart + " TO " + dateEnd + "\t" + event.getSummary().getValue() + " (" + event.getLocation().getValue() + ")");
    }

    public static ArrayList<String> getDates(ArrayList<VEvent> listEvents) {
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<String> listDates = new ArrayList<>();
        for (VEvent event : listEvents) {
            String date = ft.format(event.getDateStart().getValue());
            if (!listDates.contains(date)) {
                listDates.add(date);
            }
        }
        return listDates;
    }

    public static HashMap<String, ArrayList<VEvent>> convertListToMap(ArrayList<VEvent> listEvents) {
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");

        HashMap<String, ArrayList<VEvent>> map = new HashMap<>();
        for (VEvent event : listEvents) {
            String date = ft.format(event.getDateStart().getValue());
            if (map.containsKey(date)) {
                ArrayList<VEvent> listTmp = map.get(date);
                listTmp.add(event);
                map.replace(date, listTmp);
            } else {
                ArrayList<VEvent> listTmp = new ArrayList<>();
                listTmp.add(event);
                map.put(date, listTmp);
            }
        }
        return map;
    }

    /**
     * Get only events in the future.
     *
     * @param cal ICalendar object read from file
     * @return List of events
     */
    public static ArrayList<VEvent> getEventsFuture(ICalendar cal) {
        // Sort by DateStart
        ArrayList<VEvent> listeTrie = new ArrayList<>();
        listeTrie.add(cal.getEvents().get(0));
        for (int i = 1; i < cal.getEvents().size(); i++) {
            // Try to insert event i
            VEvent current = cal.getEvents().get(i);
            boolean insert = false;
            for (int j = 0; j < listeTrie.size(); j++) {
                int compare = current.getDateStart().getValue().compareTo(listeTrie.get(j).getDateStart().getValue());
                if (compare == -1) {
                    listeTrie.add(j, current);
                    insert = true;
                    break;
                }
            }
            if (!insert) {
                listeTrie.add(current);
            }
        }
        // Get only events future
        ArrayList<VEvent> listeEvents = new ArrayList<>();
        Date dateAujourdhui = new Date(System.currentTimeMillis());
        // Loop for print
        for (VEvent event : listeTrie) {
            int compare = event.getDateStart().getValue().compareTo(dateAujourdhui);
            if (compare == 1) {
                listeEvents.add(event);
            } else {
                int compareEnd = event.getDateEnd().getValue().compareTo(dateAujourdhui);
                if (compareEnd == 1) {
                    listeEvents.add(event);
                }
            }
        }
        return listeEvents;

    }

    /**
     * Get a Calendar from a file.
     *
     * @param path Path of the file.
     * @return ICalendar object
     */
    public static ICalendar getCalendarFromFile(String path) {
        // Get calendar
        System.out.println("Lecture du calendrier dans " + path);
        String str = getFullText(path);
        return Biweekly.parse(str).first();
    }

    /**
     * Get full text from a specific path
     *
     * @param path String
     * @return
     */
    public static String getFullText(String path) {
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            while (br.ready()) {
                line += br.readLine() + "\n";
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MaxCal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MaxCal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return line;
    }
}

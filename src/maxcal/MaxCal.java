/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maxcal;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author max
 */
public class MaxCal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Get path
        String path = (args.length == 0) ? "cal.ics" : args[0];

        File f = new File("ics");
        for(File file : f.listFiles()) {
            showCalendar(file.getAbsolutePath());
            System.out.println("\n----------------------\n");
        }
    }
    
    public static void showCalendar(String path) {
        // Get calendar
        System.out.println("Lecture du calendrier dans " + path);
        String str = getFullText(path);
        ICalendar ical = Biweekly.parse(str).first();

        // Sort by DateStart
        ArrayList<VEvent> listeEvenements = new ArrayList<>();
        listeEvenements.add(ical.getEvents().get(0));
        for (int i = 1; i < ical.getEvents().size(); i++) {
            // Try to insert event i
            VEvent current = ical.getEvents().get(i);
            boolean insert = false;
            for (int j = 0; j < listeEvenements.size(); j++) {
                int compare = current.getDateStart().getValue().compareTo(listeEvenements.get(j).getDateStart().getValue());
                if (compare == -1) {
                    listeEvenements.add(j, current);
                    insert = true;
                    break;
                }
            }
            if (!insert) {
                listeEvenements.add(current);
            }
        }

        Date dateAujourdhui = new Date(System.currentTimeMillis());
        System.out.println(dateAujourdhui + " <-- MAINTENANT");
        // Loop for print
        for (VEvent event : listeEvenements) {
            int compare = event.getDateStart().getValue().compareTo(dateAujourdhui);
            if (compare == 1) {
                System.out.println(event.getDateStart().getValue() + " TO " + event.getDateEnd().getValue() + "-" + event.getSummary().getValue() + " (" + event.getLocation().getValue() + ")");
            }
        }
    }

    public static String getFullText() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file path : ");
        String path = sc.nextLine();
        return getFullText(path);
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

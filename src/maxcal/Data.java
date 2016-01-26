/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maxcal;

import biweekly.component.VEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static maxcal.Manipulation.getCalendarFromFile;
import static maxcal.Manipulation.getEventsFuture;

/**
 *
 * @author maxime
 */
public class Data {

    /**
     * List of links for a course.
     */
    public static HashMap<String, ArrayList<String>> listLinks;

    /**
     * List of future events.
     */
    public static ArrayList<VEvent> listEvents;

    /**
     * List of future events, no bad group.
     */
    public static ArrayList<VEvent> listEventsForGroup;

    /**
     * All config.
     */
    public static HashMap<String, String> config;

    /**
     * Init list of events.
     *
     * @param path Path of ICS file
     */
    public static void initListEvents(String path) {
        listEvents = getEventsFuture(getCalendarFromFile(path));

        listEventsForGroup = new ArrayList<>();
        for (VEvent eventTmp : Data.listEvents) {
            String groupe = Data.config.get("groupe");
            String str = (groupe.equals("G1")) ? "G2" : "G1";
            if (eventTmp.getSummary().getValue().startsWith("CM") || !eventTmp.getSummary().getValue().contains(str)) {
                listEventsForGroup.add(eventTmp);
            }
        }
    }

    /**
     * Init config.
     */
    public static void initConfig() {
        config = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/config.txt"));
            while (br.ready()) {
                String line = br.readLine();
                String[] lines = line.split("=");
                config.put(lines[0].trim(), lines[1].trim());
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Init data.
     */
    public static void initData() {
        listLinks = new HashMap<>();
        File f = new File("data/links.txt");
        if (f.exists()) {
            try {
                // Read file
                BufferedReader br = new BufferedReader(new FileReader(f));
                while(br.ready()) {
                    String line = br.readLine();
                    String[] lines = line.split("=>");
                    if(lines.length == 2) {
                        if(listLinks.containsKey(lines[0])) {
                            ArrayList<String> listeTmp = listLinks.get(lines[0]);
                            listeTmp.add(lines[1]);
                            listLinks.replace(lines[0], listeTmp);
                        } else {
                            ArrayList<String> listeTmp = new ArrayList<>();
                            listeTmp.add(lines[1]);
                            listLinks.put(lines[0], listeTmp);
                        }
                    }
                }
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Error: data file doesn't exist, no data, empty list.");
        }
    }

    public static void writeData() {
        
    }
}

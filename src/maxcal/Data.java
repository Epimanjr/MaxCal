/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maxcal;

import biweekly.component.VEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
    }

    /**
     * Init config.
     */
    public static void initConfig() {
        config = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/config.txt"));
            while(br.ready()) {
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
        File f = new File("data/data.txt");
        if (f.exists()) {
            try {
                // Read file
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/data.txt"));
                Data.listLinks = (HashMap<String, ArrayList<String>>) ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Error: data file doesn't exist, no data, empty list.");
        }
    }
    
    public static void writeData() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/data.txt"));
            oos.writeObject(Data.listLinks);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

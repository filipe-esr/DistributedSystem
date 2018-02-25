package Structures;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class RegistryConfig {
    /**
     * GenRep name entry on the registry.
     */
    public static String genRepNameEntry = "generalRepository";

    /**
     * ConcentrationSite name entry on the registry.
     */
    public static String concentrationSiteNameEntry = "concentrationSite";

    /**
     * ControlSite name entry on the registry.
     */
    public static String controlSiteNameEntry = "controlSite";

    /**
     * Museum name entry on the registry.
     */
    public static String museumNameEntry = "museum";

    /**
     * AssaultParty name entry on the registry.
     */
    public static String assaultParty1NameEntry = "assaultParty1";
    
    /**
     * AssaultParty name entry on the registry.
     */
    public static String assaultParty2NameEntry = "assaultParty2";
    
    /**
     * RegisterHandler name entry on the registry.
     */
    public static String registerHandler = "RegisterHandler";
    /**
     * Bash property of the file.
     */
    private Properties prop;
    /**
     * Constructor that receives the file with the configurations.
     * @param filename path for the configuration file
     */
    public RegistryConfig(String filename) {
        prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(filename);
            prop.load(in);
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(RegistryConfig.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(RegistryConfig.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
    }
    /**
     * Loads a parameter from the bash.
     * @param param parameter name
     * @return parameter value
     */
    public String loadParam(String param) {
        return prop.getProperty(param);
    }
    
    /** 
     * Loads the parameter REGISTER_HOST from the configuration file.
     * @return parameter value
     */
    public String registryHost() {
        return loadParam("REGISTER_HOST");
    }
    /** 
     * Loads the parameter REGISTER_PORT from the configuration file.
     * @return parameter value
     */
    public int registryPort() {
        return Integer.parseInt(loadParam("REGISTER_PORT"));
    }
    /** 
     * Loads the parameter REGISTER_OBJECT_PORT from the configuration file.
     * @return parameter value
     */
    public int objectPort() {
        return Integer.parseInt(loadParam("REGISTER_OBJECT_PORT"));
    }
    /** 
     * Loads the parameter GENREP_PORT from the configuration file.
     * @return parameter value
     */
    public int genRepPort() {
        return Integer.parseInt(loadParam("GENREP_PORT"));
    }
    /** 
     * Loads the parameter CONCENTRATIONSITE_PORT from the configuration file.
     * @return parameter value
     */
    public int concentrationSitePort() {
        return Integer.parseInt(loadParam("CONCENTRATIONSITE_PORT"));
    }
    /** 
     * Loads the parameter CONTROLSITE_PORT from the configuration file.
     * @return parameter value
     */
    public int controlSitePort() {
        return Integer.parseInt(loadParam("CONTROLSITE_PORT"));
    }
    /** 
     * Loads the parameter MUSEUM_PORT from the configuration file.
     * @return parameter value
     */
    public int museumPort() {
        return Integer.parseInt(loadParam("MUSEUM_PORT"));
    }
    /** 
     * Loads the parameter ASSAULTPARTY_PORT from the configuration file.
     * @return parameter value
     */
    public int assaultParty1Port() {
        return Integer.parseInt(loadParam("ASSAULTPARTY1_PORT"));
    }
    /** 
     * Loads the parameter ASSAULTPARTY_PORT from the configuration file.
     * @return parameter value
     */
    public int assaultParty2Port() {
        return Integer.parseInt(loadParam("ASSAULTPARTY2_PORT"));
    }
}

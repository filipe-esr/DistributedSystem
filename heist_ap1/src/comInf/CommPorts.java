/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package comInf;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class CommPorts {
    /*
    Somos o Grupo 04
    Ports:  223GX, X € [0,9] - G numero do grupo
    PC's:   l040101-wsYY.ua.pt - YY € [01,12]
    login:  sd030g - g numero do grupo
    pass:   qwerty -> sockets
    
    server : l040101-ws01.ua.pt/sd0304
    ports : 22340 - 22349*/
    
    /**
     * Variable that holds the address for the genRep server.
     */
    //public final static String genRepServerName = "l040101-ws01.ua.pt";
    public final static String genRepServerName = "localhost";

    /**
     * Variable that holds the port number for the genRep server.
     */
    public final static int genRepServerPort = 22340;
    
    /**
     * Variable that holds the address for the museum server.
     */
    //public final static String museumServerName = "l040101-ws03.ua.pt";
    public final static String museumServerName = "localhost";

    /**
     * Variable that holds the port number for the museum server.
     */
    public final static int museumServerPort = 22341;
    
    /**
     * Variable that holds the address for the concentrationSite server.
     */
    //public final static String concentrationSiteServerName = "l040101-ws04.ua.pt";
    public final static String concentrationSiteServerName = "localhost";
    /**
     * Variable that Sholds the port number for the concentrationSite server.
     */
    public final static int concentrationSiteServerPort = 22342;
    
    /**
     * Variable that holds the address for the controlSite server.
     */
    //public final static String controlSiteServerName = "l040101-ws05.ua.pt";
    public final static String controlSiteServerName = "localhost";
    /**
     * Variable that holds the port number for the controlSite server.
     */
    public final static int controlSiteServerPort = 22343;
    
    /**
     * Variable that holds the address for the assaultParty server.
     */
    //public final static String assaultPartyServerName = "l040101-ws07.ua.pt";
    public final static String[] assaultPartyServerName = {"localhost", "localhost"};
    /**
     * Variable that holds the port number for the assaultParty server.
     */
    public final static int[] assaultPartyServerPort = {22344,22345};
    
    /**
     * Variable that holds the timeout value for the server sockets.
     */
    public final static int socketTimeout = 500;
}

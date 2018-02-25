/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.ControlSite;

import genclass.GenericIO;
import comInf.CommPorts;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class ControlSiteExec {
    /**
     * ControlSiteExec main function to start the ControlSite service.
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        ServerCom scon, sconi;
        ClientProxy cliProxy;

        scon = new ServerCom(CommPorts.controlSiteServerPort);
        scon.start();
        MControlSite controlSite = new MControlSite();
        ControlSiteItf controlSiteItf = new ControlSiteItf(controlSite);
        GenericIO.writelnString("ControlSite service has started!");
        GenericIO.writelnString("Server is listening.");

        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(scon, sconi, controlSiteItf);
            cliProxy.start();
        }
    }
}

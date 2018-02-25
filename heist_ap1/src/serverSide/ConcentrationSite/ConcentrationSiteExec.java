/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.ConcentrationSite;

import comInf.GlobalInfo;
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
public class ConcentrationSiteExec {
    /**
     * ConcentrationSiteExec main function to start the ConcentrationSite service.
     * 
     * @param args no need
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        ServerCom scon, sconi;
        ClientProxy cliProxy;

        scon = new ServerCom(CommPorts.concentrationSiteServerPort);
        scon.start();
        MConcentrationSite concentrationSite = new MConcentrationSite(GlobalInfo.nParties);
        ConcentrationSiteItf concentrationSiteItf = new ConcentrationSiteItf(concentrationSite);
        GenericIO.writelnString("ConcentrationSite service has started!");
        GenericIO.writelnString("Server is listening.");

        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(scon, sconi, concentrationSiteItf);
            cliProxy.start();
        }
    }
}

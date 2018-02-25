/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.Museum;

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
public class MuseumExec {
    /**
     * MuseumExec main function to start the Museum service.
     * 
     * @param args no need
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        ServerCom scon, sconi;
        ClientProxy cliProxy;

        scon = new ServerCom(CommPorts.museumServerPort);
        scon.start();
        MMuseum museum = new MMuseum(GlobalInfo.nRooms);
        MuseumItf museumItf = new MuseumItf(museum);
        GenericIO.writelnString("Museum service has started!");
        GenericIO.writelnString("Server is listening.");

        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(scon, sconi, museumItf);
            cliProxy.start();
        }
    }
}

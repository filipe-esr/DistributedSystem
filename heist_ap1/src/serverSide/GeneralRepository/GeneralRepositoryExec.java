/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.GeneralRepository;

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
public class GeneralRepositoryExec {
    /**
     * GeneralRepositoryExec main function to start the GeneralRepository service.
     * 
     * @param args no need
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        ServerCom scon, sconi;
        ClientProxy cliProxy;

        scon = new ServerCom(CommPorts.genRepServerPort);
        scon.start();
        MGeneralRepository genRep = new MGeneralRepository(GlobalInfo.nRooms, GlobalInfo.nThieves, GlobalInfo.nParties);
        GeneralRepositoryItf genRepItf = new GeneralRepositoryItf(genRep);
        GenericIO.writelnString("GeneralRepository service has started!");
        GenericIO.writelnString("Server is listening.");

        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(scon, sconi, genRepItf);
            cliProxy.start();
        }
    }
}

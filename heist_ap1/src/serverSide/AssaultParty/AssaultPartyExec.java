/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package serverSide.AssaultParty;

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
public class AssaultPartyExec {
    /**
     * AssaultPartyExec main function to start the AssaultParty service.
     * 
     * @param args no need
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        ServerCom scon, sconi;                              // canais de comunicacao
        ClientProxy cliProxy;                               // thread agente prestador do serviço
        
        /* estabelecimento do servico */
        scon = new ServerCom(CommPorts.assaultPartyServerPort[1]);    // criação do canal de escuta e sua associacao
        scon.start();                                       // com o endereço publico
        MAssaultParty assaultParty = new MAssaultParty(GlobalInfo.thievesPerParty, GlobalInfo.maxDisp);

        AssaultPartyItf assaultPartyItf = new AssaultPartyItf(assaultParty);
        GenericIO.writelnString("AssaultParty service has started!");
        GenericIO.writelnString("Server is listening.");

        /* processamento de pedidos */
        while (true) {
            sconi = scon.accept();                         // entrada em processo de escuta
            cliProxy = new ClientProxy(scon, sconi, assaultPartyItf);     // lançamento do agente prestador do serviço
            cliProxy.start();
        }
    }
}

package ClientSide.MasterThief;
import Interfaces.ItfMTAssaultParty;
import Interfaces.ItfMTConcentrationSite;
import Interfaces.ItfMTControlSite;
import Interfaces.ItfMTMuseum;
import Interfaces.ItfGenRep;
import Structures.GlobalInfo;
import java.util.Scanner;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class TMasterThiefClient {
  
    public static void main(String[] args) throws RemoteException{
      	
      	String rmiRegHostName;                                // nome do sistema onde está localizado o serviço de registos RMI
        int rmiRegPortNumb;                                   // port de escuta do serviço
        Scanner sc = new Scanner(System.in);
      	/* obtenção da localização do serviço de registo RMI */
        /*RegistryConfig rc = new RegistryConfig("config.ini");
        rmiRegHostName = rc.registryHost();
        rmiRegPortNumb = rc.registryPort();*/
        System.out.println("Nome do nó de processamento onde está localizado o serviço de registo? ");
        rmiRegHostName = sc.nextLine();
        System.out.println("Número do port de escuta do serviço de registo? ");
        rmiRegPortNumb = sc.nextInt();
      	
      	/* instanciação e instalação do gestor de segurança */
        /*System.setProperty("java.security.policy","java.policy");
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        */
      	/* Inicialização das Interfaces */
      	ItfMTControlSite controlSiteItf = null;
      	ItfMTMuseum museumItf = null;
      	ItfMTConcentrationSite concentrationSiteItf = null;
        ItfMTAssaultParty assaultParty1Itf = null;
        ItfMTAssaultParty assaultParty2Itf = null;
        //MAssaultParty assaultParty2 = null;
        //MAssaultParty[] assaultPartyItf = null;
      	ItfGenRep genRep = null;
      	
      	/* localização por nome do objecto remoto no serviço de registos RMI */
        try
        { Registry registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
            controlSiteItf = (ItfMTControlSite) registry.lookup ("controlSite");
            museumItf = (ItfMTMuseum) registry.lookup("Museum");
            concentrationSiteItf = (ItfMTConcentrationSite) registry.lookup("concentrationSite");
            assaultParty1Itf = (ItfMTAssaultParty) registry.lookup("assaultParty1");
            assaultParty2Itf = (ItfMTAssaultParty) registry.lookup("assaultParty2");
            genRep = (ItfGenRep) registry.lookup("generalRepository");
        }
        catch (RemoteException | NotBoundException e)
        { System.out.println("Excepção na localização dos servers: " + e.getMessage () + "!");
            e.printStackTrace ();
            System.exit (1);
        }
        
      	//assaultPartyItf[0] = assaultParty1;
        //assaultPartyItf[1] = assaultParty2;
      	
        /* criação dos threads cliente */
      	TMasterThief mt = new TMasterThief(controlSiteItf, assaultParty1Itf,assaultParty2Itf, museumItf, concentrationSiteItf, 
                                            genRep, GlobalInfo.nRooms, GlobalInfo.nThieves);
                                            /* Quando mudo para genRep passa a dar erro na assaultPartyItf */
                                            
        /* arranque da simulação */
        mt.start();
        System.out.println("O cliente MasterThief começou a trabalhar");

        /* aguardar o fim da simulação */
        System.out.println();
        {
            try {
                mt.join();
            } catch (InterruptedException e) {
            }
            System.out.println("O cliente MasterThief terminou.");
        }
        System.out.println();	
        
        try{ 
            genRep.terminate();
            controlSiteItf.terminate();
            museumItf.terminate();
            concentrationSiteItf.terminate();
            assaultParty1Itf.terminate();
            assaultParty2Itf.terminate();
        }catch (RemoteException e)
        { 
            System.out.println("Excepção na invocação remota do método terminate: " + e.getMessage () + "!");
            e.printStackTrace ();
            System.exit (1);
        }
        System.out.println("O servidor foi mandado encerrar.");
        System.out.println();
    }
}



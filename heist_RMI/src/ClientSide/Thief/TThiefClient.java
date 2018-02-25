package ClientSide.Thief;

import Interfaces.ItfTAssaultParty;
import Interfaces.ItfTConcentrationSite;
import Interfaces.ItfTControlSite;
import Interfaces.ItfTMuseum;
import Interfaces.ItfGenRep;
import Structures.GlobalInfo;
import java.util.Scanner;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class TThiefClient {
  
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
        //System.setProperty("java.security.policy","java.policy");
        /*if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        */
      	/* Inicialização das Interfaces */
      	ItfTControlSite controlSiteItf = null;
      	ItfTMuseum museumItf = null;
      	ItfTConcentrationSite concentrationSiteItf = null;
        ItfTAssaultParty assaultParty1Itf = null;
        ItfTAssaultParty assaultParty2Itf = null;
        //MAssaultParty assaultParty2 = null;
        //MAssaultParty[] assaultPartyItf = null;
      	ItfGenRep genRepItf = null;
      	
      	/* localização por nome do objecto remoto no serviço de registos RMI */
        try
        { Registry registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
            controlSiteItf = (ItfTControlSite) registry.lookup ("controlSite");
            museumItf = (ItfTMuseum) registry.lookup("Museum");
            concentrationSiteItf = (ItfTConcentrationSite) registry.lookup("concentrationSite");
            assaultParty1Itf = (ItfTAssaultParty) registry.lookup("assaultParty1");
            assaultParty2Itf = (ItfTAssaultParty) registry.lookup("assaultParty2");
            genRepItf = (ItfGenRep) registry.lookup("generalRepository");
        }
        catch (RemoteException | NotBoundException e)
        { System.out.println("Excepção na localização dos servers: " + e.getMessage () + "!");
            e.printStackTrace ();
            System.exit (1);
        }
        
      	//assaultPartyItf[0] = assaultParty1;
        //assaultPartyItf[1] = assaultParty2;
      	
        /* criação dos threads cliente */
        ArrayList<TThief> thief = new ArrayList<>(GlobalInfo.nThieves);
        int k = -1;
        for(int i = 0; i < GlobalInfo.nThieves;i++){
            if (i % GlobalInfo.thievesPerParty == 0)
                k+=1;
            if(k == 0)
                thief.add(new TThief(i,GlobalInfo.maxDisp,k,controlSiteItf,museumItf,assaultParty1Itf,concentrationSiteItf,genRepItf,GlobalInfo.nThieves));
            else if(k == 1) 
                thief.add(new TThief(i,GlobalInfo.maxDisp,k,controlSiteItf,museumItf,assaultParty2Itf,concentrationSiteItf,genRepItf,GlobalInfo.nThieves));/* Quando mudo para genRep passa a dar erro na assaultPartyItf */
        }                         
        /* arranque da simulação */
        for (TThief t : thief)
            t.start();
        
        System.out.println("Client (Thief) operations started");
        for (TThief t : thief) {
            try {
                t.join ();
            } catch (InterruptedException e) {}
        }
        System.out.println("Client (Thief) operations done!");
    }
}



package ServerSide.AssaultParty;

import Structures.GlobalInfo;
import java.util.Scanner;

import Interfaces.ItfAssaultParty;
import Interfaces.ItfGenRep;
import Interfaces.Register;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Filipe Rocha - 67432 and Luis Gameiro - 67989
 * 
 * Project for the course MIECT - SD - P3 G04
 */
public class MAssaultPartyClient {
    private static boolean end = false;
    /**
     * MAssaultPartyClient main function to start the AssaultParty service.
     * 
     * @param args no need
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        
        /* get location of the generic registry service */

     String rmiRegHostName;
     int rmiRegPortNumb;
     Scanner sc = new Scanner(System.in);
     //int listeningPort1 = 22345;
     //int listeningPort2 = 22346;
     int listeningPort1;
     int listeningPort2;
     String nameEntry1 = "assaultParty1";
     String nameEntry2 = "assaultParty2";

     System.out.println("Nome do nó de processamento onde está localizado o serviço de registo? ");
     rmiRegHostName = sc.nextLine();
     System.out.println("Número do port de escuta do serviço de registo? ");
     rmiRegPortNumb = sc.nextInt();
     System.out.println("Número do port para este serviço AP1: ");
     listeningPort1 = sc.nextInt();
     System.out.println("Número do port para este serviço AP2: ");
     listeningPort2 = sc.nextInt();
     
     /* instanciação e instalação do gestor de segurança */
        //System.setProperty("java.security.policy","java.policy");

    if (System.getSecurityManager() == null)
        System.setSecurityManager(new SecurityManager());

     //Localização do registo central
    Registry registry = null;
    try {
            registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
        } catch (RemoteException ex) {
            System.out.println("Wrong registry location!!!");
            System.exit (1);
        }
        System.out.println("RMI registry created!");
        
    /*Localização do registo do generalRepository*/
    ItfGenRep genRepInt = null;    
    try {
            genRepInt = (ItfGenRep) registry.lookup("generalRepository");
        } catch (Exception e) {
            System.out.println("Excepção no lookup do repositorio geral: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    
    /*Geração do stub assaultParty1*/
    MAssaultParty assaultParty1 = new MAssaultParty(GlobalInfo.thievesPerParty,GlobalInfo.maxDisp, genRepInt, GlobalInfo.nThieves);
    ItfAssaultParty ap1Int = null; 
    try {
          ap1Int = (ItfAssaultParty) UnicastRemoteObject.exportObject(assaultParty1, listeningPort1);
      } catch (RemoteException e) {
          System.out.println("Excepção na geração do stub para o assaultParty1: " + e.getMessage());
          e.printStackTrace();
          System.exit(1);
      }
      System.out.println("O stub para a assaultParty1 foi gerado!");  
      
      /*Registo do serviço controlSite no RMI*/
      Register register = null;
      try {
            register = (Register) registry.lookup("RegisterHandler");
        } catch (RemoteException | NotBoundException ex) {
            System.out.println("Wrong register location!");
            System.exit (1);
        }
      
      try {
            register.bind(nameEntry1, ap1Int);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo da assaultParty1: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("A assaultParty1 já está registado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("A assaultParty1 foi registada!");
        
        /*Geração do stub assaultParty1*/
    MAssaultParty assaultParty2 = new MAssaultParty(GlobalInfo.thievesPerParty,GlobalInfo.maxDisp, genRepInt, GlobalInfo.nThieves);
    ItfAssaultParty ap2Int = null; 
    try {
          ap2Int = (ItfAssaultParty) UnicastRemoteObject.exportObject(assaultParty2, listeningPort2);
      } catch (RemoteException e) {
          System.out.println("Excepção na geração do stub para a assaultParty2: " + e.getMessage());
          e.printStackTrace();
          System.exit(1);
      }
      System.out.println("O stub para a assaultParty2 foi gerado!");
      
    try {
          register.bind(nameEntry2, ap2Int);
      } catch (RemoteException e) {
          System.out.println("Excepção no registo da assaultParty2: " + e.getMessage());
          e.printStackTrace();
          System.exit(1);
      } catch (Exception e) {
          System.out.println("A assaultParty2 já está registado: " + e.getMessage());
          e.printStackTrace();
          System.exit(1);
      }
      System.out.println("A assaultParty2 foi registada!");
    
    try{ 
        while (!end) synchronized (Class.forName ("ServerSide.AssaultParty.MAssaultPartyClient")){ 
            try{ 
                (Class.forName ("ServerSide.AssaultParty.MAssaultPartyClient")).wait();
            }catch (InterruptedException e){ 
                System.out.println("O programa principal foi interrompido!");
            }
            }
        }catch (ClassNotFoundException e){ 
            System.out.println("O tipo de dados AssaultParty não foi encontrado (bloqueio)!");
            e.printStackTrace ();
            System.exit (1);
        }

        /* shutdown do servidor */
        try{ 
            register.unbind(nameEntry1);
            register.unbind(nameEntry2);
        }catch (RemoteException e){ 
            System.out.println("Excepção no registo do AssaultParty: " + e.getMessage());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e){ 
            System.out.println("O AssaultParty não está registada: " + e.getMessage());
            e.printStackTrace ();
            System.exit (1);
        }
        System.out.println("O AssaultParty vai ser encerrado!");
        System.exit (1);
    }

    /**
    *  Encerramento de operações.
    */
    public static void shutdown ()
    {
        end = true;
        try{ 
            synchronized (Class.forName ("ServerSide.AssaultParty.MAssaultPartyClient")){ 
                (Class.forName ("ServerSide.AssaultParty.MAssaultPartyClient")).notify();
            }
        }catch (ClassNotFoundException e){ 
            System.out.println("O tipo de dados AssaultParty não foi encontrado (acordar)!");
            e.printStackTrace ();
            System.exit (1);
       }
    }
}



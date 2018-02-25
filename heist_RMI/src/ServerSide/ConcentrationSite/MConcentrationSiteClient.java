package ServerSide.ConcentrationSite;


import Structures.GlobalInfo;
import java.util.Scanner;

import Interfaces.ItfConcentrationSite;
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
public class MConcentrationSiteClient {
    private static boolean end = false;
    /**
     * MConcentrationSiteClient main function to start the ConcentrationSite service.
     * 
     * @param args no need
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        
        /* get location of the generic registry service */

     String rmiRegHostName;
     int rmiRegPortNumb;
     Scanner sc = new Scanner(System.in);
     //int listeningPort = 22344;
     int listeningPort;
     String nameEntry = "concentrationSite";

     System.out.println("Nome do nó de processamento onde está localizado o serviço de registo? ");
     rmiRegHostName = sc.nextLine();
     System.out.println("Número do port de escuta do serviço de registo? ");
     rmiRegPortNumb = sc.nextInt();
     System.out.println("Número do port para este serviço: ");
     listeningPort = sc.nextInt();
     
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
    
    /*Geração do stub concentrationSite*/
    MConcentrationSite concentrationSite = new MConcentrationSite(GlobalInfo.nParties, GlobalInfo.nThieves);
    ItfConcentrationSite csInt = null; 
    try {
          csInt = (ItfConcentrationSite) UnicastRemoteObject.exportObject(concentrationSite, listeningPort);
      } catch (RemoteException e) {
          System.out.println("Excepção na geração do stub para o concentrationSite: " + e.getMessage());
          e.printStackTrace();
          System.exit(1);
      }
      System.out.println("O stub para a concentrationSite foi gerado!");  
      
      /*Registo do serviço ConcentrationSite no RMI*/
      Register register = null;
      try {
            register = (Register) registry.lookup("RegisterHandler");
        } catch (RemoteException | NotBoundException ex) {
            System.out.println("Wrong register location!");
            System.exit (1);
        }
      
      try {
            register.bind(nameEntry, csInt);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo do concentrationSite: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("O concentrationSite já está registado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("O concentrationSite foi registado!");
    
    try{ 
        while (!end) synchronized (Class.forName ("ServerSide.ConcentrationSite.MConcentrationSiteClient")){ 
            try{ 
                (Class.forName ("ServerSide.ConcentrationSite.MConcentrationSiteClient")).wait();
            }catch (InterruptedException e){ 
                System.out.println("O programa principal foi interrompido!");
            }
            }
        }catch (ClassNotFoundException e){ 
            System.out.println("O tipo de dados concentrationSite não foi encontrado (bloqueio)!");
            e.printStackTrace ();
            System.exit (1);
        }

        /* shutdown do servidor */
        try{ 
            register.unbind (nameEntry);
        }catch (RemoteException e){ 
            System.out.println("Excepção no registo do concentrationSite: " + e.getMessage());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e){ 
            System.out.println("O concentrationSite não está registada: " + e.getMessage());
            e.printStackTrace ();
            System.exit (1);
        }
        System.out.println("O concentrationSite vai ser encerrado!");
        System.exit (1);
    }

    /**
    *  Encerramento de operações.
    */
    public static void shutdown ()
    {
        end = true;
        try{ 
            synchronized (Class.forName ("ServerSide.ConcentrationSite.MConcentrationSiteClient")){ 
                (Class.forName ("ServerSide.ConcentrationSite.MConcentrationSiteClient")).notify();
            }
        }catch (ClassNotFoundException e){ 
            System.out.println("O tipo de dados concentrationSite não foi encontrado (acordar)!");
            e.printStackTrace ();
            System.exit (1);
       }
    }
}



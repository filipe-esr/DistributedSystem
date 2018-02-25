/**
 * heist_tp2 is group of functions that simulate a heist to a museum to work as a case of study based on concurrency and sockets.
 */
package clientSide.MasterThief;

import clientSide.ClientCom;
import comInf.GlobalInfo;
import comInf.Message;
import comInf.CommPorts;
import genclass.GenericIO;
/**
 * Thread that controls the workflow of the Thief threads.
 * 
 *  @author Filipe Rocha - 67432 <p>Luis Gameiro - 67989</p>
 * 
 *  <p>Project for the course MIECT - SD - P3 G04</p>
 */
public class TMasterThief extends Thread {
    private String currentState;
    private boolean[] filledRooms;  //Array that indicates for each room true if it has canvas
    private int[] distRooms;
    private boolean canvasFlag;     //True if there is canvas in the museum
    private int[] partiesInRoom;    //Position indicates the party number and the value of the positon indicates the room
    private int[] partyBusy;
    private int numThieves;
    private boolean partyIsNeeded;
    public boolean change = false;
    
    private String controlSiteName = CommPorts.controlSiteServerName;
    private int controlSitePort = CommPorts.controlSiteServerPort;
    private String[] assaultPartyName = CommPorts.assaultPartyServerName;
    private int[] assaultPartyPort = CommPorts.assaultPartyServerPort;
    private String museumName = CommPorts.museumServerName;
    private int museumPort = CommPorts.museumServerPort;
    private String concentrationSiteName = CommPorts.concentrationSiteServerName;
    private int concentrationSitePort = CommPorts.concentrationSiteServerPort;
    private String logName = CommPorts.genRepServerName;
    private int logPort = CommPorts.genRepServerPort;
    
    /**
     * 
     * @param rooms number of rooms in the museum.
     * @param numThieves total number of thieves available for the heist.
     * @param nParties number of parties.
     */
    public TMasterThief(int rooms, int numThieves, int nParties){
        partiesInRoom = new int[nParties];
        partyBusy = new int[nParties];
        for(int i = 0; i < nParties; i++){
             partiesInRoom[i] = -1;
             this.partyBusy[i] = 3;
        }
        this.numThieves = numThieves;
        canvasFlag = true; 
        currentState = "PlanningTheHeist";
        LOGsetMTState(1000);
        filledRooms = new boolean[rooms];
        distRooms = new int[rooms];
        for(int i = 0; i < rooms; i++){
            this.filledRooms[i] = true;
            this.distRooms[i] = 0;
        }
        partyIsNeeded = false;
    }
    
    /**
     * Executes the life cycle of the Master Thief thread. 
     * 5 states: PlanningTheHeist, DecidingWhatToDo, AssemblingAGroup, WaitingForArrival and PresentingTheReport.
     * 
     */
    @Override
    public void run() {
        try{
            int partyFree = -1;
            int numThievesWaiting;
            boolean go = true;
            while(go){
                switch(currentState){
                    case "PlanningTheHeist":
                        LOGprint("First");
                        LOGprint("Change");
                        distRooms = museumGetDist();
                        currentState = controlSiteStartOperations();
                        LOGsetMTState(2000);
                        change = true;
                        GenericIO.writelnString("MT started operations!");
                        break;
                    case "DecidingWhatToDo":
                        boolean temp = false;
                        for(int i=0;i<filledRooms.length;i++){
                            if(filledRooms[i] == true)
                                temp = true;
                        }
                        canvasFlag = temp;
                        
                        partyFree = -1;
                        for(int i = 0; i < partyBusy.length; i++){
                            if(partyBusy[i] == 3){
                                partyFree = i;
                                break;
                            }
                        }
                        
                        numThievesWaiting = 0;
                        for(int i = 0; i < partyBusy.length;i++){
                            numThievesWaiting += partyBusy[i];
                        }
                        
                        if(!canvasFlag){
                            if(numThievesWaiting == numThieves)
                                currentState = controlSiteSumUpResults();
                            else{
                                currentState = "WaitingForArrival";
                                LOGsetMTState(4000);
                                change = true;
                                partyFree = controlSiteTakeARest();
                            }
                        }else if(partyFree != -1){
                            int room=-1;
                            boolean temporaryVariable;
                            for(int x=0;x<filledRooms.length;x++){  //Check all rooms
                                temporaryVariable = false;
                                if(filledRooms[x]){ //Any rooms available?
                                    for(int j = 0; j < partiesInRoom.length; j++){  //Check all parties in work
                                        if(partiesInRoom[j] == x){  //Checks if any part in room
                                            temporaryVariable = true;
                                            break;
                                        }
                                    }
                                    if(!temporaryVariable){ //If false no party in room
                                        room = x;    
                                        break;
                                    }
                                }
                            }
                            if(room == -1){
                                for(int a = 0; a < filledRooms.length; a++)
                                    if(filledRooms[a])
                                        room = a;
                            }
                            
                            currentState = "AssemblingAGroup";
                            LOGsetMTState(3000);
                            partiesInRoom[partyFree] = room;
                            LOGsetPartyRoom(partyFree, room);
                            change = true;
                            partyBusy[partyFree] -= 3;
                            prepareAssaultParty(partyFree, room, distRooms[room]);
                            partyIsNeeded = true;
                            concentrationSitePartyNeeded(partyFree,partyIsNeeded);
                        }else{
                            currentState = "WaitingForArrival";
                            LOGsetMTState(4000);
                            change = true;
                            partyFree = controlSiteTakeARest();
                        }
                        break;
                    case "AssemblingAGroup":
                        sendAssaultParty(partyFree);
                        partyIsNeeded = false;
                        concentrationSitePartyNeeded(partyFree,partyIsNeeded);
                        currentState = "DecidingWhatToDo";
                        LOGsetMTState(2000);
                        change = true;
                        break;
                    case "WaitingForArrival":
                        boolean var = true;
                        var = controlSiteCollectCanvas();
                        if(var)
                            LOGaddCanvasTotal();
                        else
                            filledRooms[partiesInRoom[partyFree]] = var;
                        currentState = "DecidingWhatToDo";
                        LOGsetMTState(2000);
                        change = true;
                        partyBusy[partyFree]+=1;
                        break;
                    case "PresentingTheReport":
                        sleep(20);
                        LOGsetMTState(5000);
                        LOGprint("Final");
                        concentrationSiteStopT();
                        stopAll();
                        go = false;
                        break;
                }
                if(change){
                    LOGprint("Change");
                    change = false;
                }
            }
        }catch(Exception e){
        }
    }
    
    /**
     * Sets the MasterThief state in the log.
     * 
     * @param state 
     */
    private void LOGsetMTState(int state){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())    // aguarda ligação
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETMTSTATE, state);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
      }
    
    /**
     * Prints in the log.
     * 
     * @param txt 
     */
    private void LOGprint(String txt){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        if(txt.equals("First"))
            outMessage = new Message (Message.REQPRINTFIRST);
        else if(txt.equals("Change"))
            outMessage = new Message (Message.REQPRINTCHANGE);
        else if(txt.equals("Final"))
            outMessage = new Message (Message.REQPRINTALL);
        else
            outMessage = new Message(Message.REQSTOPT);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
    
    /**
     * Sets party in the room log.
     * 
     * @param party
     * @param room 
     */
    private void LOGsetPartyRoom(int party, int room){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSETPARTYROOM, party, room);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
    
    /**
     * Adds canvas to the total.
     */
    private void LOGaddCanvasTotal(){
        ClientCom con = new ClientCom (logName, logPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQADDCANVASTOTAL);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
    
    /**
     * Gets the array distance to the museum.
     * 
     * @return 
     */
    private int[] museumGetDist(){
        ClientCom con = new ClientCom (museumName, museumPort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQDIST);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPDIST))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getDistRooms();
    }
    
    /**
     * Starts operations on the controlSite.
     * 
     * @return 
     */
    private String controlSiteStartOperations(){
        ClientCom con = new ClientCom (controlSiteName, controlSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSTARTOP);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPSTARTOP))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getMTState();
    }
    
    /**
     * Sums up the results in the controlSite.
     * 
     * @return 
     */
    private String controlSiteSumUpResults(){
        ClientCom con = new ClientCom (controlSiteName, controlSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSUMUPRESULTS);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPSUMUPRESULTS))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getMTState();
    }
    
    /**
     * Calls take a rest.
     * 
     * @return 
     */
    private int controlSiteTakeARest(){
        ClientCom con = new ClientCom (controlSiteName, controlSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQTAKEREST);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPTAKEREST))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getPartyFree();
    }
    
    /**
     * Prepares assault party.
     * 
     * @param party
     * @param room
     * @param distRoom 
     */
    private void prepareAssaultParty(int party, int room, int distRoom){
        ClientCom con = new ClientCom (assaultPartyName[party], assaultPartyPort[party]);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQPREPAREASSAULTPARTY, room, distRoom);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
    
    /**
     * Changes value party needed in the concentration site.
     * 
     * @param party
     * @param status 
     */
    private void concentrationSitePartyNeeded(int party, boolean status){
        ClientCom con = new ClientCom (concentrationSiteName, concentrationSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQPARTYNEEDED, party, status);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
    
    /**
     * Sends assault party.
     * 
     * @param party 
     */
    private void sendAssaultParty(int party){
        ClientCom con = new ClientCom (assaultPartyName[party], assaultPartyPort[party]);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSENDASSAULTPARTY);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
    
    /**
     * Collect a canvas in the control site.
     * 
     * @return 
     */
    private boolean controlSiteCollectCanvas(){
        ClientCom con = new ClientCom (controlSiteName, controlSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQCOLLECTCANVAS);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.RESPCOLLECTCANVAS))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        return inMessage.getCollectCanvasValue();
    }
    
    /**
     * Stops the threads thieves.
     */
    private void concentrationSiteStopT(){
        ClientCom con = new ClientCom (concentrationSiteName, concentrationSitePort);
        Message inMessage, outMessage;

        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.REQSTOPT);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
    
    /**
     * Stops the services.
     */
    private void stopAll(){
        //ControlSite
        ClientCom con = new ClientCom (controlSiteName, controlSitePort);
        Message inMessage, outMessage;
        
        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.END);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();

        //AssaultParties
        for (int i = 0; i < GlobalInfo.nParties; i++){
        con = new ClientCom (assaultPartyName[i], assaultPartyPort[i]);
        
        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.END);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        }
        
        //Museum
        con = new ClientCom (museumName, museumPort);
        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.END);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
        
        //ConcentrationSite
        con = new ClientCom (concentrationSiteName, concentrationSitePort);
        while (!con.open ())
        { try
          { sleep ((long) (10));
          }
          catch (InterruptedException e) {}
        }
        outMessage = new Message (Message.END);
        con.writeObject (outMessage);
        inMessage = (Message) con.readObject ();
        if ((inMessage.getType () != Message.ACK))
           { GenericIO.writelnString ("Thread " + getName () + ": Invalid Type!");
             GenericIO.writelnString (inMessage.toString ());
             System.exit (1);
           }
        con.close ();
    }
}

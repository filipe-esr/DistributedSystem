package ClientSide.MasterThief;

import Interfaces.ItfMTAssaultParty;
import Interfaces.ItfMTMuseum;
import Interfaces.ItfMTConcentrationSite;
import Interfaces.ItfMTControlSite;
import Interfaces.ItfGenRep;
import Structures.VectorClock;
import java.rmi.RemoteException;
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
    public boolean change = false;
    private ItfMTControlSite MTControlSite;
    private ItfMTAssaultParty[] MTAssaultParty;
    private ItfMTMuseum MTMuseum;
    private ItfMTConcentrationSite MTConcentrationSite;
    public ItfGenRep log;
    private VectorClock vc;
    private Object[] obj;
    
    /**
     * Initializes the thread Master Thief.
     * 
     * @param masterThiefCS interface controlSite to access
     * @param masterThiefAP1 interface assaultParty1 to access
     * @param masterThiefAP2 interface assaultParty2 to access
     * @param masterThiefMuseum interface museum to access
     * @param masterThiefConcentrationSite interface ConcentrationSite to access
     * @param generalRepository interface where changes will be documented in a log
     * @param rooms number of rooms in the museum
     * @param numThieves total number of thieves available for the heist
     * @throws RemoteException 
     */
    public TMasterThief(ItfMTControlSite masterThiefCS,ItfMTAssaultParty masterThiefAP1, ItfMTAssaultParty masterThiefAP2,ItfMTMuseum masterThiefMuseum,ItfMTConcentrationSite masterThiefConcentrationSite,ItfGenRep generalRepository ,int rooms, int numThieves) throws RemoteException{
      vc = new VectorClock(numThieves+1,0);  
      obj = new Object[2];
      this.MTConcentrationSite = masterThiefConcentrationSite;
        this.MTControlSite = masterThiefCS;
        this.log = generalRepository;
        partiesInRoom = new int[2];//new int[masterThiefAP.length];
        partyBusy = new int[2];//new int[masterThiefAP.length];
        this.MTAssaultParty = new ItfMTAssaultParty[2];//new ItfMTAssaultParty[masterThiefAP.length];
        MTAssaultParty[0] = masterThiefAP1;
        MTAssaultParty[1] = masterThiefAP2;
        for(int i = 0; i < 2; i++){ //masterThiefAP.length
             //this.MTAssaultParty[i] = (ItfMTAssaultParty) masterThiefAP[i];
             partiesInRoom[i] = -1;
             this.partyBusy[i] = 3;
        }
        this.numThieves = numThieves;
        this.MTMuseum = masterThiefMuseum;
        canvasFlag = true; 
        currentState = "PlanningTheHeist";
        obj = log.setMTState(1000, vc.getCopy());
      	vc.update((VectorClock) obj[0]);
        filledRooms = new boolean[rooms];
        distRooms = new int[rooms];
        for(int i = 0; i < rooms; i++){
            this.filledRooms[i] = true;
            this.distRooms[i] = 0;
        }
      
    }
    
    /**
     * Executes the life cycle of the Master Thief thread. 
     * 5 states: PlanningTheHeist, DecidingWhatToDo, AssemblingAGroup, WaitingForArrival and PresentingTheReport.
     * 
     */
    @Override
    public void run(){
        try{
            int partyFree = -1;
            int numThievesWaiting;
            boolean go = true;
            while(go){
                switch(currentState){
                    case "PlanningTheHeist":
                        log.printFirst();
                        log.printChange();
                        vc.increment();
                        obj = MTMuseum.getDist(vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        distRooms = (int[]) obj[1];
                        vc.increment();
                        obj = MTControlSite.startOperations(vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        currentState = (String) obj[1];
                        vc.increment();
                        obj = log.setMTState(2000, vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        change = true;
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
                          if(numThievesWaiting == numThieves){
                            vc.increment();
                            obj = MTControlSite.sumUpResults(vc.getCopy());
                            vc.update((VectorClock) obj[0]);
                            currentState = (String) obj[1];
                          }
                            else{
                              currentState = "WaitingForArrival";
                              vc.increment();
                              obj = log.setMTState(4000, vc.getCopy());
                              vc.update((VectorClock) obj[0]);
                              change = true;
                              vc.increment();
                              obj = MTControlSite.takeARest(vc.getCopy());
                              vc.update((VectorClock) obj[0]);
                              partyFree = (int) obj[1];
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
                            vc.increment();
                            obj = log.setMTState(3000,vc.getCopy());
                            vc.update((VectorClock) obj[0]);
    
                            partiesInRoom[partyFree] = room;
                            vc.increment();
                            obj = log.setPartyRoom(partyFree,room,vc.getCopy());
                            vc.update((VectorClock) obj[0]);
                            change = true;
                            partyBusy[partyFree] -= 3;
                           
                            vc.increment();
                            obj = MTAssaultParty[partyFree].prepareAssaultParty(room, distRooms[room], vc.getCopy());
                            vc.update((VectorClock) obj[0]);
                          
                            vc.increment();
                            obj = MTConcentrationSite.partyNeeded(partyFree,true, vc.getCopy()); //mudar o valor para true da party que foi preparada
                            vc.update((VectorClock) obj[0]);
                            
                        
                        }else{
                            currentState = "WaitingForArrival";
                            vc.increment();
                            obj = log.setMTState(4000,vc.getCopy());
                            vc.update((VectorClock) obj[0]);
                            change = true;
                            vc.increment();
                            obj = MTControlSite.takeARest(vc.getCopy());
                            vc.update((VectorClock) obj[0]);
                            partyFree = (int) obj[1];
                        }
                        break;
                    case "AssemblingAGroup":
                        vc.increment();
                        obj = MTAssaultParty[partyFree].sendAssaultParty(vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                  
                        vc.increment();
                        obj = MTConcentrationSite.partyNeeded(partyFree,false, vc.getCopy()); //mudar o valor para true da party que foi enviada
                        vc.update((VectorClock) obj[0]);
                        
                        currentState = "DecidingWhatToDo";
                  
                        vc.increment();
                        obj = log.setMTState(2000, vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        change = true;
                        break;
                    case "WaitingForArrival":
                        boolean var = true;
                        vc.increment();
                        obj = MTControlSite.collectCanvas(vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        var = (boolean) obj[1];
                        if(!var)
                            filledRooms[partiesInRoom[partyFree]] = var;
                        currentState = "DecidingWhatToDo";
                        vc.increment();
                        obj = log.setMTState(2000, vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        change = true;
                        partyBusy[partyFree]+=1;
                        break;
                    case "PresentingTheReport":
                        sleep(20);
                        vc.increment();
                        obj =  log.setMTState(5000, vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        log.printItAllOut();
                        /*for (ItfMTAssaultParty MTAssaultParty1 : MTAssaultParty) {
                            MTAssaultParty1.stopT();
                        }*/
                        vc.increment();
                        obj = MTConcentrationSite.stopT(vc.getCopy());
                        vc.update((VectorClock) obj[0]);
                        go = false;
                        break;
                }
                if(change){
                    log.printChange();
                    change = false;
                }
            }
        }catch(Exception e){
        }
    }
}